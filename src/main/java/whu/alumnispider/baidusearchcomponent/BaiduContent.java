package whu.alumnispider.baidusearchcomponent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import whu.alumnispider.utils.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaiduContent {
    private static final String[] SCHOOLNAME = {"武汉大学", "武汉水利电力大学", "武汉测绘科技大学", "湖北医科大学",
            "武汉水利水电学院", "葛洲坝水电工程学院", "武汉测绘学院", "武汉测量制图学院", "湖北医学院", "湖北省医学院",
            "湖北省立医学院", "武汉水利电力学院"};

    private static Utility util = new Utility();

    public static String getBriefIntro(Html html) {
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

    public static String getLabel(Html html) {
        Selectable labelPage;
        String label;
        String labelXpath = "//div[@id='open-tag']/dd[@id='open-tag-item']/allText()";

        labelPage = html.xpath(labelXpath);
        label = labelPage.toString();
        return label;
    }

    /**
     * @param html html
     * @return 正文的html
     * @description 获取人物正文的html
     */
    public static String getMainContent(Html html) {
        String editRgex = "<a class=\"edit-icon j-edit-link\"[\\s\\S].*?</a>";
        Document doc = Jsoup.parse(html.toString());
        Elements nodes = doc.getElementsByClass("main-content");
        Element node = nodes.get(0);
        String maincontentRgex;
        String mainContent = node.html();
        String mainContentFront = getMainContentFront(mainContent);
        String mainContentEnd = getMainContentEnd(mainContent);
        if (mainContentEnd == null || mainContentEnd.isEmpty()) {
            maincontentRgex = mainContentFront + "([\\s\\S]*)";
        } else {
            maincontentRgex = mainContentFront + "([\\s\\S]*?)" + mainContentEnd;
        }
        Pattern pattern = Pattern.compile(maincontentRgex);
        Matcher m = pattern.matcher(mainContent);
        if (m.find()) {
            mainContent = m.group(1);
        }
        if (mainContent!=null){
            mainContent = util.getPureStringFromHtml(mainContent);
            mainContent = mainContent.replaceAll(editRgex,"");
        }
        return mainContent;
    }

    private static String getMainContentFront(String html) {
        String catalogClassName = "lemmaWgt-lemmaCatalog";
        String tableClassName = "basic-info";
        String relationClassName = "lemmaWgt-focusAndRelation";
        String summaryClassName = "lemma-summary";
        String[] classNames = {catalogClassName, tableClassName, relationClassName, summaryClassName};
        Element contentFront;
        Elements contentFrontList;
        String mainContentFront;
        Document doc = Jsoup.parse(html);
        for (String className : classNames) {
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

    private static String getMainContentEnd(String html) {
        String annotationClassName = "go-auth-box";
        String albumClassName = "album-list";
        String tashuoClassName = "tashuo-bottom";
        String footClassName = "rs-container-foot";
        String referenceClassName = "lemma-reference";
        String tagIdName = "open-tag";
        String[] classNames = {annotationClassName, albumClassName, tashuoClassName,
                footClassName, referenceClassName};
        Element contentEnd;
        Elements contentEndList;
        String mainContentEnd;
        Document doc = Jsoup.parse(html);
        for (String className : classNames) {
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
        if (contentEnd != null) {
            mainContentEnd = contentEnd.outerHtml();
            return mainContentEnd;
        }
        return null;
    }

    private static String transformStr2Rgex(String word) {
        String rgex;
        rgex = word.replaceAll("\\?", "\\\\?");
        rgex = rgex.replaceAll("\\(", "\\\\(");
        rgex = rgex.replaceAll("\\)", "\\\\)");
        rgex = rgex.replaceAll("\\*", "\\\\*");
        rgex = rgex.replaceAll("\\+", "\\\\+");
        rgex = rgex.replaceAll("\\[", "\\\\[");
        rgex = rgex.replaceAll("\\.", "\\\\.");
        return rgex;
    }

    /**
     * @param html 人物词条html
     * @return 所有包含武大同义词的段落
     * @description
     */
    public static String getRelatedContent(Html html) {
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
}
