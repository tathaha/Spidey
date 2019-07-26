package me.canelex.spidey.objects.search;

import net.dv8tion.jda.api.utils.data.DataObject;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class SearchResult {

    private String title;
    private String content;
    private String url;
    private static final Logger logger = LoggerFactory.getLogger(SearchResult.class);

    static SearchResult fromGoogle(final DataObject googleResult) {

        final var result = new SearchResult();
        result.title = cleanString(googleResult.getString("title"));
        result.content = cleanString(googleResult.getString("snippet"));

        result.url = URLDecoder.decode(cleanString(googleResult.getString("link")), StandardCharsets.UTF_8);

        return result;

    }

    public final String getContent() {
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