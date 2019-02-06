package uk.co.holborn.carparkclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import uk.co.holborn.carparkclient.controllers.LandingPageController;
import uk.co.holborn.carparkclient.controllers.PaymentMethodsController;
import uk.co.holborn.carparkclient.controllers.TicketCheckController;

import java.io.IOException;
import java.net.URL;

/**
 * Scenes contains all of the scenes windows
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
        Object getController() {
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
        Object getController() {
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
    PAYMENT_METHODS {
        PaymentMethodsController controller = new PaymentMethodsController();
        AnchorPane root;

        @Override
        String getFXMLLocation() {
            return "/fxml/payment_methods.fxml";
        }

        @Override
        Object getController() {
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
    };


    abstract String getFXMLLocation();

    abstract Object getController();

    abstract AnchorPane getRootAnchor();

    abstract void setRootAnchor(AnchorPane root);

    abstract void initialise();

    protected URL getURLResource() {
        return getClass().getResource(getFXMLLocation());
    }

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
