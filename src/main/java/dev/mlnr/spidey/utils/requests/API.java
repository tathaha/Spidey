package dev.mlnr.spidey.utils.requests;

public enum API
{
    KSOFT("Bearer " + System.getenv("ksoft")),
    DBL("");

    private final String key;

    API(final String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return this.key;
    }
}