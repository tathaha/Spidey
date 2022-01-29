package dev.mlnr.spidey.objects.commands.slash.category;

public interface ICategory {
	String getName();
	String getFriendlyName();
	CategoryFlag getFlag();

	enum CategoryFlag {
		BASE,
		SETTINGS,
	}
}