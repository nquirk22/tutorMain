/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tutoringfx;

import java.awt.print.Book;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.ORM;
import models.Subject;

/**
 * FXML Controller class
 *
 * @author Nathan
 */
public class AddSubjectController implements Initializable {

    @FXML
    private TextArea displaySubjects;

    @FXML
    private TextField newSubjectField;

    @FXML
    private void add(Event event) {
        try {
            String newSubjectName = newSubjectField.getText().trim();
            if (newSubjectName == null || newSubjectName.isEmpty()) {
                throw new ExpectedException("enter a subject");
            }
            Subject existingSubject
                    = ORM.findOne(Subject.class, "where name=?", new Object[]{newSubjectName});
            if (existingSubject != null) {
                newSubjectField.clear();
                throw new ExpectedException("subject exists");
            }
            
            Subject newSubject = new Subject(newSubjectName);
            ORM.store(newSubject);
            
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
        StringBuilder subjectText = new StringBuilder();
        for (Subject subject : subjects) {
            subjectText.append(subject.getName()).append("\n");
        }
        displaySubjects.setText(subjectText.toString());
    }

}
