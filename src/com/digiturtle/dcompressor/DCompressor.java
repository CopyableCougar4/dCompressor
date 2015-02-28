package com.digiturtle.dcompressor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Simple file compression using dictionary lookup. #, `, and ~ are reserved characters
 * @author Jonathan
 */
public class DCompressor {
	
	private ArrayList<String> splitup = new ArrayList<String>();
	
	/**
	 * Compress the data in an input stream and write it to the output stream
	 * @param input Place for original data
	 * @param output Place for compressed data
	 * @param sectionalSplits Letters that can split a segment
	 */
	public void compress(InputStream input, OutputStream output, char sectionalSplits) {
		// Reader everything
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
			String line = reader.readLine();
			if (line != null) {
				builder.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String decompressed = builder.toString();
		// Go through and split up using every sectional split
		String[] data = decompressed.split(sectionalSplits + "");
		for (String piece : data) {
			splitup.add(piece);
		}
		// Find repeats and generate a dictionary
		HashMap<String, Integer> counts = new HashMap<String, Integer>();
		for (String splitupPiece : splitup) {
			if (counts.containsKey(splitupPiece)) {
				counts.put(splitupPiece, counts.get(splitupPiece) + 1);
			} else {
				counts.put(splitupPiece, 1);
			}
		}
		ArrayList<String> dictionary = new ArrayList<String>();
		for (Entry<String, Integer> entry : counts.entrySet()) {
			if (entry.getValue() > 1) {
				dictionary.add(entry.getKey());
			}
		}
		// Write the dictionary and smaller text to string
		String outputText = "";
		int index = 0;
		for (String key : dictionary) {
			if (index > 0) {
				outputText += "~";
			}
			outputText += key;
			index++;
		}
		outputText += "`";
		String compressed = decompressed;
		index = 0;
		for (String key : dictionary) {
			compressed = compressed.replaceAll(key, "#" + index);
			index++;
		}
		outputText += compressed;
		// Write the string to the output source
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output))) {
			writer.write(outputText);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Decompress an input stream and store it as a String
	 * @param input Input stream
	 * @return Decompressed text
	 */
	public String decompress(InputStream input) {
		// Reader everything
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
			String line = reader.readLine();
			if (line != null) {
				builder.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String compressed = builder.toString();
		// Build the dictionary
		String dictionaryBased = compressed.split("`")[0];
		String[] dictionary = dictionaryBased.split("~");
		String compressedData = compressed.substring(dictionaryBased.length());
		// Decompress the rest
		for (int id = 0; id < dictionary.length; id++) {
			compressedData = compressedData.replaceAll("#" + id, dictionary[id]);
		}
		return compressedData;
	}

}
