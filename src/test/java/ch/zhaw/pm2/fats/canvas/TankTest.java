package ch.zhaw.pm2.fats.canvas;

import ch.zhaw.pm2.fats.Config;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TankTest {
    private Tank testTank;

    @BeforeEach
    void setUp() {
        final int tankNumberOne = 1;
        final int width = 100;
        final int height = 400;
        Point2D tankPosition = new Point2D(width, height);
        testTank = new Tank(tankNumberOne, tankPosition);
    }

    @Test
    void testGetCollisionPoints() {

        Set<Point2D> points = testTank.getCollisionPoints();

        assertEquals(900, points.size());
        assertFalse(points.contains(new Point2D(100 + 20, 400 - 5)));
        assertTrue(points.contains(new Point2D(100 + 5, 400 - 5)));
    }

    @Test
    void testArmorStillIntactAfterHit() {
        int expectedArmor = Config.MAX_PERK_VALUE - Config.Item.STANDARD.getQuantity();

        testTank.setHealthAndArmorAfterHit(Config.Item.STANDARD);

        assertEquals(expectedArmor, testTank.getArmor());
    }

    @Test
    void testArmorGetsDestroyedWithHit() {
        int initialArmor = 20;
        int expectedArmor = 0;
        int expectedHealth = Config.MAX_PERK_VALUE - Config.Item.STANDARD.getQuantity();
        testTank.setArmor(initialArmor);

        testTank.setHealthAndArmorAfterHit(Config.Item.CHEMICAL);

        assertEquals(expectedArmor, testTank.getArmor());
        assertEquals(expectedHealth, testTank.getHealth());
    }

    @Test
    void testArmorIsAlreadyDestroyedBeforeHit() {
        int expectedHealth = Config.MAX_PERK_VALUE - Config.Item.STANDARD.getQuantity();
        testTank.setArmor(Config.MIN_PERK_VALUE);

        testTank.setHealthAndArmorAfterHit(Config.Item.STANDARD);

        assertEquals(expectedHealth, testTank.getHealth());
    }

    @Test
    void testTankGetsDestroyedWithHit() {
        int expectedHealth = Config.MIN_PERK_VALUE;
        int initialHealth = 20;
        boolean testTankIsDestroyed = true;
        testTank.setArmor(Config.MIN_PERK_VALUE);
        testTank.setHealth(initialHealth);

        testTank.setHealthAndArmorAfterHit(Config.Item.CHEMICAL);

        assertEquals(expectedHealth, testTank.getHealth());
        assertEquals(testTankIsDestroyed, testTank.getIsAlive());
    }

    @Test
    void testTankGetsBarelyNotDestroyedWithHit() {
        int expectedHealth = 1;
        int initialHealth = 31;
        boolean testTankIsDestroyed = false;
        testTank.setArmor(Config.MIN_PERK_VALUE);
        testTank.setHealth(initialHealth);

        testTank.setHealthAndArmorAfterHit(Config.Item.CHEMICAL);

        assertEquals(expectedHealth, testTank.getHealth());
        assertEquals(testTankIsDestroyed, testTank.getIsAlive());
    }

    @Test
    void testArmorGetsExactlyDestroyed() {
        int expectedArmor = 0;
        int expectedHealth = Config.MAX_PERK_VALUE;
        int initialArmor = 10;
        testTank.setArmor(initialArmor);

        testTank.setHealthAndArmorAfterHit(Config.Item.STANDARD);

        assertEquals(expectedArmor, testTank.getArmor());
        assertEquals(expectedHealth, testTank.getHealth());
    }

}
