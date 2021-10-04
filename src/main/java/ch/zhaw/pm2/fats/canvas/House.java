package ch.zhaw.pm2.fats.canvas;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.HashSet;
import java.util.Set;

/**
 * It's a drawable canvas object.
 * It contains 4 wall squares and 2 roof triangles. Parts disappear after being shot.
 */
public class House implements Drawable {

    private int id;
    private Point2D position;
    private static final int WALL_WIDTH = 50;
    private static final int WALL_HEIGHT = 50;
    private static final int ROOF_WIDTH = 75;
    private static final int ROOF_HEIGHT = 75;

    private boolean isWallBottomRightActive = true;
    private boolean isWallBottomLeftActive = true;
    private boolean isWallTopRightActive = true;
    private boolean isWallTopLeftActive = true;
    private boolean isRoofRightActive = true;
    private boolean isRoofLeftActive = true;

    /**
     * Constructor for a new house on a fix position
     * @param position should be on ground level
     * @param id if you use more then one house in one game
     */
    public House(Point2D position, int id) {
        this.position = position;
        this.id = id;
    }

    @Override
    public Set<Point2D> getCollisionPoints() {
        Set<Point2D> points = new HashSet<>();
        if (isWallBottomRightActive) { points.addAll(getWallBottomRight()); }
        if (isWallBottomLeftActive) { points.addAll(getWallBottomLeft()); }
        if (isWallTopRightActive) { points.addAll(getWallTopRight()); }
        if (isWallTopLeftActive) { points.addAll(getWallTopLeft()); }
        if (isRoofLeftActive) { points.addAll(getPointsFromLeftRoof()); }
        if (isRoofRightActive) { points.addAll(getPointsFromRightRoof()); }
        return points;
    }

    private Set<Point2D> getWallBottomRight() {
        Point2D startPoint = new Point2D(position.getX(), position.getY() - WALL_HEIGHT);
        Point2D endPoint = new Point2D(startPoint.getX() + WALL_WIDTH, startPoint.getY() + WALL_HEIGHT);
        return Drawable.getPointsFromRectangle(startPoint, endPoint);
    }

    private Set<Point2D> getWallBottomLeft() {
        Point2D startPoint = new Point2D(position.getX() - WALL_WIDTH, position.getY() - WALL_HEIGHT);
        Point2D endPoint = new Point2D(startPoint.getX() + WALL_WIDTH, startPoint.getY() + WALL_HEIGHT);
        return Drawable.getPointsFromRectangle(startPoint, endPoint);
    }

    private Set<Point2D> getWallTopRight() {
        Point2D startPoint = new Point2D(position.getX(), position.getY() - (2*WALL_HEIGHT));
        Point2D endPoint = new Point2D(startPoint.getX() + WALL_WIDTH, startPoint.getY() + WALL_HEIGHT);
        return Drawable.getPointsFromRectangle(startPoint, endPoint);
    }

    private Set<Point2D> getWallTopLeft() {
        Point2D startPoint = new Point2D(position.getX() - WALL_WIDTH, position.getY() - (2*WALL_HEIGHT));
        Point2D endPoint = new Point2D(startPoint.getX() + WALL_WIDTH, startPoint.getY() + WALL_HEIGHT);
        return Drawable.getPointsFromRectangle(startPoint, endPoint);
    }

    /**
     * When projectile collides with a house a certain part of the house disappears
     * It's done to adjust the environment
     * @param point first point of collision
     */
    public void setInactiveAfterCollision(Point2D point) {
        if (getWallBottomRight().contains(point)) { isWallBottomRightActive = false; }
        if (getWallBottomLeft().contains(point)) { isWallBottomLeftActive = false; }
        if (getWallTopRight().contains(point)) { isWallTopRightActive = false; }
        if (getWallTopLeft().contains(point)) { isWallTopLeftActive = false; }
        if (getPointsFromLeftRoof().contains(point)) { isRoofLeftActive = false; }
        if (getPointsFromRightRoof().contains(point)) { isRoofRightActive = false; }
    }

    /**
     * Draws 4 wall squares and 2 roof triangles.
     * The drawing depends on one Point: position
     * position is always a ground point -> It should be on the same level as the ground
     * @param graphics
     */
    @Override
    public void drawMe(GraphicsContext graphics) {
        graphics.setFill(Color.LIGHTYELLOW);
        if (isWallBottomRightActive) {
            graphics.fillRect(position.getX(), position.getY() - WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT);
        }
        if (isWallBottomLeftActive) {
            graphics.fillRect(position.getX() - WALL_WIDTH, position.getY() - WALL_HEIGHT, WALL_WIDTH, WALL_HEIGHT);
        }
        if (isWallTopRightActive) {
            graphics.fillRect(position.getX(), position.getY() - (2*WALL_HEIGHT), WALL_WIDTH, WALL_HEIGHT);
        }
        if (isWallTopLeftActive) {
            graphics.fillRect(position.getX() - WALL_WIDTH, position.getY() - (2*WALL_HEIGHT), WALL_WIDTH, WALL_HEIGHT);
        }
        graphics.setFill(Color.DARKORANGE);
        if (isRoofRightActive) {
            graphics.fillPolygon(new double[]{position.getX(), position.getX(), position.getX() + ROOF_WIDTH},
                    new double[]{position.getY() - (2*WALL_HEIGHT), position.getY() - (2*WALL_HEIGHT) - ROOF_HEIGHT, position.getY() - (2*WALL_HEIGHT)}, 3);
        }
        if (isRoofLeftActive) {
            graphics.fillPolygon(new double[]{position.getX(), position.getX(), position.getX() - ROOF_WIDTH},
                    new double[]{position.getY() - (2 * WALL_HEIGHT), position.getY() - (2 * WALL_HEIGHT) - ROOF_HEIGHT, position.getY() - (2 * WALL_HEIGHT)}, 3);
        }
    }

    protected Set<Point2D> getPointsFromLeftRoof() {
        Set<Point2D> points = new HashSet<>();
        Point2D pointOne = new Point2D(position.getX(), position.getY() - (2*WALL_HEIGHT));
        Point2D pointTwo = new Point2D(position.getX(), position.getY() - (2*WALL_HEIGHT) - ROOF_HEIGHT);
        Point2D pointThree = new Point2D(position.getX() - ROOF_WIDTH,position.getY() - (2*WALL_HEIGHT));
        int counter = 1;
        for (int i = (int) pointTwo.getY(); i < pointOne.getY(); i++) {
            for (int j = (int) pointOne.getX(); j > pointOne.getX() - counter; j--) {
                points.add(new Point2D(j, i));
            }
            counter++;
        }
        return points;
    }

    protected Set<Point2D> getPointsFromRightRoof() {
        Set<Point2D> points = new HashSet<>();
        Point2D pointOne = new Point2D(position.getX(), position.getY() - (2*WALL_HEIGHT));
        Point2D pointTwo = new Point2D(position.getX(), position.getY() - (2*WALL_HEIGHT) - ROOF_HEIGHT);
        Point2D pointThree = new Point2D(position.getX() - ROOF_WIDTH,position.getY() - (2*WALL_HEIGHT));
        int counter = 1;
        for (int i = (int) pointTwo.getY(); i < pointOne.getY(); i++) {
            for (int j = (int) pointOne.getX(); j < pointOne.getX() + counter; j++) {
                points.add(new Point2D(j, i));
            }
            counter++;
        }
        return points;
    }
}
