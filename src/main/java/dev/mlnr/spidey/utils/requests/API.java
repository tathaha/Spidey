package dev.mlnr.spidey.utils.requests;

public enum API
{
    KSOFT(""),
    DBL("");

    private final String key;

    API(final String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }
}