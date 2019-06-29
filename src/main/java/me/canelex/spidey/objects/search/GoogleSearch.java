package me.canelex.spidey.objects.search;

import me.canelex.spidey.Secrets;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GoogleSearch {

    private GoogleSearch(){
        super();
    }

    private static final String GOOGLE_URL = "https://www.googleapis.com/customsearch/v1/?cx=%s&key=%s&num=1&q=%s";
    private static final String GOOGLE_API_KEY = Secrets.GOOGLE_API_KEY;
    private static final Logger logger = LoggerFactory.getLogger(GoogleSearch.class);

    public static SearchResult performSearch(final String engineId, String terms) {

        SearchResult result = null;

        try {
            terms = terms.replace(" ", "%20");
            final String searchUrl = String.format(GOOGLE_URL, engineId, GOOGLE_API_KEY, terms);
            final DataObject o = DataObject.fromJson(Utils.getSiteContent(searchUrl)).getArray("items").getObject(0);
            result = SearchResult.fromGoogle(o);
        }

        catch (final IOException e) {
            logger.error("Exception)", e);
        }

        return result;

    }

}