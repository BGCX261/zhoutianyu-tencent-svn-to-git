package com.cyou.wg.sns.gs.core.dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.cglib.beans.BeanMap;

import org.aspectj.runtime.internal.cflowstack.ThreadStack;
import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.cyou.wg.sns.gs.core.domain.ClassPath;
import com.cyou.wg.sns.gs.core.exception.CyouSysException;
import com.cyou.wg.sns.gs.core.factory.fileModify.FileModifyFactory;
import com.cyou.wg.sns.gs.core.factory.log.LogFactory;
import com.cyou.wg.sns.gs.core.util.StringUtil;


public class CoreDao4Xml{
	
	public static FileCheck check;
	
	

	public CoreDao4Xml() {
		if(check == null) {
			check = new FileCheck();
			check.setDaemon(true);
			check.setName("FileModifyCheck");
			check.start();
		}
	}
	
	
	
	
	
	/**
	 * 把配置文件初始化为一个列表
	 * @param fileName
	 * @param c
	 * @return
	 */
	protected <T> List<T> coverXml2List(String fileName, Class<T> c){
		File file;
		try {
			file = getFile(fileName);
			check.addFile2Check(file, FileCheck.XML_2_LIST,c);
			return this.coverXml2List0(file, c);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CyouSysException("初始化数据错误：" + fileName);
		}
	}
	
	protected <T> List<T> coverXml2List0(File file, Class<T> c) throws DocumentException, InstantiationException, IllegalAccessException {
		SAXReader saxReader = new SAXReader();
		Element root = saxReader.read(file).getRootElement();
		List<Element> list = root.elements();
		List<T> result = new ArrayList<T>();
		for(int i = 0; i < list.size(); i++) {
			result.add(coverElement2Object(list.get(i), c));
		}
		return result;
	}
	
	private <T> T coverElement2Object(Element e, Class<T> c) throws InstantiationException, IllegalAccessException {
		T t = c.newInstance();
		BeanMap map = BeanMap.create(t);
		List<Attribute> list = e.attributes();
		for(Attribute a : list) {
			Object obj = map.get(a.getName());
			if(obj instanceof Long) {
				map.put(a.getName(), (long)Double.valueOf(a.getStringValue()).doubleValue());
			}else if(obj instanceof Integer) {
				map.put(a.getName(), (int)Double.valueOf(a.getStringValue()).doubleValue());
			}else if(obj instanceof Short) {
				map.put(a.getName(), (short)Double.valueOf(a.getStringValue()).doubleValue());
			}else if(obj instanceof Byte) {
				map.put(a.getName(), (byte)Double.valueOf(a.getStringValue()).doubleValue());
			}else if(obj instanceof Boolean) {
				map.put(a.getName(), Boolean.valueOf(a.getStringValue()));
			}else if(obj instanceof String) {
				if(StringUtil.isEmpty(a.getStringValue())) {
					obj = null;
				}
				map.put(a.getName(), obj);
			}else if(obj == null ) {
				if(!StringUtil.isEmpty(a.getStringValue())) {
					map.put(a.getName(), a.getStringValue());
				}
			}else {
				throw new CyouSysException("不支持数据类型：" + obj.getClass().toString());
			}
		}
		return (T)map.getBean();
	}
	
	/**
	 * 把配置文件初始化为一个对象
	 * @param fileName
	 * @param c
	 * @return
	 */
	protected <T> T coverXml2Object(String fileName, Class<T> c){
		File file;
		try {
			file = ClassPath.location.createRelative("/modelData/" + fileName).getFile();
			return this.coverXml2Objcet0(file, c);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CyouSysException("初始化数据错误：" + fileName);
		}
	}
	private <T> T coverXml2Objcet0(File file, Class<T> c) throws DocumentException, InstantiationException, IllegalAccessException {
		SAXReader saxReader = new SAXReader();
		Element root = saxReader.read(file).getRootElement();
		List<Element> list = root.elements();
		return coverElement2Object2(list.get(0), c);
	}
	/**
	 * 把元素属性转换为Int数组
	 * 数组名统一为properties
	 * @param e
	 * @param c
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private <T> T coverElement2Object2(Element e, Class<T> c) throws InstantiationException, IllegalAccessException {
		T t = c.newInstance();
		BeanMap map = BeanMap.create(t);
		int[] properties = new int[e.attributeCount()];
		for(int i = 0; i < properties.length; i++) {
			properties[i] = Integer.valueOf(e.attribute(i).getStringValue());
		}
		map.put("properties", properties);
		return (T)map.getBean();
	}
	
	
	/**
	 * 把对象转换为2维int数组
	 * @param file
	 * @param c
	 * @return
	 * @throws DocumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException 
	 */
	protected <T> T coverXmlTo2DArray(String fileName, Class<T> c) {
		try {
			File file = getFile(fileName);
			check.addFile2Check(file, FileCheck.XML_2_ARRAY,c);
			return coverXmlTo2DArray0(file, c);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CyouSysException("初始化数据错误：" + fileName);
		}
	}
	
	private <T> T coverXmlTo2DArray0(File file, Class<T> c) throws Exception{
			SAXReader saxReader = new SAXReader();
			Element root = saxReader.read(file).getRootElement();
			List<Element> list = root.elements();
			int width = list.get(0).attributeCount();
			int[][] properties = new int[list.size()][width];
			for(int i = 0; i < list.size(); i++) {
				for(int j = 0; j < width; j++) {
					properties[i][j] = Integer.valueOf(list.get(i).attribute(i).getStringValue());
				}
			}
			T t = c.newInstance();
			BeanMap map = BeanMap.create(t);
			map.put("properties", properties);
			return (T)map.getBean();
	}
	
	private File getFile(String fileName) throws IOException {
		File file = ClassPath.location.createRelative("/modelData/" + fileName).getFile();
		return file;
	}
	/**
	 * 检查
	 * @author Administrator
	 *
	 */
	class FileCheck extends Thread {
		
		public static final int XML_2_ARRAY = 3;
		public static final int XML_2_OBJECT = 2;
		public static final int XML_2_LIST = 1;
		private Object lock = new Object();
		/**
		 * 文件最后变更时间
		 */
		private List<FileCell> fileList = new ArrayList<FileCell>();
		
		/**
		 * 添加需要校验的文件
		 * @param file
		 */
		public void addFile2Check(File file, int type, Class clazz) {
			FileCell cell = new FileCell();
			cell.file = file;
			cell.lastModifyTime = file.lastModified();
			cell.type = type;
			cell.clazz = clazz;
			synchronized (lock) {
				fileList.add(cell);
			}
		}
		/**
		 * 执行校验的线程
		 */
		public void run() {
			while(true) {
				synchronized (lock) {
					for(FileCell cell : fileList) {
						if(cell.lastModifyTime != cell.file.lastModified()) {//文件有过变化
							cell.lastModifyTime = cell.file.lastModified();//重置变化的配置文件
							try {
								reloadFile(cell);//重新加载
							} catch (Exception e) {
								LogFactory.getLogger(LogFactory.SYS_ERROR_LOG).error("reload file error :" + cell.file.getName());
								e.printStackTrace();
							}
						}
					}
				}
				try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		/**
		 * 重新加载配置文件
		 * @param cell
		 * @throws Exception 
		 */
		public void reloadFile(FileCell cell) throws Exception {
			if(cell.type == XML_2_ARRAY) {//把对象转换为2维数组
				Object obj = coverXmlTo2DArray0(cell.file, cell.clazz);
				FileModifyFactory.createExecutor().execute4Object(obj);
			}else if(cell.type == XML_2_LIST) {
				List list = coverXml2List0(cell.file, cell.clazz);
				FileModifyFactory.createExecutor().execute4List(list);
			}else if(cell.type == XML_2_OBJECT) {
				Object obj = coverXml2Objcet0(cell.file, cell.clazz);
				FileModifyFactory.createExecutor().execute4Object(obj);
			}
			LogFactory.getLogger(LogFactory.SYS_WARN_LOG).warn("reload file :" + cell.file.getName() + "  complete");
		}
	}
	/**
	 * 文件类型
	 * @author Administrator
	 *
	 */
	class FileCell {
		File file;
		long lastModifyTime;
		int type;
		Class clazz;
	}
	
	
	
}
