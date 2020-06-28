package dev.mlnr.spidey.utils.requests;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Requester
{
    private static final Logger LOG = LoggerFactory.getLogger(Requester.class);
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    private Requester()
    {
        super();
    }

    public static String executeRequest(final String url, final API api)
    {
        final var requestBuilder = new Request.Builder().url(url).header("user-agent", "dev.mlnr.spidey");
        if (api != null)
            requestBuilder.header("Authorization", api.getKey());
        try (final var body = HTTP_CLIENT.newCall(requestBuilder.build()).execute().body())
        {
            return body != null ? body.string() : "";
        }
        catch (final IOException ex)
        {
            LOG.error("There was an error while executing a request for url {}:", url, ex);
        }
        return "";
    }
}