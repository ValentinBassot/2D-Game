package com.reboot.view;

import com.reboot.model.settings.Action;
import com.reboot.model.settings.KeyBindings;
import java.util.List;
import java.util.function.IntConsumer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Un écran de menu : un titre et une liste d'entrées, au clavier ou à la souris.
 *
 * <p>Pattern Template Method : cette classe fixe le déroulé (navigation, validation, rendu)
 * et chaque menu ne décrit que son titre et ses entrées. Ajouter un menu revient à écrire
 * une sous-classe, sans toucher à la navigation.
 */
public abstract class MenuView extends StackPane {

    /** Largeur de l'écran. */
    protected static final int WIDTH = ExplorationView.VIEW_WIDTH;
    /** Hauteur de l'écran. */
    protected static final int HEIGHT = ExplorationView.VIEW_HEIGHT;
    /** Couleur du texte principal. */
    protected static final Color TEXT = Color.rgb(235, 238, 250);
    /** Couleur du texte secondaire. */
    protected static final Color MUTED = Color.rgb(150, 158, 190);
    /** Couleur de l'entrée sélectionnée. */
    protected static final Color HIGHLIGHT = Color.rgb(60, 180, 255);

    private static final Color BACKGROUND = Color.rgb(10, 14, 28);
    private static final double ITEM_WIDTH = 560;
    private static final double ITEM_HEIGHT = 42;

    /** Une entrée de menu : un bouton ou un réglage ajustable à gauche et à droite. */
    protected record MenuItem(String label, Runnable onConfirm, IntConsumer onAdjust) {

        /**
         * @param label le texte de l'entrée
         * @param action ce que fait la validation
         * @return un bouton
         */
        public static MenuItem button(String label, Runnable action) {
            return new MenuItem(label, action, delta -> { });
        }

        /**
         * @param label le texte de l'entrée, avec sa valeur actuelle
         * @param adjust reçoit {@code -1} ou {@code 1} ; valider revient à augmenter
         * @return un réglage
         */
        public static MenuItem setting(String label, IntConsumer adjust) {
            return new MenuItem(label, () -> adjust.accept(1), adjust);
        }
    }

    /** Les touches du joueur, utilisées pour naviguer. */
    protected final KeyBindings keys;
    private final Canvas canvas = new Canvas(WIDTH, HEIGHT);
    private int selected;

    /**
     * @param keys les touches du joueur
     */
    protected MenuView(KeyBindings keys) {
        this.keys = keys;
        getChildren().add(canvas);
        setFocusTraversable(true);
        setOnKeyPressed(this::onKeyPressed);
        setOnMouseMoved(this::onMouseMoved);
        setOnMouseClicked(this::onMouseClicked);
    }

    /** @return le titre du menu */
    protected abstract String title();

    /** @return les entrées du menu, recalculées à chaque affichage pour montrer les valeurs à jour */
    protected abstract List<MenuItem> items();

    /** @return une ligne sous le titre, vide par défaut */
    protected String subtitle() {
        return "";
    }

    /** @return l'aide affichée en bas de l'écran */
    protected String footer() {
        return "Entrée : valider  ·  Gauche / Droite : régler  ·  Échap : retour";
    }

    /** Appelée quand le joueur demande à revenir en arrière ; ne fait rien par défaut. */
    protected void onBack() {
    }

    /**
     * Laisse un menu traiter une touche avant la navigation, par exemple pour réassigner une touche.
     *
     * @param event la touche pressée
     * @return {@code true} si la touche a été traitée
     */
    protected boolean interceptKey(KeyEvent event) {
        return false;
    }

    /**
     * Dessine un décor propre au menu, sous les entrées ; rien par défaut.
     *
     * @param gc le contexte graphique
     */
    protected void decorate(GraphicsContext gc) {
    }

    /** @return la hauteur de la première entrée */
    protected double itemsTop() {
        return 300;
    }

    /** @return l'index de l'entrée sélectionnée */
    protected final int getSelected() {
        return selected;
    }

    /** Affiche le menu et prend le focus du clavier. */
    public void start() {
        requestFocus();
        render();
    }

    /** Redessine le menu, après un changement de valeur. */
    protected final void refresh() {
        render();
    }

    private void onKeyPressed(KeyEvent event) {
        if (interceptKey(event)) {
            render();
            return;
        }
        List<MenuItem> items = items();
        keys.actionOf(event.getCode().name()).ifPresent(action -> handle(action, items));
        render();
    }

    private void handle(Action action, List<MenuItem> items) {
        switch (action) {
            case MOVE_UP -> selected = Math.floorMod(selected - 1, items.size());
            case MOVE_DOWN -> selected = Math.floorMod(selected + 1, items.size());
            case MOVE_LEFT -> items.get(selected).onAdjust().accept(-1);
            case MOVE_RIGHT -> items.get(selected).onAdjust().accept(1);
            case CONFIRM -> items.get(selected).onConfirm().run();
            case PAUSE -> onBack();
        }
    }

    private int itemAt(double x, double y) {
        double left = (WIDTH - ITEM_WIDTH) / 2;
        int index = (int) Math.floor((y - itemsTop()) / ITEM_HEIGHT);
        boolean inside = x >= left && x <= left + ITEM_WIDTH && y >= itemsTop();
        return inside && index < items().size() ? index : -1;
    }

    private void onMouseMoved(MouseEvent event) {
        int index = itemAt(event.getX(), event.getY());
        if (index >= 0 && index != selected) {
            selected = index;
            render();
        }
    }

    private void onMouseClicked(MouseEvent event) {
        int index = itemAt(event.getX(), event.getY());
        if (index >= 0) {
            selected = index;
            items().get(index).onConfirm().run();
            render();
        }
    }

    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(BACKGROUND);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        decorate(gc);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        gc.setFill(TEXT);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 64));
        gc.fillText(title(), WIDTH / 2.0, 140);
        gc.setFill(MUTED);
        gc.setFont(Font.font("Monospaced", 18));
        gc.fillText(subtitle(), WIDTH / 2.0, 205);

        List<MenuItem> items = items();
        selected = Math.min(selected, items.size() - 1);
        double left = (WIDTH - ITEM_WIDTH) / 2;
        for (int i = 0; i < items.size(); i++) {
            double y = itemsTop() + i * ITEM_HEIGHT;
            if (i == selected) {
                gc.setFill(HIGHLIGHT.deriveColor(0, 1, 1, 0.2));
                gc.fillRect(left, y + 3, ITEM_WIDTH, ITEM_HEIGHT - 6);
                gc.setStroke(HIGHLIGHT);
                gc.setLineWidth(2);
                gc.strokeRect(left, y + 3, ITEM_WIDTH, ITEM_HEIGHT - 6);
            }
            gc.setFill(i == selected ? TEXT : MUTED);
            gc.setFont(Font.font("Monospaced", i == selected ? FontWeight.BOLD : FontWeight.NORMAL, 21));
            gc.fillText(items.get(i).label(), WIDTH / 2.0, y + ITEM_HEIGHT / 2);
        }

        gc.setFill(MUTED);
        gc.setFont(Font.font("Monospaced", 14));
        gc.fillText(footer(), WIDTH / 2.0, HEIGHT - 30);
    }
}
