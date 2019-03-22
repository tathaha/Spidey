package me.canelex.Spidey.utils;

public class PermissionError {
	
	private final static String missingPerms = ":no_entry: Action can't be completed due to missing permission **";
	
	public static final String getErrorMessage(String permName) {
		
		return missingPerms + permName + "**.";
		
	}

}