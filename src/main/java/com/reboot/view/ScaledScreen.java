package com.reboot.view;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * Conteneur de la fenêtre. Il agrandit la vue à la taille de la fenêtre en gardant ses proportions, avec
 * des bandes noires si besoin.
 */
public class ScaledScreen extends StackPane {

    private final NumberBinding scale;

    /**
     * Crée le cadre.
     *
     * @param baseWidth la largeur de base des vues
     * @param baseHeight la hauteur de base des vues
     */
    public ScaledScreen(double baseWidth, double baseHeight) {
        this.scale = Bindings.min(widthProperty().divide(baseWidth), heightProperty().divide(baseHeight));
        setStyle("-fx-background-color: black;");
        setPrefSize(baseWidth, baseHeight);
    }

    /**
     * Règle la luminosité de tout l'écran.
     *
     * @param level de -5 à 5, 0 pour la luminosité normale
     */
    public void setBrightness(int level) {
        setEffect(level == 0 ? null : new ColorAdjust(0, 0, level / 10.0, 0));
    }

    /**
     * Affiche une vue à la place de la précédente.
     *
     * @param view la vue à afficher
     */
    public void show(Region view) {
        view.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        view.scaleXProperty().bind(scale);
        view.scaleYProperty().bind(scale);
        getChildren().setAll(view);
    }
}
