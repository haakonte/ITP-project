package flashcard.core;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Opprinnelig hadde vi en abstrakt superklasse for flashcards men vi så
 * det mer hensiktsmessig å ha en enkel klasse som var kun for tekststrenger.
 * Dette gjorde vi fordi vi fjernet kravet om et flashcard med bilde.
 * Flashcard klasse for to tekststrenger.
 * I denne klassen er det enkle metoder som gettere, settere, toString og konstruktør.
 */
@JsonDeserialize(as = Flashcard.class)
public class Flashcard {

  private String text1;
  private String text2;

  /**
   * Konstruktør som oppretter flashcard.
   * @param text1 - ene siden av flashcarden
   * @param text2 - andre siden av flashcarden
   */
  public Flashcard(String text1, String text2) {
    this.text1 = text1;
    this.text2 = text2;
  }

  public String getText1() {
    return text1;
  }

  public String getText2() {
    return text2;
  }

  public void setText1(String text1) {
    this.text1 = text1;
  }

  public void setText2(String text2) {
    this.text2 = text2;
  }

  @Override
  public String toString() {
    return "FlashCard{ text1=" + this.text1
            + ", text2=" + this.text2 + '}';
  }
}
