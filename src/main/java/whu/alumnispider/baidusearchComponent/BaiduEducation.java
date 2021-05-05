package whu.alumnispider.baidusearchComponent;

import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import whu.alumnispider.DAO.PersonGraduateDAO;
import whu.alumnispider.DAO.PersonInfoDAO;
import whu.alumnispider.utilities.EducationDetail;
import whu.alumnispider.utilities.Graduate;
import whu.alumnispider.utilities.Person;
import whu.alumnispider.utilities.School;
import whu.alumnispider.utils.Utility;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static whu.alumnispider.baidusearchComponent.BaiduTable.getContentFromTable;

public class BaiduEducation {
    /*
    private static final String[] SCHOOLNAME = {"武汉大学", "武汉水利电力大学", "武汉测绘科技大学", "湖北医科大学",
            "武汉水利水电学院", "葛洲坝水电工程学院", "武汉测绘学院", "武汉测量制图学院", "湖北医学院", "湖北省医学院",
            "湖北省立医学院", "武汉水利电力学院"};
     */
    private Utility util = new Utility();
    private static PersonGraduateDAO personGraduateDAO = new PersonGraduateDAO();
    private static BaiduEducationDetial baiduEducationDetial = new BaiduEducationDetial();
    private static PersonInfoDAO personInfoDAO = new PersonInfoDAO();
    private String[] schoolName;
    private School school;
    private Person person;
    private final static int EMPTYEDUCATION = -3;
    private final static int INSERTGRADUATEERROR = -2;
    private final static int GETDETAILERROR = -4;

    public BaiduEducation() {
    }

    public void setup(School school, Person person) {
        this.school = school;
        this.person = person;
        this.schoolName = school.getExtname().split("，");
    }
    /**
     * @description
     * @return -1:更新maxedu失败; -2:插入graduate失败; -3:education为空; 0:更新maxedu不成功，sql成功运行但没有更新成功; -4:获取detail失败
     */
    /*
    public int insertGraduate() {

        // totalEducation是人物全部的学习经历，包含所有学位
        String totalEducation = getEducation(person);
        if (totalEducation == null || totalEducation.isEmpty())
            return EMPTYEDUCATION;
        baiduEducationDetial.setSchoolName(schoolName);
        EducationDetail educationDetail = baiduEducationDetial.getEduDetailFromEducation(totalEducation);
        if (educationDetail==null){
            return GETDETAILERROR;
        }
        Graduate graduate = new Graduate();
        String id = java.util.UUID.randomUUID().toString();
        String personId = person.getId();
        int baikeId = person.getBaikeId();
        int schoolId = school.getSchoolId();
        String personName = person.getName();
        String schoolName = school.getName();
        String matchName = educationDetail.getMatchName();
        // 此处的education专指人物某一学位的学习经历
        String education = educationDetail.getEducation();
        String educationDegree = educationDetail.getDegree();
        String educatinoField = educationDetail.getField();
        String educationTime = educationDetail.getTime();
        Timestamp time = util.getTime();
        int addType = 1;

        graduate.setId(id);
        graduate.setPersonId(personId);
        graduate.setBaikeId(baikeId);
        graduate.setSchoolId(schoolId);
        graduate.setPersonName(personName);
        graduate.setSchoolName(schoolName);
        graduate.setMatch_name(matchName);
        graduate.setEducationEntire(totalEducation);
        graduate.setEducation(education);
        graduate.setEducationDegree(educationDegree);
        graduate.setEducationField(educatinoField);
        graduate.setEducationTime(educationTime);
        graduate.setTime(time);
        graduate.setAddType(addType);
        int insertGraduateResult = personGraduateInfoDAO.insertGraduateSqlserver(graduate);
        int updateMaxeduResult = INSERTGRADUATEERROR;
        if (insertGraduateResult!= -1){
            updateMaxeduResult = personInfoDAO.updatePersonMaxedu(graduate.getEducationDegree(),graduate.getPersonId());
        }
        System.out.println("更新maxedu: "+ updateMaxeduResult);
        return updateMaxeduResult;
    }
    */

    /**
     * @description
     * @return whu.alumnispider.utilities.Graduate graduate数据；null,暂时判断没有问题
     * @author zww
     * @date 2020/12/4 16:18
     */
    public Graduate getGraduate(){
        // totalEducation是人物全部的学习经历，包含所有学位
        String totalEducation = getEducation(person);
        if (totalEducation == null || totalEducation.isEmpty())
            return null;
        baiduEducationDetial.setSchoolName(schoolName);
        EducationDetail educationDetail = baiduEducationDetial.getEduDetailFromEducation(totalEducation);
        if (educationDetail==null){
            System.out.format("person_id为：%s ，从教育经历中获取学历、学位、时间失败。",person.getId());
            return null;
        }
        Graduate graduate = new Graduate();
        String id = java.util.UUID.randomUUID().toString();
        String personId = person.getId();
        int baikeId = person.getBaikeId();
        int schoolId = school.getSchoolId();
        String personName = person.getName();
        String schoolName = school.getName();
        String matchName = educationDetail.getMatchName();
        // 此处的education专指人物某一学位的学习经历
        String education = educationDetail.getEducation();
        String educationDegree = educationDetail.getDegree();
        String educatinoField = educationDetail.getField();
        String educationTime = educationDetail.getTime();
        Timestamp time = util.getTime();
        int addType = 1;
        int lock = 0;

        graduate.setId(id);
        graduate.setPersonId(personId);
        graduate.setBaikeId(baikeId);
        graduate.setSchoolId(schoolId);
        graduate.setPersonName(personName);
        graduate.setSchoolName(schoolName);
        graduate.setMatch_name(matchName);
        graduate.setEducationEntire(totalEducation);
        graduate.setEducation(education);
        graduate.setEducationDegree(educationDegree);
        graduate.setEducationField(educatinoField);
        graduate.setEducationTime(educationTime);
        graduate.setTime(time);
        graduate.setAddType(addType);
        graduate.setAddTime(time);
        graduate.setLock(lock);

        return graduate;
    }

    // 匹配失败则返回""
    public String getEducation(Person person) {
        String education;
        education = getEducationFromBody(person);
        if (education.isEmpty()) {
            if (person.getTableContent() == null)
                return null;
            String tableContent = person.getTableContent().replaceAll("\\?", "");
            education = BaiduTable.getContentFromTableStr(tableContent, "毕业院校");
            if (education == null || education.isEmpty()) {
                return null;
            }
            if (!util.isWordContainsKeys(education, schoolName))
                return null;
        }
        return education;
    }

    /**
     * @param person 人物名人表记录
     * @return 人物学历信息
     * @description 获取人物学历信息，如果匹配不到，则返回""
     */
    private String getEducationFromBody(Person person) {
        String education = "";
        Selectable mainContentPage;
        List<String> mainContentList = new ArrayList<>();
        String[] contentSplitArray;
        String blank160Rgex = "\\u00A0*";
        String indexPath = "\\[\\d*?-?\\d*?]";
        String mainTextXpath = "//div[@class='para']/allText()";
        if (person.getBriefInfo()!=null){
            mainContentList.add(person.getBriefInfo());
        }
        if (person.getMainContent()!=null){
            Html html = new Html(person.getMainContent());
            mainContentPage = html.xpath(mainTextXpath);
            mainContentList.addAll(mainContentPage.all());
        }

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

    private String transformSymbolEn2Cn(String word) {
        word = word.replaceAll(",", "，");
        word = word.replaceAll(":", "：");
        word = word.replaceAll("\\(", "（");
        word = word.replaceAll("\\)", "）");
        word = word.replaceAll(";", "；");
        word = word.replaceAll("-", "—");
        return word;
    }

    private String[] getSplitContent(String word) {
        // 将（其间： ）格式去除
        String bracketRgex = "(.*?)（(?:[其期]间[，：]?，?)?((?:\\d{2}[年.]\\d{1,2}|\\d{4}[年.至—~]).*)）(.*)";
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

    private String analyzeSplitContent(String[] wordArray) {
        String[] graduateWordArray = {"毕业", "学历", "学士", "硕士", "博士", "学习", "专业", "学院", "系", "考上", "考取",
                "学者", "任教", "教授", "讲师", "研究员", "主任", "书记", "教师"};
        String[] eduWordArray = {"学历", "学士", "硕士", "博士", "学者", "任教", "教授", "讲师", "研究员", "主任", "书记",
                "教师", "学院", "系", "专业", "学习"};
        String[] teacherWordArray = {"出版社"};
        String educationContent = "";
        for (int i = 0; i < wordArray.length; i++) {
            if (isWordContains(wordArray[i], schoolName)) {
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

    private boolean isWordContains(String word, String[] keyWordArray) {
        for (String keyWord : keyWordArray) {
            if (word.contains(keyWord)) {
                return true;
            }
        }
        return false;
    }

    // 将左括号到右括号的内容连接在一起
    private String[] combineBracket(String[] wordArray) {
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
    private String[] combineDateContent(String[] wordArray) {
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

    private boolean isContainDate(String word) {
        String dateRgex = "\\d{2}[年.]\\d{1,2}|^\\d{4}[年.至—~]";
        return util.isWordContainsRgex(word, dateRgex);
    }

    private boolean isContainChar(String word) {
        word = word.replaceAll("[年月日至今其间后]", "");
        // 查看该段文字是否还包含其他文本信息，如果包含其他文本信息，那它就是能表达完整信息的句子，如果没有，则反
        String characterRgex = "[a-zA-Z\\u4E00-\\u9FA5]";
        return util.isWordContainsRgex(word, characterRgex);
    }

    private boolean isWordRelated2Whu(String word) {
        for (String name : schoolName) {
            if (word.contains(name)) {
                return true;
            }
        }
        return false;
    }

    public String[] getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String[] schoolName) {
        this.schoolName = schoolName;
    }
}
