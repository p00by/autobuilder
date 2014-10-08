package org.ruben.autobuilder.util;

public class StringUtil {

	private StringUtil() {
		
	}
	
	public static String upperCaseFirst(String s) {
		if (s.length() <= 1) {
			return s.toUpperCase();
		} else {
			return s.substring(0, 1).toUpperCase() + s.substring(1);
		}
	}
	
	public static String lowerCaseFirst(String s) {
		if (s.length() <= 1) {
			return s.toLowerCase();
		} else {
			return s.substring(0, 1).toLowerCase() + s.substring(1);
		}
	}

}
