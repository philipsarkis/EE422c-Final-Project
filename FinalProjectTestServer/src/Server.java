import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

import com.google.gson.Gson;

class Server extends Observable {
	private ArrayList<Product> productList = new ArrayList<Product>();

  public static void main(String[] args) {
    new Server().runServer();
  }

  private void runServer() {
    try {
    	productList.add(new Product("Umbrella", 0.0, "No One Yet"));
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
    					System.out.println(bid.bidderName);
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
    		output = gson.toJson(j);
    		this.setChanged();
          	this.notifyObservers(output);
    	    } catch (Exception e) {
    	      e.printStackTrace();
    	    }
    }
    
  }

}