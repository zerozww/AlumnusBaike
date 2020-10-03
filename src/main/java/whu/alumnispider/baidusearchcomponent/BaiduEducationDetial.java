package whu.alumnispider.baidusearchcomponent;

import whu.alumnispider.utilities.EducationDetail;
import whu.alumnispider.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaiduEducationDetial {
    /*
    private static final String[] SCHOOLNAME = {"武汉大学", "武汉水利电力大学", "武汉测绘科技大学", "湖北医科大学",
            "武汉水利水电学院", "葛洲坝水电工程学院", "武汉测绘学院", "武汉测量制图学院", "湖北医学院", "湖北省医学院",
            "湖北省立医学院", "武汉水利电力学院"};
     */
    private Utility util = new Utility();
    private String[] schoolName;

    public BaiduEducationDetial(){}
    /**
     * @param education 人物学历信息
     * @return 人物的匹配当前学校的最高学位学习经历
     * @description 获取人物的学历信息中的时间、院系、学位
     */
    public EducationDetail getEduDetailFromEducation(String education) {
        EducationDetail resultEducationDetail = null;
        if (education == null || education.isEmpty())
            return null;
        List<EducationDetail> educationDetailList = new ArrayList<>();
        List<String> educationRgexList;
        String schoolName = null;
        String degree;
        String field;
        String time;

        String[] educationSplitArray = education.split("；");
        for (String educationSplit : educationSplitArray) {
            //对每个分割内容进行分析并提取信息保存下来，之后作比较
            degree = getEducationDegree(educationSplit);
            if (degree == null) {
                schoolName = getSchoolNameFromWord(educationSplit);
                educationRgexList = getEducationRgexList(schoolName);
                String[] educationRgexArray = new String[educationRgexList.size()];
                educationRgexList.toArray(educationRgexArray);
                field = util.getMatching(educationSplit, educationRgexArray);
                time = getEducationTime(educationSplit);
                educationDetailList.add(new EducationDetail(schoolName,educationSplit,null, field, time));
            } else {
                String[] wordArray = educationSplit.split("，");
                String educationForAnalyze;
                int degreeIndex;
                int schoolNameIndex;
                int i, j;
                for (i = 0; i < wordArray.length; i++) {
                    String degreeFind = getEducationDegree(wordArray[i]);
                    if (degreeFind != null && degreeFind.equals(degree))
                        break;
                }
                degreeIndex = Math.min(i, wordArray.length - 1);
                for (j = degreeIndex; j >= 0; j--) {
                    schoolName = getSchoolNameFromWord(wordArray[j]);
                    if (schoolName != null)
                        break;
                }
                schoolNameIndex = Math.max(j, 0);
                // 找不到符合的校名
                if (schoolName == null)
                    continue;
                educationForAnalyze = wordArray[schoolNameIndex];
                j = schoolNameIndex;
                while (j < degreeIndex) {
                    j++;
                    educationForAnalyze = educationForAnalyze + wordArray[j];
                }
                educationRgexList = getEducationRgexList(schoolName);
                String[] educationRgexArray = new String[educationRgexList.size()];
                educationRgexList.toArray(educationRgexArray);
                field = util.getMatching(educationForAnalyze, educationRgexArray);
                time = getEducationTime(educationForAnalyze);
                educationDetailList.add(new EducationDetail(schoolName,educationSplit,degree, field, time));
            }//end of else
        }//end of for

        if (!educationDetailList.isEmpty()) {
            resultEducationDetail = getHigherEducation(educationDetailList);
            return resultEducationDetail;
        } else
            return null;
    }

    private EducationDetail getHigherEducation(List<EducationDetail> educationDetailList) {
        EducationDetail resultEducationDetail = educationDetailList.get(educationDetailList.size() - 1);
        if (educationDetailList.size() == 1) {
            return resultEducationDetail;
        } else {
            int level = 100;
            for (EducationDetail educationDetail : educationDetailList) {
                int currLevel = educationDetail.getLevel();
                if (currLevel < level) {
                    level = currLevel;
                    resultEducationDetail = educationDetail;
                }
            }
            return resultEducationDetail;
        }
    }


    private String getEducationTime(String word) {
        String rgex1 = "\\d{2,4}[年.]\\d{1,2}[月]?([~～\\-]|(—{1,2})?)\\d{2,4}[年.]\\d{1,2}[月]?";
        String rgex2 = "\\d{2,4}[年.]\\d{1,2}[月]?";
        String rgex3 = "\\d{2,4}年";
        Pattern pattern1 = Pattern.compile(rgex1);
        Pattern pattern2 = Pattern.compile(rgex2);
        Pattern pattern3 = Pattern.compile(rgex3);
        Matcher m1 = pattern1.matcher(word);
        Matcher m2 = pattern2.matcher(word);
        Matcher m3 = pattern3.matcher(word);
        if (m1.find()) {
            return m1.group();
        }
        if (m2.find()) {
            return m2.group();
        }
        if (m3.find()) {
            return m3.group();
        }
        return null;
    }

    private List<String> getEducationRgexList(String schoolName) {
        List<String> educationRgexList = new ArrayList<>();
        String[] fieldArray = {"专业", "系", "学院", "社会学", "心理学", "法学", "经济学"};
        for (String field : fieldArray) {
            String educationRgex = schoolName + "(.*?" + field + ")";
            educationRgexList.add(educationRgex);
        }
        return educationRgexList;
    }

    private String getEducationDegree(String word) {
        String[] degreeArray = {"学士", "本科", "大专", "讲师", "教师"};
        if (word.contains("教授")) {
            if (word.contains("副教授"))
                return "副教授";
            else
                return "教授";
        }
        if (word.contains("博士")) {
            if (word.contains("博士生导师"))
                return "博士生导师";
            else
                return "博士";
        }
        if (word.contains("研究生")) {
            if (word.contains("研究生导师"))
                return "研究生导师";
            else
                return "研究生";
        }
        if (word.contains("硕士")) {
            if (word.contains("硕士导师"))
                return "硕士导师";
            else
                return "硕士";
        }
        for (String degree : degreeArray) {
            if (word.contains(degree))
                return degree;
        }
        return null;
    }

    private String getSchoolNameFromWord(String word) {
        for (String schoolName : schoolName) {
            if (word.contains(schoolName)) {
                return schoolName;
            }
        }
        return null;
    }

    public String[] getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String[] schoolName) {
        this.schoolName = schoolName;
    }
}
