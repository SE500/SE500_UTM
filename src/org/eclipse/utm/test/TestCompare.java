package org.eclipse.utm.test;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.utm.compare.UTMDB;
import org.eclipse.utm.compare.UTMDBAttribute;
import org.eclipse.utm.compare.UTMDBClass;
import org.eclipse.utm.compare.UTMDBMethod;
import org.eclipse.utm.compare.UTMDBReference;
import org.eclipse.utm.parseSource.ParseSource;
import org.eclipse.utm.parseUML.ParseUML;
import java.sql.*;
/**
 * 
 * @author Thomas Colborne
 * Testing the compare feature functionality
 *
 */
import java.util.ArrayList;
public class TestCompare {

	public static void main(String[] args) throws IOException {
		// Read/Create New Elements
		{
			UTMDB db = new UTMDB();
			if(!db.Open())
			{
				System.err.println("DB.Open Failed");
			}
			else
			{
				db.InitDatabase();
				db.NewSourceClass("ClassA.java", 1, "ClassA", "public", false, false, false);
				db.NewSourceReference("ClassA", "extends", "ClassB");
				db.NewSourceReference("ClassA", "implements", "UIFormElementClass");
				db.NewSourceReference("ClassA", "implements", "UIPanel");
				db.NewSourceAttribute("ClassA.java", 4, "ClassA", "private", "boolean", "_isInit");
				db.NewSourceAttribute("ClassA.java", 5, "ClassA", "private", "int", "_counter");
				db.NewSourceAttribute("ClassA.java", 6, "ClassA", "public", "String", "Type");
				db.NewSourceAttribute("ClassA.java", 8, "ClassA", "public", "UIPanelControl", "Control");
				db.NewSourceMethod("ClassA.java", 10, "ClassA", "public", "void", "DoTheThing", "int num, boolean isTrue");
				db.NewSourceMethod("ClassA.java", 19, "ClassA", "public", "boolean", "IsSomethingDone", "int num");
				db.NewSourceMethod("ClassA.java", 42, "ClassA", "public", "void", "WriteAnother", "String written");
				db.NewSourceMethod("ClassA.java", 58, "ClassA", "public", "int", "GetCounter", "");
	
				db.NewSourceClass("ClassB.java", 1, "ClassB", "public", false, true, false);
				db.NewSourceAttribute("ClassB.java", 4, "ClassB", "private", "boolean", "_isGood");
				db.NewSourceMethod("ClassB.java", 10, "ClassB", "public", "void", "SetIsGood", "boolean isGood");
				db.NewSourceMethod("ClassB.java", 19, "ClassB", "public", "boolean", "GetIsGood", "");
				db.NewSourceMethod("ClassB.java", 42, "ClassB", "public", "void", "WriteSomething", "String written");
	
				db.NewUMLClass("JavaSystem.uml", "ClassA", "public", false, false, false);
				db.NewUMLReference("ClassA", "extends", "ClassB");
				db.NewUMLReference("ClassA", "implements", "UIFormElementClass");
				db.NewUMLReference("ClassA", "implements", "UIPanel");
				db.NewUMLAttribute("JavaSystem.uml", "ClassA", "private", "boolean", "_isInit");
				db.NewUMLAttribute("JavaSystem.uml", "ClassA", "private", "int", "_counter");
				db.NewUMLAttribute("JavaSystem.uml", "ClassA", "public", "String", "Type");
				db.NewUMLAttribute("JavaSystem.uml", "ClassA", "public", "UIPanelControl", "Control");
				db.NewUMLMethod("JavaSystem.uml", "ClassA", "public", "void", "DoTheThing", "int num, boolean isTrue");
				db.NewUMLMethod("JavaSystem.uml", "ClassA", "public", "boolean", "IsSomethingDone", "int num");
				db.NewUMLMethod("JavaSystem.uml", "ClassA", "public", "void", "WriteAnother", "String written");
				db.NewUMLMethod("JavaSystem.uml", "ClassA", "public", "int", "GetCounter", "");
	
				db.NewUMLClass("JavaSystem.uml", "ClassB", "public", false, true, false);
				db.NewUMLAttribute("JavaSystem.uml", "ClassB", "private", "boolean", "_isGood");
				db.NewUMLMethod("JavaSystem.uml", "ClassB", "public", "void", "SetIsGood", "boolean isGood");
				db.NewUMLMethod("JavaSystem.uml", "ClassB", "public", "boolean", "GetIsGood", "");
				db.NewUMLMethod("JavaSystem.uml", "ClassB", "public", "void", "WriteSomething", "String written");
				
				db.Relate();
				db.Match();
				db.Commit();
				db.Close();
			}
		}
		
		// Retrieve Comparison Results
		{
			UTMDB db = new UTMDB();
			if(!db.Open())
			{
				System.err.println("DB.Open Failed");
			}
			else
			{
				db.InitDatabase();
				
				System.out.println("Source Classes");
				// List All Source Elements
				float totalSourceNumTotal = 0;
				float totalSourceNumMismatched = 0;
				
				ArrayList<UTMDBClass> SourceClassList = db.GetSourceClassList();
				for(UTMDBClass curClass : SourceClassList)
				{
					float numTotal = 0;
					float numMismatched = 0;
					
					ArrayList<UTMDBReference> SourceClassReference = db.GetSourceReferencesList(curClass.ClassID);
					ArrayList<UTMDBAttribute> SourceAttributeList = db.GetSourceAttributesList(curClass.ClassID);
					ArrayList<UTMDBMethod> SourceMethodList = db.GetSourceMethodsList(curClass.ClassID);
					
					System.out.println(curClass.ClassName + (curClass.NumMismatched > 0 || curClass.OtherID < 1 ? "**" : ""));
					for(UTMDBReference ref : SourceClassReference)
					{
						numTotal++;
						if(ref.OtherID <= 0)
						{
							numMismatched++;
						}
						System.out.println("\t- " + ref.AccessType + " " + ref.ReferenceClassName + (ref.OtherID <= 0 ? "*" : ""));
					}
					System.out.println("\tClassID: " + curClass.ClassID);
					System.out.println("\tLocation: " + curClass.Filename + ":" + curClass.LineNumber);
					System.out.println("\tAccess: " + curClass.AccessType);
					System.out.println("\tStatic: " + (curClass.IsStatic ? "true" : "false"));
					System.out.println("\tAbstract: " + (curClass.IsAbstract ? "true" : "false"));
					System.out.println("\tFinal: " + (curClass.IsFinal ? "true" : "false"));
					System.out.println("\tOtherID: " + curClass.OtherID);
					System.out.println("\tNumMismatched: " + curClass.NumMismatched);
					System.out.println("\tAttributes: ");
					for(UTMDBAttribute attr : SourceAttributeList)
					{
						UTMDBAttribute otherAttr = db.GetUMLAttribute(attr.OtherID);
						System.out.println(
								"\t\t" + 
								attr.AccessType + (otherAttr == null || otherAttr.AccessType.compareTo(attr.AccessType) != 0 ? "*" : "") + " " + 
								attr.Type + (otherAttr == null || otherAttr.Type.compareTo(attr.Type) != 0 ? "*" : "") + " " + 
								attr.Name + (attr.NumMismatched > 0 || attr.OtherID < 1 ? "**" : "")
						);
						System.out.println("\t\t\tAttributeID: " + attr.AttributeID);
						System.out.println("\t\t\tClassID: " + attr.ClassID);
						System.out.println("\t\t\tLocation: " + attr.Filename + ":" + attr.LineNumber);
						System.out.println("\t\t\tClassName: " + attr.ClassName);
						System.out.println("\t\t\tOtherID: " + attr.OtherID);
						System.out.println("\t\t\tNumMismatched: " + attr.NumMismatched);
						
						numTotal++;
						if(attr.NumMismatched > 0)
						{
							numMismatched++;
						}
					}
					System.out.println("\tMethods: ");
					for(UTMDBMethod method : SourceMethodList)
					{
						UTMDBMethod otherMethod = db.GetUMLMethod(method.OtherID);
						System.out.println(
								"\t\t" + 
								method.AccessType + (otherMethod == null || otherMethod.AccessType.compareTo(method.AccessType) != 0 ? "*" : "") + " " + 
								method.Type + (otherMethod == null || otherMethod.Type.compareTo(method.Type) != 0 ? "*" : "") + " " + 
								method.Name + 
								"(" + 
										method.Parameters + 
										(otherMethod == null || otherMethod.Parameters.compareTo(method.Parameters) != 0 ? "*" : "")  + 
								")" + 
								(method.NumMismatched > 0 || method.OtherID < 1 ? "**" : "")
						);
						System.out.println("\t\t\tMethodID: " + method.MethodID);
						System.out.println("\t\t\tClassID: " + method.ClassID);
						System.out.println("\t\t\tLocation: " + method.Filename + ":" + method.LineNumber);
						System.out.println("\t\t\tClassName: " + method.ClassName);
						System.out.println("\t\t\tOtherID: " + method.OtherID);
						System.out.println("\t\t\tNumMismatched: " + method.NumMismatched);
						
						numTotal++;
						if(method.NumMismatched > 0)
						{
							numMismatched++;
						}
					}
					
					System.out.println(curClass.ClassName + " " + Math.round(numMismatched) + " Mismatched of " + Math.round(numTotal) + " Elements (" + ((numTotal - numMismatched) / numTotal) * 100 + "% Matched)");
					
					totalSourceNumTotal += numTotal;
					totalSourceNumMismatched += numMismatched;
				}
				
				System.out.println("Source: " + Math.round(totalSourceNumMismatched) + " Mismatched of " + Math.round(totalSourceNumTotal) + " Elements (" + ((totalSourceNumTotal - totalSourceNumMismatched) / totalSourceNumTotal) * 100 + "% Matched)");
				
				System.out.println("UML Classes");
				// List All UML Elements
				float totalUMLNumTotal = 0;
				float totalUMLNumMismatched = 0;
				
				ArrayList<UTMDBClass> UMLClassList = db.GetUMLClassList();
				for(UTMDBClass curClass : UMLClassList)
				{
					float numTotal = 0;
					float numMismatched = 0;
					
					ArrayList<UTMDBReference> UMLClassReference = db.GetUMLReferencesList(curClass.ClassID);
					ArrayList<UTMDBAttribute> UMLAttributeList = db.GetUMLAttributesList(curClass.ClassID);
					ArrayList<UTMDBMethod> UMLMethodList = db.GetUMLMethodsList(curClass.ClassID);
					
					System.out.println(curClass.ClassName + (curClass.NumMismatched > 0 || curClass.OtherID < 1 ? "**" : ""));
					for(UTMDBReference ref : UMLClassReference)
					{
						System.out.println("\t- " + ref.AccessType + " " + ref.ReferenceClassName);
					}
					System.out.println("\tClassID: " + curClass.ClassID);
					System.out.println("\tLocation: " + curClass.Filename);
					System.out.println("\tAccess: " + curClass.AccessType);
					System.out.println("\tStatic: " + (curClass.IsStatic ? "true" : "false"));
					System.out.println("\tAbstract: " + (curClass.IsAbstract ? "true" : "false"));
					System.out.println("\tFinal: " + (curClass.IsFinal ? "true" : "false"));
					System.out.println("\tOtherID: " + curClass.OtherID);
					System.out.println("\tNumMismatched: " + curClass.NumMismatched);
					System.out.println("\tAttributes: ");
					for(UTMDBAttribute attr : UMLAttributeList)
					{
						UTMDBAttribute otherAttr = db.GetSourceAttribute(attr.OtherID);
						System.out.println(
								"\t\t" + 
								attr.AccessType + (otherAttr == null || otherAttr.AccessType.compareTo(attr.AccessType) != 0 ? "*" : "") + " " + 
								attr.Type + (otherAttr == null || otherAttr.Type.compareTo(attr.Type) != 0 ? "*" : "") + " " + 
								attr.Name + 
								(attr.NumMismatched > 0 || attr.OtherID < 1 ? "**" : "")
						);
						System.out.println("\t\t\tAttributeID: " + attr.AttributeID);
						System.out.println("\t\t\tClassID: " + attr.ClassID);
						System.out.println("\t\t\tLocation: " + attr.Filename);
						System.out.println("\t\t\tClassName: " + attr.ClassName);
						System.out.println("\t\t\tOtherID: " + attr.OtherID);
						System.out.println("\t\t\tNumMismatched: " + attr.NumMismatched);
					}
					System.out.println("\tMethods: ");
					for(UTMDBMethod method : UMLMethodList)
					{
						UTMDBMethod otherMethod = db.GetSourceMethod(method.OtherID);
						System.out.println(
								"\t\t" + 
								method.AccessType + (otherMethod.AccessType.compareTo(method.AccessType) != 0 ? "*" : "") + " " + 
								method.Type + (otherMethod.Type.compareTo(method.Type) != 0 ? "*" : "") + " " + 
								method.Name + 
								"(" + 
										method.Parameters + 
										(otherMethod == null || otherMethod.Parameters.compareTo(method.Parameters) != 0 ? "*" : "")  + 
								")" + 
								(method.NumMismatched > 0 || method.OtherID < 1 ? "**" : "")
						);
						System.out.println("\t\t\tMethodID: " + method.MethodID);
						System.out.println("\t\t\tClassID: " + method.ClassID);
						System.out.println("\t\t\tLocation: " + method.Filename);
						System.out.println("\t\t\tClassName: " + method.ClassName);
						System.out.println("\t\t\tOtherID: " + method.OtherID);
						System.out.println("\t\t\tNumMismatched: " + method.NumMismatched);
						
						// Count Mismatches
						numTotal++;
						if(method.NumMismatched > 0)
						{
							numMismatched++;
						}
					}

					System.out.println(curClass.ClassName + " " + Math.round(numMismatched) + " Mismatched of " + Math.round(numTotal) + " Elements (" + ((numTotal - numMismatched) / numTotal) * 100 + "% Matched)");
					
					totalUMLNumTotal += numTotal;
					totalUMLNumMismatched += numMismatched;
				}
				
				System.out.println("UML: " + Math.round(totalUMLNumMismatched) + " Mismatched of " + Math.round(totalUMLNumTotal) + " Elements (" + ((totalUMLNumTotal - totalUMLNumMismatched) / totalUMLNumTotal) * 100 + "% Matched)");
				
				db.Close();
			}
		}
		System.exit(0);
	}
}
