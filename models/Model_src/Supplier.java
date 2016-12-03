/*******************************************************************************
 * 2016, All rights reserved.
 *******************************************************************************/

import java.util.HashSet;
// Start of user code (user defined imports)

// End of user code

/**
 * Description of Supplier.
 * 
 * @author 0924
 */
public class Supplier {
	/**
	 * Description of the property name.
	 */
	public String name = "";

	/**
	 * Description of the property orders.
	 */
	public HashSet<PurchaseOrder> orders = new HashSet<PurchaseOrder>();

	/**
	 * Description of the property pendingOrders.
	 */
	public HashSet<PurchaseOrder> pendingOrders = new HashSet<PurchaseOrder>();

	/**
	 * Description of the property shippedOrders.
	 */
	public HashSet<PurchaseOrder> shippedOrders = new HashSet<PurchaseOrder>();

	/**
	 * Description of the property customers.
	 */
	public HashSet<Customer> customers = new HashSet<Customer>();

	// Start of user code (user defined attributes for Supplier)

	// End of user code

	/**
	 * The constructor.
	 */
	public Supplier() {
		// Start of user code constructor for Supplier)
		super();
		// End of user code
	}

	// Start of user code (user defined methods for Supplier)

	// End of user code
	/**
	 * Returns name.
	 * @return name 
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets a value to attribute name. 
	 * @param newName 
	 */
	public void setName(String newName) {
		this.name = newName;
	}

	/**
	 * Returns orders.
	 * @return orders 
	 */
	public HashSet<PurchaseOrder> getOrders() {
		return this.orders;
	}

	/**
	 * Returns pendingOrders.
	 * @return pendingOrders 
	 */
	public HashSet<PurchaseOrder> getPendingOrders() {
		return this.pendingOrders;
	}

	/**
	 * Returns shippedOrders.
	 * @return shippedOrders 
	 */
	public HashSet<PurchaseOrder> getShippedOrders() {
		return this.shippedOrders;
	}

	/**
	 * Returns customers.
	 * @return customers 
	 */
	public HashSet<Customer> getCustomers() {
		return this.customers;
	}

}
