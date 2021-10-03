package jsmahy.ups_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jsmahy.ups_client.net.Client;
import jsmahy.ups_client.net.Packet;

import java.io.IOException;
import java.util.Scanner;

/**
 * The type Hello application.
 */
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) throws IOException {
        Client c = new Client("127.0.0.1", 5000);
        Thread t = new Thread(c);
        t.setDaemon(true);
        t.start();

        Scanner sc = new Scanner(System.in);

        String line = "";

        while(!line.equalsIgnoreCase("exit")){
            line = sc.nextLine();
            String finalLine = line;
            String recv = c.sendPacket(new Packet() {
                @Override
                public String getMessage() {
                    return finalLine;
                }
            });
            System.out.println("Recvd: " + recv);
        }
        c.disconnect();
        //launch();
    }
}