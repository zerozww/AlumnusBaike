package whu.alumnispider.utilities;

public class Province {
    public String index;
    public String name;

    public Province() {}

    public Province(String index, String name) {
        this.index = index;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndex() {
        return this.index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}
