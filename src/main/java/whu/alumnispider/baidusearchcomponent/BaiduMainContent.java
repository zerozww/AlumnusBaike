package whu.alumnispider.baidusearchcomponent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.selector.Html;
import whu.alumnispider.utils.Utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaiduMainContent {

    private static Utility util = new Utility();

    /**
     * @param html html
     * @return 正文的html
     * @description 获取人物正文的html
     */
    public static String getMainContent(Html html) {
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
        mainContent = util.getPureStringFromHtml(mainContent);
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
}
