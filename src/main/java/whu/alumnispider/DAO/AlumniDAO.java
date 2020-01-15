package whu.alumnispider.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import whu.alumnispider.utilities.*;

public class AlumniDAO {
    private Connection conn = null;
    private Statement stmt = null;

    public AlumniDAO() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?serverTimezone=UTC", "root", "123456");
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String str, String property, String index, String tableName) {
        try {
            String sql = "UPDATE " + tableName + " SET " + property + "=\'" + str + "\'" + "WHERE" + " id=" + index;
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void update(int num, String property, String index, String tableName) {
        try {
            String sql = "UPDATE " + tableName + " SET " + property + "=\'" + num + "\'" + "WHERE" + " id=" + index;
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // read data in database.
    public List<String> readFromTable(String tableName, String selectProperty) {
        try {
            List<String> rets = new ArrayList<String>();
            String sql = "SELECT " + selectProperty + " FROM " + tableName;
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String ret = resultSet.getString(1);
                rets.add(ret);
            }

            return rets;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ;

        return null;
    }

}
