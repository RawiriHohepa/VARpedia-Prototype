package wikisearch;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class BashCommand extends Task<List<String>> {

	private String[] _command;
	
	public BashCommand(String[] command) {
		_command = command;
	}
	
	@Override
	protected List<String> call() throws Exception {
		List<String> output = new ArrayList<String>();
		try {
			ProcessBuilder builder = new ProcessBuilder(_command);
			Process process = builder.start();
			InputStream stdout = process.getInputStream();
//			InputStream stderr = process.getErrorStream();
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			String line = null;
			while ((line = stdoutBuffered.readLine()) != null ) {
				output.add(line);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return output;
	}

}
