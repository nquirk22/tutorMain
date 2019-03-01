/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tutoringfx;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.DBProps;
import models.Interaction;
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

    @FXML
    private TextArea display;

    @FXML
    private RadioButton orderButton;

    @FXML
    private MenuItem removeStudentMenuItem;

    @FXML
    private MenuItem removeTutorMenuItem;

    @FXML
    private Interaction currentInteraction;

    private Node lastFocused = null;

    private final Collection<Integer> tutorsOfStudent = new HashSet<>();
    private final Collection<Integer> studentsOfTutor = new HashSet<>();

    ListView<Student> getStudentList() {
        return studentList;
    }

    ListView<Tutor> getTutorList() {
        return tutorList;
    }

    TextArea getDisplay() {
        return display;
    }

    Interaction getCurrentInteraction() {
        return currentInteraction;
    }

    @FXML
    private void refocus(Event event) {
        if (lastFocused != null) {
            lastFocused.requestFocus();
        }
    }

    void setLastFocused(Node lastFocused) {
        this.lastFocused = lastFocused;
    }

    @FXML
    private void studentSelect(Event event) {
        Student student = studentList.getSelectionModel().getSelectedItem();
        if (student == null) {
            refocus(event);
            return;
        }
        lastFocused = studentList;

        // get all tutors of this student
        Collection<Interaction> interactions = ORM.findAll(Interaction.class,
                "where student_id=?", new Object[]{student.getId()});

        // set tutorsOfStudent to the ids of tutors teaching student
        tutorsOfStudent.clear();
        for (Interaction interaction : interactions) {
            tutorsOfStudent.add(interaction.getTutorId());
        }

        tutorList.refresh();
        display.setText(Helper.info(student));
    }

    @FXML
    private void tutorSelect(Event event) {
        Tutor tutor = tutorList.getSelectionModel().getSelectedItem();
        if (tutor == null) {
            refocus(event);
            return;
        }
        lastFocused = tutorList;

        // get all students of this tutor
        Collection<Interaction> interactions = ORM.findAll(Interaction.class,
                "where tutor_id=?", new Object[]{tutor.getId()});

        // set tutorsOfStudent to the ids of tutors teaching student
        studentsOfTutor.clear();
        for (Interaction interaction : interactions) {
            studentsOfTutor.add(interaction.getSudentId());
        }

        studentList.refresh();
        display.setText(Helper.info(tutor));

    }

    // BUTTONS=============================================================
    @FXML
    private void report(Event event) {
        try {
            Tutor tutor = tutorList.getSelectionModel().getSelectedItem();
            Student student = studentList.getSelectionModel().getSelectedItem();
            if (tutor == null || student == null) {
                throw new ExpectedException("must select student and tutor");
            }
            Interaction interaction = ORM.findOne(Interaction.class,
                    "where tutor_id=? and student_id=?",
                    new Object[]{tutor.getId(), student.getId()}
            );

            if (interaction == null) {
                throw new ExpectedException("tutor is not assigned to this student");
            }

            String report = interaction.getReport();
            if (report == null || report.isEmpty()) {
                report = "--EMPTY--";
            }
            display.setText(report);

        } catch (ExpectedException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(ex.getMessage());
            alert.show();
            refocus(event);
        }
    }

    @FXML
    private void assignTutor(Event event) {
        try {
            Tutor tutor = tutorList.getSelectionModel().getSelectedItem();
            Student student = studentList.getSelectionModel().getSelectedItem();
            if (tutor == null || student == null) {
                throw new ExpectedException("must select student and tutor");
            }
            if (student.hasTutor(tutor.getId())) {
                throw new ExpectedException("student is already assigned to this tutor");
            }
            if (student.getSubjectIds().contains(tutor.getSubjectId())) {
                throw new ExpectedException("student already has a tutor for this subject");
            }

            Interaction interaction = new Interaction(tutor.getId(), student.getId(), "");
            ORM.store(interaction);

            tutorsOfStudent.add(tutor.getId());
            studentsOfTutor.add(student.getId());
            studentList.refresh();
            tutorList.refresh();

            lastFocused.requestFocus();    // there must be a lastFocused
            if (lastFocused == studentList) {
                display.setText(Helper.info(student));
            } else if (lastFocused == tutorList) {
                display.setText(Helper.info(tutor));
            }

        } catch (ExpectedException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(ex.getMessage());
            alert.show();
            refocus(event);
        }
    }

    @FXML
    private void unassignTutor(Event event) {
        try {
            Tutor tutor = tutorList.getSelectionModel().getSelectedItem();
            Student student = studentList.getSelectionModel().getSelectedItem();
            if (tutor == null || student == null) {
                throw new ExpectedException("must select tutor and student");
            }

            // get the student from database
            Interaction interaction = ORM.findOne(Interaction.class,
                    "where tutor_id=? and student_id=?",
                    new Object[]{tutor.getId(), student.getId()}
            );
            if (interaction == null) {
                throw new ExpectedException("tutor not assigned to this student");
            }
            // remove the tutor/student link
            ORM.remove(interaction);

            // reset booklist
            tutorsOfStudent.remove(tutor.getId());
            studentsOfTutor.remove(student.getId());
            studentList.refresh();
            tutorList.refresh();

            lastFocused.requestFocus();    // there must be a lastFocused
            if (lastFocused == studentList) {
                display.setText(Helper.info(student));
            } else if (lastFocused == tutorList) {
                display.setText(Helper.info(tutor));
            }
        } catch (ExpectedException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(ex.getMessage());
            alert.show();
            refocus(event);
        }
    }

    @FXML
    private void clear(Event event) {
        studentList.getSelectionModel().clearSelection();
        tutorList.getSelectionModel().clearSelection();
        tutorsOfStudent.clear();
        studentsOfTutor.clear();
        studentList.refresh();
        tutorList.refresh();
        display.setText("");
        lastFocused = null;
    }

    @FXML
    private void orderBy(Event event) {
        System.out.println("button pressed");
        if (orderButton.isSelected()) {
            studentList.setItems(studentList.getItems().sorted(Comparator.comparing(Student::getEnrolled)));
            studentList.refresh();
        } else {
            studentList.setItems(studentList.getItems().sorted(Comparator.comparing(Student::getName)));
            studentList.refresh();
        }
    }

    //MENUS================================================================
    @FXML
    private void removeStudent(Event event) {
        try {
            Student student = studentList.getSelectionModel().getSelectedItem();
            if (student == null) {
                // shouldn't ever get here
                throw new ExpectedException("must select a student");
            }

            // find all the tutors linked to student
            Collection<Interaction> interactions = ORM.findAll(Interaction.class,
                    "where student_id=?", new Object[]{student.getId()});

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Are you sure?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() != ButtonType.OK) {
                return;
            }
            // remove interactions
            if (!interactions.isEmpty()) {
                for (Interaction interaction : interactions) {
                    ORM.remove(interaction);
                }
            }

            // remove from student table
            ORM.remove(student);

            // remove from list
            studentList.getItems().remove(student);
            studentList.getSelectionModel().clearSelection();

            if (lastFocused == studentList) {
                clear(event);
            }
        } catch (ExpectedException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(ex.getMessage());
            alert.show();
            if (lastFocused != null) {
                lastFocused.requestFocus();
            }
        }
    }

    @FXML
    private void addStudent(Event event) {
        try {
            // get fxmlLoader
            URL fxml = getClass().getResource("AddStudent.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(fxml);
            fxmlLoader.load();

            // get scene from loader
            Scene scene = new Scene(fxmlLoader.getRoot());

            // create a stage for the scene
            Stage dialogStage = new Stage();
            dialogStage.setScene(scene);

            // specify dialog title
            dialogStage.setTitle("Add a Student");

            // make it block the application
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // invoke the dialog
            dialogStage.show();

            // get AddBook dialog controller from fxmlLoader
            AddStudentController dialogController = fxmlLoader.getController();

            // pass the LibraryController to the dialog controller
            dialogController.setMainController(this);

            // query window closing
            dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setContentText("Are you sure you want to exit this dialog?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() != ButtonType.OK) {
                        event.consume();
                    }
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    @FXML
    private void removeTutor(Event event) {
        try {
            Tutor tutor = tutorList.getSelectionModel().getSelectedItem();
            if (tutor == null) {
                // shouldn't ever get here
                throw new ExpectedException("must select a tutor");
            }

            // find all the students linked to tutor
            Collection<Interaction> interactions = ORM.findAll(Interaction.class,
                    "where tutor_id=?", new Object[]{tutor.getId()});

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Are you sure?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() != ButtonType.OK) {
                return;
            }
            // remove interactions
            if (!interactions.isEmpty()) {
                for (Interaction interaction : interactions) {
                    ORM.remove(interaction);
                }
            }

            // remove from student table
            ORM.remove(tutor);

            // remove from list
            tutorList.getItems().remove(tutor);
            tutorList.getSelectionModel().clearSelection();

            if (lastFocused == tutorList) {
                clear(event);
            }
        } catch (ExpectedException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(ex.getMessage());
            alert.show();
            if (lastFocused != null) {
                lastFocused.requestFocus();
            }
        }
    }

    @FXML
    private void addTutor(Event event) {
        try {
            // get fxmlLoader
            URL fxml = getClass().getResource("AddTutor.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(fxml);
            fxmlLoader.load();

            // get scene from loader
            Scene scene = new Scene(fxmlLoader.getRoot());

            // create a stage for the scene
            Stage dialogStage = new Stage();
            dialogStage.setScene(scene);

            // specify dialog title
            dialogStage.setTitle("Add a Student");

            // make it block the application
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // invoke the dialog
            dialogStage.show();

            // get AddBook dialog controller from fxmlLoader
            AddTutorController dialogController = fxmlLoader.getController();

            // pass the LibraryController to the dialog controller
            dialogController.setMainController(this);

            // query window closing
            dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setContentText("Are you sure you want to exit this dialog?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() != ButtonType.OK) {
                        event.consume();
                    }
                }
            });

        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    @FXML
    private void addSubject(Event event) {
        try {
            // get fxmlLoader
            URL fxml = getClass().getResource("AddSubject.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(fxml);
            fxmlLoader.load();

            // get scene from loader
            Scene scene = new Scene(fxmlLoader.getRoot());

            // create a stage for the scene
            Stage dialogStage = new Stage();
            dialogStage.setScene(scene);

            // specify dialog title
            dialogStage.setTitle("Add a Subject");

            // make it block the application
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // invoke the dialog
            dialogStage.show();

            // query window closing
            dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setContentText("Are you sure you want to exit this dialog?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() != ButtonType.OK) {
                        event.consume();
                    }
                }
            });

        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    @FXML
    private void editInteraction(Event event) {
        try {
            Tutor tutor = tutorList.getSelectionModel().getSelectedItem();
            Student student = studentList.getSelectionModel().getSelectedItem();
            if (tutor == null || student == null) {
                throw new ExpectedException("must select tutor and student");
            }

            // get the student from database
            currentInteraction = ORM.findOne(Interaction.class,
                    "where tutor_id=? and student_id=?",
                    new Object[]{tutor.getId(), student.getId()}
            );
            if (currentInteraction == null) {
                throw new ExpectedException("tutor not assigned to this student");
            }

            // get fxmlLoader
            URL fxml = getClass().getResource("EditInteraction.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(fxml);
            // get AddBook dialog controller from fxmlLoader
            fxmlLoader.load();

            // get scene from loader
            Scene scene = new Scene(fxmlLoader.getRoot());

            // create a stage for the scene
            Stage dialogStage = new Stage();
            dialogStage.setScene(scene);

            // specify dialog title
            dialogStage.setTitle("Edit an Interaction");

            // make it block the application
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // invoke the dialog
            dialogStage.show();

            EditInteractionController dialogController = fxmlLoader.getController();

            // pass the LibraryController to the dialog controller
            dialogController.setMainController(this);
            dialogController.setCurrentInteraction(currentInteraction);
            if (currentInteraction.getReport() != null) {
                dialogController.setTextArea(currentInteraction);
            }

            dialogController.setStudentLabel(student.getName());
            dialogController.setSubjectLabel(tutor.getTutorSubjectName());
            dialogController.setTutorLabel(tutor.getName());
            
            // query window closing
            dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    if (dialogController.getEditedFlag()) {
                        alert.setContentText("Contents have changed, still exit?");
                    } else {
                        alert.setContentText("Are you sure you want to exit this dialog?");
                    }
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() != ButtonType.OK) {
                        event.consume();
                    }                    
                }
            });

        } catch (ExpectedException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(ex.getMessage());
            alert.show();
            if (lastFocused != null) {
                lastFocused.requestFocus();
            }

        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    @FXML
    private void activateUsersMenuItems(Event event) {
        Student student = studentList.getSelectionModel().getSelectedItem();
        removeStudentMenuItem.setDisable(student == null);
        Tutor tutor = tutorList.getSelectionModel().getSelectedItem();
        removeTutorMenuItem.setDisable(tutor == null);
    }

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

        tutorCellCallback.setHightlightedIds(tutorsOfStudent);
        studentCellCallback.setHightlightedIds(studentsOfTutor);
    }
}
