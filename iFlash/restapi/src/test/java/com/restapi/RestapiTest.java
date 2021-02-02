package com.restapi;

import static org.junit.jupiter.api.Assertions.assertThrows;
import com.google.gson.Gson;
import flashcard.core.Category;
import flashcard.core.Flashcard;
import flashcard.ui.WebClientCategoryClient;

import org.junit.jupiter.api.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RestapiTest{

  private final Gson gson = new Gson();

  private CategoryController categoryController;
  private Category testCategory;

  private String[] strings;

  @BeforeAll
  void setup() {
    strings = new String[]{""};
    RestapiApplicationHelper.main(strings);
    categoryController = new CategoryController();
    testCategory = new Category("ibfLISDFJUQpWSsINVMNAIqVAlWfzgOL");
    try {
      categoryController.createCategory(gson.toJson(testCategory));
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testGetCategories(){
    try {
      categoryController.getCategories();
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void testGetCategory() {
    try {
      categoryController.getCategory("ibfLISDFJUQpWSsINVMNAIqVAlWfzgOL");
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void testUpdateCategory() {
    testCategory.addFlashCard(new Flashcard("test", "test2"));
    try {
      categoryController.updateCategory(gson.toJson(testCategory));
    } catch (Exception e) {
      fail();
    }
    try {
      Assertions.assertEquals(gson.toJson(testCategory), categoryController.getCategory("ibfLISDFJUQpWSsINVMNAIqVAlWfzgOL"));
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void testUpdateCategoryName() {
    categoryController.updateCategoryName("JPIdkvgPxMBpAyLCjowPqNxyKhcRmWeQ", "ibfLISDFJUQpWSsINVMNAIqVAlWfzgOL");

    try {
      Assertions.assertEquals(gson.toJson("null"), gson.toJson(categoryController.getCategory("ibfLISDFJUQpWSsINVMNAIqVAlWfzgOL")));
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
    }
    try {
      categoryController.getCategory("JPIdkvgPxMBpAyLCjowPqNxyKhcRmWeQ");
    } catch (Exception e) {
      fail();
    }

  }

  @Test
  void testUpdateCategoryName_CategoryIsAlreadyThere() {
    assertThrows(ResponseStatusException.class, () -> categoryController.updateCategoryName("ibfLISDFJUQpWSsINVMNAIqVAlWfzgOL", "ibfLISDFJUQpWSsINVMNAIqVAlWfzgOL"));
  }

  /*
     Tester WebClientCategoryClient klassen.

     Vi valgte å ikke lage en egen modul for integration-tests.
     Dette er for å unngå å starte og stoppe lokal server unødvendig mange ganger.
     For å spare tid under install er derfor testing av WebClientCategoryClient
     inkludert i unit-testene her.
     */
  @Test
  void testWebClientCategoryClient() {
    WebClientCategoryClient client = new WebClientCategoryClient();
    Category category = new Category("YbfmhTTlQZZCVpjXvyCgeBLhWxdNDMVA");
    category.addFlashCard(new Flashcard("Test1", "Test2"));

    try {
      //Tester createCategory
      client.createCategory(category);

      //Tester getCategories
      Assertions.assertNotNull(client.getCategories());

      //Tester getCategory
      Category actual = client.getCategory(category.getName());

      Assertions.assertEquals(category.getName(), actual.getName());
      Assertions.assertEquals(
              category.iterator().next().toString(),
              actual.iterator().next().toString()
      );

      //Tester updateCategory
      category.addFlashCard(new Flashcard("second card", "test"));
      client.updateCategory(category);

      actual = client.getCategory(category.getName());
      Iterator actualIterator = actual.iterator();

      Assertions.assertEquals(
              "FlashCard{ text1=Test1, text2=Test2}",
              actualIterator.next().toString()
      );
      Assertions.assertEquals(
              "FlashCard{ text1=second card, text2=test}",
              actualIterator.next().toString()
      );

      //Tester updateCategoryName
      int response = client.updateCategoryName (
              category.getName(),
              "BlvjGHzQMcKKbetBwaidTZiFskQtzkEx"
      );

      Assertions.assertNotEquals(400, response);

      category.setName("BlvjGHzQMcKKbetBwaidTZiFskQtzkEx");

      Assertions.assertNull(client.getCategory("YbfmhTTlQZZCVpjXvyCgeBLhWxdNDMVA"));
      Assertions.assertEquals(
              category.getName(),
              client.getCategory(category.getName()).getName()
      );

      //Tester deleteCategory
      client.deleteCategory("BlvjGHzQMcKKbetBwaidTZiFskQtzkEx");
      Assertions.assertNull(client.getCategory("BlvjGHzQMcKKbetBwaidTZiFskQtzkEx"));



    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
      fail();
    }

  }

  @AfterAll
  void tearDown() {
    try {
      categoryController.deleteCategory("JPIdkvgPxMBpAyLCjowPqNxyKhcRmWeQ");
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
    }
    RestapiApplicationHelper.stopRunning();
  }
}
