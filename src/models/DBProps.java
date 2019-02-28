package models;

import java.util.Properties;

public class DBProps {
  public enum DB {
    mysql, sqlite
  }

  public static final DB which = DB.mysql;  // DB.mysql or DB.sqlite

  public static Properties getProps() {
    Properties db = new Properties();
    switch (which) {
      case mysql:
        db.put("url", "jdbc:mysql://localhost/test");
        db.put("username", "guest");
        db.put("password", "");
        break;
      case sqlite:
        db.put("url", "jdbc:sqlite:database.sqlite");
        break;
    }
    return db;
  }
}
