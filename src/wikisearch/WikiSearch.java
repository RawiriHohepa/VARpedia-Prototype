package wikisearch;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class WikiSearch extends Task<List<String>> {

	private String[] _command;
	
	public WikiSearch(String[] command) {
		_command = command;
	}
	
	@Override
	protected List<String> call() throws Exception {
		List<String> searchResult = new ArrayList<String>();
//		if (result.isPresent()) {
//			String searchTerm = result.get();
//
//			if (!searchTerm.equals("")) {
//				searchResult = runBashCommand(new String[]{"/bin/bash", "-c", "./script.sh s " + searchTerm});
//				
//				for (String line : searchResult) {
//					System.out.println(line);
//				}
//			} else {
//				searchResult.add("Term not found");
//			}
//		} else {
//			// Quitting
//			searchResult.add("(Quitting)");
//		}
		
		return searchResult;
	}

}
