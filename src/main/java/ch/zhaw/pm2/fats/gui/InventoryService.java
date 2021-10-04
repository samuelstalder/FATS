package ch.zhaw.pm2.fats.gui;

import ch.zhaw.pm2.fats.Config;
import ch.zhaw.pm2.fats.Config.Item;
import ch.zhaw.pm2.fats.InventoryData;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.control.Tooltip;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class manages everything which has to do with the inventory.
 *
 * @author Tim Rhomberg
 */
public class InventoryService {
    private final Map<Integer, InventoryObject> playerAInventory;
    private final Map<Integer, InventoryObject> playerBInventory;
    private final PlayWindowController playWindowController;

    /**
     * The main class constructor initialize everything and put it in the two HashMaps.
     *
     * @param playWindowController      The playWindowController object.
     * @param playerAInventoryLabel     The ArrayList of all Inventory Label of Player A.
     * @param playerAInventoryImageView The ArrayList of all Inventory ImageView of Player A.
     * @param playerAInventoryHBox      The ArrayList of all Inventory HBox of Player A.
     * @param playerBInventoryLabel     The ArrayList of all Inventory Label of Player B.
     * @param playerBInventoryImageView The ArrayList of all Inventory ImageView of Player B.
     * @param playerBInventoryHBox      The ArrayList of all Inventory HBox of Player B.
     */
    public InventoryService(PlayWindowController playWindowController, ArrayList<Label> playerAInventoryLabel, ArrayList<ImageView> playerAInventoryImageView, ArrayList<HBox> playerAInventoryHBox, ArrayList<Label> playerBInventoryLabel, ArrayList<ImageView> playerBInventoryImageView, ArrayList<HBox> playerBInventoryHBox) {
        this.playWindowController = playWindowController;
        List<InventoryData> inventoryDataPlayerA = InventoryData.initializeItems();
        List<InventoryData> inventoryDataPlayerB = InventoryData.initializeItems();
        playerAInventory = new TreeMap<>();
        playerBInventory = new TreeMap<>();
        createHashMap(playerAInventoryLabel, playerAInventoryImageView, playerAInventoryHBox, inventoryDataPlayerA, "A");
        createHashMap(playerBInventoryLabel, playerBInventoryImageView, playerBInventoryHBox, inventoryDataPlayerB, "B");
    }

    /**
     * Creates a hash map and insert all the inventory information of the player.
     *
     * @param playerInventoryLabel     The arrayList of all Inventory Label.
     * @param playerInventoryImageView The ArrayList of all Inventory ImageView.
     * @param playerInventoryHBox      The ArrayList of all Inventory HBox.
     * @param inventoryDataList        The ArrayList of all InventoryData.
     * @param player                   The character of the player.
     */
    private void createHashMap(ArrayList<Label> playerInventoryLabel, ArrayList<ImageView> playerInventoryImageView, ArrayList<HBox> playerInventoryHBox, List<InventoryData> inventoryDataList, String player) {
        for (int index = 0; index < playerInventoryLabel.size(); index++) {
            String nameOfId = playerInventoryLabel.get(index).getId().substring(6, 8);
            int indexOfId = Integer.parseInt(nameOfId);
            InventoryData inventoryData = null;
            if (inventoryDataList.size() > index)
                inventoryData = inventoryDataList.get(index);

            if (player.equals("A")) {
                playerAInventory.put(indexOfId, new InventoryObject(playerInventoryLabel.get(index), playerInventoryImageView.get(index), playerInventoryHBox.get(index), inventoryData));
            } else {
                playerBInventory.put(indexOfId, new InventoryObject(playerInventoryLabel.get(index), playerInventoryImageView.get(index), playerInventoryHBox.get(index), inventoryData));
            }
        }
    }

    /**
     * Executes the tests if the item can be selected.
     *
     * @param id    The id of the player.
     * @param index The index of the element in the grid.
     * @return The item if it's passed the tests or null.
     */
    public Item clickItem(String id, int index) {
        Item item = null;
        Map<Integer, InventoryObject> inventoryObjectMap = id.contains("A") ? playerAInventory : playerBInventory;

        // Is item a weapon
        if (inventoryObjectMap.get(index).isWeapon()) {
            if (inventoryObjectMap.get(index).getSelected()) {
                inventoryObjectMap.get(index).setSelected();
                playWindowController.disableErrorMessage();
            } else {
                if (isWeaponAlreadySelected(id)) {
                    playWindowController.printErrorMessage("Weapon already selected! Unselect the weapon if you want to choose another weapon.");
                } else {
                    playWindowController.disableErrorMessage();
                    inventoryObjectMap.get(index).setSelected();
                    item = inventoryObjectMap.get(index).getItem();
                }
            }
        } else {
            item = inventoryObjectMap.get(index).getItem();
        }
        return item;
    }

    /**
     * Get the selected weapon.
     *
     * @param id The character of the player.
     */
    public Item getSelectedWeapon(String id) {
        Item item = Item.STANDARD;
        Map<Integer, InventoryObject> inventoryObjectMap = id.contains("A") ? playerAInventory : playerBInventory;

        for (Map.Entry<Integer, InventoryObject> entry : inventoryObjectMap.entrySet()) {
            if (entry.getValue().getBorder() && entry.getValue().isWeapon())
                item = entry.getValue().getItem();
        }
        return item;
    }

    /**
     * Delete one weapon.
     *
     * @param id   The character of the player.
     * @param item The item.
     * @return True if the item could be removed, that means it has enough of it in the inventory, false if the item could not be used.
     */
    public boolean useOneItem(String id, Item item) {
        Map<Integer, InventoryObject> inventoryObjectMap = id.contains("A") ? playerAInventory : playerBInventory;

        for (Map.Entry<Integer, InventoryObject> entry : inventoryObjectMap.entrySet()) {
            if (entry.getValue().getInventoryData() != null) {
                if (entry.getValue().getItem() == item) {
                    return entry.getValue().getInventoryData().removeOneItem();
                }
            }
        }
        return false;
    }

    /**
     * Add an item to the inventory Data.
     *
     * @param id   The character of the player.
     * @param item The item.
     */
    public void addItem(String id, Item item) {
        Map<Integer, InventoryObject> inventoryObjectMap = id.contains("A") ? playerAInventory : playerBInventory;

        for (Map.Entry<Integer, InventoryObject> entry : inventoryObjectMap.entrySet()) {
            if (entry.getValue().getInventoryData() != null) {
                if (entry.getValue().getItem() == item) {
                    entry.getValue().getInventoryData().addOneItem();
                }
            }
        }
    }

    /**
     * Iterate over all HashMaps. If the border is selected and the enum is a Weapon, it will return true.
     *
     * @param id The id of the character.
     * @return True if the enum is a weapon and the border is selected.
     */
    private boolean isWeaponAlreadySelected(String id) {
        boolean result = false;
        Map<Integer, InventoryObject> inventoryObjectMap = id.contains("A") ? playerAInventory : playerBInventory;

        for (Map.Entry<Integer, InventoryObject> entry : inventoryObjectMap.entrySet()) {
            if (entry.getValue().getBorder() && entry.getValue().isWeapon())
                result = true;
        }
        return result;
    }

    /**
     * The Inventory Object, which contains per each inventory object all information's.
     *
     * @author Tim Rhomberg
     */
    protected static class InventoryObject {
        private final ImageView imageView;
        private final HBox hBox;
        private boolean borderActive;
        private final InventoryData inventoryData;

        /**
         * The main Constructor of the InventoryObject.
         *
         * @param label         The Label Object.
         * @param imageView     The ImageView Object.
         * @param hBox          The hBox Object.
         * @param inventoryData The inventoryData.
         */
        public InventoryObject(Label label, ImageView imageView, HBox hBox, InventoryData inventoryData) {
            this.imageView = imageView;
            this.hBox = hBox;
            this.borderActive = false;
            this.inventoryData = inventoryData;

            if (inventoryData != null) {
                setImages();

                Integer quantity = getInventoryData().getQuantityProperty().get();
                if (quantity.equals(-1)) {
                    label.setText("oo");
                } else {
                    label.textProperty().bind(inventoryData.getQuantityProperty().asString());
                    inventoryData.getQuantityProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue.intValue() == Config.MIN_PERK_VALUE) {
                            imageView.disableProperty().set(true);
                            if (getSelected())
                                setSelected();
                        } else {
                            imageView.disableProperty().set(false);
                        }
                    });
                }
                Tooltip.install(imageView, new Tooltip("The value is: " + getItem().getQuantity()));

                String value;
                if (getInventoryData().getQuantityProperty().get() == -1) {
                    value = "∞";
                } else {
                    value = quantity.toString();
                }
                Tooltip.install(label, new Tooltip("The player has: " + value + " " + getItem().toString()));
            }
        }

        /**
         * Set the image.
         */
        private void setImages() {
            this.imageView.setImage(inventoryData.getImage());
        }

        /**
         * Checks if the item in the inventoryData a weapon is.
         *
         * @return True if it's a weapon and false if not.
         */
        public boolean isWeapon() {
            return inventoryData.getItem().isWeapon();
        }

        /**
         * Checks if the border is on.
         *
         * @return True if the border is on.
         */
        public boolean getBorder() {
            return borderActive;
        }

        /**
         * Get the item from the inventoryData.
         *
         * @return The item.
         */
        public Item getItem() {
            return inventoryData.getItem();
        }

        /**
         * Get the inventoryData.
         *
         * @return The inventoryData.
         */
        public InventoryData getInventoryData() {
            return this.inventoryData;
        }

        /**
         * Get a value if the border is active.
         *
         * @return True if border is active.
         */
        public Boolean getSelected() {
            return borderActive;
        }

        /**
         * Set the border to active, if it is already active, delete the border.
         */
        public void setSelected() {
            if (borderActive) {
                this.borderActive = false;
                hBox.setStyle("");
            } else {
                this.borderActive = true;
                hBox.setStyle("-fx-border-color: red;");
            }
        }
    }
}
