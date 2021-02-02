package flashcard.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import flashcard.core.Category;
import flashcard.core.Flashcard;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testfx.framework.junit5.ApplicationTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameControllerTest extends ApplicationTest implements FxHelperMethods {

  private Stage primaryStage;
  private GameController gameController;
  private Category testCategory;
  private Button next;
  private Button previous;
  private Label flashcard;
  private Node flipButton;

  @Override
  public void start(Stage stage) throws Exception {
    final FXMLLoader loader = new FXMLLoader(getClass().getResource("GameTest.fxml"));
    final Parent parent = loader.load();
    primaryStage = stage;
    this.gameController = loader.getController();
    primaryStage.setScene(new Scene(parent));
    primaryStage.setMinHeight(100);
    primaryStage.setMinWidth(200);

    testCategory = new Category("TestKategori");
    testCategory.addFlashCard(new Flashcard("card1", "Test"));
    testCategory.addFlashCard(new Flashcard("Tester", "Testing"));
    gameController.setCategory(testCategory);
    gameController.afterInitialize();

    primaryStage.show();
  }

  @BeforeEach
  void setUp() {

    next = find("#nextButton");
    previous = find("#previousButton");
    flashcard = find("#flashCardText");
    flipButton = find("#flipButton");
  }

  @Test
  void testNextCard() {
    if (!next.isVisible()) {
      clickOn(previous);
      FxHelperMethods.waitForFX();
    }
    String expected;
    if (flashcard.getText().equals("Tester")) {
      expected = "card1";
    } else {
      expected = "Tester";
    }
    clickOn(next);
    FxHelperMethods.waitForFX();
    assertEquals(expected, flashcard.getText());
  }

  @Test
  void testFlipCard() {
    String expected;
    if (flashcard.getText().equals("Tester")) {
      expected = "Testing";
    } else {
      expected = "Test";
    }
    clickOn(flipButton);
    FxHelperMethods.waitForFX();
    assertEquals(expected, flashcard.getText());
  }

  @Test
  void testFlipCard_ThenFlipBack() {
    final String expected = flashcard.getText();
    clickOn(flipButton);
    FxHelperMethods.waitForFX();
    clickOn(flipButton);
    FxHelperMethods.waitForFX();
    assertEquals(expected, flashcard.getText());
  }

  @Test
  void testPreviousCard() {
    if (!previous.isVisible()) {
      clickOn(next);
      FxHelperMethods.waitForFX();
    }
    String expected;
    if (flashcard.getText().equals("Tester")) {
      expected = "card1";
    } else {
      expected = "Tester";
    }
    clickOn(previous);
    FxHelperMethods.waitForFX();
    assertEquals(expected, flashcard.getText());
  }

  @Test
  void testEndOfFlashcards() {
    clickOn(next);
    FxHelperMethods.waitForFX();
    Node lastNext = find("#nextButton");
    FxHelperMethods.waitForFX();
    assertFalse(lastNext.isVisible());
  }

  @AfterAll
  void testNoFlashcards() {
    gameController.setCategory(new Category("Empty category"));
    gameController.afterInitialize();
    assertEquals(
            "There are no flashcards in this category.",
            flashcard.getText()
    );
  }

  @Override
  public <T extends Node> T find(final String query) {
    return lookup(query).query();
  }

}
