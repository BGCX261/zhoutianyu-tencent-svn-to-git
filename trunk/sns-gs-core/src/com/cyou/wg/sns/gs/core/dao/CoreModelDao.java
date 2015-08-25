package com.cyou.wg.sns.gs.core.dao;

import java.io.File;
import java.util.List;


import com.cyou.wg.sns.gs.core.domain.ClassPath;
import com.cyou.wg.sns.gs.core.domain.UserHash;

public class CoreModelDao extends CoreDao4Xml{
	
	public List<UserHash> getUserHash() throws Exception {
		File file = ClassPath.location.createRelative("/hash/hash.xml").getFile();
		check.addFile2Check(file, 1, UserHash.class);
		return this.coverXml2List0(file, UserHash.class);
	}
}
