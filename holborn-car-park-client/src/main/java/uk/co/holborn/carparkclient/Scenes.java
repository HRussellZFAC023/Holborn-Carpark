package uk.co.holborn.carparkclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import uk.co.holborn.carparkclient.controllers.*;

import java.io.IOException;
import java.net.URL;

/**
 * Scenes class contains every scene definition of the UI,
 * making sure there can only be one instance of a scene at a time
 *
 * @author Vlad Alboiu
 * @version 1.0.1
 */
public enum Scenes {
    LANDING("/fxml/landing_page.fxml", new LandingPageController()) {
        @Override
        void onSceneEnter() {
        }
    },
    TICKET_CHECK("/fxml/check.fxml", new CheckController()) {
        @Override
        void onSceneEnter() {
            ((CheckController) controller).setTicketMode();
            ((CheckController) controller).setup();
        }
    },
    SMARTCARD_CHECK("/fxml/check.fxml", new CheckController()) {
        @Override
        void onSceneEnter() {
            ((CheckController) controller).setSmartcardMode();
            ((CheckController) controller).setup();
        }
    },
    PAYMENT_METHODS("/fxml/payment_methods.fxml", new PaymentMethodsController()) {
        @Override
        void onSceneEnter() {
            ((PaymentMethodsController) controller).setup();
        }
    },
    PAYMENT_METHODS_CASH("/fxml/payment_methods_cash.fxml", new PaymentMethodsCashController()) {
        @Override
        void onSceneEnter() {
            ((PaymentMethodsCashController) controller).setup();
        }
    },
    PAYMENT_METHODS_CONTACTLESS("/fxml/payment_methods_contactless.fxml", new PaymentMethodsContactlessController()) {
        @Override
        void onSceneEnter() {
            ((PaymentMethodsContactlessController) controller).setup();
        }
    },
    FINISH("/fxml/finish.fxml", new FinishController()) {
        @Override
        void onSceneEnter() {
            ((FinishController) controller).setup();
        }
    };

    Object controller;
    String fxmlLocation;
    AnchorPane root;

    /**
     * The constructor takes as a parameter the location of the FXML file
     * found in the resources folder and a new  instance of a controller
     *
     * @param fxmlLocation the location of the fxml file
     * @param controller   the instance of the controller to be attached
     */
    Scenes(String fxmlLocation, Object controller) {
        this.fxmlLocation = fxmlLocation;
        this.controller = controller;
        initialise();
    }

    /**
     * Method that is called before returning the scene
     * <p>
     * This is called in {@link #getScene()}
     */
    abstract void onSceneEnter();

    /**
     * Method that gets called in the constructor
     * to prepare all the necessary scenes
     */
    private void initialise() {
        FXMLLoader loader = new FXMLLoader(getURLResource());
        loader.setController(controller);
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that gets an URL pointing to the resources folder with the file path
     *
     * @return resource URL
     */
    protected URL getURLResource() {
        return getClass().getResource(fxmlLocation);
    }

    /**
     * Method that returns the controller object attached to the screen.
     * Note: the returned data is of type Object. Make sure to cast the object to the controller type before using any methods
     *
     * @return the controller as Object
     */
    public Object getController() {
        return controller;
    }

    /**
     * This method loads the FXML file and sets to the loader the controller instance received from {@link #getController()} .
     * Every time this function is called, {@link #initialise()} is called to prepare everything before the UI is being returned
     *
     * @return the root anchor pane of the loaded UI
     */
    public AnchorPane getScene() {
        onSceneEnter();
        return root;
    }

}
