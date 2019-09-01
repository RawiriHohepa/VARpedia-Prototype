package wikisearch;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class WikiSearchTask extends Task<List<String>> {

	private String _searchTerm;
	
	public WikiSearchTask(String searchTerm) {
		_searchTerm = searchTerm;
	}
	
	@Override
	protected List<String> call() throws Exception {
		List<String> searchResult = new ArrayList<String>();

		if (!_searchTerm.equals("")) {
			searchResult = runBashCommand(new String[]{"/bin/bash", "-c", "./script.sh s " + _searchTerm});
		} else {
			// No search term entered, therefore no term found
			searchResult.add("(Term not found)");
		}

		return searchResult;
	}
	
	protected List<String> runBashCommand(String[] command) {
		List<String> output = new ArrayList<String>();
		try {
			ProcessBuilder builder = new ProcessBuilder(command);
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
