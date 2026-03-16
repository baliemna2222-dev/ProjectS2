package JStream.controller;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Controller {

    @FXML
    private Canvas gradientCanvas;

    @FXML
    public void initialize() {
        drawConicGradient();
        gradientCanvas.setEffect(new GaussianBlur(10));

        RotateTransition rotate = new RotateTransition(Duration.seconds(4), gradientCanvas);
        rotate.setByAngle(360);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.setInterpolator(javafx.animation.Interpolator.LINEAR);
        rotate.play();
    }

    private void drawConicGradient() {
        GraphicsContext gc = gradientCanvas.getGraphicsContext2D();

        double centerX = gradientCanvas.getWidth() / 2;
        double centerY = gradientCanvas.getHeight() / 2;
        double radius = Math.min(centerX, centerY) - 20;

        for (int i = 0; i < 360; i++) {
            Color color = interpolateColor(i / 360.0);
            gc.setStroke(color);
            gc.setLineWidth(20);
            gc.strokeArc(centerX - radius, centerY - radius, radius * 2, radius * 2, i, 1, javafx.scene.shape.ArcType.OPEN);
        }
    }

    private Color interpolateColor(double t) {
        if (t < 0.33)
            return Color.web("#00c3ff").interpolate(Color.web("#4d0199"), t / 0.33);
        else if (t < 0.66)
            return Color.web("#4d0199").interpolate(Color.web("#6300c6"), (t - 0.33) / 0.33);
        else
            return Color.web("#6300c6").interpolate(Color.web("#00c3ff"), (t - 0.66) / 0.34);
    }
}
