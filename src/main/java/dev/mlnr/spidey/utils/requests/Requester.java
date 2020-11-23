package dev.mlnr.spidey.utils.requests;

import dev.mlnr.spidey.objects.music.VideoSegment;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Float.parseFloat;

public class Requester
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Requester.class);
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
            LOGGER.error("There was an error while executing a request for url {}:", url, ex);
        }
        return DataObject.empty();
    }

    public static List<VideoSegment> retrieveVideoSegments(final String videoId)
    {
        REQUEST_BUILDER.url("https://sponsor.ajay.app/api/skipSegments?videoID=" + videoId + "&category=music_offtopic");
        try (final var response = HTTP_CLIENT.newCall(REQUEST_BUILDER.build()).execute(); final var body = response.body())
        {
            if (response.code() == 404)
                return Collections.emptyList();
            final var json = DataArray.fromJson(body.string());
            final var segments = new ArrayList<VideoSegment>();
            for (var i = 0; i < json.length(); i++)
            {
                final var segmentTimes = json.getObject(i).getArray("segment");
                final var segmentStart = (long) (parseFloat(segmentTimes.getString(0)) * 1000);
                final var segmentEnd = (long) (parseFloat(segmentTimes.getString(1)) * 1000);
                segments.add(new VideoSegment(segmentStart, segmentEnd));
            }
            return segments;
        }
        catch (final IOException ex)
        {
            LOGGER.error("There was an error while executing a segments request:", ex);
        }
        return Collections.emptyList();
    }
}