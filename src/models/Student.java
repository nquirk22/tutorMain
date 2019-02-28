package models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Student extends Model {

    private int id = 0;
    private String name;
    private Date enrolled;

    // corresponds to a database table
    public static final String TABLE = "student";

    // must have default constructor accessible to the package
    Student() {
    }

    public Student(String name, Date enrolled) {
        this.name = name;
        this.enrolled = enrolled;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getEnrolled() {
        return enrolled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEnrolled(Date enrolled) {
        this.enrolled = enrolled;
    }

    // used for SELECT operations in ORM.load, ORM.findAll, ORM.findOne
    @Override
    void load(ResultSet rs) {
        try {
            id = rs.getInt("id");
            name = rs.getString("name");
            enrolled = rs.getDate("enrolled");
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getClass() + ":" + ex.getMessage());
        }
    }

    // user for INSERT operations in ORM.store (for new record)
    @Override
    void insert() {
        Connection cx = ORM.connection();
        try {
            String sql = String.format(
                    "insert into %s (name,enrolled) values (?,?)", TABLE);
            PreparedStatement st = cx.prepareStatement(sql);
            int i = 0;
            st.setString(++i, name);
            st.setDate(++i, enrolled);
            st.executeUpdate();
            id = ORM.getMaxId(TABLE);
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getClass() + ":" + ex.getMessage());
        }
    }

    // used for UPDATE operations in ORM.store (for existing record)
    @Override
    void update() {
        Connection cx = ORM.connection();
        try {
            String sql = String.format(
                    "update %s set name=? where id=?", TABLE);
            PreparedStatement st = cx.prepareStatement(sql);
            int i = 0;
            st.setString(++i, name);
            st.setInt(++i, id);
            st.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getClass() + ":" + ex.getMessage());
        }
    }

    @Override
    public String toString() {
        return String.format("(%s,%s)", id, name);
    }
}
