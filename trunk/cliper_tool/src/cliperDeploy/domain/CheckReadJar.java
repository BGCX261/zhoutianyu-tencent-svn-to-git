package cliperDeploy.domain;

import cliperDeploy.conf.CliperConfig;
import cliperDeploy.util.FileUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class CheckReadJar
{
    private CliperConfig cc = null;

    public CheckReadJar(String CliperConfPath) throws Exception {
        this.cc = FileUtil.createCliperConfigFromConf(CliperConfPath);
        if (this.cc == null)
            throw new Exception("加密器构造错误：" + CliperConfPath);
    }

    public void startCheck() throws Exception
    {
        String[] jars = this.cc.getLoadJarName();
        if ((jars == null) || (jars.length <= 0)) {
            throw new Exception("对jar加密取不到jar名：" + this.cc.loadJarDir);
        }
        for (String string : jars) {
            visitJar(string);
            System.out.println();
            visitJar(string);
        }
    }

    private void visitJar_bk(String strPath)
        throws Exception
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

                byte[] bb = new byte[(int)jarEntry.getSize()];
                System.out.println(" bb length " + bb.length);
                in.read(bb);

                JarEntry jarEntryNew = new JarEntry(jarEntry.getName());

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

    private void visitJar(String strPath)
        throws Exception
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
                int i = 0;
                try {
                    while ((i = in.read()) != -1) {
                        baos.write(i);
                    }
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] bb = baos.toByteArray();

                in.read(bb);
                JarEntry jarEntryNew = new JarEntry(jarEntry.getName());

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
}