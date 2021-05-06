package whu.alumnispider.baidusearchComponent;

import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import whu.alumnispider.utils.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BaiduPersonInfo {
    private static final String[] ILLEGALWORDS = {"违纪", "违法"};
    private static final String[] PERSONILLEGALWORDS = {"涉嫌", "因"};
    private static Utility util = new Utility();

    public static String getName(Html html) {
        String personNamePath = "//dd[@class='lemmaWgt-lemmaTitle-title']/h1/text()";
        Selectable personNamePage;
        String personName;
        personNamePage = html.xpath(personNamePath);
        personName = personNamePage.toString();
        return personName;
    }

    public static boolean isPersonRelated2Illegal(Html html) {
        String mainTextXpath = "//div[@class='para']/allText()";
        Selectable personWord;
        List<String> personWords;
        personWord = html.xpath(mainTextXpath);
        personWords = personWord.all();
        return isWordRelated2Illegal(personWords);
    }

    public static boolean isWordRelated2Illegal(List<String> personWords) {
        for (String illegalWord : ILLEGALWORDS) {
            for (String word : personWords)
                if (word.contains(illegalWord)) {
                    for (String personIllegalWord : PERSONILLEGALWORDS) {
                        if (word.contains(personIllegalWord))
                            return true;
                    }
                }
        }
        return false;
    }

    public static String getJob(Html html) {
        // xpath
        String personJobInfoPath1 = "//dd[@class='lemmaWgt-lemmaTitle-title']/h2/text()";
        String personJobInfoPath2 = "//div[@class='para']/allText()";
        String personJobInfoPath3 = "//div[@class='basic-info cmn-clearfix']/allText()";
        // java rgex
        String[] personJobPath2s = {"现任(.*?)。", "职业为(.*?)。", "现为(.*?)。", "现系(.*?)。", "现任(.*?)；", "职业为(.*?)；", "现为(.*?)；", "现系(.*?)；"};
        String[] personJobPath3 = {"职务 (.*?) ", "职称 (.*?) ", "职业 (.*?) "};
        String[] personJobPath4s = {"曾任(.*?)。","曾为(.*?)。","曾系(.*?)。","曾任(.*?)；","曾为(.*?)；","曾系(.*?)；"};
        String personJobPath1 = "（(.*)）";
        String indexPath = "\\[\\d*?[-—]?\\d*?\\]";
        String blankPath = "(\\s|\\u00A0)*";
        String blank160Path = "\\u00A0*";

        Selectable personJobInfoPage;
        String personJob;
        List<String> personJobInfos = new ArrayList<>();

        // 从匹配模式二获取职业信息
        personJobInfoPage = html.xpath(personJobInfoPath2);
        personJobInfos = personJobInfoPage.all();
        personJob = util.getMatching(personJobInfos, personJobPath2s);
        if (personJob == null) {
            // 从匹配模式一种获取职业信息
            personJobInfoPage = html.xpath(personJobInfoPath1);
            personJob = personJobInfoPage.toString();
            // 判断从匹配模式一是否能够获取职业信息
            if (personJob != null) {
                //java正则匹配去除结果中括号
                personJob = util.getMatching(personJob, personJobPath1);
            }
            //前两个模式都匹配失败，尝试从匹配模式三种获取职业信息
            else {
                personJobInfoPage = html.xpath(personJobInfoPath3);
                personJob = personJobInfoPage.toString();
                //结尾加空格，使得dd的内容始终被空格包裹
                personJob = personJob + " ";
                personJob = personJob.replaceAll(blank160Path, "");
                personJob = util.getMatching(personJob, personJobPath3);
            }
        }
        // 如果获取不到现任信息，则尝试爬取曾任信息
        if (personJob==null){
            personJobInfoPage = html.xpath(personJobInfoPath1);
            personJob = personJobInfoPage.toString();
            personJob = util.getMatching(personJob,personJobPath4s);
        }
        if (personJob != null) {
            personJob = personJob.replaceAll(indexPath, "");
            personJob = personJob.replaceAll(blankPath, "");
        }
        return personJob;
    }

    /**
     * @param tableContent 人物表格字符串
     * @return 逝世返回true, 在世返回false
     * @description
     */
    public static boolean getIsDead(String tableContent) {
        String[] passAwayKeywordCHN = {"逝世日期", "过世日期"};
        String aliveStr = BaiduTable.getContentFromTableStr(tableContent, passAwayKeywordCHN);
        return aliveStr != null;
    }

    /**
     * @param job 人物姓名
     * @return 人物是否退休
     * @description
     */
    public static boolean isRetiredFromJob(String job) {
        if (job == null || job.isEmpty())
            return false;
        else {
            char head = job.charAt(0);
            return head == '原';
        }
    }
    /**
     * @description 获取百科人物ID
     * @param html 页面html
     * @return int 百科人物ID
     */
    public static int getBaikeId(Html html){
        String baikeIdPath = "//div[@class='lemmaWgt-promotion-rightPreciseAd']/@data-lemmaid";
        String baikeIdStr = html.xpath(baikeIdPath).toString();
        int baikeId;
        baikeId = Integer.parseInt(baikeIdStr);
        return baikeId;
    }

    /**
     * @description 从人物简介中判断人物性别
     * @param briefContent 人物词条简介字符串
     * @return 男性返回true，女性返回false，无结果返回null
     */
    public static Boolean getSex(String briefContent){
        boolean isMan = Pattern.matches("[\\s\\S]*[,，]\\s*男\\s*[,，][\\s\\S]*",briefContent);
        if (isMan)
            return true;
        boolean isFemale = Pattern.matches("[\\s\\S]*[,，]\\s*女\\s*[,，][\\s\\S]*",briefContent);
        if (isFemale)
            return false;
        return null;
    }

    /**
     * @description 从人物词条表格中获取民族信息
     * @param tableContent 人物词条表格字符串
     * @return 民族信息，无结果返回null
     */
    public static String getNation(String tableContent){
        String nationKeywordCHN = "民族";
        String nationStr;
        nationStr = BaiduTable.getContentFromTableStr(tableContent, nationKeywordCHN);
        return nationStr;
    }

    /**
     * @description 从页面html中获取人物标题
     * @param html 页面html
     * @return 人物的标题
     */
    public static String getTitle(Html html){
        String titlePath = "//dd[@class='lemmaWgt-lemmaTitle-title']/h2/text()";
        String titlePurePattern = "（(.*)）";
        String title = html.xpath(titlePath).toString();
        if (title!=null){
            // 去除括号
            title = util.getMatching(title,titlePurePattern);
        }
        return title;
    }

    /**
     * @description 从人物词条表格中获取国籍信息
     * @param tableContent 人物词条表格字符串
     * @return 国籍信息，无结果返回null
     */
    public static String getState(String tableContent){
        String stateKeywordCHN = "国籍";
        String stateStr;
        stateStr = BaiduTable.getContentFromTableStr(tableContent, stateKeywordCHN);
        return stateStr;
    }

    /**
     * @description 从人物词条表格中获取出生地信息
     * @param tableContent 人物词条表格字符串
     * @return 出生地信息，无结果返回null
     */
    public static String getBirthplace(String tableContent){
        String birthplaceKeywordCHN = "出生地";
        String birthplaceStr;
        birthplaceStr = BaiduTable.getContentFromTableStr(tableContent, birthplaceKeywordCHN);
        return birthplaceStr;
    }
}
