package org.eclipse.utm.test;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.utm.parseUML.ParseUML;
/**
 * 
 * @author Thomas Colborne
 * Testing the ParseUML feature functionality
 *
 */
public class TestParseUML {

	public static void main(String[] args) throws IOException {
		File selectedModel = ParseUML.selectUmlFile();
		if(selectedModel != null) {
			ParseUML test = new ParseUML(selectedModel);
			test.launch(new NullProgressMonitor());
			
		}
		else {
			System.err.println("Error: No UML File selected.");
		}
		System.exit(0);	
	}
}
