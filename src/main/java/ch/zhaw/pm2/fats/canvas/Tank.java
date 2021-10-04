package ch.zhaw.pm2.fats.canvas;

import ch.zhaw.pm2.fats.App;
import ch.zhaw.pm2.fats.Config;
import ch.zhaw.pm2.fats.Config.Item;
import ch.zhaw.pm2.fats.Player;
import ch.zhaw.pm2.fats.gui.RendererService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tank is the main entity to represent a {@link Player} on the playing field
 */
public class Tank implements Drawable {
    private final int tank;
    private int bulletSpeed;
    private IntegerProperty health;
    private IntegerProperty armor;
    private IntegerProperty fuel;
    private boolean isDestroyed = false;
    private Point2D position;
    private Point2D cannonPosition;
    private int cannonAngle = 0;

    private static final int TANK_WIDTH = 30;
    private static final int TANK_HEIGHT = 30;
    private static final int CANNON_WIDTH = 5;
    private static final int CANNON_HEIGHT = 25;
    private static final int MAX_DEGREE = 360;
    private boolean movementAllowed = true;
    private Item collectedItem = null;

    private static final Logger LOGGER = Logger.getLogger(App.class.getCanonicalName());

    public Tank(int tank, Point2D position) {
        this.tank = tank;
        this.health = new SimpleIntegerProperty(Config.MAX_PERK_VALUE);
        this.armor = new SimpleIntegerProperty(Config.MAX_PERK_VALUE);
        this.fuel = new SimpleIntegerProperty(Config.MAX_PERK_VALUE);
        this.position = position;
        this.cannonPosition = new Point2D(position.getX(), position.getY() - (TANK_HEIGHT / 2));
    }

    public int getArmor() {
        return armor.get();
    }

    public int getFuel() {
        return fuel.get();
    }

    public int getHealth() {
        return health.get();
    }

    public Item getItem() {
        Item item = collectedItem;
        collectedItem = null;
        return item;
    }

    public Boolean getIsAlive() {
        return isDestroyed;
    }

    public int getCannonAngle() {
        return cannonAngle;
    }

    public IntegerProperty getHealthProperty() {
        return health;
    }

    public IntegerProperty getArmorProperty() {
        return armor;
    }

    public IntegerProperty getFuelProperty() {
        return fuel;
    }

    public void setArmor(int armor) {
        this.armor.setValue(armor);
    }

    public void setHealth(int health) {
        this.health.set(health);
    }

    public void setCannonAngle(int cannonAngle) {
        this.cannonAngle = cannonAngle;
    }

    public void setDestroyed() {
        isDestroyed = true;
    }

    public void setHealthWithInventory(int health) {
        this.health.set(this.health.get() + health);
    }

    public void setArmorWithInventory(int armor) {
        this.armor.set(this.armor.get() + armor);
    }

    public void setFuelWithInventory(int fuel) {
        this.fuel.set(this.fuel.get() + fuel);
    }

    private void setArmorToMinValue() {
        this.armor.set(Config.MIN_PERK_VALUE);
    }

    /**
     * Calculates the armor and health left after tank got hit by a {@link Config.Item}
     * @param weapon the weapon the tank got hit from
     */
    public void setHealthAndArmorAfterHit(Config.Item weapon) {
        //Armor is still intact after hit
        if(armor.get() - weapon.getQuantity() > Config.MIN_PERK_VALUE) {
            setArmor(calculateArmorAfterHit(weapon));
            //Armor is already destroyed, but tank doesn't get destroyed
        } else if((armor.get() == Config.MIN_PERK_VALUE) && (health.get() - weapon.getQuantity() > Config.MIN_PERK_VALUE)) {
            setHealth(calculateHealthAfterHitWithNoArmorLeft(weapon));
            //Armor is destroyed and tank gets destroyed with hit
        } else if((armor.get() == Config.MIN_PERK_VALUE) && (health.get() - weapon.getQuantity() <= Config.MIN_PERK_VALUE)) {
            setHealth(calculateHealthAfterHitWithNoArmorLeft(weapon));
            setDestroyed();
            LOGGER.log(Level.INFO, "Tank " + tank + " is destroyed");
            //Armor gets destroyed with hit
        }else if((armor.get() - weapon.getQuantity()) <= Config.MIN_PERK_VALUE) {
            setHealth(calculateHealthWithBreakingArmor(weapon));
            setArmorToMinValue();
        }
    }

    private int calculateArmorAfterHit(Config.Item weapon) {
        armor.set(armor.get() - weapon.getQuantity());
        return armor.get();
    }

    private int calculateHealthAfterHitWithNoArmorLeft(Config.Item weapon) {
        health.set(health.get() - weapon.getQuantity());
        if(health.get() < 0) {
            health.set(Config.MIN_PERK_VALUE);
        }
        return health.get();
    }

    private int calculateHealthWithBreakingArmor(Config.Item weapon) {
        int remainingHitPower;
        remainingHitPower = weapon.getQuantity() - armor.get();
        health.set(health.get() - remainingHitPower);
        return health.get();
    }

    /**
     * Creates a new projectile with his trajectory
     * @param bulletSpeed
     * @param weapon
     * @return
     */
    public Projectile shootProjectile(int bulletSpeed, Config.Item weapon) {
        Projectile projectile = new Projectile(cannonAngle, bulletSpeed, getShootPosition(), weapon);
        return projectile;
    }

    public void setBulletSpeed(int value) {
        bulletSpeed = value;
    }

    @Override
    public Set<Point2D> getCollisionPoints() {
        Point2D startPoint = new Point2D(position.getX() - (TANK_WIDTH / 2), position.getY() - TANK_HEIGHT);
        Point2D endPoint = new Point2D(startPoint.getX() + TANK_WIDTH, startPoint.getY() + TANK_HEIGHT);
        return Drawable.getPointsFromRectangle(startPoint, endPoint);
    }

    /**
     * Draws the tank in the playing window
     * @param graphics element needed to be able to draw
     */
    @Override
    public void drawMe(GraphicsContext graphics) {
        graphics.setFill(Color.GRAY);
        graphics.fillRect(position.getX() - (TANK_WIDTH / 2), position.getY() - TANK_HEIGHT, TANK_WIDTH, TANK_HEIGHT);
        graphics.setStroke(Color.DARKGRAY);
        graphics.setLineWidth(CANNON_WIDTH);
        Point2D shootPosition = getShootPosition();
        graphics.strokeLine(cannonPosition.getX(), cannonPosition.getY(), shootPosition.getX(), shootPosition.getY());
    }

    private Point2D getShootPosition() {
        //rotate vector: https://de.wikipedia.org/wiki/Drehmatrix
        Point2D baseCannonShootPosition = new Point2D(cannonPosition.getX() + CANNON_HEIGHT, cannonPosition.getY());
        //AB = B - A
        Point2D vectorA = new Point2D(baseCannonShootPosition.getX() - cannonPosition.getX(), baseCannonShootPosition.getY() - cannonPosition.getY());
        double x = Math.round(vectorA.getX() * Math.cos(Math.toRadians(MAX_DEGREE - cannonAngle)) - vectorA.getY() * Math.sin(Math.toRadians(MAX_DEGREE - cannonAngle)));
        double y = Math.round(vectorA.getX() * Math.sin(Math.toRadians(MAX_DEGREE - cannonAngle)) + vectorA.getY() * Math.cos(Math.toRadians(MAX_DEGREE - cannonAngle)));
        //B = AB + A
        return new Point2D(x + cannonPosition.getX(), y + cannonPosition.getY());
    }

    public void setMovementStatus(Boolean status) {
        movementAllowed = status;
    }


    public enum MovementDirection {
        RIGHT(new Point2D(1, 0)), LEFT(new Point2D(-1, 0));

        private final Point2D point;

        MovementDirection(Point2D point) {
            this.point = point;
        }

        public double getX() {
            return point.getX();
        }

        public double getY() {
            return point.getY();
        }
    }

    /**
     * Moves tank to the left or to the right
     * Movement depends on:
     * amount of fuel
     * blocking collision with another object
     * canvas frame width
     * @param relativeVector vector describing the direction the tank should move
     * @param rendererService
     * @param consumption fuel consumption for present ground type
     */
    public void move(MovementDirection relativeVector, RendererService rendererService, int consumption) {
        checkAndModifyTankMovementStatus(relativeVector, rendererService);

        if (fuel.get() > 0 && movementAllowed) {
            position = new Point2D(position.getX() + relativeVector.getX(), position.getY() + relativeVector.getY());
            cannonPosition = new Point2D(position.getX(), position.getY() - (TANK_HEIGHT / 2));
            fuel.set(fuel.get() - consumption);
        }
    }


    private boolean checkTankForCollision(RendererService rendererService, MovementDirection relativeVector) {
        ArrayList<Drawable> allCanvasObjectPoints = rendererService.getCanvasObjectsWithoutTankGroundAndProjectile();
        Point2D collisionPointLeft = new Point2D(position.getX() - (TANK_WIDTH / 2 + 1) , position.getY() - (TANK_HEIGHT / 2));
        Point2D collisionPointRight = new Point2D(position.getX() + (TANK_WIDTH / 2), position.getY() - (TANK_HEIGHT / 2));
        Point2D relevantCollisionPoint = (relativeVector == MovementDirection.RIGHT)? collisionPointRight : collisionPointLeft;
        //check border
        if (relevantCollisionPoint.getX() < 0 || relevantCollisionPoint.getX() > Config.CANVAS_WIDTH) {return true;}
        //check other canvas object
        for (Drawable object : allCanvasObjectPoints) {
            if (object.getCollisionPoints().contains(relevantCollisionPoint) ||
                    (object.getCollisionPoints().contains(new Point2D(relevantCollisionPoint.getX(), relevantCollisionPoint.getY() - (TANK_HEIGHT / 2 - 5)))) ||
                    (object.getCollisionPoints().contains(new Point2D(relevantCollisionPoint.getX(), relevantCollisionPoint.getY() + (TANK_HEIGHT / 2 - 5))))) {
                if (object.getClass() == Chest.class) {
                    Chest chest = (Chest) object;
                    rendererService.removeChest();
                    collectedItem = chest.getItem();
                }
                return true;
            }
        }
        return false;
    }


    private void checkAndModifyTankMovementStatus(MovementDirection relativeVector, RendererService rendererService) {
        if (checkTankForCollision(rendererService, relativeVector)) {
            setMovementStatus(false);
            //to prevent getting stuck
            if (relativeVector == MovementDirection.RIGHT) {
                //push one pixel to the left
                position = new Point2D(position.getX() - relativeVector.getX(), position.getY() - relativeVector.getY());
                cannonPosition = new Point2D(position.getX() - relativeVector.getX(), position.getY() - (TANK_HEIGHT / 2));
            } else {
                //push one pixel to the right
                position = new Point2D(position.getX() - relativeVector.getX(), position.getY() - relativeVector.getY());
                cannonPosition = new Point2D(position.getX() - relativeVector.getX(), position.getY() - (TANK_HEIGHT / 2));
            }
        } else {
            setMovementStatus(true);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tank)) return false;
        Tank tank = (Tank) o;
        return getHealth() == tank.getHealth() &&
                getArmor() == tank.getArmor() &&
                getFuel() == tank.getFuel() &&
                isDestroyed == tank.isDestroyed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHealth(), getArmor(), getFuel(), isDestroyed);
    }

    @Override
    public String toString() {
        return "Tank{" +
                "health=" + health +
                ", armor=" + armor +
                ", fuel=" + fuel +
                ", isAlive=" + isDestroyed +
                '}';
    }
}
