package whu.alumnispider.matchComponent;

import whu.alumnispider.DAO.PersonInfoDAO;
import whu.alumnispider.DAO.PersonInfoHistoryDAO;
import whu.alumnispider.utilities.Person;
import whu.alumnispider.utils.Utility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;



public class PersonMatch {
    // person_info中，addType为空（数据库会返回0）或者为1是指系统生成数据。其他情况不作处理。
    private static final int[] SYSGENADDTYPE = new int[]{0,1};
    private static final String[] CIRS = new String[]{"title","briefInfo","tableContent","label","job","retired","illegal"};
    private static final PersonInfoDAO personInfoDAO = new PersonInfoDAO();
    private static final PersonInfoHistoryDAO personInfoHistoryDAO = new PersonInfoHistoryDAO();

    public static void personMatch(Person person){
        int baikeId = person.getBaikeId();
        String name = person.getName();
        // 匹配人物，匹配成功会返回person，匹配失败会返回null
        Person personMatch = personInfoDAO.getMatchPersonInfo(baikeId,name);
        if (personMatch==null){
            insertPerson(person);
        }else {
            updatePersonMatch(person,personMatch);

        }
    }

    private static void insertPerson(Person person){
        int insertNum = 0;
        insertNum = personInfoDAO.insertPersonInfo(person);
        System.out.println("person_info插入数据：" + insertNum);
    }

    /**
     * @description 将person中为空的字段用personMatch对应的字段补全，另外如果图片URL不同，会将本地图片路径设置为null
     * @param person person
     * @param personMatch personMatch
     * @return void
     * @author zww
     * @date 2020/12/3 17:33
     */
    private static void updatePersonMatch(Person person,Person personMatch){
        if (!isSysgen(personMatch)){
            System.out.format("数据为人工修改，不作处理：baidu_id: %s ,name: %s。",personMatch.getBaikeId(),personMatch.getName());
        }else {
            if(isUpdate(person,personMatch)) {
                updatePerson(person, personMatch);
            }
            // 如果图片URL改变，设置当地图片地址为空，这样在下载图片程序中就会去下载新图片
            if (isUpdatePersonPicture(person,personMatch)){
                person.setPictureLocal(null);
            }

            // 设置status为0，这样毕业信息提取系统就会访问该记录
            person.setStatus(false);
            // 更新person_info数据
            boolean isUpdateSuccess = updatePersonTable(person);
            // 更新成功后，再插入到历史表
            if (isUpdateSuccess){
                personMatch.setHistoryId(personMatch.getId());
                String id = java.util.UUID.randomUUID().toString();
                personMatch.setId(id);
                insertPersonHistoryTable(personMatch);
            }
        }
    }

    private static boolean updatePersonTable(Person person){
        int updateNum = 0;
        updateNum = personInfoDAO.updatePersonInfo(person);
        System.out.println("person_info更新数据：" + updateNum);
        return updateNum == 1;
    }

    private static boolean insertPersonHistoryTable(Person personMatch){
        int insertNum = 0;
        insertNum = personInfoHistoryDAO.insertPersonInfoHistory(personMatch);
        System.out.println("personInfoHistory插入数据："+insertNum);
        return insertNum==1;
    }

    private static boolean isUpdatePersonPicture(Person person, Person personMatch) {
        return (person.getPictureWeb()!=null && !(person.getPictureWeb().equals(personMatch.getPictureWeb())));
    }

    private static boolean isSysgen(Person personMatch){
        for(int i : SYSGENADDTYPE){
            if (personMatch.getAddType()==i)
                return true;
        }
        return false;
    }

    private static boolean isCirs(String str){
        for (String cir:CIRS){
            if (cir.equals(str))
                return true;
        }
        return false;
    }
    /**
     * @description 判断是否需要更新person_info表
     * @param person 爬虫获取的person_info数据
     * @param personMatch 数据库匹配的person_info数据
     * @return boolean 是否需要更新person_info
     * @author zww
     * @date 2020/12/2 10:13
     */
    private static boolean isUpdate(Person person,Person personMatch){
        boolean isUpdate = false;
        Field[] fields = person.getClass().getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            String fieldName = field.getName();
            Object object = null;
            Object objectMatch = null;
            try{
                object = field.get(person);
                objectMatch = field.get(personMatch);
                // 如果获取到的字段为空，该字段爬取的内容不更新
                if (object==null||object.toString().length()==0)
                    continue;
                // 如果在重要的属性上数值不一样，才执行更新操作
                if (isCirs(fieldName)&& !object.toString().equals(objectMatch.toString())){
                    isUpdate=true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isUpdate;
    }


    /**
     * @description 对person进行空白字段填充操作
     * @param person 爬虫获取的person_info数据
     * @param personMatch 数据库匹配的person_info数据
     * @return void
     * @author zww
     * @date 2020/12/2 17:15
     */
    private static void updatePerson(Person person,Person personMatch){
        person.setId(personMatch.getId());
        Field[] fields = person.getClass().getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            String fieldType = field.getGenericType().toString();
            String fieldName = field.getName();
            Object object = null;
            Object objectMatch = null;
            try{
                object = field.get(person);
                objectMatch = field.get(personMatch);
                // 如果是String类型，则判断person是否为空，如果为空，则把数据库的数据填上去
                switch (fieldType) {
                    case "class java.lang.String":
                        if (object == null && objectMatch != null) {
                            String objectMatchStr = objectMatch.toString();
                            Method method = person.getClass().getMethod("set" + Utility.upperCase(fieldName), String.class);
                            method.invoke(person, objectMatchStr);
                        } else if (object != null && objectMatch != null) {
                            String objectStr = object.toString();
                            String objectMatchStr = objectMatch.toString();
                            if (objectStr.length() == 0 && objectMatchStr.length() != 0) {
                                Method method = person.getClass().getMethod("set" + Utility.upperCase(fieldName), String.class);
                                method.invoke(person, objectMatchStr);
                            }
                        }
                        break;
                    case "class java.lang.Integer":
                        if (object == null && objectMatch != null) {
                            Integer objectMatchStr = Integer.parseInt(objectMatch.toString());
                            Method method = person.getClass().getMethod("set" + Utility.upperCase(fieldName), Integer.class);
                            method.invoke(person, objectMatchStr);
                        }
                        break;
                    case "class java.lang.Boolean":
                        if (object == null && objectMatch != null) {
                            Boolean objectMatchBoolean = Boolean.valueOf(objectMatch.toString());
                            Method method = person.getClass().getMethod("set" + Utility.upperCase(fieldName), Boolean.class);
                            method.invoke(person, objectMatchBoolean);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
