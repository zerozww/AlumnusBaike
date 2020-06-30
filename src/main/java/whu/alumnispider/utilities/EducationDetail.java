package whu.alumnispider.utilities;

public class EducationDetail {
    private String matchName;
    private String education;
    private String degree;
    private String field;
    private String time;
    private int level;

    public EducationDetail(String matchName,String education,String degree, String field, String time) {
        this.matchName = matchName;
        this.education = education;
        this.degree = degree;
        this.field = field;
        this.time = time;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLevel(){
        if (degree==null||degree.isEmpty()){
            return 100;
        }
        else if (degree.equals("教授"))
            return 1;
        else if (degree.equals("博士生导师"))
            return 2;
        else if (degree.equals("副教授"))
            return 3;
        else if (degree.equals("研究生导师")||degree.equals("硕士导师"))
            return 4;
        else if (degree.equals("讲师")||degree.equals("教师"))
            return 5;
        else if (degree.equals("博士"))
            return 6;
        else if (degree.equals("硕士")||degree.equals("研究生"))
            return 7;
        else if (degree.equals("学士")||degree.equals("本科"))
            return 8;
        else if (degree.equals("大专"))
            return 9;
        else
            return 99;
    }
}
