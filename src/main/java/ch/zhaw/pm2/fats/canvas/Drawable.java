package ch.zhaw.pm2.fats.canvas;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import java.util.HashSet;
import java.util.Set;

/**
 * All visible objects which are capable of colliding
 */
public interface Drawable {

    /**
     * Gets all Points inside a canvas object
     * @return Set<Point2D>
     */
    Set<Point2D> getCollisionPoints();

    /**
     * Draws the shape with GraphicsContext on canvas
     * @param graphics
     */
    void drawMe(GraphicsContext graphics);

    /**
     * Calculates all points inside a rectangle
     * @param startPoint point left top
     * @param endPoint point right bottom
     * @return Set<Point2D>
     */
    static Set<Point2D> getPointsFromRectangle(Point2D startPoint, Point2D endPoint) {
        Set<Point2D> points = new HashSet<>();
        int startX = (int) startPoint.getX();
        int startY = (int) startPoint.getY();
        int endX = (int) endPoint.getX();
        int endY = (int) endPoint.getY();
        while (startX < endX) {
            while (startY < endY) {
                points.add(new Point2D(startX, startY));
                startY++;
            }
            startY = (int) startPoint.getY();
            startX++;
        }
        return points;
    }
}
