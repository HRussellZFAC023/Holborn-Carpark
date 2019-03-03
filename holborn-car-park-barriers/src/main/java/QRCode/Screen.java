import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Screen extends Application {

    //https://www.thonky.com/qr-code-tutorial/introduction
    public static void main(String[] args) {
     launch();
    }

    @Override
    public void start(Stage mainStage){
        GridPane gp = new GridPane();
        Scene scene = new Scene(gp);
        mainStage.setScene(scene);

        int magicBoxSizeSpacing = 5;//Set the spacing of the boxes
        int max = 4;

        HBox rPad = new HBox();//Spacing away from the sides
        rPad.setPrefWidth(magicBoxSizeSpacing);
        gp.add(rPad, max, 0);
        HBox lPad = new HBox();
        lPad.setPrefWidth(magicBoxSizeSpacing);
        gp.add(lPad, 0, 0);
        VBox tPad = new VBox();
        tPad.setPrefHeight(magicBoxSizeSpacing);
        gp.add(tPad, 0, 0);
        VBox bPad = new VBox();
        bPad.setPrefHeight(magicBoxSizeSpacing);
        gp.add(bPad, max, max);

        TextField inputArea = new TextField("Hello world");
        gp.add(inputArea,1,1, 2,1);

        Label pixelSizeL = new Label("PixelSize:");
        gp.add(pixelSizeL,1,3);

        ComboBox<Integer> pixelSize = new ComboBox<>();
        pixelSize.getItems().addAll(1,2,3,4,5,6,7,8,9,10);
        pixelSize.setValue(10);
        gp.add(pixelSize,2,3);

        Label errLvlL = new Label("Error correction level:");
        gp.add(errLvlL,1,2);

        ComboBox<Integer> errLvlBox = new ComboBox<>();
        errLvlBox.getItems().addAll(1,2,3,4);
        errLvlBox.setValue(3);
        gp.add(errLvlBox,2,2);

        QRCode code = new QRCode();
        gp.add(code,3,1,10,10);

        Button generateButton = new Button("Generate");
        generateButton.setOnAction(event ->
                code.generate(inputArea.getText(), 2, errLvlBox.getValue(), pixelSize.getValue())
        );
        gp.add(generateButton,0,1);

        mainStage.setWidth(800);
        mainStage.setHeight(600);

        mainStage.show();
    }
}
