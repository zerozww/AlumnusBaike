package whu.alumnispider.baidusearchcomponent;

import whu.alumnispider.DAO.BaiduRetiredDAO;
import whu.alumnispider.utilities.Alumni;

import java.util.List;
//只在更新数据时使用，在正常运行程序时不会使用该类
public class BaiduRetired {

    private static BaiduRetiredDAO baiduRetiredDAO = new BaiduRetiredDAO();

    /**
     * @param job 人物姓名
     * @return 人物是否退休
     * @description
     */
    public static boolean isRetiredFromJob(String job) {
        if (job == null || job.isEmpty())
            return false;
        else {
            char head = job.charAt(0);
            return head == '原';
        }
    }

    public static void updateAllRetired() {
        List<Alumni> alumniList = baiduRetiredDAO.getJobList();
        for (Alumni alumni : alumniList) {
            String website = alumni.getWebsite();
            String job = alumni.getJob();
            boolean isRetired = isRetiredFromJob(job);
            baiduRetiredDAO.updateRetired(isRetired, website);
        }
    }

}
