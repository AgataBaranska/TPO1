/**
 *
 *  @author Barańska Agata S19487
 *
 */

package zad2;

import java.io.IOException;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Main extends Application implements EventHandler<ActionEvent> {

	Scene scene;
	Button getCityWeatherButt, getRateForButt, getNBPRateForButt, getWikiDescriptionButt;
	TabPane tabPane;
	BorderPane mainFrame;
	VBox menuBox;

	private Service service;

	public static void main(String[] args) throws IOException {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Service");
		prepareStage();
		primaryStage.setScene(scene);
		primaryStage.show();

		String country = "";
		for (int i = 0; i < 3; i += 1) {
			country = getUserInput("Enter country");
			country = country.trim();
			if (country.isEmpty()) {
				System.err.println("No country name entered");
				continue;
			}

			try {
				service = new Service(country);
				break;
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}

		if (country.isEmpty()) {
			System.err.println("No valid country name entered, exiting");
			System.exit(1);
		}
	}

	private void prepareStage() {
		mainFrame = new BorderPane();
		mainFrame.setPrefWidth(500);
		scene = new Scene(mainFrame);

		menuBox = new VBox();
		menuBox.setSpacing(5);
		Label welcomeLabel = new Label("Welcome, what do you want to do?");

		getCityWeatherButt =createButton("Get weather");
		getRateForButt = createButton("Get exchange rate for currency");
		getNBPRateForButt = createButton("Get NBP rate for country currency");
		getWikiDescriptionButt = createButton("Get Wiki description of city");
	
		menuBox.getChildren().addAll(welcomeLabel, getCityWeatherButt, getRateForButt, getNBPRateForButt,
				getWikiDescriptionButt);
		
		menuBox.setAlignment(Pos.TOP_CENTER);
		menuBox.setPadding(new Insets(10, 10, 10, 10));
		menuBox.setPrefSize(250, 200);

		tabPane = new TabPane();
		tabPane.prefWidthProperty().bind(mainFrame.widthProperty());
		VBox tabBox = new VBox(tabPane);
		mainFrame.setRight(tabBox);
		mainFrame.setLeft(menuBox);
	}

	private Button createButton(String name) {
		Button bt = new Button(name);
		bt.setOnAction(this);
		bt.setPrefWidth(230);
		return bt;
	}
	private String getUserInput(String message) {
		TextInputDialog td = new TextInputDialog();
		td.setHeaderText(message);
		Optional<String> result = td.showAndWait();

		if (!result.isPresent()) {
			return "";
		}
		return td.getEditor().getText();
	}

	@Override
	public void handle(ActionEvent event) {

		if (event.getSource() == getCityWeatherButt) {

			String city = getUserInput("Enter city");

			if (!(city.equals(""))) {
				String weather = service.getWeather(city);
				JsonParser parser = new JsonParser();
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				JsonElement el = parser.parse(weather);
				String result = gson.toJson(el);

				String tabText = "Weather in: " + city;
				addTabWithResult(result, tabText);
			}
		}

		if (event.getSource() == getRateForButt) {

			String currencyCode = getUserInput("Enter currency code");
			Double rate = service.getRateFor(currencyCode);
			String tabText = "Rate for " + currencyCode;
			addTabWithResult(rate.toString(), tabText);
		}

		if (event.getSource() == getNBPRateForButt) {

			Double nbpRateForCountryCurrency = service.getNBPRate();
			String tabText = "Rate for PLN";
			addTabWithResult(nbpRateForCountryCurrency.toString(), tabText);

		}

		if (event.getSource() == getWikiDescriptionButt) {

			TextInputDialog td = new TextInputDialog();
			td.setHeaderText("Enter city");
			td.showAndWait();

			if (!(td.getEditor().getText().equals(""))) {

				String city = td.getEditor().getText();
				String webUrl = service.getWikiDescription(city);
				WebView browser = new WebView();
				WebEngine webEngine = browser.getEngine();
				webEngine.load(webUrl);

				String tabText = "Wiki:" + city;
				addTabWithResult(browser, tabText);
			}
		}
	}

	private void addTabWithResult(Object result, String tabText) {
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		if(result instanceof WebView) {
			WebView browser = (WebView)result;
			scrollPane.setContent(browser);
		}else if(result instanceof String) {
			Text text = new Text(result.toString());
			scrollPane.setContent(text);
		}
		
		Tab resultTab = new Tab(tabText, scrollPane);
		tabPane.getTabs().add(resultTab);
		tabPane.getSelectionModel().select(resultTab);
	}
}