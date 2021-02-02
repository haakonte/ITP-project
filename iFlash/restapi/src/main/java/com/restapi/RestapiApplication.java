package com.restapi;

import flashcard.server.FirestoreInitializer;
import flashcard.ui.App;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Dette er roten i maven oppstarten, det vil si, det er her Spring boot appen starter.
 * Spring boot serveren starter og setter opp en kobling til firestore databasen.
 * Etter det starter den javafx appen og "implisitt" instansierer RestController-klassen.
 */
@SpringBootApplication
public class RestapiApplication {

  private static ConfigurableApplicationContext ctx;

  /**
   * Main funksjonen har tre hensikter.
   * Starte javafx applikasjonen.
   * Starte Springboot applikasjonen.
   * Sette opp databasekoblingen.
   */
  public static void main(String[] args) {
    FirestoreInitializer firestoreInitializer = new FirestoreInitializer();
    firestoreInitializer.initialize();
    ctx = new SpringApplicationBuilder(RestapiApplication.class).run();
    App.main(ctx, args);
  }

}