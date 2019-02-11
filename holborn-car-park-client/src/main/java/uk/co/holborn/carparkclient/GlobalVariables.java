package uk.co.holborn.carparkclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * Class that provides all the global variables used during runtime loaded from a configuration file
 *
 * @author Vlad Alboiu
 * @version 1.0
 */
public class GlobalVariables {
    static String APP_NAME = "";
    static String CAR_PARK_NAME = "";
    static String MAIN_WINDOW_NAME = "";
    public static String LANDING_PAGE_WELCOME = "";
    public static String CAR_PARK_ID = "";
    public static String WEBSERVICE_SOCKET = "";
    public static int SESSION_TIMEOUT_S = 300;
    public static int SESSION_TIMEOUT_POPUP_DURATION_S = 3;
    private Logger logger = LogManager.getLogger(getClass().getName());
    String configName = "config.xml";
    Properties appProp;

    /**
     * Constructor
     */
    public GlobalVariables() {
        loadFile();
    }

    private OutputStream output = null;
    private InputStream input = null;

    /**
     * Loads the variables from the configuration file. If the file doesn't exist, it will be created.
     */
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
                appProp.setProperty("session_timeout_seconds", "300");
                appProp.setProperty("session_timeout_popup_duration", "3");
                appProp.storeToXML(output, null);
                logger.warn("File created! Please modify the config file with the received information from your administrator.");
                System.exit(-1);
            }
            input = new FileInputStream(confFile);
            // load a properties file
            logger.info("Loaded configuration file");
            appProp.loadFromXML(input);
            APP_NAME = appProp.getProperty("app_name");
            CAR_PARK_NAME = appProp.getProperty("car_park_name");
            CAR_PARK_ID = appProp.getProperty("car_park_id");
            if (CAR_PARK_ID.isEmpty()) {
                logger.error("The CAR_PARK_ID must be specified!");
                System.exit(-1);
            }
            WEBSERVICE_SOCKET = appProp.getProperty("webservice");
            MAIN_WINDOW_NAME = APP_NAME + " - " + CAR_PARK_NAME;
            LANDING_PAGE_WELCOME = "Welcome to " + CAR_PARK_NAME + "!";
            SESSION_TIMEOUT_S = Integer.parseInt((appProp.getProperty("session_timeout_seconds")));
            SESSION_TIMEOUT_POPUP_DURATION_S = Integer.parseInt((appProp.getProperty("session_timeout_popup_duration")));

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
