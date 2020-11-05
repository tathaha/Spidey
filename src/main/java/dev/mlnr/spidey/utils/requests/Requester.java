package dev.mlnr.spidey.utils.requests;

import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Requester
{
    private static final Logger LOG = LoggerFactory.getLogger(Requester.class);
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private static final Request.Builder REQUEST_BUILDER = new Request.Builder().header("user-agent", "dev.mlnr.spidey");

    private Requester() {}

    public static DataObject executeRequest(final String url, final API api)
    {
        REQUEST_BUILDER.url(url);
        if (api != null)
            REQUEST_BUILDER.header("Authorization", api.getKey());
        try (final var response = HTTP_CLIENT.newCall(REQUEST_BUILDER.build()).execute(); final var body = response.body())
        {
            return DataObject.fromJson(body.string());
        }
        catch (final IOException ex)
        {
            LOG.error("There was an error while executing a request for url {}:", url, ex);
        }
        return DataObject.empty();
    }

    public static Map<Long, Long> retrieveVideoSegments(final String videoId)
    {
        REQUEST_BUILDER.url("https://sponsor.ajay.app/api/skipSegments?videoID=" + videoId + "&category=music_offtopic");
        try (final var response = HTTP_CLIENT.newCall(REQUEST_BUILDER.build()).execute(); final var body = response.body())
        {
            if (response.code() == 404)
                return null;
            final var json = DataArray.fromJson(body.string());
            final var segments = new LinkedHashMap<Long, Long>();
            for (var i = 0; i < json.length(); i++)
            {
                final var segmentTimes = json.getObject(i).getArray("segment");
                segments.put(segmentTimes.getLong(0) * 1000, segmentTimes.getLong(1) * 1000);
            }
            return segments;
        }
        catch (final IOException ex)
        {
            LOG.error("There was an error while executing a segments request:", ex);
        }
        return null;
    }
}