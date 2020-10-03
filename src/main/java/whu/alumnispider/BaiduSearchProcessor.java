package whu.alumnispider;

import org.apache.commons.collections.CollectionUtils;
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
    private static final PersonInfoDAO personInfoDAO = new PersonInfoDAO();
    private static final PersonNameDAO personNameDAO = new PersonNameDAO();
    private static final SchoolDAO schoolDAO = new SchoolDAO();
    private final Utility util = new Utility();
    private Site site = Site.me().setSleepTime(150).setRetryTimes(2)
            .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
    private static List<String> searchNameList;

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void process(Page page) {
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
            // 有内容的界面中寻找其他界面的url
            personPage = page.getHtml().xpath(personLinkXpath);
            personPages = personPage.all();
            for (String tempPage : personPages) {
                Request request = new Request(tempPage);
                page.addTargetRequest(request);
            }
            // 无内容的界面，只有其他界面url列表的界面中，寻找其他界面的url
            personPage = page.getHtml().xpath(personLinkXpath2);
            personPages = personPage.all();
            for (String tempPage : personPages) {
                tempPage = tempPage + "#viewPageContent";
                Request request = new Request(tempPage);
                page.addTargetRequest(request);
            }
            // 若第一次搜索，就得出无内容的界面，只需对人名表进行更新，不需要进行爬虫
            if (personPages.size() > 0) {
                personNameDAO.updatePersonName(name);
                return;
            }
        }

        getInformation(page);

        //已经爬取完
        if (searchNameList.contains(name)) {
            personNameDAO.updatePersonName(name);
        }
    }


    // get person information,use method from baidusearchcomponent
    private void getInformation(Page page) {
        Html html = page.getHtml();
        String website = page.getUrl().toString();
        String tableContent = BaiduTable.getAllContentFromTable(html);

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
        System.out.println("person_info插入数据：" + result);

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
        searchNameList = personNameDAO.getPersonNameSqlserver(1);
        List<String> urls = new ArrayList<>();
        List<String> sqlServerNameList = new ArrayList<>();
        CollectionUtils.addAll(sqlServerNameList, new Object[searchNameList.size()]);
        Collections.copy(sqlServerNameList, searchNameList);
        for (String name : sqlServerNameList) {
            String nameCHN = name.replaceAll("[^\\u4e00-\\u9fa5]", "");
            if (nameCHN.equals(name)) {
                // 名字全为中文则直接对名字进行爬虫
                urls.add("https://baike.baidu.com/item/" + name);
            } else {
                if (nameCHN.length() > 0) {
                    int existNameNum = personNameDAO.getExistPersonNameNum(nameCHN);
                    if (existNameNum > 0) {
                        dealWithSpecialChar(urls, name);
                    } else if (existNameNum == 0) {
                        // 将中文添加到名字库中，并关联原名字
                        int isInsert = personNameDAO.insertPersonName(nameCHN, name);
                        if (isInsert == 1) {
                            //若原中文名含特殊符号，则原名字不进行爬虫，直接更新名字库
                            dealWithSpecialChar(urls, name);
                            searchNameList.add(nameCHN);
                            urls.add("https://baike.baidu.com/item/" + nameCHN);
                        }
                    }
                } else {
                    //若原中文名含特殊符号，则原名字不进行爬虫，直接更新名字库
                    dealWithSpecialChar(urls, name);
                }
            }
        }
        String[] urlArray = new String[urls.size()];
        urls.toArray(urlArray);
        Spider.create(new BaiduSearchProcessor())
                .addUrl(urlArray)
                .thread(3)
                .run();
    }

    private static void dealWithSpecialChar(List<String> urls, String name) {
        if (name.contains("%") | name.contains("+") | name.contains("/") | name.contains("?") | name.contains("#") | name.contains("&") | name.contains("=")) {
            personNameDAO.updatePersonName(name);
        } else {
            urls.add("https://baike.baidu.com/item/" + name);
        }
    }


    private static void searchAlumniForTest() {
        searchNameList = Arrays.asList("雷军");
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

    public static void main(String[] args) {
        //PropertyConfigurator.configure("E:\\GitHub\\AlumnusBaike\\src\\log4j.properties");
        searchAllAlumniFromWeb();
        updateAllGraduate();
        //updateGraduateForTest(1);
        downloadAllPictures();
        //searchAlumniForTest();
    }
}
