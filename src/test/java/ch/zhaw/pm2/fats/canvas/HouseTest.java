package ch.zhaw.pm2.fats.canvas;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class HouseTest {

    House house;

    @BeforeEach
    void setUp() {
        house = new House(new Point2D(100, 400), 1);
    }

    @Test
    void testGetCollisionPoints() {
        Set<Point2D> points = house.getCollisionPoints();
        assertEquals(15625, points.size());
        assertFalse(points.contains(new Point2D(100 + 55, 400 - 5)));
        assertTrue(points.contains(new Point2D(100 + 5, 400 - 5)));
    }

    @Test
    void testGetPointsFromLeftRoof() {
        Set<Point2D> points = house.getPointsFromLeftRoof();
        assertEquals(2850, points.size());
    }

    @Test
    void estgetPointsFromRightRoof() {
        Set<Point2D> points = house.getPointsFromRightRoof();
        assertEquals(2850, points.size());
    }

}
