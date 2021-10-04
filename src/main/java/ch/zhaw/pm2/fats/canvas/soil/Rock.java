package ch.zhaw.pm2.fats.canvas.soil;

import ch.zhaw.pm2.fats.Config;
import ch.zhaw.pm2.fats.canvas.Ground;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

/**
 * The ground for the soilType Rock.
 */
public class Rock extends Ground {
    private ArrayList<Point2D> stones;

    /**
     * The main constructor.
     *
     * @param points The list of Points in which a ground can be draw.
     */
    public Rock(ArrayList<Point2D> points) {
        super(points);
        stones = new ArrayList<>();
    }

    /**
     * The draw method for the Rock ground.
     *
     * @param graphics The graphics element.
     */
    public void drawMe(GraphicsContext graphics) {
        graphics.setStroke(Color.web("#8B8D7A"));
        graphics.beginPath();
        graphics.moveTo(getPoints().get(0).getX(), getPoints().get(0).getY());
        for (int i = 1; i < getPoints().size(); i++) {
            graphics.lineTo(getPoints().get(i).getX(), getPoints().get(i).getY());
        }
        graphics.stroke();
        if(stones.size() < 1)
            createFloor(graphics);
        else
            drawFloor(graphics);
    }

    /**
     * Create the floor for the rock soil type and store it in a ArrayList.
     *
     * @param graphics The graphics element.
     */
    public void createFloor(GraphicsContext graphics) {
        graphics.setFill(Color.LIGHTGRAY);
        graphics.fillRect(getPoints().get(0).getX(), getPoints().get(0).getY(), getPoints().get(1).getX(), Config.CANVAS_HEIGHT - getPoints().get(0).getY());
        graphics.fill();

        // create 500 stones
        for (int countRandomNumber = 0; countRandomNumber < AMOUNT_OF_STONES; countRandomNumber++) {
            int randomNumberY = new Random().nextInt(Config.CANVAS_HEIGHT - Config.GROUND_LEVEL);
            int randomNumberX = new Random().nextInt(Config.CANVAS_WIDTH);

            stones.add(new Point2D(randomNumberX, Config.GROUND_LEVEL + randomNumberY));
            graphics.setFill(Color.DARKGRAY);
            graphics.fillOval(randomNumberX, Config.GROUND_LEVEL + randomNumberY, 5, 5);
            graphics.fill();
        }
    }

    /**
     * Draw the floor for the rock soil type.
     *
     * @param graphics The graphics element.
     */
    public void drawFloor(GraphicsContext graphics) {
        graphics.setFill(Color.LIGHTGREY);
        graphics.fillRect(getPoints().get(0).getX(), getPoints().get(0).getY(), getPoints().get(1).getX(), Config.CANVAS_HEIGHT - getPoints().get(0).getY());
        graphics.fill();

        for(Point2D point2D : stones) {
            graphics.setFill(Color.DARKGRAY);
            graphics.fillOval(point2D.getX(), point2D.getY(), 5, 5);
            graphics.fill();
        }
    }

    public void setPoints() {

    }

    public void updateGroundAfterCollision() {

    }
}
