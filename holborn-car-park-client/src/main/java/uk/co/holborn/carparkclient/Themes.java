package uk.co.holborn.carparkclient;

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
    abstract String getStylesheetPath();
}
