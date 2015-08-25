package cliperDeploy.util;

import java.io.File;

public class KeyFile
    implements Comparable<KeyFile>
{
    private File file;

    public File getFile()
    {
        return this.file;
    }

    public KeyFile(File f) {
        this.file = f;
    }

    public int compareTo(KeyFile arg0)
    {
        int index1 = getKeyFileIndexFromNM(this.file.getName());
        int index2 = getKeyFileIndexFromNM(arg0.getFile().getName());

        if (index1 > index2)
            return 1;
        if (index1 < index2) {
            return -1;
        }
        return 0;
    }

    private int getKeyFileIndexFromNM(String name) {
        return Integer.parseInt(name.split("\\.")[0]);
    }
}