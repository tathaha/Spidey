package dev.mlnr.spidey.objects.guild;

import dev.mlnr.spidey.DatabaseManager;

public class GuildSettings
{
    private final long guildId;

    private long logChannelId;
    private long joinRoleId;
    private String prefix;
    private long djRoleId;
    private boolean segmentSkippingEnabled;
    private boolean snipingEnabled;
    private boolean vip;

    public GuildSettings(final long guildId, final long logChannelId, final long joinRoleId, final String prefix, final long djRoleId, final boolean segmentSkippingEnabled, final boolean snipingEnabled,
                         final boolean vip)
    {
        this.guildId = guildId;

        this.logChannelId = logChannelId;
        this.joinRoleId = joinRoleId;
        this.prefix = prefix;
        this.djRoleId = djRoleId;
        this.segmentSkippingEnabled = segmentSkippingEnabled;
        this.snipingEnabled = snipingEnabled;
        this.vip = vip;
    }

    // getters

    public long getLogChannelId()
    {
        return this.logChannelId;
    }

    public long getJoinRoleId()
    {
        return this.joinRoleId;
    }

    public String getPrefix()
    {
        return this.prefix;
    }

    public long getDjRoleId()
    {
        return this.djRoleId;
    }

    public boolean isSegmentSkippingEnabled()
    {
        return this.segmentSkippingEnabled;
    }

    public boolean isSnipingEnabled()
    {
        return this.snipingEnabled;
    }

    public boolean isVip()
    {
        return this.vip;
    }

    // setters

    public void setLogChannelId(final long logChannelId)
    {
        this.logChannelId = logChannelId;
        DatabaseManager.setLogChannelId(guildId, logChannelId);
    }

    public void setJoinRoleId(final long joinRoleId)
    {
        this.joinRoleId = joinRoleId;
        DatabaseManager.setJoinRoleId(guildId, joinRoleId);
    }

    public void setPrefix(final String prefix)
    {
        this.prefix = prefix;
        DatabaseManager.setPrefix(guildId, prefix);
    }

    public void setDjRoleId(final long djRoleId)
    {
        this.djRoleId = djRoleId;
        DatabaseManager.setDJRoleId(guildId, djRoleId);
    }

    public void setSegmentSkippingEnabled(final boolean segmentSkippingEnabled)
    {
        this.segmentSkippingEnabled = segmentSkippingEnabled;
        DatabaseManager.setSegmentSkippingEnabled(guildId, segmentSkippingEnabled);
    }

    public void setSnipingEnabled(final boolean snipingEnabled)
    {
        this.snipingEnabled = snipingEnabled;
        DatabaseManager.setSnipingEnabled(guildId, snipingEnabled);
    }

    public void setVip(final boolean vip)
    {
        this.vip = vip;
        DatabaseManager.setVip(guildId, vip);
    }
}