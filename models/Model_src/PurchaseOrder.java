/*******************************************************************************
 * 2016, All rights reserved.
 *******************************************************************************/

import java.util.Date;
import java.util.HashSet;
// Start of user code (user defined imports)

// End of user code

/**
 * Description of PurchaseOrder.
 * 
 * @author 0924
 */
public class PurchaseOrder {
	/**
	 * Description of the property comment.
	 */
	public String comment = "";

	/**
	 * Description of the property orderDate.
	 */
	public Date orderDate = new Date();

	/**
	 * Description of the property status.
	 */
	public OrderStatus status = null;

	/**
	 * Description of the property totalAmount.
	 */
	public int totalAmount = 0;

	/**
	 * Description of the property customer.
	 */
	public Customer customer = null;

	/**
	 * Description of the property previousOrder.
	 */
	public PurchaseOrder previousOrder = null;

	/**
	 * Description of the property items.
	 */
	public HashSet<Item> items = new HashSet<Item>();

	/**
	 * Description of the property billTo.
	 */
	public Address billTo = null;

	/**
	 * Description of the property shipTo.
	 */
	public Address shipTo = null;

	// Start of user code (user defined attributes for PurchaseOrder)

	// End of user code

	/**
	 * The constructor.
	 */
	public PurchaseOrder() {
		// Start of user code constructor for PurchaseOrder)
		super();
		// End of user code
	}

	// Start of user code (user defined methods for PurchaseOrder)

	// End of user code
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
	 * Returns orderDate.
	 * @return orderDate 
	 */
	public Date getOrderDate() {
		return this.orderDate;
	}

	/**
	 * Sets a value to attribute orderDate. 
	 * @param newOrderDate 
	 */
	public void setOrderDate(Date newOrderDate) {
		this.orderDate = newOrderDate;
	}

	/**
	 * Returns status.
	 * @return status 
	 */
	public OrderStatus getStatus() {
		return this.status;
	}

	/**
	 * Sets a value to attribute status. 
	 * @param newStatus 
	 */
	public void setStatus(OrderStatus newStatus) {
		this.status = newStatus;
	}

	/**
	 * Returns totalAmount.
	 * @return totalAmount 
	 */
	public int getTotalAmount() {
		return this.totalAmount;
	}

	/**
	 * Sets a value to attribute totalAmount. 
	 * @param newTotalAmount 
	 */
	public void setTotalAmount(int newTotalAmount) {
		this.totalAmount = newTotalAmount;
	}

	/**
	 * Returns customer.
	 * @return customer 
	 */
	public Customer getCustomer() {
		return this.customer;
	}

	/**
	 * Sets a value to attribute customer. 
	 * @param newCustomer 
	 */
	public void setCustomer(Customer newCustomer) {
		this.customer = newCustomer;
	}

	/**
	 * Returns previousOrder.
	 * @return previousOrder 
	 */
	public PurchaseOrder getPreviousOrder() {
		return this.previousOrder;
	}

	/**
	 * Sets a value to attribute previousOrder. 
	 * @param newPreviousOrder 
	 */
	public void setPreviousOrder(PurchaseOrder newPreviousOrder) {
		if (this.previousOrder != null) {
			this.previousOrder.set(null);
		}
		this.previousOrder.set(this);
	}

	/**
	 * Returns items.
	 * @return items 
	 */
	public HashSet<Item> getItems() {
		return this.items;
	}

	/**
	 * Returns billTo.
	 * @return billTo 
	 */
	public Address getBillTo() {
		return this.billTo;
	}

	/**
	 * Sets a value to attribute billTo. 
	 * @param newBillTo 
	 */
	public void setBillTo(Address newBillTo) {
		if (this.billTo != null) {
			this.billTo.set(null);
		}
		this.billTo.set(this);
	}

	/**
	 * Returns shipTo.
	 * @return shipTo 
	 */
	public Address getShipTo() {
		return this.shipTo;
	}

	/**
	 * Sets a value to attribute shipTo. 
	 * @param newShipTo 
	 */
	public void setShipTo(Address newShipTo) {
		if (this.shipTo != null) {
			this.shipTo.set(null);
		}
		this.shipTo.set(this);
	}

}
