/*
 * Copyright (c) 2019. Ionut-Vlad Alboiu.
 * And me?
 */
package uk.co.holborn.carparkbarriers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * Class that provides all the global variables used
 * during runtime loaded from a configuration file
 *
 * @author Vlad Alboiu
 * @author Cameron
 * @version 1.0.1
 */
public class GlobalVariables {
    private Logger logger = LogManager.getLogger(getClass().getName());

    private static final String APP_NAME = "Holborn Car Park System";
    private static String CAR_PARK_NAME = "";
    static String MAIN_WINDOW_NAME = "";
    public static String LANDING_PAGE_WELCOME = "";
    public static String BARRIER_TYPE = "";
    public static String SOCKET_ADDRESS = "";
    private static int SESSION_TIMEOUT_S = 300;
    private static int SESSION_TIMEOUT_POPUP_DURATION_S = 3;
    private static int TRANSACTION_FINISHED_DELAY_S = 10;
    private static boolean AUTO_NIGHT_TIME = true;
    private static int NIGHT_TIME_START = 19;
    private static int NIGHT_TIME_END = 7;

    private OutputStream output = null;
    private InputStream input = null;
    private String configName = "config.xml";
    private Properties appProp;

    /**
     * Constructor loads the file
     */
    public GlobalVariables() {
        loadFile();
    }

    /**
     * Loads the variables from the configuration file.
     * If the file doesn't exist, it will be created.
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
                appProp.setProperty("car_park_name", "");
                appProp.setProperty("car_park_id", "");
                appProp.setProperty("webservice", "IP:PORT");
                appProp.setProperty("barrier_type", "");
                appProp.setProperty("session_timeout_seconds", "300");
                appProp.setProperty("session_timeout_popup_duration", "3");
                appProp.setProperty("auto_night_time", "true");
                appProp.setProperty("night_time_start", "19");
                appProp.setProperty("night_time_end", "7");
                appProp.setProperty("transaction_finished_delay", "10");
                appProp.storeToXML(output, null);
                logger.info("Configuration file created! ");
                logger.error("Please modify the config file with the received information " +
                        "from your administrator. (Configuration file: " + configName + ")");
                Alerter.showUnableToStartAlertAndOpenRunningDirectory(
                        "Configuration file created!",
                        "Before starting the application, please update the configuration" +
                                " file with the received information from your administrator. (Configuration file: \" + configName + \")\""
                );
            }
            input = new FileInputStream(confFile);
            // load a properties file
            appProp.loadFromXML(input);
            CAR_PARK_NAME = checkNullOrEmptyProperty("car_park_name");
            SOCKET_ADDRESS = checkNullOrEmptyProperty("webservice");
            MAIN_WINDOW_NAME = APP_NAME + " - " + CAR_PARK_NAME;
            LANDING_PAGE_WELCOME = "Welcome to " + CAR_PARK_NAME + "!";
            BARRIER_TYPE = checkValidBarrier();
            TRANSACTION_FINISHED_DELAY_S = Integer.parseInt(checkNullOrEmptyProperty("transaction_finished_delay"));
            SESSION_TIMEOUT_S = Integer.parseInt(checkNullOrEmptyProperty("session_timeout_seconds"));
            SESSION_TIMEOUT_POPUP_DURATION_S = Integer.parseInt(checkNullOrEmptyProperty("session_timeout_popup_duration"));
            AUTO_NIGHT_TIME = Boolean.parseBoolean(checkNullOrEmptyProperty("auto_night_time"));
            if (AUTO_NIGHT_TIME) {
                NIGHT_TIME_START = Integer.parseInt(checkNullOrEmptyProperty("night_time_start"));
                NIGHT_TIME_END = Integer.parseInt(checkNullOrEmptyProperty("night_time_end"));
            }
            logger.info("Loaded configuration file");
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Method that checks if the property is null or not
     *
     * @param property the property to be checked
     * @return true or false
     */
    private String checkNullProperty(String property) {
        String s = appProp.getProperty(property);
        if (s == null) {
            logger.error("The \"" + property + "\" property does not exist!");
            Alerter.showUnableToStartAlertAndOpenRunningDirectory(
                    "The \"" + property + "\" property does not exist!",
                    "Please add it in the configuration file with the received" +
                            " information from your administrator. (Configuration file: " + configName + ")"
            );
        }
        return s;
    }

    /**
     * Method that checks if the property is null or empty
     *
     * @param property the property to be checked
     * @return true or false
     */
    private String checkNullOrEmptyProperty(String property) {
        String s = checkNullProperty(property);
        if (s.isEmpty()) {
            logger.error("The \"" + property + "\" property cannot be empty!");
            Alerter.showUnableToStartAlertAndOpenRunningDirectory(
                    "The \"" + property + "\" cannot be empty!",
                    "Please add it in the configuration file with the received" +
                            " information from your administrator. (Configuration file: " + configName + ")"
            );
        }
        return s;
    }

    /**
     * Method that checks if the barrier definition is "In" or "Out".
     *
     * @return true if the barrier type is "In" or "Out", false otherwise
     */
    private String checkValidBarrier(){
        String s = checkNullOrEmptyProperty("barrier_type");
        if(!s.equalsIgnoreCase("In")&&!s.equalsIgnoreCase("Out")){
            logger.error("The \"" + "BARRIER_TYPE" + "\" is not \"In\" or \"Out\"!");
            Alerter.showUnableToStartAlertAndOpenRunningDirectory(
                    "The \"" + "BARRIER_TYPE" + "\" must be \"In\" or \"Out\"!",
                    "Please add it in the configuration file with the received" +
                            " information from your administrator. (Configuration file: " + configName + ")"
            );
        }
        return s;
    }

    /**
     * Method that checks if the property is null or empty
     *
     * @return true if the barrier is defined as an 'In' barrier, false otherwise
     */
    public static boolean getBarrierType() {
        return BARRIER_TYPE.equalsIgnoreCase("In");
    }

    /**
     * Method that gets whether the screen auto-changes between themes
     *
     * @return true or false
     */
    public static boolean AUTO_NIGHT_TIME(){
        return AUTO_NIGHT_TIME;
    }

    /**
     * Method that gets the start hour of night theme
     *
     * @return hour that night theme is started at
     */
    public static int NIGHT_TIME_START(){
        return NIGHT_TIME_START;
    }

    /**
     * Method that gets the end hour of night theme
     *
     * @return hour that night theme is ended at
     */
    public static int NIGHT_TIME_END(){
        return NIGHT_TIME_END;
    }

    /**
     * Method that gets the length of a session
     *
     * @return the time that a session lasts.
     */
    public static int SESSION_TIMEOUT_S(){
        return SESSION_TIMEOUT_S;
    }

    /**
     * Method that gets the length of time the popup about sessions lasts
     *
     * @return the time that the popup lasts lasts.
     */
    public static int SESSION_TIMEOUT_POPUP_DURATION_S(){
        return SESSION_TIMEOUT_POPUP_DURATION_S;
    }
}
