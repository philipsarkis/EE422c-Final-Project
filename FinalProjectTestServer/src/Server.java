import java.io.File;
import java.net.ServerSocket;
import com.google.gson.reflect.TypeToken;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;

import com.google.gson.Gson;

class Server extends Observable {
	private ArrayList<Product> productList = new ArrayList<Product>();

  public static void main(String[] args) {
    new Server().runServer();
  }

  private void runServer() {
    try {
    	File f = new File("ProductList.txt");
        Scanner scan = new Scanner(f);
        while(scan.hasNextLine())
        {
            String string = scan.nextLine();
            String[] inputs = new String[3];
            inputs = string.split(",");
            productList.add(new Product(inputs[0], Double.parseDouble(inputs[1]), Double.parseDouble(inputs[2]), Integer.parseInt(inputs[3])));
            
        }
      setUpNetworking();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
  }

  private void setUpNetworking() throws Exception {
    @SuppressWarnings("resource")
    ServerSocket serverSock = new ServerSocket(4242);
    while (true) {
      Socket clientSocket = serverSock.accept();
      System.out.println("Connecting to... " + clientSocket);

      ClientHandler handler = new ClientHandler(this, clientSocket);
      this.addObserver(handler);

      Thread t = new Thread(handler);
      t.start();
    }
  }

  protected void processRequest(String input) {
    String output = "Error";
    ArrayList<Product> j = new ArrayList<Product>();
    Gson gson = new Gson();
    if(input.contains("amount")) {
    	Bid bid = gson.fromJson(input, Bid.class);
    	try {
    		for(int i = 0; i < productList.size(); i++) {
    			if(bid.item.equals(productList.get(i).product)) {
    				if(bid.amount > productList.get(i).bid) {
    					productList.get(i).setBid(bid.amount);
    					productList.get(i).setBidder(bid.bidderName);
    				}
    			}
    			j = productList;
    			output = gson.toJson(j);
    			
    	      }
    	      this.setChanged();
    	      this.notifyObservers(output);
    	    } catch (Exception e) {
    	      e.printStackTrace();
    	    }
    }
    else {
    	try {
    		System.out.println("welcome, " + input);
    		  
       		j = productList;
       		for(int i = 0; i < j.size(); i++) {
  			  System.out.println(j.get(i).product);
  			  System.out.println(j.get(i).bid);
  			  System.out.println(j.get(i).highestBidder);
  		  }
       		
       		j = productList;
			output = gson.toJson(j);
			
    		this.setChanged();
          	this.notifyObservers(output);
    	    } catch (Exception e) {
    	      e.printStackTrace();
    	    }
    }
    
  }

}