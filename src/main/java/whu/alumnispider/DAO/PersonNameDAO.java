package whu.alumnispider.DAO;

import whu.alumnispider.utils.Utility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonNameDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private final String personNameSqlserver = "[person_name]";
    private Utility util = new Utility();

    public PersonNameDAO() {
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

    public int insertPersonName(String name,String originalName){
        try{
            String sql="insert into " + personNameSqlserver + "([name],[original_name]) values (?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,name);
            if (originalName == null){
                preparedStatement.setNull(2,Types.NVARCHAR);
            }else {
                preparedStatement.setString(2,originalName);
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    public int getExistPersonNameNum(String name){
        try{
            String sql="select count(*) from" + personNameSqlserver + "where [name] = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,name);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);


        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

    }
}
