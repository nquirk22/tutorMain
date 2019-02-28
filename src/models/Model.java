package models;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Model {
  public int getId() { return 0; }
  
  /* set the object from a ResultSet table row */
  abstract void load(ResultSet rs);
  
  /* create a new record from the property values (minus id) */
  abstract void insert();
  
  /* modify an existing record (non-zero id) with property values */
  abstract void update();
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (! this.getClass().equals(obj.getClass()) ) {
      return false;
    }
    return this.getId() == ((Model)obj).getId();
  }

  @Override
  public int hashCode() {
    return getId();
  }
}
