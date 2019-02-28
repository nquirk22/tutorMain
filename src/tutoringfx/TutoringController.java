/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tutoringfx;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import models.DBProps;
import models.ORM;
import models.Student;
import models.Tutor;

/**
 * FXML Controller class
 *
 * @author Nathan Quirk
 */
public class TutoringController implements Initializable {

    @FXML
    private ListView<Student> studentList;

    @FXML
    private ListView<Tutor> tutorList;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ORM.init(DBProps.getProps());

        Collection<Student> students = ORM.findAll(Student.class);
        for (Student student : students) {
            studentList.getItems().add(student);
        }

        Collection<Tutor> tutors = ORM.findAll(Tutor.class);
        for (Tutor tutor : tutors) {
            tutorList.getItems().add(tutor);
        }
        
        StudentCellCallback studentCellCallback = new StudentCellCallback();
        studentList.setCellFactory(studentCellCallback);
        
        TutorCellCallback tutorCellCallback = new TutorCellCallback();
        tutorList.setCellFactory(tutorCellCallback);
    }
}
