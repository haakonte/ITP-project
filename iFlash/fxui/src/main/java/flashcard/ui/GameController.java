package flashcard.ui;

import flashcard.core.Category;
import flashcard.core.Flashcard;
import java.io.IOException;
import java.util.ListIterator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class GameController {

  ListIterator<Flashcard> listIterator;
  Flashcard flashcardOnlyText;
  private Category category;
  private boolean nextWasCalled = false;
  private boolean previousWasCalled = false;

  @FXML
  Button flipButton;

  @FXML
  Label categoryName;

  @FXML
  Label flashCardText;

  @FXML
  Button previousButton;

  @FXML
  Button nextButton;
  private Stage primaryStage;


  public void setCategory(Category category) {
    this.category = category;
  }

  @FXML
  public void initialize() {
    previousButton.setVisible(false);
  }

  /**
   * Endrer scene til hovedmenyen.
   */
  @FXML
  public void handleHomeScreen() {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
      Parent root = fxmlLoader.load();
      MainMenuController controller = fxmlLoader.getController();
      controller.setPrimaryStage(primaryStage);
      controller.getPrimaryStage().setScene(new Scene(root));
      controller.getPrimaryStage().show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Går til neste kort i kategorien.
   */
  @FXML
  public void handleNextCard() {
    nextWasCalled = true;
    if (previousWasCalled) {
      flashcardOnlyText = listIterator.next();
      previousWasCalled = false;
    }
    flashcardOnlyText = listIterator.next();
    if (!listIterator.hasNext()) {
      nextButton.setVisible(false);
    }
    previousButton.setVisible(true);
    loadFlashCard();

  }

  /**
   * Går til forrige kort i kategorien.
   */
  @FXML
  public void handlePreviousCard() {
    if (nextWasCalled) {
      flashcardOnlyText = listIterator.previous();
      nextWasCalled = false;
    }
    flashcardOnlyText = listIterator.previous();
    if (!listIterator.hasPrevious()) {
      previousButton.setVisible(false);
    }
    nextButton.setVisible(true);
    loadFlashCard();
    previousWasCalled = true;
  }

  /**
   * Snur kortet slik at du ser hva som er på andre siden.
   */
  @FXML
  public void handleFlipCard() {
    if (flashCardText.getText().equals(flashcardOnlyText.getText1())) {
      flashCardText.setText(flashcardOnlyText.getText2());
    } else {
      flashCardText.setText(flashcardOnlyText.getText1());
    }
  }

  @FXML
  private void loadFlashCard() {
    flashCardText.setText(flashcardOnlyText.getText1());
  }

  @FXML
  protected void afterInitialize() {
    categoryName.setText(category.getName());
    listIterator = category.iterator();
    if (listIterator.hasNext()) {
      flashcardOnlyText = listIterator.next();
      if (!listIterator.hasNext()) {
        nextButton.setVisible(false);
      }
    } else {
      flashcardOnlyText = new Flashcard("There are no flashcards in this category.",
              "Go to edit categories to make some flashcards!");
      nextButton.setVisible(false);
    }
    loadFlashCard();
  }

  public void setPrimaryStage(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  public Stage getPrimaryStage() {
    return primaryStage;
  }
}
