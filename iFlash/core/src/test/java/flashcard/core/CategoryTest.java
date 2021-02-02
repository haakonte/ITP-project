package flashcard.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryTest {

  Category category;

  @BeforeEach
  public void setUp() {
    category = new Category("category");
  }

  @Test
  public void testConstructor() {
    Category testConstructor1 = new Category("testConstructor1");
    assertEquals("testConstructor1", testConstructor1.getName());
  }

  @Test
  public void testEmptyConstructor() {
    Category testConstructor1 = new Category();
    assertNotNull(testConstructor1);
  }

  @Test
  public void testSetName() {
    category.setName("newName");
    assertEquals("newName", category.getName());
  }

  @Test
  public void testIterator() {
    Flashcard flashcardText = new Flashcard("text1", "text2");
    Flashcard flashcardText2 = new Flashcard("text3", "text4");
    category.addFlashCard(flashcardText);
    category.addFlashCard(flashcardText2);
    int numberOfCards = 0;
    for (Flashcard ignored : category) {
      numberOfCards++;
    }
    assertEquals(2, numberOfCards);
  }

  @Test
  public void testAddFlashCard() {
    Flashcard flashcardText = new Flashcard("text1", "text2");
    List<Flashcard> listOfFlashcards = new ArrayList<>();
    listOfFlashcards.add(flashcardText);
    category.addFlashCard(flashcardText);
    List<Flashcard> listOfFlashcardsFromCategory = new ArrayList<>();
    for (Flashcard fc:category) {
      listOfFlashcardsFromCategory.add(fc);
    }
    assertEquals(listOfFlashcards, listOfFlashcardsFromCategory);
  }

  @Test
  public void testAddFlashcardNotPossible() {
    Flashcard flashcardText = new Flashcard("text1", "text2");
    category.addFlashCard(flashcardText);
    category.addFlashCard(flashcardText);
    int fcCounter = 0;
    for (Flashcard ignored : category) {
      fcCounter++;
    }
    assertEquals(1, fcCounter);
  }

  @Test
  public void testRemoveFlashcard() {
    Flashcard flashcardText = new Flashcard("text1", "text2");
    category.addFlashCard(flashcardText);
    category.removeFlashCard(flashcardText);
    for (Flashcard ignored : category) {
      fail();
    }
    assertTrue(true);
  }

  @Test
  public void testRemoveFlashCard_exceptionCase() {
    Flashcard flashcardText = new Flashcard("text1", "text2");
    try {
      category.removeFlashCard(flashcardText);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }
  }

  @Test
  public void testToString() {
    category.setName("Test123");
    assertEquals("Test123", category.toString());
  }

  @Test
  public void testContainsFlashcard() {
    Flashcard flashcard = new Flashcard("Text1", "Text2");
    category.addFlashCard(flashcard);
    assertTrue(category.containsFlashcard("Text1", "Text2"));
  }

  @Test
  public void testCardNotInCategory() {
    Flashcard flashcard = new Flashcard("Text1", "Text2");
    Flashcard flashcard1 = new Flashcard("Text1", "Text3");
    category.addFlashCard(flashcard);
    assertFalse(category.containsFlashcard(flashcard1.getText1(), flashcard1.getText2()));
  }
}
