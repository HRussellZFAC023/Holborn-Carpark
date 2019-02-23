package uk.co.holborn.carparkclient;

/**
 * The Sprites class contains all the sprites used throughout the application runtime
 * <p></p>
 *
 * @author Vlad Alboiu
 * @version 1.0
 */
public enum Sprites {
    TICKET_INSERT {
        @Override
        public String getImageName() {
            return "ticket_insert_sprite.png";
        }
    };

    /**
     * Method that gets the image name
     * @return image name
     */
     abstract String getImageName();

    /**
     * Method that returns the full path of the image in resources folder (ex. /img/example.png0
     * @return path
     */
    public String getImageFromResources() {
        String imagesDirectory = "/img/";
        return imagesDirectory + getImageName();
    }
}
