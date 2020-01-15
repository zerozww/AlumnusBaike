package whu.alumnispider.DAO;

import whu.alumnispider.utilities.Alumni;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaiduAlumniDAO {
    private Connection conn = null;
    private Statement stmt = null;

    private static String alumniTable = "`alumnis_v2`";
    private static String candidateTable = "`candidates`";
    private static String websiteTable = "`websites`";


    public BaiduAlumniDAO() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?serverTimezone=UTC", "root", "123456");
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public int add(Alumni alumni) {
        try {
            String sql = "INSERT INTO `test`.`" + alumniTable + "`(`name`, `job`,`education`,`illegal`,`website`,`picture`," +
                    "`content`,`label`,`main_content`,`brief_intro`,`time`,`table_content`,`education_degree`,`education_field`," +
                    "`education_time`,`retired`,`field`,`initial`,`alive`,`location`,`birthday`)" +
                    "VALUES (?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, alumni.getName());
            preparedStatement.setString(2, alumni.getJob());
            preparedStatement.setString(3, alumni.getEducation());
            preparedStatement.setBoolean(4, alumni.isIllegal());
            preparedStatement.setString(5, alumni.getWebsite());
            preparedStatement.setString(6, alumni.getPicture());
            preparedStatement.setString(7, alumni.getContent());
            preparedStatement.setString(8, alumni.getLabel());
            preparedStatement.setString(9, alumni.getMainContent());
            preparedStatement.setString(10, alumni.getBriefIntro());
            preparedStatement.setTimestamp(11, alumni.getTime());
            preparedStatement.setString(12, alumni.getTableContent());
            preparedStatement.setString(13, alumni.getEducationDegree());
            preparedStatement.setString(14, alumni.getEducationField());
            preparedStatement.setString(15, alumni.getEducationTime());
            preparedStatement.setBoolean(16, alumni.isRetired());
            preparedStatement.setString(17, alumni.getField());
            preparedStatement.setString(18, alumni.getInitial());
            preparedStatement.setBoolean(19, alumni.isAlive());
            preparedStatement.setString(20, alumni.getLocation());
            preparedStatement.setString(21, alumni.getBirthday());

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public List<String> getCandidate() {
        try {
            List<String> rets = new ArrayList<>();
            String sql = "SELECT name FROM " + candidateTable + " WHERE state = 0 ";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String ret = resultSet.getString(1);
                rets.add(ret);
            }
            return rets;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getCandidate(int first, int last) {
        try {
            List<String> rets = new ArrayList<>();
            String sql = "SELECT name FROM " + candidateTable + " WHERE state = 0 limit " + first + "," + last;
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String ret = resultSet.getString(1);
                rets.add(ret);
            }
            return rets;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addWebsite(String website, String name) {
        try {
            String sql = "INSERT INTO " + websiteTable + " (`website`,`schoolname`) values(?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, website);
            preparedStatement.setString(2, name);
            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int updateCandidate(String name) {
        try {
            String sql = "UPDATE " + candidateTable + " SET state = 1 WHERE name = '" + name + "'";
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<String> getWebsite() {
        try {
            List<String> rets = new ArrayList<>();
            String sql = "SELECT website FROM " + websiteTable + " WHERE `time` is NULL";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String ret = resultSet.getString(1);
                rets.add(ret);
            }
            return rets;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getCandidateTemp() {
        try {
            List<String> rets = new ArrayList<>();
            String sql = "select website from `websites` WHERE schoolName is not null AND website not in (SELECT website from `alumnis_v2`)";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String ret = resultSet.getString(1);
                rets.add(ret);
            }
            return rets;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAlumniTable() {
        return alumniTable;
    }

    public static void setAlumniTable(String alumniTable) {
        BaiduAlumniDAO.alumniTable = alumniTable;
    }

    public static String getCandidateTable() {
        return candidateTable;
    }

    public static void setCandidateTable(String candidateTable) {
        BaiduAlumniDAO.candidateTable = candidateTable;
    }

    public static String getWebsiteTable() {
        return websiteTable;
    }

    public static void setWebsiteTable(String websiteTable) {
        BaiduAlumniDAO.websiteTable = websiteTable;
    }
}
