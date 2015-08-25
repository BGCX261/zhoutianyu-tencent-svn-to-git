package com.cyou.wg.sns.gs.core.domain;

import java.io.IOException;

import org.springframework.core.io.Resource;

public class ClassPath {
	public static Resource location;

	public Resource getLocation() {
		return location;
	}

	public void setLocation(Resource location) throws IOException {
		this.location = location.createRelative("WEB-INF/classes/");
	}
	
	
}
