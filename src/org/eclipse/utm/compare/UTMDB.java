package org.eclipse.utm.compare;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Class which contains all accessors for database manipulation within UTM.
 * @author Michael Freudeman
 */
public final class UTMDB {
	
	/**
	 * The connection object to the database.
	 * Is null until Open() is called.
	 */
	private Connection _c = null;
	
	/**
	 * Static flag to signify if the database has been initialized.
	 */
	private static boolean _isInit = false;
	
	/**
	 * Static flag set to true after the database has been created via initialization.
	 */
	private static boolean _hasCreatedDB = false;
	
	/**
	 * Inserts a new record into either the UMLClass or CodeClass table.
	 * If IsFromUML = true the destination table will be UMLClass else the destination table will be CodeClass.
	 * @param IsFromUML Designates if the class is from a UML or Source Code document.
	 * @param Filename Name of the file which the class originates.
	 * @param LineNumber Line number within the originating file.
	 * @param ClassName Name of the class.
	 * @param AccessType Access identifier of the class (public, private, etc..)
	 * @param IsStatic True if the class is static.
	 * @param IsAbstract True if the class is abstract.
	 * @param IsFinal True if the class is final.
	 * @return Auto-incremented Class_ID of the new record within the destination table.
	 */
	private int newClass(boolean IsFromUML, String Filename, int LineNumber, String ClassName, String AccessType, boolean IsStatic, boolean IsAbstract, boolean IsFinal)
	{
		try
		{
			String strInsert =
					"Insert Into " + (IsFromUML ? "UML" : "Code") + "Class (" +
					"FileName, LineNumber, ClassName, AccessType, IsStatic, IsAbstract, IsFinal" +
					") Values (?, ?, ?, ?, ?, ?, ?)"
			;
			
			PreparedStatement stmntInsert = this._c.prepareStatement(strInsert, Statement.RETURN_GENERATED_KEYS);
			
			stmntInsert.setString(1, Filename);
			stmntInsert.setInt(2, LineNumber);
			stmntInsert.setString(3, ClassName);
			stmntInsert.setString(4, AccessType);
			stmntInsert.setBoolean(5, IsStatic);
			stmntInsert.setBoolean(6, IsAbstract);
			stmntInsert.setBoolean(7, IsAbstract && IsFinal ? false : IsFinal); // Cannot be abstract and final, if abstract, not final
			
			stmntInsert.executeUpdate();

			ResultSet res = stmntInsert.getGeneratedKeys();
			res.next();

			int ret = res.getInt(1);
			System.out.println((IsFromUML ? "UML" : "Source") + ":newClass:" + ClassName + ":id:" + ret);
			
			res.close();
			stmntInsert.close();
			
			return ret;
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Inserts a new record into either the UMLReference or CodeReference table.
	 * If IsFromUML = true the destination table will be UMLReference else the destination table will be CodeReference.
	 * @param IsFromUML Designates if the reference is from a UML or Source Code document.
	 * @param ClassName Name of the base class.
	 * @param AccessType Access identifier of the class (implements, extends)
	 * @param RefClass Name of the referenced class.
	 * @return Auto-incremented Reference_ID of the record within the destination table.
	 */
	private int newReference(boolean IsFromUML, String ClassName, String AccessType, String RefClass)
	{	
		try
		{
			String strInsert =
					"Insert Into " + (IsFromUML ? "UML" : "Code") + "Reference (" +
					"ClassName, AccessType, RefClassName" +
					") Values (?, ?, ?)"
			;
			
			PreparedStatement stmntInsert = this._c.prepareStatement(strInsert, Statement.RETURN_GENERATED_KEYS);
			
			stmntInsert.setString(1, ClassName);
			stmntInsert.setString(2, AccessType);
			stmntInsert.setString(3, RefClass);
			
			stmntInsert.executeUpdate();
			
			ResultSet res = stmntInsert.getGeneratedKeys();
			res.next();

			int ret = res.getInt(1);
			System.out.println((IsFromUML ? "UML" : "Source") + ":newReference:" + ClassName + ":" + RefClass + ":id:" + ret);
			
			res.close();
			stmntInsert.close();
			
			return ret;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			while(e!=null)
			{
				System.err.println(e.toString());
				e = e.getNextException();
			}
			return 0;
		}
	}
	
	/**
	 * Inserts a new record into either the UMLAttribute or CodeAttribute table.
	 * If IsFromUML = true the destination table will be UMLAttribute else the destination table will be CodeAttribute.
	 * @param IsFromUML Designates if the attribute is from a UML or Source Code document.
	 * @param Filename Name of the file which the attribute originates.
	 * @param LineNumber Line number within the originating file.
	 * @param ClassName Name of the originating class.
	 * @param AccessType Access identifier of the class (public, private, etc..)
	 * @param Type Data type of the attribute.
	 * @param Name Name of the attribute.
	 * @return Auto-incremented Attribute_ID of the record within the destination table.
	 */
	private int newAttribute(boolean IsFromUML, String Filename, int LineNumber, String ClassName, String AccessType, String Type, String Name)
	{
		PreparedStatement stmntInsert = null;
		
		try
		{
			String strInsert =
					"Insert Into " + (IsFromUML ? "UML" : "Code") + "Attribute (" +
					"FileName, LineNumber, ClassName, AccessType, Type, Name" +
					") Values (?, ?, ?, ?, ?, ?)"
			;
			
			stmntInsert = this._c.prepareStatement(strInsert, Statement.RETURN_GENERATED_KEYS);
			
			stmntInsert.setString(1, Filename);
			stmntInsert.setInt(2, LineNumber);
			stmntInsert.setString(3, ClassName);
			stmntInsert.setString(4, AccessType);
			stmntInsert.setString(5, Type);
			stmntInsert.setString(6, Name);

			stmntInsert.executeUpdate();
			
			ResultSet res = stmntInsert.getGeneratedKeys();
			res.next();

			int ret = res.getInt(1);
			System.out.println((IsFromUML ? "UML" : "Source") + ":newAttribute:" + Name + ":id:" + ret);
			
			stmntInsert.close();
			
			return ret;
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Inserts a new record into either the UMLAttribute or CodeAttribute table.
	 * If IsFromUML = true the destination table will be UMLAttribute else the destination table will be CodeAttribute.
	 * @param IsFromUML Designates if the attribute is from a UML or Source Code document.
	 * @param Filename Name of the file which the attribute originates.
	 * @param LineNumber Line number within the originating file.
	 * @param ClassName Name of the originating class.
	 * @param AccessType Access identifier of the class (public, private, etc..)
	 * @param Type Data type of the attribute.
	 * @param Name Name of the attribute.
	 * @param Params Comma-delimited list of parameters of the method in the format of "DataType Name, DataType Name"
	 * @return Auto-incremented Method_ID of the record within the destination table.
	 */
	private int newMethod(boolean IsFromUML, String Filename, int LineNumber, String ClassName, String AccessType, String Type, String Name, String Params)
	{
		try
		{
			String strInsert =
					"Insert Into " + (IsFromUML ? "UML" : "Code") + "Method (" +
					"FileName, LineNumber, ClassName, AccessType, Type, Name, Parameters" +
					") Values (?, ?, ?, ?, ?, ?, ?)"
			;
			
			PreparedStatement stmntInsert = this._c.prepareStatement(strInsert, Statement.RETURN_GENERATED_KEYS);
			
			stmntInsert.setString(1, Filename);
			stmntInsert.setInt(2, LineNumber);
			stmntInsert.setString(3, ClassName);
			stmntInsert.setString(4, AccessType);
			stmntInsert.setString(5, Type);
			stmntInsert.setString(6, Name);
			stmntInsert.setString(7, Params);

			stmntInsert.executeUpdate();
			
			ResultSet res = stmntInsert.getGeneratedKeys();
			res.next();

			int ret = res.getInt(1);
			System.out.println((IsFromUML ? "UML" : "Source") + ":newMethod:" + Name + ":id:" + ret);
			
			res.close();
			stmntInsert.close();
			
			return ret;
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * If fromUML = true then count will be against UMLMethod table else CodeMethod table will be used.
	 * @param fromUML Designates if the attribute to count are from a UML or Source Code document.
	 * @return Count of methods within either UMLMethod or CodeMethod tables.
	 */
	private int countMethods(boolean fromUML)
	{
		int count = 0;
		try
		{
			String strCount = "Select Count(*) From " + (fromUML ? "UMLMethod" : "CodeMethod");
			PreparedStatement stmntCount = this._c.prepareStatement(strCount);
			
			ResultSet rs = stmntCount.executeQuery();
			rs.next();
			
			count = rs.getInt(1);
			
			rs.close();
			stmntCount.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return -1;
		}
		return count;
	}
	
	/**
	 * If fromUML = true then count will be against UMLAttribute table else CodeAttribute table will be used.
	 * @param fromUML Designates if the attribute to count are from a UML or Source Code document.
	 * @return Count of attributes within either UMLAttribute or CodeAttribute tables.
	 */
	private int countAttributes(boolean fromUML)
	{
		int count = 0;
		try
		{
			String strCount = "Select Count(*) From " + (fromUML ? "UMLAttribute" : "CodeAttribute");
			PreparedStatement stmntCount = this._c.prepareStatement(strCount);
			
			ResultSet rs = stmntCount.executeQuery();
			rs.next();
			
			count = rs.getInt(1);

			rs.close();
			stmntCount.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return -1;
		}
		return count;
	}
	
	/**
	 * If fromUML = true then count will be against UMLClass table else CodeClass table will be used.
	 * @param fromUML Designates if the class to count are from a UML or Source Code document.
	 * @return Count of classes within either UMLClass or CodeClass tables.
	 */
	private int countClasses(boolean fromUML)
	{
		int count = 0;
		try
		{
			String strCount = "Select Count(*) From " + (fromUML ? "UMLClass" : "CodeClass");
			PreparedStatement stmntCount = this._c.prepareStatement(strCount);
			
			ResultSet rs = stmntCount.executeQuery();
			rs.next();
			
			count = rs.getInt(1);

			rs.close();
			stmntCount.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return -1;
		}
		return count;
	}
	
	/**
	 * If fromUML = true then count will be against UMLReference table else CodeReference table will be used.
	 * @param fromUML Designates if the reference to count are from a UML or Source Code document.
	 * @return Count of references within either UMLReference or CodeReference tables.
	 */
	private int countReferences(boolean fromUML)
	{
		int count = 0;
		try
		{
			String strCount = "Select Count(*) From " + (fromUML ? "UMLReference" : "CodeReference");
			PreparedStatement stmntCount = this._c.prepareStatement(strCount);
			
			ResultSet rs = stmntCount.executeQuery();
			rs.next();
			
			count = rs.getInt(1);

			rs.close();
			stmntCount.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return -1;
		}
		
		return count;
	}
	
	/**
	 * If fromUML = true then count will be against UMLMethod table else CodeMethod table will be used.
	 * @param fromUML Designates if the methods to count are from a UML or Source Code document.
	 * @param ClassName Name of the class to count from.
	 * @return Count of methods within either UMLClass or CodeClass tables associated with the given ClassName.
	 */
	private int countMethods(boolean fromUML, String ClassName)
	{
		int count = 0;
		try
		{
			String strCount = "Select Count(*) From " + (fromUML ? "UMLMethod" : "CodeMethod") + " Where ClassName = ?";
			PreparedStatement stmntCount = this._c.prepareStatement(strCount);
			stmntCount.setString(1, ClassName);
			
			ResultSet rs = stmntCount.executeQuery();
			rs.next();
			
			count = rs.getInt(1);

			rs.close();
			stmntCount.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return -1;
		}
		return count;
	}
	
	/**
	 * If fromUML = true then count will be against UMLAttribute table else CodeAttribute table will be used.
	 * @param fromUML Designates if the attribute is from a UML or Source Code document.
	 * @param ClassName Name of the class to count from.
	 * @return Count of attributes within either UMLAttribute or CodeAttribute tables associated with the given ClassName.
	 */
	private int countAttributes(boolean fromUML, String ClassName)
	{
		int count = 0;
		try
		{
			String strCount = "Select Count(*) From " + (fromUML ? "UMLAttribute" : "CodeAttribute") + " Where ClassName = ?";
			PreparedStatement stmntCount = this._c.prepareStatement(strCount);
			stmntCount.setString(1, ClassName);
			
			ResultSet rs = stmntCount.executeQuery();
			rs.next();
			
			count = rs.getInt(1);

			rs.close();
			stmntCount.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return -1;
		}
		return count;
	}
	
	/**
	 * If fromUML = true then count will be against UMLReference table else CodeReference table will be used.
	 * @param fromUML Designates if the reference is from a UML or Source Code document.
	 * @param ClassName Name of the class to count from.
	 * @return Count of references with ClassName as the referenced class.
	 */
	private int countReferencesOf(boolean fromUML, String ClassName)
	{
		int count = 0;
		try
		{
			String strCount = "Select Count(*) From " + (fromUML ? "UMLReference" : "CodeReference") + " Where RefClassName = ?";
			PreparedStatement stmntCount = this._c.prepareStatement(strCount);
			stmntCount.setString(1, ClassName);
			
			ResultSet rs = stmntCount.executeQuery();
			rs.next();
			
			count = rs.getInt(1);

			rs.close();
			stmntCount.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return -1;
		}
		return count;
	}
	
	/**
	 * If fromUML = true then count will be against UMLReference table else CodeReference table will be used.
	 * @param fromUML Designates if the reference is from a UML or Source Code document.
	 * @param ClassName Name of the class to count from.
	 * @return Count of references with ClassName as the base class of the reference.
	 */
	private int countReferencesTo(boolean fromUML, String ClassName)
	{
		int count = 0;
		try
		{
			String strCount = "Select Count(*) From " + (fromUML ? "UMLReference" : "CodeReference") + " Where ClassName = ?";
			PreparedStatement stmntCount = this._c.prepareStatement(strCount);
			stmntCount.setString(1, ClassName);
			
			ResultSet rs = stmntCount.executeQuery();
			rs.next();
			
			count = rs.getInt(1);

			rs.close();
			stmntCount.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return -1;
		}
		return count;
	}
	
	/**
	 * If fromUML = true then UMLClass table is used else the CodeClass table is used.
	 * @param fromUML Sets the table to use as the source.
	 * @param ClassID Sets the Class_ID to reference from the source table.
	 * @return UTMDBClass object representing the given ClassID of the sourced table.
	 */
	private UTMDBClass getClass(boolean fromUML, int ClassID)
	{
		UTMDBClass o = null;
		
		try
		{
			Statement select = this._c.createStatement();
			ResultSet rs = select.executeQuery("Select * From " + (fromUML ? "UML" : "Code") + "Class Where Class_ID = " + ClassID);
			if(!rs.isBeforeFirst())
			{ // Exit if no results returned
				return null;
			}
			rs.next();
			
			o = new UTMDBClass();
			
			o.ClassID = rs.getInt(1);
			o.Filename = rs.getString(2);
			o.LineNumber = rs.getInt(3);
			o.ClassName = rs.getString(4);
			o.AccessType = rs.getString(5);
			o.IsStatic = rs.getBoolean(6);
			o.IsAbstract = rs.getBoolean(7);
			o.IsFinal = rs.getBoolean(8);
			o.OtherID = rs.getInt(9);
			o.NumMismatched = rs.getInt(10);
			
			rs.close();
			
			select.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return null;
		}
		
		return o;
	}
	
	/**
	 * If fromUML = true then UMLAttribute table is used else the CodeAttribute table is used.
	 * @param fromUML Sets the table to use as the source.
	 * @param AttributeID Sets the AttributeID to reference from the source table.
	 * @return 
	 * 		UTMDBAttribute object representing the given ClassID of the sourced table.
	 */
	private UTMDBAttribute getAttribute(boolean fromUML, int AttributeID)
	{
		UTMDBAttribute o = null;
		try
		{
			Statement select = this._c.createStatement();
			ResultSet rs = select.executeQuery("Select * From " + (fromUML ? "UML" : "Code") + "Attribute Where Attribute_ID = " + AttributeID);
			if(!rs.isBeforeFirst())
			{ // Exit if no results returned
				return null;
			}
			rs.next();
			
			o = new UTMDBAttribute();
			
			o.AttributeID = rs.getInt(1);
			o.ClassID = rs.getInt(2);
			o.Filename = rs.getString(3);
			o.LineNumber = rs.getInt(4);
			o.ClassName = rs.getString(5);
			o.AccessType = rs.getString(6);
			o.Name = rs.getString(7);
			o.Type = rs.getString(8);
			o.OtherID = rs.getInt(9);
			o.NumMismatched = rs.getInt(10);
			
			rs.close();
			
			select.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return null;
		}
		return o;
	}
	
	/**
	 * If fromUML = true then UMLMethod table is used else the CodeMethod table is used.
	 * @param fromUML Sets the table to use as the source.
	 * @param MethodID Sets the MethodID to reference from the source table.
	 * @return UTMDBMethod object representing the given ClassID of the sourced table.
	 */
	private UTMDBMethod getMethod(boolean fromUML, int MethodID)
	{
		UTMDBMethod o = null;
		try
		{
			Statement select = this._c.createStatement();
			ResultSet rs = select.executeQuery("Select * From " + (fromUML ? "UML" : "Code") + "Method Where Method_ID = " + MethodID);
			if(!rs.isBeforeFirst())
			{ // Exit if no results returned
				return null;
			}
			rs.next();
			
			o = new UTMDBMethod();
			
			o.MethodID = rs.getInt(1);
			o.ClassID = rs.getInt(2);
			o.Filename = rs.getString(3);
			o.LineNumber = rs.getInt(4);
			o.ClassName = rs.getString(5);
			o.AccessType = rs.getString(6);
			o.Type = rs.getString(7);
			o.Name = rs.getString(8);
			o.Parameters = rs.getString(9);
			o.OtherID = rs.getInt(10);
			o.NumMismatched = rs.getInt(11);
			
			rs.close();
			
			select.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return null;
		}
		return o;
	}
	
	/**
	 * Retrieves the class object representing the class record created using {@link #NewSourceClass(String, int, String, String, boolean, boolean, boolean) NewSourceClass} method.
	 * @param ClassID ID of the class to retrieve.
	 * @return UTMDBClass object representing the class record.
	 */
	public UTMDBClass GetSourceClass(int ClassID)
	{
		return this.getClass(false, ClassID);
	}
	
	/**
	 * Retrieves the class object representing the class record created using {@link #NewUMLClass(String, String, String, boolean, boolean, boolean) NewUMLClass} method.
	 * @param ClassID ID of the class to retrieve.
	 * @return UTMDBClass object representing the class record.
	 */
	public UTMDBClass GetUMLClass(int ClassID)
	{
		return this.getClass(true, ClassID);
	}
	
	/**
	 * Retrieves the attribute object representing the attribute record created using {@link #NewSourceAttribute(String, int, String, String, String, String) NewSourceAttribute} method.
	 * @param AttributeID ID of the attribute to retrieve.
	 * @return UTMDBClass object representing the class record.
	 */
	public UTMDBAttribute GetSourceAttribute(int AttributeID)
	{
		return this.getAttribute(false, AttributeID);
	}
	
	/**
	 * Retrieves the attribute object representing the attribute record created using {@link #NewUMLAttribute(String, String, String, String, String) NewUMLAttribute} method.
	 * @param AttributeID ID of the attribute to retrieve.
	 * @return UTMDBAttribute object representing the attribute record.
	 */
	public UTMDBAttribute GetUMLAttribute(int AttributeID)
	{
		return this.getAttribute(false, AttributeID);
	}
	
	/**
	 * Retrieves the method object representing the method record created using {@link #NewSourceMethod(String, int, String, String, String, String, String) NewSourceMethod} method.
	 * @param MethodID ID of the method to retrieve.
	 * @return UTMDBMethod object representing the method record.
	 */
	public UTMDBMethod GetSourceMethod(int MethodID)
	{
		return this.getMethod(false, MethodID);
	}
	
	/**
	 * Retrieves the method object representing the method record created using {@link #NewUMLMethod(String, String, String, String, String, String) NewUMLMethod} method.
	 * @param MethodID ID of the method to retrieve.
	 * @return UTMDBMethod object representing the method record.
	 */
	public UTMDBMethod GetUMLMethod(int MethodID)
	{
		return this.getMethod(true, MethodID);
	}
	
	/**
	 * @return List of UTMDBClass objects which represent all records in the CodeClass table.
	 */
	public ArrayList<UTMDBClass> GetSourceClassList()
	{
		ArrayList<UTMDBClass> list = new ArrayList<UTMDBClass>();
		
		try
		{
			Statement stmntSelect = this._c.createStatement();
			ResultSet r = stmntSelect.executeQuery("Select Class_ID, Filename, LineNumber, ClassName, AccessType, IsStatic, IsAbstract, IsFinal, Other_ID, NumMismatched From CodeClass");
		
			while(r.next())
			{
				UTMDBClass udb = new UTMDBClass();
				udb.ClassID = r.getInt(1);
				udb.Filename = r.getString(2);
				udb.LineNumber = r.getInt(3);
				udb.ClassName = r.getString(4);
				udb.AccessType = r.getString(5);
				udb.IsStatic = r.getBoolean(6);
				udb.IsAbstract = r.getBoolean(7);
				udb.IsFinal = r.getBoolean(8);
				udb.OtherID = r.getInt(9);
				udb.NumMismatched = r.getInt(10);
				
				list.add(udb);
			}
			
			stmntSelect.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * @return List of UTMDBClass objects which represent all records in the UMLClass table.
	 */
	public ArrayList<UTMDBClass> GetUMLClassList()
	{
		ArrayList<UTMDBClass> list = new ArrayList<UTMDBClass>();
		
		try
		{
			Statement stmntSelect = this._c.createStatement();
			ResultSet r = stmntSelect.executeQuery("Select Class_ID, ClassName, AccessType, IsStatic, IsAbstract, IsFinal, Other_ID, NumMismatched From UMLClass");
		
			while(r.next())
			{
				UTMDBClass udb = new UTMDBClass();
				udb.ClassID = r.getInt(1);
				udb.Filename = "UML";
				udb.LineNumber = 0;
				udb.ClassName = r.getString(2);
				udb.AccessType = r.getString(3);
				udb.IsStatic = r.getBoolean(4);
				udb.IsAbstract = r.getBoolean(5);
				udb.IsFinal = r.getBoolean(6);
				udb.OtherID = r.getInt(7);
				udb.NumMismatched = r.getInt(8);
				
				list.add(udb);
			}
			
			stmntSelect.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
		}
		
		return list;
	}

	/**
	 * Get the attributes for a class from the source code 
	 * @param ClassID is the class to retrieve the attributes from
	 * @return List of UTMDBAttribute objects which represent all records in the CodeAttributes table.
	 */
	public ArrayList<UTMDBAttribute> GetSourceAttributesList(int ClassID)
	{
		ArrayList<UTMDBAttribute> list = new ArrayList<UTMDBAttribute>();
		
		try
		{
			Statement stmntSelect = this._c.createStatement();
			ResultSet r = stmntSelect.executeQuery("Select Attribute_ID, Class_ID, Filename, LineNumber, ClassName, AccessType, Name, Type, Other_ID, NumMismatched From CodeAttribute Where Class_ID = " + ClassID);
		
			while(r.next())
			{
				UTMDBAttribute udb = new UTMDBAttribute();
				udb.AttributeID = r.getInt(1);
				udb.ClassID = r.getInt(2);
				udb.Filename = r.getString(3);
				udb.LineNumber = r.getInt(4);
				udb.ClassName = r.getString(5);
				udb.AccessType = r.getString(6);
				udb.Name = r.getString(7);
				udb.Type = r.getString(8);
				udb.OtherID = r.getInt(9);
				udb.NumMismatched = r.getInt(10);
				
				list.add(udb);
			}
			
			stmntSelect.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * Get the attributes for a class from the uml
	 * @param ClassID is the class to retrieve the attributes from
	 * @return List of UTMDBAttribute objects which represent all records in the UMLAttributes table.
	 */
	public ArrayList<UTMDBAttribute> GetUMLAttributesList(int ClassID)
	{
		ArrayList<UTMDBAttribute> list = new ArrayList<UTMDBAttribute>();
		
		try
		{
			Statement stmntSelect = this._c.createStatement();
			ResultSet r = stmntSelect.executeQuery("Select Attribute_ID, Class_ID, ClassName, AccessType, Name, Type, Other_ID, NumMismatched From UMLAttribute Where Class_ID = " + ClassID);
		
			while(r.next())
			{
				UTMDBAttribute udb = new UTMDBAttribute();
				udb.AttributeID = r.getInt(1);
				udb.ClassID = r.getInt(2);
				udb.Filename = "UML";
				udb.LineNumber = 0;
				udb.ClassName = r.getString(3);
				udb.AccessType = r.getString(4);
				udb.Name = r.getString(5);
				udb.Type = r.getString(6);
				udb.OtherID = r.getInt(7);
				udb.NumMismatched = r.getInt(8);
				
				list.add(udb);
			}
			
			stmntSelect.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * Get the methods for a class from the source code
	 * @param ClassID is the class to retrieve the methods from
	 * @return List of UTMDBMethod objects which represent all records in the CodeMethod table.
	 */
	public ArrayList<UTMDBMethod> GetSourceMethodsList(int ClassID)
	{
		ArrayList<UTMDBMethod> list = new ArrayList<UTMDBMethod>();
		
		try
		{
			Statement stmntSelect = this._c.createStatement();
			ResultSet r = stmntSelect.executeQuery("Select Method_ID, Class_ID, Filename, LineNumber, ClassName, AccessType, Type, Name, Parameters, Other_ID, NumMismatched From CodeMethod Where Class_ID = " + ClassID);
		
			while(r.next())
			{
				UTMDBMethod udb = new UTMDBMethod();
				udb.MethodID = r.getInt(1);
				udb.ClassID = r.getInt(2);
				udb.Filename = r.getString(3);
				udb.LineNumber = r.getInt(4);
				udb.ClassName = r.getString(5);
				udb.AccessType = r.getString(6);
				udb.Type = r.getString(7);
				udb.Name = r.getString(8);
				udb.Parameters = r.getString(9);
				udb.OtherID = r.getInt(10);
				udb.NumMismatched = r.getInt(11);
				
				list.add(udb);
			}
			
			stmntSelect.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * Get the methods for a class from the uml
	 * @param ClassID is the class to retrieve the methods from
	 * @return List of UTMDBMethod objects which represent all records in the UMLMethod table.
	 */
	public ArrayList<UTMDBMethod> GetUMLMethodsList(int ClassID)
	{
		ArrayList<UTMDBMethod> list = new ArrayList<UTMDBMethod>();
		
		try
		{
			Statement stmntSelect = this._c.createStatement();
			ResultSet r = stmntSelect.executeQuery("Select Method_ID, Class_ID, ClassName, AccessType, Type, Name, Parameters, Other_ID, NumMismatched From UMLMethod Where Class_ID = " + ClassID);
		
			while(r.next())
			{
				UTMDBMethod udb = new UTMDBMethod();
				udb.MethodID = r.getInt(1);
				udb.ClassID = r.getInt(2);
				udb.Filename = "UML";
				udb.LineNumber = 0;
				udb.ClassName = r.getString(3);
				udb.AccessType = r.getString(4);
				udb.Type = r.getString(5);
				udb.Name = r.getString(6);
				udb.Parameters = r.getString(7);
				udb.OtherID = r.getInt(8);
				udb.NumMismatched = r.getInt(9);
				
				list.add(udb);
			}
			
			stmntSelect.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * Get the reference for a class from the source code
	 * @param ClassID is the class to retrieve the methods from
	 * @return List of UTMDBReference objects which represent all records in the CodeReference table.
	 */
	public ArrayList<UTMDBReference> GetSourceReferencesList(int ClassID)
	{
		ArrayList<UTMDBReference> list = new ArrayList<UTMDBReference>();
		
		try
		{
			Statement stmntSelect = this._c.createStatement();
			ResultSet r = stmntSelect.executeQuery("Select Reference_ID, Class_ID, ClassName, AccessType, Ref_Class_ID, RefClassName, Other_ID, NumMismatched From CodeReference Where Class_ID = " + ClassID);
		
			while(r.next())
			{
				UTMDBReference udb = new UTMDBReference();
				udb.ReferenceID = r.getInt(1);
				udb.ClassID = r.getInt(2);
				udb.ClassName = r.getString(3);
				udb.AccessType = r.getString(4);
				udb.ReferenceClassID = r.getInt(5);
				udb.ReferenceClassName = r.getString(6);
				udb.OtherID = r.getInt(7);
				udb.NumMismatched = r.getInt(8);
				
				list.add(udb);
			}
			
			stmntSelect.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * Get the references for a class from the uml
	 * @param ClassID is the class to retrieve the methods from
	 * @return List of UTMDBReference objects which represent all records in the UMLReference table.
	 */
	public ArrayList<UTMDBReference> GetUMLReferencesList(int ClassID)
	{
		ArrayList<UTMDBReference> list = new ArrayList<UTMDBReference>();
		
		try
		{
			Statement stmntSelect = this._c.createStatement();
			ResultSet r = stmntSelect.executeQuery("Select Reference_ID, Class_ID, ClassName, AccessType, Ref_Class_ID, RefClassName, Other_ID, NumMismatched From UMLReference Where Class_ID = " + ClassID);
		
			while(r.next())
			{
				UTMDBReference udb = new UTMDBReference();
				udb.ReferenceID = r.getInt(1);
				udb.ClassID = r.getInt(2);
				udb.ClassName = r.getString(3);
				udb.AccessType = r.getString(4);
				udb.ReferenceClassID = r.getInt(5);
				udb.ReferenceClassName = r.getString(6);
				udb.OtherID = r.getInt(7);
				udb.NumMismatched = r.getInt(8);
				
				list.add(udb);
			}
			
			stmntSelect.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * Create a new record in the UMLClass table.
	 * Calls the {@link #newClass(boolean, String, int, String, String, boolean, boolean, boolean) newClass} method.
	 * @param Filename Name of the java file
	 * @param ClassName Name of the class.
	 * @param AccessType Access identifier of the class (public, private, etc..)
	 * @param IsStatic True if the class is static.
	 * @param IsAbstract True if the class is abstract.
	 * @param IsFinal True if the class is final.
	 * @return Auto-incremented Class_ID of the new record within the destination table.
	 */
	public int NewUMLClass(String Filename, String ClassName, String AccessType, boolean IsStatic, boolean IsAbstract, boolean IsFinal)
	{
		return this.newClass(true, Filename, 0, ClassName, AccessType, IsStatic, IsAbstract, IsFinal);
	}
	
	/**
	 * Create a new record in the SourceClass table.
	 * Calls the {@link #newClass(boolean, String, int, String, String, boolean, boolean, boolean) newClass} method.
	 * @param Filename Name of the file which the class originates.
	 * @param LineNumber Line number within the originating file.
	 * @param ClassName Name of the class.
	 * @param AccessType Access identifier of the class (public, private, etc..)
	 * @param IsStatic True if the class is static.
	 * @param IsAbstract True if the class is abstract.
	 * @param IsFinal True if the class is final.
	 * @return Auto-incremented Class_ID of the new record within the destination table.
	 */
	public int NewSourceClass(String Filename, int LineNumber, String ClassName, String AccessType, boolean IsStatic, boolean IsAbstract, boolean IsFinal)
	{
		return this.newClass(false, Filename, LineNumber, ClassName, AccessType, IsStatic, IsAbstract, IsFinal);
	}
	
	/**
	 * Create a new record in the UMLReference table.
	 * Call the {@link #newReference(boolean, String, String, String) newReference} method.
	 * @param ClassName Name of the class.
	 * @param AccessType Reference identifier of the class (extends, implements)
	 * @param RefClass Name of the class being referenced.
	 * @return Auto-incremented Class_ID of the new record within the destination table.
	 */
	public int NewUMLReference(String ClassName, String AccessType, String RefClass)
	{
		return this.newReference(true, ClassName, AccessType, RefClass);
	}
	
	/**
	 * Create a new record in the CodeReference table.
	 * Call the {@link #newReference(boolean, String, String, String) newReference} method.
	 * @param ClassName Name of the class.
	 * @param AccessType Reference identifier of the class (extends, implements)
	 * @param RefClass Name of the class being referenced.
	 * @return Auto-incremented Class_ID of the new record within the destination table.
	 */
	public int NewSourceReference(String ClassName, String AccessType, String RefClass)
	{
		return this.newReference(false, ClassName, AccessType, RefClass);
	}
	
	/**
	 * Create a new record in the UMLAttribute table.
	 * Call the {@link #newAttribute(boolean, String, int, String, String, String, String) newAttribute} method.
	 * @param Filename Name of the file which the attribute originates.
	 * @param ClassName Name of the class.
	 * @param AccessType Access identifier of the class (public, private, etc..)
	 * @param Type Data type of the attribute.
	 * @param Name Name of the attribute.
	 * @return Auto-incremented Attribute_ID of the record within the destination table.
	 */
	public int NewUMLAttribute(String Filename, String ClassName, String AccessType, String Type, String Name)
	{
		return this.newAttribute(true, Filename, 0, ClassName, AccessType, Type, Name);
	}
	
	/**
	 * Create a new record in the CodeAttribute table.
	 * Call the {@link #newAttribute(boolean, String, int, String, String, String, String) newAttribute} method.
	 * @param Filename Name of the file which the attribute originates.
	 * @param LineNumber Line number within the originating file.
	 * @param ClassName Name of the class.
	 * @param AccessType Access identifier of the class (public, private, etc..)
	 * @param Type Data type of the attribute.
	 * @param Name Name of the attribute.
	 * @return Auto-incremented Attribute_ID of the record within the destination table.
	 */
	public int NewSourceAttribute(String Filename, int LineNumber, String ClassName, String AccessType, String Type, String Name)
	{
		return this.newAttribute(false, Filename, LineNumber, ClassName, AccessType, Type, Name);
	}
	
	/**
	 * Create a new record in the UMLMethod table.
	 * Call the {@link #newMethod(boolean, String, int, String, String, String, String, String) newMthod} method.
	 * @param Filename Name of the file which the attribute originates.
	 * @param ClassName Name of the class the method is contained within.
	 * @param AccessType Access identifier of the class (public, private, etc..)
	 * @param Type Data type of the attribute.
	 * @param Name Name of the method.
	 * @param Params Comma-delimited list of parameters of the method in the format of "DataType Name, DataType Name"
	 * @return Auto-incremented Method_ID of the record within the destination table.
	 */
	public int NewUMLMethod(String Filename, String ClassName, String AccessType, String Type, String Name, String Params)
	{
		return this.newMethod(true, Filename, 0, ClassName, AccessType, Type, Name, Params);
	}
	
	/**
	 * Create a new record in the CodeMethod table.
	 * Call the {@link #newMethod(boolean, String, int, String, String, String, String, String) newMthod} method.
	 * @param Filename Name of the file which the method originates.
	 * @param LineNumber Line number within the originating file.
	 * @param ClassName Name of the class the method is contained within.
	 * @param AccessType Access identifier of the class (public, private, etc..)
	 * @param Type Data type of the method.
	 * @param Name Name of the method.
	 * @param Params Comma-delimited list of parameters of the method in the format of "DataType Name, DataType Name"
	 * @return Auto-incremented Method_ID of the record within the destination table.
	 */
	public int NewSourceMethod(String Filename, int LineNumber, String ClassName, String AccessType, String Type, String Name, String Params)
	{
		return this.newMethod(false, Filename, LineNumber, ClassName, AccessType, Type, Name, Params);
	}
	
	/**
	 * Calls the {@link #countClasses(boolean) countClasses} method.
	 * @return Total count of records within the UMLClass table.
	 */
	public int CountUMLClasses()
	{
		return countClasses(true);
	}
	
	/**
	 * Calls the {@link #countClasses(boolean) countClasses} method.
	 * @return Total count of records within the CodeClass table.
	 */
	public int CountSourceClasses()
	{
		return countClasses(false);
	}
	
	/**
	 * Calls the {@link #countAttributes(boolean) countAttributes} method.
	 * @return Total count of records within the UMLAttribute table.
	 */
	public int CountUMLAttributes()
	{
		return countAttributes(true);
	}
	
	/**
	 * Calls the {@link #countAttributes(boolean) countAttributes} method.
	 * @return Total count of records within the CodeAttribute table.
	 */
	public int CountSourceAttributes()
	{
		return countAttributes(false);
	}
	
	/**
	 * Calls the {@link #countMethods(boolean) countMethods} method.
	 * @return Total count of records within the UMLMethod table.
	 */
	public int CountUMLMethods()
	{
		return countMethods(true);
	}
	
	/**
	 * Calls the {@link #countMethods(boolean) countMethods} method.
	 * @return Total count of records within the CodeMethod table.
	 */
	public int CountSourceMethods()
	{
		return countMethods(false);
	}
	
	/**
	 * Calls the {@link #countReferences(boolean) countReferences} method.
	 * @return Total count of records within the UMLReference table.
	 */
	public int CountUMLReferences()
	{
		return countReferences(true);
	}
	
	/**
	 * Calls the {@link #countReferences(boolean) countReferences} method.
	 * @return Total count of records within the CodeReference table.
	 */
	public int CountSourceReferences()
	{
		return countReferences(false);
	}
	
	/**
	 * Calls the {@link #countAttributes(boolean, String) countAttributes} method.
	 * @param ClassName Name of the class to count from.
	 * @return Count of attributes within UMLAttribute table associated with the given ClassName.
	 */
	public int CountUMLAttributes(String ClassName)
	{
		return countAttributes(true, ClassName);
	}
	
	/**
	 * Calls the {@link #countAttributes(boolean, String) countAttributes} method.
	 * @param ClassName Name of the class to count from.
	 * @return Count of attributes within CodeAttribute table associated with the given ClassName.
	 */
	public int CountSourceAttributes(String ClassName)
	{
		return countAttributes(false, ClassName);
	}
	
	/**
	 * Calls the {@link #countMethods(boolean, String) countMethods} method.
	 * @param ClassName Name of the class to count from.
	 * @return Count of methods within UMLMethod table associated with the given ClassName.
	 */
	public int CountUMLMethods(String ClassName)
	{
		return countMethods(true, ClassName);
	}
	
	/**
	 * Calls the {@link #countMethods(boolean, String) countMethods} method.
	 * @param ClassName Name of the class to count from.
	 * @return Count of methods within CodeMethod table associated with the given ClassName.
	 */
	public int CountSourceMethods(String ClassName)
	{
		return countMethods(false, ClassName);
	}
	
	/**
	 * Calls the {@link #countReferencesOf(boolean, String) countReferencesOf} method.
	 * @param ClassName Name of the base class to count from.
	 * @return Count of references of the base class ClassName within the UMLReference table.
	 */
	public int CountUMLReferencesOf(String ClassName)
	{
		return countReferencesOf(true, ClassName);
	}
	
	/**
	 * Calls the {@link #countReferencesOf(boolean, String) countReferencesOf} method.
	 * @param ClassName 
	 * 		Name of the base class to count from
	 * @return Count of references of the base class ClassName within the CodeReference table.
	 */
	public int CountSourceReferencesOf(String ClassName)
	{
		return countReferencesOf(false, ClassName);
	}
	
	/**
	 * Calls the {@link #countReferencesTo(boolean, String)}
	 * @param ClassName 
	 * 		Name of the referenced class to count
	 * @return Count of references to the referenced ClassName within the UMLReference table.
	 */
	public int CountUMLReferencesTo(String ClassName)
	{
		return countReferencesTo(true, ClassName);
	}
	
	/**
	 * Calls the {@link #countReferencesTo(boolean, String)}
	 * @param ClassName 
	 * 		Name of the referenced class to count
	 * @return Count of references to the referenced ClassName within the CodeReference table.
	 */
	public int CountSourceReferencesTo(String ClassName)
	{
		return countReferencesTo(false, ClassName);
	}
	
	/**
	 * Commit must be called to save data changes made by NewUML, NewSource, Match, and Relate methods.
	 */
	public void Commit()
	{
		try
		{
			this._c.commit();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
		}
	}
	
	/**
	 * @return true if the database has been initialized.
	 */
	public boolean IsInitialized()
	{
		return _isInit;
	}
	
	/**
	 * @return true if a database connection has been opened.
	 */
	public boolean IsOpen()
	{
		if(this._c == null)
		{
			return false;
		}
		try
		{
			return !this._c.isClosed();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Relates Class_ID of Attribute, Method, and Reference entries.
	 */
	public void Relate()
	{
		try
		{
			String strUpdateAttributesUML = "Update	UMLAttribute Set Class_ID = (Select UMLClass.Class_ID From UMLClass Where UMLClass.Classname = UMLAttribute.Classname)";
			
			String strUpdateAttributesCode = "Update CodeAttribute Set Class_ID = (Select CodeClass.Class_ID From CodeClass Where CodeClass.Classname = CodeAttribute.Classname)";
			
			String strUpdateMethodsUML = "Update UMLMethod Set Class_ID = (Select CodeClass.Class_ID From CodeClass Where CodeClass.Classname = UMLMethod.Classname)";
			
			String strUpdateMethodsCode = "Update CodeMethod Set Class_ID = (Select CodeClass.Class_ID From CodeClass Where CodeClass.Classname = CodeMethod.Classname)";
			
			String strUpdateReferencesUML = "Update UMLReference Set Class_ID = (Select UMLClass.Class_ID From UMLClass Where UMLClass.ClassName = UMLReference.ClassName), Ref_Class_ID = (Select UMLClass.Class_ID From UMLClass Where UMLClass.ClassName = UMLReference.RefClassName)";
			
			String strUpdateReferencesCode = "Update CodeReference Set Class_ID = (Select CodeClass.Class_ID From CodeClass Where CodeClass.ClassName = CodeReference.ClassName), Ref_Class_ID = (Select CodeClass.Class_ID From CodeClass Where CodeClass.ClassName = CodeReference.RefClassName)";
			
			Statement stmntUpdateAttributesUML = this._c.createStatement();
			Statement stmntUpdateAttributesCode = this._c.createStatement();
			Statement stmntUpdateMethodsUML = this._c.createStatement();
			Statement stmntUpdateMethodsCode = this._c.createStatement();
			Statement stmntUpdateReferencesUML = this._c.createStatement();
			Statement stmntUpdateReferencesCode = this._c.createStatement();
			
			stmntUpdateAttributesUML.executeUpdate(strUpdateAttributesUML);
			stmntUpdateAttributesCode.executeUpdate(strUpdateAttributesCode);
			stmntUpdateMethodsUML.executeUpdate(strUpdateMethodsUML);
			stmntUpdateMethodsCode.executeUpdate(strUpdateMethodsCode);
			stmntUpdateReferencesUML.executeUpdate(strUpdateReferencesUML);
			stmntUpdateReferencesCode.executeUpdate(strUpdateReferencesCode);
			
			stmntUpdateAttributesUML.close();
			stmntUpdateAttributesCode.close();
			stmntUpdateMethodsUML.close();
			stmntUpdateMethodsCode.close();
			stmntUpdateReferencesUML.close();
			stmntUpdateReferencesCode.close();
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates Other_ID of each table record to the corresponding ID of the opposite type. Calculates NumMismatched value of each record.
	 */
	public void Match()
	{
		try
		{
			String strUpdateClassCode = "Update CodeClass Set Other_ID = (Select UMLClass.Class_ID From UMLClass Where UMLClass.ClassName = CodeClass.ClassName)";
			String strUpdateClassUML = "Update UMLClass Set Other_ID = (Select CodeClass.Class_ID From CodeClass Where CodeClass.ClassName = UMLClass.ClassName)";
			String strUpdateAttributeCode = "Update CodeAttribute Set Other_ID = (Select UMLAttribute.Attribute_ID From UMLAttribute Where UMLAttribute.ClassName = CodeAttribute.ClassName And UMLAttribute.Name = CodeAttribute.Name)";
			String strUpdateAttributeUML = "Update UMLAttribute Set Other_ID = (Select CodeAttribute.Attribute_ID From CodeAttribute Where CodeAttribute.ClassName = UMLAttribute.ClassName And CodeAttribute.Name = UMLAttribute.Name)";
			String strUpdateMethodCode = "Update CodeMethod Set Other_ID = (Select UMLMethod.Method_ID From UMLMethod Where UMLMethod.ClassName = CodeMethod.ClassName And UMLMethod.Name = CodeMethod.Name)";
			String strUpdateMethodUML = "Update UMLMethod Set Other_ID = (Select CodeMethod.Method_ID From CodeMethod Where CodeMethod.ClassName = UMLMethod.ClassName And CodeMethod.Name = UMLMethod.Name)";
			String strUpdateReferenceCode = "Update CodeReference Set Other_ID = (Select UMLReference.Reference_ID From UMLReference Where UMLReference.ClassName = CodeReference.ClassName And UMLReference.RefClassName = CodeReference.RefClassName)";
			String strUpdateReferenceUML = "Update UMLReference Set Other_ID = (Select CodeReference.Reference_ID From CodeReference Where CodeReference.ClassName = UMLReference.ClassName And CodeReference.RefClassName = UMLReference.RefClassName)";

			Statement stmntUpdateClassCode = this._c.createStatement();
			Statement stmntUpdateClassUML = this._c.createStatement();
			Statement stmntUpdateAttributeCode = this._c.createStatement();
			Statement stmntUpdateAttributeUML = this._c.createStatement();
			Statement stmntUpdateMethodCode = this._c.createStatement();
			Statement stmntUpdateMethodUML = this._c.createStatement();
			Statement stmntUpdateReferenceCode = this._c.createStatement();
			Statement stmntUpdateReferenceUML = this._c.createStatement();

			stmntUpdateClassCode.executeUpdate(strUpdateClassCode);
			stmntUpdateClassUML.executeUpdate(strUpdateClassUML);
			stmntUpdateAttributeCode.executeUpdate(strUpdateAttributeCode);
			stmntUpdateAttributeUML.executeUpdate(strUpdateAttributeUML);
			stmntUpdateMethodCode.executeUpdate(strUpdateMethodCode);
			stmntUpdateMethodUML.executeUpdate(strUpdateMethodUML);
			stmntUpdateReferenceCode.executeUpdate(strUpdateReferenceCode);
			stmntUpdateReferenceUML.executeUpdate(strUpdateReferenceUML);
			
			stmntUpdateClassCode.close();
			stmntUpdateClassUML.close();
			stmntUpdateAttributeCode.close();
			stmntUpdateAttributeUML.close();
			stmntUpdateMethodCode.close();
			stmntUpdateMethodUML.close();
			stmntUpdateReferenceCode.close();
			stmntUpdateReferenceUML.close();
			
			String strUpdateCodeClassMismatch = "Update CodeClass Set NumMismatched = ( Select Case When CodeClass.AccessType = UMLClass.AccessType Then 0 Else 1 End + Case When CodeClass.IsStatic = UMLClass.IsStatic Then 0 Else 1 End + Case When CodeClass.IsAbstract = UMLClass.IsAbstract Then 0 Else 1 End + Case When CodeClass.IsFinal = UMLClass.IsFinal Then 0 Else 1 End From UMLClass Where CodeClass.Other_ID = UMLClass.Class_ID )";
			String strUpdateUMLClassMismatch = "Update UMLClass Set NumMismatched = ( Select Case When CodeClass.AccessType = UMLClass.AccessType Then 0 Else 1 End + Case When CodeClass.IsStatic = UMLClass.IsStatic Then 0 Else 1 End + Case When CodeClass.IsAbstract = UMLClass.IsAbstract Then 0 Else 1 End + Case When CodeClass.IsFinal = UMLClass.IsFinal Then 0 Else 1 End From CodeClass Where CodeClass.Other_ID = UMLClass.Class_ID )";
			String strUpdateCodeAttributeMismatch = "Update CodeAttribute Set NumMismatched = ( Select Case When CodeAttribute.AccessType = UMLAttribute.AccessType Then 0 Else 1 End + Case When CodeAttribute.Type = UMLAttribute.Type Then 0 Else 1 End From UMLAttribute Where UMLAttribute.Other_ID = CodeAttribute.Attribute_ID )";
			String strUpdateUMLAttributeMismatch = "Update UMLAttribute Set NumMismatched = ( Select Case When CodeAttribute.AccessType = UMLAttribute.AccessType Then 0 Else 1 End + Case When CodeAttribute.Type = UMLAttribute.Type Then 0 Else 1 End From CodeAttribute Where CodeAttribute.Other_ID = UMLAttribute.Attribute_ID )";
			String strUpdateCodeMethodMismatch = "Update CodeMethod Set NumMismatched = ( Select Case When CodeMethod.AccessType = UMLMethod.AccessType Then 0 Else 1 End + Case When CodeMethod.Type = UMLMethod.Type Then 0 Else 1 End + Case When CodeMethod.Parameters = UMLMethod.Parameters Then 0 Else 1 End From UMLMethod Where UMLMethod.Other_ID = CodeMethod.Method_ID )";
			String strUpdateUMLMethodMismatch = "Update UMLMethod Set NumMismatched = ( Select Case When CodeMethod.AccessType = UMLMethod.AccessType Then 0 Else 1 End + Case When CodeMethod.Type = UMLMethod.Type Then 0 Else 1 End + Case When CodeMethod.Parameters = UMLMethod.Parameters Then 0 Else 1 End From CodeMethod Where CodeMethod.Other_ID = UMLMethod.Method_ID )";
			String strUpdateCodeReferenceMismatch = "Update CodeReference Set NumMismatched = ( Select Case When CodeReference.AccessType = UMLReference.AccessType Then 0 Else 1 End From UMLReference Where UMLReference.Other_ID = CodeReference.Reference_ID )";
			String strUpdateUMLReferenceMismatch = "Update UMLReference Set NumMismatched = ( Select Case When CodeReference.AccessType = UMLReference.AccessType Then 0 Else 1 End From CodeReference Where CodeReference.Other_ID = UMLReference.Reference_ID )";
			
			Statement stmntUpdateCodeClassMismatch = this._c.createStatement();
			Statement stmntUpdateUMLClassMismatch = this._c.createStatement();
			Statement stmntUpdateCodeAttributeMismatch = this._c.createStatement();
			Statement stmntUpdateUMLAttributeMismatch = this._c.createStatement();
			Statement stmntUpdateCodeMethodMismatch = this._c.createStatement();
			Statement stmntUpdateUMLMethodMismatch = this._c.createStatement();
			Statement stmntUpdateCodeReferenceMismatch = this._c.createStatement();
			Statement stmntUpdateUMLReferenceMismatch = this._c.createStatement();
			
			stmntUpdateCodeClassMismatch.executeUpdate(strUpdateCodeClassMismatch);
			stmntUpdateUMLClassMismatch.executeUpdate(strUpdateUMLClassMismatch);
			stmntUpdateCodeAttributeMismatch.executeUpdate(strUpdateCodeAttributeMismatch);
			stmntUpdateUMLAttributeMismatch.executeUpdate(strUpdateUMLAttributeMismatch);
			stmntUpdateCodeMethodMismatch.executeUpdate(strUpdateCodeMethodMismatch);
			stmntUpdateUMLMethodMismatch.executeUpdate(strUpdateUMLMethodMismatch);
			stmntUpdateCodeReferenceMismatch.executeUpdate(strUpdateCodeReferenceMismatch);
			stmntUpdateUMLReferenceMismatch.executeUpdate(strUpdateUMLReferenceMismatch);
			
			stmntUpdateCodeClassMismatch.close();
			stmntUpdateUMLClassMismatch.close();
			stmntUpdateCodeAttributeMismatch.close();
			stmntUpdateUMLAttributeMismatch.close();
			stmntUpdateCodeMethodMismatch.close();
			stmntUpdateUMLMethodMismatch.close();
			stmntUpdateCodeReferenceMismatch.close();
			stmntUpdateUMLReferenceMismatch.close();
			
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			
		}
	}
	
	/**
	 * Re-initializes the database. Calls {@link #Close()} sets {@link #_isInit} and {@link #_hasCreatedDB} to false then calls {@link #Open()} and {@link #InitDatabase()}.
	 * This deletes the database and re-creates it using new structures.
	 */
	public void ReInitDatabase()
	{
		if(this._c != null)
		{
			this.Close();
		}
		
		UTMDB._isInit = false;
		UTMDB._hasCreatedDB = false;
		
		this.Open();
		
		this.InitDatabase();
	}
	
	/**
	 * Creates the database structures.
	 */
	public void InitDatabase()
	{
		if(UTMDB._isInit)
		{
			return;
		}
		
		try
		{	
			Statement stmntCreateUMLClass = this._c.createStatement();
			Statement stmntCreateUMLAttribute = this._c.createStatement();
			Statement stmntCreateUMLMethod = this._c.createStatement();
			Statement stmntCreateUMLReference = this._c.createStatement();
			Statement stmntCreateCodeClass = this._c.createStatement();
			Statement stmntCreateCodeAttribute = this._c.createStatement();
			Statement stmntCreateCodeMethod = this._c.createStatement();
			Statement stmntCreateCodeReference = this._c.createStatement();
			
			String	strCreateFileParams = 
					"Filename		Text		Null," +
					"LineNumber		Integer		Null"
			;
			
			String strMatchTracking = 
					"Other_ID		Integer		Null Default -1," +
					"NumMismatched	Integer		Null Default 0"
			;
			
			// Table Sources
			String strCreateClass = 
					"(" +
					"Class_ID		Integer		Not Null Primary Key AutoIncrement," +
					strCreateFileParams + "," +
					"ClassName		Text		Not Null," +
					"AccessType		Text		Null," +
					"IsStatic		Boolean		Null Default 0," +
					"IsAbstract		Boolean		Null Default 0," +	
					"IsFinal		Boolean		Null Default 0," +
					strMatchTracking +
					")"
			;
			
			String strCreateAttribute = 
					"(" +
					"Attribute_ID		Integer		Not Null Primary Key AutoIncrement," +
					"Class_ID			Integer		Null," +
					strCreateFileParams + "," +
					"ClassName			Text		Not Null," +
					"AccessType			Text		Not Null Default 'No Modifier'," +
					"Name				Text		Not Null," +
					"Type				Text		Null," +
					strMatchTracking +
					")"
			;
			
			String strCreateMethod =
					"(" +
					"Method_ID			Integer		Not Null Primary Key AutoIncrement," +
					"Class_ID			Integer		Null," +
					strCreateFileParams + "," +
					"ClassName			Text		Not Null," +
					"AccessType			Text		Not Null Default 'No Modifier'," +
					"Type				Text		Not Null," +
					"Name				Text		Not Null," +
					"Parameters			Text		Null," +
					strMatchTracking +
					")"
			;
			
			String strCreateReference = 
					"(" +
					"Reference_ID		Integer		Not Null Primary Key AutoIncrement," +
					"Class_ID			Integer		Null," +
					"ClassName			Text		Not Null," +
					"AccessType			Text		Not Null Default 'No Modifier'," +
					"Ref_Class_ID		Integer		Null," +
					"RefClassName		Text		Not Null," +
					strMatchTracking +
					")"
			;
			
			// Table Structures
			// UML
			String strCreateUMLClass =
					"Create Table UMLClass" +
					strCreateClass
			;
			
			String strCreateUMLAttribute =
					"Create Table UMLAttribute" +
					strCreateAttribute
			;
			
			String strCreateUMLMethod = 
					"Create Table UMLMethod" +
					strCreateMethod
			;
			
			String strCreateUMLReference =
					"Create Table UMLReference" +
					strCreateReference
			;
			
			// Code
			String strCreateCodeClass =
					"Create Table CodeClass" +
					strCreateClass
			;
			
			String strCreateCodeAttribute =
					"Create Table CodeAttribute" +
					strCreateAttribute
			;
			
			String strCreateCodeMethod = 
					"Create Table CodeMethod" +
					strCreateMethod
			;
			
			String strCreateCodeReference =
					"Create Table CodeReference" +
					strCreateReference
			;

			// Create Tables
			stmntCreateUMLClass.executeUpdate(strCreateUMLClass);
			stmntCreateUMLAttribute.executeUpdate(strCreateUMLAttribute);
			stmntCreateUMLMethod.executeUpdate(strCreateUMLMethod);
			stmntCreateUMLReference.executeUpdate(strCreateUMLReference);
			stmntCreateCodeClass.executeUpdate(strCreateCodeClass);
			stmntCreateCodeAttribute.executeUpdate(strCreateCodeAttribute);
			stmntCreateCodeMethod.executeUpdate(strCreateCodeMethod);
			stmntCreateCodeReference.executeUpdate(strCreateCodeReference);
			
			// Close Statements
			stmntCreateUMLClass.close();
			stmntCreateUMLAttribute.close();
			stmntCreateUMLMethod.close();
			stmntCreateUMLReference.close();
			stmntCreateCodeClass.close();
			stmntCreateCodeAttribute.close();
			stmntCreateCodeMethod.close();
			stmntCreateCodeReference.close();
			
			UTMDB._isInit = true;
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens a connection to the database.
	 * @return true if the database is open. false if there was an error.
	 */
	public boolean Open()
	{
		if(this.IsOpen())
		{
			return true;
		}
		try{
			String dbpath;
			try{
				dbpath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString()
						+ System.getProperty("file.separator") + "utm.db";
			}
			catch(Exception e){
				dbpath = "utm.db";
			}
			
			if(!UTMDB._hasCreatedDB)
			{
				File file = new File(dbpath);
				if(file.exists())
				{
					file.delete();
				}
			}
			
			Class.forName("org.sqlite.JDBC");
			this._c = DriverManager.getConnection("jdbc:sqlite:" + dbpath);
			
			this._c.setAutoCommit(false);
			
			UTMDB._hasCreatedDB = true;
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Closes a connection to the database.
	 * @return true if the database is closed. false if an error occurred.
	 */
	public boolean Close()
	{
		try
		{
			if(!this._c.isClosed())
			{
				this._c.close();
			}
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public UTMDB()
	{
	}
	
	/**
	 * Closes the database connection.
	 */
	public void finalize()
	{
		try
		{
			if(!this._c.isClosed())
			{
				this._c.close();
			}
		}
		catch(Exception e)
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
		}
	}
}
