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
    private boolean partner;
    private boolean verified;
    private String avatar;
    private String country;
    private String banner;
    private static final Logger logger = LoggerFactory.getLogger(SocialBlade.class);

    public final SocialBlade getYouTube(final String id) throws IOException {
        final String email = Secrets.EMAIL;
        final JSONObject l = getJson(
                "https://api.socialblade.com/v2/bridge?email=" + email + "&password=" + getMD5());

        final String token = l.getJSONObject("id").getString("token");
        return fromJson(getJson("https://api.socialblade.com/v2/youtube/statistics?query=statistics&username="
                + id + "&email=" + email + "&token=" + token));

    }

    private SocialBlade fromJson(final JSONObject o) {

        final JSONObject data = o.getJSONObject("data");
        this.videos = data.getInt("uploads");
        this.subs = data.getInt("subs");
        this.views = data.getLong("views");
        this.partner = 1 == data.getInt("partner");
        this.verified = 1 == data.getInt("isVerified");
        this.avatar = data.getString("avatar").replace("//a//", "/a/").replace("s88", "s300");
        this.country = data.getString("country");
        this.banner = data.getString("banner");

        return this;

    }

    private String getMD5() {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte[] encode = Secrets.SPASS.getBytes();
            final byte[] encoded = md.digest(encode);

            final StringBuilder sb = new StringBuilder(2 * encoded.length);
            for (final byte b : encoded) {
                sb.append("0123456789ABCDEF".charAt((b & 0xF0) >> 4));
                sb.append("0123456789ABCDEF".charAt((b & 0x0F)));
            }
            return sb.toString().toLowerCase();
        } catch (final NoSuchAlgorithmException e) {
            logger.error("Exception!", e);
            return null;
        }
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

    public final int getSubs(){ return subs; }
    public final long getViews() { return views; }
    public final int getVideos() { return videos; }
    public final boolean isPartner(){ return partner; }
    public final boolean isVerified() { return verified; }
    public final String getAvatar() { return avatar; }
    public final String getCountry(){ return country; }
    public final String getBanner() { return banner; }

}