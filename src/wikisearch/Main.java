package wikisearch;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;

public class Main {
	public static void main(String[] args) {
		try {
//			ProcessBuilder builder = new ProcessBuilder("ls", "-l", "/usr");
//			Process process = builder.start();
//			InputStream stdout = process.getInputStream();
//			InputStream stderr = process.getErrorStream();
//			BufferedReader stdoutBuffered =
//					new BufferedReader(new InputStreamReader(stdout));
//			String line = null;
//			while ((line = stdoutBuffered.readLine()) != null ) {
//				System.out.println(line);
//			}
//
//			ProcessBuilder builder = new ProcessBuilder("wc");
//			Process process = builder.start();
//			OutputStream in = process.getOutputStream();
//			PrintWriter stdin = new PrintWriter(in);
//			stdin.println("This is one line");
//			stdin.println("And another");
//			stdin.close();
//			
//			InputStream stdout = process.getInputStream();
//			InputStream stderr = process.getErrorStream();
//			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
//			String line = null;
//			while ((line = stdoutBuffered.readLine()) != null ) {
//				System.out.println(line);
//			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
