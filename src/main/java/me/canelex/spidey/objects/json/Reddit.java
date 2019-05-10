package me.canelex.spidey.objects.json;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Reddit {

    private int subs;
    private String name;
    private String desc;
    private String title;
    private int active;
    private boolean nsfw;
    private String icon;
    private String comIcon;

    public final Reddit getSubReddit(final String name) throws IOException {

        return exists(name) ? null : fromJson(getJson(
                "https://reddit.com/r/" + name + "/about.json"));

    }

    private boolean exists(final String name) throws IOException {
        final int i = getJson("https://reddit.com/subreddits/search.json?q=" + name).getJSONObject("data").getInt("dist");
        return i == 0;
    }

    private Reddit fromJson(final JSONObject o) {

        final JSONObject data = o.getJSONObject("data");
        this.subs = data.getInt("subscribers");
        this.name = data.getString("display_name");
        this.desc = data.getString("public_description");
        this.title = data.getString("title");
        this.active = data.getInt("accounts_active");
        this.nsfw = data.getBoolean("over18");
        this.icon = data.getString("icon_img");
        this.comIcon = data.getString("community_icon");

        return this;

    }

    private String getUrl(final String url) throws IOException {
        final URL obj = new URL(url);
        final HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        final String userAgent = "me.canelex.spidey";
        con.setRequestProperty("User-Agent", userAgent);

        final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        final StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();

    }

    private JSONObject getJson(final String url) throws IOException {
        return new JSONObject(getUrl(url));
    }

    public final int getSubs(){
        return subs;
    }
    public final String getName() { return name; }
    public final String getDesc() { return desc; }
    public final String getTitle() { return title; }
    public final int getActive() { return active; }
    public final boolean isNsfw() { return nsfw; }
    public final String getIcon() { return icon; }
    public final String getCommunityIcon() { return comIcon; }
}