package flashcard.ui;

import static flashcard.ui.FxHelperMethods.waitForFX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import flashcard.core.Category;
import flashcard.core.Flashcard;
import java.io.IOException;
import java.util.Arrays;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;

public class MainMenuControllerTest extends ApplicationTest implements FxHelperMethods {

  private MainMenuController mainMenuController;
  private ComboBox<String> categoriesDropdown;
  private final Category category;
  private final Flashcard test1;
  private final Flashcard test2;

  private final WebClientCategoryClient categoryClient =
          Mockito.mock(WebClientCategoryClient.class);

  @Override
  public void start(final Stage stage) throws Exception {
    final FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenuTest.fxml"));
    final Parent parent = loader.load();
    this.mainMenuController = loader.getController();
    this.mainMenuController.setCategoryClient(categoryClient);
    mainMenuController.setPrimaryStage(stage);
    mainMenuController.getPrimaryStage().setScene(new Scene(parent));
    mainMenuController.getPrimaryStage().setMinHeight(100);
    mainMenuController.getPrimaryStage().setMinWidth(200);
    mainMenuController.getPrimaryStage().show();
  }

  /**
   * Konstruktør som setter noen datastrukturer til testingen.
   */
  public MainMenuControllerTest() {
    category = new Category("Test1");
    test1 = new Flashcard("test1", "test2");
    test2 = new Flashcard("test3", "test4");
    category.addFlashCard(test1);
    category.addFlashCard(test2);
  }

  @Override
  public <T extends Node> T find(final String query) {
    return lookup(query).query();
  }

  /**
   * Setter opp testcasen.
   */
  @BeforeEach
  public void setUp() {
    //vi må lage et par kategorier, og legge dem til mainMenu klassen
    //da har vi lastet inn nødvendige data
    categoriesDropdown = find("#categoryComboBox");
    categoriesDropdown.getItems().clear();
    categoriesDropdown.getItems().addAll(Arrays.asList("Test1", "Test2"));
  }

  @Test
  void testCategoryNotSelected() {
    Button play = find("#playButton");
    clickOn(play);
    waitForFX();
    Label warning = find("#feedBack");
    waitForFX();
    assertEquals("You must choose a category", warning.getText());
  }

  @Test
  void testLoadCategory() throws IOException, InterruptedException {
    when(categoryClient.getCategory("Test1")).thenReturn(category);

    clickOn(categoriesDropdown);
    waitForFX();
    Node categoryName = find("Test1");
    clickOn(categoryName);
    waitForFX();

    Category actual = mainMenuController.getCategory();

    Assertions.assertEquals(category.getName(), actual.getName());
    Assertions.assertEquals(
            category.iterator().next().getText1(),
            actual.iterator().next().getText1()
    );
  }

  @Test
  void testPlayCategory() throws IOException, InterruptedException {
    when(categoryClient.getCategory("Test1")).thenReturn(category);
    clickOn(categoriesDropdown);
    waitForFX();
    Node categoryName = find("Test1");
    clickOn(categoryName);
    waitForFX();
    Node play = find("#playButton");
    clickOn(play);
    waitForFX();
    Node categoryNameGameController = find("Test1");
    assertEquals("Test1", ((Label) categoryNameGameController).getText());
  }

  @Test
  void testSwitchToCategoryWindow() {
    Node categoryWindow = find("Edit categories");
    clickOn(categoryWindow);
    waitForFX();
    Node title = find("Categories");
    assertNotNull(title);
  }
}
