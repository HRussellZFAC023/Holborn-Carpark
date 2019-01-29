package uk.co.holborn.carparkclient;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class GlobalVariables {
    static String app_name = "";
    static String car_park_name = "";
    static String main_window_name = app_name + " - " + car_park_name;
    public static String landing_page_welcome = "Welcome to " + car_park_name;
    public static String car_park_id = "";

    //    public static String db_domain = "https://notification-service-test-run.localtunnel.me";
    public static String webservice_socket = "";

    String configName = "app.properties";
    Properties appProp;

    public GlobalVariables() {
        loadFile();
    }

    private OutputStream output = null;
    private InputStream input = null;

    private void loadFile() {
        appProp = new Properties();
        try {
            File confFile = new File(configName);
            boolean created = confFile.createNewFile();
            if (created) {
                System.out.println("Configuration file missing.");
                System.out.println("Created configuration file.");
                output = new FileOutputStream(confFile);
                appProp.setProperty("app_name", "Holborn Car Park System");
                appProp.setProperty("car_park_name", " ");
                appProp.setProperty("car_park_id", " ");
                appProp.setProperty("webservice", "http://DOMAIN:PORT");
                appProp.storeToXML(output,null);
            }
            input = new FileInputStream(confFile);
            // load a properties file
            System.out.println("Loaded configuration file.");
            appProp.loadFromXML(input);
            app_name = appProp.getProperty("app_name");
            car_park_name = appProp.getProperty("car_park_name");
            car_park_id = appProp.getProperty("car_park_id");
            webservice_socket = appProp.getProperty("webservice");
            main_window_name = app_name + " - " + car_park_name;

        } catch (FileNotFoundException not_found) {
            System.out.println("Configuration file is missing.");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
