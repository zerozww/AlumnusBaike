package whu.alumnispider.DAO;

import whu.alumnispider.utilities.Graduate;

import java.sql.*;

public class PersonGraduateInfoDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private String graduateTableSqlserver = "[person_graduate_info]";

    public PersonGraduateInfoDAO() {

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=alumnus", "sa", "zww123456");
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public int insertGraduateSqlserver(Graduate graduate) {
        try {
            String sql = "insert into " + graduateTableSqlserver + "([id],[person_id],[baike_id],[school_id],[person_name]," +
                    "[school_name],[match_name],[education],[education_degree],[education_field],[education_time]," +
                    "[time],[addtype],[education_entire]) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, graduate.getId());
            preparedStatement.setString(2, graduate.getPersonId());
            preparedStatement.setInt(3, graduate.getBaikeId());
            preparedStatement.setInt(4, graduate.getSchoolId());
            preparedStatement.setString(5, graduate.getPersonName());
            preparedStatement.setString(6, graduate.getSchoolName());
            preparedStatement.setString(7, graduate.getMatch_name());
            preparedStatement.setString(8, graduate.getEducation());
            preparedStatement.setString(9, graduate.getEducationDegree());
            preparedStatement.setString(10, graduate.getEducationField());
            preparedStatement.setString(11, graduate.getEducationTime());
            preparedStatement.setTimestamp(12, graduate.getTime());
            preparedStatement.setInt(13,graduate.getAddType());
            preparedStatement.setString(14,graduate.getEducationEntire());
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
