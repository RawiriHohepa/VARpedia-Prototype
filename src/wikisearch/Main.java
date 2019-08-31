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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.BufferedReader;

public class Main extends Application {
	Button btnCreate = new Button("Create New Creation");
	Button btnPlay = new Button("Play Selected Creation");
	Button btnDelete = new Button("Delete Selected Creation");

	@Override
	public void start(Stage primaryStage) throws Exception {
		final double sceneWidth = 1200;
		final double sceneHeight = 800;
		final double buttonsPaneHGap = 50;
		final double buttonsPaneMargin = 50;
		final double buttonsWidth = (sceneWidth - 2*buttonsPaneMargin - 2*buttonsPaneHGap) / 3 - 1;

		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, sceneWidth, sceneHeight);

		FlowPane buttonsPane = new FlowPane();
		root.setTop(buttonsPane);
		buttonsPane.setHgap(buttonsPaneHGap);
		BorderPane.setMargin(buttonsPane, new Insets(buttonsPaneMargin));

		populateButtonsPane(buttonsPane, buttonsWidth);


		//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		List<String> output = buildProcess(new String[]{"/bin/bash", "-c", "./script.sh p"});
		System.out.println(output);
		
		launch(args);
	}

	private static List<String> buildProcess(String[] command) {
		List<String> output = new ArrayList<String>();
		try {
			ProcessBuilder builder = new ProcessBuilder();
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
		btnCreate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Create");
			}
		});

		buttonsPane.getChildren().add(btnPlay);
		btnPlay.setPrefWidth(buttonsWidth);
		btnPlay.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Play");
			}
		});

		buttonsPane.getChildren().add(btnDelete);
		btnDelete.setPrefWidth(buttonsWidth);
		btnDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Delete");
			}
		});
	}
}
