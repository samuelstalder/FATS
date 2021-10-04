package ch.zhaw.pm2.fats.gui;

import ch.zhaw.pm2.fats.App;
import ch.zhaw.pm2.fats.Config;
import ch.zhaw.pm2.fats.GameState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Controller class of the PlaySubScene. Manage the methods of the subScene.
 */
public class PlaySubSceneController {
    private static final Logger LOGGER = Logger.getLogger(App.class.getCanonicalName());
    private static final int STAGE_WIDTH = 1020;
    private static final int STAGE_HEIGHT = 660;

    @FXML
    private RadioButton rock, earth, sand;
    @FXML
    private Button playGame;
    @FXML
    private TextField playerAName, playerBName;
    @FXML
    private TextFlow errorMessages;

    private ToggleGroup radioGroup;

    /**
     * Initialize the game and set a toggle group which contains all radio buttons.
     */
    @FXML
    public void initialize() {
        radioGroup = new ToggleGroup();
        sand.setToggleGroup(radioGroup);
        rock.setToggleGroup(radioGroup);
        earth.setToggleGroup(radioGroup);

        radioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            LOGGER.log(Level.INFO, "The new Toggle is " + ((RadioButton) newValue).getText());
            playGame.setDisable(false);
            new InputService(errorMessages, playerAName).start();
            new InputService(errorMessages, playerBName).start();
        });
    }

    /**
     * Read the name of the selected radio button and ask the Enum of it. Create an ArrayList with the Player Names.
     * Create the Game Object and pass it to the Controller. Load a new Scene and applied it to the new Controller
     */
    @FXML
    public void startGame() {
        try {
            if (errorMessages.getChildren().isEmpty()) {
                String soilName = ((RadioButton) radioGroup.getSelectedToggle()).getText();
                Config.SoilType soilType = Config.SoilType.valueOf(soilName.toUpperCase());

                ArrayList<String> playerNames = new ArrayList<>();
                playerNames.add(playerAName.getText());
                playerNames.add(playerBName.getText());
                GameState gameState = new GameState(soilType, playerNames);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayWindow.fxml"));

                Parent root = loader.load();

                Scene playScene = new Scene(root);

                PlayWindowController playWindowController = loader.getController();
                playWindowController.initGame(gameState);

                Stage primaryStage = (Stage) playGame.getScene().getWindow();
                primaryStage.setMinWidth(STAGE_WIDTH);
                primaryStage.setMinHeight(STAGE_HEIGHT);
                primaryStage.setScene(playScene);
            }
        } catch (IOException | NullPointerException e) {
            LOGGER.log(Level.WARNING, "Something got wrong while loading the fxml", e);
        }
    }

    /**
     * Checks if the value of the textfield of Player A is correct.
     */
    @FXML
    public void checkPlayerA() {
        new InputService(errorMessages, playerAName).start();
    }

    /**
     * Checks if the value of the textfield of Player B is correct.
     */
    @FXML
    public void checkPlayerB() {
        new InputService(errorMessages, playerBName).start();
    }
}
