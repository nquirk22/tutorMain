/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tutoringfx;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import models.Interaction;
import models.ORM;

/**
 * FXML Controller class
 *
 * @author Nathan
 */
public class EditInteractionController implements Initializable {

    private TutoringController mainController;

    private Interaction currentInteraction;

    private boolean isEdited = false;

    @FXML
    private TextArea interactionArea;

    @FXML
    private Label tutorLabel;

    @FXML
    private Label studentLabel;

    @FXML
    private Label subjectLabel;

    void setMainController(TutoringController mainController) {
        this.mainController = mainController;
    }

    void setCurrentInteraction(Interaction currentInteraction) {
        this.currentInteraction = currentInteraction;
    }

    void setTextArea(Interaction interaction) {
        interactionArea.setText(interaction.getReport());
    }

    void setTutorLabel(String tutorName) {
        tutorLabel.setText(tutorName);
    }

    void setStudentLabel(String studentName) {
        studentLabel.setText(studentName);
    }

    void setSubjectLabel(String subjectName) {
        subjectLabel.setText(subjectName);
    }

    public boolean getEditedFlag() {
        return isEdited;
    }

    @FXML
    private void textChanged(Event event) {
        isEdited = true;
    }

    @FXML
    private void add(Event event) {
        currentInteraction.setReport(interactionArea.getText());
        ORM.store(currentInteraction);
        
        TextArea display = mainController.getDisplay();
        display.setText(currentInteraction.getReport());
        ((Button) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    private void cancel(Event event) {
        if (isEdited) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("You will lose changes, are you sure?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() != ButtonType.OK) {
                return;
            }
        }
        ((Button) event.getSource()).getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb
    ) {
        if (interactionArea.getText().isEmpty()) {
            interactionArea.setPromptText("No interactions yet...");
        }
    }
}
