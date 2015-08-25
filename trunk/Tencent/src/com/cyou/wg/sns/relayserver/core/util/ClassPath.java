package com.cyou.wg.sns.relayserver.core.util;

public class ClassPath
{

	private static ClassPath classPath;

	private static ClassPath getInstance()
	{
		if (classPath == null)
			classPath = new ClassPath();
		return classPath;
	}

	public static String createRelative(String path)
	{
		return (new StringBuilder()).append("/").append(getInstance().getClass().getResource("/").getPath().substring(1).replace("%20", " ")).append(path).toString();
	}
}
