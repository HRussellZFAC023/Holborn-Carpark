/*
 * Copyright (c) 2019. Ionut-Vlad Alboiu.
 */

package uk.co.holborn.carparkclient.Networking;

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
    public NoConnectionError(String errorMessage) {
        super(errorMessage);
    }
}
