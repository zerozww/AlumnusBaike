package whu.alumnispider.DAO;

import whu.alumnispider.utilities.Alumni;
import whu.alumnispider.utilities.EducationDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaiduEducationDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private static String alumniTable = "`alumnis_v2`";

    public BaiduEducationDAO() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?serverTimezone=UTC", "root", "123456");
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Alumni> getEducationList() {

        try {
            List<Alumni> alumniList = new ArrayList<>();
            String sql = "select website,education from " + alumniTable;
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String website = resultSet.getString(1);
                String education = resultSet.getString(2);
                Alumni alumni = new Alumni();
                alumni.setWebsite(website);
                alumni.setEducation(education);
                alumniList.add(alumni);
            }
            return alumniList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateEducationDetail(EducationDetail educationDetail, String website) {
        try {
            String sql = "UPDATE " + alumniTable + " SET `education_degree` = ?,`education_field` = ?,`education_time` = ?  WHERE `website` = ? ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, educationDetail.getDegree());
            preparedStatement.setString(2, educationDetail.getField());
            preparedStatement.setString(3, educationDetail.getTime());
            preparedStatement.setString(4, website);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEducation(String education, String website) {
        try {
            String sql = "UPDATE " + alumniTable + " SET `education` = ? WHERE `website` = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, education);
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
        BaiduEducationDAO.alumniTable = alumniTable;
    }
}
