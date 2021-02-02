package flashcard.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FlashcardTest {

  Flashcard flashcard;

  @BeforeAll
  public void setUp() {
    flashcard = new Flashcard("one", "two");
  }

  @Test
  public void testGetter() {
    assertEquals("one", flashcard.getText1());
  }

  @Test
  public void testType2() {
    assertEquals("two", flashcard.getText2());
  }

  @Test
  public void testFlashcard1() {
    Flashcard test = new Flashcard("three", "four");
    assertNotSame(test.getText1(), flashcard.getText1());
  }

  @Test
  public void testFlashcard2() {
    Flashcard test = new Flashcard("three", "four");
    assertNotSame(flashcard.getText2(), test.getText2());
  }

  @Test
  public void testSetText1() {
    flashcard.setText1("1");
    assertEquals("1", flashcard.getText1());
  }

  @Test
  public void testSetText2() {
    flashcard.setText2("2");
    assertEquals("2", flashcard.getText2());
  }

  @Test
  public void testToString() {
    assertEquals("FlashCard{ text1=" + "one"
                    + ", text2=" + "two" + '}', flashcard.toString());
  }
}
