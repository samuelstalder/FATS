package ch.zhaw.pm2.fats.canvas;

import ch.zhaw.pm2.fats.Config;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Its a flat ground based on two points.
 * Its only 3 pixel thick but it is drawn with 80 pixel
 */
public abstract class Ground implements Drawable{
    private ArrayList<Point2D> points;
    protected static final int AMOUNT_OF_STONES = 100;

    public Ground(ArrayList<Point2D> points) {
        this.points = points;
    }

    public abstract void drawMe(GraphicsContext graphics);

    /**
     * Gets ground points
     * For optimization reason the ground is only 3 pixel thick
     * @return Set<Point2D>
     */
    @Override
    public Set<Point2D> getCollisionPoints() {
        Point2D startPoint = new Point2D(points.get(0).getX(), points.get(0).getY());
        Point2D endPoint = new Point2D(Config.CANVAS_WIDTH, Config.GROUND_LEVEL + 3);
        return Drawable.getPointsFromRectangle(startPoint, endPoint);
    }



    protected List<Point2D> getPoints() {
        return points;
    }
}
