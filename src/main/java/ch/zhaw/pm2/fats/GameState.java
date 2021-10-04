package ch.zhaw.pm2.fats;

import ch.zhaw.pm2.fats.canvas.Chest;
import ch.zhaw.pm2.fats.canvas.Drawable;
import ch.zhaw.pm2.fats.canvas.House;
import ch.zhaw.pm2.fats.canvas.Projectile;
import ch.zhaw.pm2.fats.canvas.Tank;
import ch.zhaw.pm2.fats.canvas.Tree;
import ch.zhaw.pm2.fats.gui.RendererService;
import ch.zhaw.pm2.fats.canvas.soil.Earth;
import ch.zhaw.pm2.fats.canvas.soil.Rock;
import ch.zhaw.pm2.fats.canvas.soil.Sand;
import javafx.geometry.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The game state class manages the game logic and stores all the relevant information.
 */
public class GameState {
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final ArrayList<Player> players;
    private final ArrayList<Tank> tanks;
    private Config.SoilType field;
    private Player currentPlayer;
    private Tank currentTank;
    private ArrayList<Player> playersRemaining;
    private ArrayList<Drawable> collidedObjects;
    private ArrayList<Drawable> drawableList;
    private ArrayList<Point2D> groundPoints;

    private static final Logger LOGGER = Logger.getLogger(App.class.getCanonicalName());

    /**
     * This class alters the game-state and initializes all properties needed to play the game.
     *
     * @param field       Config.SoilType of the map
     * @param playerNames list of all the players names
     */
    public GameState(Config.SoilType field, ArrayList<String> playerNames) {
        players = new ArrayList<>();
        tanks = new ArrayList<>();
        playersRemaining = new ArrayList<>();
        drawableList = new ArrayList<>();
        groundPoints = new ArrayList<>();
        collidedObjects = new ArrayList<>();
        this.field = field;
        initializePlayerAndTank(playerNames);
        setCurrentPlayerAndTank();
    }

    /**
     * Get an ArrayList of all Tanks.
     *
     * @return ArrayList of all Tanks.
     */
    public ArrayList<Tank> getTanks() {
        return tanks;
    }

    /**
     * Get the SoilType.
     * @return The SoilType of this object.
     */
    public Config.SoilType getSoilType() {
        return this.field;
    }

    /**
     * Switch current Player and Tank. After that the information that something has changed will be propagated to the controller.
     */
    public void switchPlayerAndTank() {
        Player oldPlayer = currentPlayer;
        if (currentPlayer.getPlayer() == 0) {
            currentPlayer = players.get(1);
            currentTank = tanks.get(1);
        } else {
            currentPlayer = players.get(0);
            currentTank = tanks.get(0);
        }
        propertyChangeSupport.firePropertyChange("currentPlayer", oldPlayer, currentPlayer);
    }

    /**
     * Get the current Player.
     *
     * @return The current Player.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Get the current Tank.
     *
     * @return The current Tank.
     */
    public Tank getCurrentTank() {
        return currentTank;
    }

    /**
     * Gets an ArrayList of all Player who are still alive.
     *
     * @return ArrayList of all Player who are still alive.
     */
    public ArrayList<Player> getPlayersAlive() {
        playersRemaining.clear();
        for (int id = 0; id < tanks.size(); id++) {
            if (!(tanks.get(id).getIsAlive())) {
                playersRemaining.add(players.get(id));
            }
        }
        return playersRemaining;
    }

    /**
     * Gets the winner of all remaining Player. For that it checks the size of the ArrayList.
     *
     * @param playerRemaining The ArrayList of all remaining Players.
     * @return The player who is the winner or null.
     */
    public Player getWinner(ArrayList<Player> playerRemaining) {
        Player winner = null;
        final int onePlayerLeft = 1;
        if (playerRemaining.size() == onePlayerLeft) {
            winner = playerRemaining.get(0);
        }
        return winner;
    }

    /**
     * Checks if any of the playing Tanks is destroyed
     */
    public void checkIfTankGotDestroyed() {
        for (Player player : players) {
            if (tanks.get(player.getPlayer()).getHealth() <= Config.MIN_PERK_VALUE)
                tanks.get(player.getPlayer()).setDestroyed();
        }
    }

    /**
     * Gets an ArrayList of all Players.
     *
     * @return The ArrayList of all Player.
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Initialize the Player and the Tank.
     *
     * @param playerNames An ArrayList of player names.
     */
    public void initializePlayerAndTank(ArrayList<String> playerNames) {
        final int correctionForArrayList = 1;
        for (int player = 1; player <= Config.PLAYER_AMOUNT; player++) {
            players.add(new Player(playerNames.get(player - correctionForArrayList), player - 1));
            tanks.add(new Tank(player, Config.getTankStartingPosition(player)));
        }
    }

    /**
     * Set the initial value of the current Player and Tank.
     */
    private void setCurrentPlayerAndTank() {
        final int firstElementFromList = 0;
        currentPlayer = players.get(firstElementFromList);
        currentTank = tanks.get(firstElementFromList);
    }

    /**
     * Adds a Listener to the propertyChangeSupport object.
     *
     * @param listener The listener Object.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Create all Canvas Objects and put it in a ArrayList. The ground differentiate between the different soil types.
     *
     * @return An ArrayList of all drawable Objects.
     */
    public ArrayList<Drawable> createCanvasObjects() {
        groundPoints.add(new Point2D(0, Config.GROUND_LEVEL));
        groundPoints.add(new Point2D(Config.CANVAS_WIDTH, Config.GROUND_LEVEL));
        if (field == Config.SoilType.EARTH) {
            drawableList.add(new Earth(groundPoints));
        } else if (field == Config.SoilType.ROCK) {
            drawableList.add(new Rock(groundPoints));
        } else {
            drawableList.add(new Sand(groundPoints));
        }
        //drawableList.add(new Ground(groundPoints));
        drawableList.add(new Chest(new Point2D(200, Config.GROUND_LEVEL), 1));
        drawableList.add(new House(new Point2D(300, Config.GROUND_LEVEL), 0));
        drawableList.add(new Tree(new Point2D(500, Config.GROUND_LEVEL), 2));

        drawableList.addAll(tanks);

        return drawableList;
    }


    /**
     * Add the inventory item to the tank.
     *
     * @param item The inventory item.
     */
    public void addInventoryItem(Config.Item item, Tank tank) {
        if (item == Config.Item.ARMOR)
            tank.setArmorWithInventory(item.getQuantity());
        else if (item == Config.Item.HEALTH)
            tank.setHealthWithInventory(item.getQuantity());
        else if (item == Config.Item.FUEL)
            tank.setFuelWithInventory(item.getQuantity());
    }


    /**
     * Returns a projectile object that has recently been fired
     * @param bulletSpeed speed of the shot
     * @param weapon the type of projectile
     * @return
     */
    public void fire(int bulletSpeed, Config.Item weapon, RendererService rendererService) {
        Projectile projectile;
        //setBulletSpeed Parameter noch eintragen.
        getCurrentTank().setBulletSpeed(bulletSpeed);
        projectile = getCurrentTank().shootProjectile(bulletSpeed, weapon);
        Drawable obj = getCollisionObject(rendererService, projectile.getTrajectory());
        if (obj != null) {
            if (!obj.equals(null) && obj.getClass().equals(Tank.class)) {
                LOGGER.log(Level.INFO, "Tank was shot");
                Tank tank = (Tank) obj;
                tank.setHealthAndArmorAfterHit(weapon);
            }
        }
        rendererService.removeLastProjectile();
        rendererService.addLatestProjectile(projectile);
        checkIfTankGotDestroyed();
    }

    /**
     * compares trajectory and with list of canvasObjects and returns common points in a drawable object
     *
     * @param rendererService
     * @param trajectory trajectory off which the collision points need to be calculated
     * @return
     */
    public Drawable getCollisionObject(RendererService rendererService, HashMap<Integer, Point2D> trajectory) {
        ArrayList<Drawable> canvasObjects = rendererService.getCanvasObjects();
        for (int i = 0; i < trajectory.size(); i++) {
            Point2D projectilePoint = new Point2D(trajectory.get(i).getX(), trajectory.get(i).getY());
            if (projectilePoint.getX() >= Config.CANVAS_WIDTH || projectilePoint.getY() >= Config.CANVAS_HEIGHT) {
                LOGGER.info("projectile out of canvas frame");
                return null;
            }

            for (Drawable object: canvasObjects) {
                Set<Point2D> points = object.getCollisionPoints();
                if (object.getClass() != Projectile.class) {
                    if (points.contains(projectilePoint)) {
                        LOGGER.log(Level.INFO, "Object was hit {0}", object.getClass().toString());
                        if (object.getClass() == Tree.class) {
                            Tree tree = (Tree) object;
                            tree.setInactiveAfterCollision(projectilePoint);
                        }
                        if (object.getClass() == House.class) {
                            House house = (House) object;
                            house.setInactiveAfterCollision(projectilePoint);
                        }
                        return object;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Searches for objects that contain points from inside collisionObject and adds them to the returned ArrayList. This list contains the canvas-objects that were hit.
     *
     * @param collisionObject points that will be checked for collision
     * @param canvasObjects objects present on the canvas
     * @return
     */
    public ArrayList<Drawable> getCollidedObjects(Drawable collisionObject, ArrayList<Drawable> canvasObjects) {

        for (Drawable object : canvasObjects) {
            for (Point2D point : object.getCollisionPoints()) {
                if (collisionObject.getCollisionPoints().contains(point)) {
                    collidedObjects.add(object);
                    break;
                }
            }
            break;
        }
        return collidedObjects;
    }
}
