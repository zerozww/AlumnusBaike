package whu.alumnispider.utilities;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

// TODO: Extractor the property of leaders.
public class ReExpUtility {
    public static final String reYearMonth = "[1-9][0-9]{3}年([0-9]{1,2}月)?";

    public static final String reYear = "[1-9][0-9]{3}(?=年)|[1-9][0-9]{3}";

    public static final String reDateBirth = "(出?生于{reYearMonth}|{reYearMonth}出?生)"
            .replace("{reYearMonth}", reYearMonth);

    public static final String reProvince = "(山东|江苏|上海|浙江|安徽|福建|江西|广东|广西|海南|河南|湖南|湖北|北京|天津|河北|山西|内蒙古|宁夏|青海|陕西|甘肃|新疆|四川|贵州|云南|重庆|西藏|辽宁|吉林|黑龙江)";

    public static final String rePlaceBirth = "{reProvince}\\S+人[，|,]"
            .replace("{reProvince}", reProvince);

    // Including other name fo WHU.
    public static final String reWuhanUniversity = "武汉大学|武汉水利电力大学|武汉测绘科技大学|湖北医科大学|武汉水利电力学院|武汉测绘学院|葛洲坝水电工程学院|武汉测量制图学院|湖北医学院|湖北省医学院|湖北省立医学院";

    public static final String reKeywordinCV = "领导|简历";

    // extracted by the Gender suffix and specific prefix like "<span>"
    // Specifically, "/s" can't match all space character in JAVA.
    public static final String reNameGenderSuffix = "(?<=[\\s|\"|;|>|\\u0020|\\u3000|\\u00A0])\\S{2,4}(?=[,|，][男|女][,|，])";

    public static final String reJobPosition = "(?<=现任)(.*?)[。|.]";

    public static final String reNumberYearMonth = "[0-9]{4}[\\.][0-9]{2}";

    // possible job will end with "</p>".
    public static final String reUnionYear = "{reNumberYearMonth}[-|－]+(?!{reNumberYearMonth}).*[\\<\\/p\\>]"
            .replace("{reNumberYearMonth}", reNumberYearMonth);
}
