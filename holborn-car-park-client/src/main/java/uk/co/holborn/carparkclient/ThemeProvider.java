package uk.co.holborn.carparkclient;

import javafx.scene.Scene;

public class ThemeProvider {
    static Scene scene;
    static Themes currentTheme;
    ThemeProvider(Scene scene, Themes defaultTheme){
        ThemeProvider.scene =scene;
        switchTheme(defaultTheme);
    }
    public static void switchTheme(Themes theme){
        if(currentTheme == null) currentTheme = theme;
        else scene.getStylesheets().remove(currentTheme.getStylesheetPath());
        scene.getStylesheets().add(theme.getStylesheetPath());
    }
}
