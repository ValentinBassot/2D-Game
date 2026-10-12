package com.reboot.view;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class SpriteStore {

    private static final String ASSETS_ROOT = "/com/reboot/assets/";

    private final Map<String, Image> cache = new HashMap<>();

    public void draw(GraphicsContext gc, String path, double x, double y, double size, Color fallback) {
        Image image = cache.computeIfAbsent(path, SpriteStore::load);
        if (image == null) {
            gc.setFill(fallback);
            gc.fillRect(x, y, size, size);
        } else {
            gc.drawImage(image, x, y, size, size);
        }
    }

    private static Image load(String path) {
        InputStream stream = SpriteStore.class.getResourceAsStream(ASSETS_ROOT + path);
        if (stream == null) {
            return null;
        }
        Image image = new Image(stream);
        return image.isError() ? null : image;
    }
}
