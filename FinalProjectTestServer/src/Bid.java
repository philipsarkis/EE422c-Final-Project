class Bid {
  String item;
  Double amount;
  String bidderName;
  protected Bid() {
    this.item = "";
    this.amount = 0.0;
  }

  protected Bid(String input, Double number) {
    this.item = input;
    this.amount = number;
  }
  
  protected Bid(String input, Double number, String name) {
	    this.item = input;
	    this.amount = number;
	    bidderName = name;
	  }
  
  protected Double getAmount() {
	  return amount;
  }
  
  protected String getItem() {
	  return item;
  }
  
  protected String getName() {
	  return bidderName;
  }
  
  
  public String toString() {
	  return item + "'s current bid is at : $" + amount;
  }
}