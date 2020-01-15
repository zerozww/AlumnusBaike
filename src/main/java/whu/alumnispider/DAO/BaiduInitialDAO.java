package whu.alumnispider.DAO;

import whu.alumnispider.utilities.Alumni;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaiduInitialDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private static String alumniTable = "`alumnus_v3`";

    public BaiduInitialDAO() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?serverTimezone=UTC", "root", "123456");
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Alumni> getAllName() {
        try {
            List<Alumni> alumniList = new ArrayList<>();
            String sql = "select website,name from " + alumniTable;
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String website = resultSet.getString(1);
                String name = resultSet.getString(2);
                Alumni alumni = new Alumni();
                alumni.setWebsite(website);
                alumni.setName(name);
                alumniList.add(alumni);
            }
            return alumniList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateInitial(String website, String initial) {
        try {
            String sql = "UPDATE " + alumniTable + " SET `initial` = ?  WHERE `website` = ? ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, initial);
            preparedStatement.setString(2, website);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getAlumniTable() {
        return alumniTable;
    }

    public static void setAlumniTable(String alumniTable) {
        BaiduInitialDAO.alumniTable = alumniTable;
    }
}
