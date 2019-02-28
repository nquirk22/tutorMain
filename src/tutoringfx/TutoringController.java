/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tutoringfx;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
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

    private Node lastFocused = null;

    void setLastFocused(Node lastFocused) {
        this.lastFocused = lastFocused;
    }

    private final Collection<Integer> tutorsOfStudent = new HashSet<>();
    private final Collection<Integer> studentsOfTutor = new HashSet<>();

    @FXML
    private void refocus(Event event) {
        if (lastFocused != null) {
            lastFocused.requestFocus();
        }
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
               throw new ExpectedException("student is already assigned to this tutor") ;
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
