package whu.alumnispider.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import whu.alumnispider.utilities.*;

public class BaiduPictureDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private static String pictureTable = "`picture`";
    private static String alumniTable = "`alumnis_v2`";

    public BaiduPictureDAO() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?serverTimezone=UTC", "root", "zww123456");
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public int insertPictureId(String picture) {
        try {
            String insertSql = "INSERT INTO " + pictureTable + " (`picture`) SELECT ? FROM DUAL WHERE NOT EXISTS (SELECT" + alumniTable + "  FROM `picture` WHERE `picture` = ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSql);
            preparedStatement.setString(1, picture);
            preparedStatement.setString(2, picture);
            preparedStatement.executeUpdate();
            String sql = "SELECT LAST_INSERT_ID()";
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getPictureId(String picture) {
        try {
            String sql = "SELECT `id` FROM " + pictureTable + " WHERE `isSave` = 0 AND `picture` = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, picture);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<String> getPictures() {
        List<String> pictures = new ArrayList<>();
        try {
            String sql = "SELECT `picture` FROM " + alumniTable + " WHERE `picture` is not null AND `picture` not in (SELECT " + pictureTable + " FROM `picture` WHERE `isSave` = 1)";

            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                pictures.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pictures;
    }


    public void updatePictureSave(int id) {
        try {
            String sql = "UPDATE " + pictureTable + " SET `isSave` = 1 WHERE `id` = ? ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePictureLocation(String website, String picture_path) {
        try {
            String sql = "UPDATE " + alumniTable + " SET `picture_path` = ? WHERE `picture` = ? ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, picture_path);
            preparedStatement.setString(2, website);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<HashMap<String, String>> getPictureSaved() {
        List<HashMap<String, String>> pictures = new ArrayList<>();
        try {
            String sql = "SELECT `id`,`picture` FROM " + pictureTable + " WHERE `isSave` = 1";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                HashMap<String, String> map = new HashMap<>();
                int id = resultSet.getInt(1);
                String picture = resultSet.getString(2);
                map.put("picture", picture);
                map.put("id", Integer.toString(id));
                pictures.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pictures;
    }

    public static String getPictureTable() {
        return pictureTable;
    }

    public static void setPictureTable(String pictureTable) {
        BaiduPictureDAO.pictureTable = pictureTable;
    }

    public static String getAlumniTable() {
        return alumniTable;
    }

    public static void setAlumniTable(String alumniTable) {
        BaiduPictureDAO.alumniTable = alumniTable;
    }
}
