
import java.util.Scanner;
import com.google.gson.reflect.TypeToken;

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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Client extends Application{
  private static ArrayList<Product> clientProductList = new ArrayList<Product>();
  private static String host = "127.0.0.1";
  private BufferedReader fromServer;
  private PrintWriter toServer;
  private Scanner consoleInput = new Scanner(System.in);
  private static String clientName;
  private int loginOK = 0;
  TableView<Product> table;
  Runnable setTableInfo;


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
//          System.out.println(request);
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
	 clientProductList = g.fromJson(input, new TypeToken<ArrayList<Product>>() {}.getType()); 
	 Platform.runLater(() -> {
		this.setTableInfo.run(); 
	 });

	  
	 // clientProductList.add(g.fromJson(input, Product.class));
  }

  protected void sendToServer(String string) {
    System.out.println("Sending to server: " + string);
    toServer.println(string);
    toServer.flush();
  }
  
  public void start(Stage primaryStage) {
	  try {
	      this.setUpNetworking();
	      
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
		GridPane.setConstraints(logoutButton, 950, 425);
		logoutButton.setOnAction(e -> window.setScene(loginScene));
		
		TextField bidInput = new TextField();
		loginInput.setPromptText("Enter Bid Amount");
		GridPane.setConstraints(bidInput, 1, 0);
		
		Button bidButton = new Button("Place Bid!");
		GridPane.setConstraints(bidButton, 2, 0);
		
		window.setScene(loginScene);
		window.show();
		
		login.setOnAction(e -> {
			String s = loginInput.getText();
			GsonBuilder builder1 = new GsonBuilder();
	        Gson gson1 = builder1.create();
	        sendToServer(gson1.toJson(s));
	        try {
				Thread.sleep(250);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			clientName = s;
			//System.out.println(clientName);
			//System.out.println(clientProductList.size());
			ChoiceBox<String> choiceBox = new ChoiceBox<>();
			for(int i = 0; i < clientProductList.size(); i++) {
				choiceBox.getItems().add(clientProductList.get(i).product);
			}
			
		//	for(int i = 0; i < clientProductList.size(); i++) {
		//		  System.out.println(clientProductList.get(i).product);
		//		  System.out.println(clientProductList.get(i).bid);
		//		  System.out.println(clientProductList.get(i).highestBidder);
		//	  }
			
			choiceBox.setValue(clientProductList.get(0).product);
			GridPane.setConstraints(choiceBox, 0, 0);
			
			
			
			TableColumn<Product, String> productColumn = new TableColumn<>("Product");
			productColumn.setMinWidth(200);
			productColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
			
			TableColumn<Product, Double> bidColumn = new TableColumn<>("Bid");
			bidColumn.setMinWidth(100);
			bidColumn.setCellValueFactory(new PropertyValueFactory<>("bid"));

			TableColumn<Product, Double> bidderColumn = new TableColumn<>("Highest Bidder");
			bidderColumn.setMinWidth(100);
			bidderColumn.setCellValueFactory(new PropertyValueFactory<>("highestBidder"));
			
			TableColumn<Product, Double> buyNowColumn = new TableColumn<>("Buy It Now");
			buyNowColumn.setMinWidth(100);
			buyNowColumn.setCellValueFactory(new PropertyValueFactory<>("buyNow"));
			
			TableColumn<Product, Integer> timeColumn = new TableColumn<>("Time Remaining");
			timeColumn.setMinWidth(100);
			timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
		
			
			table = new TableView<>();
			Platform.runLater(() -> {
				table.setItems(getProduct());			
			});

			table.getColumns().addAll(productColumn, bidColumn, bidderColumn, buyNowColumn, timeColumn);
			GridPane.setConstraints(table, 15, 0);
			
			GridPane layout2 = new GridPane();
			layout2.getChildren().addAll(logoutButton, choiceBox, bidInput, bidButton, table);
			Scene auctionScene = new Scene(layout2, 1000, 500);
			
			bidButton.setOnAction(f -> {
				String bp = choiceBox.getValue();
				if(bidInput.getText() != null) {
					Bid br = new Bid(bp, Double.parseDouble(bidInput.getText()));
					br.setName(clientName);
			         GsonBuilder builder2 = new GsonBuilder();
			          Gson gson2 = builder2.create();
			          sendToServer(gson2.toJson(br));
				}
			});	
			
			this.setTableInfo = new Runnable() {
				@Override
				public void run() {
					table.getColumns().clear();
					timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
					buyNowColumn.setCellValueFactory(new PropertyValueFactory<>("buyNow"));
					bidderColumn.setCellValueFactory(new PropertyValueFactory<>("highestBidder"));
					bidColumn.setCellValueFactory(new PropertyValueFactory<>("bid"));
					productColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
					table.setItems(getProduct());
		        	table.getColumns().addAll(productColumn, bidColumn, bidderColumn, buyNowColumn, timeColumn);
				}
			};
		
			
			
			window.setScene(auctionScene);
		});

		
		
		
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
  
  public ObservableList<Product> getProduct(){
	  ObservableList<Product> productList = FXCollections.observableArrayList();
	  for(int i = 0; i < clientProductList.size(); i++) {
		  productList.add(clientProductList.get(i));
	  }
	  return productList;
  }
  



}