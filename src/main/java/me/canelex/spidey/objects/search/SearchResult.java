package me.canelex.spidey.objects.search;

import net.dv8tion.jda.api.utils.data.DataObject;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class SearchResult {

    private String title;
    private String content;
    private String url;

    static SearchResult fromGoogle(final DataObject googleResult) {

        final SearchResult result = new SearchResult();
        result.title = cleanString(googleResult.getString("title"));
        result.content = cleanString(googleResult.getString("snippet"));

        try {

            result.url = URLDecoder.decode(cleanString(googleResult.getString("link")), "UTF-8");

        }

        catch (final UnsupportedEncodingException e) {

            LoggerFactory.getLogger(SearchResult.class).error("Exception!", e);

        }

        return result;

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
                                .replaceAll("<.*?>", "")
                                .replaceAll("\"", "")));

    }

}