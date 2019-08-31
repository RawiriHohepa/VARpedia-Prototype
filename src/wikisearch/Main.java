package wikisearch;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.BufferedReader;

public class Main extends Application {
	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		try {
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "./script.sh p");
			Process process = builder.start();
			InputStream stdout = process.getInputStream();
			InputStream stderr = process.getErrorStream();
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			String line = null;
			while ((line = stdoutBuffered.readLine()) != null ) {
				System.out.println(line);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	
}
