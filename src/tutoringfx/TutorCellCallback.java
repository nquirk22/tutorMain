package tutoringfx;
 
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.Tutor;

class TutorCellCallback implements Callback<ListView<Tutor>, ListCell<Tutor>> {  
  @Override
  public ListCell<Tutor> call(ListView<Tutor> p) {
    ListCell<Tutor> cell = new ListCell<Tutor>() {
      @Override
      protected void updateItem(Tutor tutor, boolean empty) {
        super.updateItem(tutor, empty);
        if (empty) {
          this.setText(null);
          return;
        }
 
        this.setText(tutor.getName());
      }
    };
    return cell;
  }
}
