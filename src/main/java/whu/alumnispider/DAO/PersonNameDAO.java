package whu.alumnispider.DAO;

import whu.alumnispider.utils.Utility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonNameDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private String personNameMysql = "`alumnus`.`person_name`";
    private String personNameSqlserver = "[person_name]";
    private Utility util = new Utility();

    public PersonNameDAO() {
        /** mysql版本
         try {
         Class.forName("com.mysql.jdbc.Driver");
         conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/alumnus?serverTimezone=UTC&characterEncoding=utf8", "root", "zww123456");
         stmt = conn.createStatement();
         } catch (ClassNotFoundException | SQLException e) {
         e.printStackTrace();
         }
         */

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=alumnus", "sa", "zww123456");
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @description
     * @return 查询正确返回人名列表，失败返回null
     */
    public List<String> getPersonNameSqlserver() {
        try {
            List<String> personNameList = new ArrayList<>();
            String sql = "SELECT [name] FROM " + personNameSqlserver + " WHERE [status] = 0 ";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String ret = resultSet.getString("name");
                personNameList.add(ret);
            }
            return personNameList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @description
     * @param length 需要获取的长度
     * @return 查询正确返回人名列表，失败返回null
     */
    public List<String> getPersonNameSqlserver(int length) {
        try {
            List<String> personNameList = new ArrayList<>();
            String sql = "SELECT top "+ length +" [name] FROM " + personNameSqlserver + " WHERE [status] = 0";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String ret = resultSet.getString("name");
                personNameList.add(ret);
            }
            return personNameList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int updatePersonName(String name){
        try {
            String sql="update " + personNameSqlserver + " set [status] = 1,[time] = ? where [name] = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setTimestamp(1,util.getTime());
            preparedStatement.setString(2,name);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
