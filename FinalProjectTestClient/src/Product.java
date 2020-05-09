import java.util.ArrayList;

public class Product{
	String product;
	double bid;
	String highestBidder;
	double buyNow;
	int time;
	boolean sold;
	ArrayList<Bid> bidHistory;
	
	public Product() {
	    this.product = "";
	    this.bid = 0.0;
	    this.highestBidder = "";
	    this.buyNow = 0;
	    this.time = 100;
	  }

	  public Product(String input, double number, double buyPrice, int timer) {
	    this.product = input;
	    this.bid = number;
	    this.buyNow = buyPrice;
	    this.highestBidder = "No one Yet";
	    this.time = timer;
	  }
	  
	  public Product(String input, double number, String name, double buyPrice, int timer) {
		    this.product = input;
		    this.bid = number;
		    this.buyNow = buyPrice;
		    this.highestBidder = name;
		    this.time = timer;
	  }
	  
	  public void setBid(double bidAmount) {
		  bid = bidAmount;
	  }
	  
	  public void setBidder(String bidderName) {
		  highestBidder = bidderName;
	  }
	  
	  public void setBuyNow(double instaBuy) {
		 buyNow = instaBuy;
	  }
	  
	  public void setTime(int timer) {
		  time = timer;
	  }
	  
	  public String getProduct() {
		  return product;
	  }
	  public Double getBid() {
		  return bid;
	  }
	  public String getHighestBidder() {
		  return highestBidder;
	  }
	  public Double getBuyNow() {
		  return buyNow;
	  }
	  public int getTime() {
		  return time;
	  }
}
