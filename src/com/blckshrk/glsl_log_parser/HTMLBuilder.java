package com.blckshrk.glsl_log_parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import com.blckshrk.glsl_log_parser.LogParser.SHADER_TYPE;

public class HTMLBuilder {
	
	public static final String HTML_FILE_LOCATION = "index.html";
	
	private ArrayList<String> fragShaderFilesList;
	private ArrayList<String> vertShaderFilesList;
	
	/**
	 * @param vertShaderFilesList TODO
	 * @param shaderFileLocation
	 */
	public HTMLBuilder(ArrayList<String> vertShaderFilesList, ArrayList<String> fragShaderFilesList) {
		this.fragShaderFilesList = fragShaderFilesList;
		this.vertShaderFilesList = vertShaderFilesList;
	}

	public void generateHTMLFile(String logFilePath) throws IOException {
		FileWriter writer = new FileWriter(HTML_FILE_LOCATION);
		
		writer.append(generateHeader("GLSL Log Parser"));
		writer.append(generateBody(logFilePath));
		writer.append(generateFooter());
		
		writer.flush();
		writer.close();
	}
	
	private String generateHeader(String title) {
		return "<!DOCTYPE html>"
				+ "<html lang=\"en\">"
				+ "<head>"
				+ "<meta charset=\"UTF-8\">"
				+ "<title>" + title + "</title>"
//				+ "<link rel=\"stylesheet\" href=\"style.css\">"
				+ "<style>"
				+ "body{font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;font-size:1em;color:#333;}"
				+ "h1{font-size:1.4em;}"
				+ "table{border:none;border-collapse:collapse;}"
				+ ".line_number,.line_counter{background-color:#f6ffb8;padding:0 3px;}"
				+ ".line_number{color:#bbb;text-align:right;}"
				+ ".line_counter{color:#bb0000;}"
				+ ".line_color_light_green{background-color:#93ff93;}"
				+ ".line_content{padding-left:5px;}"
				+ "</style>"
				+ "</head>"
				+ "<body>";
	}
	
	private String generateFooter() {
		return "</body>" +
				"</html>";
	}
	
	private String generateBody(String logFilePath) throws IOException {
		String body = "";
		
		// Vertex Shaders
		for (String shaderFile : vertShaderFilesList) {
			Map<String, Integer> countMap = 
					LogParser.countLineExecution(logFilePath, SHADER_TYPE.VERT_SHADER);
			
			body += "<h1>" + shaderFile + "</h1>";
			body += generateHTMLFromShader(shaderFile, countMap);
		}
		
		// Fragment Shaders
		for (String shaderFile : fragShaderFilesList) {
			Map<String, Integer> countMap = 
					LogParser.countLineExecution(logFilePath, SHADER_TYPE.FRAG_SHADER);
			
			body += "<h1>" + shaderFile + "</h1>";
			body += generateHTMLFromShader(shaderFile, countMap);
		}
		
		return body;
	}

	
	private String generateHTMLFromShader(String shaderFileLocation, 
			Map<String, Integer> countMap) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(shaderFileLocation));
		
		String line, htmlOutput = "<table>";
		int count = 1;
		while ((line = reader.readLine()) != null) {
			Integer instrCount = countMap.get(String.valueOf(count));
			if (instrCount == null) {
				instrCount = new Integer(0);
			}
			
		   htmlOutput += "<tr>" +
		   		"<th class=\"line_counter\">" + instrCount + "</th>" +
		   		"<td class=\"line_number\">" + count + "</td>" +
		   		"<td class=\"line_content" + (instrCount > 0 ? " line_color_light_green" : "") + "\">"
		   			+ line.replaceAll(" ", "&nbsp;").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;") + "</td>" +
		   				"</tr>";
		   count++;
		}
		htmlOutput += "</table>";
		
		reader.close();
		
		return htmlOutput;
	}
}
