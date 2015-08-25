package com.cyou.wg.sns.ctsvr.startboot.loader;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.Key;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import net.spy.memcached.MemcachedClient;

public class CustomClassLoader extends BaseClassLoader {

	protected Map<String, byte[]> classByteArr = new HashMap<String, byte[]>();
	protected Map<String,Class<?>> classMap = new HashMap<String,Class<?>>();
	protected byte buff[] = new byte[128*1024];
	private String ip;
	private List<Key> kList = null;
	private MemcachedClient dataClient = null;
	public static Cipher decode;
	
	public CustomClassLoader(URL urls[]) throws IOException {
		super(urls);
		init(urls);
	}
	
	public Class<?> findClass(String name) throws ClassNotFoundException {
		if (name.indexOf("com.cyou.wg") < 0)
			return super.findClass(name);
		if (classMap.containsKey(name))
			return (Class<?>)classMap.get(name);
		byte b[] = null;
		try
		{
			b = loadClassData(name);
		}
		catch (Exception e)
		{
			b = null;
			e.printStackTrace();
		}
		if (b != null)
		{
			Class<?> clazz = defineClass(name, b, 0, b.length);
			classMap.put(name, clazz);
			return clazz;
		} else
		{
			return null;
		}
	}
	
	public void init(URL urls[]) throws IOException {
		for(URL url : urls){
			if (url.getFile().indexOf("sns") < 0 || url.getFile().indexOf("startboot") >= 0)
				continue;
			File f = new File(url.getFile());
			JarFile jar = new JarFile(f);
			Enumeration<JarEntry> entrys = jar.entries();
			while(entrys.hasMoreElements()){
				JarEntry jarEntry = (JarEntry)entrys.nextElement();
				if(jarEntry.getExtra() != null && jarEntry.getName().startsWith("com/cyou/wg") && jarEntry.getName().indexOf("com/cyou/wg/startboot") < 0) {
					classByteArr.put(jarEntry.getName(), jarEntry.getExtra());
				}
			}
		}

		Properties p = new Properties();
		p.load(new FileInputStream(new File("conf/centerServer/need.cf")));
		ip = p.getProperty("ip");
	}
	
	private byte[] loadClassData(String name) throws Exception {
		String nm = (new StringBuilder()).append(name.replace(".", "/")).append(".class").toString();
		byte codeBytes[] = (byte[])classByteArr.get(nm);
		codeBytes = getOriginClassData(codeBytes, nm);
		return codeBytes;
	}
	
	private byte[] getOriginClassData(byte fileBytes[], String name) throws Exception {
		if ((kList == null || kList.size() == 0) && !loadKList())
			throw new Exception((new StringBuilder()).append("load k error: ip=").append(ip).toString());
		int hashcode = name.hashCode();
		int index = Math.abs(hashcode) % kList.size();
		Key key = (Key)kList.get(index);
		if (key == null)
			throw new Exception((new StringBuilder()).append("get k error: kList.size=").append(kList.size()).append(": index=").append(index).append(": classnm=").append(name).toString());
		if (decode == null)
			decode = Cipher.getInstance("AES");
		decode.init(2, key);
		if (fileBytes == null)
			return null;
		else
			return decode.doFinal(fileBytes);
	}
	
	
	private boolean loadKList() {
		if (ip == null || "".equals(ip))
			return false;
		if (dataClient == null)
			try
			{
				dataClient = new MemcachedClient(new InetSocketAddress[] {
					new InetSocketAddress(ip, 40000)
				});
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		byte b[] = (byte[])(byte[])dataClient.get("data_mem|data");
		if (b == null)
			return false;
		ByteBuffer buff = ByteBuffer.wrap(b);
		int cn = buff.getInt();
		for (int i = 0; i < cn; i++)
		{
			Key key = new SecretKeySpec(getByteArrayFromBuff(buff), "AES");
			if (kList == null)
				kList = new ArrayList<Key>();
			kList.add(key);
		}

		dataClient.shutdown();
		return true;
	}
	
	
	private byte[] getByteArrayFromBuff(ByteBuffer buff) {
		short length = buff.getShort();
		if (length <= 0)
		{
			return null;
		} else
		{
			byte res[] = new byte[length];
			buff.get(res);
			return res;
		}
	}
	
}
