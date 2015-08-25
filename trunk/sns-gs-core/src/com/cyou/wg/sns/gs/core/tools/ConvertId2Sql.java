package com.cyou.wg.sns.gs.core.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import com.cyou.wg.sns.gs.core.domain.ClassPath;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;

/**
 * 把sql的id和sql对应起来，记录为文件
 * @author Administrator
 *
 */
public class ConvertId2Sql {
	public static void init() throws IOException {
		File file = ClassPath.location.createRelative("sql/sqlmap-config.xml").getFile();
		File outPut = ClassPath.location.createRelative("sqlIdConf.cf").getFile();
		if(outPut.exists()) {
			outPut.delete();
		}
		outPut.createNewFile();
		SqlMapClientImpl impl = (SqlMapClientImpl) SqlMapClientBuilder.buildSqlMapClient(new FileInputStream(file));
		SqlMapExecutorDelegate d = impl.getDelegate();
		Iterator<String> it = d.getMappedStatementNames();
		BufferedWriter bw = new BufferedWriter(new FileWriter(outPut));
		while(it.hasNext()) {
			String s = it.next();
			MappedStatement ms = impl.getMappedStatement(s);
			String value = ms.getSql().getSql(null, null);
			bw.write(s + "=" + value);
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}
}
