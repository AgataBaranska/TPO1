/**
 *
 *  @author Barańska Agata S19487
 *
 */

package zad2;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Service {

	private String country = null;

	private HttpClient httpClient;

	public Service(String country) {
		this.country = country;

		// Initialisation of HTTPClient
		httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
				.followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(30)).build();

	}

	private void throwOnFailedResponseCode(HttpResponse response) throws Exception {
		// Prawidłowe kody odpowiedzi to te z zakresu 200-299.
		// Dzielenie całkowite zastosowane żeby to sprawdzić.
		if (response.statusCode() / 100 != 2) { // not in range 200-299?
			throw new Exception("Response (code " + response.statusCode() + "): " + response.toString());
		}
	}

//returns info about Weather for specific town in JSON format
	public String getWeather(String city) {
		String API_KEY = "56d1e70427d1f795eb2f2b027377ad11";
		String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY;

		try {
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
			HttpResponse<String> response = httpClient.send(request,
					HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
			throwOnFailedResponseCode(response);
			return response.body();

		} catch (Exception ex) {
			System.err.println(ex.toString());
		}

		return "";
	}

	// return information about exchange rate of country's currency against currency
	// specified by user
	public Double getRateFor(String currencyCode) {

		// get all exchange rates for currency specified by currencyCode
		/*
		 * Przykład zlecenia dla uzyskania kursu bata wobec złotego:
		 * https://api.exchangerate.host/latest?base=PLN&symbols=THB
		 */
		String countryCurrencyCode = getAvailableCurrencies().get(country);
		String urlString = "https://api.exchangerate.host/latest?base=" + countryCurrencyCode + "&symbols="
				+ currencyCode;
		try {
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
			HttpResponse<String> response = httpClient.send(request,
					HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
			throwOnFailedResponseCode(response);

			JsonElement root = new JsonParser().parse(response.body());
			Double exchangeRate = root.getAsJsonObject().get("rates").getAsJsonObject().get(currencyCode).getAsDouble();

			return exchangeRate;

		} catch (Exception ex) {
			System.err.println(ex.toString());
		}

		return null;
	}

	// PLN exchange rate
	public Double getNBPRate() {

		String[] urlString = { "https://www.nbp.pl/kursy/xml/a061z210330.xml",
				"https://www.nbp.pl/kursy/xml/b013z210331.xml" };

		Double exchangeRate = null;

		for (int i = 0; i < 2 && exchangeRate == null; i++) {
		
			try {
				HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString[i])).build();
				HttpResponse<String> response = httpClient.send(request,
						HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
				throwOnFailedResponseCode(response);

				String countryCurrencyCode = getAvailableCurrencies().get(country);

				Document document = convertStringToXMLDocument(response.body());
				document.getDocumentElement().normalize();
				NodeList pozycjaNodeList = document.getElementsByTagName("pozycja");

				for (int j = 0; j < pozycjaNodeList.getLength(); j++) {

					Node nNode = pozycjaNodeList.item(j);
					NodeList pozycjaChildNodes = nNode.getChildNodes();
					for (int h = 1; h < pozycjaChildNodes.getLength(); h++) {
						Node hNode = pozycjaChildNodes.item(h);

						System.out.println();
						if (hNode.getTextContent().equals(countryCurrencyCode)) {
							Node hhNode = pozycjaChildNodes.item(h + 1);
							exchangeRate = Double.parseDouble(hhNode.getTextContent());
						}
					}

				}

				System.out.println(exchangeRate);
				

			} catch (Exception ex) {
				System.err.println(ex.toString());
			}
		}

		return exchangeRate;
	}

	private static Document convertStringToXMLDocument(String xmlString) {
		// Parser that produces DOM object trees from XML content
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// API to obtain DOM Document instance
		DocumentBuilder builder = null;
		try {
			// Create DocumentBuilder with default configuration
			builder = factory.newDocumentBuilder();

			// Parse the content to Document object
			Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, String> getAvailableCurrencies() {
		Locale[] locales = Locale.getAvailableLocales();
		Map<String, String> availableCurrencies = new TreeMap<>();
		for (Locale locale : locales) {
			try {
				availableCurrencies.put(locale.getDisplayCountry(), Currency.getInstance(locale).getCurrencyCode());

			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		return availableCurrencies;
	}

}
