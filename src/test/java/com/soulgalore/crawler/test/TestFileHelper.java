package com.soulgalore.crawler.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class TestFileHelper {

	private TestFileHelper() {
		
	}
	
	/**
	 * Fetch a text file, return each line as a String of a Set. Note, every row
	 * need to be unique, else only one of them will be returned.
	 * 
	 * @param fileName
	 *            is the name of the file, located in the class path
	 * @return the text file as a Set of Strings, one String is one row in the
	 *         file
	 * @throws IOException
	 *             if the file isn't found
	 */
	public static List<String> fetchFileFromClasspath(String fileName)
			throws IOException {

		final BufferedReader buffer = new BufferedReader(new InputStreamReader(
				TestFileHelper.class.getResourceAsStream(fileName)));

		final List<String> lines = new LinkedList<String>();

		try {

			String row = buffer.readLine();
			lines.add(row);

			while (row != null) {
				row = buffer.readLine();
				if (row != null)
					lines.add(row);
			}
		} finally {
			buffer.close();
		}

		return lines;
	}
	
	public static String fetchFileFromClasspathAsString(String fileName)
			throws IOException {
		
		List<String> strings = fetchFileFromClasspath(fileName);
		StringBuilder builder = new StringBuilder();
		for (String string : strings) {
			builder.append(string);
		}
		return builder.toString();
		
	}
}
