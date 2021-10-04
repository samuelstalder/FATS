package ch.zhaw.pm2.fats.canvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import java.util.HashSet;
import java.util.Set;

/**
 * It's a drawable canvas object.
 * It contains 4 root squares and 2 crown squares. Parts disappear after being shot.
 */
public class Tree implements Drawable {

    private int id;
    private Point2D position;
    private static final int ROOT_WIDTH = 12;
    private static final int ROOT_HEIGHT = 12;
    private static final int CROWN_WIDTH = 30;
    private static final int CROWN_HEIGHT = 40;

    private boolean isRootBottomActive = true;
    private boolean isRootMiddleActive = true;
    private boolean isRootTopActive = true;
    private boolean isCrownRightActive = true;
    private boolean isCrownLeftActive = true;

    /**
     * Constructor for a new house on a fix position
     * @param position should be on ground level
     * @param id if you use more then one tree in one game
     */
    public Tree (Point2D position, int id) {
        this.position = position;
        this.id = id;
    }

    /**
     *
     * @param graphics
     */
    public void drawMe(GraphicsContext graphics) {
        graphics.setFill(Color.BROWN);
        if (isRootBottomActive) {
            graphics.fillRect(position.getX() - (ROOT_WIDTH / 2), position.getY() - (1 * ROOT_HEIGHT), ROOT_WIDTH, ROOT_HEIGHT);
        }
        if (isRootMiddleActive) {
            graphics.fillRect(position.getX() - (ROOT_WIDTH / 2), position.getY() - (2 * ROOT_HEIGHT), ROOT_WIDTH, ROOT_HEIGHT);
        }
        if (isRootTopActive) {
            graphics.fillRect(position.getX() - (ROOT_WIDTH / 2), position.getY() - (3 * ROOT_HEIGHT), ROOT_WIDTH, ROOT_HEIGHT);
        }
        graphics.setFill(Color.LIGHTGREEN);
        if (isCrownLeftActive) {
            graphics.fillRect(position.getX() - (1*CROWN_WIDTH), position.getY() - (3*ROOT_HEIGHT) - CROWN_HEIGHT, CROWN_WIDTH, CROWN_HEIGHT);
        }
        if (isCrownRightActive) {
            graphics.fillRect(position.getX(), position.getY() - (3*ROOT_HEIGHT) - CROWN_HEIGHT, CROWN_WIDTH, CROWN_HEIGHT);
        }
    }

    @Override
    public Set<Point2D> getCollisionPoints() {
        Set<Point2D> points = new HashSet<>();
        if (isRootBottomActive) { points.addAll(getRootBottomPoints()); }
        if (isRootMiddleActive) { points.addAll(getRootMiddlePoints()); }
        if (isRootTopActive) { points.addAll(getRootTopPoints()); }
        if (isCrownLeftActive) { points.addAll(getCrownLeftPoints()); }
        if (isCrownRightActive) { points.addAll(getCrownRightPoints()); }
        return points;
    }

    private Set<Point2D> getRootBottomPoints() {
        Point2D startPoint = new Point2D(position.getX() - (ROOT_WIDTH / 2), position.getY() - (1 * ROOT_HEIGHT));
        Point2D endPoint = new Point2D(startPoint.getX() + ROOT_WIDTH, startPoint.getY() + ROOT_HEIGHT);
        return Drawable.getPointsFromRectangle(startPoint, endPoint);
    }

    private Set<Point2D> getRootMiddlePoints() {
        Point2D startPoint = new Point2D(position.getX() - (ROOT_WIDTH / 2), position.getY() - (2 * ROOT_HEIGHT));
        Point2D endPoint = new Point2D(startPoint.getX() + ROOT_WIDTH, startPoint.getY() + ROOT_HEIGHT);
        return Drawable.getPointsFromRectangle(startPoint, endPoint);
    }

    private Set<Point2D> getRootTopPoints() {
        Point2D startPoint = new Point2D(position.getX() - (ROOT_WIDTH / 2), position.getY() - (3 * ROOT_HEIGHT));
        Point2D endPoint = new Point2D(startPoint.getX() + ROOT_WIDTH, startPoint.getY() + ROOT_HEIGHT);
        return Drawable.getPointsFromRectangle(startPoint, endPoint);
    }

    private Set<Point2D> getCrownLeftPoints() {
        Point2D startPoint = new Point2D(position.getX() - (1 * CROWN_WIDTH), position.getY() - (3 * ROOT_HEIGHT) - CROWN_HEIGHT);
        Point2D endPoint = new Point2D(startPoint.getX() + CROWN_WIDTH, startPoint.getY() + CROWN_HEIGHT);
        return Drawable.getPointsFromRectangle(startPoint, endPoint);
    }

    private Set<Point2D> getCrownRightPoints() {
        Point2D startPoint = new Point2D(position.getX(), position.getY() - (3 * ROOT_HEIGHT) - CROWN_HEIGHT);
        Point2D endPoint = new Point2D(startPoint.getX() + CROWN_WIDTH, startPoint.getY() + CROWN_HEIGHT);
        return Drawable.getPointsFromRectangle(startPoint, endPoint);
    }

    /**
     * When projectile collides with a tree a certain part of the tree disappears
     * It's done to adjust the environment
     * @param point first point of collision
     */
    public void setInactiveAfterCollision(Point2D point) {
        if (getRootBottomPoints().contains(point)) { isRootBottomActive = false; }
        if (getRootMiddlePoints().contains(point)) { isRootMiddleActive = false; }
        if (getRootTopPoints().contains(point)) { isRootTopActive = false; }
        if (getCrownLeftPoints().contains(point)) { isCrownLeftActive = false; }
        if (getCrownRightPoints().contains(point)) { isCrownRightActive = false; }
    }

}
