package uk.co.holborn.carparkclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import uk.co.holborn.carparkclient.controllers.PaymentMethodsCashController;
import uk.co.holborn.carparkclient.controllers.LandingPageController;
import uk.co.holborn.carparkclient.controllers.PaymentMethodsController;
import uk.co.holborn.carparkclient.controllers.TicketCheckController;

import java.io.IOException;
import java.net.URL;

/**
 * Scenes class contains every scene definition of the UI,
 * making sure there can only be one instance of a scene at a time
 *
 * @author Vlad Alboiu
 * @version 1.0
 */
public enum Scenes {
    LANDING {
        LandingPageController controller = new LandingPageController();
        AnchorPane root;

        @Override
        String getFXMLLocation() {
            return "/fxml/landing_page.fxml";
        }

        @Override
        public Object getController() {
            return controller;
        }

        @Override
        AnchorPane getRootAnchor() {
            return root;
        }

        @Override
        void setRootAnchor(AnchorPane root) {
            this.root = root;
        }

        @Override
        void initialise() {
        }

    },
    TICKET_CHECK {
        TicketCheckController controller = new TicketCheckController();
        AnchorPane root;

        @Override
        String getFXMLLocation() {
            return "/fxml/ticket_check.fxml";
        }

        @Override
        public Object getController() {
            return controller;
        }

        @Override
        AnchorPane getRootAnchor() {
            return root;
        }

        @Override
        void setRootAnchor(AnchorPane root) {
            this.root = root;
        }

        @Override
        void initialise() {
            controller.setup();
        }
    },
    PAYMENT_METHODS {
        PaymentMethodsController controller = new PaymentMethodsController();
        AnchorPane root;

        @Override
        String getFXMLLocation() {
            return "/fxml/payment_methods.fxml";
        }

        @Override
        public Object getController() {
            return controller;
        }

        @Override
        AnchorPane getRootAnchor() {
            return root;
        }

        @Override
        void setRootAnchor(AnchorPane root) {
            this.root = root;
        }

        @Override
        void initialise() {

        }
    },
    PAYMENT_METHODS_CASH {
        PaymentMethodsCashController controller = new PaymentMethodsCashController();
        AnchorPane root;

        @Override
        String getFXMLLocation() {
            return "/fxml/payment_methods_cash.fxml";
        }

        @Override
        public Object getController() {
            return controller;
        }

        @Override
        AnchorPane getRootAnchor() {
            return root;
        }

        @Override
        void setRootAnchor(AnchorPane root) {
            this.root = root;
        }

        @Override
        void initialise() {
            controller.setup();
        }
    };
    /**
     * Method that gets the location
     * of the fxml file within resources folder
     * <p>
     * This is called in {@link #getURLResource()}
     *
     * @return a string
     */
    abstract String getFXMLLocation();

    /**
     * Method that gets an Object
     * containing a reference to the controller
     * <p>
     * This is called in {@link #getScene()}
     *
     * @return the controller object
     */
    public abstract Object getController();

    /**
     * Method that gets an Anchor Pane
     * containing a reference to the root anchor pane
     * <p>
     * This is called in {@link #getScene()}
     *
     * @return the root anchor pane
     */
    abstract AnchorPane getRootAnchor();

    /**
     * Method that sets an Anchor Pane
     * containing a reference to the root anchor pane
     * <p>
     * This is called in {@link #getScene()}
     * @param root set the AnchorPane to this
     */
    abstract void setRootAnchor(AnchorPane root);

    /**
     * Method that does all the necessary
     * preparation in the controller before returning the root anchor pane
     */
    abstract void initialise();

    /**
     * Method that gets an URL pointing to the resources folder with the file path returned by {@link #getFXMLLocation()}
     *
     * @return resource URL
     */
    protected URL getURLResource() {
        return getClass().getResource(getFXMLLocation());
    }

    /**
     * This method loads the FXML file and sets to the loader the controller instance received from {@link #getController()} .
     * Every time this function is called, {@link #initialise()} is called to prepare everything before the UI is being returned
     *
     * @return the root anchor pane of the loaded UI
     */
    public AnchorPane getScene() {
        if (getRootAnchor() == null) {
            FXMLLoader loader = new FXMLLoader(getURLResource());
            loader.setController(getController());
            try {
                setRootAnchor(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            initialise();
        }
        return getRootAnchor();
    }

}
