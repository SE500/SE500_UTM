/*******************************************************************************
 * 2016, All rights reserved.
 *******************************************************************************/

// Start of user code (user defined imports)

// End of user code

/**
 * Description of GlobalAddress.
 * 
 * @author 0924
 */
public class GlobalAddress extends AddressGlobalLocation {
	/**
	 * Description of the property location.
	 */
	public String location = "";

	// Start of user code (user defined attributes for GlobalAddress)

	// End of user code

	/**
	 * The constructor.
	 */
	public GlobalAddress() {
		// Start of user code constructor for GlobalAddress)
		super();
		// End of user code
	}

	// Start of user code (user defined methods for GlobalAddress)

	// End of user code
	/**
	 * Returns location.
	 * @return location 
	 */
	public String getLocation() {
		return this.location;
	}

	/**
	 * Sets a value to attribute location. 
	 * @param newLocation 
	 */
	public void setLocation(String newLocation) {
		this.location = newLocation;
	}

}
