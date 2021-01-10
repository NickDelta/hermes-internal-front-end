package org.hua.hermes.frontend.util;

import java.util.Arrays;

public class TemplateUtil
{
	public static String generateLocation(String basePage, String... paths) {

		if(basePage == null) throw new IllegalArgumentException("basePage cannot be null");

		StringBuilder location = new StringBuilder(basePage);
		var iterator = Arrays.stream(paths).iterator();

		while (iterator.hasNext()){
			var path = iterator.next();
			if (path == null) continue;
			location.append('/');
			location.append(path);
		}

		return location.toString();
	}
}
