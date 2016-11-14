package org.eclipse.utm.compare;

import java.io.File;
import java.sql.*;

public final class UTMDB {
	
	private Connection _c = null;
	
	private static boolean _isInit = false;
	
	private static boolean _hasCreatedDB = false;
	
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
			
			int rowc = stmntInsert.executeUpdate();
			
			ResultSet res = stmntInsert.getGeneratedKeys();
			res.next();

			int ret = res.getInt(1);
			System.out.println("newClass " + ret);
			
			res.close();
			stmntInsert.close();
			
			return ret;
		}
		catch(Exception e)
		{
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
			return 0;
		}
	}
	
	private int newReference(boolean IsFromUML, String ClassName, String AccessType, String RefClass)
	{
		if(true)
		{
			return 0;
		}
		
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
			
			int rowc = stmntInsert.executeUpdate();
			
			ResultSet res = stmntInsert.getGeneratedKeys();
			res.next();

			int ret = res.getInt(1);
			System.out.println("newReference " + ret);
			
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
			
			int rowc = stmntInsert.executeUpdate();
			
			ResultSet res = stmntInsert.getGeneratedKeys();
			res.next();

			int ret = res.getInt(1);
			System.out.println("newAttribute " + ret);
			
			stmntInsert.close();
			
			return ret;
		}
		catch(Exception e)
		{
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
			return 0;
		}
	}
	
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
			
			int rowc = stmntInsert.executeUpdate();
			
			ResultSet res = stmntInsert.getGeneratedKeys();
			res.next();

			int ret = res.getInt(1);
			System.out.println("newMethod " + ret);
			
			res.close();
			stmntInsert.close();
			
			return ret;
		}
		catch(Exception e)
		{
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
			return 0;
		}
	}
	
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
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
			return -1;
		}
		return count;
	}
	
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
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
			return -1;
		}
		return count;
	}
	
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
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
			return -1;
		}
		return count;
	}
	
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
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
			return -1;
		}
		
		return count;
	}
	
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
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
			return -1;
		}
		return count;
	}
	
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
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
			return -1;
		}
		return count;
	}
	
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
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
			return -1;
		}
		return count;
	}
	
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
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
			return -1;
		}
		return count;
	}
	
	public int NewUMLClass(String Filename, String ClassName, String AccessType, boolean IsStatic, boolean IsAbstract, boolean IsFinal)
	{
		return this.newClass(true, Filename, 0, ClassName, AccessType, IsStatic, IsAbstract, IsFinal);
	}
	
	public int NewSourceClass(String Filename, int LineNumber, String ClassName, String AccessType, boolean IsStatic, boolean IsAbstract, boolean IsFinal)
	{
		return this.newClass(false, Filename, LineNumber, ClassName, AccessType, IsStatic, IsAbstract, IsFinal);
	}
	
	public int NewUMLReference(String ClassName, String AccessType, String RefClass)
	{
		return this.newReference(true, ClassName, AccessType, RefClass);
	}
	
	public int NewSourceReference(String ClassName, String AccessType, String RefClass)
	{
		return this.newReference(false, ClassName, AccessType, RefClass);
	}
	
	public int NewUMLAttribute(String Filename, String ClassName, String AccessType, String Type, String Name)
	{
		return this.newAttribute(true, Filename, 0, ClassName, AccessType, Type, Name);
	}
	
	public int NewSourceAttribute(String Filename, int LineNumber, String ClassName, String AccessType, String Type, String Name)
	{
		return this.newAttribute(false, Filename, LineNumber, ClassName, AccessType, Type, Name);
	}
	
	public int NewUMLMethod(String Filename, String ClassName, String AccessType, String Type, String Name, String Params)
	{
		return this.newMethod(true, Filename, 0, ClassName, AccessType, Type, Name, Params);
	}
	
	public int NewSourceMethod(String Filename, int LineNumber, String ClassName, String AccessType, String Type, String Name, String Params)
	{
		return this.newMethod(false, Filename, LineNumber, ClassName, AccessType, Type, Name, Params);
	}
	
	public int CountUMLClasses()
	{
		return countClasses(true);
	}
	
	public int CountSourceClasses()
	{
		return countClasses(false);
	}
	
	public int CountUMLAttributes()
	{
		return countAttributes(true);
	}
	
	public int CountSourceAttributes()
	{
		return countAttributes(false);
	}
	
	public int CountUMLMethods()
	{
		return countMethods(true);
	}
	
	public int CountSourceMethods()
	{
		return countMethods(false);
	}
	
	public int CountUMLReferences()
	{
		return countReferences(true);
	}
	
	public int CountSourceReferences()
	{
		return countReferences(false);
	}
	
	public int CountUMLAttributes(String ClassName)
	{
		return countAttributes(true, ClassName);
	}
	
	public int CountSourceAttributes(String ClassName)
	{
		return countAttributes(false, ClassName);
	}
	
	public int CountUMLMethods(String ClassName)
	{
		return countMethods(true, ClassName);
	}
	
	public int CountSourceMethods(String ClassName)
	{
		return countMethods(false, ClassName);
	}
	
	public int CountUMLReferencesOf(String ClassName)
	{
		return countReferencesOf(true, ClassName);
	}
	
	public int CountSourceReferencesOf(String ClassName)
	{
		return countReferencesOf(false, ClassName);
	}
	
	public int CountUMLReferencesTo(String ClassName)
	{
		return countReferencesTo(true, ClassName);
	}
	
	public int CountSourceReferencesTo(String ClassName)
	{
		return countReferencesTo(false, ClassName);
	}
	
	public void Commit()
	{
		try
		{
			this._c.commit();
		}
		catch(Exception e)
		{
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	public boolean IsInitialized()
	{
		return this._isInit;
	}
	
	public boolean IsOpen()
	{
		try
		{
			return !this._c.isClosed();
		}
		catch(Exception e)
		{
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}
	}
	
	public void Relate()
	{
		try
		{
			String strUpdateAttributesUML = "Update	UMLAttribute Set Class_ID = (Select UMLClass.Class_ID From UMLClass Where UMLClass.Classname = UMLAttribute.Classname)";
			
			String strUpdateAttributesCode = "Update CodeAttribute Set Class_ID = (Select CodeClass.Class_ID From CodeClass Where CodeClass.Classname = CodeAttribute.Classname)";
			
			String strUpdateMethodsUML = "Update UMLMethod Set Class_ID = (Select CodeClass.Class_ID From CodeClass Where CodeClass.Classname = UMLMethod.Classname)";
			
			String strUpdateMethodsCode = "Update	CodeMethod Set Class_ID = (Select CodeClass.Class_ID From CodeClass Where CodeClass.Classname = CodeMethod.Classname)";
			
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
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
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
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
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
					"Ref_Class_ID		Integer		Not Null," +
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
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	public boolean Open()
	{
		try{
			if(!UTMDB._hasCreatedDB)
			{
				File file = new File("utm.db");
				if(file.exists())
				{
					file.delete();
				}
			}
			
			Class.forName("org.sqlite.JDBC");
			this._c = DriverManager.getConnection("jdbc:sqlite:utm.db");
			
			this._c.setAutoCommit(false);
			
			UTMDB._hasCreatedDB = true;
		}
		catch(Exception e)
		{
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}
		return true;
	}
	
	public boolean Close()
	{
		try
		{
			this._c.close();
		}
		catch(Exception e)
		{
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}
		return true;
	}
	
	public UTMDB()
	{
	}
	
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
			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
}
