class Product{
	String product;
	double bid;
	String highestBidder;
	
	protected Product() {
	    this.product = "";
	    this.bid = 0.0;
	    this.highestBidder = "";
	  }

	  protected Product(String input, double number) {
	    this.product = input;
	    this.bid = number;
	    this.highestBidder = "No one Yet";
	  }
	  
	  protected Product(String input, double number, String name) {
		    this.product = input;
		    this.bid = number;
		    this.highestBidder = name;
	  }
	  
	  public void setBid(double bidAmount) {
		  bid = bidAmount;
	  }
	  
	  public void setBidder(String bidderName) {
		  highestBidder = bidderName;
	  }

}
