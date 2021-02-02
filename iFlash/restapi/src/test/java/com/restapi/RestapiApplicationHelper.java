package com.restapi;

import flashcard.server.FirestoreInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RestapiApplicationHelper {

    private static ConfigurableApplicationContext ctx;

    /**
     * Main funksjonen har to hensikter.
     * Starte Springboot applikasjonen.
     * Sette opp databasekoblingen.
     */
    public static void main(String[] args) {
        FirestoreInitializer firestoreInitializer = new FirestoreInitializer();
        firestoreInitializer.initialize();
        ctx = new SpringApplicationBuilder(RestapiApplicationHelper.class).run();
    }

    /**
     * Denne sørger for å avslutte og stoppe javafx applikasjonen og databasetilkoblingen.
     */
    public static void stopRunning() {
        ctx.close();
        ctx.stop();
    }

}
