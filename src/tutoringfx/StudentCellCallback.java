package tutoringfx;

import java.util.Collection;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.Student;

class StudentCellCallback implements Callback<ListView<Student>, ListCell<Student>> {

    private Collection<Integer> highlightedIds = null;

    void setHightlightedIds(Collection<Integer> highlightedIds) {
        this.highlightedIds = highlightedIds;
    }

    @Override
    public ListCell<Student> call(ListView<Student> p) {
        ListCell<Student> cell = new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty) {
                    this.setText(null);
                    return;
                }
                this.setText(student.getName());

                if (highlightedIds == null) {
                    return;
                }

                String css = ""
                        + "-fx-text-fill: #c00;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-style: italic;";

                if (highlightedIds.contains(student.getId())) {
                    this.setStyle(css);
                } else {
                    this.setStyle(null);
                }
            }
        };
        return cell;
    }
}
