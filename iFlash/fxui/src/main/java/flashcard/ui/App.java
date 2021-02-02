package flashcard.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;

public class App extends Application {

  private static ConfigurableApplicationContext ctx;

  @Override
  public void start(final Stage primaryStage) throws Exception {
    final FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
    final Parent parent = loader.load();
    final MainMenuController controller = loader.getController();
    primaryStage.setScene(new Scene(parent));
    primaryStage.setMinHeight(100);
    primaryStage.setMinWidth(200);
    controller.setPrimaryStage(primaryStage);
    primaryStage.show();
  }


  @Override
  public void stop() throws Exception {
    ctx.close();
    ctx.stop();
    super.stop();
  }

  public static void main(ConfigurableApplicationContext ctx, final String[] args) {
    App.ctx = ctx;
    launch(args);
  }
}