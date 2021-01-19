package dev.mlnr.spidey.objects;

import net.dv8tion.jda.api.utils.data.DataObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class I18n
{
    private static final Map<String, I18n> LANGUAGE_MAP;

    private static final Logger LOGGER = LoggerFactory.getLogger(I18n.class);

    private final DataObject data;

    private I18n(DataObject data)
    {
        this.data = data;
    }

    static
    {
        var tmp = new HashMap<String, I18n>();

        try (var langs = I18n.class.getResourceAsStream("/assets/languages/langs.txt"))
        {
            for (var langCode : IOUtils.toString(langs, StandardCharsets.UTF_8).split("\n"))
            {
                try (var langJsonStream = I18n.class.getResourceAsStream("/assets/languages/" + langCode + ".json"))
                {
                    tmp.put(langCode, new I18n(DataObject.fromJson(langJsonStream)));
                }
            }
        }
        catch (IOException ex)
        {
            LOGGER.error("There was an error while loading languages, exiting", ex);
            System.exit(1);
        }
        LANGUAGE_MAP = tmp;
    }

    public static I18n ofLanguage(String language)
    {
        return LANGUAGE_MAP.get(language);
    }

    public String get(String key, Object... args)
    {
        if (!key.contains("."))
            return data.getString(key);
        var object = data;
        var parts = key.split("\\.");
        for (var i = 0; i < (parts.length - 1); i++)
            object = object.getObject(parts[i]);

        var string = object.getString(parts[parts.length - 1]);
        if (args.length != 0)
            string = String.format(string, args);
        return string;
    }
}