package ch.zhaw.pm2.fats.gui;

import ch.zhaw.pm2.fats.App;
import javafx.animation.TranslateTransition;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Controller of the Start Window.
 */
public class StartWindowController {
    private static final Logger LOGGER = Logger.getLogger(App.class.getCanonicalName());
    private HostServices services;

    @FXML
    private Pane startPane;

    /**
     * Change the scene of the pane. For that it checks the id of the button, which was clicked.
     */
    @FXML
    public void sceneChange(Event event) {
        Button eventButton = (Button) event.getSource();
        String filename;
        if (eventButton.getId().equals("scenePlayGame"))
            filename = "PlaySubScene.fxml";
        else if (eventButton.getId().equals("sceneCredits"))
            filename = "CreditsSubScene.fxml";
        else
            filename = "HelpSubScene.fxml";
        Platform.runLater(() -> {
            deleteLastPane();
            Pane pane = getPane(filename);
            pane.setLayoutX(750);
            pane.setLayoutY(50);

            TranslateTransition translateTransition = new TranslateTransition();
            translateTransition.setDuration(Duration.seconds(2));
            translateTransition.setToX(-500);
            translateTransition.setNode(pane);

            startPane.getChildren().add(pane);
            translateTransition.play();
        });
    }

    /**
     * Close the game.
     */
    @FXML
    public void sceneEndGame() {
        Stage primaryStage = (Stage) startPane.getScene().getWindow();
        primaryStage.close();
    }

    /**
     * Loads the FXML File and returns the view.
     *
     * @param fileName The filename of the fxml file.
     * @return The view of the fxml file
     */
    private Pane getPane(String fileName) {
        Pane view = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
            view = loader.load();

            if (fileName.equals("HelpSubScene.fxml")) {
                HelpSubSceneController helpSubSceneController = loader.getController();
                helpSubSceneController.setServices(services);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "An error occurred while loading an FXML File" + e);
        }
        return view;
    }

    /**
     * Delete the last pane from the startPane
     */
    private void deleteLastPane() {
        if (startPane.getChildren().size() == 3)
            startPane.getChildren().remove(2);
    }

    /**
     * Set the hostService.
     *
     * @param services The services object.
     */
    public void setService(HostServices services) {
        this.services = services;
    }
}
