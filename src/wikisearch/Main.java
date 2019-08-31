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
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
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
	
	Button btnCreate = new Button("Create New Creation");
	Button btnPlay = new Button("Play Selected Creation");
	Button btnDelete = new Button("Delete Selected Creation");
	
	ToggleGroup creationsGroup = new ToggleGroup();
	RadioButton noSelection = new RadioButton("No Creation Selected");
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, sceneWidth, sceneHeight);

		FlowPane buttonsPane = new FlowPane();
		root.setTop(buttonsPane);
		buttonsPane.setHgap(buttonsPaneHGap);
		BorderPane.setMargin(buttonsPane, new Insets(buttonsPaneMargin));
		populateButtonsPane(buttonsPane, buttonsWidth);
		
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
			
			root.setCenter(noCreations);
		} else {
			ScrollPane creationsWindow = new ScrollPane();
			populateCreationsWindow(creationsWindow, listOfCreations, numberOfCreations);
//			creationsWindow.setPrefWidth(sceneWidth);
			
			root.setCenter(creationsWindow);
		}
		
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
				System.out.println("Create");
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
				String selectedCreation = creationsGroup.getSelectedToggle().getUserData().toString();
				if (selectedCreation != "No Creation Selected") {
					Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete " + selectedCreation + "?");
					Optional<ButtonType> result = alert.showAndWait();
					if (result.isPresent() && result.get() == ButtonType.OK) {
						System.out.println(runBashCommand(new String[]{"/bin/bash", "-c", "./script.sh d " + selectedCreation}));
					}
				}
				
				System.out.println("Delete");
				System.out.println(selectedCreation);
			}
		});
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
}
