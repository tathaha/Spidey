package me.canelex.Spidey.objects.search;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;

public class SearchResult {
	
    private String title;
    private String content;
    private String url;

    static SearchResult fromGoogle(final JSONObject googleResult) {

    	final SearchResult result = new SearchResult();
        result.title = cleanString(googleResult.getString("title"));
        result.content = cleanString(googleResult.getString("snippet"));

        try {

            result.url = URLDecoder.decode(cleanString(googleResult.getString("link")), "UTF-8");

        }

        catch (final UnsupportedEncodingException e) {

            e.printStackTrace();

        }

        return result;

    }

    public final String getTitle() {
    	
        return title;
        
    }

    public final String getContent() {
    	
        return content;
        
    }

    public final String getUrl() {
    	
        return url;
        
    }

    public final String getSuggestedReturn() {
    	
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