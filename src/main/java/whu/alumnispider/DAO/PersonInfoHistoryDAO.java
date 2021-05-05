package whu.alumnispider.DAO;

import whu.alumnispider.BaiduSearchProcessor;
import whu.alumnispider.utilities.Person;

import java.sql.*;

public class PersonInfoHistoryDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private String personInfoHistoryTable = "[person_info_history]";

    public PersonInfoHistoryDAO() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=peoplebigdata", "sa", "15212xXX!@#");
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public int insertPersonInfoHistory(Person person) {
        if (BaiduSearchProcessor.isTest)return 0;
        try {
            String sql = "INSERT INTO " + personInfoHistoryTable + "([id],[baike_id],[website],[name],[title]," +
                    "[picture_web],[picture_local],[brief_info],[table_content],[main_content],[label],[job]," +
                    "[field],[location],[organization],[position],[grade],[grade_name],[sex],[nation]," +
                    "[retired],[dead],[illegal],[initial],[birthday],[time],[status],[maxedu],[state]," +
                    "[province],[city],[area],[town],[birthplace],[addType],[locationlevel],[address],[mobile]," +
                    "[telphone],[email],[industry],[birthmonth],[remark],[history_id])" +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, person.getId());
            preparedStatement.setInt(2, person.getBaikeId());
            preparedStatement.setString(3, person.getWebsite());
            preparedStatement.setString(4, person.getName());
            preparedStatement.setString(5, person.getTitle());
            preparedStatement.setString(6, person.getPictureWeb());
            preparedStatement.setString(7, person.getPictureLocal());
            preparedStatement.setString(8, person.getBriefInfo());
            preparedStatement.setString(9, person.getTableContent());
            preparedStatement.setString(10, person.getMainContent());
            preparedStatement.setString(11, person.getLabel());
            preparedStatement.setString(12, person.getJob());
            preparedStatement.setString(13, person.getField());
            preparedStatement.setString(14, person.getLocation());
            preparedStatement.setString(15, person.getOrganization());
            preparedStatement.setString(16, person.getPosition());
            if (person.getGrade() == null) {
                preparedStatement.setNull(17, Types.INTEGER);
            } else {
                preparedStatement.setInt(17, person.getGrade());
            }
            preparedStatement.setString(18, person.getGradeName());
            if (person.getSex() == null) {
                preparedStatement.setNull(19, Types.BOOLEAN);
            } else {
                preparedStatement.setBoolean(19, person.getSex());
            }
            preparedStatement.setString(20, person.getNation());
            preparedStatement.setBoolean(21, person.isRetired());
            preparedStatement.setBoolean(22, person.isDead());
            preparedStatement.setBoolean(23, person.isIllegal());
            preparedStatement.setString(24, person.getInitial());
            if (person.getBirthday()==null){
                preparedStatement.setNull(25,Types.INTEGER);
            } else {
                preparedStatement.setInt(25, person.getBirthday());
            }
            preparedStatement.setTimestamp(26, person.getTime());
            preparedStatement.setBoolean(27,person.isStatus());
            preparedStatement.setString(28,person.getMaxedu());
            preparedStatement.setString(29,person.getState());
            preparedStatement.setString(30,person.getProvince());
            preparedStatement.setString(31,person.getCity());
            preparedStatement.setString(32,person.getArea());
            preparedStatement.setString(33,person.getTown());
            preparedStatement.setString(34,person.getBirthplace());
            preparedStatement.setInt(35,person.getAddType());
            if (person.getLocationLevel()==null){
                preparedStatement.setNull(36,Types.INTEGER);
            }else {
                preparedStatement.setInt(36,person.getLocationLevel());
            }
            preparedStatement.setString(37,person.getAddress());
            preparedStatement.setString(38,person.getMobile());
            preparedStatement.setString(39,person.getTelphone());
            preparedStatement.setString(40,person.getEmail());
            preparedStatement.setString(41,person.getIndustry());
            if (person.getBirthmonth()==null){
                preparedStatement.setNull(42,Types.INTEGER);
            }else {
                preparedStatement.setInt(42,person.getBirthmonth());
            }
            preparedStatement.setString(43,person.getRemark());
            preparedStatement.setString(44,person.getHistoryId());
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
