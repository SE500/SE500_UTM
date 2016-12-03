/*******************************************************************************
 * 2016, All rights reserved.
 *******************************************************************************/

// Start of user code (user defined imports)

// End of user code

/**
 * Description of Address.
 * 
 * @author 0924
 */
public abstract class Address {
	/**
	 * Description of the property name.
	 */
	public String name = "";

	/**
	 * Description of the property country.
	 */
	public String country = "";

	// Start of user code (user defined attributes for Address)

	// End of user code

	/**
	 * The constructor.
	 */
	public Address() {
		// Start of user code constructor for Address)
		super();
		// End of user code
	}

	// Start of user code (user defined methods for Address)

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
	 * Returns country.
	 * @return country 
	 */
	public String getCountry() {
		return this.country;
	}

	/**
	 * Sets a value to attribute country. 
	 * @param newCountry 
	 */
	public void setCountry(String newCountry) {
		this.country = newCountry;
	}

}
