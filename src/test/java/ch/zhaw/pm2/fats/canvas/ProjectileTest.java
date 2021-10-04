package ch.zhaw.pm2.fats.canvas;

import ch.zhaw.pm2.fats.Config;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class ProjectileTest {
    private Projectile projectile;
    private HashMap<Integer, Point2D> trajectory;
    private int angle;
    private int speed;

    @BeforeEach
    void setUp() {
        angle = 45;
        speed = 200;
        projectile = new Projectile(angle, speed, new Point2D(0, 400), Config.Item.STANDARD);
        projectile.setGravitation(Projectile.Gravitation.EARTH);
        trajectory = new HashMap<>();
    }


    @Test
    void testAngleConversionAndSetter() {
        assertEquals(projectile.getAngle(), Math.toRadians(angle));
    }


    @Test
    void testRedundancyRemover() {
        Point2D point1 = new Point2D(1.0, 1.0);
        Point2D point2 = new Point2D(1.0, 1.0);
        Point2D point3 = new Point2D(2.0, 2.0);
        Point2D point4 = new Point2D(3.0, 3.0);
        ArrayList<Point2D> points = new ArrayList<>();
        points.add(point1);
        points.add(point2);
        points.add(point3);
        points.add(point4);
        assert (countOccurence(points, point1) == 2);
        points = projectile.removeRedundantPoints(points);
        assertEquals(points.size(), 3);
        assert (countOccurence(points, point1) == 1);
    }

    @Test
    void testBresenham() {
        Point2D point1 = new Point2D(1.0, 1.0);
        Point2D point2 = new Point2D(3.0, 3.0);
        ArrayList<Point2D> points;
        int expectedNumberOfPoints = (int) point2.getX();
        points = projectile.getPointsBetweenTwoPoints((int) point1.getX(), (int) point1.getY(), (int) point2.getX(), (int) point2.getY());
        assertNotEquals(points.size(), 2);
        assertEquals(points.size(), expectedNumberOfPoints);
    }

    @Test
    void testSophisticatedTrajectoryGeneration() {
        projectile.generateFullTrajectoryFromBasePoints();
        assertNotEquals(projectile.getTrajectory().size(), projectile.getSimpleTrajectory().size());
    }

    @Test
    void testBasePointCalculation() {
        int expectedSize = Projectile.MAX_TRAJECTORY_POINTS + 1;
        projectile.calculateTrajectoryBasePoints();
        trajectory = projectile.getSimpleTrajectory();
        assertEquals(trajectory.size(), expectedSize);
    }

    int countOccurence(ArrayList<Point2D> points, Point2D pointToCheck) {

        int pointOccurence = 0;
        for (Point2D point : points) {
            if (point.equals(pointToCheck)) {
                pointOccurence++;
            }
        }
        return pointOccurence;
    }
}


