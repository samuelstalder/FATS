package ch.zhaw.pm2.fats;

import ch.zhaw.pm2.fats.Config.Item;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ch.zhaw.pm2.fats.Config.INVENTORY_LIST_FILE_NAME;

/**
 * The Inventory Data class contains all the data of one inventory object.
 *
 * @author Tim Rhomberg
 */
public class InventoryData {
    private static final Logger LOGGER = Logger.getLogger(App.class.getCanonicalName());

    private final Item item;
    private final IntegerProperty quantity;
    private final Image image;

    /**
     * Class constructor of the Inventory Data.
     *
     * @param fileName The filename of the icon
     * @param quantity The quantity.
     * @param item     The enum of the item.
     */
    public InventoryData(String fileName, int quantity, Item item) {
        File file = new File("ch/zhaw/pm2/fats/items", fileName);
        this.image = new Image(file.toString());
        this.quantity = new SimpleIntegerProperty(quantity);
        this.item = item;
    }

    /**
     * Get the image.
     *
     * @return The image.
     */
    public Image getImage() {
        return this.image;
    }

    /**
     * Get the item.
     *
     * @return The enum item.
     */
    public Item getItem() {
        return item;
    }

    /**
     * Get the quantity property.
     *
     * @return The quantity IntegerProperty.
     */
    public IntegerProperty getQuantityProperty() {
        return quantity;
    }

    /**
     * Remove one item.
     *
     * @return True if one item could be removed, false if not.
     */
    public boolean removeOneItem() {
        if (quantity.get() > 0) {
            quantity.set(quantity.get() - 1);
            LOGGER.log(Level.INFO, "Remove one {0} from the Inventory", item.toString());
            return true;
        }
        return false;
    }

    /**
     * Add one item.
     */
    public void addOneItem() {
        quantity.set(quantity.get() + 1);
        LOGGER.log(Level.INFO, "Add one {0} to the Inventory", item.toString());
    }

    /**
     * Initialize the Config File INVENTORY_LIST_FILE_NAME.
     *
     * @return 2D Array of the content of the config file.
     */
    public static List<InventoryData> initializeItems() {
        List<InventoryData> inventoryData = new ArrayList<>();
        try {
            URL inventoryUrl = ClassLoader.getSystemResource(INVENTORY_LIST_FILE_NAME);

            if (inventoryUrl != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(inventoryUrl.openStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] input = line.split(" ");
                        String fileName = input[0];
                        int quantity = Integer.parseInt(input[1]);
                        Item item = Item.valueOf(input[2].toUpperCase());
                        inventoryData.add(new InventoryData(fileName, quantity, item));
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error while loading Inventory List {0}", e);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error while loading Inventory List {0}", e);
        }
        return inventoryData;
    }
}
