package whu.alumnispider.DAO;

import whu.alumnispider.utilities.Alumni;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaiduRetiredDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private static String alumniTable = "`alumnis_v2`";

    public BaiduRetiredDAO() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?serverTimezone=UTC", "root", "zww123456");
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Alumni> getJobList() {
        try {
            List<Alumni> alumniList = new ArrayList<>();
            String sql = "select website,job from " + alumniTable;
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String website = resultSet.getString(1);
                String job = resultSet.getString(2);
                Alumni alumni = new Alumni();
                alumni.setWebsite(website);
                alumni.setJob(job);
                alumniList.add(alumni);
            }
            return alumniList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateRetired(boolean isRetired, String website) {
        try {
            String sql = "UPDATE " + alumniTable + " SET `retired` = ?  WHERE `website` = ? ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setBoolean(1, isRetired);
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
        BaiduRetiredDAO.alumniTable = alumniTable;
    }
}
