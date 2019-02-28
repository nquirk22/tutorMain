package tutoringfx;
 
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.Student;

class StudentCellCallback implements Callback<ListView<Student>, ListCell<Student>> {  
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
 
        // more code coming
      }
    };
    return cell;
  }
}
