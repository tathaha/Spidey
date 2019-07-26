package me.canelex.spidey.objects.search;

import me.canelex.spidey.Secrets;
import me.canelex.spidey.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GoogleSearch {

    private GoogleSearch() { super(); }

    private static final String GOOGLE_URL = "https://www.googleapis.com/customsearch/v1/?cx=%s&key=%s&num=1&q=%s";
    private static final String GOOGLE_API_KEY = Secrets.GOOGLE_API_KEY;
    private static final Logger logger = LoggerFactory.getLogger(GoogleSearch.class);

    public static SearchResult performSearch(String terms) {

        SearchResult result = null;

        try {
            terms = terms.replace(" ", "%20");
            final var searchUrl = String.format(GOOGLE_URL, "015021391643023377625:kq7ex3xgvoq", GOOGLE_API_KEY, terms);
            final var o = Utils.getJson(searchUrl).getArray("items").getObject(0);
            result = SearchResult.fromGoogle(o);
        }

        catch (final IOException e) {
            logger.error("Exception)", e);
        }

        return result;

    }

}