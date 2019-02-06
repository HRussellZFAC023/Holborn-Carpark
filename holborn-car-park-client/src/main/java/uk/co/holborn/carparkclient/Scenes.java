package uk.co.holborn.carparkclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import uk.co.holborn.carparkclient.controllers.LandingPageController;
import uk.co.holborn.carparkclient.controllers.TicketCheckController;

import java.io.IOException;
import java.net.URL;

public enum Scenes {
    LANDING {
        LandingPageController controller = new LandingPageController();
        AnchorPane root;
        @Override
        URL getURLPath() {return getClass().getResource("/fxml/landing_page.fxml");
        }
        @Override
        Object getController() { return controller; }
        @Override
        public AnchorPane getRootPane() { if (root == null) root = getPaneFromFXMLLoader();return root; }

    },
    TICKET_CHECK {
        TicketCheckController controller = new TicketCheckController();
        AnchorPane root;
        @Override
        URL getURLPath() {return getClass().getResource("/fxml/ticket_check.fxml");
        }
        @Override
        Object getController() { return controller; }
        @Override
        public AnchorPane getRootPane() { if (root == null) root = getPaneFromFXMLLoader();return root; }
    },
    PAYMENT_METHODS {
        LandingPageController controller = new LandingPageController();
        AnchorPane root;

        @Override
        URL getURLPath() {
            return getClass().getResource("/fxml/landing_page.fxml");

        }

        @Override
        Object getController() {
            return null;
        }

        @Override
        public AnchorPane getRootPane() {
            FXMLLoader loader = new FXMLLoader(getURLPath());
            // controller = new LandingPageController();
            loader.setController(controller);
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return root;
        }
    },
    PAYMENT_METHOD_CASH {
        LandingPageController controller = new LandingPageController();
        AnchorPane root;

        @Override
        URL getURLPath() {
            return getClass().getResource("/fxml/landing_page.fxml");

        }

        @Override
        Object getController() {
            return null;
        }

        @Override
        public AnchorPane getRootPane() {
            FXMLLoader loader = new FXMLLoader(getURLPath());
            // controller = new LandingPageController();
            loader.setController(controller);
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return root;
        }


    };

    abstract URL getURLPath();

    abstract Object getController();

    public abstract AnchorPane getRootPane();

    public AnchorPane getPaneFromFXMLLoader() {
        AnchorPane root = null;
        FXMLLoader loader = new FXMLLoader(getURLPath());
        loader.setController(getController());
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }

}
