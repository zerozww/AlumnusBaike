package whu.alumnispider.baidusearchcomponent;

import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import whu.alumnispider.DAO.BaiduTableDAO;
import whu.alumnispider.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class BaiduTable {

    private static Utility util = new Utility();
    private static BaiduTableDAO baiduTableDAO = new BaiduTableDAO();

    /**
     * @param html 人物词条html
     * @return 人物词条表格纯文本
     * @description
     */
    public static String getAllContentFromTable(Html html) {
        String content;
        String blank160Rgex = "\\u00A0*";
        String indexPath = "\\[\\d*?-?\\d*?]";
        Selectable contentInfoPage;
        String tableXpath = "//div[@class='basic-info cmn-clearfix']/allText()";
        contentInfoPage = html.xpath(tableXpath);
        content = contentInfoPage.toString();
        if (content!=null){
            content = content.replaceAll(blank160Rgex, "");
            content = content.replaceAll(indexPath, "");
        }
        return content;
    }

    /**
     * @param website   人物词条网址
     * @param attrNames 属性名称数组
     * @return 返回属性对应的值
     * @description 用于从数据库中获取人物表格属性对应的值
     */
    public static String getTableContentFromDatabase(String website, String[] attrNames) {
        String content;
        String[] rgexArray;
        /** 采集数据时已经对数据进行处理
        String blank160Rgex = "\\u00A0*";
        String indexPath = "\\[\\d*?-?\\d*?]";
         */
        // 将传入的属性名转化为正则表达式
        List<String> rgexList = new ArrayList<>();
        for (String attrName : attrNames) {
            rgexList.add(attrName + " (.*?) ");
        }
        rgexArray = new String[rgexList.size()];
        rgexList.toArray(rgexArray);
        content = baiduTableDAO.getTableContent(website);
        //结尾加空格，使得dd的内容始终被空格包裹
        content = content + " ";
        /** 采集数据时已经对数据进行处理
        content = content.replaceAll(blank160Rgex, "");
        content = content.replaceAll(indexPath, "");
         */
        content = util.getMatching(content, rgexArray);
        return content;
    }

    /**
     * @param website  人物词条网址
     * @param attrName 属性名称
     * @return 返回属性对应的值
     * @description 用于从数据库中获取人物表格属性对应的值
     */
    public static String getTableContentFromDatabase(String website, String attrName) {
        String[] attrNames = {attrName};
        return getTableContentFromDatabase(website, attrNames);
    }

    /**
     * @param html      人物词条html
     * @param attrNames 属性名称数组
     * @return 返回属性对应的值
     * @description 用于从百度百科词条表格中挑选指定属性的值，一旦匹配成功就返回
     */
    public static String getContentFromTable(Html html, String[] attrNames) {
        String content;
        Selectable contentInfoPage;
        String tableXpath = "//div[@class='basic-info cmn-clearfix']/allText()";
        String[] rgexArray;
        String blank160Rgex = "\\u00A0*";
        String indexPath = "\\[\\d*?-?\\d*?]";

        // 将传入的属性名转化为正则表达式
        List<String> rgexList = new ArrayList<>();
        for (String attrName : attrNames) {
            rgexList.add(attrName + " (.*?) ");
        }
        rgexArray = new String[rgexList.size()];
        rgexList.toArray(rgexArray);

        contentInfoPage = html.xpath(tableXpath);
        content = contentInfoPage.toString();
        //结尾加空格，使得dd的内容始终被空格包裹
        content = content + " ";
        content = content.replaceAll(blank160Rgex, "");
        content = content.replaceAll(indexPath, "");
        content = util.getMatching(content, rgexArray);
        return content;
    }

    /**
     * @param html     人物词条html
     * @param attrName 属性名称
     * @return 返回属性对应的值
     * @description 用于从百度百科词条表格中挑选指定属性的值，一旦匹配成功就返回
     */
    public static String getContentFromTable(Html html, String attrName) {
        String[] attrNames = {attrName};
        return getContentFromTable(html, attrNames);
    }

    public static String getContentFromTableStr(String tableStr, String[] attrNames) {
        String content;
        String[] rgexArray;
        /** 采集数据时已经对数据进行处理
        String blank160Rgex = "\\u00A0*";
        String indexPath = "\\[\\d*?-?\\d*?]";
         */
        // 将传入的属性名转化为正则表达式
        List<String> rgexList = new ArrayList<>();
        for (String attrName : attrNames) {
            rgexList.add(attrName + " (.*?) ");
        }
        rgexArray = new String[rgexList.size()];
        rgexList.toArray(rgexArray);

        content = tableStr;
        //结尾加空格，使得dd的内容始终被空格包裹
        content = content + " ";
        /** 采集数据时已经对数据进行处理
        content = content.replaceAll(blank160Rgex, "");
        content = content.replaceAll(indexPath, "");
         */
        content = util.getMatching(content, rgexArray);
        return content;
    }

    public static String getContentFromTableStr(String tableStr, String attrName) {
        String[] attrNames = {attrName};
        return getContentFromTableStr(tableStr, attrNames);
    }

}
