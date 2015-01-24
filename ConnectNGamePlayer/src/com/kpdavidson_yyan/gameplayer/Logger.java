/**
 * Kyle Davidson
 * Yan Yan
 */
package com.kpdavidson_yyan.gameplayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author Kyle & Yan
 *
 */
public class Logger {
	
	/**
	 * Logger to let the program print out useful information in a separate file
	 */
	
	static FileWriter fw = null;

	/**
	 * Initialize logger
	 * @throws IOException
	 */
	static void init() throws IOException{
		fw = new FileWriter(new File("testlog2.txt"));
	}
	
	/**
	 * Write information to the file. If the logger is not initialized, 
	 * do nothing.
	 * @param str the str to be written to the file
	 */
	static void log(String str) {
		if(fw != null){
			try {
				fw.write(str);
				fw.write("\r\n");
				fw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
