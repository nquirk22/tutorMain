package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Tutor extends Model {

    private int id = 0;
    private String name;
    private int subject_id;

    // corresponds to a database table
    public static final String TABLE = "tutor";

    // must have default constructor accessible to the package
    Tutor() {
    }

    public Tutor(String name, int subject_id) {
        this.name = name;
        this.subject_id = subject_id;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSubjectId() {
        return subject_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubjectId(int subject_id) {
        this.subject_id = subject_id;
    }

    // used for SELECT operations in ORM.load, ORM.findAll, ORM.findOne
    @Override
    void load(ResultSet rs) {
        try {
            id = rs.getInt("id");
            name = rs.getString("name");
            subject_id = rs.getInt("subject_id");
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
                    "insert into %s (name,subject_id) values (?,?)", TABLE);
            PreparedStatement st = cx.prepareStatement(sql);
            int i = 0;
            st.setString(++i, name);
            st.setInt(++i, subject_id);
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
                    "update %s set name=?,subject_id=? where id=?", TABLE);
            PreparedStatement st = cx.prepareStatement(sql);
            int i = 0;
            st.setString(++i, name);
            st.setInt(++i, subject_id);
            st.setInt(++i, id);
            st.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getClass() + ":" + ex.getMessage());
        }
    }

    @Override
    public String toString() {
        return String.format("(%s,%s,%s)", id, name, subject_id);
    }
}
