package whu.alumnispider.DAO;

import whu.alumnispider.utilities.Alumni;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaiduConnDAO {
    private Connection conn = null;
    private Statement stmt = null;


    public BaiduConnDAO() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?serverTimezone=UTC", "root", "zww123456");
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Alumni> getFeiFan2zww() {
        try {
            List<Alumni> alumniList = new ArrayList<>();
            String sql = "select website,birthday,location,alive from `alumnus_v2`";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String website = resultSet.getString(1);
                String birthday = resultSet.getString(2);
                String location = resultSet.getString(3);
                boolean alive = resultSet.getBoolean(4);
                Alumni alumni = new Alumni();
                alumni.setWebsite(website);
                alumni.setBirthday(birthday);
                alumni.setLocation(location);
                alumni.setAlive(alive);
                alumniList.add(alumni);
            }
            return alumniList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateFeiFan2zww(Alumni alumni) {
        String birthday = alumni.getBirthday();
        if (birthday != null)
            birthday = birthday.substring(0, 4);
        String location = alumni.getLocation();
        boolean alive = alumni.isAlive();
        String website = alumni.getWebsite();
        try {
            String sql = "UPDATE `alumnis_v2` SET `birthday` = ?,`location`=?,`alive`=?  WHERE `website` = ? ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, birthday);
            preparedStatement.setString(2, location);
            preparedStatement.setBoolean(3, alive);
            preparedStatement.setString(4, website);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int updateCandidateFF(String name) {
        try {
            String sql = "UPDATE `candidates_feifan` SET state = 1 WHERE name = '" + name + "'";
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<String> getCandidateFF() {
        try {
            List<String> rets = new ArrayList<>();
            String sql = "SELECT name FROM `candidates_feifan` WHERE state = 0 ";
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
}
