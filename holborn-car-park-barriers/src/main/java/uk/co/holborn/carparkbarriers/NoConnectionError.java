package uk.co.holborn.carparkbarriers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A custom error that is thrown to start the barrier trying to reconnect to the client.
 *
 * @author Cameron
 * @version 1.0.0
 */

public class NoConnectionError extends Exception {

    /**
     * The constructor for the error, passes the error message onto the super-constructor.
     *
     * @param errorMessage The error message that is displayed.
     * @since 1.0.0
     */
    //TODO Ask Vlad if this needs to write into the logger?
    public NoConnectionError(String errorMessage) {
        super(errorMessage);
        Logger logger = LogManager.getLogger(getClass().getName());
        logger.error(errorMessage);
    }
}
