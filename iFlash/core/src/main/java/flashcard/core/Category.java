package flashcard.core;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.assertj.core.annotations.NonNull;

/**
 * Denne klassen innehar logikk for kategori objektet.
 * Den implementerer et Iterable-grensesnitt som brukes for å
 * gå gjennom flashcards i kategorien. Videre er det getter og
 * setter for navn til kategorien. Det er også metoder
 * for å legge til og fjerne flashcards til kategorien. Det er
 * også en metode som stokker om alle flashcardsene i kategorien.
 */
public class Category implements Iterable<Flashcard> {
  private String name;
  private final List<Flashcard> flashcards = new ArrayList<>();

  public Category(String name) {
    this.name = name;
  }

  public Category() {
  }

  @Override
  @NonNull
  public ListIterator<Flashcard> iterator() {
    return flashcards.listIterator();
  }

  /**
   * Legger til card i flashcards hvis card er unikt.
   * @param card - tar inn flashcard som skal legges til i kategorien
   */
  public void addFlashCard(Flashcard card) {
    if (!containsFlashcard(card.getText1(), card.getText2())) {
      this.flashcards.add(0,card);
    }
  }

  /**
   * Fjerner et flashcard fra kategorien dersom det finnes i  kategorien.
   * @param card - flashcardet som skal fjernes
   * @throws IllegalArgumentException - dersom kortet ikke er i kategorien
   */
  public void removeFlashCard(Flashcard card) throws IllegalArgumentException {
    if (flashcards.contains(card)) {
      flashcards.remove(card);
    } else {
      throw new IllegalArgumentException("kortet ligger ikke i kategorien");
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  @Override
  public String toString() {
    return name;
  }

  /**
   * Sjekker om kateogorien inneholder et flashcard med text på ene siden og
   * tex1 på andre siden.
   * @param text - ene siden av flashcardet
   * @param text1 - andre siden av flashcardet
   * @return true dersom flashcardet er i kategorien, og false hvis det ikke er det
   */
  public boolean containsFlashcard(String text, String text1) {
    return flashcards.stream()
            .anyMatch(flashcard ->
                    flashcard.getText1().equals(text) && flashcard.getText2().equals(text1));
  }
}