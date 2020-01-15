package whu.alumnispider.baidusearchcomponent;

import whu.alumnispider.DAO.AlumniDAO;
import whu.alumnispider.utilities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaiduBirthday {
    private static AlumniDAO alumniDAO = new AlumniDAO();
    private static String alumniTable = "alumnus_v3";
    private static Pattern birthYearPattern = Pattern.compile(ReExpUtility.reYear);

    // add "date_birth" property in database.
    public static void dateBirthProcessor() {
        List<String> mainContents = alumniDAO.readFromTable(alumniTable, "main_content");
        List<String> briefIntros = alumniDAO.readFromTable(alumniTable, "brief_intro");
        List<String> websites = alumniDAO.readFromTable(alumniTable, "website");
        List<String> ids = alumniDAO.readFromTable(alumniTable, "id");
        int listSize = mainContents.size();
        List<String> dateBirths = new ArrayList<String>();
        String[] dateBirthKeywordCHN = {"出生日期"};

        // use table_content first to match the dateBirth first.
        for (int i = 0; i < listSize; i++) {
            String dateBirthStr = BaiduTable.getTableContentFromDatabase(websites.get(i), dateBirthKeywordCHN);
            Matcher birthYearMatcher = null;

            if (dateBirthStr != null) {
                birthYearMatcher = birthYearPattern.matcher(dateBirthStr);
            }

            if (dateBirthStr == null || !birthYearMatcher.find()) {
                dateBirthStr = birthDateParser(briefIntros.get(i));

                if (dateBirthStr == null) {
                    dateBirthStr = birthDateParser(mainContents.get(i));
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

            dateBirths.add(dateBirthStr);
        }

        for (int i = 0; i < listSize; i++) {
            if (dateBirths.get(i) != null) {
                alumniDAO.update(dateBirths.get(i), "birthday", ids.get(i), alumniTable);
            }
        }
    }

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

    public static String getAlumniTable() {
        return alumniTable;
    }

    public static void setAlumniTable(String alumniTable) {
        BaiduBirthday.alumniTable = alumniTable;
    }
}
