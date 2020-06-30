package whu.alumnispider.utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    /**
     * @description （用于职业匹配模式三）
     * @param soap 匹配的字符串
     * @param rgexs 正则表达式数组
     * @return 第一个匹配成功的子字符串
     */
    public String getMatching(String soap, String[] rgexs) {
        for (String rgex : rgexs) {
            Pattern pattern = Pattern.compile(rgex);
            Matcher m = pattern.matcher(soap);
            if (m.find()) {
                return m.group(1);
            }
        }
        return null;
    }

    /**
     * @description 循环搜索字符串列表的元素，前提是已知所有元素中只会有一个需要的结果（用于职业匹配模式二）
     * @param soaps 匹配的字符串列表
     * @param rgexs 正则表达式数组
     * @return 第一个匹配成功的子字符串
     */
    public String getMatching(List<String> soaps, String[] rgexs) {
        for (String soap : soaps) {
            for (String rgex : rgexs) {
                Pattern pattern = Pattern.compile(rgex);
                Matcher m = pattern.matcher(soap);
                if (m.find()) {
                    return m.group(1);
                }
            }
        }
        return null;
    }

    /**
     * @description （用于职业匹配模式一）
     * @param soap 匹配的字符串
     * @param rgex 正则表达式
     * @return 第一个匹配的子字符串
     */
    public String getMatching(String soap, String rgex) {
        Pattern pattern = Pattern.compile(rgex);
        Matcher m = pattern.matcher(soap);
        if (m.find()) {
            return m.group(1);
        }
        return soap;
    }

    public boolean isWordContainsKeys(String word, String[] keyWordArray) {
        for (String keyWord : keyWordArray) {
            if (word.contains(keyWord)) {
                return true;
            }
        }
        return false;
    }

    public boolean isWordContainsRgex(String word,String rgex){
        Pattern pattern = Pattern.compile(rgex);
        Matcher m = pattern.matcher(word);
        return m.find();
    }

    public String getPureStringFromHtml(String word){
        String blank160Path = "&nbsp;";
        String indexPath = "\\[\\d*?[-—]?\\d*?\\]";
        String pureWord;
        pureWord = word.replaceAll(blank160Path,"");
        pureWord = pureWord.replaceAll(indexPath,"");
        return pureWord;
    }

    public String getPureStringFromText(String word){
        String blank160Path = "\\u00A0*";
        String indexPath = "\\[\\d*?[-—]?\\d*?\\]";
        String pureWord;
        pureWord = word.replaceAll(blank160Path,"");
        pureWord = pureWord.replaceAll(indexPath,"");
        return pureWord;
    }

    public Timestamp getTime() {
        Date date = new Date();
        return new Timestamp(date.getTime());
    }
}
