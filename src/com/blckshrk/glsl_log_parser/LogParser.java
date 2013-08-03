package com.blckshrk.glsl_log_parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LogParser {

	public enum SHADER_TYPE {
		VERT_SHADER,
		FRAG_SHADER
	}
	
	private String logFileLocation;
	
	/**
	 * @param logFileLocation
	 */
	public LogParser(String logFileLocation) {
		this.logFileLocation = logFileLocation;
	}

	static public Map<String, Integer> countLineExecution(String fileName, SHADER_TYPE type) throws IOException {		
		switch (type) {
		case VERT_SHADER:
			System.out.println("Parsing Vertex Shader file " + fileName + "...");
			return countLineExecutionVertShader(fileName);
			
		case FRAG_SHADER:
			System.out.println("Parsing Fragment Shader file " + fileName + "...");
			return countLineExecutionFragShader(fileName);

		default:
			break;
		}
		
		return Collections.EMPTY_MAP;
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private static Map<String, Integer> countLineExecutionVertShader(String fileName) throws IOException {
		Map<String, Integer> lineNumExecMap = new HashMap();

		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		
		String line;
		while ((line = reader.readLine()) != null) {
			
			// search fragment marker
			if (!line.matches("^START draw_llvm_shader.*")) {
				continue;
			}
			
			line = reader.readLine();
			if (!line.matches("^PROLOGUE$")) {
				reader.close();
				throw new Error("Improperly formatted file : \"PROLOGUE\" tag is missing.");
			}
			
			// count instructions
			while (!(line = reader.readLine()).matches("^EPILOGUE$")) {
				
				//HACK: workaround for malformed files
				if (!line.matches("^[A-Z0-9]+ \\d \\d+ \\d+-\\d+ \\d+-\\d+$")) {
					reader.close();
					throw new Error("Improperly formatted file : incorrect syntax\n" +
							line + " should be : ^[A-Z0-9]+ \\d \\d+ \\d+-\\d+ \\d+-\\d+$");
				}
				
				String[] lineSplit = line.split(" ");
				String lineKey = lineSplit[3].split("-")[0];
				if (lineKey.equals("0")) {
					continue; // skipe empty values
				}
				
				// line is in map ?
				Integer countLineValue = lineNumExecMap.get(lineKey);
				if (countLineValue == null) {
					lineNumExecMap.put(lineKey, 1);
				} else {
					lineNumExecMap.put(lineKey, countLineValue + 1);	
				}
			}
		}
		
		reader.close();
		
		return lineNumExecMap;
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private static Map<String, Integer> countLineExecutionFragShader(String fileName) throws IOException {
		Map<String, Integer> lineNumExecMap = new HashMap();

		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		
		String line;
		while ((line = reader.readLine()) != null) {
			
			// search fragment marker
			if (!line.matches("^START fs\\d+_variant0_partial.*") &&
					!line.matches("^START fs\\d+_variant0_whole.*")) {
				continue;
			}
			
			line = reader.readLine();
			while (!line.matches("^END fs\\d+_variant0_partial") &&
					!line.matches("^END fs\\d+_variant0_whole")) {
				
				
				if (!line.matches("^PROLOGUE$")) {
					reader.close();
					throw new Error("Improperly formatted file : \"PROLOGUE\" tag is missing.");
				}
				
				// count instructions
				while (!(line = reader.readLine()).matches("^EPILOGUE$")) {
					
					if (!line.matches("^[A-Z0-9]+ \\d \\d+ \\d+-\\d+ \\d+-\\d+$")) {
						reader.close();
						throw new Error("Improperly formatted file : incorrect syntax\n" +
								line + " should be : ^[A-Z0-9]+ \\d \\d+ \\d+-\\d+ \\d+-\\d+$");
					}
					
					String[] lineSplit = line.split(" ");
					String lineKey = lineSplit[3].split("-")[0];
					if (lineKey.equals("0")) {
						continue; // skipe empty values
					}
					
					// line is in map ?
					Integer countLineValue = lineNumExecMap.get(lineKey);
					if (countLineValue == null) {
						lineNumExecMap.put(lineKey, 1);
					} else {
						lineNumExecMap.put(lineKey, countLineValue + 1);	
					}
				}
				
				line = reader.readLine();
			}
		}
		
		reader.close();
		
		return lineNumExecMap;
	}
}
