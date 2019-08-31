package wikisearch;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, sceneWidth, sceneHeight);

		FlowPane buttonsPane = new FlowPane();
		root.setTop(buttonsPane);
		buttonsPane.setHgap(buttonsPaneHGap);
		BorderPane.setMargin(buttonsPane, new Insets(buttonsPaneMargin));
		populateButtonsPane(buttonsPane, buttonsWidth);
		
		// The first element in listOfCreations is the number of creations it contains
		List<String> listOfCreations = runBashCommand(new String[]{"/bin/bash", "-c", "./script.sh l"});
		for (String line : listOfCreations) {
			System.out.println(line);
		}
		int numberOfCreations = Integer.parseInt(listOfCreations.get(0));
		
		
		RadioButton noSelection = new RadioButton("No Creation Selected");
	    noSelection.setToggleGroup(creationsGroup);
	    noSelection.setSelected(true);
		if (numberOfCreations == 0) {
			Label noCreations = new Label("There are currently no creations.");
			root.setCenter(noCreations);
			noCreations.setScaleX(3);
			noCreations.setScaleY(3);
		} else {
			ScrollPane creationsWindow = new ScrollPane();
			root.setCenter(creationsWindow);
//			creationsWindow.setPrefWidth(sceneWidth);
			
			FlowPane creationsGrid = new FlowPane();
			creationsWindow.setContent(creationsGrid);
			creationsGrid.setPrefWidth(sceneWidth);
			
//			List<RadioButton> creations = new ArrayList<RadioButton>();
			for (int i = 1; i <= numberOfCreations; i++) {
				RadioButton button = new RadioButton(listOfCreations.get(i));
//				creations.add(button);
				button.setToggleGroup(creationsGroup);
				creationsGrid.getChildren().add(button);
				button.setPrefWidth(sceneWidth/3);
				button.setPadding(new Insets(creationsPadding));
//				button.setStyle("");
//				button.getStyleClass().add("class_name");
			}
		}
		
		//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	private static List<String> runBashCommand(String[] command) {
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
		btnCreate.setPrefWidth(buttonsWidth);
		btnCreate.setPadding(new Insets(buttonsPadding));
		btnCreate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Create");
			}
		});

		buttonsPane.getChildren().add(btnPlay);
		btnPlay.setPrefWidth(buttonsWidth);
		btnPlay.setPadding(new Insets(buttonsPadding));
		btnPlay.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Play");
				System.out.println(creationsGroup.getSelectedToggle().toString());
			}
		});

		buttonsPane.getChildren().add(btnDelete);
		btnDelete.setPrefWidth(buttonsWidth);
		btnDelete.setPadding(new Insets(buttonsPadding));
		btnDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Delete");
				System.out.println(creationsGroup.getSelectedToggle().toString());
			}
		});
	}
}
