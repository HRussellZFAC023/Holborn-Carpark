package uk.co.holborn.carparkclient;

import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * The SpriteSheet class contains helps us keep track of all
 * the sprite sheets in the app while alo allowing preloading evey image on load
 * This class needs to have the sprite paths declared in {@link Sprites}
 *
 * @author Vlad Alboiu
 * @version 1.0.1
 * @see SpriteSettings
 */
public class SpriteSheets {
    private final Logger log;
    private final Map<Sprites, SpriteSettings> spriteSettingsMap;
    private boolean loadedAll;

    /**
     * Initialises the class
     */
    public SpriteSheets() {
        log = LogManager.getLogger(getClass().getName());
        spriteSettingsMap = new HashMap<>();
    }

    /**
     * Load all the images
     */
    public void load() {
        loadedAll = true;
        log.info("Loading images");
        int i = 1;
        for (Sprites sprite : Sprites.values()) {
            log.info("Loading " + i + " out of " + Sprites.values().length);
            SpriteSettings sp = sprite.getSpriteSettings();
            sp.setImage(loadImage(sp));
            spriteSettingsMap.put(sprite, sp);
            i++;
        }
        if (loadedAll) log.info("All images have been loaded!");
        else {
            log.warn("Unable to load some images!");
        }
    }

    /**
     * This method returns the SpriteSettings stored in the hash map
     *
     * @param sprite the sprite from where we want its settings
     * @return a {@link SpriteSettings} object
     */
    public SpriteSettings getSpriteSettings(Sprites sprite) {
        return spriteSettingsMap.get(sprite);
    }

    /**
     * This method return a image based on a source defined in the sprite settings
     *
     * @param spriteSettings the name of the image
     * @return a Image object
     */
    private Image loadImage(SpriteSettings spriteSettings) {
        try {
            return new Image(
                    getClass().getResource(spriteSettings.getPath()).toExternalForm(),
                    spriteSettings.getScaleToWidth(),
                    spriteSettings.getScaleToHeight(),
                    true,
                    true, true);
        } catch (Exception e) {
            loadedAll = false;
            e.printStackTrace();
            log.warn("Unable to load: " + spriteSettings.getPath() + ". Message: " + e.getMessage());
        }
        return null;
    }
}
