package flashcard.server;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import flashcard.core.Category;
import flashcard.core.Flashcard;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServerTest {

  Category category;
  FirestoreService firestoreService;
  FirestoreInitializer firestoreInitializer = new FirestoreInitializer();

  /**
   * En oppsett metode som setter opp databasen slik at vi kan begynne testing.
   * @throws ExecutionException - håndteres i høyere abstraksjon
   * @throws InterruptedException - håndteres i høyere abstraksjon
   */
  @BeforeAll
  public void setup() throws ExecutionException, InterruptedException {
    category = new Category("PehmqxgsofprbHmhdElweeKuyiFUycUR");
    firestoreInitializer.initialize();
    firestoreService = new FirestoreService();
    firestoreService.saveCategory(category);

  }

  @Test
  public void testWriteSuccess() {
    category.addFlashCard(new Flashcard("tekst1", "tekst2"));
    category.addFlashCard(new Flashcard("tekst2", "tekst3"));
    assertDoesNotThrow(() -> firestoreService.saveCategory(category));
  }

  @Test
  public void testReadSuccess() {
    Category retrivedCategory = new Category();
    try {
      retrivedCategory = firestoreService.getCategory(category.getName());
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
    }
    assertEquals(category.getName(), retrivedCategory.getName());
  }

  @Test
  public void testChangeCategoryNameValid() {
    firestoreService.changeCategoryName(category.getName(), "rUQiKIGHAsxlCBuFHxzCYqFbKScIGXCw");
    Category oldCategory = new Category();
    try {
      category = firestoreService.getCategory("rUQiKIGHAsxlCBuFHxzCYqFbKScIGXCw");
      oldCategory = firestoreService.getCategory("PehmqxgsofprbHmhdElweeKuyiFUycUR");
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
    }
    assertEquals("rUQiKIGHAsxlCBuFHxzCYqFbKScIGXCw", category.getName());
    assertNull(oldCategory);
  }

  @Test
  public void testChangeCategoryNameInvalid() {
    assertEquals("No such category Exists",
                firestoreService
                        .changeCategoryName(
                                "FJkvElFnFeZtpzWJAoUWJdztTFovNITt",
                                "rUQiKIGHAsxlCBuFHxzCYqFbKScIGXCw"
                        )
    );
  }

  @Test
  void testUpdateCategoryNameWhenCategoryAlreadyExists()
          throws ExecutionException, InterruptedException {
    Category category = new Category("cAecQAJzLePIYTGrBZyanpJlQcsUoios");
    firestoreService.saveCategory(category);
    String response = firestoreService
            .changeCategoryName(category.getName(), "cAecQAJzLePIYTGrBZyanpJlQcsUoios");
    assertEquals("A category with this name already exists.", response);
  }

  @Test
  public void testDeleteCategory() {
    assertDoesNotThrow(() -> firestoreService.deleteCategory(category.getName()));
    Category retrivedCategory = new Category();
    try {
      retrivedCategory = firestoreService.getCategory(category.getName());
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
    }
    assertNull(retrivedCategory);
  }

  @Test
  public void testGetCategories() {
    try {
      List<String> categoryNames = firestoreService.getCategories();
      assertNotEquals(null, categoryNames);
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
      fail();
    }
  }

  @AfterAll
  public void finish() throws ExecutionException, InterruptedException {
    firestoreService.deleteCategory("rUQiKIGHAsxlCBuFHxzCYqFbKScIGXCw");
    firestoreService.deleteCategory("cAecQAJzLePIYTGrBZyanpJlQcsUoios");
  }

}
