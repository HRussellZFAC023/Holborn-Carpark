package uk.co.holborn.carparkclient;

/**
 * Themes class contains two themes, LIGHT and DARK
 * with their specified stylesheet resource.
 *
 * @author Vlad Alboiu
 * @version 1.0
 */
public enum Themes {
    LIGHT{
        @Override
        String getStylesheetPath() {
            return "/css/light.css";
        }
    },DARK{
        @Override
        String getStylesheetPath() {
            return "/css/dark.css";
        }
    };
    /**
     * Method that gets the stylesheets location string within the resources folder
     * @return stylesheet location
     */
    abstract String getStylesheetPath();
}
