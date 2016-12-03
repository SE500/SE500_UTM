/*******************************************************************************
 * 2016, All rights reserved.
 *******************************************************************************/

// Start of user code (user defined imports)

// End of user code

/**
 * Description of USAddress.
 * 
 * @author 0924
 */
public class USAddress extends Address {
	/**
	 * Description of the property street.
	 */
	public String street = "";

	/**
	 * Description of the property city.
	 */
	public String city = "";

	/**
	 * Description of the property state.
	 */
	public String state = "";

	/**
	 * Description of the property zip.
	 */
	public String zip = "";

	// Start of user code (user defined attributes for USAddress)

	// End of user code

	/**
	 * The constructor.
	 */
	public USAddress() {
		// Start of user code constructor for USAddress)
		super();
		// End of user code
	}

	// Start of user code (user defined methods for USAddress)

	// End of user code
	/**
	 * Returns street.
	 * @return street 
	 */
	public String getStreet() {
		return this.street;
	}

	/**
	 * Sets a value to attribute street. 
	 * @param newStreet 
	 */
	public void setStreet(String newStreet) {
		this.street = newStreet;
	}

	/**
	 * Returns city.
	 * @return city 
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * Sets a value to attribute city. 
	 * @param newCity 
	 */
	public void setCity(String newCity) {
		this.city = newCity;
	}

	/**
	 * Returns state.
	 * @return state 
	 */
	public String getState() {
		return this.state;
	}

	/**
	 * Sets a value to attribute state. 
	 * @param newState 
	 */
	public void setState(String newState) {
		this.state = newState;
	}

	/**
	 * Returns zip.
	 * @return zip 
	 */
	public String getZip() {
		return this.zip;
	}

	/**
	 * Sets a value to attribute zip. 
	 * @param newZip 
	 */
	public void setZip(String newZip) {
		this.zip = newZip;
	}

}
