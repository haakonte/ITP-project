package flashcard.ui;

import static flashcard.ui.FxHelperMethods.waitForFX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import flashcard.core.Category;
import java.io.IOException;
import java.util.Arrays;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.service.query.EmptyNodeQueryException;

public class CategoryControllerTest extends ApplicationTest implements FxHelperMethods {

  private CategoryController editCategoryController;
  private final Category test1;
  private final Category test2;
  private ComboBox<String> categories;
  private Button deleteSelection;
  private Button editSelection;
  private Button createNew;
  private TextField categoryName;
  private Label feedBack;

  private final WebClientCategoryClient webClientCategoryClient =
          Mockito.mock(WebClientCategoryClient.class);


  @Override
  public void start(Stage stage) throws Exception {
    final FXMLLoader loader =
            new FXMLLoader(CategoryControllerTest.class.getResource("CategoryTest.fxml"));
    final Parent parent = loader.load();
    this.editCategoryController = loader.getController();
    editCategoryController.setWebClient(webClientCategoryClient);
    editCategoryController.setPrimaryStage(stage);
    editCategoryController.getPrimaryStage().setScene(new Scene(parent));
    editCategoryController.getPrimaryStage().setMinHeight(100);
    editCategoryController.getPrimaryStage().setMinWidth(200);
    editCategoryController.getPrimaryStage().show();
  }

  public CategoryControllerTest() {
    this.test1 = new Category("test1");
    this.test2 = new Category("test2");
  }

  @BeforeEach
  void setUp() {
    assertNotNull(editCategoryController);
    categories = find("#categories");
    deleteSelection = find("#deleteSelection");
    createNew = find("#createCategory");
    feedBack = find("#feedback");
    categoryName = find("#categoryName");
    editSelection = find("#editSelection");
    categories.getItems().addAll(Arrays.asList(test1.getName(), test2.getName()));
  }

  @Test
  void testCreateCategory() throws IOException, InterruptedException {
    Mockito.doNothing().when(editCategoryController.getWebClient()).createCategory(test1);
    clickOn(categoryName).write("Test");
    waitForFX();
    clickOn(createNew);
    clickOn(categories);
    Node test1 = find("Test");
    waitForFX();
    assertNotNull(test1);
  }

  @Test
  void testSelectCategory() throws IOException, InterruptedException {
    when(editCategoryController.getWebClient().getCategory("test1")).thenReturn(test1);
    clickOn(categories);
    Node test1 = find("test1");
    clickOn(test1);
    waitForFX();
    assertNotNull(editCategoryController.getCategory());
    assertEquals("test1", editCategoryController.getCategory().getName());
  }

  @Test
  void testDeleteCategory() throws IOException, InterruptedException {
    Mockito.doNothing().when(editCategoryController.getWebClient()).deleteCategory("test1");
    clickOn(categories);
    Node test1 = find("test1");
    clickOn(test1);
    waitForFX();
    clickOn(deleteSelection);
    Node yes = find("Yes");
    clickOn(yes);
    waitForFX();
    clickOn(categories);
    assertThrows(EmptyNodeQueryException.class, () -> find("test1"));
  }

  @Override
  public <T extends Node> T find(final String query) {
    return lookup(query).query();
  }

  @Test
  public void testEveryButtonIsDisabled() {
    assertTrue(createNew.isDisabled());
    assertTrue(editSelection.isDisabled());
    assertTrue(deleteSelection.isDisabled());
  }

  @Test
  void testSwitchToEditMenu() throws IOException, InterruptedException {
    when(webClientCategoryClient.getCategory("Test3")).thenReturn(new Category("Test3"));
    clickOn(categoryName).write("Test3");
    clickOn(createNew);
    waitForFX();
    clickOn(categories);
    Node test1 = find("Test3");
    clickOn(test1);
    clickOn(editSelection);
    waitForFX();
    Node categoryTitle = find("#title");
    waitForFX();
    assertEquals("Test3", ((Label) categoryTitle).getText());
    Node nameInField = find("#categoryName");
    assertEquals("Test3", ((TextField) nameInField).getText());
  }

  @Test
  void testCategoryAlreadyThere() {
    clickOn(categoryName).write("test1");
    assertEquals("Category test1 already exists", feedBack.getText());
  }
}
