package com.blckshrk.glsl_log_parser;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Insufficient number of arguments");
			System.exit(-1);
		}
		
		ArrayList<String> vertShadersList = new ArrayList<>();
		vertShadersList.add(args[1]);
		
		ArrayList<String> fragShadersList = new ArrayList<>();
		fragShadersList.add(args[2]);
		
		HTMLBuilder builder = new HTMLBuilder(vertShadersList, fragShadersList);
		
		try {
			System.out.println("Generation of " + HTMLBuilder.HTML_FILE_LOCATION + "...");
			builder.generateHTMLFile(args[0]);
			System.out.println("\nFile generated successfully !");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
