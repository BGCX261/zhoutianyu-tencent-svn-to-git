package cliperDeploy.domain;

import cliperDeploy.conf.CliperConfig;
import cliperDeploy.util.FileUtil;
import cliperDeploy.util.KeyFile;
import java.io.File;
import java.net.InetSocketAddress;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;
import org.apache.mina.core.buffer.IoBuffer;

public class UpdateKeyToRemote
{
    private CliperConfig cc = null;

    private List<Key> keyList = null;
    public static MemcachedClient dataClient;

    public UpdateKeyToRemote(String CliperConfPath)
        throws Exception
    {
        this.cc = FileUtil.createCliperConfigFromConf(CliperConfPath);
        if (this.cc == null) {
            throw new Exception("加密器构造错误：" + CliperConfPath);
        }
        this.keyList = new ArrayList();
    }

    public void startUpdate() throws Exception {
        List list = new ArrayList();
        loadFileKeys(this.cc.keyFileDir, list);
        makeFileKeys(list);
        if (this.keyList.size() == 0) {
            throw new Exception("没找到key文件，请尼玛检查路径：" + this.cc.keyFileDir);
        }
        setMemcached();
    }

    private List<KeyFile> loadFileKeys(String strPath, List<KeyFile> list)
    {
        File file = null;
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        if (files == null)
            return list;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                loadFileKeys(files[i].getAbsolutePath(), list);
            } else {
                file = files[i];
                if ((file != null) && (file.getName().endsWith(".inKey")))
                {
                    list.add(new KeyFile(file));
                }
            }
        }
        return list;
    }

    public void makeFileKeys(List<KeyFile> list) {
        KeyFile[] fs = (KeyFile[])list.toArray(new KeyFile[list.size()]);
        Arrays.sort(fs);
        for (KeyFile keyFile : fs)
            FileUtil.readKeyBytesFile(keyFile.getFile(), this.keyList);
    }

    private void setMemcached()
        throws Exception
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
}