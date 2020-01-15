package whu.alumnispider;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import whu.alumnispider.DAO.AlumniDAO;
import whu.alumnispider.DAO.AlumniSchoolDAO;
import whu.alumnispider.DAO.BaiduPictureDAO;
import whu.alumnispider.utilities.*;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.io.*;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaiduSearchAddProcessor implements PageProcessor {
    private static AlumniDAO alumniDAO = new AlumniDAO();
    private static BaiduPictureDAO baiduPictureDAO = new BaiduPictureDAO();
    private static AlumniSchoolDAO alumniSchoolDAO = new AlumniSchoolDAO();
    private Site site = Site.me().setSleepTime(150).setRetryTimes(2)
            .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
    private static List<String> searchWebsiteList;
    private static final String[] SCHOOLNAME = {"武汉大学","武汉水利电力大学","武汉测绘科技大学","湖北医科大学",
            "武汉水利水电学院", "葛洲坝水电工程学院","武汉测绘学院","武汉测量制图学院","湖北医学院","湖北省医学院",
            "湖北省立医学院","武汉水利电力学院"};

    @Override
    public void process(Page page) {
        //getOtherNames();
        //int d = 1;
        /*
        Html html = page.getHtml();
        String tableContent = getAllContentFromTable(html);
        String website = page.getUrl().toString();
        alumniDAO.updateTableContent(tableContent,website);

         */
        /*
        Timestamp timestamp = getTime();
        String label = getLabel(html);
        String mainContent = getMainContent(html);
        String briefIntro = getBriefIntro(html);

        int a = alumniDAO.updateContent2(label,mainContent,briefIntro,timestamp,url);
        System.out.println(a);

         */

    }
    /*
    private String getAllContentFromTable(Html html){
        String content;
        Selectable contentInfoPage;
        String tableXpath = "//div[@class='basic-info cmn-clearfix']/allText()";
        contentInfoPage = html.xpath(tableXpath);
        content = contentInfoPage.toString();
        return content;
    }
    private static String getPureString(String word){
        String blank160Path = "&nbsp;";
        String indexPath = "\\[\\d*?[-—]?\\d*?\\]";
        String pureWord;
        pureWord = word.replaceAll(blank160Path,"");
        pureWord = pureWord.replaceAll(indexPath,"");
        return pureWord;
    }

    private boolean isWordRelated2Whu(String word){
        for (String name : SCHOOLNAME){
            if (word.contains(name)){
                return true;
            }
        }
        return false;
    }

    // 用于从百度百科词条表格中挑选指定属性的值，一旦匹配成功就返回
    private String getContentFromTable(Html html,String[] attrNames){
        String content;
        Selectable contentInfoPage;
        String tableXpath = "//div[@class='basic-info cmn-clearfix']/allText()";
        String[] rgexArray;
        String blank160Rgex = "\\u00A0*";
        String indexPath = "\\[\\d*?\\-?\\d*?\\]";

        // 将传入的属性名转化为正则表达式
        List<String> rgexList = new ArrayList<>();
        for (String attrName : attrNames){
            rgexList.add(attrName + " (.*?) ");
        }
        rgexArray = new String[rgexList.size()];
        rgexList.toArray(rgexArray);

        contentInfoPage = html.xpath(tableXpath);
        content = contentInfoPage.toString();
        //结尾加空格，使得dd的内容始终被空格包裹
        content = content + " ";
        content = content.replaceAll(blank160Rgex,"");
        content = content.replaceAll(indexPath,"");
        content = getMatching(content, rgexArray);
        return content;
    }

    private String getContentFromTable(Html html,String attrName){
        String[] attrNames = {attrName};
        return getContentFromTable(html,attrNames);
    }

    // 如果匹配不到，则返回""
    private String getEducation(Html html) {
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
        if (!education.isEmpty()){
            education = education.replaceAll(indexPath,"");
            education = education.replaceAll(blank160Rgex,"");
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

    private String analyzeSplitContent(String[] wordArray) {
        String[] graduateWordArray = {"毕业", "学历", "学士", "硕士", "博士", "学习", "专业","学院","系", "考上", "考取",
                "学者","任教","教授","讲师","研究员","主任","书记","教师"};
        String[] eduWordArray = {"学历", "学士", "硕士", "博士","学者","任教","教授","讲师","研究员","主任","书记","教师",
                "学院","系","专业","学习"};
        String[] teacherWordArray = {"出版社"};
        String educationContent = "";
        for (int i = 0; i < wordArray.length; i++) {
            if (isWordContains(wordArray[i], SCHOOLNAME)) {
                if (isWordContains(wordArray[i], graduateWordArray) && !isWordContains(wordArray[i], teacherWordArray)) {
                    int j = i;
                    String word = wordArray[i];
                    while (i < wordArray.length - 1) {
                        // 提前分析是否包含延续学历关键词
                        j++;
                        if (isWordContains(wordArray[j], eduWordArray) && !isWordContains(wordArray[j], teacherWordArray)) {
                            word = word + "，" + wordArray[j];
                            i++;
                            continue;
                        }
                        break;
                    }
                    if (educationContent.isEmpty()) {
                        educationContent = word + "；";
                    } else {
                        educationContent = educationContent + word + "；";
                    }
                }
            }
        }
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
        Pattern datePattern = Pattern.compile(dateRgex);
        Matcher m = datePattern.matcher(word);
        return m.find();
    }

    private boolean isContainChar(String word) {
        word = word.replaceAll("[年月日至今其间后]", "");
        // 查看该段文字是否还包含其他文本信息，如果包含其他文本信息，那它就是能表达完整信息的句子，如果没有，则反
        String characterRgex = "[a-zA-Z\\u4E00-\\u9FA5]";
        Pattern charPattern = Pattern.compile(characterRgex);
        Matcher m = charPattern.matcher(word);
        return m.find();
    }

    private String getPersonName(Html html) {
        String personNamePath = "//dd[@class='lemmaWgt-lemmaTitle-title']/h1/text()";
        Selectable personNamePage;
        String personName;
        personNamePage = html.xpath(personNamePath);
        personName = personNamePage.toString();
        return personName;
    }

    private String getPersonContent(Html html) {
        String content = "";
        String contentXpath1="//div[@class='para']/allText()";
        String contentXpath2="//dd[@class='lemmaWgt-lemmaTitle-title']/allText()";
        String contentXpath3="//div[@class='basic-info cmn-clearfix']//dd/allText()";
        String contentXpath4="//dl[@class='lemma-reference collapse nslog-area log-set-param']//li/allText()";
        List<String> contents = new ArrayList<>();
        Selectable contentPage;
        contentPage = html.xpath(contentXpath1);
        contents = contentPage.all();
        contentPage = html.xpath(contentXpath2);
        contents.add(contentPage.toString());
        contentPage = html.xpath(contentXpath3);
        contents.addAll(contentPage.all());
        contentPage = html.xpath(contentXpath4);
        contents.addAll(contentPage.all());
        for(String tempContent : contents){
            for (String schoolName : SCHOOLNAME){
                if (tempContent.contains(schoolName)){
                    content = content + tempContent;
                }
            }
        }
        return content;
    }
/*
    private String getMainContent(Html html){
        Selectable mainContentPage;
        String mainContentXpath = "//div[@class='main-content']";
        mainContentPage = html.xpath(mainContentXpath);
        String mainContent = mainContentPage.toString();

        String outOfSummaryPath = "<div class=\"lemma-summary\" label-module=\"lemmaSummary\">" +
                "(?:\\s*?<div class=\"para\" label-module=\"para\">[\\s\\S]*?</div>)*\\s*?</div>([\\s\\S]*)";
        String personMainContentPath1 = "<div class=\"para-title level-2\"[\\s\\S]*<div class=\"para\" label-module=\"para\">[\\s\\S]*?</div>";
        String personMainContentPath2 = "((\\s*?<div class=\"para\" label-module=\"para\">[\\s\\S]*?<div class=\"lemma-picture text-pic layout-right\"[\\s\\S]*?</div>[\\s\\S]*?</div>)|(\\s*?<div class=\"para\" label-module=\"para\">[\\s\\S]*?</div>))+";
        //String bbbb = "|(\\s*?<div class=\"para\" label-module=\"para\">(?!\\s*?<div)[\\s\\S]*?</div>)";
        Pattern outOfSumPattern = Pattern.compile(outOfSummaryPath);
        Pattern mainContentPattern1 = Pattern.compile(personMainContentPath1);
        Pattern mainContentPattern2 = Pattern.compile(personMainContentPath2);

        Matcher m = outOfSumPattern.matcher(mainContent);
        // 去除简介
        if (m.find()){
            mainContent = m.group(1);
        }
        // 获取正文
        m = mainContentPattern1.matcher(mainContent);
        if (m.find()){
            mainContent = m.group(0);
        }else {
            m = mainContentPattern2.matcher(mainContent);
            if (m.find()){
                mainContent = m.group(0);
            }
        }
        // 转换成document
        Document document = Jsoup.parse(mainContent);
        JXDocument jxDocument = new JXDocument(document);
        List<JXNode> jxNodes = new ArrayList<>();
        try {
            jxNodes = jxDocument.selN("./allText()");
        } catch (XpathSyntaxErrorException e) {
            e.printStackTrace();
        }
        mainContent = jxNodes.get(0).toString();

        return mainContent;
    }

    private String getMainContent2(Html html){
        Document doc = Jsoup.parse(html.toString());
        Elements nodes = doc.getElementsByClass("main-content");
        Element node = nodes.get(0);
        String maincontentRgex;
        String mainContent = node.html();
        String mainContentFront = getMainContentFront(mainContent);
        String mainContentEnd = getMainContentEnd(mainContent);
        if (mainContentEnd==null||mainContentEnd.isEmpty()){
            maincontentRgex = mainContentFront + "([\\s\\S]*)";
        } else {
            maincontentRgex = mainContentFront + "([\\s\\S]*?)" + mainContentEnd;
        }
        Pattern pattern = Pattern.compile(maincontentRgex);
        Matcher m = pattern.matcher(mainContent);
        if (m.find()){
            mainContent = m.group(1);
        }
        return mainContent;
    }

    private String getMainContentFront(String html){
        String catalogClassName = "lemmaWgt-lemmaCatalog";
        String tableClassName = "basic-info";
        String relationClassName = "lemmaWgt-focusAndRelation";
        String summaryClassName = "lemma-summary";
        String[] classNames = {catalogClassName,tableClassName,relationClassName,summaryClassName};
        Element contentFront;
        Elements contentFrontList;
        String mainContentFront;
        Document doc = Jsoup.parse(html);
        for (String className : classNames){
            contentFrontList = doc.getElementsByClass(className);
            if (!contentFrontList.isEmpty()) {
                contentFront = contentFrontList.get(0);
                mainContentFront = contentFront.outerHtml();
                mainContentFront = transformStr2Rgex(mainContentFront);
                return mainContentFront;
            }
        }
        return null;
    }

    private String getMainContentEnd(String html){
        String annotationClassName = "go-auth-box";
        String albumClassName = "album-list";
        String tashuoClassName = "tashuo-bottom";
        String footClassName = "rs-container-foot";
        String referenceClassName = "lemma-reference";
        String tagIdName = "open-tag";
        String[] classNames = {annotationClassName,albumClassName,tashuoClassName,
                footClassName,referenceClassName};
        Element contentEnd;
        Elements contentEndList;
        String mainContentEnd;
        Document doc = Jsoup.parse(html);
        for (String className : classNames){
            contentEndList = doc.getElementsByClass(className);
            if (!contentEndList.isEmpty()) {
                contentEnd = contentEndList.get(0);
                mainContentEnd = contentEnd.outerHtml();
                mainContentEnd = transformStr2Rgex(mainContentEnd);
                return mainContentEnd;
            }
        }
        // 前面的部分获取不到，才获取标签,标签命名不是class，是id
        contentEnd = doc.getElementById(tagIdName);
        if (contentEnd!=null){
            mainContentEnd = contentEnd.outerHtml();
            return mainContentEnd;
        }
        return null;
    }

    private String transformStr2Rgex(String word){
        String rgex;
        rgex = word.replaceAll("\\?","\\\\?");
        rgex = rgex.replaceAll("\\(","\\\\(");
        rgex = rgex.replaceAll("\\)","\\\\)");
        rgex = rgex.replaceAll("\\*","\\\\*");
        rgex = rgex.replaceAll("\\+","\\\\+");
        rgex = rgex.replaceAll("\\[","\\\\[");
        rgex = rgex.replaceAll("\\.","\\\\.");
        return rgex;
    }

    private String getLabel(Html html){
        Selectable labelPage;
        String label;
        String labelXpath = "//div[@id='open-tag']/dd[@id='open-tag-item']/allText()";

        labelPage = html.xpath(labelXpath);
        label = labelPage.toString();
        return label;
    }

    private Timestamp getTime(){
        Date date = new Date();
        return new Timestamp(date.getTime());
    }

    private String getBriefIntro(Html html){
        Selectable briefIntroPage;
        String briefIntro = "";
        List<String> briefIntroList;
        String briefIntroXpath="//div[@class='lemma-summary']/div[@class='para']/allText()";


        briefIntroPage = html.xpath(briefIntroXpath);
        briefIntroList = briefIntroPage.all();
        for (String str : briefIntroList){
            briefIntro = briefIntro + str;
        }
        return briefIntro;
    }

    private static void saveImage(String pictureUrl,String path)  {
        URL url;
        int id;
        id= baiduPictureDAO.insertPictureId(pictureUrl);
        if (id==0){
            id = baiduPictureDAO.getPictureId(pictureUrl);
        }else if (id==-1){
            return;
        }
        try {
            url = new URL(pictureUrl);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path+id+".jpg"));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
            baiduPictureDAO.updatePictureSave(id);
        }  catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 用于职业匹配模式三
    private String getMatching(String soap,String[] rgexs){
        for (String rgex : rgexs){
            Pattern pattern = Pattern.compile(rgex);
            Matcher m = pattern.matcher(soap);
            if(m.find()){
                return m.group(1);
            }
        }
        return null;
    }

    // 用于职业匹配模式二
    private String getMatching(List<String> soaps,String[] rgexs){
        for (String soap : soaps){
            for (String rgex : rgexs){
                Pattern pattern = Pattern.compile(rgex);
                Matcher m = pattern.matcher(soap);
                if(m.find()){
                    return m.group(1);
                }
            }
        }
        return null;
    }

    // 用于职业匹配模式一
    private String getMatching(String soap,String rgex){
        Pattern pattern = Pattern.compile(rgex);
        Matcher m = pattern.matcher(soap);
        if(m.find()){
            return m.group(1);
        }
        return soap;
    }

    private static void downloadAllImage(){
        List<String> pictureList = baiduPictureDAO.getPictures();
        String frontPath = "E:/pictures/";
        for (String picture:pictureList){
            saveImage(picture,frontPath);
        }
        List<HashMap<String,String>> pictureListSaved = baiduPictureDAO.getPictureSaved();
        for (HashMap<String,String> map : pictureListSaved){
            String picture = map.get("picture");
            String picturePath = frontPath + map.get("id") + ".jpg";
            baiduPictureDAO.updatePictureLocation(picture,picturePath);
        }

    }

    private String getSchoolNameFromWord(String word){
        for (String schoolName : SCHOOLNAME){
            if (word.contains(schoolName)){
                return schoolName;
            }
        }
        return null;
    }

    private void analyzeEducationDetail(Alumni alumni){
        String website = alumni.getWebsite();
        String education = alumni.getEducation();
        EducationDetail resultEducationDetail = null;
        if (education.isEmpty())
            return;
        List<EducationDetail> educationDetailList = new ArrayList<>();
        List<String> educationRgexList;
        String schoolName = null;
        String degree;
        String field;
        String time;

        // TODO 获取多个education，最后比较获取最大的education
        String[] educationSplitArray = education.split("；");
        for (String educationSplit : educationSplitArray){
            //对每个分割内容进行分析并提取信息保存下来，之后作比较
            degree = getEducationDegree(educationSplit);
            if (degree==null){
                schoolName = getSchoolNameFromWord(educationSplit);
                educationRgexList = getEducationRgexList(schoolName);
                String[] educationRgexArray = new String[educationRgexList.size()];
                educationRgexList.toArray(educationRgexArray);
                field = getMatching(educationSplit,educationRgexArray);
                time = getEducationTime(educationSplit);
                educationDetailList.add(new EducationDetail(null,field,time));
            }else {
                String[] wordArray = educationSplit.split("，");
                String educationForAnalyze;
                int degreeIndex;
                int schoolNameIndex;
                int i,j;
                for (i = 0;i<wordArray.length;i++){
                    String degreeFind = getEducationDegree(wordArray[i]);
                    if (degreeFind!=null&& degreeFind.equals(degree))
                        break;
                }
                degreeIndex = Math.min(i, wordArray.length - 1);
                for (j=degreeIndex;j>=0;j--){
                    schoolName = getSchoolNameFromWord(wordArray[j]);
                    if (schoolName!=null)
                        break;
                }
                schoolNameIndex = Math.max(j, 0);
                // 找不到符合的校名
                if (schoolName==null)
                    continue;
                educationForAnalyze = wordArray[schoolNameIndex];
                j=schoolNameIndex;
                while (j < degreeIndex){
                    j++;
                    educationForAnalyze = educationForAnalyze + wordArray[j];
                }
                educationRgexList = getEducationRgexList(schoolName);
                String[] educationRgexArray = new String[educationRgexList.size()];
                educationRgexList.toArray(educationRgexArray);
                field = getMatching(educationForAnalyze,educationRgexArray);
                time = getEducationTime(educationForAnalyze);
                educationDetailList.add(new EducationDetail(degree,field,time));
            }//end of else
        }//end of for
        if (!educationDetailList.isEmpty()){
            resultEducationDetail = getHigherEducation(educationDetailList);
            alumniDAO.updateEducationDetail(resultEducationDetail,website);
        }
    }

    private EducationDetail getHigherEducation(List<EducationDetail> educationDetailList){
        EducationDetail resultEducationDetail = educationDetailList.get(educationDetailList.size()-1);
        if (educationDetailList.size()==1){
            return resultEducationDetail;
        }else {
            int level = 100;
            for (EducationDetail educationDetail : educationDetailList){
                int currLevel = educationDetail.getLevel();
                if (currLevel<level){
                    level = currLevel;
                    resultEducationDetail = educationDetail;
                }
            }
            return resultEducationDetail;
        }
    }

    private String getEducationTime(String word){
        String rgex1 = "\\d{2,4}[年.]\\d{1,2}[月]?([~～\\-]|(—{1,2})?)\\d{2,4}[年.]\\d{1,2}[月]?";
        String rgex2 = "\\d{2,4}[年.]\\d{1,2}[月]?";
        String rgex3 = "\\d{2,4}年";
        Pattern pattern1 = Pattern.compile(rgex1);
        Pattern pattern2 = Pattern.compile(rgex2);
        Pattern pattern3 = Pattern.compile(rgex3);
        Matcher m1 = pattern1.matcher(word);
        Matcher m2 = pattern2.matcher(word);
        Matcher m3 = pattern3.matcher(word);
        if (m1.find()){
            return m1.group();
        }
        if (m2.find()){
            return m2.group();
        }
        if (m3.find()){
            return m3.group();
        }
        return null;
    }


    private List<String> getEducationRgexList(String schoolName){
        List<String> educationRgexList = new ArrayList<>();
        String[] fieldArray = {"专业","系","学院","社会学","心理学","法学","经济学"};
        for (String field : fieldArray){
            String educationRgex = schoolName + "(.*?" + field + ")";
            educationRgexList.add(educationRgex);
        }
        return educationRgexList;
    }

    private String getEducationDegree(String word){
        String[] degreeArray = {"学士","本科","大专","讲师","教师"};
        if (word.contains("教授")){
            if (word.contains("副教授"))
                return "副教授";
            else
                return "教授";
        }
        if (word.contains("博士")){
            if (word.contains("博士生导师"))
                return "博士生导师";
            else
                return "博士";
        }
        if (word.contains("研究生")){
            if (word.contains("研究生导师"))
                return "研究生导师";
            else
                return "研究生";
        }
        if (word.contains("硕士")){
            if (word.contains("硕士导师"))
                return "硕士导师";
            else
                return "硕士";
        }
        for (String degree : degreeArray){
            if (word.contains(degree))
                return degree;
        }
        return null;
    }

    private void analyzeAllEducationDetail(){
        List<Alumni> alumniList = alumniDAO.getEducationList();
        for (Alumni alumni : alumniList){
            analyzeEducationDetail(alumni);
        }
    }

    private boolean isWordRelated2Retired(String word){
        if (word==null||word.isEmpty())
            return false;
        else {
            char head = word.charAt(0);
            return head == '原';
        }
    }

    private void updateAllRetired(){
        List<Alumni> alumniList = alumniDAO.getJobList();
        for (Alumni alumni : alumniList){
            String website = alumni.getWebsite();
            String job = alumni.getJob();
            boolean isRetired = isWordRelated2Retired(job);
            alumniDAO.updateRetired(isRetired,website);
        }
    }

    private String getFieldFromJob(String job){
        //先判断是否学界
        String[] educationFieldArray = {"学院","大学","教授","讲师","教师","研究","导师","博导","硕导","教研","教务处","实验室"};
        String[] polityFieldArray = {"局长","处长","科长","主任","区长","县长","市长","省长","委员","书记","庭长","常委",
                "厅长","巡视员","法院","检察","领事","政协","市委","省委","中央","司长","主席","政协","纪检","监察",
                "参事","大使","秘书长","外交部"};
        String[] businessFieldArray = {"董事","工程师","经理","总监","公司","总裁","监事","创始人"};
        String[] otherFieldArray = {"医师"};
        if (job==null)
            return "其他";
        for (String otherfield : otherFieldArray){
            if (job.contains(otherfield))
                return "其他";
        }
        for (String educationField : educationFieldArray){
            if (job.contains(educationField))
                return "学界";
        }
        for (String businessField : businessFieldArray){
            if (job.contains(businessField))
                return "商界";
        }
        for (String polityField : polityFieldArray){
            if (job.contains(polityField))
                return "政界";
        }

        return "其他";
    }

    private void getAllField(){
        List<Alumni> alumniList = alumniDAO.getJobList();
        for (Alumni alumni : alumniList){
            String website = alumni.getWebsite();
            String job = alumni.getJob();
            String field = getFieldFromJob(job);
            alumniDAO.updateField(website,field);
        }
    }

    private void updateAllFeiFan(){
        List<Alumni> alumniList = alumniDAO.getFeiFan2zww();
        for (Alumni alumni:alumniList){
            alumniDAO.updateFeiFan2zww(alumni);
        }
    }

    private static void updateInitial(){
        List<Alumni> alumniList = alumniDAO.getAllName();
        for (Alumni alumni:alumniList){
            String website = alumni.getWebsite();
            String name = alumni.getName();
            String initial = getLowerCase(name,false);
            alumniDAO.updateInitial(website,initial);
        }
    }

    private void getOtherNames(){
        List<String> nameList = alumniSchoolDAO.getOtherName();
        List<String> magNameList = new ArrayList<>();
        for (String name : nameList){
            name = name.replaceAll(" ","");
            name = name.replaceAll("（女）","");
            magNameList.add(name);
        }
        String[] nameArray = new String[nameList.size()];
        nameList.toArray(nameArray);
        String[] magNameArray = new String[magNameList.size()];
        magNameList.toArray(magNameArray);
        for (int i = 0;i<nameList.size();i++){
            //alumniSchoolDAO.updateOtherName(nameArray[i],magNameArray[i]);
            alumniSchoolDAO.insertCandidates(magNameArray[i]);
        }
    }
*/


    //////
    /**
     *  获取汉字首字母或全拼大写字母
     *
     * @param chinese 汉字
     * @param isFull  是否全拼 true:表示全拼 false表示：首字母
     *
     * @return 全拼或者首字母大写字符窜
     */
    public static String getUpperCase(String chinese,boolean isFull){
        return convertHanzi2Pinyin(chinese,isFull).toUpperCase();
    }

    /**
     * 获取汉字首字母或全拼小写字母
     *
     * @param chinese 汉字
     * @param isFull 是否全拼 true:表示全拼 false表示：首字母
     *
     * @return 全拼或者首字母小写字符窜
     */
    public static  String getLowerCase(String chinese,boolean isFull){
        return convertHanzi2Pinyin(chinese,isFull).toLowerCase();
    }

    /**
     * 将汉字转成拼音
     * <P>
     * 取首字母或全拼
     *
     * @param hanzi 汉字字符串
     * @param isFull 是否全拼 true:表示全拼 false表示：首字母
     *
     * @return 拼音
     */
    private static String convertHanzi2Pinyin(String hanzi,boolean isFull){
        /***
         * ^[\u2E80-\u9FFF]+$ 匹配所有东亚区的语言
         * ^[\u4E00-\u9FFF]+$ 匹配简体和繁体
         * ^[\u4E00-\u9FA5]+$ 匹配简体
         */
        String regExp="^[\u4E00-\u9FFF]+$";
        StringBuffer sb=new StringBuffer();
        if(hanzi==null||"".equals(hanzi.trim())){
            return "";
        }
        String pinyin="";
        for(int i=0;i<hanzi.length();i++){
            char unit=hanzi.charAt(i);
            //是汉字，则转拼音
            if(match(String.valueOf(unit),regExp)){
                pinyin=convertSingleHanzi2Pinyin(unit);
                if(isFull){
                    sb.append(pinyin);
                }
                else{
                    sb.append(pinyin.charAt(0));
                }
            }else{
                sb.append(unit);
            }
        }
        return sb.toString();
    }

    /**
     * 将单个汉字转成拼音
     *
     * @param hanzi 汉字字符
     *
     * @return 拼音
     */
    private static String convertSingleHanzi2Pinyin(char hanzi){
        HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        String[] res;
        StringBuffer sb=new StringBuffer();
        try {
            res = PinyinHelper.toHanyuPinyinStringArray(hanzi,outputFormat);
            sb.append(res[0]);//对于多音字，只用第一个拼音
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return sb.toString();
    }

    /***
     * 匹配
     * <P>
     * 根据字符和正则表达式进行匹配
     *
     * @param str 源字符串
     * @param regex 正则表达式
     *
     * @return true：匹配成功  false：匹配失败
     */
    private static boolean match(String str,String regex){
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(str);
        return matcher.find();
    }





    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        //updateInitial();
        //downloadAllImage();

        /*
        List<Alumni> alumniList = alumniDAO.getMainContent();
        for (Alumni alumni:alumniList){
            String main_content = alumni.getMainContent();
            String website = alumni.getWebsite();
            alumniDAO.updateMainContent(main_content,website);
        }

         */
        /*
        List<Alumni> alumniList = alumniDAO.getBriefAndMain();
        for (Alumni alumni : alumniList){
            String brief_intro = alumni.getBriefIntro();
            String main_content = alumni.getMainContent();
            String website = alumni.getWebsite();
            brief_intro = getPureString(brief_intro);
            main_content = getPureString(main_content);
            alumniDAO.updateBriefAndMain(brief_intro,main_content,website);
        }

         */


        //saveImage("https://gss0.bdstatic.com/-4o3dSag_xI4khGkpoWK1HF6hhy/baike/w%3D268%3Bg%3D0/sign=e70649c58544ebf86d716339e1c2b017/8694a4c27d1ed21b82925e23af6eddc450da3f98.jpg","E:/pictures/");



/*

        searchWebsiteList = alumniDAO.getWebsitesForTest();

        List<String> urls = new ArrayList<>(searchWebsiteList);
        String[] urlArray = new String[urls.size()];
        urls.toArray(urlArray);
*/

        Spider.create(new BaiduSearchAddProcessor())
                .addUrl("https://baike.baidu.com/item/%E5%82%85%E6%98%A5/10886860#viewPageContent")
                .thread(3)
                .run();



        /*
        String rgex = "foodd(foodd)*[^foodd]";
        String str = "tomfooddfooddzoofdd";
        Pattern pattern = Pattern.compile(rgex);
        Matcher m = pattern.matcher(str);
        if (m.find()){
            System.out.println(m.groupCount());
            System.out.println(m.group(0));
            System.out.println(m.group(1));

        }
*/
    }
}
