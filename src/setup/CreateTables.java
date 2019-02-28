package setup;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import models.DBProps;

public class CreateTables {

  public static void main(String[] args)
      throws ClassNotFoundException, SQLException, IOException 
  {
    Properties props = DBProps.getProps();
    System.out.format("\n---- database = %s\n", DBProps.which);

    Helper.createTables(props);
  }
}
