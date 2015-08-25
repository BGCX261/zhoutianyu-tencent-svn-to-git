package cliperDeploy.domain;

import cliperDeploy.conf.CliperConfig;
import cliperDeploy.util.FileUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;
import org.apache.mina.core.buffer.IoBuffer;

public class CliperEncode
{
    private CliperConfig cc = null;

    private List<Key> keyList = null;
    public static Cipher encode;
    public static Cipher decode;
    public static MemcachedClient dataClient;

    public CliperEncode(String CliperConfPath)
        throws Exception
    {
        this.cc = FileUtil.createCliperConfigFromConf(CliperConfPath);
        if (this.cc == null) {
            throw new Exception("加密器构造错误：" + CliperConfPath);
        }
        this.keyList = new ArrayList();
    }

    public void startEncode() throws Exception
    {
        if (this.keyList.size() == 0) {
            this.keyList = createKeyFiles(this.cc.keyFileDir, 
                Integer.parseInt(this.cc.keyFileCount), 
                Integer.parseInt(this.cc.keyFileKeyCount));
            if (this.keyList.size() == 0) {
                throw new Exception("不能生成key文件" + this.cc.keyFileDir);
            }
        }

        setMemcached();

        cliperJar();
    }
    
    public void startDecode() throws Exception
    {
        if (this.keyList.size() == 0) {
            this.keyList = createKeyFiles(this.cc.keyFileDir, 
                Integer.parseInt(this.cc.keyFileCount), 
                Integer.parseInt(this.cc.keyFileKeyCount));
            if (this.keyList.size() == 0) {
                throw new Exception("不能生成key文件" + this.cc.keyFileDir);
            }
        }

        decodeJar();
    }

    public void dnCodeTest()
    {
    }

    private void cliperJar() throws Exception
    {
        String[] jars = this.cc.getLoadJarName();
        if ((jars == null) || (jars.length <= 0)) {
            throw new Exception("对jar加密取不到jar名：" + this.cc.loadJarDir);
        }
        for (String string : jars)
            visitJar(string);
    }
    
    private void decodeJar() throws Exception
	{
	    String[] jars = this.cc.getLoadJarName();
	    if ((jars == null) || (jars.length <= 0)) {
	        throw new Exception("对jar加密取不到jar名：" + this.cc.loadJarDir);
	    }
	    for (String string : jars)
	        visitJar2(string);
	}

    private void cliperClass()
    {
    }

    private byte[] encode(byte[] byteFile, String className)
    {
        int hashcode = className.hashCode();
        int index = Math.abs(hashcode) % this.keyList.size();
        Key key = (Key)this.keyList.get(index);
        try {
            if (encode == null) {
                encode = Cipher.getInstance("AES");
            }
            encode.init(1, key);

            return encode.doFinal(byteFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decode(byte[] fileBytes, String className)
    {
        if ((this.keyList == null) || (this.keyList.size() == 0)) {
            loadKeyFromMemcached();
        }
        int hashcode = className.hashCode();
        int index = Math.abs(hashcode) % this.keyList.size();
        Key key = (Key)this.keyList.get(index);
        if (key == null) {
            System.err.println("解密key获取失败" + index);
            return null;
        }
        try {
            if (decode == null) {
                decode = Cipher.getInstance("AES");
            }
            decode.init(2, key);
            return decode.doFinal(fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }return null;
    }

    /**
     * encode
     * @param strPath
     * @throws Exception
     */
    private void visitJar(String strPath) throws Exception
    {
        File f = new File(strPath);
        if (!f.exists()) {
            System.out.println("找不到jar文件,将跳过：" + strPath);
            return;
        }
        JarFile jar = new JarFile(f);
        File handled = new File(strPath + "-");
        JarOutputStream jos = null;
        InputStream in = null;
        try {
            jos = new JarOutputStream(new FileOutputStream(handled));
            for (Enumeration entrys = jar.entries(); entrys
                .hasMoreElements(); )
            {
                JarEntry jarEntry = (JarEntry)entrys.nextElement();
                in = jar.getInputStream(jarEntry);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int iMark = 0;
                try {
                    while ((iMark = in.read()) != -1) {
                        baos.write(iMark);
                    }
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] bb = baos.toByteArray();
                in.read(bb);
                JarEntry jarEntryNew = new JarEntry(jarEntry.getName());
                if (jarEntry.getName().endsWith(".class"))
                {
                    bb = encode(bb, jarEntry.getName());
                    byte[] extraByte = new byte[bb.length];
                    int i = extraByte.length - 1; for (int j = bb.length - 1; i > -1; j--) {
                        extraByte[i] = bb[j];

                        i--;
                    }

                    jarEntryNew.setExtra(extraByte);
                }

                jos.putNextEntry(jarEntryNew);
                in.close();
                jos.write(bb);
                jos.closeEntry();
            }
            jos.close();
            jar.close();

            f.delete();
            handled.renameTo(new File(strPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (jos != null) {
                try {
                    jos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (jar != null)
                try {
                    jar.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * decode
     * @param strPath
     * @throws Exception
     */
    private void visitJar2(String strPath) throws Exception
	{
	    File f = new File(strPath);
	    if (!f.exists()) {
	        System.out.println("找不到jar文件,将跳过：" + strPath);
	        return;
	    }
	    JarFile jar = new JarFile(f);
	    File handled = new File(strPath + "-");
	    JarOutputStream jos = null;
	    try {
	        jos = new JarOutputStream(new FileOutputStream(handled));
	        for (Enumeration entrys = jar.entries(); entrys.hasMoreElements(); )
	        {
	            JarEntry jarEntry = (JarEntry)entrys.nextElement();
	
	            byte[] bb = null;
	            JarEntry jarEntryNew = new JarEntry(jarEntry.getName());
	            if (jarEntry.getName().endsWith(".class"))
	            {
	            	bb = jarEntry.getExtra();
	
	                jos.putNextEntry(jarEntryNew);
	                jos.write(decode(bb, jarEntry.getName()));
		            jos.closeEntry();
	            }
	        }
	        jos.close();
	        jar.close();
	
	        f.delete();
	        handled.renameTo(new File(strPath));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    finally
	    {
	        if (jos != null) {
	            try {
	                jos.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        if (jar != null)
	            try {
	                jar.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	    }
	}
    
    
    
    private void visitAndOptFileList(String strPath, int optFlg)
    {
        File file = null;
        byte[] bytesFile = (byte[])null;
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        if (files == null)
            return;
        for (int i = 0; i < files.length; i++)
            if (files[i].isDirectory()) {
                visitAndOptFileList(files[i].getAbsolutePath(), optFlg);
            } else {
                file = files[i];
                if (file != null)
                {
                    if (optFlg == -1) {
                        if (file.getName().endsWith(".class"))
                        {
                            bytesFile = FileUtil.getBytesFromFile(file);
                            bytesFile = encode(bytesFile, file.getName());
                            if (bytesFile == null) {
                                System.err.println("加密错误");
                                return;
                            }
                            FileUtil.replaceFileFromBytes(bytesFile, 
                                file.getAbsolutePath());
                        } } else if (optFlg == 1) {
                        FileUtil.readKeyBytesFile(file, this.keyList);
                    } else if ((optFlg == -2) && 
                        (file.getName().endsWith(".class")))
                    {
                        bytesFile = FileUtil.getBytesFromFile(file);
                        bytesFile = decode(bytesFile, file.getName());
                        if (bytesFile == null) {
                            System.err.println("解密错误");
                            return;
                        }
                        FileUtil.replaceFileFromBytes(bytesFile, 
                            file.getAbsolutePath());
                    }
                }
            }
    }

    private void loadKeyFromMemcached()
    {
        if (dataClient == null) {
            try {
                dataClient = new MemcachedClient(new InetSocketAddress[] { new InetSocketAddress(
                    this.cc.keyFileMemcachedURL, 
                    Integer.parseInt(this.cc.keyFileMemcachedPort)) });
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        byte[] b = (byte[])dataClient.get("data_mem|data");
        IoBuffer buff = IoBuffer.wrap(b);
        int cn = buff.getInt();
        for (int i = 0; i < cn; i++) {
            Key key = new SecretKeySpec(FileUtil.getByteArrayFromBuff(buff), 
                "AES");
            this.keyList.add(key);
        }
        dataClient.shutdown();
    }

    private void setMemcached() throws Exception
    {
        if (dataClient == null) {
            dataClient = new MemcachedClient(new InetSocketAddress[] { new InetSocketAddress(
                this.cc.keyFileMemcachedURL, 
                Integer.parseInt(this.cc.keyFileMemcachedPort)) });
        }

        IoBuffer buff = IoBuffer.allocate(32);
        buff.setAutoExpand(true);
        buff.putInt(this.keyList.size());
        for (int i = 0; i < this.keyList.size(); i++) {
            FileUtil.putByteArray2Buff(buff, ((Key)this.keyList.get(i)).getEncoded());
        }
        byte[] bb = FileUtil.buff2Array(buff);
        OperationFuture res = dataClient.set(
            "data_mem|data", 2147483647, bb);

        boolean r = ((Boolean)res.get()).booleanValue();
        dataClient.shutdown();
    }

    public List<Key> createKeyFiles(String keyURL, int fileCount, int inFileKeyCount)
        throws NoSuchAlgorithmException, IOException
    {
        visitAndOptFileList(keyURL, 1);
        if (this.keyList.size() > 0) {
            return this.keyList;
        }

        List keyBytesList = new ArrayList();
        for (int i = 0; i < fileCount; i++) {
            FileUtil.createNewDefaultKey(new File(keyURL + 
                i + 
                ".inKey"), inFileKeyCount, 
                keyBytesList);
        }
        return keyBytesList;
    }
}