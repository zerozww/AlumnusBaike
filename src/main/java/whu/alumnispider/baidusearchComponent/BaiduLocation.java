package whu.alumnispider.baidusearchComponent;


import whu.alumnispider.DAO.BaiduLocationDAO;
import whu.alumnispider.utilities.Province;
import whu.alumnispider.utilities.University;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaiduLocation {

    private static BaiduLocationDAO baiduLocationDAO = new BaiduLocationDAO();
    private static String provinceTable = "provinces";
    private static String schoolTable = "tb_school";
    private static List<String> universitiesAreas = baiduLocationDAO.readFromTable(schoolTable, "area_name");
    private static List<String> universitiesNames = baiduLocationDAO.readFromTable(schoolTable, "school_name");
    private static List<String> provinceIndexes = baiduLocationDAO.readFromTable(provinceTable, "number");
    private static List<String> provinceNames = baiduLocationDAO.readFromTable(provinceTable, "name");
    private static List<University> universities = getUniversities();
    private static List<Province> provinces = getProvinces();


    private static List<University> getUniversities() {
        List<University> universityList = new ArrayList<>();
        for (int i = 0; i < universitiesNames.size(); i++) {
            University university = new University(universitiesAreas.get(i), universitiesNames.get(i));
            universityList.add(university);
        }
        return universityList;
    }

    private static List<Province> getProvinces() {
        List<Province> provinceList = new ArrayList<>();
        for (int i = 0; i < provinceNames.size(); i++) {
            String tempName = provinceNames.get(i);
            tempName = tempName.trim();
            provinceNames.set(i, tempName);

            Province province = new Province(provinceIndexes.get(i), provinceNames.get(i));
            provinceList.add(province);
        }
        return provinceList;
    }

    public static String getProvince(String job) {
        String ret = null;
        if (job != null) {
            for (University university : universities) {
                Pattern pattern = Pattern.compile(university.name);
                Matcher matcher = pattern.matcher(job);

                if (matcher.find()) {
                    ret = university.province;
                    break;
                }
            }

            if (ret == null) {
                for (Province province : provinces) {
                    if (province.name != null) {
                        Pattern patternA = Pattern.compile(province.name);
                        //Pattern patternB = Pattern.compile(province.name.substring(0,2));
                        Matcher matcherA = patternA.matcher(job);
                        //Matcher matcherB = patternB.matcher(str);
                        String index = null;

                        if (matcherA.find()) {
                            index = province.index;
                            index = index.substring(0, 2);
                            ret = locationParser(index);
                            break;
                        }
                    }
                }
            }

            return ret;
        } else {
            return null;
        }
    }

    private static String locationParser(String index) {
        if (index == "11") return "北京市";
        if (index == "12") return "天津市";
        if (index == "13") return "河北省";
        if (index == "14") return "山西省";
        if (index == "15") return "内蒙古";
        if (index == "21") return "辽宁省";
        if (index == "22") return "吉林省";
        if (index == "23") return "黑龙江省";
        if (index == "31") return "上海市";
        if (index == "32") return "江苏省";
        if (index == "33") return "浙江省";
        if (index == "34") return "安徽省";
        if (index == "35") return "福建省";
        if (index == "36") return "江西省";
        if (index == "37") return "山东省";
        if (index == "41") return "河南省";
        if (index == "42") return "湖北省";
        if (index == "43") return "湖南省";
        if (index == "44") return "广东省";
        if (index == "45") return "广西省";
        if (index == "46") return "海南省";
        if (index == "50") return "重庆市";
        if (index == "51") return "四川省";
        if (index == "52") return "贵州省";
        if (index == "53") return "云南省";
        if (index == "54") return "西藏省";
        if (index == "61") return "陕西省";
        if (index == "62") return "甘肃省";
        if (index == "63") return "青海省";
        if (index == "64") return "宁夏省";
        if (index == "65") return "新疆省";

        return null;
    }
}
