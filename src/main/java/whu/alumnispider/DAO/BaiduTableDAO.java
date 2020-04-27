package whu.alumnispider.DAO;

import java.sql.*;

public class BaiduTableDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private static String alumniTable = "`alumnis_v2`";

    public BaiduTableDAO() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?serverTimezone=UTC", "root", "zww123456");
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public String getTableContent(String website) {
        try {
            String sql = "SELECT table_content FROM " + alumniTable + " WHERE `website` = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, website);
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet.next()) {
                String tableContent = resultSet.getString(1);
                return tableContent;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAlumniTable() {
        return alumniTable;
    }

    public static void setAlumniTable(String alumniTable) {
        BaiduTableDAO.alumniTable = alumniTable;
    }
}
