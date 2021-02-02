package flashcard.ui;

import static flashcard.ui.FxHelperMethods.waitForFX;
import static flashcard.ui.FxHelperMethods.waitForRunLater;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import flashcard.core.Category;
import flashcard.core.Flashcard;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.service.query.EmptyNodeQueryException;

public class EditControllerTest extends ApplicationTest implements FxHelperMethods {

  private EditController editController;
  private Category categoryTest;
  private Flashcard card1;
  private Flashcard card2;
  private Button addFlashcard;
  private Button changeCategoryName;
  private TextField input1;
  private TextField input2;
  private TextField categoryName;
  private Label feedback;
  private Label savingfeedback;

  private final WebClientCategoryClient webClientCategoryClient =
          Mockito.mock(WebClientCategoryClient.class);

  @Override
  public void start(final Stage stage) throws Exception {
    final FXMLLoader loader = new FXMLLoader(EditControllerTest.class.getResource("EditTest.fxml"));
    final Parent parent = loader.load();
    this.editController = loader.getController();
    editController.setWebClient(webClientCategoryClient);
    stage.setScene(new Scene(parent));
    stage.setMinHeight(100);
    stage.setMinWidth(200);
    stage.show();
  }

  /**
   * Setter opp testcase.
   * @throws Exception - exception
   */
  @BeforeEach
  public void setUp() throws Exception {
    categoryTest = new Category("Test");
    card1 = new Flashcard("Input1", "Input2");
    card2 = new Flashcard("test_front", "test_back");
    categoryTest.addFlashCard(card1);
    categoryTest.addFlashCard(card2);

    assertNotNull(editController);
    editController.setCategory(categoryTest);
    Platform.runLater(() -> editController.afterInitialize());
    waitForRunLater();
    addFlashcard = find("#addFlashcardButton");
    changeCategoryName = find("#changeName");
    input1 = find("#input1");
    input2 = find("#input2");
    categoryName = find("#categoryName");
    feedback = find("#feedBack");
    savingfeedback = find("#savingFeedBack");
  }

  @Test
  void testChangeCategoryName() throws IOException, InterruptedException {
    when(editController.getWebClient().updateCategoryName(categoryName.getText(), "Endring"))
            .thenReturn(200);
    clickOn(categoryName).write("Endring");
    clickOn(changeCategoryName);
    waitForFX();
    assertEquals("TestEndring", categoryName.getText());
  }

  @Test
  void testButtonsAreDisabled() {
    assertTrue(addFlashcard.isDisabled());
    assertTrue(changeCategoryName.isDisabled());
  }

  @Test
  void testCategoryNameIsSet() {
    Label title = find("#title");
    assertEquals(categoryTest.getName(), title.getText());
    TextField name = find("#categoryName");
    waitForFX();
    assertEquals(categoryTest.getName(), name.getText());
  }

  @Test
  void testAddFlashcard() throws IOException, InterruptedException {
    Mockito.doNothing().when(editController.getWebClient()).updateCategory(categoryTest);
    clickOn(input1).write("Endring front");
    clickOn(input2).write("Endring back");
    clickOn(addFlashcard);
    Node change = find("Endring front");
    Node changeBack = find("Endring front");
    assertNotNull(change);
    assertNotNull(changeBack);
  }

  @Test
  void testChangeCategoryName_ShouldProduceClientBasedWarning()
          throws InterruptedException, IOException {
    Mockito.doThrow(InterruptedException.class)
            .when(editController.getWebClient())
            .updateCategoryName(categoryName.getText(), "TestKategori");
    Platform.runLater(() -> categoryName.setText(""));
    waitForRunLater();
    waitForFX();
    clickOn(categoryName).write("TestKategori");
    clickOn(changeCategoryName);
    assertEquals(""
            + "An error occured, and the category name was not changed",
            feedback.getText());
  }

  @Test
  void testAddFlashcard_AlreadyThere() throws IOException, InterruptedException {
    Mockito.doNothing().when(editController.getWebClient()).updateCategory(categoryTest);
    clickOn(input1).write("Input1");
    clickOn(input2).write("Input2");
    clickOn(addFlashcard);
    assertEquals("The flashcard does already exist",
            savingfeedback.getText());
  }

  @Test
  void testCategoryNameTooLong() throws InterruptedException {
    Platform.runLater(() -> categoryName.setText("T" + "e".repeat(30) + "st"));
    waitForRunLater();
    assertEquals(""
            + "The name of the category must be between 2 and 30 characters",
            feedback.getText());
    assertTrue(changeCategoryName.isDisabled());
  }

  @Test
  void testFlashcardNameTooLong() throws InterruptedException {
    Platform.runLater(() -> input2.setText("T" + "e".repeat(50) + "st"));
    waitForRunLater();
    assertEquals(""
            + "The text on the flashcard must be below 50 characters",
            feedback.getText());
  }

  @Test
  void testFlashcardWasNotAdded() throws IOException, InterruptedException {
    //simulerer en interrupted exception
    Mockito.doThrow(InterruptedException.class)
            .when(editController.getWebClient())
            .updateCategory(categoryTest);
    clickOn(input1).write("feedBack trigger");
    clickOn(input2).write("test");
    clickOn(addFlashcard);
    assertEquals("An error occured, and the flashcard was not added", savingfeedback.getText());
  }

  @Test
  void testChangeFlashcardName() {
    Node flashcardFieldInput1 = find("test_front");
    Node flashcardFieldInput2 = find("test_back");
    clickOn(flashcardFieldInput1).write("Endring");
    clickOn(flashcardFieldInput2).write("Endring");
    Node changeFlashcard = find("Confirm changes");
    clickOn(changeFlashcard);
    waitForFX();
    assertEquals("The flashcard was updated", feedback.getText());
  }

  @Test
  void testFlashcardValidationRegex() {
    clickOn(input1).type(KeyCode.SPACE).type(KeyCode.SPACE);
    assertEquals("The text on the flashcard can not be empty or contain only spaces",
            editController.getFeedBack().getText());
    assertTrue(input2.isDisabled());
  }

  @Test
  void testRemoveFlashcard_CategoryClientFails() throws IOException, InterruptedException {
    Mockito.doThrow(InterruptedException.class)
            .when(editController.getWebClient())
            .updateCategory(categoryTest);
    Node delete = find("Delete");
    clickOn(delete);
    FxHelperMethods.waitTwoSeconds();
    Node yes = find("Yes");
    clickOn(yes);
    assertEquals("Could not remove flashcard", feedback.getText());
  }

  @Test
  void testRemoveFlashcard() throws IOException, InterruptedException {
    Mockito.doNothing().when(editController.getWebClient()).updateCategory(categoryTest);
    Node delete = find("Delete");
    clickOn(delete);
    Node yes = find("Yes");
    clickOn(yes);
    var obj = new Object() {
      Node test1;
      Node test2;
    };
    FxHelperMethods.waitTwoSeconds();
    waitForFX();
    assertThrows(EmptyNodeQueryException.class, () -> obj.test1 = find("test_front"));
    assertThrows(EmptyNodeQueryException.class, () -> obj.test2 = find("test_back"));
  }

  @Override
  public <T extends Node> T find(final String query) {
    return lookup(query).query();
  }

}


