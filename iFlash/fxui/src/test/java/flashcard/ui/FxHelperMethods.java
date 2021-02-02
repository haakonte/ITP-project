package flashcard.ui;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.Node;
import org.testfx.util.WaitForAsyncUtils;

/** En hjelpeklasse med statiske metoder for å endre scener relativt smertefritt
 *  To metoder foreløpig som endrer scene fra MainMenu til kategori-vindu,
 *  og motsatt.
 */
public interface FxHelperMethods {

  /**
   * En enkel hjelpemetode som gjør at testene ikke går så lynraskt.
   * Da kan vi i hvertfall se hva som foregår.
   */
  static void waitTwoSeconds() {
    try {
      TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * En metode som fungerer slik at vi venter på Platform.runLater() kallet.
   * Det kallet er vi avhengige av for å få separert ut endring av scene i en separat
   * thread som ikke går på JavaFX threaden, og ved å bruke denne metoden, så
   * lar vi Platform.runLater() gjøre seg ferdig, og så går vi videre i programmet
   * @throws InterruptedException - ved interrupt
   */
  static void waitForRunLater() throws InterruptedException {
    Semaphore semaphore = new Semaphore(0);
    Platform.runLater(semaphore::release);
    semaphore.acquire();
  }

  /**
   * En hjelpemetode som gjør at vi får ventet til alle FX-events har gjort seg ferdig.
   * Da slipper vi at FX-events tuller til threaden eller testen
   */
  static void waitForFX() {
    WaitForAsyncUtils.waitForFxEvents();
  }

  /**
   * En hjelpemetode som gjør generell leting for oss.
   * @param query - tar inn en query
   */
  <T extends Node> T find(final String query);
}
