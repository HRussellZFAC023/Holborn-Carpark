package uk.co.holborn.carparkclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class GlobalVariables {
    static String app_name = "";
    static String car_park_name = "";
    static String main_window_name ="";
    public static String landing_page_welcome="";
    public static String car_park_id = "";
    private Logger logger = LogManager.getLogger(getClass().getName());

    //    public static String db_domain = "https://notification-service-test-run.localtunnel.me";
    public static String webservice_socket = "";

    String configName = "config.xml";
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
                logger.warn("Configuration file is missing");
                logger.info("Creating default configuration file");
                output = new FileOutputStream(confFile);
                appProp.setProperty("app_name", "Holborn Car Park System");
                appProp.setProperty("car_park_name", "");
                appProp.setProperty("car_park_id", "");
                appProp.setProperty("webservice", "http://DOMAIN:PORT");
                appProp.storeToXML(output,null);
                logger.warn("File created! Please modify the config file with the received information from your administrator.");
                System.exit(-1);
            }
            input = new FileInputStream(confFile);
            // load a properties file
            logger.info("Loaded configuration file");
            appProp.loadFromXML(input);
            app_name = appProp.getProperty("app_name");
            car_park_name = appProp.getProperty("car_park_name");
            car_park_id = appProp.getProperty("car_park_id");
            if(car_park_id.isEmpty()) {
                logger.error("The car_park_id must be specified!");
                System.exit(-1);
            }
            webservice_socket = appProp.getProperty("webservice");
            main_window_name = app_name + " - " + car_park_name;
            landing_page_welcome = "Welcome to " + car_park_name+  "!";

        } catch (IOException ex) {
            logger.trace(ex.getStackTrace());
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.trace(e.getStackTrace());
                    e.printStackTrace();
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    logger.trace(e.getStackTrace());
                    e.printStackTrace();
                }
            }

        }
    }
}
