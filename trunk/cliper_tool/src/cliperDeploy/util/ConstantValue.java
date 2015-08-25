package cliperDeploy.util;

import java.io.File;

public class ConstantValue
{
    public static final String CONF_PATH = System.getProperty("user.dir") + 
        File.separator + 
        "conf" + 
        File.separator + 
        "jiamikey.keyConfig";
    public static final String KEY_MEMCACHED_KEY = "data_mem|data";
    public static final String CLIPER_ALG = "AES";
    public static final int KEY_LONG = 128;
    public static final int OPT_ENCODE = -1;
    public static final int OPT_DECODE = -2;
    public static final int OPT_RELOAD_KEY_FILE = 1;
    public static final String KEY_NM_HEADER = "";
    public static final String KEY_NM_BOTTOM = ".inKey";
}