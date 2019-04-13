package me.canelex.Spidey.objects.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import me.canelex.Spidey.Secrets;

public class GoogleSearch {
	
    private static final String GOOGLE_URL = "https://www.googleapis.com/customsearch/v1/?cx=%s&key=%s&num=1&q=%s";
    private static final String GOOGLE_API_KEY = Secrets.googleapikey;

    public static List<SearchResult> performSearch(final String engineId, String terms) {
    	
        try {

        	terms = terms.replace(" ", "%20");
        	final String searchUrl = String.format(GOOGLE_URL, engineId, GOOGLE_API_KEY, terms);

        	final URL searchURL = new URL(searchUrl);
        	final URLConnection conn = searchURL.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:39.0) Gecko/20100101 " + randomName());
            final BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuilder json = new StringBuilder();
            String line;
            
            while ((line = in.readLine()) != null) {
            	
                json.append(line).append("\n");                
                
            }
            
            in.close();

            final JSONArray jsonResults = new JSONObject(json.toString()).getJSONArray("items");
            final List<SearchResult> results = new LinkedList<>();
            
            for (int i = 0; i < jsonResults.length(); i++) {
            	            	
                results.add(SearchResult.fromGoogle(jsonResults.getJSONObject(i)));
                
            }
            
            return results;
            
        }
        
        catch (final IOException e) {
        	
            e.printStackTrace();
            return null;
            
        }
        
    }	
    
    private static String randomName() {
    	
    	final char[] characters = new char[]
                {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
                'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
                '1','2','3','4','5','6','7','8','9','0'};

    	final Random rand = new Random();
    	final StringBuilder builder = new StringBuilder();
        builder.append("Spidey/");
        
        for (int i = 0; i < 10; i++) {
        	
            builder.append(characters[rand.nextInt(characters.length)]);
            
        }
        
        return builder.toString();
        
    }    

}