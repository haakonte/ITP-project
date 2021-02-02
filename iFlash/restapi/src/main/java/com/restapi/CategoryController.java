package com.restapi;

import com.google.gson.Gson;
import flashcard.core.Category;
import flashcard.server.FirestoreService;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Denne klassen er ansvarlig for å ta inn HTTP-requestene fra front-end klienten vår.
 * Den er REST-api knutepunktet mellom javaFX-frontend og google databasen.
 * Denne klassen brukes ikke som instans noen steder, men Spring boot bruker den til
 * å ta inn HTTP-requests og sende de til firestore databasen vår, tar inn responsen,
 * og returnerer den til front-end klienten vår.
 */
@RestController
public class CategoryController {

  Gson gson = new Gson();

  FirestoreService firestoreService = new FirestoreService();

  /**Funksjon som henter en liste med kategorier fra databasen.
   * @return Liste av kategorier.
   * @throws ExecutionException - kaster en exception som må håndteres i en høyere abstraksjon.
   * @throws InterruptedException - kaster en exception som må håndteres i en høyere abstraksjon.
   */
  @GetMapping("/getCategories")
  public List<String> getCategories() throws ExecutionException, InterruptedException {
    return firestoreService.getCategories();
  }

  /**Funksjon som henter en kategori fra databasen som har samme navn som input elementet name.
   * @param name - inputnavn til kategorien.
   * @return kategori med navn lik input.
   * @throws ExecutionException - kaster en exception som må håndteres i en høyere abstraksjon.
   * @throws InterruptedException - kaster en exception som må håndteres i en høyere abstraksjon.
   */
  @GetMapping("/getCategory")
  public String getCategory(@RequestParam(value = "name") String name)
          throws ExecutionException, InterruptedException {
    return gson.toJson(firestoreService.getCategory(name));
  }

  /**Funksjon som lager en ny kategori. Navnet til den nye kategorien er gitt av input elementet.
   * Funksjonen returnerer den nye kategorien til databasen.
   * @param category - nytt navn til kategorien.
   * @return en http kode som indikerer om funksjonen kjørte eller ikke.
   * @throws ExecutionException - kaster en exception som må håndteres i en høyere abstraksjon.
   * @throws InterruptedException - kaster en exception som må håndteres i en høyere abstraksjon.
   */
  @PostMapping("/createCategory")
  public String createCategory(@RequestParam(value = "category") String category)
          throws ExecutionException, InterruptedException {
    return firestoreService.saveCategory(gson.fromJson(category, Category.class));
  }

  /**Funksjon som sletter en kategori.
   *Kategorien som slettes er den som har samme verdi som input til funksjonen.
   *Den slettede kategorien blir fjernet fra databasen.
   * @param name - navnet til kategorien som slettes.
   * @return en http kode som indikerer om funksjonen kjørte eller ikke.
   * @throws ExecutionException - kaster en exception som må håndteres i en høyere abstraksjon.
   * @throws InterruptedException - kaster en exception som må håndteres i en høyere abstraksjon.
   */
  @DeleteMapping("/deleteCategory")
  public String deleteCategory(@RequestParam(value = "name") String name)
          throws ExecutionException, InterruptedException {
    return firestoreService.deleteCategory(name);
  }

  /**Funksjon som oppdaterer navnet til kategorien fra oldName til newName.
   * @param newName - nye navnet til kategorien.
   * @param oldName - gamle navnet til kategorien.
   * @return en http kode som indikerer om funksjonen kjørte eller ikke.
   */
  @RequestMapping(method = RequestMethod.PUT, value = "/updateCategoryName/{oldName}")
  public String updateCategoryName(
          @RequestParam(value = "newName") String newName, @PathVariable String oldName) {
    String response =  firestoreService.changeCategoryName(oldName, newName);

    if (response.equals("A category with this name already exists.")) {
      throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST, "A category with this name already exists."
      );
    }
    return response;
  }

  /**Funksjon som oppdaterer kategorien.
   * @param categoryJson - kategorien som skal oppdateres.
   * @return en http kode som indikerer om funksjonen kjørte eller ikke.
   * @throws ExecutionException - kaster en exception som må håndteres i en høyere abstraksjon.
   * @throws InterruptedException - kaster en exception som må håndteres i en høyere abstraksjon.
   */
  @PutMapping("/updateCategory")
  public String updateCategory(@RequestParam(value = "category") String categoryJson)
          throws ExecutionException, InterruptedException {
    Category category = gson.fromJson(categoryJson, Category.class);
    firestoreService.deleteCategory(category.getName());
    return firestoreService.saveCategory(category);
  }

}