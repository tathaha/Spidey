package me.canelex.spidey.objects.json;

import me.canelex.spidey.Secrets;
import me.canelex.spidey.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SocialBlade
{
    private int subs;
    private long views;
    private int videos;
    private boolean partner;
    private boolean verified;
    private String avatar;
    private String country;
    private String banner;
    private final String id;
    private static final Logger LOG = LoggerFactory.getLogger(SocialBlade.class);

    public SocialBlade(final String id)
    {
        this.id = id;
        setData();
    }

    private void setData()
    {
        final var email = Secrets.EMAIL;
        final var auth = Utils.getJson("https://api.socialblade.com/v2/bridge?email=" + email + "&password=" + getMD5());
        final var json = Utils.getJson("https://api.socialblade.com/v2/youtube/statistics?query=statistics&username="
                + id + "&email=" + email + "&token=" + auth.getObject("id").getString("token"));
        if (json.getObject("status").getInt("response") == 200)
        {
            final var data = json.getObject("data");
            this.videos = data.getInt("uploads");
            this.subs = data.getInt("subs");
            this.views = data.getLong("views");
            this.partner = 1 == data.getInt("partner");
            this.verified = 1 == data.getInt("isVerified");
            this.avatar = data.getString("avatar").replace("//a//", "/a/").replace("s88", "s300");
            this.country = data.getString("country");
            this.banner = data.getString("banner");
        }
    }

    private String getMD5()
    {
        try
        {
            final var md = MessageDigest.getInstance("MD5");
            final var encode = Secrets.SPASS.getBytes();
            final var encoded = md.digest(encode);

            final var sb = new StringBuilder(2 * encoded.length);
            for (final var b : encoded)
            {
                sb.append("0123456789ABCDEF".charAt((b & 0xF0) >> 4));
                sb.append("0123456789ABCDEF".charAt((b & 0x0F)));
            }
            return sb.toString().toLowerCase();
        }
        catch (final NoSuchAlgorithmException e)
        {
            LOG.error("Exception!", e);
            return null;
        }
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