package whu.alumnispider.DAO;

import whu.alumnispider.utilities.Graduate;

import java.sql.*;

public class PersonGraduateDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private String graduateTableSqlserver = "[person_graduate_info]";

    public PersonGraduateDAO() {

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=alumnus", "sa", "15212xXX!@#");
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

    public Graduate getGraduate(String personId){
        try{
            String sql = "SELECT * from " + graduateTableSqlserver + " WHERE [person_id] = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,personId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                Graduate graduate = new Graduate();
                graduate.setId(resultSet.getString("id"));
                graduate.setPersonId(resultSet.getString("person_id"));
                graduate.setBaikeId(resultSet.getInt("baike_id"));
                graduate.setSchoolId(resultSet.getInt("school_id"));
                graduate.setPersonName(resultSet.getString("person_name"));
                graduate.setSchoolName(resultSet.getString("school_name"));
                graduate.setMatch_name(resultSet.getString("match_name"));
                graduate.setEducationEntire(resultSet.getString("education_entire"));
                graduate.setEducation(resultSet.getString("education"));
                graduate.setEducationDegree(resultSet.getString("education_degree"));
                graduate.setEducationField(resultSet.getString("education_field"));
                graduate.setEducationTime(resultSet.getString("education_time"));
                graduate.setTime(resultSet.getTimestamp("time"));
                graduate.setAddType(resultSet.getInt("addtype"));

                return graduate;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public int updateGraduate(Graduate graduate){
        try {
            String sql = "UPDATE " + graduateTableSqlserver + " SET [school_id]=?,[person_name]=?,[school_name]=?,"+
                    "[match_name]=?,[education_entire]=?,[education]=?,[education_degree]=?,[education_field]=?,"+
                    "[education_time]=?,[time]=? WHERE [id]=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1,graduate.getSchoolId());
            preparedStatement.setString(2,graduate.getPersonName());
            preparedStatement.setString(3,graduate.getSchoolName());
            preparedStatement.setString(4,graduate.getMatch_name());
            preparedStatement.setString(5,graduate.getEducationEntire());
            preparedStatement.setString(6,graduate.getEducation());
            preparedStatement.setString(7,graduate.getEducationDegree());
            preparedStatement.setString(8,graduate.getEducationField());
            preparedStatement.setString(9,graduate.getEducationTime());
            preparedStatement.setTimestamp(10,graduate.getTime());
            preparedStatement.setString(11,graduate.getId());

            return preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

}
