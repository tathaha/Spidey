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

    public Reddit getSubReddit(String name) throws IOException {

        return exists(name) ? null : fromJson(getJson(
                "https://reddit.com/r/" + name + "/about.json"));

    }

    private boolean exists(String name) throws IOException {
        final int i = getJson("https://reddit.com/subreddits/search.json?q=" + name).getJSONObject("data").getInt("dist");
        return i == 0;
    }

    private Reddit fromJson(JSONObject o) {

        JSONObject data = o.getJSONObject("data");
        this.subs = data.getInt("subscribers");
        this.name = data.getString("display_name");
        this.desc = data.getString("public_description");
        this.title = data.getString("title");
        this.active = data.getInt("accounts_active");
        this.nsfw = data.getBoolean("over18");
        this.icon = data.getString("icon_img");

        return this;

    }

    private String getUrl(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        String userAgent = "me.canelex.spidey";
        con.setRequestProperty("User-Agent", userAgent);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();

    }

    private JSONObject getJson(String url) throws IOException {
        return new JSONObject(getUrl(url));
    }

    public int getSubs(){
        return subs;
    }
    public String getName() { return name; }
    public String getDesc() { return desc; }
    public String getTitle() { return title; }
    public int getActive() { return active; }
    public boolean isNsfw() { return nsfw; }
    public String getIcon() { return icon; }
}