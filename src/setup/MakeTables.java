package setup;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import models.DBProps;

public class MakeTables {

  public static void main(String[] args) throws 
    IOException, SQLException, ClassNotFoundException 
  {
    Properties props = DBProps.getProps();
    System.out.format("\n---- database = %s\n", DBProps.which);
    
    Helper.createTables(props);
    Helper.populateTables(props);
    
    //Helper.dumpTables(props);
  }
}
