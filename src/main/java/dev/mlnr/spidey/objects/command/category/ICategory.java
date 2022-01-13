package dev.mlnr.spidey.objects.command.category;

public interface ICategory {
	String getName();
	String getFriendlyName();
	CategoryFlag getFlag();

	enum CategoryFlag {
		BASE,
		SETTINGS,
	}
}