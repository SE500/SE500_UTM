/*******************************************************************************
 * 2016, All rights reserved.
 *******************************************************************************/

import java.util.Date;
// Start of user code (user defined imports)

// End of user code

/**
 * Description of Item.
 * 
 * @author 0924
 */
public class Item {
	/**
	 * Description of the property productName.
	 */
	public String productName = "";

	/**
	 * Description of the property quantity.
	 */
	public int quantity = 0;

	/**
	 * Description of the property usPrice.
	 */
	public int usPrice = 0;

	/**
	 * Description of the property comment.
	 */
	public String comment = "";

	/**
	 * Description of the property shipDate.
	 */
	public Date shipDate = new Date();

	/**
	 * Description of the property partNum.
	 */
	public SKU partNum = null;

	/**
	 * Description of the property order.
	 */
	public PurchaseOrder order = null;

	// Start of user code (user defined attributes for Item)

	// End of user code

	/**
	 * The constructor.
	 */
	public Item() {
		// Start of user code constructor for Item)
		super();
		// End of user code
	}

	// Start of user code (user defined methods for Item)

	// End of user code
	/**
	 * Returns productName.
	 * @return productName 
	 */
	public String getProductName() {
		return this.productName;
	}

	/**
	 * Sets a value to attribute productName. 
	 * @param newProductName 
	 */
	public void setProductName(String newProductName) {
		this.productName = newProductName;
	}

	/**
	 * Returns quantity.
	 * @return quantity 
	 */
	public int getQuantity() {
		return this.quantity;
	}

	/**
	 * Sets a value to attribute quantity. 
	 * @param newQuantity 
	 */
	public void setQuantity(int newQuantity) {
		this.quantity = newQuantity;
	}

	/**
	 * Returns usPrice.
	 * @return usPrice 
	 */
	public int getUsPrice() {
		return this.usPrice;
	}

	/**
	 * Sets a value to attribute usPrice. 
	 * @param newUsPrice 
	 */
	public void setUsPrice(int newUsPrice) {
		this.usPrice = newUsPrice;
	}

	/**
	 * Returns comment.
	 * @return comment 
	 */
	public String getComment() {
		return this.comment;
	}

	/**
	 * Sets a value to attribute comment. 
	 * @param newComment 
	 */
	public void setComment(String newComment) {
		this.comment = newComment;
	}

	/**
	 * Returns shipDate.
	 * @return shipDate 
	 */
	public Date getShipDate() {
		return this.shipDate;
	}

	/**
	 * Sets a value to attribute shipDate. 
	 * @param newShipDate 
	 */
	public void setShipDate(Date newShipDate) {
		this.shipDate = newShipDate;
	}

	/**
	 * Returns partNum.
	 * @return partNum 
	 */
	public SKU getPartNum() {
		return this.partNum;
	}

	/**
	 * Sets a value to attribute partNum. 
	 * @param newPartNum 
	 */
	public void setPartNum(SKU newPartNum) {
		this.partNum = newPartNum;
	}

	/**
	 * Returns order.
	 * @return order 
	 */
	public PurchaseOrder getOrder() {
		return this.order;
	}

	/**
	 * Sets a value to attribute order. 
	 * @param newOrder 
	 */
	public void setOrder(PurchaseOrder newOrder) {
		this.order = newOrder;
	}

}
