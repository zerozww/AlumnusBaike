package whu.alumnispider.baidusearchComponent;

import whu.alumnispider.utilities.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaiduBirthday {
    private static Pattern birthYearPattern = Pattern.compile(ReExpUtility.reYear);

    /**
     * @param tableContent 表格内容字符串
     * @param briefIntro   简介字符串
     * @param mainContent  正文字符串
     * @return 返回人物生日年份
     * @description
     */
    public static String getBirthday(String tableContent, String briefIntro, String mainContent) {

        String dateBirthStr = BaiduTable.getContentFromTableStr(tableContent, "出生日期");
        Matcher birthYearMatcher = null;

        if (dateBirthStr != null) {
            birthYearMatcher = birthYearPattern.matcher(dateBirthStr);
        }

        if (dateBirthStr == null || !birthYearMatcher.find()) {
            dateBirthStr = birthDateParser(briefIntro);

            if (dateBirthStr == null) {
                dateBirthStr = birthDateParser(mainContent);
            }
        }

        if (dateBirthStr != null) {
            birthYearMatcher = birthYearPattern.matcher(dateBirthStr);

            if (birthYearMatcher.find()) {
                dateBirthStr = birthYearMatcher.group(0);
            } else {
                dateBirthStr = null;
            }
        }
        return dateBirthStr;
    }

    private static String birthDateParser(String source) {
        if (source==null)
            return null;
        Pattern birthDatePattern = Pattern.compile(ReExpUtility.reDateBirth);
        Matcher birthDateMatcher = birthDatePattern.matcher(source);
        int index = -1;
        String retStr = "blank";

        while (birthDateMatcher.find()) {
            index++;

            if (index == 0) {
                retStr = birthDateMatcher.group();

                return retStr;
            }
        }

        return null;
    }
}
