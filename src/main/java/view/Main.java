/*
author :
    1. Tesalonika Miracle Makalew - 825250020
    2. Riza Rosmeri - 825250153
    3. Vionanda Ginting - 825250018
*/

package main.java.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/login_form.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        var css = getClass().getResource("/main/resources/css/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        stage.setTitle("Kopi Kenangan");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
