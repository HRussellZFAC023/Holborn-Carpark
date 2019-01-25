package uk.co.holborn.carparkclient;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class NotificationListViewCell extends ListCell<Notification> {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label id;
    @FXML
    private Label title;
    @FXML
    private Label dateCreated;


    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(Notification notification, boolean empty) {
        super.updateItem(notification, empty);

        if(empty || notification == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/fxml/notificationListViewCell.fxml"));
                mLLoader.setController(this);
                try {
                    mLLoader.load();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

            }
            title.setText(notification.getTitle());
            id.setText(notification.get_id());
            dateCreated.setText(notification.getCreated().toString());
            setText(null);
            setGraphic(anchorPane);
        }

    }
    public Label getTitle(){
        return title;
    }
}
