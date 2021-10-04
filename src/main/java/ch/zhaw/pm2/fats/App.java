package ch.zhaw.pm2.fats;

import ch.zhaw.pm2.fats.gui.AppGUI;
import javafx.application.Application;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * The main application
 */
public class App {
    private static final Logger LOGGER = Logger.getLogger(App.class.getCanonicalName());

    /**
     * Main Application.
     *
     * @param args  The arguments from the program.
     */
    public static void main(String[] args) {
        // Initialize LogManager: must only be done once at application startup
        try {
            InputStream config = App.class.getClassLoader().getResourceAsStream("log.properties");
            LogManager.getLogManager().readConfiguration(config);
        } catch (IOException e) {
            LOGGER.log(Level.CONFIG,"No log.properties", e);
        }
        // Start UI
        LOGGER.info("Starting FATS Application");
        Application.launch(AppGUI.class, args);
        LOGGER.info("FATS Application ended");
    }
}
