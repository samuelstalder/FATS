package ch.zhaw.pm2.fats.canvas;

import ch.zhaw.pm2.fats.canvas.soil.Earth;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GroundTest {

    Ground ground;

    @BeforeEach
    void setUp() {
        ArrayList<Point2D> groundPoints = new ArrayList<>();
        groundPoints.add(new Point2D(0, 400));
        groundPoints.add(new Point2D(640, 400));
        ground = new Earth(groundPoints);
    }

    @Test
    void testGetCollisionPoints() {
        Set<Point2D> points = ground.getCollisionPoints();
        assertEquals(1920, points.size());
        assertTrue(points.contains(new Point2D(402, 402)));
        assertFalse(points.contains(new Point2D(111, 111)));
    }


}
