/**
 * Kyle Davidson
 * Yan Yan
 */
package com.kpdavidson_yyan.gameplayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
	
	static FileWriter fw = null;;

	
	static void init() throws IOException{
		fw = new FileWriter(new File("testlog1.txt"));
	}
	
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
