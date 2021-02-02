package flashcard.ui;

import flashcard.core.Category;
import java.io.IOException;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Kontroller for kategori vinduet i applikasjonen vår.
 * Datastrukturene som tas inn i denne klassen,
 * er et MainMenu objekt, som har en liste med Categories,
 * der man når man kommer fra MainMenu og laster inn et Category objekt,
 * som er hoved kilde for datahåndtering i denne kontrolleren.
 */

public class CategoryController {

  private WebClientCategoryClient categoryClient = new WebClientCategoryClient();
  private Category category;

  @FXML
  private TextField categoryName;

  @FXML
  private Label feedback;

  @FXML
  private Button createCategory;

  @FXML
  private ComboBox<String> categories;

  @FXML
  private Button deleteSelection;

  @FXML
  private Button editSelection;

  private Stage primaryStage;


  /**
   * Setter opp FXML-elementer.
   * Fyller opp kombobox.
   * Setter opp lytter for categoryName feltet, for validering
   */
  @FXML
  public void initialize() {
    List<String> categoryList = loadCategories();
    categoryNameListener(categoryName);
    if (categoryList != null) {
      categories.getItems().addAll(categoryList);
    }
    setUpButtonListeners();
    categories.setPromptText("Choose category");
  }

  private void setUpButtonListeners() {
    createCategory.disableProperty().bind(Bindings.createBooleanBinding(() ->
                    categoryName.getText() == null
                            || categories.getItems().contains(categoryName.getText())
                            || categoryName.getText().length() > 30
                            || categoryName.getText().length() < 2
                            || !categoryName.getText().matches(".*\\w.*"),
            categoryName.textProperty()));

    editSelection.disableProperty().bind(Bindings.createBooleanBinding(() ->
                    categories.getSelectionModel().getSelectedItem() == null,
            categories.getSelectionModel().selectedItemProperty()));

    deleteSelection.disableProperty().bind(Bindings.createBooleanBinding(() ->
                    categories.getSelectionModel().getSelectedItem() == null,
            categories.getSelectionModel().selectedItemProperty()));
  }


  /**
   * Velger en category fra comboboxen.
   */

  @FXML
  public void handleSelectCategory() {
    String categoryName = categories.getValue();
    if (categoryName != null) {
      try {
        this.category = categoryClient.getCategory(categoryName);
      } catch (IOException | InterruptedException e) {
        setFeedback("Could not select the category", "red");
      }
    }
  }

  /**
   * Hjelpemetode som får delegert lytting av tekstfelt.
   * Håndterer validering og passer på at man ikke kan lage
   * kategorier med altfor langt navn
   * @param field - tekstfelt som vi lytter på
   */
  private void categoryNameListener(TextField field) {
    field.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        return;
      }
      if (newValue.length() > 30 || newValue.length() < 2
              || !categoryName.getText().matches(".*\\w.*")) {
        feedback.setText("Category name must be between 2 and 30 characters");
      } else if (categories.getItems().contains(newValue)) {
        feedback.setText("Category " + newValue + " already exists");
      } else {
        feedback.setText(null);
      }
    });
  }

  /**
   * Oppretter en ny kategori og legger den til i comboboxen.
   */
  @FXML
  public void handleCreateCategory() {
    Category newCategory = new Category(categoryName.getText());
    try {
      categoryClient.createCategory(newCategory);
      categories.getItems().add(newCategory.getName());
      categoryName.setText(null);
      categories.getSelectionModel().selectLast();
    } catch (IOException | InterruptedException e) {
      setFeedback("Could not create the category", "red");
    }
  }


  /**
   * Sletter den valgte kategorien.
   */
  @FXML
  public void handleDeleteSelected() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you wish to delete "
            + categories.getSelectionModel().getSelectedItem() + " ?",
            ButtonType.YES, ButtonType.NO);
    alert.showAndWait();
    if (alert.getResult() == ButtonType.YES) {
      String categoryName = categories.getValue();
      try {
        categoryClient.deleteCategory(categoryName);
        categories.getItems().remove(categoryName);
        categories.getSelectionModel().selectFirst();
      } catch (IOException | InterruptedException e) {
        setFeedback("Could not delete the selected category", "red");
      }

    }
  }

  /**
   * En viktig hjelpemetode som er primær-kilden til henting og setting av data.
   * Her henter vi et mainMenu objekt som er den overordnede datastrukturen som inneholder en
   * kolleksjon av Category-objekter
   */
  private List<String> loadCategories() {
    List<String> categories;
    try {
      categories = categoryClient.getCategories();
    } catch (IOException | InterruptedException e) {
      categories = null;
    }
    return categories;
  }

  /**
   * Denne metoden tar for seg endring av vindu til vinduet der man redigererer en enkelt kategori.
   * Den fungerer veldig likt som switchToMainMenu, bare at den setter noen data i editcontrolleren
   * slik at kategorier allerede er satt idét man bytter vindu
   */
  @FXML
  public void handleEditSelected() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("Edit.fxml"));
      Parent root = loader.load();
      assert root != null;
      EditController editController = loader.getController();
      Category category = categoryClient.getCategory(categories.getValue());
      editController.setCategory(category);
      editController.afterInitialize();
      editController.setPrimaryStage(primaryStage);
      editController.getPrimaryStage().setScene(new Scene(root));
      editController.getPrimaryStage().show();
    } catch (IOException | InterruptedException | NullPointerException e) {
      setFeedback("Could not switch to the edit menu", "red");
    }
  }


  /**
   * Denne metoden endrer scene fra CategoryController vinduet,
   * og tilbake til Mine kategorier vinduet.
   * Tar i bruk den statiske metoden i app for å hente primaryStage,
   * og sette det til en ny scene med Main Menu som rot.
   */

  @FXML
  public void handleSwitchToMainMenu() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
      Parent root = loader.load();
      MainMenuController mainMenuController = loader.getController();
      mainMenuController.setPrimaryStage(primaryStage);
      mainMenuController.getPrimaryStage().setScene(new Scene(root));
      mainMenuController.getPrimaryStage().show();
    } catch (IOException | NullPointerException e) {
      setFeedback("Could not switch to main menu", "red");
    }
  }

  public WebClientCategoryClient getWebClient() {
    return categoryClient;
  }

  public void setWebClient(WebClientCategoryClient webClientCategoryClient) {
    this.categoryClient = webClientCategoryClient;
  }

  public Category getCategory() {
    return category;
  }

  public void setPrimaryStage(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  public Stage getPrimaryStage() {
    return primaryStage;
  }

  /**
   * Endrer teksten som vises på feedback-labelen.
   * @param string - det som skal vises på labelen
   * @param colour - fargen på labelen
   */
  @FXML
  public void setFeedback(String string, String colour) {
    if (colour.equals("blue")) {
      setBlueColor(feedback);
    } else if (colour.equals("red")) {
      setRedColor(feedback);
    }
    feedback.setText(string);
  }

  private void setRedColor(Label l) {
    l.setStyle("-fx-text-fill: red");
  }

  private void setBlueColor(Label l) {
    l.setStyle("-fx-text-fill: cornflowerblue");
  }
}

