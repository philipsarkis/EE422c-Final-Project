
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Client extends Application{
	private ArrayList<Product> clientProductList = new ArrayList<Product>();
  private static String host = "127.0.0.1";
  private BufferedReader fromServer;
  private PrintWriter toServer;
  private Scanner consoleInput = new Scanner(System.in);
  private static String clientName;
  private int loginOK = 0;


  public static void main(String[] args) {

    launch(args);
  }

  private void setUpNetworking() throws Exception {
    @SuppressWarnings("resource")
    Socket socket = new Socket(host, 4242);
    System.out.println("Connecting to... " + socket);
    fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    toServer = new PrintWriter(socket.getOutputStream());

    Thread readerThread = new Thread(new Runnable() {
      @Override
      public void run() {
        String input;
        try {
          while ((input = fromServer.readLine()) != null) {
            System.out.println("From server: " + input);
            processRequest(input);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    Thread writerThread = new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          String input = consoleInput.nextLine();
          String[] variables = input.split(",");
          Bid request = new Bid(variables[0], Double.valueOf(variables[1]));
          request.setName(clientName);
          System.out.println(request);
          GsonBuilder builder = new GsonBuilder();
          Gson gson = builder.create();
          sendToServer(gson.toJson(request));
        }
      }
    });

    readerThread.start();
    writerThread.start();
  }

  protected void processRequest(String input) {
	  Gson g = new Gson();
	  clientProductList = g.fromJson(input, ArrayList.class);

  }

  protected void sendToServer(String string) {
    System.out.println("Sending to server: " + string);
    toServer.println(string);
    toServer.flush();
  }
  
  public void start(Stage primaryStage) {
	  try {
	      new Client().setUpNetworking();
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
		
		//Create a GridPane for the control panel and adjust its constraints
	  	Stage window = primaryStage;
	  	window.setTitle("EHills");
	  	
		GridPane loginWindow = new GridPane();
		loginWindow.setPadding(new Insets(10, 10, 10, 10));
		loginWindow.setVgap(8);
		loginWindow.setHgap(10);
		
		Label usernameLabel = new Label("Username:");
		GridPane.setConstraints(usernameLabel, 0, 0);
		//loginWindow.add(usernameLabel, 0, 0);
		
		TextField loginInput = new TextField();
		loginInput.setPromptText("Username");
		GridPane.setConstraints(loginInput, 1, 0);
		//loginWindow.add(usernameLabel, 1, 1);
		
		Button login = new Button("Login");
		GridPane.setConstraints(login, 0, 1);
		//loginWindow.add(usernameLabel, 2, 2);
		
		loginWindow.getChildren().addAll(usernameLabel, loginInput, login);
		Scene loginScene = new Scene(loginWindow, 300, 200);
		
		Button logoutButton = new Button("Logout");
		logoutButton.setOnAction(e -> window.setScene(loginScene));
		
		login.setOnAction(e -> {
			String s = loginInput.getText();
			GsonBuilder builder1 = new GsonBuilder();
	        Gson gson1 = builder1.create();
	        //sendToServer(gson1.toJson(s));
	        System.out.println("Sending to server: " + gson1.toJson(s));
	        toServer.println(gson1.toJson(s) + "\n");
	        toServer.flush();
			clientName = s;
			System.out.println(clientName);
			
			
			//window.setScene(auctionScene);
		});
		
		window.setScene(loginScene);
		window.show();
		
		
		
		//ChoiceBox<String> choiceBox = new ChoiceBox<>();
		//for(int i = 0; i < clientProductList.size(); i++) {
		//	choiceBox.getItems().add(clientProductList.get(i).product);
		//}
		//choiceBox.setValue(clientProductList.get(0).product);
		//
		///StackPane layout2 = new StackPane();
		//layout2.getChildren().addAll(logoutButton, choiceBox);
		//Scene auctionScene = new Scene(layout2, 1000, 500);
		

		

		
		
		
  }


}