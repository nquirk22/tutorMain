package tutoringfx;

import models.Student;
import models.Tutor;

public class Helper {

    public static String info(Student student) {
        return String.format(
                "id: %s\n"
                + "name: %s\n"
                + "enrolled: %s\n"
                + "tutored in: %s\n",
                student.getId(),
                student.getName(),
                student.getEnrolled(),
                student.getSubjectNames()
        );
    }

    public static String info(Tutor tutor) {
        return String.format(
                "id: %s\n"
                + "name: %s\n"
                + "email: %s\n"
                + "subject: %s\n",
                tutor.getId(),
                tutor.getName(),
                tutor.getEmail(),
                "MISSING SUBJECT ID" //tutor.getSubjectId()
        );
    }

    public static java.sql.Date currentDate() {
        long now = new java.util.Date().getTime();
        java.sql.Date date = new java.sql.Date(now);
        return date;
    }
}
