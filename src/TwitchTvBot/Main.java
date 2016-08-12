package TwitchTvBot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("TwitchTvBot.fxml"));
        primaryStage.setTitle("Twitch ChatBot");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);

        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
