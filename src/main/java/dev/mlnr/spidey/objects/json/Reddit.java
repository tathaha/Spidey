package dev.mlnr.spidey.objects.json;

import dev.mlnr.spidey.utils.Utils;

public class Reddit
{
    private int subs;
    private String displayName;
    private String desc;
    private String title;
    private int active;
    private boolean nsfw;
    private String icon;
    private String comIcon;
    private boolean exists = false;
    private final String subName;

    public Reddit(final String name)
    {
        this.subName = name.toLowerCase();
        setData();
    }

    private void setData()
    {
        if (Utils.getJson("https://reddit.com/subreddits/search.json?limit=1&q=" + subName).getObject("data").getInt("dist") == 1)
        {
            final var data = Utils.getJson("https://reddit.com/r/" + subName + "/about.json").getObject("data");
            this.subs = data.getInt("subscribers");
            this.displayName = data.getString("display_name");
            this.desc = Utils.cleanString(data.getString("public_description"));
            this.title = data.getString("title");
            this.active = data.getInt("accounts_active");
            this.nsfw = data.getBoolean("over18");
            this.icon = data.getString("icon_img");
            this.comIcon = data.getString("community_icon");
            this.exists = true;
        }
    }

    public final int getSubs(){ return subs; }
    public final String getName() { return displayName; }
    public final String getDesc() { return desc; }
    public final String getTitle() { return title; }
    public final int getActive() { return active; }
    public final boolean isNsfw() { return nsfw; }
    public final String getIcon() { return icon; }
    public final String getCommunityIcon() { return comIcon; }
    public final boolean exists() { return exists; }
}