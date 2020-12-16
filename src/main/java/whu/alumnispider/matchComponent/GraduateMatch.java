package whu.alumnispider.matchComponent;

import whu.alumnispider.DAO.PersonGraduateDAO;
import whu.alumnispider.DAO.PersonGraduateHistoryDAO;
import whu.alumnispider.DAO.PersonInfoDAO;
import whu.alumnispider.DAO.SchoolDAO;
import whu.alumnispider.baidusearchComponent.BaiduEducation;
import whu.alumnispider.utilities.Graduate;
import whu.alumnispider.utilities.Person;
import whu.alumnispider.utilities.School;
import whu.alumnispider.utils.Utility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class GraduateMatch {
    private static final PersonInfoDAO personInfoDAO = new PersonInfoDAO();
    private static final PersonGraduateDAO personGraduateDAO = new PersonGraduateDAO();
    private static final PersonGraduateHistoryDAO personGraduateHistoryDAO = new PersonGraduateHistoryDAO();
    private static final SchoolDAO schoolDAO = new SchoolDAO();
    private static BaiduEducation baiduEducation = new BaiduEducation();
    private static final String[] CIRS = new String[]{"person_name","school_name","match_name","education_entire","education","education_degree","education_field","education_time"};


    /**
     * @description 插入或更新校友毕业信息
     * @param person person
     * @return void
     * @author zww
     * @date 2020/12/10 15:06
     */
    public static void graduateMatch(Person person){
        boolean isUpdateSuccess = true;
        String personId = person.getId();
        List<School> schoolList = schoolDAO.getSchoolList();
        Graduate graduateMatch = personGraduateDAO.getGraduate(personId);
        for (School school : schoolList) {
            baiduEducation.setup(school, person);
            Graduate graduate = baiduEducation.getGraduate();
            if (graduate!=null){
                if (graduateMatch==null){
                    if(insertGraduate(graduate))
                        isUpdateSuccess = false;
                }else {
                    if (isUpdate(graduate,graduateMatch)){
                        updateGraduateInfo(graduate,graduateMatch);
                        // 更新时，设置id不变
                        graduate.setId(graduateMatch.getId());
                        if(updateGraduate(graduate,graduateMatch))
                            isUpdateSuccess = false;
                    }
                }
            }
        }
        if (isUpdateSuccess)
            personInfoDAO.updatePersonStatus(person.getId());
    }

    /**
     * @description 更新校友毕业信息数据库，并插入历史表
     * @param graduate graduate
     * @param graduateMatch graduateMatch
     * @return boolean
     * @author zww
     * @date 2020/12/10 15:07
     */
    private static boolean updateGraduate(Graduate graduate, Graduate graduateMatch) {
        if (graduate==null||graduateMatch==null)return false;
        // 更新校友毕业信息数据库
        boolean isUpdateGraduateSuccess = updateGraduateTable(graduate);
        if (isUpdateGraduateSuccess){
            //更新成功后才将数据搬移到旧数据表格
            String id = java.util.UUID.randomUUID().toString();
            graduateMatch.setHistoryId(graduateMatch.getId());
            graduateMatch.setId(id);
            boolean isInsertGraduateHistorySuccess = insertGraduateHistoryTable(graduateMatch);
            return isInsertGraduateHistorySuccess;
        }
        return false;
    }

    private static boolean isUpdate(Graduate graduate, Graduate graduateMatch){
        boolean isUpdate = false;
        Field[] fields = graduate.getClass().getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            String fieldName = field.getName();
            Object object = null;
            Object objectMatch = null;
            try{
                object = field.get(graduate);
                objectMatch = field.get(graduateMatch);
                // 如果在重要的属性上数值不一样，才执行更新操作
                if (isCirs(fieldName) && !object.toString().equals(objectMatch.toString())){
                    isUpdate=true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isUpdate;
    }

    private static boolean isCirs(String str){
        for (String cir:CIRS){
            if (cir.equals(str))
                return true;
        }
        return false;
    }

    private static boolean updateGraduateTable(Graduate graduate){
        int updateGraduateNum = 0;
        updateGraduateNum = personGraduateDAO.updateGraduate(graduate);
        System.out.println("更新校友毕业信息表："+updateGraduateNum);
        return updateGraduateNum==1;
    }

    private static boolean insertGraduateHistoryTable(Graduate graduateMatch){
        int insertGraduateNum = 0;
        insertGraduateNum = personGraduateHistoryDAO.insertGraduateHistory(graduateMatch);
        System.out.println("插入校友历史毕业信息表："+insertGraduateNum);
        return insertGraduateNum==1;
    }

    private static void updateGraduateInfo(Graduate graduate, Graduate graduateMatch){
        if (graduate==null || graduateMatch==null)return;
        graduate.setId(graduateMatch.getId());
        Field[] fields = graduate.getClass().getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            String fieldType = field.getGenericType().toString();
            String fieldName = field.getName();
            Object object = null;
            Object objectMatch = null;
            try{
                object = field.get(graduate);
                objectMatch = field.get(graduateMatch);
                // 如果是String类型，则判断person是否为空，如果为空，则把数据库的数据填上去
                switch (fieldType) {
                    case "class java.lang.String":
                        if (object == null && objectMatch != null) {
                            String objectMatchStr = objectMatch.toString();
                            Method method = graduate.getClass().getMethod("set" + Utility.upperCase(fieldName), String.class);
                            method.invoke(graduate, objectMatchStr);
                        } else if (object != null && objectMatch != null) {
                            String objectStr = object.toString();
                            String objectMatchStr = objectMatch.toString();
                            if (objectStr.length() == 0 && objectMatchStr.length() != 0) {
                                Method method = graduate.getClass().getMethod("set" + Utility.upperCase(fieldName), String.class);
                                method.invoke(graduate, objectMatchStr);
                            }
                        }
                        break;
                    case "class java.lang.Integer":
                        if (object == null && objectMatch != null) {
                            Integer objectMatchStr = Integer.parseInt(objectMatch.toString());
                            Method method = graduate.getClass().getMethod("set" + Utility.upperCase(fieldName), Integer.class);
                            method.invoke(graduate, objectMatchStr);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @description 插入graduate数据，并更新person_info表字段
     * @param graduate graduate对象
     * @return boolean true,数据库交互成功；false 数据库交互失败
     * @author zww
     * @date 2020/12/4 16:29
     */
    public static boolean insertGraduate(Graduate graduate) {
        if (graduate!=null) {
            // 首先插入person_graduate_info表
            int insertGraduateResult = personGraduateDAO.insertGraduateSqlserver(graduate);
            System.out.println("插入person_graduate_info: " + insertGraduateResult);

            // 插入成功后才更新person_info表中的字段
            int updateMaxeduResult = 0;
            if (insertGraduateResult == 1) {
                updateMaxeduResult = personInfoDAO.updatePersonMaxedu(graduate.getEducationDegree(), graduate.getPersonId());
            }
            System.out.println("更新maxedu: " + updateMaxeduResult);
            // 若中间数据库交互出错，则返回false;
            if (updateMaxeduResult==-1||insertGraduateResult==-1)return false;
        }
        return true;
    }
}
