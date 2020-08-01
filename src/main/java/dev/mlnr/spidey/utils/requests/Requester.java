package dev.mlnr.spidey.utils.requests;

import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Requester
{
    private static final Logger LOG = LoggerFactory.getLogger(Requester.class);
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private static final Request.Builder REQUEST_BUILDER = new Request.Builder().header("user-agent", "dev.mlnr.spidey");

    private Requester()
    {
        super();
    }

    public static DataObject executeRequest(final String url, final API api)
    {
        REQUEST_BUILDER.url(url);
        if (api != null)
            REQUEST_BUILDER.header("Authorization", api.getKey());
        try (final var body = HTTP_CLIENT.newCall(REQUEST_BUILDER.build()).execute().body())
        {
            return DataObject.fromJson(body.string());
        }
        catch (final IOException ex)
        {
            LOG.error("There was an error while executing a request for url {}:", url, ex);
        }
        return DataObject.empty();
    }
}