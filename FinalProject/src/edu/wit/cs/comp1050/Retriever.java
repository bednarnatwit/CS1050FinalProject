package edu.wit.cs.comp1050;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Retriever {
	public static void main(String[] args) {
		try {
			displayWeatherData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void displayWeatherData() {
		try {
			String url = "https://api.open-meteo.com/v1/forecast?latitude=42.3584&longitude=-71.0598&current=temperature_2m,apparent_temperature,precipitation,cloud_cover,wind_speed_10m&hourly=temperature_2m,precipitation_probability,cloud_cover&daily=temperature_2m_max,temperature_2m_min,precipitation_probability_max&timezone=America%2FNew_York";
			HttpURLConnection apiConnection = fetchApiResponse(url);
			

			if (apiConnection.getResponseCode() != 200) {
				System.out.println("Error: Could not connect to API");
				return;
			}
			
			String jsonResponse = readApiResponse(apiConnection);
			System.out.println("API Response: " + jsonResponse);
			
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
			JSONObject currentWeatherJson = (JSONObject) jsonObject.get("current");
			
			if (currentWeatherJson != null) {
				
				String time = (String)currentWeatherJson.get("time");
				double cTemperature = (double)currentWeatherJson.get("temperature_2m");
				long cCloudCover = (long)currentWeatherJson.get("cloud_cover");
				double cPrecipitation = (double)currentWeatherJson.get("precipitation");
				double appTemp = (double)currentWeatherJson.get("apparent_temperature");
				double windSpeed = (double)currentWeatherJson.get("wind_speed_10m");
				System.out.printf("%n%n");
				System.out.println("Current Time: " + time);
				System.out.println("Current Temperature: " + cTemperature);
				System.out.println("Current Cloud Cover: " + cCloudCover);
				System.out.println("Current Precipitation: " + cPrecipitation);
				System.out.println("Apparent Temperature: " + appTemp);
				System.out.println("Wind Speed: " + windSpeed);
			}
			JSONObject hourlyWeatherJson = (JSONObject) jsonObject.get("hourly");
			if (hourlyWeatherJson != null) {
				
				JSONArray hTempArr = (JSONArray) hourlyWeatherJson.get("temperature_2m");
	            JSONArray hPrecipiProbArr = (JSONArray) hourlyWeatherJson.get("precipitation_probability");
	            JSONArray hCloudCoverArr = (JSONArray) hourlyWeatherJson.get("cloud_cover");

	            double[] hTemperature = jsonArrayToDoubleArray(hTempArr);
	            long[] hPreciProb = jsonArrayToLongArray(hPrecipiProbArr);
	            long[] hCloudCover = jsonArrayToLongArray(hCloudCoverArr);
	            
	            System.out.printf("%n%nHourly Temperatures: ");
	            for (int i = 0; i < hTemperature.length; i++) {
	            	System.out.print(hTemperature[i] + "  ");
	            }
	            System.out.printf("%nHourly Precipitation Probabilities: ");
	            for (int i = 0; i < hPreciProb.length; i++) {
	            	System.out.print(hPreciProb[i] + "  ");
	            }
	            System.out.printf("%nHourly Cloud Coverages: ");
	            for (int i = 0; i < hCloudCover.length; i++) {
	            	System.out.print(hCloudCover[i] + "  ");
	            }
			}
			JSONObject dailyWeatherJson = (JSONObject) jsonObject.get("daily");
			if (dailyWeatherJson != null) {
				
				JSONArray dmaTempArr = (JSONArray) dailyWeatherJson.get("temperature_2m_max");
	            JSONArray dmiTempArr = (JSONArray) dailyWeatherJson.get("temperature_2m_min");
	            JSONArray dPreciProbArr = (JSONArray) dailyWeatherJson.get("precipitation_probability_max");

	            double[] dmaTemperature = jsonArrayToDoubleArray(dmaTempArr);
	            double[] dmiTemperature = jsonArrayToDoubleArray(dmiTempArr);
	            long[] dPreciProb = jsonArrayToLongArray(dPreciProbArr);
	            
	            System.out.printf("%n%nDaily Maximum Temperatures: ");
	            for (int i = 0; i < dmaTemperature.length; i++) {
	            	System.out.print(dmaTemperature[i] + "  ");
	            }
	            System.out.printf("%nDaily Maximum Temperatures: ");
	            for (int i = 0; i < dmiTemperature.length; i++) {
	            	System.out.print(dmiTemperature[i] + "  ");
	            }
	            System.out.printf("%nDaily Precipitation Probabilities: ");
	            for (int i = 0; i < dPreciProb.length; i++) {
	            	System.out.print(dPreciProb[i] + "  ");
	            }
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static double[] jsonArrayToDoubleArray(JSONArray jsonArray) {
	    double[] resultArray = new double[jsonArray.size()];
	    for (int i = 0; i < jsonArray.size(); i++) {
	        resultArray[i] = (double)jsonArray.get(i);
	    }
	    return resultArray;
	}

	private static long[] jsonArrayToLongArray(JSONArray jsonArray) {
	    long[] resultArray = new long[jsonArray.size()];
	    for (int i = 0; i < jsonArray.size(); i++) {
	        resultArray[i] = (long)jsonArray.get(i);
	    }
	    return resultArray;
	}
	
	private static String readApiResponse(HttpURLConnection apiConnection) {
		try (Scanner input = new Scanner(apiConnection.getInputStream())){
			StringBuilder resultJson = new StringBuilder();
			while (input.hasNext()) {
				resultJson.append(input.nextLine());
			}
			input.close();
			return resultJson.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} finally {
			apiConnection.disconnect();
		}
	}


	private static HttpURLConnection fetchApiResponse(String urlString) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			return conn;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
}
