package dev.mlnr.spidey.objects;

import net.dv8tion.jda.api.utils.data.DataObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public class I18n {
	private static final Map<String, I18n> LANGUAGE_MAP = new HashMap<>();

	private static final Logger logger = LoggerFactory.getLogger(I18n.class);

	public static void loadLanguages() {
		try (var langs = I18n.class.getResourceAsStream("/assets/languages/langs.txt")) {
			for (var langCode : IOUtils.toString(langs, StandardCharsets.UTF_8).split("\n")) {
				try (var langJsonStream = I18n.class.getResourceAsStream("/assets/languages/" + langCode + ".json")) {
					LANGUAGE_MAP.put(langCode, new I18n(DataObject.fromJson(langJsonStream), langCode));
				}
			}
		}
		catch (IOException ex) {
			logger.error("There was an error while loading languages, exiting", ex);
			System.exit(1);
		}
	}

	private final DataObject data;
	private final String langCode;
	private final Map<String, String> stringCache;

	private I18n(DataObject data, String langCode) {
		this.data = data;
		this.langCode = langCode;
		this.stringCache = new HashMap<>();
	}

	public static I18n ofLanguage(String language) {
		return LANGUAGE_MAP.get(language);
	}

	public String getLangCode() {
		return langCode;
	}

	public String get(String key, Object... args) {
		if (!key.contains(".")) {
			return applyArguments(computeOrGet(key, data::getString), args);
		}
		var string = computeOrGet(key, k -> {
			var object = data;
			var parts = key.split("\\.");
			for (var i = 0; i < (parts.length - 1); i++)
				object = object.getObject(parts[i]);
			return object.getString(parts[parts.length - 1]);
		});
		return applyArguments(string, args);
	}

	private String applyArguments(String string, Object... args) {
		return args.length == 0 ? string : String.format(string, args);
	}

	private String computeOrGet(String key, UnaryOperator<String> accumulator) {
		return stringCache.computeIfAbsent(key, accumulator);
	}
}