/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tutoringfx;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.ORM;
import models.Student;

/**
 * FXML Controller class
 *
 * @author Nathan
 */
public class AddStudentController implements Initializable {

    private TutoringController mainController;

    void setMainController(TutoringController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private void add(Event event) {
        try {
            String lastName = lastNameField.getText().trim();
            String firstName = firstNameField.getText().trim();
            if (lastName.isEmpty() || firstName.isEmpty()) {
                throw new ExpectedException("provide a first and last name");
            }
            String fullName = String.format("%s,%s", lastName, firstName);

            Student studentWithName
                    = ORM.findOne(Student.class, "where name=?", new Object[]{fullName});
            if (studentWithName != null) {
                throw new ExpectedException("existing student with same name");
            }
            // validation OK

            Student newStudent = new Student(fullName, Helper.currentDate());
            ORM.store(newStudent);

            // access the features of TutoringController
            ListView<Student> studentList = mainController.getStudentList();
            TextArea display = mainController.getDisplay();

            // reload studentList from database
            studentList.getItems().clear();
            Collection<Student> students = ORM.findAll(Student.class);
            for (Student student : students) {
                studentList.getItems().add(student);
            }

            // select in list and scroll to added student
            studentList.getSelectionModel().select(newStudent);
            studentList.scrollTo(newStudent);

            studentList.requestFocus();
            mainController.setLastFocused(studentList);

            // set text display to added book
            display.setText(Helper.info(newStudent));

            ((Button) event.getSource()).getScene().getWindow().hide();
        } catch (ExpectedException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(ex.getMessage());
            alert.show();
        }
    }

    @FXML
    private void cancel(Event event) {
        ((Button) event.getSource()).getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}
