package flashcard.ui;

import flashcard.core.Category;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class MainMenuController {

  private WebClientCategoryClient categoryClient = new WebClientCategoryClient();

  private Category category;

  @FXML
  private Label feedBack;

  @FXML
  ComboBox<String> categoryComboBox;

  private Stage primaryStage;

  public MainMenuController() {
    super();
  }

  /**
   * Initialiserer controlleren ved å loade kategoriene, og fylle comoboxen med dem.
   */
  @FXML
  public void initialize() {
    List<String> categories = loadCategories();
    if (categories != null) {
      categoryComboBox.getItems().addAll(categories);
    }
  }


  private List<String> loadCategories() {
    List<String> categories;
    try {
      categories = categoryClient.getCategories();
    } catch (InterruptedException | IOException e) {
      return null;
    }
    return categories;
  }

  /**
   * Loader kategori.
   */
  @FXML
  public void handleLoadCategory() {
    try {
      String categoryName = this.categoryComboBox.getValue();
      category = categoryClient.getCategory(categoryName);
    } catch (NullPointerException | NoSuchElementException | IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Endrer til scenen der man spiller gjennom flashcardsene.
   */
  @FXML
  public void handlePlayCategory() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
      Parent root = loader.load();
      GameController gameController = loader.getController();
      initializeGameController(gameController, root);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NullPointerException e) {
      feedBack.setText("You must choose a category");
    }
  }

  /**
   * Hjelpemetode for å sette felt i GameController.
   * @param gameController - kontrolleren til game-vinduet
   * @param root - parent scene-roten
   */
  private void initializeGameController(GameController gameController, Parent root) {
    gameController.setCategory(category);
    gameController.afterInitialize();
    gameController.setPrimaryStage(primaryStage);
    gameController.getPrimaryStage().setScene(new Scene(root));
    gameController.getPrimaryStage().show();
    gameController.setPrimaryStage(primaryStage);
  }

  /**
   * Endrer til siden med overikt over kategoriene.
   */
  @FXML
  public void handleSwitchToCategories() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("Category.fxml"));
      Parent root = loader.load();
      CategoryController controller = loader.getController();
      controller.setPrimaryStage(primaryStage);
      controller.getPrimaryStage().setScene(new Scene(root));
      controller.getPrimaryStage().show();
    } catch (IOException | NullPointerException e) {
      feedBack.setText("Could not switch to category window");
    }
  }

  public void setPrimaryStage(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  public Stage getPrimaryStage() {
    return primaryStage;
  }

  public Category getCategory() {
    return category;
  }

  /**
   * Brukes som hjelpemetode i MainMenuControllerTest.
   * @param categoryClient - klient feltet skal settes til
   */
  public void setCategoryClient(WebClientCategoryClient categoryClient) {
    this.categoryClient = categoryClient;
  }
}
