package cliperDeploy;

import cliperDeploy.domain.CheckReadJar;
import cliperDeploy.domain.CliperEncode;
import cliperDeploy.domain.UpdateKeyToRemote;
import cliperDeploy.util.ConstantValue;

public class Main
{
    public static void main(String[] param)
        throws Exception
    {
        if ((param == null) || (param.length != 1))
//            throw new Exception("启动函数参数指令错误" + param[0]);
        	new CliperEncode(ConstantValue.CONF_PATH).startDecode();
        else if (param[0].equals("encode"))
        {
            new CliperEncode(ConstantValue.CONF_PATH).startEncode();
        } else if (param[0].equals("updateKeyToRemote"))
        {
            new UpdateKeyToRemote(ConstantValue.CONF_PATH).startUpdate();
        } else if (param[0].equals("testreadjar"))
        {
            new CheckReadJar(ConstantValue.CONF_PATH).startCheck();
        } else if (param[0].equals("decode")){
        	new CliperEncode(ConstantValue.CONF_PATH).startDecode();
        }
        else throw new Exception("启动函数参数指令错误" + param[0]);
    }
}