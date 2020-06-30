package whu.alumnispider.DAO;

import whu.alumnispider.utilities.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonInfoDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private String personInfoTableMysql = "`alumnus`.`person_info`";
    private String personInfoTableSqlserver = "[person_info]";

    public PersonInfoDAO() {
        /** mysql
         try {
         Class.forName("com.mysql.jdbc.Driver");
         conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/alumnus?serverTimezone=UTC&characterEncoding=utf8", "root", "zww123456");
         stmt = conn.createStatement();
         } catch (ClassNotFoundException | SQLException e) {
         e.printStackTrace();
         }
         */
        // sql server

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=alumnus", "sa", "zww123456");
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }
    /*
    public int insertPersonInfoMysql(Person person) {
        try {
            String sql = "INSERT INTO " + personInfoTableSqlserver + "(`id`,`baike_id`,`website`,`name`,`title`," +
                    "`picture_web`,`picture_local`,`brief_info`,`table_content`,`main_content`,`label`,`job`," +
                    "`field`,`location`,`organization`,`position`,`grade`,`grade_name`,`sex`,`nation`," +
                    "`retired`,`dead`,`illegal`,`initial`,`birthday`,`time`)" +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
            preparedStatement.setString(25, person.getBirthday());
            preparedStatement.setTimestamp(26, person.getTime());

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
     */
    public int insertPersonInfoSqlserver(Person person) {
        try {
            String sql = "INSERT INTO " + personInfoTableSqlserver + "([id],[baike_id],[website],[name],[title]," +
                    "[picture_web],[picture_local],[brief_info],[table_content],[main_content],[label],[job]," +
                    "[field],[location],[organization],[position],[grade],[grade_name],[sex],[nation]," +
                    "[retired],[dead],[illegal],[initial],[birthday],[time],[status],[maxedu],[state]," +
                    "[province],[city],[area],[town],[birthplace])" +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * @return 查询成功返回List<Person>，查询失败返回null
     * @description
     */
    public List<Person> getPersonInfoList() {
        List<Person> personInfoList = new ArrayList<>();
        try {
            String sql = "select * from " + personInfoTableSqlserver + "where [status] = 0";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                Person person = new Person();

                person.setId(resultSet.getString("id"));
                person.setBaikeId(resultSet.getInt("baike_id"));
                person.setWebsite(resultSet.getString("website"));
                person.setName(resultSet.getString("name"));
                person.setTitle(resultSet.getString("title"));
                person.setPictureWeb(resultSet.getString("picture_web"));
                person.setPictureLocal(resultSet.getString("picture_local"));
                person.setBriefInfo(resultSet.getString("brief_info"));
                person.setTableContent(resultSet.getString("table_content"));
                person.setMainContent(resultSet.getString("main_content"));
                person.setLabel(resultSet.getString("label"));
                person.setJob(resultSet.getString("job"));
                person.setField(resultSet.getString("field"));
                person.setLocation(resultSet.getString("location"));
                person.setOrganization(resultSet.getString("organization"));
                person.setPosition(resultSet.getString("position"));
                person.setGrade(resultSet.getInt("grade"));
                person.setGradeName(resultSet.getString("grade_name"));
                person.setSex(resultSet.getBoolean("sex"));
                person.setNation(resultSet.getString("nation"));
                person.setRetired(resultSet.getBoolean("retired"));
                person.setDead(resultSet.getBoolean("dead"));
                person.setIllegal(resultSet.getBoolean("illegal"));
                person.setInitial(resultSet.getString("initial"));
                person.setBirthday(resultSet.getInt("birthday"));
                person.setTime(resultSet.getTimestamp("time"));
                person.setStatus(resultSet.getBoolean("status"));
                person.setMaxedu(resultSet.getString("maxedu"));
                person.setState(resultSet.getString("state"));
                person.setProvince(resultSet.getString("province"));
                person.setCity(resultSet.getString("city"));
                person.setArea(resultSet.getString("area"));
                person.setTown(resultSet.getString("town"));
                person.setBirthplace(resultSet.getString("birthplace"));

                personInfoList.add(person);
            }
            return personInfoList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Person> getPersonInfoList(int length) {
        List<Person> personInfoList = new ArrayList<>();
        try {
            String sql = "select top " + length + " * from " + personInfoTableSqlserver + "where [status] = 0";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                Person person = new Person();

                person.setId(resultSet.getString("id"));
                person.setBaikeId(resultSet.getInt("baike_id"));
                person.setWebsite(resultSet.getString("website"));
                person.setName(resultSet.getString("name"));
                person.setTitle(resultSet.getString("title"));
                person.setPictureWeb(resultSet.getString("picture_web"));
                person.setPictureLocal(resultSet.getString("picture_local"));
                person.setBriefInfo(resultSet.getString("brief_info"));
                person.setTableContent(resultSet.getString("table_content"));
                person.setMainContent(resultSet.getString("main_content"));
                person.setLabel(resultSet.getString("label"));
                person.setJob(resultSet.getString("job"));
                person.setField(resultSet.getString("field"));
                person.setLocation(resultSet.getString("location"));
                person.setOrganization(resultSet.getString("organization"));
                person.setPosition(resultSet.getString("position"));
                person.setGrade(resultSet.getInt("grade"));
                person.setGradeName(resultSet.getString("grade_name"));
                person.setSex(resultSet.getBoolean("sex"));
                person.setNation(resultSet.getString("nation"));
                person.setRetired(resultSet.getBoolean("retired"));
                person.setDead(resultSet.getBoolean("dead"));
                person.setIllegal(resultSet.getBoolean("illegal"));
                person.setInitial(resultSet.getString("initial"));
                person.setBirthday(resultSet.getInt("birthday"));
                person.setTime(resultSet.getTimestamp("time"));
                person.setStatus(resultSet.getBoolean("status"));
                person.setMaxedu(resultSet.getString("maxedu"));
                person.setState(resultSet.getString("state"));
                person.setProvince(resultSet.getString("province"));
                person.setCity(resultSet.getString("city"));
                person.setArea(resultSet.getString("area"));
                person.setTown(resultSet.getString("town"));
                person.setBirthplace(resultSet.getString("birthplace"));

                personInfoList.add(person);
            }
            return personInfoList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @description
     * @param id 人物的唯一识别id
     * @return int 正常运行返回1，错误返回-1
     */
    public int updatePersonStatus(String id){
        try{
            String sql = "update " + personInfoTableSqlserver + " set [status] = 1 where [id] = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,id);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Person> getPersonPictureList(){
        List<Person> personInfoList = new ArrayList<>();
        try {
            String sql = "select * from " + personInfoTableSqlserver + "where [picture_local] is null AND [picture_web] is not null";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                Person person = new Person();

                person.setId(resultSet.getString("id"));
                person.setBaikeId(resultSet.getInt("baike_id"));
                person.setWebsite(resultSet.getString("website"));
                person.setName(resultSet.getString("name"));
                /*
                person.setTitle(resultSet.getString("title"));
                 */
                person.setPictureWeb(resultSet.getString("picture_web"));
                /*
                person.setPictureLocal(resultSet.getString("picture_local"));
                person.setBriefInfo(resultSet.getString("brief_info"));
                person.setTableContent(resultSet.getString("table_content"));
                person.setMainContent(resultSet.getString("main_content"));
                person.setLabel(resultSet.getString("label"));
                person.setJob(resultSet.getString("job"));
                person.setField(resultSet.getString("field"));
                person.setLocation(resultSet.getString("location"));
                person.setOrganization(resultSet.getString("organization"));
                person.setPosition(resultSet.getString("position"));
                person.setGrade(resultSet.getInt("grade"));
                person.setGradeName(resultSet.getString("grade_name"));
                person.setSex(resultSet.getBoolean("sex"));
                person.setNation(resultSet.getString("nation"));
                person.setRetired(resultSet.getBoolean("retired"));
                person.setDead(resultSet.getBoolean("dead"));
                person.setIllegal(resultSet.getBoolean("illegal"));
                person.setInitial(resultSet.getString("initial"));
                person.setBirthday(resultSet.getInt("birthday"));
                person.setTime(resultSet.getTimestamp("time"));
                person.setStatus(resultSet.getBoolean("status"));
                person.setMaxedu(resultSet.getString("maxedu"));
                person.setState(resultSet.getString("state"));
                person.setProvince(resultSet.getString("province"));
                person.setCity(resultSet.getString("city"));
                person.setArea(resultSet.getString("area"));
                person.setTown(resultSet.getString("town"));
                person.setBirthplace(resultSet.getString("birthplace"));
                 */
                personInfoList.add(person);
            }
            return personInfoList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Person> getPersonPictureList(int length){
        List<Person> personInfoList = new ArrayList<>();
        try {
            String sql = "select top " + length + " * from " + personInfoTableSqlserver + "where [picture_local] is null AND [picture_web] is not null";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                Person person = new Person();

                person.setId(resultSet.getString("id"));
                person.setBaikeId(resultSet.getInt("baike_id"));
                person.setWebsite(resultSet.getString("website"));
                person.setName(resultSet.getString("name"));
                /*
                person.setTitle(resultSet.getString("title"));
                 */
                person.setPictureWeb(resultSet.getString("picture_web"));
                /*
                person.setPictureLocal(resultSet.getString("picture_local"));
                person.setBriefInfo(resultSet.getString("brief_info"));
                person.setTableContent(resultSet.getString("table_content"));
                person.setMainContent(resultSet.getString("main_content"));
                person.setLabel(resultSet.getString("label"));
                person.setJob(resultSet.getString("job"));
                person.setField(resultSet.getString("field"));
                person.setLocation(resultSet.getString("location"));
                person.setOrganization(resultSet.getString("organization"));
                person.setPosition(resultSet.getString("position"));
                person.setGrade(resultSet.getInt("grade"));
                person.setGradeName(resultSet.getString("grade_name"));
                person.setSex(resultSet.getBoolean("sex"));
                person.setNation(resultSet.getString("nation"));
                person.setRetired(resultSet.getBoolean("retired"));
                person.setDead(resultSet.getBoolean("dead"));
                person.setIllegal(resultSet.getBoolean("illegal"));
                person.setInitial(resultSet.getString("initial"));
                person.setBirthday(resultSet.getInt("birthday"));
                person.setTime(resultSet.getTimestamp("time"));
                person.setStatus(resultSet.getBoolean("status"));
                person.setMaxedu(resultSet.getString("maxedu"));
                person.setState(resultSet.getString("state"));
                person.setProvince(resultSet.getString("province"));
                person.setCity(resultSet.getString("city"));
                person.setArea(resultSet.getString("area"));
                person.setTown(resultSet.getString("town"));
                person.setBirthplace(resultSet.getString("birthplace"));
                 */

                personInfoList.add(person);
            }
            return personInfoList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int updatePictureLocal(String id,String pictureLocal){
        try{
            String sql = "update " + personInfoTableSqlserver + " set [picture_local] = ? where [id] = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,pictureLocal);
            preparedStatement.setString(2,id);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * @description
     * @return 返回person_info中status=0的记录数，若查询失败则返回-1
     */
    public int getPersonInfoCount(){
        try{
            String sql = "select count(*) from " + personInfoTableSqlserver + " where [status] = 0";
            ResultSet resultSet = stmt.executeQuery(sql);
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int updatePersonMaxedu(String maxedu,String id){
        try{
            String sql = "update " + personInfoTableSqlserver + " set [maxedu] = ? where [id] = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,maxedu);
            preparedStatement.setString(2,id);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


}
