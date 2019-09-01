package wikisearch;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.BufferedReader;

public class Main extends Application {
	private ExecutorService team = Executors.newFixedThreadPool(3);
//	private ExecutorService team = Executors.newSingleThreadExecutor();
	
	final double sceneWidth = 1200;
	final double sceneHeight = 800;
	
	final double buttonsPaneHGap = 50;
	final double buttonsPaneMargin = 25;
	final double buttonsPadding = 25;
	final double buttonsWidth = (sceneWidth - 2*buttonsPaneMargin - 2*buttonsPaneHGap) / 3;
	
	final double creationsPadding = 25;
	
	BorderPane root = new BorderPane();
	
	Button btnCreate = new Button("Create New Creation");
	Button btnPlay = new Button("Play Selected Creation");
	Button btnDelete = new Button("Delete Selected Creation");
	
	ToggleGroup creationsGroup = new ToggleGroup();
	RadioButton noSelection = new RadioButton("(No Creation Selected)");
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(root, sceneWidth, sceneHeight);

		FlowPane buttonsPane = new FlowPane();
		root.setTop(buttonsPane);
		buttonsPane.setHgap(buttonsPaneHGap);
		BorderPane.setMargin(buttonsPane, new Insets(buttonsPaneMargin));
		populateButtonsPane(buttonsPane, buttonsWidth);
		
		Node creationsPane = createCreationsPane();
		root.setCenter(creationsPane);
		
		//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	private List<String> runBashCommand(String[] command) {
		BashCommand bashCommand = new BashCommand(command);
		team.submit(bashCommand);
		try {
			return  bashCommand.get();
		} catch (Exception e) {
			return null;
		}
	}
	
	private void populateButtonsPane(FlowPane buttonsPane, double buttonsWidth) {
		buttonsPane.getChildren().add(btnCreate);
		buttonsPane.getChildren().add(btnPlay);
		buttonsPane.getChildren().add(btnDelete);
		
		btnCreate.setPrefWidth(buttonsWidth);
		btnPlay.setPrefWidth(buttonsWidth);
		btnDelete.setPrefWidth(buttonsWidth);
		
		btnCreate.setPadding(new Insets(buttonsPadding));
		btnPlay.setPadding(new Insets(buttonsPadding));
		btnDelete.setPadding(new Insets(buttonsPadding));
		
		btnCreate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {				
				createNewCreation();
			}
		});
		
		btnPlay.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String selectedCreation = creationsGroup.getSelectedToggle().getUserData().toString();
				if (selectedCreation != "(No Creation Selected)") {
					runBashCommand(new String[]{"/bin/bash", "-c", "./script.sh p " + selectedCreation});
				}
			}
		});
		
		btnDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String selectedCreation = creationsGroup.getSelectedToggle().getUserData().toString();
				if (selectedCreation != "(No Creation Selected)") {
					Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete " + selectedCreation + "?");
					Optional<ButtonType> result = alert.showAndWait();
					if (result.isPresent() && result.get() == ButtonType.OK) {
						runBashCommand(new String[]{"/bin/bash", "-c", "./script.sh d " + selectedCreation});
					}
				}
				
				// Update creations window
				Node creationsPane = createCreationsPane();
				root.setCenter(creationsPane);
			}
		});
	}
	private void createNewCreation() {
		// Get Search Term
		
		TextInputDialog searchTermInput = new TextInputDialog("Apple");
		searchTermInput.setTitle("Enter Search Term");
		searchTermInput.setHeaderText("Enter the term you would like to search:");
		
		List<String> searchResult;
		Optional<String> result;
		
		do {
			result = searchTermInput.showAndWait();
			if (result.isPresent()) {
				String searchTerm = result.get();
				
				searchResult = wikiSearch(searchTerm);
			} else {
				// Quitting
				return;
			}
			
			// Only gets displayed if the loop repeats
			searchTermInput.setHeaderText("Term not found, please try again.");
			
		} while (searchResult.get(0).equals("(Term not found)"));

		
		
		// Get number of sentences to include
		
		int totalSentences = Integer.parseInt(searchResult.get(0));
		String searchTerm = searchResult.get(1);
		// Remove the number of sentences and search term
		searchResult.remove(1);
		searchResult.remove(0);
		
		TextInputDialog includedSentencesInput = new TextInputDialog("2");
		includedSentencesInput.setTitle("Choose Sentences");
		includedSentencesInput.setHeaderText("How many sentences would you like to include? [1-" + totalSentences + "]:");
		
		// Reformat the search result into a single string to display to the user
		String content = "";
		for (int i = 0; i < totalSentences; i++) {
			content += searchResult.get(i) + "\n";
		}
		includedSentencesInput.setContentText(content);	
		
		
		int includedSentences;
		Optional<String> resultIncludedSentences;
		
		do {
			resultIncludedSentences = includedSentencesInput.showAndWait();
			if (resultIncludedSentences.isPresent()) {
				String includedSentencesString = resultIncludedSentences.get();
	
				if (!includedSentencesString.equals("")) {
					includedSentences = Integer.parseInt(includedSentencesString);
				} else {
					includedSentences = -1;
				}
			} else {
				// Quitting
				return;
			}
			
			includedSentencesInput.setHeaderText("Number not within range of sentences [1-" + totalSentences + "], please try again.");
			
		} while (includedSentences < 1 || includedSentences > totalSentences);
		
		
		
		// Get Name of Creation
		
		TextInputDialog creationNameInput = new TextInputDialog("Apple1");
		creationNameInput.setTitle("Enter Creation Name");
		creationNameInput.setHeaderText("What would you like to name your creation?");
		
		List<String> listOfCreations = runBashCommand(new String[]{"/bin/bash", "-c", "./script.sh l"});
		if (listOfCreations.size() != 1) {
			listOfCreations.remove(0);
		}
		
		
		String creationName;
		Pattern p = Pattern.compile("[a-zA-Z1-9_-]+");
		Matcher m;
		boolean b;
		
		do {
			Optional<String> resultCreationName = creationNameInput.showAndWait();
			if (resultCreationName.isPresent()) {
				creationName = resultCreationName.get();
			} else {
				return;
			}
			
			m = p.matcher(creationName);
			b = m.matches();
			
			creationNameInput.setHeaderText("Invalid creation name, please try again.");
		} while (!b || listOfCreations.contains(creationName));
		
		
		
		runBashCommand(new String[]{"/bin/bash", "-c", "./script.sh c " + searchTerm + " " + includedSentences + " " + creationName});
		// Update creations window
		Node creationsPane = createCreationsPane();
		root.setCenter(creationsPane);
	}
	private Node createCreationsPane() {
		noSelection.setUserData("(No Creation Selected)");
		noSelection.setToggleGroup(creationsGroup);
	    noSelection.setSelected(true);
		
		// The first element in listOfCreations is the number of creations it contains
		List<String> listOfCreations = runBashCommand(new String[]{"/bin/bash", "-c", "./script.sh l"});
		
		int numberOfCreations = Integer.parseInt(listOfCreations.get(0));
		listOfCreations.remove(0);
		
		if (numberOfCreations == 0) {
			Label noCreations = new Label("There are currently no creations.");
			noCreations.setScaleX(3);
			noCreations.setScaleY(3);
			
			return noCreations;
		} else {
			ScrollPane creationsWindow = new ScrollPane();
			populateCreationsWindow(creationsWindow, listOfCreations, numberOfCreations);
//			creationsWindow.setPrefWidth(sceneWidth);
			
			return creationsWindow;
		}
	}
	
	private void populateCreationsWindow(ScrollPane creationsWindow, List<String> listOfCreations, int numberOfCreations) {
		FlowPane creationsGrid = new FlowPane();
		creationsWindow.setContent(creationsGrid);
		creationsGrid.setPrefWidth(sceneWidth);
		
		for (int i = 0; i < numberOfCreations; i++) {
			String creationName = listOfCreations.get(i);
			RadioButton button = new RadioButton(creationName);
			button.setUserData(creationName);
			
			button.setToggleGroup(creationsGroup);
			creationsGrid.getChildren().add(button);
			
			button.setPrefWidth(sceneWidth/3);
			button.setPadding(new Insets(creationsPadding));
//			button.setStyle("");
//			button.getStyleClass().add("class_name");
		}
	}

	/**
	 * Returns a list containing the results of the wiki search of the specified search term,
	 * with one sentence per line
	 * The first element contains the number of sentences and the second element 
	 * contains the search term that was used
	 * If the term was not found, returns a list with only one element: "(Term not found)"
	 */
	private List<String> wikiSearch(String searchTerm) {
		List<String> searchResult = new ArrayList<String>();
		
		if (!searchTerm.equals("")) {
			searchResult = runBashCommand(new String[]{"/bin/bash", "-c", "./script.sh s " + searchTerm});
		} else {
			// No search term entered, therefore no term found
			searchResult.add("(Term not found)");
		}
		
		return searchResult;
	}
}
