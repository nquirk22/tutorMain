package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Tutor extends Model {

    private int id = 0;
    private String name;
    private String email;
    private int subject_id;

    // corresponds to a database table
    public static final String TABLE = "tutor";

    // must have default constructor accessible to the package
    Tutor() {
    }

    public Tutor(String name, String email, int subject_id) {
        this.name = name;
        this.email = email;
        this.subject_id = subject_id;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }

    public int getSubjectId() {
        return subject_id;
    }
    
    public String getTutorSubjectName() {
        return getTutorSubject().getName();
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setEmail(String email) {
        this.email = email;
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
            email = rs.getString("email");
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
                    "insert into %s (name,email,subject_id) values (?,?,?)", TABLE);
            PreparedStatement st = cx.prepareStatement(sql);
            int i = 0;
            st.setString(++i, name);
            st.setString(++i, email);
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
                    "update %s set name=?,email=?,subject_id=? where id=?", TABLE);
            PreparedStatement st = cx.prepareStatement(sql);
            int i = 0;
            st.setString(++i, name);
            st.setString(++i, email);
            st.setInt(++i, subject_id);
            st.setInt(++i, id);
            st.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getClass() + ":" + ex.getMessage());
        }
    }
    
    public Subject getTutorSubject() {
        Subject subject = ORM.findOne(Subject.class,
                    "where id=?",
                    new Object[]{this.subject_id}
            );
        return subject;
    }

    @Override
    public String toString() {
        return String.format("(%s,%s,%s,%s)", id, name, email, subject_id);
    }
}
