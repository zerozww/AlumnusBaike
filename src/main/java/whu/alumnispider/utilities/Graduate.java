package whu.alumnispider.utilities;

import java.sql.Timestamp;

public class Graduate {
    private String id;
    private String personId;
    private int baikeId;
    private int schoolId;
    private String personName;
    private String schoolName;
    private String match_name;
    private String educationEntire;
    private String education;
    private String educationDegree;
    private String educationField;
    private String educationTime;
    private Timestamp time;
    // addType,数据添加类型，系统添加为1，人工修改为2
    private int addType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public int getBaikeId() {
        return baikeId;
    }

    public void setBaikeId(int baikeId) {
        this.baikeId = baikeId;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getMatch_name() {
        return match_name;
    }

    public void setMatch_name(String match_name) {
        this.match_name = match_name;
    }

    public String getEducationEntire() {
        return educationEntire;
    }

    public void setEducationEntire(String educationEntire) {
        this.educationEntire = educationEntire;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public String getEducationField() {
        return educationField;
    }

    public void setEducationField(String educationField) {
        this.educationField = educationField;
    }

    public String getEducationTime() {
        return educationTime;
    }

    public void setEducationTime(String educationTime) {
        this.educationTime = educationTime;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public int getAddType() {
        return addType;
    }

    public void setAddType(int addType) {
        this.addType = addType;
    }
}
