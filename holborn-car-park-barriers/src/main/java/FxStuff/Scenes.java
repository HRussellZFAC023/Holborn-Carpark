package FxStuff;

import FxStuff.Controllers.*;
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
    /**
     * Landing In scene
     */
    LANDING_IN("/fxml/landing_In_page.fxml", new LandingInPageController(MainViewController.getInstance())) {
        @Override
        void onSceneEnter() {
        }
    },
    /**
     * Landing Out scene
     */
    LANDING_OUT("/fxml/landing_Out_page.fxml", new LandingOutPageController(MainViewController.getInstance())) {
        @Override
        void onSceneEnter() {
        }
    },
    /**
     * Ticket check scene
     */
    TICKET_CHECK("/fxml/check.fxml", new CheckController(MainViewController.getInstance())) {
        @Override
        void onSceneEnter() {
            ((CheckController) controller).setTicketMode();
        }
    },
    /**
     * Print Ticket scene
     */
    PRINT_TICKET("/fxml/printing_ticket.fxml", new TicketPrintingController(MainViewController.getInstance())) {
        @Override
        void onSceneEnter() {
            ((TicketPrintingController)controller).getTicket();
        }
    };

    Object controller;
    private final String fxmlLocation;
    private AnchorPane root;

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
    private URL getURLResource() {
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
