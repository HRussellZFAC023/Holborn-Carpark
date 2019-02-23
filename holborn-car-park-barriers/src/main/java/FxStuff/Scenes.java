package FxStuff;

import FxStuff.Controllers.LandingPageController;
import FxStuff.Controllers.MainViewController;
import FxStuff.Controllers.TicketPrintingController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

/**
 * FxStuff.Scenes class contains every scene definition of the UI,
 * making sure there can only be one instance of a scene at a time
 *
 * @author Vlad Alboiu
 * @author Cameron
 * @version 1.0
 */
public enum Scenes {
    LANDING {
        LandingPageController controller = new LandingPageController(MainViewController.getInstance());
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
    PRINT_TICKET {
        TicketPrintingController controller = new TicketPrintingController(MainViewController.getInstance());
        AnchorPane root;

        @Override
        String getFXMLLocation() {
            return "/fxml/printing_ticket.fxml";
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
            System.out.println("Hit it.");
            controller.getTicket();
        }
    }/*,
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
    }*/;

    /**
     * Abstraction of a method that returns the location
     * of the fxml file within resources folder
     * <p>
     * This is called in {@link #getURLResource()}
     *
     * @return a string
     */
    abstract String getFXMLLocation();

    /**
     * Abstraction of a method that returns Object
     * containing a reference to the controller
     * <p>
     * This is called in {@link #getScene()}
     *
     * @return the controller object
     */
    public abstract Object getController();

    /**
     * Abstraction of a method that returns an Anchor Pane
     * containing a reference to the root anchor pane
     * <p>
     * This is called in {@link #getScene()}
     *
     * @return the root anchor pane
     */
    abstract AnchorPane getRootAnchor();

    /**
     * Abstraction of a method that sets an Anchor Pane
     * containing a reference to the root anchor pane
     * <p>
     * This is called in {@link #getScene()}
     */
    abstract void setRootAnchor(AnchorPane root);

    /**
     * Abstraction of a method that does all the necessary
     * preparation in the controller before returning the root anchor pane
     */
    abstract void initialise();

    /**
     * Method returns an URL pointing to the resources folder with the file path returned by {@link #getFXMLLocation()}
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
        }
        initialise();
        return getRootAnchor();
    }

}