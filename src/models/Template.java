package models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Template extends Model {
  private int id = 0;
  
  // corresponds to a database table
  public static final String TABLE = "-- SOME TABLE --";
  
  // must have default constructor accessible to the package
  Template() {}
  
  @Override
  public int getId() { 
    return id;
  }
  
  // used for SELECT operations in ORM.load, ORM.findAll, ORM.findOne
  @Override
  void load(ResultSet rs) {
    try {
      id = rs.getInt("id");
    }
    catch (SQLException ex) {
      throw new RuntimeException(ex.getClass() + ":" + ex.getMessage());
    }
  }
  
  // user for INSERT operations in ORM.store (for new record)
  @Override
  void insert() {
    // use this to make you aware that it is being called
    // discard throw statement once you start coding here
    throw new UnsupportedOperationException("insert in " + this.getClass());
  }
  
  // used for UPDATE operations in ORM.store (for existing record)
  @Override
  void update() {
    // use this to make you aware that it is being called
    // discard throw statement once you start coding here
    throw new UnsupportedOperationException("update in " + this.getClass());
  }
}
