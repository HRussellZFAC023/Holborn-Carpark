package uk.co.holborn.carparkclient;

/**
 * Themes class contains two themes, LIGHT and DARK
 * with their specified stylesheet resource.
 *
 * @author Vlad Alboiu
 * @version 1.0
 */
public enum Themes {
    /**
     * Light theme
     */
    LIGHT("/css/light.css"),
    /**
     * Dark theme
     */
    DARK("/css/dark.css");

    private final String stylesheetPath;

    /**
     * Constructor for a the theme
     *
     * @param stylesheetPath the path where the css file can be found under resources folder
     */
    Themes(String stylesheetPath) {
        this.stylesheetPath = stylesheetPath;
    }

    /**
     * Method that gets the stylesheets location string within the resources folder
     *
     * @return stylesheet location
     */
    public String getStylesheetPath() {
        return stylesheetPath;
    }
}
