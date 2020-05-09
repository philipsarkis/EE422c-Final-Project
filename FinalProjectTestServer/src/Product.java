import java.util.ArrayList;

public class Product{
	String product;
	double bid;
	String highestBidder;
	double buyNow;
	int time;
	boolean sold;
	ArrayList<Bid> bidHistory = new ArrayList<Bid>();
	boolean bidBefore;
	
	public Product() {
	    this.product = "";
	    this.bid = 0.0;
	    this.highestBidder = "";
	    this.buyNow = 0;
	    this.time = 100;
	    this.sold = false;
	    this.bidBefore = false;
	    this.bidHistory = new ArrayList<Bid>();
	  }

	  public Product(String input, double number, double buyPrice, int timer) {
	    this.product = input;
	    this.bid = number;
	    this.buyNow = buyPrice;
	    this.highestBidder = "No one Yet";
	    this.time = timer;
	    this.sold = false;
	    this.bidBefore = false;
	    this.bidHistory = new ArrayList<Bid>();
	    
	  }
	  
	  public Product(String input, double number, String name, double buyPrice, int timer) {
		    this.product = input;
		    this.bid = number;
		    this.buyNow = buyPrice;
		    this.highestBidder = name;
		    this.time = timer;
		    this.sold = false;
		    this.bidBefore = false;
		    this.bidHistory = new ArrayList<Bid>();
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
	  public void setSold(boolean soldYet) {
		  sold = soldYet;
	  }
	  
	  public void setBidBefore(boolean bidYet) {
		  bidBefore = bidYet;
	  }
	  
	  public void setBidHistory(Bid b) {
		  bidHistory.add(b);
	  }
	  
	  public ArrayList<Bid> getBidHistory() {
		  return bidHistory;
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
	  public boolean getSold() {
		  return sold;
	  }
	  public boolean getBidBefore() {
		  return bidBefore;
	  }
}
