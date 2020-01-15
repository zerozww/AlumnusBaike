package whu.alumnispider.baidusearchcomponent;

import whu.alumnispider.DAO.BaiduFieldDAO;
import whu.alumnispider.utilities.Alumni;

import java.util.List;

public class BaiduField {
    private static BaiduFieldDAO baiduFieldDAO = new BaiduFieldDAO();

    /**
     * @param job 人物职业
     * @return 人物领域{"政界","学界","商界","其他"}
     * @description
     */
    public static String getFieldFromJob(String job) {
        //先判断是否学界
        String[] educationFieldArray = {"学院", "大学", "教授", "讲师", "教师", "研究", "导师", "博导", "硕导",
                "教研", "教务处", "实验室"};
        String[] polityFieldArray = {"局长", "处长", "科长", "主任", "区长", "县长", "市长", "省长", "委员",
                "书记", "庭长", "常委", "厅长", "巡视员", "法院", "检察", "领事", "政协", "市委", "省委", "中央",
                "司长", "主席", "政协", "纪检", "监察", "参事", "大使", "秘书长", "外交部"};
        String[] businessFieldArray = {"董事", "工程师", "经理", "总监", "公司", "总裁", "监事", "创始人"};
        String[] otherFieldArray = {"医师"};
        if (job == null)
            return "其他";
        for (String otherfield : otherFieldArray) {
            if (job.contains(otherfield))
                return "其他";
        }
        for (String educationField : educationFieldArray) {
            if (job.contains(educationField))
                return "学界";
        }
        for (String businessField : businessFieldArray) {
            if (job.contains(businessField))
                return "商界";
        }
        for (String polityField : polityFieldArray) {
            if (job.contains(polityField))
                return "政界";
        }
        return "其他";
    }

    /**
     * @return void
     * @description 获取并更新所有人物的领域
     */
    public static void updateAllField() {
        List<Alumni> alumniList = baiduFieldDAO.getJobList();
        for (Alumni alumni : alumniList) {
            String website = alumni.getWebsite();
            String job = alumni.getJob();
            String field = getFieldFromJob(job);
            baiduFieldDAO.updateField(website, field);
        }
    }

}
