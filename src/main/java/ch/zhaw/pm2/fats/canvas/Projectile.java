package ch.zhaw.pm2.fats.canvas;

import ch.zhaw.pm2.fats.Config;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * The projectile class describes the projectile shot by the tank. The class can calculate a simple shooting trajectory and a more sophisticated one (every x-value gets a y-value).
 */
public class Projectile implements Drawable {

    public final static int MAX_TRAJECTORY_POINTS = 21;

    private Config.Item projectileType = Config.Item.STANDARD; //immer ganzzahlen
    private double angle;
    private Point2D initialPosition; //shooting position
    private Point2D wind; //vector of wind speed
    private double speed; //projectile speed
    private Gravitation gravitation = Gravitation.EARTH; //gravitation in direction of positive y axis of canvas
    private final HashMap<Integer, Point2D> trajectory;
    private final HashMap<Integer, Point2D> simpleTrajectory;
    private static final int ABOVE_HOUSE_LEVEL = 220;

    /**
     * Constructor for a new projectile, with a given start position, speed, angle and type.
     * @param angle given shooting angle
     * @param speed speed of the projectile
     * @param initialPosition point from where the trajetory is calculated
     * @param weaponType type of the generated project
     */
    public Projectile(int angle, int speed, Point2D initialPosition, Config.Item weaponType) {
        this.angle = Math.toRadians(angle);
        this.speed = speed;
        this.initialPosition = initialPosition;
        this.projectileType = weaponType;
        simpleTrajectory = new HashMap<>();
        trajectory = new HashMap<>();
        wind = new Point2D(0, 0);
        calculateTrajectoryBasePoints();
        generateFullTrajectoryFromBasePoints();
    }

    /**
     * Getter for a more sophisticated trajectory with a y-value for every x-value.
     * @return simpleTrajectory
     */
    public HashMap<Integer, Point2D> getTrajectory() {
        return trajectory;
    }

    public double getAngle() {
        return angle;
    }

    /**
     * Getter for a simple trajectory with less points.
     * @return simpleTrajectory
     */
    public HashMap<Integer, Point2D> getSimpleTrajectory() {
        return simpleTrajectory;
    }

    @Override
    public Set<Point2D> getCollisionPoints() {
        return null;
    }

    /**
     * Draws a the shooting trajectory. The drawing depends on the simple trajectory. wall squares and 2 roof triangles.
     * The drawing depends on one Point: position
     * position is always a ground point -> It should be on the same level as the ground
     * @param graphics
     */
    @Override
    public void drawMe(GraphicsContext graphics) {
        graphics.setStroke(Color.BLACK);
        graphics.setLineWidth(1);
        graphics.beginPath();
        graphics.moveTo(simpleTrajectory.get(0).getX(), simpleTrajectory.get(0).getY());
        for (int i = 1; i < simpleTrajectory.size(); i++) {
            graphics.lineTo(simpleTrajectory.get(i).getX(), simpleTrajectory.get(i).getY());
        }
        graphics.stroke();
    }

    /**
     * Enum class for different gravitations.
     */
    public enum Gravitation {
        EARTH(10), MOON(2);

        private int g;

        Gravitation(Integer g) {
            this.g = g;
        }

        public int getValue() {
            return g;
        }
    }

    public void setGravitation(Gravitation gravitation) {
        this.gravitation = gravitation;
    }

    /**
     * Calculates base points of the shooting trajectory. The amount of points generated depends on the projectile speed that is chosen. This is because the time intervals are constant.
     * Formula: see trajectory-formula.jpg in Documents folder on github
     */
    void calculateTrajectoryBasePoints() {
        double x;
        double y;
        int t;

        for (t = 0; t <= MAX_TRAJECTORY_POINTS; t++) {

            x = (((speed * Math.cos(angle)) - wind.getX()) * t) + initialPosition.getX();
            y = -1 * ((-.5 * gravitation.getValue() * t * t) + (((speed * Math.sin(angle)) - wind.getY()) * t) - initialPosition.getY());
            if ((int)x < Config.CANVAS_HEIGHT || (int)y < Config.CANVAS_WIDTH) {
                simpleTrajectory.put(t, new Point2D((int) x, (int) y));
            }
        }
    }

    /**
     * Generates a complete trajectory with a y-value for every x-value.
     */
    void generateFullTrajectoryFromBasePoints() {

        ArrayList<Point2D> points = new ArrayList<>();
        int i;
        int j;

        for (i = 0; i < simpleTrajectory.size() - 1; i++) {
            //optimization: there is no need to calculate points above the house
            if (simpleTrajectory.get(i).getY() > ABOVE_HOUSE_LEVEL || simpleTrajectory.get(+1).getY() > ABOVE_HOUSE_LEVEL) {
                points.addAll(getPointsBetweenTwoPoints((int) simpleTrajectory.get(i).getX(), (int) simpleTrajectory.get(i).getY(), (int) simpleTrajectory.get(i + 1).getX(), (int) simpleTrajectory.get(i + 1).getY()));
            } else {
                points.add(new Point2D(simpleTrajectory.get(i).getX(), simpleTrajectory.get(i).getY()));
            }
        }

        points = removeRedundantPoints(points);

        for (j = 0; j < points.size(); j++) {
            trajectory.put(j, points.get(j));
        }
    }

    /**
     * Calculates the points between two given points with the bresenham's line algorithm
     *
     * @param x1 x value of start point
     * @param y1 y value of start point
     * @param x2 x value of end point
     * @param y2 y value of end point
     * @return generated points between the two given points
     */
    ArrayList<Point2D> getPointsBetweenTwoPoints(int x1, int y1, int x2, int y2) {

        ArrayList<Point2D> points = new ArrayList<>();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;

        int error = dx - dy;
        int error2;

        while (true) {
            points.add(new Point2D(x1, y1));

            if (x1 == x2 && y1 == y2)
                break;

            error2 = 2 * error;
            if (error2 > -dy) {
                error = error - dy;
                x1 = x1 + sx;
            }

            if (error2 < dx) {
                error = error + dx;
                y1 = y1 + sy;
            }
        }
        return points;
    }

    /**
     * Removes redundant points from a given list.
     * @param list to modify
     * @return modified list without redundancy
     */
    ArrayList<Point2D> removeRedundantPoints(ArrayList<Point2D> list) {
        ArrayList<Point2D> points;
        points = list;
        for (int k = 0; k < points.size() - 1; k++) {
            if (points.get(k).getX() == points.get(k + 1).getX()) {
                points.remove(k + 1);
            }
        }
        return points;
    }

}

