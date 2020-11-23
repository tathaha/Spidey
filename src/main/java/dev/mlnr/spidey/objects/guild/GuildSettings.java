package dev.mlnr.spidey.objects.guild;

import dev.mlnr.spidey.DatabaseManager;

public class GuildSettings
{
    private final long guildId;

    private long logChannelId;
    private long joinRoleId;
    private String prefix;

    private boolean snipingEnabled;
    private boolean vip;

    private long djRoleId;
    private boolean segmentSkippingEnabled;
    private int defaultVolume;

    private boolean fairQueueEnabled;
    private int fairQueueThreshold;

    public GuildSettings(final long guildId, final long logChannelId, final long joinRoleId, final String prefix, final boolean snipingEnabled, final boolean vip, final long djRoleId,
                         final boolean segmentSkippingEnabled, final int defaultVolume, final boolean fairQueueEnabled, final int fairQueueThreshold)
    {
        this.guildId = guildId;

        this.logChannelId = logChannelId;
        this.joinRoleId = joinRoleId;
        this.prefix = prefix;

        this.snipingEnabled = snipingEnabled;
        this.vip = vip;

        this.djRoleId = djRoleId;
        this.segmentSkippingEnabled = segmentSkippingEnabled;
        this.defaultVolume = defaultVolume;

        this.fairQueueEnabled = fairQueueEnabled;
        this.fairQueueThreshold = fairQueueThreshold;
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

    public boolean isSnipingEnabled()
    {
        return this.snipingEnabled;
    }

    public boolean isVip()
    {
        return this.vip;
    }

    // music getters

    public long getDjRoleId()
    {
        return this.djRoleId;
    }

    public boolean isSegmentSkippingEnabled()
    {
        return this.segmentSkippingEnabled;
    }

    public int getDefaultVolume()
    {
        return this.defaultVolume;
    }

    // fair queue getters

    public boolean isFairQueueEnabled()
    {
        return this.fairQueueEnabled;
    }

    public int getFairQueueThreshold()
    {
        return this.fairQueueThreshold;
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

    public void setSnipingEnabled(final boolean enabled)
    {
        this.snipingEnabled = enabled;
        DatabaseManager.setSnipingEnabled(guildId, enabled);
    }

    public void setVip(final boolean vip)
    {
        this.vip = vip;
        DatabaseManager.setVip(guildId, vip);
    }

    // music setters

    public void setDjRoleId(final long djRoleId)
    {
        this.djRoleId = djRoleId;
        DatabaseManager.setDJRoleId(guildId, djRoleId);
    }

    public void setSegmentSkippingEnabled(final boolean enabled)
    {
        this.segmentSkippingEnabled = enabled;
        DatabaseManager.setGuildSegmentSkippingEnabled(guildId, enabled);
    }

    public void setDefaultVolume(final int defaultVolume)
    {
        this.defaultVolume = defaultVolume;
        DatabaseManager.setDefaultVolume(guildId, defaultVolume);
    }

    // fair queue setters

    public void setFairQueueEnabled(final boolean enabled)
    {
        this.fairQueueEnabled = enabled;
        DatabaseManager.setFairQueueEnabled(guildId, enabled);
    }

    public void setFairQueueThreshold(final int threshold)
    {
        this.fairQueueThreshold = threshold;
        DatabaseManager.setFairQueueThreshold(guildId, threshold);
    }
}