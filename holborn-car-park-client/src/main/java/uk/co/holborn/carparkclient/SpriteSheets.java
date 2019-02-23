package uk.co.holborn.carparkclient;

import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * The SpriteSheet class contains helps us keep track of all
 * the sprite sheets in the app while alo allowing preloading evey image on load
 * <p></p>
 * This class needs to have the sprite paths declared in {@link Sprites}
 *
 * @author Vlad Alboiu
 * @version 1.0
 */
public class SpriteSheets {
    private Logger log;
    private Map<Sprites, Image> images;

    /**
     * Initialises the class
     */
    public SpriteSheets() {
        log = LogManager.getLogger(getClass().getName());
        images = new HashMap<>();
    }

    /**
     * Load all the images
     */
    public void load(){
        log.info("Loading images");
        int i = 1;
        for(Sprites sprite : Sprites.values()){
            log.info("Loading " + i + " out of " + Sprites.values().length);
            images.put(sprite, loadImage(sprite.getImageFromResources()));
            i++;
        }
        log.info("All images have been loaded!");
    }

    /**
     * Get the image corresponding to a Sprite
     * @param sprite the key
     * @return the sprite image
     */
    public Image getImage(Sprites sprite){
        return images.get(sprite);
    }

    /**
     * This method return a image based on a source in the resources folder
     * @param source the name of the image
     * @return a Image object
     */
    private Image loadImage(String source){
        try{
            return new Image(getClass().getResourceAsStream(source));
        }catch (Exception e){
            e.printStackTrace();
            log.trace(e.getStackTrace());
        }
        return null;
    }
}
