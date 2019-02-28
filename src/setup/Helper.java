package setup;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import models.DBProps;

class Helper {

  static String getResourceContent(String filename) throws IOException {
    InputStream istr = Helper.class.getResourceAsStream(filename);
    if (istr == null) {
      throw new IOException("Missing file: " + filename);
    }
    Scanner s = new Scanner(istr).useDelimiter("\\A");
    return s.next();
  }

  public static void createTables(Properties props) throws
      IOException, SQLException, ClassNotFoundException {

    String url = props.getProperty("url");
    String username = props.getProperty("username");
    String password = props.getProperty("password");
    String driver = props.getProperty("driver");
    if (driver != null) {
      Class.forName(driver); // load driver if necessary
    }
    Connection cx = DriverManager.getConnection(url, username, password);

    @SuppressWarnings("unchecked")
    ArrayList<String> create_order
        = new ArrayList(Arrays.asList("subject", "student", "tutor", "interaction"));

    @SuppressWarnings("unchecked")
    ArrayList<String> drop_order = (ArrayList<String>) create_order.clone();
    Collections.reverse(drop_order);

    Statement stmt = cx.createStatement();

    System.out.format("\n---- drop tables\n");
    for (String table : drop_order) {
      String sql = String.format("drop table if exists %s", table);
      System.out.println(sql);
      stmt.execute(sql);
    }

    System.out.format("\n---- create tables\n");
    for (String table : create_order) {
      String filename = String.format("tables/%s-%s.sql", table, DBProps.which);
      String sql = getResourceContent(filename).trim();
      System.out.println(sql);
      stmt.execute(sql);
    }
  }

  static void populateTables(Properties props)
      throws SQLException, ClassNotFoundException, IOException {
    String url = props.getProperty("url");
    String username = props.getProperty("username");
    String password = props.getProperty("password");
    String driver = props.getProperty("driver");
    if (driver != null) {
      Class.forName(driver); // load driver if necessary
    }
    Connection cx = DriverManager.getConnection(url, username, password);
    PreparedStatement stmt;

    // these are for internal use to correspond names to table id values
    Map<String, Integer> subjectId = new HashMap<>();
    Map<String, Integer> tutorId = new HashMap<>();
    Map<String, Integer> studentId = new HashMap<>();
    int id;

    //========================================================
    System.out.println("\n--- add subjects");

    stmt = cx.prepareStatement("insert into subject (name) values(?)");

    String[] subjects = {
      "Chemistry", "Math", "Physics", "Biology",};

    id = 0;
    for (String name : subjects) {
      stmt.setString(1, name);
      stmt.execute();
      subjectId.put(name, ++id);
      System.out.printf("%s: %s\n", id, name);
    }
    
    //========================================================
    System.out.println("\n--- add tutors");

    stmt = cx.prepareStatement(
      String.format("insert into tutor (name,email,subject_id) values(?,?,?)")
    );
    
    Object tutors[][] = new Object[][]{
      new Object[]{"Guo,Cheryl", "guo33@yahoo.com", subjectId.get("Chemistry")}, 
      new Object[]{"Lanier,Jennifer", "lanije@outlook.com", subjectId.get("Chemistry")}, 
      new Object[]{"Sippel,John", "sipp45@live.com", subjectId.get("Physics")}, 
      new Object[]{"Brady,Chad", "chadbrad@aol.com", subjectId.get("Biology")}, 
      new Object[]{"Mazer,Dominique", "domaz11@juno.com", subjectId.get("Physics")}, 
      new Object[]{"Wooding,Bernard", "berwood@yandex.com", subjectId.get("Math")}, 
    };

    id = 0;
    for (Object[] triple : tutors) {
      stmt.setString(1, (String) triple[0]);
      stmt.setString(2, (String) triple[1]);
      stmt.setInt(3, (Integer) triple[2]);
      stmt.execute();
      tutorId.put((String) triple[0], ++id);
      System.out.printf("%s: %s\n", id, Arrays.toString(triple));
    }

    //========================================================
    System.out.println("\n--- add students");

    stmt = cx.prepareStatement(
      String.format("insert into student (name,enrolled) values(?,?)")
    );
    String students[][] = new String[][]{
      new String[]{"Carson,Susy", "2015-07-28"},
      new String[]{"Bard,Thomas", "2014-12-28"},
      new String[]{"Mcgillis,Alysa", "2013-07-20"},
      new String[]{"Carson,James", "2011-11-21"},
      new String[]{"Liggett,James", "2016-01-08"},
      new String[]{"Rothman,Alonso", "2014-11-21"},
      new String[]{"Collier,Rosanne", "2018-07-12"},
      new String[]{"Farney,Tommy", "2014-03-25"},
    };

    id = 0;
    for (String[] triple : students) {
      stmt.setString(1, triple[0]);
      stmt.setDate(2, Date.valueOf(triple[1]));
      stmt.execute();
      studentId.put(triple[0], ++id);
      System.out.printf("%s: %s\n", id, Arrays.toString(triple));
    }

    //========================================================
    System.out.println("\n--- add interactions");
    
    stmt = cx.prepareStatement(
      String.format("insert into interaction (tutor_id,student_id,report) values(?,?,?)"));
    
    String interactions[][] = new String[][]{
      new String[]{"Lanier,Jennifer", "Collier,Rosanne", 
      "Most likely a hopeless case.\n"
      + "Wants help with pre-calc but cannot\n"
      + "even solve an equation with one variable!"
      },
      new String[]{"Guo,Cheryl", "Carson,James", 
      "We went through the Periodic Table.\n"
      + "Struggling with basic concepts like the\n "
      + "significance of H-2-O."
      },
      new String[]{"Sippel,John", "Collier,Rosanne", 
        "Discussed four fundamental interactions:\n"
        + "gravitation, electromagnetism,\n"
        + "the weak and strong interactions."
        },
      new String[]{"Guo,Cheryl", "Farney,Tommy", ""},
      new String[]{"Brady,Chad", "Rothman,Alonso", 
        "Finally understands that biology is the\n"
        + "study of living organisms."
      },
      new String[]{"Wooding,Bernard", "Farney,Tommy", ""},
      new String[]{"Guo,Cheryl", "Bard,Thomas", ""},
      new String[]{"Sippel,John", "Mcgillis,Alysa", ""},
    };
    
    for (String[] triple : interactions) {
      String tutor = triple[0];
      String student = triple[1];
      String report = triple[2];
      stmt.setInt(1, tutorId.get(tutor));
      stmt.setInt(2, studentId.get(student));
      stmt.setString(3, report);
      stmt.execute();
      
      System.out.format("*** %s <=> %s ***\n%s\n\n", tutor, student, report);
    }
  }
}
