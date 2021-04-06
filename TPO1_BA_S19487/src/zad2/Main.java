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

		getCityWeatherButt = new Button("Get weather");
		getCityWeatherButt.setOnAction(this);
		getCityWeatherButt.setPrefWidth(230);

		getRateForButt = new Button("Get exchange rate for currency");
		getRateForButt.setPrefWidth(230);
		getRateForButt.setOnAction(this);

		getNBPRateForButt = new Button("Get NBP rate for country currency");
		getNBPRateForButt.setPrefWidth(230);
		getNBPRateForButt.setOnAction(this);

		getWikiDescriptionButt = new Button("Get Wiki description of city");
		getWikiDescriptionButt.setPrefWidth(230);
		getWikiDescriptionButt.setOnAction(this);

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

				// Scroll bar doesn't work
				ScrollPane scrollPane = new ScrollPane();
				scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
				scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
				Text text = new Text(result);
				scrollPane.setContent(text);
				Tab resultTab = new Tab("Weather in: " + city, scrollPane);
				tabPane.getTabs().add(resultTab);
			}
		}

		if (event.getSource() == getRateForButt) {

			String currencyCode = getUserInput("Enter currency code");
			Double rate = service.getRateFor(currencyCode);
			ScrollPane scrollPane = new ScrollPane();
			scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
			scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
			Text text = new Text(rate.toString());
			scrollPane.setContent(text);
			Tab resultTab = new Tab("Rate for " + currencyCode, scrollPane);
			tabPane.getTabs().add(resultTab);

		}

		if (event.getSource() == getNBPRateForButt) {

			Double nbpRateForCountryCurrency = service.getNBPRate();
			ScrollPane scrollPane = new ScrollPane();
			scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
			scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
			Text text = new Text(nbpRateForCountryCurrency.toString());
			scrollPane.setContent(text);
			Tab resultTab = new Tab("Rate for PLN", scrollPane);
			tabPane.getTabs().add(resultTab);
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
				ScrollPane scrollPane = new ScrollPane();
				scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
				scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
				scrollPane.setContent(browser);
				Tab browserTab = new Tab("Wiki:" + city, scrollPane);
				tabPane.getTabs().add(browserTab);

			}

		}

	}

}