package ch.zhaw.pm2.fats.canvas;

import ch.zhaw.pm2.fats.Config.Item;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Set;

/**
 * Its a collectable canvas object which contains a random item
 */
public class Chest implements Drawable{

    private int id;
    private Point2D position;
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;
    private Item item;

    /**
     * The constructor of chest, it will use a random item from the item enum.
     *
     * @param position The position vector.
     * @param id if you use more then one tree in one game
     */
    public Chest (Point2D position, int id) {
        this.position = position;
        this.id = id;
        this.item = Item.getRandomItem();
    }

    public Item getItem() {
        return this.item;
    }

    @Override
    public Set<Point2D> getCollisionPoints() {
        Point2D startPoint = new Point2D(position.getX() - (WIDTH/2), position.getY() - HEIGHT);
        Point2D endPoint = new Point2D(startPoint.getX() + WIDTH, startPoint.getY() + HEIGHT);
        return Drawable.getPointsFromRectangle(startPoint, endPoint);
    }

    @Override
    public void drawMe(GraphicsContext graphics) {
        graphics.setFill(Color.GOLD);
        graphics.fillRect(position.getX() - (WIDTH/2), position.getY() - HEIGHT, WIDTH, HEIGHT);
    }

}
