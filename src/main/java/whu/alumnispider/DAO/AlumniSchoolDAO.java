package whu.alumnispider.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlumniSchoolDAO {
    private Connection conn = null;
    private Statement stmt = null;

    public AlumniSchoolDAO() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?serverTimezone=UTC", "root", "zww123456");
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getOtherName() {
        try {
            List<String> nameList = new ArrayList<>();
            String sql = "SELECT sa.`姓名` FROM school_alumnus as sa WHERE sa.`姓名` not in (SELECT candidates.`name` FROM candidates)";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                nameList.add(name);
            }
            return nameList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateOtherName(String name, String magName) {
        try {
            String sql = "UPDATE `school_alumnus` SET `姓名` = ?  WHERE `姓名` = ? ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, magName);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertCandidates(String name) {
        try {
            String sql = "INSERT INTO `candidates` (`name`) values(?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllName2Find() {
        try {
            List<String> nameList = new ArrayList<>();
            String sql = "SELECT name FROM candidate_2020_1_7";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                nameList.add(name);
            }
            return nameList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
