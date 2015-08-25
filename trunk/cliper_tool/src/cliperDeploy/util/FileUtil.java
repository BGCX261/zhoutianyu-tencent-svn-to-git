package cliperDeploy.util;

import cliperDeploy.conf.CliperConfig;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import org.apache.mina.core.buffer.IoBuffer;

public class FileUtil
{
    public static CliperConfig createCliperConfigFromConf(String confPath)
    {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(confPath)));
            CliperConfig kc = new CliperConfig();
            String tempString = null;
            String[] keyValues = new String[2];
            while ((tempString = reader.readLine()) != null) {
                if (!"#".equalsIgnoreCase(tempString))
                {
                    keyValues = tempString.split("=");
                    Field[] fidlds = kc.getClass().getFields();
                    for (Field field : fidlds) {
                        if (keyValues[0].equals(field.getName()))
                            field.set(kc, keyValues[1]);
                    }
                }
            }
            reader.close();
            return kc;
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static byte[] getBytesFromFile(File file)
    {
        byte[] ret = (byte[])null;
        try {
            if (file == null) {
                return null;
            }
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(
                in.available());
            byte[] b = new byte[in.available()];
            int n;
            while ((n = in.read(b)) != -1)
            {
                out.write(b, 0, n);
            }
            in.close();
            out.close();
            return out.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static File replaceFileFromBytes(byte[] bytes, String outputFile)
    {
        File ret = null;
        BufferedOutputStream stream = null;
        try {
            ret = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(ret);
            stream = new BufferedOutputStream(fstream);
            stream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    public static void readKeyBytesFile(File file, List<Key> keyList)
    {
        if (!file.getName().endsWith(".inKey")) {
            return;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(
                file.getAbsolutePath())));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                char[] keyChars = tempString.toCharArray();
                byte[] keyBytes = changeChar2Hpy(keyChars);
                Key key = new SecretKeySpec(keyBytes, "AES");
                keyList.add(key);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static void createNewDefaultKey(File file, int keyCount, List<Key> keyBytesList)
        throws NoSuchAlgorithmException, IOException
    {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        for (int i = 0; i < keyCount; i++) {
            Key key = keyGenerator.generateKey();
            recoedKey(bw, null, key);
            keyBytesList.add(key);
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }

    public static String changeByte2String(byte[] bytes)
    {
        return String.valueOf(changeByte2HpyChar(bytes));
    }

    public static byte[] changeChar2Hpy(char[] src)
    {
        byte[] res = new byte[src.length / 2];
        for (int i = 0; i < res.length; i++) {
            res[i] = ((byte)(changeHpyChar2Byte(src[(i * 2)]) << 4 | changeHpyChar2Byte(src[(i * 2 + 1)])));
        }
        return res;
    }

    public static byte changeHpyChar2Byte(char c)
    {
        if (c > '9') {
            return (byte)(c - 'W');
        }
        return (byte)(c - '0');
    }

    public static char[] changeByte2HpyChar(byte[] src)
    {
        char[] res = new char[src.length * 2];
        for (int i = 0; i < src.length; i++) {
            res[(i * 2)] = changeHyp2Char(src[i] >> 4 & 0xF);
            res[(i * 2 + 1)] = changeHyp2Char(src[i] & 0xF);
        }
        return res;
    }

    public static char changeHyp2Char(int value) {
        if ((value < 0) || (value > 15)) {
            System.err.println("para not legal");
        }
        if (value < 10) {
            return (char)(48 + value);
        }
        return (char)(87 + value);
    }

    public static byte[] getByteArrayFromBuff(IoBuffer buff)
    {
        short length = buff.getShort();
        if (length <= 0) {
            return null;
        }
        byte[] res = new byte[length];
        buff.get(res);
        return res;
    }

    private static void recoedKey(BufferedWriter s, String name, Key key)
        throws IOException
    {
        if (name != null) {
            s.write(name + "=");
        }
        s.write(changeByte2HpyChar(key.getEncoded()));
    }

    public static void putByteArray2Buff(IoBuffer buff, byte[] arr)
    {
        if ((arr == null) || (arr.length == 0)) {
            buff.putShort((short)0);
            return;
        }
        if (arr.length > 65535) {
            System.err.println("字节数组长度过长。");
        }
        buff.putShort((short)arr.length);
        buff.put(arr);
    }

    public static byte[] buff2Array(IoBuffer buff)
    {
        byte[] res = new byte[buff.position()];
        System.arraycopy(buff.array(), 0, res, 0, res.length);
        return res;
    }
}