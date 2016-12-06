package org.eclipse.utm.test;

import java.io.File;
import java.net.URI;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;

/**
 * @author Marcelo Paternostro (mpaternostro@gmail.com)
 */
public class PlatformURITest extends TestCase {
	public void testFileURL() throws Exception {
		assertURI("platform:/plugin/org.eclipse.osgi", true);
		assertURI("platform:/plugin/org.eclipse.core.runtime", true);

		assertURI("platform:/config/", false);
		
		assertURI("platform:/meta/", false);

		// This bundle
		assertURI("platform:/plugin/org.eclipse.utm.test/build.properties", false);
	}

	private void assertURI(String uriString, boolean isBundleDirOrJar) throws Exception {
		URI uri = new URI(uriString);
		URL url = FileLocator.toFileURL(uri.toURL());
		System.out.println("\nURI: " + uri + "\nFile URL: " + url);

		File file = new File(url.toURI());
		assertTrue(file.getAbsolutePath(), file.exists());

		if (isBundleDirOrJar) {
			if (file.isDirectory()) {
				File manifestFile = new File(file, "META-INF/MANIFEST.MF");
				assertTrue(manifestFile.getAbsolutePath(), manifestFile.isFile());
			} else {
				assertTrue(file.getAbsolutePath(), file.getName().endsWith(".jar"));
			}
		}
	}
}
