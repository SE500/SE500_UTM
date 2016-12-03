/*******************************************************************************
 * 2016, All rights reserved.
 *******************************************************************************/

import java.util.HashSet;
// Start of user code (user defined imports)

// End of user code

/**
 * Description of Customer.
 * 
 * @author 0924
 */
public class Customer {
	/**
	 * Description of the property customerID.
	 */
	public int customerID = 0;

	/**
	 * Description of the property orders.
	 */
	public HashSet<PurchaseOrder> orders = new HashSet<PurchaseOrder>();

	// Start of user code (user defined attributes for Customer)

	// End of user code

	/**
	 * The constructor.
	 */
	public Customer() {
		// Start of user code constructor for Customer)
		super();
		// End of user code
	}

	// Start of user code (user defined methods for Customer)

	// End of user code
	/**
	 * Returns customerID.
	 * @return customerID 
	 */
	public int getCustomerID() {
		return this.customerID;
	}

	/**
	 * Sets a value to attribute customerID. 
	 * @param newCustomerID 
	 */
	public void setCustomerID(int newCustomerID) {
		this.customerID = newCustomerID;
	}

	/**
	 * Returns orders.
	 * @return orders 
	 */
	public HashSet<PurchaseOrder> getOrders() {
		return this.orders;
	}

}
