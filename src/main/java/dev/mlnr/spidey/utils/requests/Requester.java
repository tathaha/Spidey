package dev.mlnr.spidey.utils.requests;

import dev.mlnr.spidey.objects.music.VideoSegment;
import dev.mlnr.spidey.utils.requests.api.API;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static DataObject executeApiRequest(final API api, final String... args)
    {
        var url = api.getUrl();
        if (url.contains("%s")) // i hate this
        {
            for (final String arg : args)
                url = url.replace("%s", arg);
        }
        REQUEST_BUILDER.url(url);
        final var apiKey = api.getKey();
        if (apiKey != null)
            REQUEST_BUILDER.header("Authorization", apiKey);
        try (final var response = HTTP_CLIENT.newCall(REQUEST_BUILDER.build()).execute(); final var body = response.body())
        {
            return DataObject.fromJson(body.string());
        }
        catch (final Exception ex)
        {
            LOGGER.error("There was an error while executing a request for url {}:", url, ex);
        }
        return DataObject.empty();
    }

    public static void updateStats(final JDA jda)
    {
        final var guildCount = jda.getGuildCache().size();
        final var botId = jda.getSelfUser().getIdLong();
        for (final API.Stats statsApi : API.Stats.values())
        {
            final var requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), DataObject.empty().put(statsApi.getStatsParam(), guildCount).toString());
            final var request = new Request.Builder()
                    .url(String.format(statsApi.getUrl(), botId))
                    .header("Authorization", statsApi.getKey())
                    .post(requestBody).build();
            try (final var ignored = HTTP_CLIENT.newCall(request).execute())
            {
                LOGGER.info("Successfully updated stats for {} API: {} guilds", statsApi, guildCount);
            }
            catch (final Exception ex)
            {
                LOGGER.error("There was an error while updating stats for API {}!", statsApi, ex);
            }
        }
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
        catch (final Exception ex)
        {
            LOGGER.error("There was an error while executing a segments request:", ex);
        }
        return Collections.emptyList();
    }
}