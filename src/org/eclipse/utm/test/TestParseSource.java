package org.eclipse.utm.test;

import java.io.File;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.utm.parseSource.ParseSource;
/**
 * 
 * @author Thomas Colborne
 * Testing the ParseSource feature functionality
 *
 */
public class TestParseSource {

	public static void main(String[] args) {
		File selectedSource = ParseSource.selectSource();
		if(selectedSource != null) {
			ParseSource readfile = new ParseSource(selectedSource);
			readfile.launch(new NullProgressMonitor());
		}
		else {
			System.err.println("Error: No Source File or Folder selected.");
		}
		System.exit(0);	
	}
}
