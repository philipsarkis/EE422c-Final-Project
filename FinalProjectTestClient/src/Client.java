
import java.util.Scanner;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
  private boolean loginOK = false;
  TableView<Product> table;
  Runnable setTableInfo;
  private String bidTooLow;
  Runnable setBidTooLowMsg;
  Runnable setBidHistory;

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
	 if(input.contains("bid is too low")) {
		 bidTooLow = g.fromJson(input, String.class);
		 Platform.runLater(() -> {
				this.setBidTooLowMsg.run(); 
			 });
	 } else {
		 if(loginOK) {
			 clientProductList = g.fromJson(input, new TypeToken<ArrayList<Product>>() {}.getType()); 
			 Platform.runLater(() -> {
				this.setTableInfo.run(); 
				//this.setBidHistory.run();	
			 }); 
		 }
	 }


	  
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
		GridPane.setConstraints(logoutButton, 1500, 700);
		logoutButton.setOnAction(e -> window.setScene(loginScene));
		
		TextField bidInput = new TextField();
		loginInput.setPromptText("Enter Bid Amount");
		GridPane.setConstraints(bidInput, 1, 0);
		
		Button bidButton = new Button("Place Bid!");
		GridPane.setConstraints(bidButton, 2, 0);
		
		window.setScene(loginScene);
		window.show();
		
		login.setOnAction(e -> {
			loginOK = true;
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
			
			Label productSelected = new Label("");
			GridPane.setConstraints(productSelected, 1, 10);
			
			String choices[] = new String[5];
			
			for(int i = clientProductList.size()-1; i > -1; i--) {
				choices[i] = clientProductList.get(i).product;
			}
			
		
			
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
			
			TableColumn<Product, Boolean> soldColumn = new TableColumn<>("Sold?");
			soldColumn.setMinWidth(100);
			soldColumn.setCellValueFactory(new PropertyValueFactory<>("sold"));
			
			Text lowBid = new Text();
			lowBid.setText("");
			GridPane.setConstraints(lowBid, 0, 10);
		
			
			table = new TableView<>();
			Platform.runLater(() -> {
				table.setItems(getProduct());			
			});

			this.setBidTooLowMsg = new Runnable() {
				@Override
				public void run() {
					lowBid.setText(bidTooLow);
				}
			};
			
			Label bidHistoryText = new Label("");
			GridPane.setConstraints(bidHistoryText, 1, 5);
			
			choiceBox.setValue(clientProductList.get(0).product);
			choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue obs, Number value, Number new_value) {
					if(new_value.equals(0)) {
						bidHistoryText.setText("");
						bidButton.setDisable(false);
						if(clientProductList.get(0).sold == true) 
							bidButton.setDisable(true);
						productSelected.setText("This will keep you dry on a rainy day," + "\n" + "as long as you make sure not to leave it at home!");
						lowBid.setText("");
					}
					if(new_value.equals(1)) {
						bidHistoryText.setText("");
						bidButton.setDisable(false);
						if(clientProductList.get(1).sold == true) 
							bidButton.setDisable(true);
						productSelected.setText("Part of a nutritious everyday breakfast!" + "\n" + "(may or may not be nutritious)");
						lowBid.setText("");
					}
					if(new_value.equals(2)) {
						bidHistoryText.setText("");
						bidButton.setDisable(false);
						if(clientProductList.get(2).sold == true) 
							bidButton.setDisable(true);
						productSelected.setText("An overhyped piece of wood!" + "\n" + "Put your stuff on it!");
						lowBid.setText("");
					}
					if(new_value.equals(3)) {
						bidHistoryText.setText("");
						bidButton.setDisable(false);
						if(clientProductList.get(3).sold == true) 
							bidButton.setDisable(true);
						productSelected.setText("Warranty not included");
						lowBid.setText("");
					}
					if(new_value.equals(4)) {
						bidHistoryText.setText("");
						bidButton.setDisable(false);
						if(clientProductList.get(4).sold == true) 
							bidButton.setDisable(true);
						productSelected.setText("In times like these, no price is too high!");
						lowBid.setText("");
					}
					//productSelected.setText(choices[new_value.intValue()] + " selected");
				}
			});
			
			this.setBidHistory = new Runnable() {
				@Override
				public void run() {
					String s = new String();
					for(int a = 0; a < clientProductList.size(); a++) {
						if(choiceBox.getValue().equals(clientProductList.get(a).getProduct())) {
							if(clientProductList.get(a).getBidBefore() == true) {
								for(int x = 0; x < clientProductList.get(a).getBidHistory().size(); x++) {
									s = s + clientProductList.get(a).getBidHistory().get(x).toString() + "\n";
								}
							}
						}
					}
					bidHistoryText.setText(s);


				}
			};
			
			Button viewBidHistory = new Button("View History");
			GridPane.setConstraints(viewBidHistory, 0, 2);
			viewBidHistory.setOnAction(g -> {
				String t = new String();
				for(int a = 0; a < clientProductList.size(); a++) {
					if(choiceBox.getValue().equals(clientProductList.get(a).getProduct())) {
						if(clientProductList.get(a).getBidBefore() == true) {
							for(int x = 0; x < clientProductList.get(a).getBidHistory().size(); x++) {
								t = t + clientProductList.get(a).getBidHistory().get(x).toString() + "\n";
								if(clientProductList.get(a).getBidHistory().get(x).getAmount() > clientProductList.get(a).getBuyNow()) {
									t = t + "Sold!!!" + "\n";
								}
							}
						}
					}
				}
				bidHistoryText.setText(t);
			});
			
			table.getColumns().addAll(productColumn, bidColumn, bidderColumn, buyNowColumn, timeColumn, soldColumn);
			GridPane.setConstraints(table, 15, 0);
			
			GridPane layout2 = new GridPane();
			layout2.getChildren().addAll(logoutButton, choiceBox, bidInput, bidButton, table, lowBid, productSelected, bidHistoryText, viewBidHistory);
			Scene auctionScene = new Scene(layout2, 1500, 800);
			
			bidButton.setOnAction(f -> {
				String bp = choiceBox.getValue();
				Double buyNowPrice = 0.0;
				if(bidInput.getText() != null) {
					Bid br = new Bid(bp, Double.parseDouble(bidInput.getText()));
					for(int z = 0; z < clientProductList.size(); z++) {
						if(br.getItem().contentEquals(clientProductList.get(z).getProduct())) {
							 buyNowPrice = clientProductList.get(z).getBuyNow();
						}
					}
					if(br.getAmount() > buyNowPrice) {
						bidButton.setDisable(true);
					}
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
					soldColumn.setCellValueFactory(new PropertyValueFactory<>("sold"));
					for(int z = 0; z < clientProductList.size(); z++) {
						if(choiceBox.getValue().equals(clientProductList.get(z).getProduct())) {
							if(clientProductList.get(z).getSold()) {
								bidButton.setDisable(true);
							}
						}

					}
					table.setItems(getProduct());
		        	table.getColumns().addAll(productColumn, bidColumn, bidderColumn, buyNowColumn, timeColumn, soldColumn);
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