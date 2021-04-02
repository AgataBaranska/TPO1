/**
 *
 *  @author Barańska Agata S19487
 *
 */


package zad2;

import java.io.IOException;

public class Main  {
	public static void main(String[] args) throws IOException {
	Service s = new Service("Afganistan");
	String weatherJson = s.getWeather("Warsaw");
	System.out.println(weatherJson);
	Double rate1 = s.getRateFor("USD");
	Double rate2 = s.getNBPRate();
		// ...
		// część uruchamiająca GUI

		
		
		
	}
	
	
	
}
