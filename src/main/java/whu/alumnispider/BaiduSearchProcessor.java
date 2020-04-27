package whu.alumnispider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import whu.alumnispider.DAO.*;
import whu.alumnispider.utilities.*;
import whu.alumnispider.utils.*;
import whu.alumnispider.baidusearchcomponent.*;


import java.sql.Timestamp;
import java.util.*;

public class BaiduSearchProcessor implements PageProcessor {
    private static BaiduAlumniDAO baiduAlumniDAO = new BaiduAlumniDAO();
    private Utility util = new Utility();
    private Site site = Site.me().setSleepTime(150).setRetryTimes(2)
            .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");

    private static final String alumniTable = "alumnus_v3";
    private static final String candidateTable = "candidates";
    private static final String pictureTable = "picture";
    private static final String websiteTable = "websites";
    private static final String provinceTable = "provinces";
    private static final String schoolTable = "tb_school";
    private static List<String> searchNameList;
    private static final String[] SCHOOLNAME = {"武汉大学", "武汉水利电力大学", "武汉测绘科技大学", "湖北医科大学",
            "武汉水利水电学院", "葛洲坝水电工程学院", "武汉测绘学院", "武汉测量制图学院", "湖北医学院", "湖北省医学院",
            "湖北省立医学院", "武汉水利电力学院"};
    private static final String[] ILLEGALWORDS = {"违纪", "违法"};
    private static final String[] PERSONILLEGALWORDS = {"涉嫌", "因"};

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void process(Page page) {
        // TODO 添加网址和更新候选人代码被注释了。
        // ResultItem Selectable
        Selectable personPage;

        List<String> personPages = new ArrayList<String>();

        // Below code is the definition of Xpath sentence.
        String personLinkXpath = "//ul[@class='polysemantList-wrapper cmn-clearfix']/li/a/@href";
        String personLinkXpath2 = "//ul[@class='custom_dot  para-list list-paddingleft-1']/li/div/a/@href";
        String personNamePath = "https://baike\\.baidu\\.com/item/(.*)";
        String name = util.getMatching(page.getUrl().toString(), personNamePath);
        // 第一次搜索，才爬取其他目录的网址
        if (searchNameList.contains(name)) {
            // 寻找其他目录
            personPage = page.getHtml().xpath(personLinkXpath);
            personPages = personPage.all();
            for (String tempPage : personPages) {
                Request request = new Request(tempPage);
                page.addTargetRequest(request);
            }
            personPage = page.getHtml().xpath(personLinkXpath2);
            personPages = personPage.all();
            for (String tempPage : personPages) {
                tempPage = tempPage + "#viewPageContent";
                Request request = new Request(tempPage);
                page.addTargetRequest(request);
            }
            // 该页面不用爬取
            if (personPages.size() > 0) {
                //baiduAlumniDAO.updateCandidate(name);
                return;
            }
        }
        if (isPersonRelated2Whu(page)) {
            getInformation(page);
        }

        //已经爬取完
        if (searchNameList.contains(name)) {
            //baiduAlumniDAO.updateCandidate(name);
        }
    }

    // 判断人物是否与武大有关，并保存网址和匹配的关键词
    private boolean isPersonRelated2Whu(Page page) {
        String entryTextXpath = "//dd/allText()";
        String mainTextXpath = "//div[@class='para']/allText()";
        // 寻找当前目录是否与武大有关
        Selectable personWord;
        List<String> personWords = new ArrayList<String>();
        String schoolName;
        String url = page.getUrl().toString();
        // 先检索词条信息
        personWord = page.getHtml().xpath(entryTextXpath);
        personWords = personWord.all();
        schoolName = getWordRelated2Whu(personWords);
        if (schoolName != null) {
            //System.out.println("人物词条匹配成功");
            System.out.println("匹配成功的网址为:" + url);
            //baiduAlumniDAO.addWebsite(url,schoolName);
            return true;
        } else {
            // 词条不匹配，再检索人物的主要信息
            personWord = page.getHtml().xpath(mainTextXpath);
            personWords = personWord.all();
            schoolName = getWordRelated2Whu(personWords);
            if (schoolName != null) {
                //System.out.println("人物主要内容匹配成功");
                System.out.println("匹配成功的网址为:" + url);
                //baiduAlumniDAO.addWebsite(url,schoolName);
                return true;
            }
        }
        //baiduAlumniDAO.addWebsite(url, null);
        return false;
    }


    private boolean isPersonRelated2Illegal(Html html) {
        String mainTextXpath = "//div[@class='para']/allText()";
        Selectable personWord;
        List<String> personWords;
        personWord = html.xpath(mainTextXpath);
        personWords = personWord.all();
        return isWordRelated2Illegal(personWords);
    }

    private String getWordRelated2Whu(String word) {
        for (String schoolName : SCHOOLNAME) {
            if (word.contains(schoolName)) {
                return schoolName;
            }
        }
        return null;
    }

    private String getWordRelated2Whu(List<String> words) {
        for (String word : words) {
            for (String schoolName : SCHOOLNAME) {
                if (word.contains(schoolName)) {
                    return schoolName;
                }
            }
        }
        return null;
    }

    private boolean isWordRelated2Illegal(List<String> personWords) {
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

    // get person information
    private void getInformation(Page page) {
        Html html = page.getHtml();

        String job = getJob(html);
        String name = getName(html);
        boolean isIllegal = isPersonRelated2Illegal(html);
        String website = page.getUrl().toString();
        String picture = getPicture(html);
        String content = getContent(html);
        String label = getLabel(html);
        String briefIntro = getBriefIntro(html);
        Timestamp time = getTime();

        // use method from component below except "isDead"
        String mainContent = BaiduMainContent.getMainContent(html);
        String education = BaiduEducation.getEducation(html);
        String initial = BaiduInitial.getLowerCase(name, false);
        String tableContent = BaiduTable.getAllContentFromTable(html);
        boolean isRetied = BaiduRetired.isRetiredFromJob(job);
        boolean isDead = getIsDead(tableContent);
        String field = BaiduField.getFieldFromJob(job);
        String birthday = BaiduBirthday.getBirthday(tableContent, briefIntro, mainContent);
        String location = BaiduLocation.getLocation(job);
        // get educationDetail
        EducationDetail educationDetail = BaiduEducationDetial.getEduDetailFromEducation(education);
        String educationTime = null;
        String educationField = null;
        String educationDegree = null;
        if (educationDetail != null) {
            educationTime = educationDetail.getTime();
            educationField = educationDetail.getField();
            educationDegree = educationDetail.getDegree();
        }

        System.out.println("人物姓名：" + name + ", 人物职业：" + job);

        Alumni alumni = new Alumni();
        alumni.setName(name);
        alumni.setJob(job);
        alumni.setEducation(education);
        alumni.setIllegal(isIllegal);
        alumni.setWebsite(website);
        alumni.setPicture(picture);
        alumni.setContent(content);
        alumni.setLabel(label);
        alumni.setMainContent(mainContent);
        alumni.setBriefIntro(briefIntro);
        alumni.setTime(time);
        alumni.setTableContent(tableContent);
        alumni.setEducationDegree(educationDegree);
        alumni.setEducationField(educationField);
        alumni.setEducationTime(educationTime);
        alumni.setRetired(isRetied);
        alumni.setField(field);
        alumni.setInitial(initial);
        alumni.setAlive(isDead);
        alumni.setLocation(location);
        alumni.setBirthday(birthday);
        int a = 0;
        //baiduAlumniDAO.add(alumni);
    }

    private String getName(Html html) {
        String personNamePath = "//dd[@class='lemmaWgt-lemmaTitle-title']/h1/text()";
        Selectable personNamePage;
        String personName;
        personNamePage = html.xpath(personNamePath);
        personName = personNamePage.toString();
        return personName;
    }

    private String getPicture(Html html) {
        String personPicturePath = "//div[@class='summary-pic']/a/img/@src";
        Selectable personPicturePage;
        List<String> personPictures;
        personPicturePage = html.xpath(personPicturePath);
        personPictures = personPicturePage.all();
        for (String personPicture : personPictures) {
            return personPicture;
        }
        return null;
    }

    private String getJob(Html html) {
        // xpath
        String personJobInfoPath1 = "//dd[@class='lemmaWgt-lemmaTitle-title']/h2/text()";
        String personJobInfoPath2 = "//div[@class='para']/allText()";
        String personJobInfoPath3 = "//div[@class='basic-info cmn-clearfix']/allText()";
        // java rgex
        String[] personJobPath2s = {"现任(.*?)。", "职业为(.*?)。", "现为(.*?)。", "现系(.*?)。", "现任(.*?)；", "职业为(.*?)；", "现为(.*?)；", "现系(.*?)；"};
        String[] personJobPath3 = {"职务 (.*?) ", "职称 (.*?) ", "职业 (.*?) "};
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
        if (personJob != null) {
            personJob = personJob.replaceAll(indexPath, "");
            personJob = personJob.replaceAll(blankPath, "");
        }
        return personJob;
    }

    /**
     * @param html 人物词条html
     * @return 所有包含武大同义词的段落
     * @description
     */
    private String getContent(Html html) {
        String content = "";
        String contentXpath1 = "//div[@class='para']/allText()";
        String contentXpath2 = "//dd[@class='lemmaWgt-lemmaTitle-title']/allText()";
        String contentXpath3 = "//div[@class='basic-info cmn-clearfix']//dd/allText()";
        String contentXpath4 = "//dl[@class='lemma-reference collapse nslog-area log-set-param']//li/allText()";
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
        for (String tempContent : contents) {
            for (String schoolName : SCHOOLNAME) {
                if (tempContent.contains(schoolName)) {
                    content = content + tempContent;
                }
            }
        }
        return content;
    }

    private String getLabel(Html html) {
        Selectable labelPage;
        String label;
        String labelXpath = "//div[@id='open-tag']/dd[@id='open-tag-item']/allText()";

        labelPage = html.xpath(labelXpath);
        label = labelPage.toString();
        return label;
    }

    private Timestamp getTime() {
        Date date = new Date();
        return new Timestamp(date.getTime());
    }

    private String getBriefIntro(Html html) {
        Selectable briefIntroPage;
        String briefIntro = "";
        List<String> briefIntroList;
        String briefIntroXpath = "//div[@class='lemma-summary']/div[@class='para']/allText()";

        briefIntroPage = html.xpath(briefIntroXpath);
        briefIntroList = briefIntroPage.all();
        for (String str : briefIntroList) {
            briefIntro = briefIntro + str;
        }
        briefIntro = util.getPureStringFromText(briefIntro);
        return briefIntro;
    }

    /**
     * @param tableContent 人物表格字符串
     * @return 逝世返回true, 在世返回false
     * @description
     */
    private boolean getIsDead(String tableContent) {
        String[] passAwayKeywordCHN = {"逝世日期", "过世日期"};
        String aliveStr = BaiduTable.getContentFromTableStr(tableContent, passAwayKeywordCHN);
        return aliveStr != null;
    }

    private static void searchAllAlumniFromWeb() {
        searchNameList = baiduAlumniDAO.getCandidate();
        List<String> urls = new ArrayList<>();
        for (String name : searchNameList) {
            urls.add("https://baike.baidu.com/item/" + name);
        }
        String[] urlArray = new String[urls.size()];
        urls.toArray(urlArray);
        Spider.create(new BaiduSearchProcessor())
                .addUrl(urlArray)
                .thread(3)
                .run();
    }

    private static void searchAlumniForTest() {
        searchNameList = Arrays.asList("侯伟");
        List<String> urls = new ArrayList<>();
        for (String name : searchNameList) {
            urls.add("https://baike.baidu.com/item/" + name);
        }
        String[] urlArray = new String[urls.size()];
        urls.toArray(urlArray);
        Spider.create(new BaiduSearchProcessor())
                .addUrl(urlArray)
                .thread(3)
                .run();
    }

    private static void setupDatabase() {
        BaiduAlumniDAO.setAlumniTable(alumniTable);
        BaiduAlumniDAO.setCandidateTable(candidateTable);
        BaiduAlumniDAO.setWebsiteTable(websiteTable);
        BaiduEducationDAO.setAlumniTable(alumniTable);
        BaiduFieldDAO.setAlumniTable(alumniTable);
        BaiduInitialDAO.setAlumniTable(alumniTable);
        BaiduPictureDAO.setAlumniTable(alumniTable);
        BaiduPictureDAO.setPictureTable(pictureTable);
        BaiduRetiredDAO.setAlumniTable(alumniTable);
        BaiduTableDAO.setAlumniTable(alumniTable);

        BaiduBirthday.setAlumniTable(alumniTable);
        BaiduLocation.setAlumniTable(alumniTable);
        BaiduLocation.setProvinceTable(provinceTable);
        BaiduLocation.setSchoolTable(schoolTable);
    }

    public static void main(String[] args) {
        setupDatabase();
        searchAlumniForTest();
    }
}
