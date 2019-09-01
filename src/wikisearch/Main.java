package wikisearch;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

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

	private void playCreation() {
		String selectedCreation = creationsGroup.getSelectedToggle().getUserData().toString();
		if (selectedCreation != "(No Creation Selected)") {
			// Execute a bash function in a worker thread to play the selected creation
			String[] command = new String[]{"/bin/bash", "-c", "./script.sh p " + selectedCreation};
			BashCommand bashCommand = new BashCommand(command);
			team.submit(bashCommand);	
		}
	}

	private void deleteCreation() {
		String selectedCreation = creationsGroup.getSelectedToggle().getUserData().toString();
		if (selectedCreation != "(No Creation Selected)") {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete " + selectedCreation + "?");
			
			// Display the confirmation alert and store the button pressed
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				// Only delete the selected creation if the user has confirmed

				// Execute a bash function in a worker thread to delete the selected creation
				String[] command = new String[]{"/bin/bash", "-c", "./script.sh d " + selectedCreation};
				BashCommand bashCommand = new BashCommand(command);
				team.submit(bashCommand);
				
				bashCommand.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent event) {
						// Update the view of creations in the GUI
						Node creationsPane = createCreationsPane();
						root.setCenter(creationsPane);
					}
				});
			}
		}
	}

	
	/**
	 * Start of a chain of methods that display various input boxes to the user 
	 * to get the information required to create a new creation
	 * createNewCreation() -> getSearchTermAndResult -> getIncludedSentences -> getCreationName
	 * -> createCreation
	 */
	private void createNewCreation() {
		getSearchTermAndResult(true);
	}
	
	private void getSearchTermAndResult(boolean isFirstCall) {

		TextInputDialog searchTermInput = new TextInputDialog("Apple");
		searchTermInput.setTitle("Enter Search Term");
		if (isFirstCall) {
			searchTermInput.setHeaderText("Enter the term you would like to search:");
		} else {
			// If the user has already entered an invalid search term
			searchTermInput.setHeaderText("Term not found, please try again.");
		}

		// Display the input dialog box and store the text the user wrote
		Optional<String> result = searchTermInput.showAndWait();
		if (result.isPresent()) {
			// The user has pressed the ok button
			String searchTerm = result.get();

			// Execute a bash function in a worker thread to do a wiki search on the given term
			WikiSearchTask wikiSearchJob = new WikiSearchTask(searchTerm);
			team.submit(wikiSearchJob);

			wikiSearchJob.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					try {
						
						/**
						 * Returns a list containing the results of the wiki search of the specified term,
						 * with one sentence per line
						 * The first element contains the number of sentences
						 * If the term was not found, returns a list with only one element: "(Term not found)"
						 */
						List<String> searchResult = wikiSearchJob.get();

						if (searchResult.get(0).equals("(Term not found)")) {
							// Reprompt the user until they give a valid search term
							getSearchTermAndResult(false);
						} else {
							getIncludedSentences(searchTerm, searchResult);
						}
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			// The user has not pressed the ok button, so abort creating
			return;
		}
	}
	
	/**
	 * There are no resource-heavy computations so a separate thread is not required for the
	 * main section of this method
	 */
	private void getIncludedSentences(String searchTerm, List<String> searchResult) {
		// The first element in searchResult is the number of sentences in the search
		int totalSentences = Integer.parseInt(searchResult.get(0));
		// Remove the number of sentences from the list, leaving just the search result sentences
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
		/*
		 *  Keep prompting the user for a number of sentences to include until they
		 *  give a number within the possible range
		 */
		do {
			// Display the input dialog box and store the text the user wrote
			Optional<String> result = includedSentencesInput.showAndWait();
			if (result.isPresent()) {
				// The user has pressed the ok button
				String includedSentencesString = result.get();

				if (!includedSentencesString.equals("")) {
					/**
					 * Assume that the user inputs a valid integer (as with Assignment 1)
					 */
					includedSentences = Integer.parseInt(includedSentencesString);
				} else {
					// No input given, so reprompt the user
					includedSentences = -1;
				}
			} else {
				// The user has not pressed the ok button, so abort creating
				return;
			}

			// Only gets seen if the user is asked again to enter a value
			includedSentencesInput.setHeaderText("Number not within range of sentences [1-" + totalSentences + "], please try again.");

		} while (includedSentences < 1 || includedSentences > totalSentences);
		
		
		
		/*
		 * Execute a bash function in a worker thread to list the current creations for the next step/method
		 * Doing this here means getCreationName() does not suffer blocking and the GUI remains responsive
		 */
		String[] command = new String[]{"/bin/bash", "-c", "./script.sh l"};
		BashCommand bashCommand = new BashCommand(command);
		team.submit(bashCommand);
		
		// Create a final copy of includedSentences to be used in the EventHandler
		final int finalIncludedSentences = includedSentences;
				
		bashCommand.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				try {
					
					List<String> listOfCreations = bashCommand.get();
					getCreationName(searchTerm, finalIncludedSentences, listOfCreations);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	/**
	 * There are no resource-heavy computations so a separate thread is not required for this method
	 */
	private void getCreationName(String searchTerm, int includedSentences, List<String> listOfCreations) {
		// Remove the number of creations from the list
		listOfCreations.remove(0);
				
		TextInputDialog creationNameInput = new TextInputDialog("Apple1");
		creationNameInput.setTitle("Enter Creation Name");
		creationNameInput.setHeaderText("What would you like to name your creation?");

		String creationName;
		// Create a regex pattern that the creation name must match - only alphanumeric, underscores, or hyphens
		Pattern validCharacters = Pattern.compile("[a-zA-Z1-9_-]+");
		boolean isValidCreationName;
		/*
		 *  Keep prompting the user for a creation name until they give one that uses valid characters
		 *  and is not already taken
		 */
		do {
			// Display the input dialog box and store the text the user wrote
			Optional<String> result = creationNameInput.showAndWait();
			if (result.isPresent()) {
				// The user has pressed the ok button
				creationName = result.get();
			} else {
				// The user has not pressed the ok button, so abort creating
				return;
			}
			
			// Check if the creation name matches the regex i.e. if it uses valid characters
			Matcher matcher = validCharacters.matcher(creationName);
			isValidCreationName = matcher.matches();

			// Only gets seen if the user is asked again to enter a value
			creationNameInput.setHeaderText("Invalid creation name, please try again.");
			
		} while (!isValidCreationName || listOfCreations.contains(creationName));

		createCreation(searchTerm, includedSentences, creationName);
	}

	private void createCreation(String searchTerm, int includedSentences, String creationName) {
		/*
		 * Execute a bash function in a worker thread to create a new creation with the given search term,
		 * using the specified number of previously saved sentences, and stored with the given creation name
		 */
		String[] command = new String[]{"/bin/bash", "-c", "./script.sh c " + searchTerm + " " + includedSentences + " " + creationName};
		BashCommand bashCommand = new BashCommand(command);
		team.submit(bashCommand);
		
		bashCommand.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				// Update the view of creations in the GUI
				Node creationsPane = createCreationsPane();
				root.setCenter(creationsPane);
			}
		});
	}

	
	
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(root, sceneWidth, sceneHeight);

		FlowPane buttonsPane = new FlowPane();
		root.setTop(buttonsPane);
		buttonsPane.setHgap(buttonsPaneHGap);
		BorderPane.setMargin(buttonsPane, new Insets(buttonsPaneMargin));
		populateButtonsPane(buttonsPane, buttonsWidth);

		/*
		 *  CreationsPane is either a ScrollPane containing a list of all creations,
		 *  or a label saying that there are currently no creations
		 */
		Node creationsPane = createCreationsPane();
		root.setCenter(creationsPane);

		primaryStage.setScene(scene);
		primaryStage.show();
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
				playCreation();
			}
		});
		btnDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				deleteCreation();
			}
		});
	}

	private Node createCreationsPane() {
		// Use an invisible radio button to detect if the user has not selected a creation
		noSelection.setUserData("(No Creation Selected)");
		noSelection.setToggleGroup(creationsGroup);
		noSelection.setSelected(true);

		/*
		 *  Run a bash command to get a list of the current creations 
		 *  (not in a worker thread because there is no GUI yet to be made unresponsive)
		 */
		String[] command = new String[]{"/bin/bash", "-c", "./script.sh l"};
		List<String> listOfCreations = BashCommand.runBashCommand(command);
		
		// The first element in listOfCreations is the number of creations it contains
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

			return creationsWindow;
		}
	}

	private void populateCreationsWindow(ScrollPane creationsWindow, List<String> listOfCreations, int numberOfCreations) {
		FlowPane creationsGrid = new FlowPane();
		creationsWindow.setContent(creationsGrid);
		creationsGrid.setPrefWidth(sceneWidth);

		/* 
		 * Create one radio button for each creation
		 * make them all part of the same toggle group so that only one can be selected at a time
		 */
		for (int i = 0; i < numberOfCreations; i++) {
			String creationName = listOfCreations.get(i);
			RadioButton button = new RadioButton(creationName);
			// Used to get the name of the selected creation when a button is pressed
			button.setUserData(creationName);

			button.setToggleGroup(creationsGroup);
			creationsGrid.getChildren().add(button);

			button.setPrefWidth(sceneWidth/3);
			button.setPadding(new Insets(creationsPadding));
		}
	}
}
