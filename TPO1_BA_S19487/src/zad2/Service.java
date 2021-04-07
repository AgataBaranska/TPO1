/**
 *
 *  @author Barańska Agata S19487
 *
 */

package zad2;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Service {


	private final Locale countryLocale;
	private final String countryCurrencyCode;

	private CloseableHttpClient httpClient;


	public Service(String country) {
		
		this.countryLocale = findCountryLocale(country);
		this.countryCurrencyCode = Currency.getInstance(countryLocale).getCurrencyCode();

		//System.out.println(String.format("Country: %s, code: %s, curency: %s", country, countryLocale.getISO3Country(),
		//		countryCurrencyCode));

		httpClient = HttpClients.createDefault();

	}

	private Locale findCountryLocale(String countryName) {
		countryName = countryName.trim().toLowerCase();

		for (String iso : Locale.getISOCountries()) {
			Locale l = new Locale("", iso);
			if (l.getDisplayCountry().toLowerCase().equals(countryName)) {
				return l;
			}
		}

		return null;
	}

	private void throwOnFailedResponseCode(CloseableHttpResponse response) throws Exception {

		if (response.getStatusLine().getStatusCode() / 100 != 2) { // not in range 200-299?
			throw new Exception(
					"Response (code " + response.getStatusLine().getStatusCode() + "): " + response.toString());
		}
	}

	// returns info about Weather for specific town in JSON format
	public String getWeather(String city) {
		String API_KEY = "56d1e70427d1f795eb2f2b027377ad11";
		String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY;

		try {
			HttpGet request = new HttpGet(urlString);
			CloseableHttpResponse response = httpClient.execute(request);
			try {

				throwOnFailedResponseCode(response);

				HttpEntity entity = response.getEntity();
				String result = "";
				if (entity != null) {
					result = EntityUtils.toString(entity);
				}
				return result;

			} catch (Exception ex) {
				System.err.println(ex.toString());
			} finally {
				// response.close();
			}

		} catch (Exception ex) {
			System.err.println(ex.toString());
		} finally {

		}
		return "";
	}

	// return information about exchange rate of country's currency against currency
	// specified by user
	public Double getRateFor(String currencyCode) {

		String urlString = "https://api.exchangerate.host/latest?base=" + countryCurrencyCode + "&symbols="
				+ currencyCode;
		try {
			HttpGet request = new HttpGet(urlString);
			CloseableHttpResponse response = httpClient.execute(request);
			throwOnFailedResponseCode(response);

			HttpEntity entity = response.getEntity();
			String result = "";
			if (entity != null) {
				result = EntityUtils.toString(entity);
			}

			JsonElement root = new JsonParser().parse(result);
			Double exchangeRate = root.getAsJsonObject().get("rates").getAsJsonObject().get(currencyCode).getAsDouble();

			return exchangeRate;

		} catch (Exception ex) {
			System.err.println(ex.toString());
		}

		return null;
	}

	// PLN exchange rate
	public Double getNBPRate() {

		if (countryCurrencyCode.toLowerCase().equals("pln")) {
			return 1d;
		}

		String[] urlString = { "https://www.nbp.pl/kursy/xml/a061z210330.xml",
				"https://www.nbp.pl/kursy/xml/b013z210331.xml" };

		Double exchangeRate = null;

		for (int i = 0; i < 2 && exchangeRate == null; i++) {

			try {
				HttpGet request = new HttpGet(urlString[i]);
				CloseableHttpResponse response = httpClient.execute(request);
				throwOnFailedResponseCode(response);

				HttpEntity entity = response.getEntity();
				String resultXml = "";
				if (entity != null) {
					resultXml = EntityUtils.toString(entity);
				}

				Document doc = Jsoup.parse(resultXml, "", Parser.xmlParser());
				Elements elems = doc.select("kod_waluty:contains(" + countryCurrencyCode + ")");
				if (!elems.isEmpty()) {
					// Potrzebna instancja NumberFormat z polskim locale żeby parsować liczby z
					// przecinkiem a nie kropką (3,4455) itp.
					// Dlatego Double.parseDouble tu nie zadiała
					NumberFormat format = NumberFormat.getInstance(Locale.forLanguageTag("pl-PL"));
					exchangeRate = format.parse(elems.get(0).parent().select("kurs_sredni").get(0).text())
							.doubleValue();
					break;
				} else {
					throw new Exception("Nie znaleziono kursu waluty '" + countryCurrencyCode + "' w NBP");
				}
			} catch (Exception ex) {
				System.err.println(ex.toString());
			}
		}

		System.out.println(exchangeRate);
		return exchangeRate;
	}

	public String getWikiDescription(String city) {

		return "https://en.wikipedia.org/wiki/" + city;

	}
}