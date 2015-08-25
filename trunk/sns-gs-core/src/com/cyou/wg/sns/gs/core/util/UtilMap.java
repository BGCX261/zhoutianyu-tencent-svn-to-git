package com.cyou.wg.sns.gs.core.util;

import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.beans.BeanMap;

public class UtilMap {
	public static Map toMap(Object... obj) {
		Map map = new HashMap();
		for(int i = 0; i < obj.length;) {
			map.put(obj[i++], obj[i++]);
		}
		return map;
	}
	
	public static Map bean2Map(Object bean) {
		BeanMap map = BeanMap.create(bean);
		return map;
	}
}
