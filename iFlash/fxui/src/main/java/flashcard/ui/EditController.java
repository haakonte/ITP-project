package flashcard.ui;

import flashcard.core.Category;
import flashcard.core.Flashcard;
import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class EditController {

  private Category category;

  private final FlashcardLoader flashcardLoader = new FlashcardLoader();

  private WebClientCategoryClient categoryClient = new WebClientCategoryClient();


  //Generelle variabelnavn skal endres til mer spesifikke etterhvert, mye placeholder variabler nå.
  @FXML
  TextField categoryName;
  @FXML
  TextField input1;
  @FXML
  TextField input2;
  @FXML
  AnchorPane anchorPane;
  @FXML
  Button home;
  @FXML
  Button back;
  @FXML
  Button changeName;
  @FXML
  Button addFlashcardButton;
  @FXML
  Label feedBack;
  @FXML
  Label savingFeedBack;
  @FXML
  ScrollPane scrollPane;
  @FXML
  private Label title;
  private Stage primaryStage;

  public void setCategory(Category category) {
    this.category = category;
  }

  @FXML
  void initialize() {
    textFieldListenerInput(input1, input2);
    textFieldListenerInput(input2, input1);
    textFieldListenerChangeName(categoryName);
    setUpButtonListeners();
  }

  private void setUpButtonListeners() {
    changeName.disableProperty().bind(
            Bindings.isNull(categoryName.textProperty())
                    .or(Bindings.createBooleanBinding(() ->
                            categoryName.getText().equals(title.getText())
                                    || categoryName.getText().length() > 30
                                    || categoryName.getText().length() < 2
                                    || !categoryName.getText().matches(".*\\w.*"),
                                    categoryName.textProperty())));

    addFlashcardButton.disableProperty()
            .bind(
                    Bindings.isNull(input1.textProperty())
                            .or(Bindings.isNull(input1.textProperty()))
                            .or(Bindings.isEmpty(input1.textProperty()))
                            .or(Bindings.isEmpty(input2.textProperty()))
                            .or(Bindings.createBooleanBinding(() ->
                                            input1.getText().length() > 50
                                                    || input2.getText()
                                                    .length() > 50
                                                    || !input1.getText()
                                                    .matches(".*\\w.*")
                                                    || !input2.getText()
                                                    .matches(".*\\w.*"),
                                    input1.textProperty(),
                                    input2.textProperty())));
  }

  private void setRedColor(Label l) {
    l.setStyle("-fx-text-fill: red");
  }

  private void setBlueColor(Label l) {
    l.setStyle("-fx-text-fill: cornflowerblue");
  }

  private void textFieldListenerChangeName(TextField changeName) {
    changeName.textProperty().addListener((observableValue, old, newValue) -> {
      if (newValue == null) {
        return;
      }
      if (newValue.length() > 30 || newValue.length() < 2
              || !categoryName.getText().matches(".*\\w.*")) {
        this.setFeedback("The name of the category must be between 2 and 30 characters", "red");
      } else {
        this.setFeedback("", "blue");
      }
    });
  }

  /**
   * Hjelpemetode for validering av tekstfelt.
   * @param felt - feltet som lytteren lytter til
   */
  private void textFieldListenerInput(TextField felt, TextField felt2) {
    felt.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        return;
      }
      if (newValue.length() > 50) {
        this.setFeedback("The text on the flashcard must be below 50 characters",
                "red");
        felt2.setDisable(true);
      } else if ((!felt.getText().matches(".*\\w.*"))) {
        this.setFeedback("The text on the flashcard can not be empty or contain only spaces",
                "red");
        felt2.setDisable(true);
      } else {
        this.setFeedback("", "blue");
        felt2.setDisable(false);
      }
    });
  }

  /**
   * Endrer navnet til kategorien.
   */
  @FXML
  public void changeCategoryName() {
    try {
      String oldName = this.category.getName();
      String newName = this.categoryName.getText();
      int response = categoryClient.updateCategoryName(oldName, newName);
      if (response != 400) {
        setBlueColor(feedBack);
        this.category.setName(newName);
        title.setText(categoryName.getText());
        categoryName.setText(categoryName.getText());
        feedBack.setText("Updated category name succesfully.");
      } else {
        setRedColor(feedBack);
        feedBack.setText("Category with name '" + newName + "' already exists.");
      }

    } catch (IOException | InterruptedException e) {
      this.setFeedback("An error occured, and the category name was not changed", "red");
    }
  }


  /**
   * Legger til Flashcards fra input-feltene.
   * Sletter og legger til den samme kategorien før og etter jeg legger til flashcardet for å
   * opprettholde samhandlingen mellom cateogry og mainmenu.
   *
   */
  @FXML
  public void handleAddFlashCard() {
    savingFeedBack.setText("");
    try {
      Flashcard card = new Flashcard(input1.getText(), input2.getText());
      if (category.containsFlashcard(card.getText1(), card.getText2())) {

        savingFeedBack.setText("The flashcard does already exist");
        setRedColor(savingFeedBack);
      } else {
        category.addFlashCard(card);
      }
      input1.setText(null);
      input2.setText(null);
      categoryClient.updateCategory(this.category);

      loadFlashcard();
      if (!savingFeedBack.getText().equals("The flashcard does already exist")) {
        setBlueColor(savingFeedBack);
        savingFeedBack.setText("The flashcard was added");
        input2.setDisable(true);
      }
    } catch (IOException | InterruptedException e) {
      setRedColor(savingFeedBack);
      savingFeedBack.setText("An error occured, and the flashcard was not added");
    }

  }

  /**
   * En hjelpemetode som håndterer hendholdsvis endring av scene.
   * Håndterer endring til MainMenu og edit-vinduet
   * @param fxml - navnet på fxml-filen som skal loades
   * @throws IOException - hvis FXML-loader ikke finner filen
   */
  private void switchScene(String fxml) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
    Parent root = loader.load();
    Object controller = loader.getController();
    if (controller instanceof MainMenuController) {
      ((MainMenuController) controller).setPrimaryStage(primaryStage);
      ((MainMenuController) controller).getPrimaryStage().setScene(new Scene(root));
      ((MainMenuController) controller).getPrimaryStage().show();
    } else {
      ((CategoryController) controller).setPrimaryStage(primaryStage);
      ((CategoryController) controller).getPrimaryStage().setScene(new Scene(root));
      ((CategoryController) controller).getPrimaryStage().show();
    }
  }

  /**
   * Endrer scenen til category-oversikten.
   */
  @FXML
  public void handleSwitchToCategory() {
    try {
      switchScene("Category.fxml");
    } catch (IOException | NullPointerException e) {
      setFeedback("Could not switch to categories", "red");
    }
  }

  /**
   * Endrer scenen til hovedmenyen (forsiden).
   */
  @FXML
  public void handleSwitchToMain() {
    try {
      switchScene("MainMenu.fxml");
    } catch (IOException | NullPointerException e) {
      setFeedback("Could not switch to main menu", "red");
    }
  }

  @FXML
  public void loadFlashcard() {
    flashcardLoader.loadFlashcard(scrollPane, this);
  }

  public Label getFeedBack() {
    return feedBack;
  }

  public Category getCategory() {
    return this.category;
  }

  @FXML
  protected void afterInitialize() {
    title.setText(category.getName());
    categoryName.setText(category.getName());
    input2.setDisable(true);
    loadFlashcard();
  }

  /**
   * Fjerner et flashcard fra categorien.
   * @param flashcard - flashcardet som skal fjernes
   */
  @FXML
  public void removeFlashCard(Flashcard flashcard) {
    this.setFeedback("", "");
    this.category.removeFlashCard(flashcard);
    try {
      categoryClient.updateCategory(this.category);
    } catch (IOException | InterruptedException e) {
      setFeedback("Could not remove flashcard", "red");
    }
  }

  /**
   * Legger til et flashcard (brukes i flashcardloader-klassen).
   * @param flashcard - flashcardet som skal legges til
   */
  public void addFlashCard(Flashcard flashcard) {
    this.category.addFlashCard(flashcard);
    this.setFeedback("The flashcard was updated", "blue");
    try {
      categoryClient.updateCategory(this.category);
    } catch (IOException | InterruptedException e) {
      setFeedback("Could not update the flashcard", "red");
    }
  }

  /**
   * Endrer teksten som vises på feedback-labelen.
   * @param string - det som skal vises på labelen
   * @param colour - fargen på labelen
   */
  @FXML
  public void setFeedback(String string, String colour) {
    if (colour.equals("blue")) {
      setBlueColor(feedBack);
    } else if (colour.equals("red")) {
      setRedColor(feedBack);
    }
    feedBack.setText("");
    savingFeedBack.setText("");
    feedBack.setText(string);
  }

  public void setWebClient(WebClientCategoryClient webClientCategoryClient) {
    this.categoryClient = webClientCategoryClient;
  }

  public WebClientCategoryClient getWebClient() {
    return categoryClient;
  }

  public void setPrimaryStage(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  public Stage getPrimaryStage() {
    return primaryStage;
  }
}
