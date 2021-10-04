package ch.zhaw.pm2.fats.gui;

import ch.zhaw.pm2.fats.Config.SoilType;
import ch.zhaw.pm2.fats.canvas.Chest;
import ch.zhaw.pm2.fats.canvas.Drawable;
import ch.zhaw.pm2.fats.canvas.Ground;
import ch.zhaw.pm2.fats.canvas.Projectile;
import ch.zhaw.pm2.fats.canvas.Tank;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.util.Duration;
import java.util.ArrayList;

/**
 * The render class, which render the objects and add the clouds to the scene.
 */
public class RendererService extends Service<Void> {
    private final GraphicsContext graphicsContext;
    private final Canvas canvas;
    private ArrayList<Drawable> canvasObjects;

    /**
     * The class constructor of the renderService.
     *
     * @param graphicsContext The graphicsContext element.
     * @param canvas The canvas Object.
     * @param canvasObjects An arrayList of the drawable elements.
     * @param soilType The soilType enum.
     */
    public RendererService(GraphicsContext graphicsContext, Canvas canvas, ArrayList<Drawable> canvasObjects, SoilType soilType) {
        this.graphicsContext = graphicsContext;
        this.canvas = canvas;
        this.canvasObjects = canvasObjects;

        setOnSucceeded(event -> {
            DoubleProperty x = new SimpleDoubleProperty();
            DoubleProperty y = new SimpleDoubleProperty();

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0),
                            new KeyValue(x, -150),
                            new KeyValue(y, 0)
                    ),
                    new KeyFrame(Duration.seconds(15),
                            new KeyValue(x, canvas.getHeight() + 150),
                            new KeyValue(y, 0)
                    )
            );
            timeline.setCycleCount(Timeline.INDEFINITE);

            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    gc.setFill(soilType.getColor());
                    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                    gc.setStroke(Color.WHITE);
                    gc.beginPath();
                    gc.setLineWidth(1.0);
                    // Arc Cloud top left
                    gc.strokeArc(x.doubleValue() + 30, y.doubleValue() + 5, 30, 30, 90, 90, ArcType.OPEN);
                    // Arc Cloud left down
                    gc.strokeArc(x.doubleValue() + 15, y.doubleValue() + 20, 30, 30, 90, 90, ArcType.OPEN);
                    // Connection left right
                    gc.strokeLine(x.doubleValue() + 45, y.doubleValue() + 5, x.doubleValue() + 105, y.doubleValue() + 5);
                    // Arc Cloud top right
                    gc.strokeArc(x.doubleValue() + 90, y.doubleValue() + 5, 30, 30, 0, 90, ArcType.OPEN);
                    // Arc Cloud bottom right
                    gc.strokeArc(x.doubleValue() + 105, y.doubleValue() + 20, 30, 30, 0, 90, ArcType.OPEN);
                    gc.strokeLine(x.doubleValue() + 15, y.doubleValue() + 35, x.doubleValue() + 135, y.doubleValue() + 35);

                    gc.setStroke(Color.WHITE);
                    gc.beginPath();
                    gc.setLineWidth(1.0);
                    // Arc Cloud top left
                    gc.strokeArc(x.doubleValue() + 80, y.doubleValue() + 55, 30, 30, 90, 90, ArcType.OPEN);
                    // Arc Cloud bottom left
                    gc.strokeArc(x.doubleValue() + 65, y.doubleValue() + 70, 30, 30, 90, 90, ArcType.OPEN);
                    // Connection left right
                    gc.strokeLine(x.doubleValue() + 95, y.doubleValue() + 55, x.doubleValue() + 155, y.doubleValue() + 55);
                    // Arc Cloud top right
                    gc.strokeArc(x.doubleValue() + 140, y.doubleValue() + 55, 30, 30, 0, 90, ArcType.OPEN);
                    // Arc Cloud bottom right
                    gc.strokeArc(x.doubleValue() + 155, y.doubleValue() + 70, 30, 30, 0, 90, ArcType.OPEN);
                    gc.strokeLine(x.doubleValue() + 65, y.doubleValue() + 85, x.doubleValue() + 185, y.doubleValue() + 85);
                    renderObjects();
                }
            };
            timer.start();
            timeline.play();
        });
    }

    /**
     * Render the objects on the screen.
     */
    public void renderObjects() {
        for (Drawable canvasObject : canvasObjects) {
            canvasObject.drawMe(graphicsContext);
        }
        drawCanvasBorder();
    }

    /**
     * Draw the border of the canvas
     */
    private void drawCanvasBorder() {
        graphicsContext.beginPath();
        graphicsContext.moveTo(0, 0);
        graphicsContext.lineTo(0, canvas.getHeight());
        graphicsContext.lineTo(canvas.getWidth(), canvas.getHeight());
        graphicsContext.lineTo(canvas.getWidth(), 0);
        graphicsContext.lineTo(0, 0);
        graphicsContext.closePath();
        graphicsContext.setLineWidth(5);
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.stroke();
    }


    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() {
                return null;
            }
        };
    }

    /**
     * Remove the last projectile.
     */
    public void removeLastProjectile() {
        ArrayList<Drawable> filteredList = new ArrayList<>();
        for (Drawable obj : canvasObjects) {
            if (obj.getClass() != Projectile.class) {
                filteredList.add(obj);
            }
        }
        canvasObjects = filteredList;
    }

    /**
     * Add a projectile to the arrayList.
     *
     * @param projectile The projectile object.
     */
    public void addLatestProjectile(Projectile projectile) {
        canvasObjects.add(projectile);
    }

    /**
     * Remove a chest from the canvas.
     */
    public void removeChest() {
        ArrayList<Drawable> filteredList = new ArrayList<>();
        for (Drawable obj : canvasObjects) {
            if (obj.getClass() != Chest.class) {
                filteredList.add(obj);
            }
        }
        canvasObjects =  filteredList;
    }

    public ArrayList<Drawable> getCanvasObjects() {
        return canvasObjects;
    }

    /**
     * Get an ArrayList with all objects but without the tank, ground and projectile.
     *
     * @return The ArrayList with the objects.
     */
    public ArrayList<Drawable> getCanvasObjectsWithoutTankGroundAndProjectile() {
        ArrayList<Drawable> filteredList = new ArrayList<>();
        for (Drawable obj : canvasObjects) {
            if (obj.getClass() != Ground.class && obj.getClass() != Tank.class && obj.getClass() != Projectile.class) {
                filteredList.add(obj);
            }
        }
        return filteredList;
    }
}
