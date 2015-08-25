package cliperDeploy.conf;

public class CliperConfig
{
    public String keyFileCount;
    public String keyFileKeyCount;
    public String keyFileDir;
    public String keyFileMemcachedURL;
    public String keyFileMemcachedPort;
    public String loadJarDir;
    public String[] loadJarName;

    public String[] getLoadJarName()
    {
        if (this.loadJarName == null) {
            String[] paths = this.loadJarDir.split(",");
            this.loadJarName = new String[paths.length];
            for (int i = 0; i < paths.length; i++) {
                String[] temp = paths[i].split("/");
                this.loadJarName[i] = temp[(temp.length - 1)];
            }
        }
        return this.loadJarName;
    }
}