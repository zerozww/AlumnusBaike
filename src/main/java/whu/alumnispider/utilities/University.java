package whu.alumnispider.utilities;

public class University {
    public String province;
    public String name;

    public University() {}

    public University(String province, String name) {
        this.province = province;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
