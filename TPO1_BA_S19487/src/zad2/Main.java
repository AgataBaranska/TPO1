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
	Double rate1 = s.getRateFor("USD");
	System.out.println(rate1);
	Double rate2 = s.getNBPRate();
	System.out.println(rate2);
		// ...
		// część uruchamiająca GUI

		
		
		
	}
	
	
	
}
