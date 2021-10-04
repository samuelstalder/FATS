package ch.zhaw.pm2.fats.gui;

import ch.zhaw.pm2.fats.App;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The model of the Start Window. Checks if all input made in the Start Window are valid.
 */
public class InputService extends Service<Integer> {
    private static final Logger LOGGER = Logger.getLogger(App.class.getCanonicalName());

    private final String playerName;
    private final String characterId;
    private final List<Node> nodes;
    private final TextFlow textFlow;
    private final TextField textField;
    private Text text;

    /**
     * Constructor of InputService. Add an errorMessage if necessary.
     *
     * @param textFlow  The textFlow Object.
     * @param textField The textfield Object.
     */
    public InputService(TextFlow textFlow, TextField textField) {
        this.playerName = textField.getText();
        this.textField = textField;
        this.textFlow = textFlow;
        characterId = textField.getId().substring(6, 7);
        nodes = textFlow.getChildren();

        setOnSucceeded(event -> {
            Integer errorNumber = (Integer) event.getSource().getValue();
            if (errorNumber.equals(0)) {
                if (hasTextItemAppended(0).equals(true)) {
                    Iterator<Node> it = nodes.iterator();
                    while (it.hasNext()) {
                        String textError;
                        if (errorNumber == 0) {
                            textError = "text" + characterId;
                        } else {
                            textError = "text" + characterId + errorNumber;
                        }
                        Text node = (Text) it.next();
                        if (node.getId().startsWith(textError)) {
                            it.remove();
                        }
                    }
                }
            } else {
                if (hasTextItemAppended(errorNumber).equals(false)) {
                    textFlow.getChildren().add(text);
                }
            }
        });
    }

    /**
     * Checks if an error Message is appended to the text flow.
     *
     * @param errorNumber The errorNumber.
     * @return True if a text item was already appended, false if not.
     */
    private Boolean hasTextItemAppended(Integer errorNumber) {
        for (Node value : nodes) {
            String textError;
            if (errorNumber.equals(0))
                textError = "text" + characterId;
            else
                textError = "text" + characterId + errorNumber;
            Text node = (Text) value;
            if (node.getId().startsWith(textError)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the player name valid is.
     *
     * @param playerName The names of the Player.
     * @return 0 if all is good.
     * 1 if its blank.
     * 2 if its too long
     */
    public Integer isPlayerNameValid(String playerName) {
        if (playerName.isBlank()) {
            LOGGER.log(Level.WARNING, "The player name is blank!");
            return 1;
        }
        if (playerName.strip().length() >= 10) {
            LOGGER.log(Level.WARNING, "The player name is too long!");
            return 2;
        }
        LOGGER.log(Level.INFO, "The player name is correct.");
        return 0;
    }

    /**
     * If the error Number is 0 the Style of the textfield will be reset.
     * If the error Number is not 0 the border will change to red and a error message will be written in the text object.
     *
     * @return The error Number.
     */
    @Override
    protected Task<Integer> createTask() {
        return new Task<>() {
            @Override
            protected Integer call() {
                Integer errorNumber = isPlayerNameValid(playerName);
                if (errorNumber.equals(0)) {
                    textField.setStyle("");
                } else {
                    textField.setStyle("-fx-border-color: red;");
                    textFlow.setVisible(true);
                    if (hasTextItemAppended(errorNumber).equals(false)) {
                        if (errorNumber.equals(1)) {
                            text = new Text("[ERROR] Please put in the text field " + characterId + " Player Name!\n");
                        } else {
                            text = new Text("[ERROR] Your Player Name in the text field " + characterId + " is too long!\n");
                        }
                        LOGGER.log(Level.WARNING, text.toString());
                        text.setId("text" + characterId + errorNumber);
                        text.setFill(Color.RED);
                    }
                }
                return errorNumber;
            }
        };
    }
}
