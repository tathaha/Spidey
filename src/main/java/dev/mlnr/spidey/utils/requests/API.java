package dev.mlnr.spidey.utils.requests;

public enum API
{
    KSOFT("Bearer " + System.getenv("ksoft"));

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