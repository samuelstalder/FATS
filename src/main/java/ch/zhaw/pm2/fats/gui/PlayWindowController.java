package ch.zhaw.pm2.fats.gui;

import ch.zhaw.pm2.fats.App;
import ch.zhaw.pm2.fats.Config;
import ch.zhaw.pm2.fats.Config.Item;
import ch.zhaw.pm2.fats.GameState;
import ch.zhaw.pm2.fats.Player;
import ch.zhaw.pm2.fats.canvas.Tank;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The PlayWindowController Class manages the controls of the gui.
 */
public class PlayWindowController {
    private static final Logger LOGGER = Logger.getLogger(App.class.getCanonicalName());

    @FXML
    private Label currentPlayer, playerAHeart, playerBHeart, playerAArmor, playerBArmor, playerALabel, playerBLabel, playerAFuel, playerBFuel, errorMessages;
    @FXML
    private Canvas landscape;
    @FXML
    private Button playerAFire, playerBFire;
    @FXML
    private Slider playerAShotStrength, playerBShotStrength, playerAShotAngle, playerBShotAngle;
    @FXML
    private TextField playerAShotStrengthTxt, playerBShotStrengthTxt, playerAShotAngleTxt, playerBShotAngleTxt;
    @FXML
    private ArrayList<Label> playerAInventoryLabel, playerBInventoryLabel;
    @FXML
    private ArrayList<ImageView> playerAInventoryImageView, playerBInventoryImageView;
    @FXML
    private ArrayList<HBox> playerAInventoryHBox, playerBInventoryHBox;

    private GameState gameState;
    private RendererService rendererService;
    private InventoryService inventoryService;

    /**
     * Create a listener for the slider of the Player A and B. If the slider changes the value, it will change automatic
     * the value of the text box.
     */
    private void initializeListenerSlider() {
        playerAShotStrength.valueProperty().addListener((ov, old_val, new_val) -> {
            String output = String.format("%d", new_val.intValue());
            playerAShotStrengthTxt.setText(output);
            playerAShotStrengthTxt.positionCaret(playerAShotStrengthTxt.getText().length());
        });

        playerBShotStrength.valueProperty().addListener((ov, old_val, new_val) -> {
            String output = String.format("%d", new_val.intValue());
            playerBShotStrengthTxt.setText(output);
            playerBShotStrengthTxt.positionCaret(playerBShotStrengthTxt.getText().length());
        });

        playerAShotAngle.valueProperty().addListener((observable, oldValue, newValue) -> {
            gameState.getTanks().get(0).setCannonAngle(newValue.intValue());
            rendererService.renderObjects();
            playerAShotAngleTxt.setText(String.format("%d", newValue.intValue()));
            playerAShotAngleTxt.positionCaret(playerAShotAngleTxt.getText().length());
        });

        playerBShotAngle.valueProperty().addListener((observable, oldValue, newValue) -> {
            gameState.getTanks().get(1).setCannonAngle(newValue.intValue());
            rendererService.renderObjects();
            playerBShotAngleTxt.setText(String.format("%d", newValue.intValue()));
            playerBShotAngleTxt.positionCaret(playerBShotAngleTxt.getText().length());
        });

        gameState.addPropertyChangeListener(evt -> {
            Player newPlayer = (Player) evt.getNewValue();
            Player oldPlayer = (Player) evt.getOldValue();
            currentPlayer.setText("Current Player is " + newPlayer.getName());
            LOGGER.log(Level.INFO, "Player switched from " + oldPlayer.getName() + " to " + newPlayer.getName());
            if (gameState.getCurrentPlayer().equals(gameState.getPlayers().get(0))) {
                playerAFire.setDisable(false);
                playerBFire.setDisable(true);
            } else {
                playerAFire.setDisable(true);
                playerBFire.setDisable(false);
            }
        });
    }

    /**
     * Create a listener for the armor, health and fuel.
     */
    private void initializeListenerArmorHealthFuel() {
        playerAFuel.textProperty().bind(gameState.getTanks().get(0).getFuelProperty().asString());
        playerBFuel.textProperty().bind(gameState.getTanks().get(1).getFuelProperty().asString());
        playerAArmor.textProperty().bind(gameState.getTanks().get(0).getArmorProperty().asString());
        playerBArmor.textProperty().bind(gameState.getTanks().get(1).getArmorProperty().asString());
        playerAHeart.textProperty().bind(gameState.getTanks().get(0).getHealthProperty().asString());
        playerBHeart.textProperty().bind(gameState.getTanks().get(1).getHealthProperty().asString());
    }

    /**
     * Add key events to the scene
     */
    private void addKeyEventToScene() {
        Scene playWindowScene = landscape.getScene();
        playWindowScene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.D)) {
                LOGGER.log(Level.CONFIG, "Drive right");
                gameState.getCurrentTank().move(Tank.MovementDirection.RIGHT, rendererService, gameState.getSoilType().getConsumption());
                rendererService.renderObjects();
                addItemToInventory();
            } else if (event.getCode().equals(KeyCode.A)) {
                LOGGER.log(Level.CONFIG, "Drive left");
                gameState.getCurrentTank().move(Tank.MovementDirection.LEFT, rendererService, gameState.getSoilType().getConsumption());
                rendererService.renderObjects();
                addItemToInventory();
            } else if (event.getCode().equals(KeyCode.W)) {
                if (gameState.getCurrentPlayer().equals(gameState.getPlayers().get(0))) {
                    if (playerAShotAngle.getValue() < Config.MAX_CANNON_ANGLE) {
                        LOGGER.log(Level.CONFIG, "Change Pipe Angle");
                        gameState.getCurrentTank().setCannonAngle(gameState.getCurrentTank().getCannonAngle() + 10);
                        playerAShotAngle.setValue(gameState.getCurrentTank().getCannonAngle());
                        rendererService.renderObjects();
                    }
                } else {
                    if (playerBShotAngle.getValue() < Config.MAX_CANNON_ANGLE) {
                        LOGGER.log(Level.CONFIG, "Change Pipe Angle");
                        gameState.getCurrentTank().setCannonAngle(gameState.getCurrentTank().getCannonAngle() + 10);
                        playerBShotAngle.setValue(gameState.getCurrentTank().getCannonAngle());
                        rendererService.renderObjects();
                    }
                }
            } else if (event.getCode().equals(KeyCode.S)) {
                if (gameState.getCurrentPlayer().equals(gameState.getPlayers().get(0))) {
                    if (playerAShotAngle.getValue() >= 10) {
                        LOGGER.log(Level.INFO, "Change Pipe Angle");
                        gameState.getCurrentTank().setCannonAngle(gameState.getCurrentTank().getCannonAngle() - 10);
                        playerAShotAngle.setValue(gameState.getCurrentTank().getCannonAngle());
                        rendererService.renderObjects();
                    }
                } else {
                    if (playerBShotAngle.getValue() >= 10) {
                        LOGGER.log(Level.INFO, "Change Pipe Angle");
                        gameState.getCurrentTank().setCannonAngle(gameState.getCurrentTank().getCannonAngle() - 10);
                        playerBShotAngle.setValue(gameState.getCurrentTank().getCannonAngle());
                        rendererService.renderObjects();
                    }
                }
            }
        });
    }

    /**
     * Add collected item to the inventory.
     */
    private void addItemToInventory() {
        Item item = gameState.getCurrentTank().getItem();
        if (item != null) {
            if (gameState.getCurrentPlayer().equals(gameState.getPlayers().get(0))) {
                inventoryService.addItem("A", item);
            }
        }
    }

    /**
     * Set the data field game which is a model class. And set the Player Name.
     *
     * @param gameState The game object.
     */
    public void initGame(GameState gameState) {
        this.gameState = gameState;

        ArrayList<Player> players = gameState.getPlayers();
        playerALabel.setText(players.get(0).getName());
        playerBLabel.setText(players.get(1).getName());

        initializeListenerSlider();
        initializeListenerArmorHealthFuel();

        currentPlayer.setText("Current Player is " + gameState.getCurrentPlayer().getName());

        addKeyEventToScene();

        rendererService = new RendererService(landscape.getGraphicsContext2D(), landscape, gameState.createCanvasObjects(), gameState.getSoilType());
        rendererService.start();

        inventoryService = new InventoryService(this, playerAInventoryLabel, playerAInventoryImageView, playerAInventoryHBox,
                playerBInventoryLabel, playerBInventoryImageView, playerBInventoryHBox);

        if (gameState.getCurrentPlayer().equals(gameState.getPlayers().get(0))) {
            playerAFire.setDisable(false);
            playerBFire.setDisable(true);
        } else {
            playerAFire.setDisable(true);
            playerBFire.setDisable(false);
        }
    }

    /**
     * This method is for the inventory. When a user clicks in the inventory grid on a ImageView, it will execute this method.
     * It extract from the ImageView Id the player Character (A or B). Then it will check which index the ImageView is in the ArrayList.
     * At the end it will execute the method selectInventoryObject of the InventoryClass.
     *
     * @param event The event Object.
     */
    @FXML
    public void gridClicked(Event event) {
        String eventSource = ((ImageView) event.getSource()).getId();
        String player = eventSource.substring(0, 1).toUpperCase();
        String nameOfId = eventSource.substring(6, 8);

        Item result = inventoryService.clickItem(player, Integer.parseInt(nameOfId));
        if (result != null) {
            Item item = Item.valueOf(result.toString().toUpperCase());
            if (!item.isWeapon()) {
                if (inventoryService.useOneItem(player, item)) {
                    if (player.equals("A"))
                        gameState.addInventoryItem(result, gameState.getTanks().get(0));
                    else
                        gameState.addInventoryItem(result, gameState.getTanks().get(1));
                }
            }
        }
    }

    /**
     * Print the error Message.
     *
     * @param errorMessage The error message.
     */
    public void printErrorMessage(String errorMessage) {
        errorMessages.setVisible(true);
        errorMessages.setStyle("-fx-border-color: red;");
        errorMessages.setText(errorMessage);
    }

    /**
     * Print the error Message.
     */
    public void disableErrorMessage() {
        errorMessages.setVisible(false);
        errorMessages.setStyle("");
    }

    /**
     * This method is executed when a player press the fire Button
     *
     * @param event The event Object.
     */
    @FXML
    public void fireButton(Event event) {
        String buttonId = ((Button) event.getSource()).getId();
        String player = buttonId.substring(6, 7).toUpperCase();
        Item weapon = inventoryService.getSelectedWeapon(player);
        if (!inventoryService.useOneItem(player, weapon)) {
            weapon = Item.STANDARD;
        }
        LOGGER.log(Level.INFO, "Selected weapon value is " + weapon.toString());
        if (buttonId.startsWith("playerA")) {
            gameState.fire(Integer.parseInt(playerAShotStrengthTxt.getText()), weapon, rendererService);
            LOGGER.log(Level.INFO, "Player A fired");
        } else {
            gameState.fire(Integer.parseInt(playerBShotStrengthTxt.getText()), weapon, rendererService);
            LOGGER.log(Level.INFO, "Player B fired");
        }

        rendererService.renderObjects();
        gameState.switchPlayerAndTank();

        ArrayList<Player> playersAlive = gameState.getPlayersAlive();
        if (gameState.getWinner(playersAlive) != null) {
            Player actualWinner = gameState.getWinner(playersAlive);
            LOGGER.log(Level.INFO, "-------------" + actualWinner.toString());
            LOGGER.log(Level.INFO, "Player " + actualWinner.getName() + " has won");
            displayEndAlertBox(actualWinner.getName());
        }
    }

    /**
     * Display the Alert Box at the end.
     *
     * @param winner The winner.
     */
    private void displayEndAlertBox(String winner) {
        Alert.AlertType type = Alert.AlertType.CONFIRMATION;
        Alert alert = new Alert(type, "");

        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(currentPlayer.getScene().getWindow());
        alert.getDialogPane().setContentText(String.format("%s has won", winner));
        alert.getDialogPane().setHeaderText("Game finished");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            restartGame();
        } else {
            Stage primaryStage = (Stage) errorMessages.getScene().getWindow();
            primaryStage.close();
        }
    }

    /**
     * Reloads the game and start StartWindow.fxml.
     */
    private void restartGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StartWindow.fxml"));
            Parent root = loader.load();

            Scene startScene = new Scene(root);

            Stage primaryStage = (Stage) currentPlayer.getScene().getWindow();
            primaryStage.setScene(startScene);
        } catch (IOException | NullPointerException e) {
            LOGGER.log(Level.WARNING, "Something got wrong while loading the fxml", e);
        }
    }

    /**
     * Change the value of the slider, when someone types characters in the text field.
     */
    @FXML
    public void changeSliderValueWithTextField(Event event) {
        String textInput = ((TextField) event.getSource()).getText();
        String textId = ((TextField) event.getSource()).getId();
        Platform.runLater(() -> {
            try {
                double newValue = Double.parseDouble(textInput);
                LOGGER.log(Level.INFO, String.format("The textfield with the id %s was used, new value is %s", textId, newValue));
                if (textId.startsWith("playerAShotStrength")) {
                    playerAShotStrength.setValue(newValue);
                } else if (textId.startsWith("playerBShotStrength")) {
                    playerBShotStrength.setValue(newValue);
                } else if (textId.startsWith("playerAShotAngle")) {
                    playerAShotAngle.setValue(newValue);
                } else if (textId.startsWith("playerBShotAngle")) {
                    playerBShotAngle.setValue(newValue);
                }
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid Number Format", e);
            }
        });
    }
}
