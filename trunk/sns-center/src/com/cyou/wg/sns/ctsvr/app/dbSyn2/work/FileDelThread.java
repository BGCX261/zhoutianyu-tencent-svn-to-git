package com.cyou.wg.sns.ctsvr.app.dbSyn2.work;

import java.io.File;

import com.cyou.wg.sns.ctsvr.core.log.LogFactory;

public class FileDelThread extends Thread {

	private int fileDelInterval = 60 * 1000;

	public FileDelThread(int fileDelInterval) {
		this.fileDelInterval = fileDelInterval * 1000;
	}

	public void run() {
		while(true) {
			try {
				File file = FileUtil.getDbFile();
				File files[] = FileUtil.getDelFileList(file, FileUtil.FILENAME_SUFFIX_DEL);
				if (files != null && files.length > 0) {
					for(File f : files) {
						if (f.delete())
							LogFactory.getLogger(LogFactory.SYS_INFO_LOG).info(getName()+"删除文件成功："+f.getName());
						else
							LogFactory.getLogger(LogFactory.SYS_INFO_LOG).error(getName()+"文件删除线程文件删除失败："+getName()+f.getName());
					}

				}
			} catch (Exception e) {
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("文件删除线程文件删除失败："+getName(), e);
			}
			try {
				Thread.sleep(fileDelInterval);
			} catch (Exception e) {
				LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("文件删除线程暂停失败："+getName(), e);
			}
		} 
	}

}
