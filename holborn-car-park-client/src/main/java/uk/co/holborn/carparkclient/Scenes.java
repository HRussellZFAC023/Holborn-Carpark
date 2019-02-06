package uk.co.holborn.carparkclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import uk.co.holborn.carparkclient.controllers.LandingPageController;
import uk.co.holborn.carparkclient.controllers.PaymentMethodsController;
import uk.co.holborn.carparkclient.controllers.TicketCheckController;

import java.io.IOException;
import java.net.URL;

public enum Scenes {
    LANDING {
        LandingPageController controller = new LandingPageController();
        AnchorPane root;

        @Override
        String getFXMLLocation() {
            return "/fxml/landing_page.fxml";
        }

        @Override
        Object getController() {
            return controller;
        }
        @Override
        AnchorPane getRootPane() {
            if (root == null) root = getPaneFromFXMLLoader();
            return root;
        }

        @Override
        void initialisation() {
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
        public AnchorPane getRootPane() {
            if (root == null) root = getPaneFromFXMLLoader();
            return root;
        }

        @Override
        Object getController() {
            return controller;
        }

        @Override
        void initialisation() {

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
        public AnchorPane getRootPane() {
            if (root == null) root = getPaneFromFXMLLoader();
            return root;
        }

        @Override
        Object getController() {
            return controller;
        }

        @Override
        void initialisation() {

        }
    };

    abstract String getFXMLLocation();

    abstract AnchorPane getRootPane();

    abstract Object getController();

    abstract void initialisation();

    protected URL getURLResource() {
        return getClass().getResource(getFXMLLocation());
    }
    protected AnchorPane getPaneFromFXMLLoader() {
        AnchorPane root = null;
        FXMLLoader loader = new FXMLLoader(getURLResource());
        loader.setController(getController());
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initialisation();
        return root;
    }

}
