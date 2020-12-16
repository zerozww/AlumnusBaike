package whu.alumnispider.DAO;

import whu.alumnispider.utilities.School;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SchoolDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private String schoolNameTableMysql = "`alumnus`.`school_name`";
    private String schoolNameTableSqlserver = "[school_name]";

    public SchoolDAO() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=alumnus", "sa", "15212xXX!@#");
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @description
     * @return 若查询成功，返回学校名称的hashmap，若查询失败，返回null
     */
    public Map<String,String[]> getSchoolNameMap(){
        Map<String,String[]> schoolNameMap = new HashMap<>();
        try {
            String sql = "select * from " + schoolNameTableMysql;
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()){
                String name = resultSet.getString("name");
                int schoolId = resultSet.getInt("school_id");
                String extname = resultSet.getString("extname");
                String[] extnameArray = extname.split("，");
                schoolNameMap.put(name,extnameArray);
            }
            return schoolNameMap;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @description
     * @return 返回schoolList
     */
    public List<School> getSchoolList(){
        List<School> schoolList = new ArrayList<>();
        try{
            String sql = "select * from " + schoolNameTableSqlserver;
            ResultSet resultSet = stmt.executeQuery(sql);
            while(resultSet.next()){
                School school = new School();
                school.setSchoolId(resultSet.getInt("school_id"));
                school.setName(resultSet.getString("name"));
                school.setExtname(resultSet.getString("extname"));
                schoolList.add(school);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schoolList;
    }
}
