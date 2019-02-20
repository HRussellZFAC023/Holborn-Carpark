package uk.co.holborn.carparkclient;

import javafx.scene.Scene;

public class ThemeProvider {
    static Scene scene;
    static Themes currentTheme;

    ThemeProvider(Scene scene, Themes defaultTheme) {
        ThemeProvider.scene = scene;
        switchTheme(defaultTheme);
    }

    public static Themes getCurrentTheme() {
        return currentTheme;
    }

    public static void switchTheme(Themes theme) {
        if (currentTheme == null) currentTheme = theme;
        if (currentTheme != theme) {
            scene.getStylesheets().remove(currentTheme.getStylesheetPath());
            currentTheme = theme;
        }
        scene.getStylesheets().add(theme.getStylesheetPath());
    }

}
