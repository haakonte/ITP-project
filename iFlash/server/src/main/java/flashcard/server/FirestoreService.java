package flashcard.server;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import flashcard.core.Category;
import flashcard.core.Flashcard;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * En klasse som håndterer server-requests, dette er tilkoblingen til en database på internett.
 * I denne klassen har vi metoder som snakker med databasen, og henter og lagrer konkrete data
 * i databasen. Metodene vil i noen tilfeller returnere HTTP-koder som 200 eller 404, som
 * betyr at vi enten klarte å hente og skrive til databasen, eller om det gikk galt.
 */
@Service
public class FirestoreService {

  private final Firestore db = FirestoreClient.getFirestore();

  /**
   * En server-side metode som lagrer en kategori i fireStore-databasen vår.
   * Den tar inn en kategori som skal lagres, og returnerer en HTTP-kode.
   * HTTP-kode 200 betyr at det ikke skjedde noe errors med databasen.
   * @param category - kategorien metoden lagrer i databasen
   * @return HTTP-kode som gir en pekepinn på om vi fikk lagret eller ikke
   * @throws ExecutionException - kaster en exception som må håndteres ett nivå opp
   * @throws InterruptedException - kastes hvis server-side threaden er okkupert eller utilgjengelig
   */
  public String saveCategory(Category category) throws ExecutionException, InterruptedException {
    Map<String, Object> docData = new HashMap<>();
    docData.put("name", category.getName());
    List<Flashcard> list = new ArrayList<>();
    category.iterator().forEachRemaining(list::add);
    docData.put("flashcards", list);

    ApiFuture<WriteResult> future =  db
            .collection("categories")
            .document(category.getName())
            .set(docData);

    return future.get().getUpdateTime().toString();

  }

  /**
   * En metode som har til hensikt å slette en kategori i databasen.
   * Tar inn ett kategorinavn, som den da forsøker å finne, og skal
   * da slette den kategorien
   * @param categoryName - navnet på kategorien vi ønsker å slette
   * @return HTTP kode som gir oss en pekepinn på om vi fikk connected til og slettet eller ikke
   * @throws ExecutionException - en exception som må håndteres videre
   * @throws InterruptedException - kastes hvis en thread er opptatt eller utilgjengelig
   */
  public String deleteCategory(String categoryName) throws ExecutionException,
          InterruptedException {
    ApiFuture<WriteResult> future = db.collection("categories").document(categoryName).delete();
    return future.get().getUpdateTime().toString();
  }


  /**
   * En metode for å hente kategori fra database ut i fra et kategorinavn.
   * Returnerer et Category objekt, ellers sier den ifra at den kategorien ikke fantes
   * @param categoryName - navnet til kategorien vi ønsker å hente
   * @return - en kategori med navnet vi ønsker
   * @throws ExecutionException - må håndteres i høyere abstraksjon
   * @throws InterruptedException - kastes hvis en thread er opptatt eller utilgjengelig
   */
  public Category getCategory(String categoryName) throws ExecutionException, InterruptedException {
    DocumentReference documentReference = db.collection("categories").document(categoryName);
    ApiFuture<DocumentSnapshot> future = documentReference.get();
    DocumentSnapshot documentSnapshot = future.get();
    Category category = new Category();
    if (documentSnapshot.exists()) {
      Map<String, Object> map = documentSnapshot.getData();
      category.setName((String) map.get("name"));

      List<Map> flashCards = (List<Map>) map.get("flashcards");

      for (Map<String, String> map1 : flashCards) {
        category.addFlashCard(new Flashcard(map1.get("text1"), map1.get("text2")));
      }

    } else {
      category = null;
    }
    return category;
  }

  /**
   * En hjelpemetode for å oppdatere navnet til en kategori.
   * @param oldName - navnet på den gamle utdaterte kategorien
   * @param newName - navnet på den nye oppdatere kategorien med oppdaterte flashcards
   * @return - En HTTP-kode for om vi klarte å oppdatere en kategori
   */
  public String changeCategoryName(String oldName, String newName) {
    Category category;
    try {
      if (getCategory(newName) != null) {
        return "A category with this name already exists.";
      }

      category = getCategory(oldName);
      deleteCategory(oldName);
      category.setName(newName);
      return saveCategory(category);

    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
    } catch (NullPointerException e) {
      return "No such category Exists";
    }
    return "Something went wrong";
  }

  /**
   * En metode som henter alle kategoriene i databasen.
   * Den gir oss alle kategorinavnene i en liste.
   * @return - en liste med strings, der hvert element er et kategorinavn
   * @throws ExecutionException - må håndteres i høyere abstraksjon
   * @throws InterruptedException - hvis en thread er opptatt eller utilgjengelig
   */
  public List<String> getCategories() throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference collectionReference = db.collection("categories");
    ApiFuture<QuerySnapshot> future = collectionReference.get();
    return future.get()
                               .getDocuments()
                               .stream()
                               .map(DocumentSnapshot::getId).collect(Collectors.toList());
  }

}
