package uk.co.holborn.carparkclient;

 class GlobalVariables {
     static String app_name = "Holborn Car Park";
     static String car_park_name = "Egham Car Park";
     static String main_window_name = app_name + " - " + car_park_name;
     static String landing_page_welcome = "Welcome to " + car_park_name;
     static String car_park_id = "5c4e397ee23c000a27a94720";

//    public static String db_domain = "https://notification-service-test-run.localtunnel.me";
     static String db_domain = "http://localhost:3000";
     static String db_loginURL = db_domain + "/auth/login";
     static String db_adminVerifURL = db_domain + "/auth/meadmin";
    //TODO <- add remaining variables
     static String db_notifFetchAll = db_domain + "/notifications";
     static String db_notifUpdateOrCreate = "";
     static String db_notifDelete ="";
}
