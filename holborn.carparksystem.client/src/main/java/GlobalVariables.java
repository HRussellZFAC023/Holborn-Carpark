public class GlobalVariables {
//    public static String db_domain = "https://notification-service-test-run.localtunnel.me";
public static String db_domain = "http://localhost:3000";
    public static String db_loginURL = db_domain + "/auth/login";
    public static String db_adminVerifURL = db_domain + "/auth/meadmin";
    //TODO <- add remaining variables
    public static String db_notifFetchAll = db_domain + "/notifications";
    public static String db_notifUpdateOrCreate = "";
    public static String db_notifDelete ="";
}
