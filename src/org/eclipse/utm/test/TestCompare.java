package org.eclipse.utm.test;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.utm.compare.UTMDB;
import org.eclipse.utm.parseSource.ParseSource;
import org.eclipse.utm.parseUML.ParseUML;
import java.sql.*;
/**
 * 
 * @author Thomas Colborne
 * Testing the compare feature functionality
 *
 */
public class TestCompare {

	public static void main(String[] args) throws IOException {
		boolean sourceParsed = false;
		boolean umlParsed = false;
		
//		File selectedSource = ParseSource.selectSource();
//		File selectedModel = ParseUML.selectUmlFile();
//		
//		
//		if(selectedSource != null) {
//			ParseSource parseSource = new ParseSource(selectedSource);
//			sourceParsed = parseSource.launch(new NullProgressMonitor());
//		}
//		else {
//			System.err.println("Error: No Source File or Folder selected.");
//		}
//		if(selectedModel != null) {
//			ParseUML parseUML = new ParseUML(selectedModel);
//			umlParsed = parseUML.launch(new NullProgressMonitor(),true);
//		}
//		else {
//			System.err.println("Error: No UML File selected.");
//		}
		
		UTMDB db = new UTMDB();
		if(!db.Open())
		{
			System.err.println("DB.Open Failed");
		}
		db.InitDatabase();
		db.NewSourceReference("MyBaseClass", "extends", "MySuperClass");
		db.NewSourceReference("MyBaseClass", "implements", "MyOtherClass");
		db.NewSourceClass("Somefile.java", 1, "MyBaseClass", "public", false, false, false);
		db.Relate();
		db.Match();
		db.Commit();
		System.out.println("UML Class Count:\t" + db.CountUMLClasses());
		System.out.println("Source Class Count:\t" + db.CountSourceClasses());
		System.out.println("UML Attribute Count:\t" + db.CountUMLAttributes());
		System.out.println("Source Attribute Count:\t" + db.CountSourceAttributes());
		System.out.println("UML Method Count:\t" + db.CountUMLMethods());
		System.out.println("Source Method Count:\t" + db.CountSourceMethods());
		
		System.exit(0);
	}
}
