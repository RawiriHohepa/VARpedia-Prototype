package wikisearch;

import java.util.ArrayList;
import java.util.List;

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
			// 
			/**
			 * Runs a bash function that performs a wiki search using wikit, 
			 * stores the results (if any) in a temporary text folder, and passes the result back here
			 * Returns a list containing the results of the wiki search of the specified term,
			 * with one sentence per line
			 * The first element contains the number of sentences
			 * If the term was not found, returns a list with only one element: "(Term not found)"
			 */
			searchResult = BashCommand.runBashCommand(new String[]{"/bin/bash", "-c", "./script.sh s " + _searchTerm});
		} else {
			// No search term entered, therefore no term found
			searchResult.add("(Term not found)");
		}

		return searchResult;
	}
}
