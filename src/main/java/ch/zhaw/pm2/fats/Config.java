package ch.zhaw.pm2.fats;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The config class contains all important Game settings.
 */
public class Config {
    public static int PLAYER_AMOUNT = 2;
    public static final int MAX_PERK_VALUE = 200;
    public static final int MIN_PERK_VALUE = 0;
    public static final int MAX_CANNON_ANGLE = 360;
    public static final String INVENTORY_LIST_FILE_NAME = "InventoryList.txt";
    private static final Point2D STARTING_POINT_TANK_ONE = new Point2D(100, 400);
    private static final Point2D STARTING_POINT_TANK_TWO = new Point2D(600, 400);
    private static final int PLAYER_ONE = 1;
    private static final int PLAYER_TWO = 2;
    public static final int CANVAS_WIDTH = 640;
    public static final int CANVAS_HEIGHT = 480;
    public static final int GROUND_LEVEL = 400;


    /**
     * The SoilType define the playground.
     */
    public enum SoilType {
        EARTH(1, Color.SKYBLUE),
        ROCK(2, Color.DIMGREY),
        SAND(5, Color.web("#F4A460"));

        private final int consumption;
        private final Color color;

        SoilType(int consumption, Color color) {
            this.consumption = consumption;
            this.color = color;
        }

        public int getConsumption() {
            return this.consumption;
        }

        public Color getColor() {
            return this.color;
        }
    }

    /**
     * Items can have different benefits for the user.
     */
    public enum Item {
        HEALTH(10, false),
        ARMOR(50, false),
        FUEL(15,false),
        STANDARD(10, true),
        NUCLEAR(100, true),
        CLUSTER(25, true),
        CHEMICAL(30, true);

        private final int quantity;
        private final boolean isWeapon;

        Item(int quantity, boolean isWeapon) {
            this.quantity = quantity;
            this.isWeapon = isWeapon;
        }

        public int getQuantity() {
            return this.quantity;
        }

        public boolean isWeapon() {
            return isWeapon;
        }

        /**
         * Get a random enum item except Standard Weapon.
         *
         * @return A random enum ite.
         */
        public static Item getRandomItem() {
            List<Item> itemList = new ArrayList<>();
            for (Item item : Item.values()) {
                if(item != Item.STANDARD)
                    itemList.add(item);
            }

            Random random = new Random();
            int randomNumber = random.nextInt(itemList.size());
            return itemList.get(randomNumber);
        }
    }

    /**
     * Get the start position of the Tank.
     *
     * @param player The player number.
     * @return The point of the start position.
     */
    public static Point2D getTankStartingPosition(int player) {
        Point2D toReturn;
        switch(player) {
            case PLAYER_ONE:
                toReturn = STARTING_POINT_TANK_ONE;
                break;
            case PLAYER_TWO:
                toReturn = STARTING_POINT_TANK_TWO;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + player);
        }
        return toReturn;
    }
}
