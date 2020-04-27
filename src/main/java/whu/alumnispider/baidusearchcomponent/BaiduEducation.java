package whu.alumnispider.baidusearchcomponent;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import whu.alumnispider.DAO.BaiduEducationDAO;
import whu.alumnispider.utils.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static whu.alumnispider.baidusearchcomponent.BaiduTable.getContentFromTable;

public class BaiduEducation {
    private static final String[] SCHOOLNAME = {"武汉大学", "武汉水利电力大学", "武汉测绘科技大学", "湖北医科大学",
            "武汉水利水电学院", "葛洲坝水电工程学院", "武汉测绘学院", "武汉测量制图学院", "湖北医学院", "湖北省医学院",
            "湖北省立医学院", "武汉水利电力学院"};
    private static Utility util = new Utility();
    private static BaiduEducationDAO baiduEducationDAO = new BaiduEducationDAO();


    /**
     * @param page 人物词条page
     * @return void
     * @description 重新爬取并更新人物的education字段
     */
    public static void updateEducation(Page page) {
        String website = page.getUrl().toString();
        Html html = page.getHtml();
        String education = getEducationFromBody(html);
        baiduEducationDAO.updateEducation(education, website);
    }

    // 匹配失败则返回""
    public static String getEducation(Html html) {
        String education;
        education = getEducationFromBody(html);
        if (education.isEmpty()) {
            education = getContentFromTable(html, "毕业院校");
            if (education == null || education.isEmpty()) {
                return null;
            }
            if (!util.isWordContainsKeys(education, SCHOOLNAME))
                return null;
        }
        return education;
    }


    /**
     * @param html 人物词条html
     * @return 人物学历信息
     * @description 获取人物学历信息，如果匹配不到，则返回""
     */
    private static String getEducationFromBody(Html html) {
        String education = "";
        Selectable mainContentPage;
        List<String> mainContentList;
        String[] contentSplitArray;
        String blank160Rgex = "\\u00A0*";
        String indexPath = "\\[\\d*?-?\\d*?]";
        String mainTextXpath = "//div[@class='para']/allText()";
        mainContentPage = html.xpath(mainTextXpath);
        mainContentList = mainContentPage.all();
        for (String mainContent : mainContentList) {
            if (isWordRelated2Whu(mainContent)) {
                mainContent = transformSymbolEn2Cn(mainContent);
                mainContent = mainContent.replaceAll("\\s+", "，");
                contentSplitArray = getSplitContent(mainContent);
                contentSplitArray = combineBracket(contentSplitArray);
                contentSplitArray = combineDateContent(contentSplitArray);
                education = education + analyzeSplitContent(contentSplitArray);
            }
        }
        if (!education.isEmpty()) {
            education = education.replaceAll(indexPath, "");
            education = education.replaceAll(blank160Rgex, "");
        }
        return education;
    }

    private static String transformSymbolEn2Cn(String word) {
        word = word.replaceAll(",", "，");
        word = word.replaceAll(":", "：");
        word = word.replaceAll("\\(", "（");
        word = word.replaceAll("\\)", "）");
        word = word.replaceAll(";", "；");
        word = word.replaceAll("-", "—");
        return word;
    }

    private static String[] getSplitContent(String word) {
        // 将（其间： ）格式去除
        String bracketRgex = "(.*?)（(?:[其期]间[，：]?)?((?:\\d{2}[年.]\\d{1,2}|\\d{4}[年.至—~]).*)）(.*)";
        List<String> contentSplitList = new ArrayList<>();
        Pattern pattern = Pattern.compile(bracketRgex);
        Matcher m = pattern.matcher(word);
        if (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                String splitWord = m.group(i);
                if (splitWord != null && !splitWord.isEmpty()) {
                    String[] oneSplitArray = splitWord.split("[，。；]");
                    List<String> oneSplitList = Arrays.asList(oneSplitArray);
                    contentSplitList.addAll(oneSplitList);
                }
            }
            String[] contentSplitArray = new String[contentSplitList.size()];
            contentSplitList.toArray(contentSplitArray);
            return contentSplitArray;
        } else {
            return word.split("[，。；]");
        }
    }

    private static String analyzeSplitContent(String[] wordArray) {
        String[] graduateWordArray = {"毕业", "学历", "学士", "硕士", "博士", "学习", "专业", "学院", "系", "考上", "考取",
                "学者", "任教", "教授", "讲师", "研究员", "主任", "书记", "教师"};
        String[] eduWordArray = {"学历", "学士", "硕士", "博士", "学者", "任教", "教授", "讲师", "研究员", "主任", "书记",
                "教师", "学院", "系", "专业", "学习"};
        String[] teacherWordArray = {"出版社"};
        String educationContent = "";
        for (int i = 0; i < wordArray.length; i++) {
            if (isWordContains(wordArray[i], SCHOOLNAME)) {
                if (isWordContains(wordArray[i], graduateWordArray) &&
                        !isWordContains(wordArray[i], teacherWordArray)) {
                    int j = i;
                    String word = wordArray[i];
                    while (i < wordArray.length - 1) {
                        // 提前分析是否包含延续学历关键词
                        j++;
                        if (isWordContains(wordArray[j], eduWordArray) &&
                                !isWordContains(wordArray[j], teacherWordArray)) {
                            word = word + "，" + wordArray[j];
                            i++;
                        } else
                            break;
                    }
                    if (educationContent.isEmpty()) {
                        educationContent = word + "；";
                    } else {
                        educationContent = educationContent + word + "；";
                    }
                }
            }
        }// end of for
        return educationContent;
    }

    private static boolean isWordContains(String word, String[] keyWordArray) {
        for (String keyWord : keyWordArray) {
            if (word.contains(keyWord)) {
                return true;
            }
        }
        return false;
    }

    // 将左括号到右括号的内容连接在一起
    private static String[] combineBracket(String[] wordArray) {
        List<String> wordList = new ArrayList<>();
        String combineStr;
        for (int i = 0; i < wordArray.length; i++) {
            if (wordArray[i].contains("（") && !wordArray[i].contains("）")) {
                combineStr = wordArray[i];
                // 判断下一位是否有右括号，如果有，则合并并跳出，没有则合并继续往下找
                // 先对下标+1进行取值，所以i的范围要小于数组长度-1
                while (i < wordArray.length - 1) {
                    i++;
                    combineStr = combineStr + "，" + wordArray[i];
                    if (wordArray[i].contains("）")) {
                        break;
                    }
                }
                wordList.add(combineStr);
            } else {
                wordList.add(wordArray[i]);
            }
        }
        String[] resultArray = new String[wordList.size()];
        wordList.toArray(resultArray);
        return resultArray;
    }

    // 将时间和后续内容组合在一起，组成有意义的内容
    private static String[] combineDateContent(String[] wordArray) {
        List<String> wordList = new ArrayList<>();
        String frontDateWord = "";
        String combineWord;
        boolean isDateBegin = true;
        for (String word : wordArray) {
            // 查找内容是否包含日期，若包含，则将日期后的内容截下来
            if (isDateBegin) {
                if (isContainDate(word) && !isContainChar(word)) {
                    frontDateWord = frontDateWord + word;
                    isDateBegin = false;
                    continue;
                }
            } else {
                if (!isContainChar(word)) {
                    frontDateWord = frontDateWord + word;
                    continue;
                }
            }

            combineWord = frontDateWord + word;
            wordList.add(combineWord);
            frontDateWord = "";
            isDateBegin = true;
        }
        String[] resultArray = new String[wordList.size()];
        wordList.toArray(resultArray);
        return resultArray;
    }

    private static boolean isContainDate(String word) {
        String dateRgex = "\\d{2}[年.]\\d{1,2}|^\\d{4}[年.至—~]";
        return util.isWordContainsRgex(word, dateRgex);
    }

    private static boolean isContainChar(String word) {
        word = word.replaceAll("[年月日至今其间后]", "");
        // 查看该段文字是否还包含其他文本信息，如果包含其他文本信息，那它就是能表达完整信息的句子，如果没有，则反
        String characterRgex = "[a-zA-Z\\u4E00-\\u9FA5]";
        return util.isWordContainsRgex(word, characterRgex);
    }

    private static boolean isWordRelated2Whu(String word) {
        for (String name : SCHOOLNAME) {
            if (word.contains(name)) {
                return true;
            }
        }
        return false;
    }

}
