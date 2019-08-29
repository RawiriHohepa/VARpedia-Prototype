package wikisearch;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;

public class Main {
	public static void main(String[] args) {
		try {
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "./script.sh");
			Process process = builder.start();
			InputStream stdout = process.getInputStream();
			InputStream stderr = process.getErrorStream();
			BufferedReader stdoutBuffered =
					new BufferedReader(new InputStreamReader(stdout));
			String line = null;
			while ((line = stdoutBuffered.readLine()) != null ) {
				System.out.println(line);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
