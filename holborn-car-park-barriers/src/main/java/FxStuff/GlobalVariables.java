/*
 * Copyright (c) 2019. Ionut-Vlad Alboiu.
 * And me?
 */
package FxStuff;

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

    private static final String APP_NAME = "Holborn Car Park System";
    public static String CAR_PARK_NAME = "";
    static String MAIN_WINDOW_NAME = "";
    public static String LANDING_PAGE_WELCOME = "";
    public static String CAR_PARK_ID = "";
    public static String WEBSERVICE_SOCKET = "";
    public static int SESSION_TIMEOUT_S = 300;
    public static int SESSION_TIMEOUT_POPUP_DURATION_S = 3;
    public static int TRANSACTION_FINISHED_DELAY_S = 10;
    public static boolean AUTO_NIGHT_TIME = true;
    public static int NIGHT_TIME_START = 19;
    public static int NIGHT_TIME_END = 7;

    private OutputStream output = null;
    private InputStream input = null;

    static String app_name = "";
    static String car_park_name = "";
    static String main_window_name = "";
    public static String landing_page_welcome = "";
    public static String car_park_id = "";
    public static String barrier_type = "";
    private Logger logger = LogManager.getLogger(getClass().getName());

    public static String webservice_socket = "";

    private String configName = "config.xml";
    private Properties appProp;

    public GlobalVariables() {
        loadFile();
    }

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
                appProp.setProperty("webservice", "Server IP:PORT");
                appProp.setProperty("barrier_type", "None");
                appProp.storeToXML(output, null);
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
            if (car_park_id.isEmpty()) {
                logger.error("The car_park_id must be specified!");
                System.exit(-1);
            }
            webservice_socket = appProp.getProperty("webservice");
            barrier_type = appProp.getProperty("barrier_type");
            if (!barrier_type.equalsIgnoreCase("In") && !barrier_type.equalsIgnoreCase("Out")) {
                logger.error("The barrier_type must be specified!");
                System.exit(-1);
            }
            main_window_name = app_name + " - " + car_park_name;
            landing_page_welcome = "Welcome to " + car_park_name + "!";

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

    public static boolean getBarrierType(){
        return barrier_type.equalsIgnoreCase("In");
    }
}
