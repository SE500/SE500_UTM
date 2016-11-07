package org.eclipse.utm.test;

import java.io.File;
import java.io.IOException;

import org.eclipse.utm.compare.UTMDB;
import org.eclipse.utm.parseSource.ParseSource;
import org.eclipse.utm.parseUML.ParseUML;
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
		UTMDB db = null;
		
		File selectedSource = ParseSource.selectSource();
		File selectedModel = ParseUML.selectUmlFile();
		
		
		if(selectedSource != null) {
			ParseSource parseSource = new ParseSource(selectedSource);
			sourceParsed = parseSource.launch();
		}
		else {
			System.err.println("Error: No Source File or Folder selected.");
		}
		if(selectedModel != null) {
			ParseUML parseUML = new ParseUML(selectedModel);
			umlParsed = parseUML.launch(true);
		}
		else {
			System.err.println("Error: No UML File selected.");
		}
		
		if(sourceParsed && umlParsed) {
			db = new UTMDB();
			db.Open();
			db.InitDatabase();
			db.Relate();
			db.Match();
			db.Commit();
			System.out.println("UML Class Count:\t" + db.CountUMLClasses());
			System.out.println("Source Class Count:\t" + db.CountSourceClasses());
			System.out.println("UML Attribute Count:\t" + db.CountUMLAttributes());
			System.out.println("Source Attribute Count:\t" + db.CountSourceAttributes());
			System.out.println("UML Method Count:\t" + db.CountUMLMethods());
			System.out.println("Source Method Count:\t" + db.CountSourceMethods());
		}
		System.exit(0);
	}
}
