package flashcard.ui;

import flashcard.core.Flashcard;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class FlashcardLoader {

  private ScrollPane scrollPane1;
  private EditController editController;

  /**
   * Fyller opp scrollpanet i editcontroller med flashcards.
   * @param scrollPane - panet som flashcardene vises på
   * @param editController - kontrolleren til edit.fxml som inneholder scrollpanet
   */
  public void loadFlashcard(ScrollPane scrollPane, EditController editController) {


    this.editController = editController;
    this.scrollPane1 = scrollPane;
    VBox vbox = new VBox();
    List<Pane> panes = new ArrayList<>();
    for (Flashcard fc: editController.getCategory()) {
      panes.add(this.createFlashCard(fc));
    }
    vbox.getChildren().addAll(panes);
    scrollPane.setContent(vbox);
  }

  /**
   * Lager et flashcar på den måten det skal vises frem i appen slik
   * at det kan redigeres og slettes.
   * @param flashcard - flashcardet metoden skal gjøre om til formen som er ønskelig
   * @return panet med riktig oppsett
   */

  public Pane createFlashCard(Flashcard flashcard) {


    //Setter verdier for teksten på Flascard
    TextField text1 = new TextField();
    TextField text2 = new TextField();
    text1.setText(flashcard.getText1());
    text2.setText(flashcard.getText2());

    text1.setStyle("-fx-wrap-text: true");
    text1.setPrefHeight(25.0);
    text1.setPrefWidth(150.0);
    text1.setLayoutX(0);
    text1.setLayoutY(0);

    text2.setStyle("-fx-wrap-text: true");
    text2.setPrefHeight(25.0);
    text2.setPrefWidth(150.0);
    text2.setLayoutX(150);
    text2.setLayoutY(0);

    Button editButton = new Button("Confirm changes");
    editButton.setPrefWidth(150);
    editButton.setLayoutX(300);

    Button deleteButton = new Button("Delete");
    deleteButton.setPrefWidth(90);
    deleteButton.setLayoutX(450);

    //Setter metoder for henholdsvis å oppdatere og slette et flashcard direkte
    editButton.setOnAction(e -> updateFlashcard(flashcard, text1.getText(),
            text2.getText()));

    Pane pane = new Pane();
    deleteButton.setOnAction(e -> removeFlashcard(flashcard, pane));

    textOnFlashcardListener(text1, text2,  editButton, deleteButton);
    textOnFlashcardListener(text2, text1, editButton, deleteButton);

    //Setter verdier for pane ("ramma" rundt flashcard-et)
    pane.setPrefSize(540.0, 25.0);
    pane.setLayoutX(42.0);
    pane.setLayoutY(108.0);
    pane.setBorder(new Border(new BorderStroke(Color.BLACK,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    pane.setStyle("-fx-background-color: #ffffff");
    pane.getChildren().addAll(text1, text2, editButton, deleteButton);
    return pane;
  }

  private void textOnFlashcardListener(TextField field1, TextField field2,
                                    Button editButton, Button deleteButton) {
    field1.textProperty().addListener((observable, oldValue, newValue) -> {
      //liten kjapp null sjekk først
      if (newValue == null) {
        return;
      }
      if (newValue.length() > 50) {
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        field2.setDisable(true);
        this.editController.setFeedback("The text must be below 50 characters",
                "red");
      } else if ((!field1.getText().matches(".*\\w.*"))) {
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        field2.setDisable(true);
        this.editController
                .setFeedback("The text on the flashcard can not be empty or contain only spaces",
                "red");
      } else {
        editButton.setDisable(false);
        deleteButton.setDisable(false);
        field2.setDisable(false);
        this.editController.setFeedback("", "blue");

      }
    });
  }

  private void updateFlashcard(Flashcard flashcard, String text1, String text2) {

    if (!flashcard.getText1().equals(text1) || !flashcard.getText2().equals(text2)) {
      editController.removeFlashCard(flashcard);
      Flashcard newCard = new Flashcard(text1, text2);
      editController.addFlashCard(newCard);
      loadFlashcard(this.scrollPane1, this.editController);
    }
  }

  private void removeFlashcard(Flashcard flashcard, Pane pane) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you wish to delete this flashcard?",
            ButtonType.YES, ButtonType.NO);
    alert.showAndWait();
    if (alert.getResult() == ButtonType.YES) {
      editController.removeFlashCard(flashcard);
      pane.getChildren().removeAll();
      loadFlashcard(this.scrollPane1, this.editController);
    }
  }

}
