package me.canelex.Spidey.objects.search;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

@SuppressWarnings("deprecation")
public class SearchResult {
	
    private String title;
    private String content;
    private String url;

    public static SearchResult fromGoogle(JSONObject googleResult) {
    	
        SearchResult result = new SearchResult();
        result.title = cleanString(googleResult.getString("title"));
        result.content = cleanString(googleResult.getString("snippet"));
        
        try {
        	
            result.url = URLDecoder.decode(cleanString(googleResult.getString("link")), "UTF-8");
            
        }
        
        catch (UnsupportedEncodingException e) {
        	
            e.printStackTrace();
            
        }
        
        return result;
        
    }

    public String getTitle() {
    	
        return title;
        
    }

    public String getContent() {
    	
        return content;
        
    }

    public String getUrl() {
    	
        return url;
        
    }

    public String getSuggestedReturn() {
    	
    	if (url.startsWith("https://www.youtube.com/watch?")) {
    		
            return url.replace("https://www.youtube.com/watch?v=", "https://youtu.be/") + " - *" + title + "*: \"" + content + "\"";    		
    		
    	}
    	
    	else {
    		
            return url.replace("www.", "") + " - *" + title + "*: \"" + content + "\"";    		
    		
    	}    	
        
    }

	private static String cleanString(String uncleanString) {
		
        return StringEscapeUtils.unescapeJava(
                StringEscapeUtils.unescapeHtml4(
                        uncleanString
                                .replaceAll("\\s+", " ")
                                .replaceAll("\\<.*?>", "")
                                .replaceAll("\"", "")));
        
    }	

}