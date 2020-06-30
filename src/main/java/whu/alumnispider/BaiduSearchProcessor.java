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
    private static PersonInfoDAO personInfoDAO = new PersonInfoDAO();
    private static PersonNameDAO personNameDAO = new PersonNameDAO();
    private static SchoolDAO schoolDAO = new SchoolDAO();
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
        String errorPath = "<h1 class=\"baikeLogo\"> 百度百科错误页 </h1>";
        String name = util.getMatching(page.getUrl().toString(), personNamePath);
        if (page.getHtml().toString().contains(errorPath)) {
            personNameDAO.updatePersonName(name);
            return;
        }
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
                personNameDAO.updatePersonName(name);
                return;
            }
        }
        /*
        // 不需要判断是否与学校有关就可以提取人物信息
        if (isPersonRelated2Whu(page)) {
            getInformation(page);
        }
         */
        getInformation(page);

        //已经爬取完
        if (searchNameList.contains(name)) {
            personNameDAO.updatePersonName(name);
        }
    }

    /**
     * 新版本在爬取人物信息时不需要判断是否与武大相关
     */
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


    // get person information,use method from baidusearchcomponent
    private int getInformation(Page page) {
        Html html = page.getHtml();
        String website = page.getUrl().toString();
        String tableContent = BaiduTable.getAllContentFromTable(html);

        //String relatedContent = BaiduContent.getRelatedContent(html);
        String label = BaiduContent.getLabel(html);
        String briefIntro = BaiduContent.getBriefIntro(html);
        String mainContent = BaiduContent.getMainContent(html);

        String name = BaiduPersonInfo.getName(html);
        String job = BaiduPersonInfo.getJob(html);
        String title = BaiduPersonInfo.getTitle(html);
        boolean isIllegal = BaiduPersonInfo.isPersonRelated2Illegal(html);
        boolean isRetied = BaiduPersonInfo.isRetiredFromJob(job);
        boolean isDead = BaiduPersonInfo.getIsDead(tableContent);
        int baikeId = BaiduPersonInfo.getBaikeId(html);
        Boolean sex = BaiduPersonInfo.getSex(briefIntro);
        String nation = BaiduPersonInfo.getNation(tableContent);
        String birthdayStr = BaiduBirthday.getBirthday(tableContent, briefIntro, mainContent);
        Integer birthday = null;
        if (birthdayStr != null) {
            birthday = Integer.valueOf(birthdayStr);
        }
        String pictureWeb = BaiduPicture.getPicture(html);
        String pictureLocal = null;
        /** 不可以在线程中使用url.openStream()
         if (pictureWeb!=null){
         pictureLocal = BaiduPicture.downloadImage(pictureWeb,baikeId);
         }
         */
        String initial = BaiduInitial.getLowerCase(name, false);
        String field = BaiduField.getFieldFromJob(job);
        String location = BaiduLocation.getProvince(job);
        Timestamp time = util.getTime();
        String id = java.util.UUID.randomUUID().toString();
        //TODO 以下信息为空，未能提取
        String organization = null;
        String position = null;
        Integer grade = null;
        String gradeName = null;
        boolean status = false;
        String maxedu = null;
        String state = BaiduPersonInfo.getState(tableContent);
        String province = BaiduLocation.getProvince(job);
        String city = null;
        String area = null;
        String town = null;
        String birthplace = BaiduPersonInfo.getBirthplace(tableContent);

        /** 新版本学习经历与人物基础信息不在同一个表，名人表和名人毕业信息表是一对多的关系，需要分别处理。
         String education = BaiduEducation.getEducation(html);

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
         */

        System.out.println("人物姓名：" + name + ", 人物职业：" + job);

        Person person = new Person();
        person.setId(id);
        person.setBaikeId(baikeId);
        person.setWebsite(website);
        person.setName(name);
        person.setTitle(title);
        person.setPictureWeb(pictureWeb);
        person.setPictureLocal(pictureLocal);
        person.setBriefInfo(briefIntro);
        person.setTableContent(tableContent);
        person.setMainContent(mainContent);
        person.setLabel(label);
        person.setJob(job);
        person.setField(field);
        person.setLocation(location);
        person.setOrganization(organization);
        person.setPosition(position);
        person.setGrade(grade);
        person.setGradeName(gradeName);
        person.setSex(sex);
        person.setNation(nation);
        person.setRetired(isRetied);
        person.setDead(isDead);
        person.setIllegal(isIllegal);
        person.setInitial(initial);
        person.setBirthday(birthday);
        person.setTime(time);
        person.setStatus(status);
        person.setMaxedu(maxedu);
        person.setState(state);
        person.setProvince(province);
        person.setCity(city);
        person.setArea(area);
        person.setTown(town);
        person.setBirthplace(birthplace);
        
        int result = 0;
        result = personInfoDAO.insertPersonInfoSqlserver(person);
        System.out.println(result);
        return result;
    }

    /**
     * @return void
     * @description 从名人表中获取数据，根据学校表格中的学校关键词，获取名人的毕业信息，保存到名人毕业信息表中。
     */
    private static void updateAllGraduate() {
        BaiduEducation baiduEducation = new BaiduEducation();
        int totalNumber = personInfoDAO.getPersonInfoCount();
        for (int count = totalNumber; count > 0; count = count - 5000) {
            List<Person> personList = personInfoDAO.getPersonInfoList(5000);
            List<School> schoolList = schoolDAO.getSchoolList();
            for (Person person : personList) {
                boolean isInsertSuccess = true;
                for (School school : schoolList) {
                    baiduEducation.setup(school, person);
                    int insertResult = baiduEducation.insertGraduate();
                    if (insertResult != -3 && insertResult <= 0)
                        isInsertSuccess = false;
                }
                if (isInsertSuccess)
                    personInfoDAO.updatePersonStatus(person.getId());
            }
        }
    }

    private static void updateGraduateForTest(int count) {
        BaiduEducation baiduEducation = new BaiduEducation();
        List<Person> personList = personInfoDAO.getPersonInfoList(count);
        List<School> schoolList = schoolDAO.getSchoolList();
        for (Person person : personList) {
            boolean isInsertSuccess = true;
            for (School school : schoolList) {
                baiduEducation.setup(school, person);
                int insertResult = baiduEducation.insertGraduate();
                if (insertResult != -3 && insertResult <= 0)
                    isInsertSuccess = false;
            }
            if (isInsertSuccess)
                personInfoDAO.updatePersonStatus(person.getId());
        }
    }

    private static void downloadAllPictures() {
        List<Person> personList = personInfoDAO.getPersonPictureList();
        for (Person person : personList) {
            String pictureName = BaiduPicture.downloadImage(person.getPictureWeb(), person.getBaikeId());
            if (pictureName != null) {
                int updateResult = personInfoDAO.updatePictureLocal(person.getId(), pictureName);
                if (updateResult == 1)
                    System.out.println(updateResult);
                else
                    System.out.println(person.getWebsite() + " error:" + updateResult);
            }


        }
    }

    private static void searchAllAlumniFromWeb() {
        searchNameList = personNameDAO.getPersonNameSqlserver(80000);
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
        searchNameList = Arrays.asList("阿坝州");
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

    /*
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
     */

    public static void main(String[] args) {
        //PropertyConfigurator.configure("E:\\GitHub\\AlumnusBaike\\src\\log4j.properties");
        //searchAllAlumniFromWeb();
        //updateAllGraduate();
        //updateGraduateForTest(1);
        downloadAllPictures();
        //searchAlumniForTest();
    }
}
