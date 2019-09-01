package wikisearch;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
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
	RadioButton noSelection = new RadioButton("No Creation Selected");
	
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
				TextInputDialog searchTermInput = new TextInputDialog("Apple");
				searchTermInput.setTitle("Enter Search Term");
				searchTermInput.setHeaderText("Enter the term you would like to search:");
				
				List<String> searchResult = wikiSearch(searchTermInput);			
				
				while (searchResult.get(0).equals("Term not found")) {
					searchTermInput.setHeaderText("Term not found, please try again.");
					searchResult = wikiSearch(searchTermInput);
				}
				if (searchResult.get(0).equals("(Quitting)")) {
					return;
				}

				int totalSentences = Integer.parseInt(searchResult.get(0));
				
				
				
				TextInputDialog includedSentencesInput = new TextInputDialog("2");
				includedSentencesInput.setTitle("Choose Sentences");
				includedSentencesInput.setHeaderText("How many sentences would you like to include? [1-" + totalSentences + "]:");
				
				String content = "";
				for (int i = 1; i <= totalSentences; i++) {
					content += searchResult.get(i) + "\n";
				}
				includedSentencesInput.setContentText(content);	
				
				int includedSentences = getIncludedSentences(includedSentencesInput);
				
				while (includedSentences < 1 || includedSentences > totalSentences) {
					includedSentencesInput.setHeaderText("Number not within range of sentences [1-" + totalSentences + "], please try again.");
					
					includedSentences = getIncludedSentences(includedSentencesInput);
				}
			}
		});
		
		btnPlay.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String selectedCreation = creationsGroup.getSelectedToggle().getUserData().toString();
				if (selectedCreation != "No Creation Selected") {
					runBashCommand(new String[]{"/bin/bash", "-c", "./script.sh p " + selectedCreation});
				}
			}
		});
		
		btnDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Toggle selectedButton = creationsGroup.getSelectedToggle();
				String selectedCreation = selectedButton.getUserData().toString();
				if (selectedCreation != "No Creation Selected") {
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
	
	private Node createCreationsPane() {
		noSelection.setUserData("No Creation Selected");
		noSelection.setToggleGroup(creationsGroup);
	    noSelection.setSelected(true);
		
		// The first element in listOfCreations is the number of creations it contains
		List<String> listOfCreations = runBashCommand(new String[]{"/bin/bash", "-c", "./script.sh l"});
//		for (String line : listOfCreations) {
//			System.out.println(line);
//		}
		
		int numberOfCreations = Integer.parseInt(listOfCreations.get(0));
		
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
		
//		List<RadioButton> creations = new ArrayList<RadioButton>();
		for (int i = 1; i <= numberOfCreations; i++) {
			String creationName = listOfCreations.get(i);
			RadioButton button = new RadioButton(creationName);
			button.setUserData(creationName);
			
//			creations.add(button);
			button.setToggleGroup(creationsGroup);
			creationsGrid.getChildren().add(button);
			
			button.setPrefWidth(sceneWidth/3);
			button.setPadding(new Insets(creationsPadding));
//			button.setStyle("");
//			button.getStyleClass().add("class_name");
		}
	}

	private List<String> wikiSearch(TextInputDialog searchTermInput) {
		List<String> searchResult = new ArrayList<String>();
		Optional<String> result = searchTermInput.showAndWait();
		if (result.isPresent()) {
			String searchTerm = result.get();

			if (!searchTerm.equals("")) {
				searchResult = runBashCommand(new String[]{"/bin/bash", "-c", "./script.sh s " + searchTerm});
				
				for (String line : searchResult) {
					System.out.println(line);
				}
			} else {
				searchResult.add("Term not found");
			}
		} else {
			searchResult.add("(Quitting)");
		}
		
		return searchResult;
	}
	
	private int getIncludedSentences (TextInputDialog includedSentencesInput) {
		int includedSentences = -1;
		Optional<String> result = includedSentencesInput.showAndWait();
		if (result.isPresent()) {
			String searchTerm = result.get();

			if (!searchTerm.equals("")) {
				includedSentences = Integer.parseInt(searchTerm);
				System.out.println("" + includedSentences);
			}
		}
		
		return includedSentences;
	}
}
