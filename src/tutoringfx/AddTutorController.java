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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.ORM;
import models.Subject;
import models.Tutor;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * FXML Controller class
 *
 * @author Nathan
 */
public class AddTutorController implements Initializable {

    private TutoringController mainController;

    void setMainController(TutoringController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> subjectSelection;

    @FXML
    private void add(Event event) {
        try {
            //========= validate email
            EmailValidator validator = EmailValidator.getInstance();
            String email = emailField.getText().trim();
            if (!validator.isValid(email)) {
                throw new ExpectedException("enter a valid email address");
            }
            
            //========= validate name
            String lastName = lastNameField.getText().trim();
            String firstName = firstNameField.getText().trim();
            if (lastName.isEmpty() || firstName.isEmpty()) {
                throw new ExpectedException("provide a first and last name");
            }
            String fullName = String.format("%s,%s", lastName, firstName);
            
            Tutor tutorWithName = 
                    ORM.findOne(Tutor.class, "where name=?", new Object[]{fullName});
            
            if (tutorWithName != null) {
                throw new ExpectedException("existing tutor with same name");
            }
            
            //========= validate subject
            String subjectName = subjectSelection.getSelectionModel().getSelectedItem();
            if (subjectName == null) {
                throw new ExpectedException("select a subject");
            }
           
            Subject subject = ORM.findOne(Subject.class, "where name=?", new Object[]{subjectName});
                        
            Tutor newTutor = new Tutor(fullName, email, subject.getId());
            ORM.store(newTutor);

            // access the features of TutoringController
            ListView<Tutor> tutorList = mainController.getTutorList();
            TextArea display = mainController.getDisplay();

            // reload studentList from database
            tutorList.getItems().clear();
            Collection<Tutor> tutors = ORM.findAll(Tutor.class);
            for (Tutor tutor : tutors) {
                tutorList.getItems().add(tutor);
            }

            // select in list and scroll to added student
            tutorList.getSelectionModel().select(newTutor);
            tutorList.scrollTo(newTutor);

            tutorList.requestFocus();
            mainController.setLastFocused(tutorList);

            // set text display to added book
            display.setText(Helper.info(newTutor));

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
        Collection<Subject> subjects = ORM.findAll(Subject.class);
        for (Subject subject : subjects) {
            subjectSelection.getItems().add(subject.getName());
        }
        //subjectSelection.setValue(subjectSelection.getItems().get(0));
    }

}
