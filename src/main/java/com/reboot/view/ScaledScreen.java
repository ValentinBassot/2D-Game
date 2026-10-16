package com.reboot.view;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class ScaledScreen extends StackPane {

    private final NumberBinding scale;

    public ScaledScreen(double baseWidth, double baseHeight) {
        this.scale = Bindings.min(widthProperty().divide(baseWidth), heightProperty().divide(baseHeight));
        setStyle("-fx-background-color: black;");
        setPrefSize(baseWidth, baseHeight);
    }

    public void show(Region view) {
        view.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        view.scaleXProperty().bind(scale);
        view.scaleYProperty().bind(scale);
        getChildren().setAll(view);
    }
}
