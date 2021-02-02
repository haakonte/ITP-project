package flashcard.server;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import org.springframework.stereotype.Service;

@Service
public class FirestoreInitializer {

  /**
   * En simpel metode som bruker en service-account key til å sette opp databasen vår.
   * Den bruker serviceaccount JSON-filen til å initialisere kobling til databasen.
   */
  public void initialize() {
    try {
      FileInputStream serviceAccount =
              new FileInputStream("./serviceAccountKey.json");

      FirebaseOptions options = new FirebaseOptions.Builder()
              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
              .setDatabaseUrl("https://it1901-f5a81.firebaseio.com")
              .build();

      FirebaseApp.initializeApp(options);
    } catch (RuntimeException e) {
      System.out.println("");
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
