package me.canelex.spidey.objects.json;

import me.canelex.spidey.Secrets;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SocialBlade {

    private int subs;
    private long views;
    private int videos;
    private boolean verified;
    private String avatar;
    private String country;
    private static final Logger logger = LoggerFactory.getLogger(SocialBlade.class);

    public SocialBlade getYouTube(String id) throws IOException {
        String password = Secrets.SPASS;
        String email = Secrets.EMAIL;
        JSONObject l = getJson(
                "https://api.socialblade.com/v2/bridge?email=" + email + "&password=" + getMD5(password));

        String token = l.getJSONObject("id").getString("token");
        return fromJson(getJson("https://api.socialblade.com/v2/youtube/statistics?query=statistics&username="
                + id + "&email=" + email + "&token=" + token));

    }

    private SocialBlade fromJson(JSONObject o) {

        JSONObject data = o.getJSONObject("data");
        this.videos = data.getInt("uploads");
        this.subs = data.getInt("subs");
        this.views = data.getLong("views");
        this.verified = 1 == data.getInt("isVerified");
        this.avatar = data.getString("avatar").replace("//a//", "/a/").replace("s88", "s300");
        this.country = data.getString("country");

        return this;

    }

    private String getMD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] encode = s.getBytes();
            byte[] encoded = md.digest(encode);

            StringBuilder sb = new StringBuilder(2 * encoded.length);
            for (byte b : encoded) {
                sb.append("0123456789ABCDEF".charAt((b & 0xF0) >> 4));
                sb.append("0123456789ABCDEF".charAt((b & 0x0F)));
            }
            return sb.toString().toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Exception!", e);
            return null;
        }
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
    public long getViews(){
        return views;
    }
    public int getVideos(){
        return videos;
    }
    public boolean isVerified(){
        return verified;
    }
    public String getAvatar(){
        return avatar;
    }
    public String getCountry(){
        return country;
    }

}