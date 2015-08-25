import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import vo.MessageContentVO;
import vo.MessageVO;

public class OutputFile {

	public static String TEMPLATE_FOLDER_NAME = "template";

	public static String OUTPUT_FOLDER_NAME = "output";

	public static String OUTPUT_CLIENT_FOLDER_NAME = OUTPUT_FOLDER_NAME + "/client";

	public static String OUTPUT_SERVER_FOLDER_NAME = OUTPUT_FOLDER_NAME + "/server";

	public static String OUTPUT_CLIENT_MESSAGE_CUSTOM_FOLDER_NAME = OUTPUT_CLIENT_FOLDER_NAME + "/custom";

	public static String OUTPUT_CLIENT_MESSAGE_VO_FOLDER_NAME = OUTPUT_CLIENT_FOLDER_NAME + "/vo";

	public static String OUTPUT_SERVER_MESSAGE_REQUEST_FOLDER_NAME = OUTPUT_SERVER_FOLDER_NAME + "/request";

	public static String OUTPUT_SERVER_MESSAGE_RESPONSE_FOLDER_NAME = OUTPUT_SERVER_FOLDER_NAME + "/response";

	public static String OUTPUT_SERVER_MESSAGE_VO_FOLDER_NAME = OUTPUT_SERVER_FOLDER_NAME + "/vo";

	public static String OUTPUT_SERVER_MESSAGE_HANDLER_FOLDER_NAME = OUTPUT_FOLDER_NAME + "/server";

	public static String OUTPUT_SERVER_SERVER_MESSAGE_REQUEST_FOLDER_NAME = OUTPUT_SERVER_FOLDER_NAME + "/server";

	public static String OUTPUT_SERVER_SERVER_MESSAGE_RESPONSE_FOLDER_NAME = OUTPUT_SERVER_FOLDER_NAME + "/server";

	public static String OUTPUT_SERVER_SERVER_MESSAGE_VO_FOLDER_NAME = OUTPUT_SERVER_FOLDER_NAME + "/server";

	public static String TEMPLATE_CLIENT_MESSAGE = "ClientMessage.ftl";


	public static String TEMPLATE_CLIENT_OBJECT = "ClientClass.ftl";

	public static String TEMPLATE_CLIENT_MESSAGE_TYPE = "ClientMessageType.ftl";

	public static String TEMPLATE_CLIENT_MESSAGE_REGISTER = "ClientMessageRegister.ftl";

	public static String TEMPLATE_SERVER_NET_MSG = "ServerNetMsg.ftl";

	public static String TEMPLATE_SERVER_MESSAGE = "ServerMessage.ftl";

	public static String TEMPLATE_SERVER_HANDLER = "ServerHandler.ftl";

	public static String TEMPLATE_SERVER_OBJECT = "ServerClass.ftl";

	public static String TEMPLATE_SERVER_SERVER_MESSAGE = "ServerServerMessage.ftl";

	public static String TEMPLATE_SERVER_SERVER_OBJECT = "ServerServerClass.ftl";

	public static String EXT_H = ".h";

	public static String EXT_AS = ".as";

	public static String EXT_JAVA = ".java";

	public static String SERVER_NET_MSG_C_H = "ServerNetMsg_C" + EXT_H;

	public static String SERVER_NET_MSG_S_H = "ServerNetMsg_S" + EXT_H;

	public static String HTTP_MESSAGE_TYPE_AS = "HTTPMessageType" + EXT_AS;

	public static String HTTP_MESSAGE_REGISTER_AS = "HTTPMessageRegister" + EXT_AS;

	public void clientStart() {
//		this.startOutputClientMessageFile();
//		this.startOutputClientObjectFile();
//		this.startOutputClientMessageTypeFile();
//		this.startOutputClientMessasgeRegisterFile();
		this.startOutputServerRequestMessageFile(OUTPUT_SERVER_MESSAGE_REQUEST_FOLDER_NAME,TEMPLATE_SERVER_MESSAGE);
		this.startOutputServerResponseMessageFile(OUTPUT_SERVER_MESSAGE_RESPONSE_FOLDER_NAME,TEMPLATE_SERVER_MESSAGE);
//		this.startOutputServerObjectFile(OUTPUT_SERVER_MESSAGE_VO_FOLDER_NAME,TEMPLATE_SERVER_OBJECT);
	}


	
	private void startOutputServerHandler() {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		try {
			cfg.setDirectoryForTemplateLoading(new File("./"+ TEMPLATE_FOLDER_NAME));
			Template temp = cfg.getTemplate(TEMPLATE_SERVER_HANDLER);
			for (int i = 0; i < DataManager.getInstance().getSendMessages().size(); i++) {
				MessageVO messageVO = DataManager.getInstance().getSendMessages().get(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("messageVO", messageVO);
				map.put("typeDef", new TypeDef());
				String outpath = messageVO.getHandlerPackage().replaceAll("\\.", "/");
				String path = "./" + OUTPUT_SERVER_MESSAGE_HANDLER_FOLDER_NAME + "/" + outpath + "/" + StringUtil.toUpperCaseFirstChart(messageVO.getHandler()) + EXT_JAVA;
				File file = getLogFile(path);
				Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
				temp.process(map, out);
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}

	private void startOutputClientMessageFile() {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		try {
			cfg.setDirectoryForTemplateLoading(new File("./" + TEMPLATE_FOLDER_NAME));
			Template temp = cfg.getTemplate(TEMPLATE_CLIENT_MESSAGE);
			for (int i = 0; i < DataManager.getInstance().getMessages().size(); i++) {
				MessageVO messageVO = DataManager.getInstance().getMessages().get(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("messageVO", messageVO);
				map.put("typeDef", new TypeDef());
				String path = "./" + OUTPUT_CLIENT_MESSAGE_CUSTOM_FOLDER_NAME + "/" + messageVO.getName() + EXT_AS;
				File file = getLogFile(path);
				Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
				temp.process(map, out);
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}

	private void startOutputClientObjectFile() {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		try {
			cfg.setDirectoryForTemplateLoading(new File("./" + TEMPLATE_FOLDER_NAME));
			Template temp = cfg.getTemplate(TEMPLATE_CLIENT_OBJECT);
			for (int i = 0; i < DataManager.getInstance().getObjects().size(); i++) {
				MessageContentVO messageContentVO = DataManager.getInstance().getObjects().get(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("messageContentVO", messageContentVO);
				map.put("typeDef", new TypeDef());
				String path = "./" + OUTPUT_CLIENT_MESSAGE_VO_FOLDER_NAME + "/" + messageContentVO.getType() + EXT_AS;
				File file = getLogFile(path);
				Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
				temp.process(map, out);
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}

	private void startOutputClientMessageTypeFile() {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		try {
			cfg.setDirectoryForTemplateLoading(new File("./" + TEMPLATE_FOLDER_NAME));
			Template temp = cfg.getTemplate(TEMPLATE_CLIENT_MESSAGE_TYPE);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("messages", DataManager.getInstance().getMessages());
			String path = "./" + OUTPUT_CLIENT_FOLDER_NAME + "/" + HTTP_MESSAGE_TYPE_AS;
			File file = new File(path);
			Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			temp.process(map, out);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}

	private void startOutputClientMessasgeRegisterFile() {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		try {
			cfg.setDirectoryForTemplateLoading(new File("./" + TEMPLATE_FOLDER_NAME));
			Template temp = cfg.getTemplate(TEMPLATE_CLIENT_MESSAGE_REGISTER);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("messages", DataManager.getInstance().getMessages());
			String path = "./" + OUTPUT_CLIENT_FOLDER_NAME + "/" + HTTP_MESSAGE_REGISTER_AS;
			File file = getLogFile(path);
			Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
	
			temp.process(map, out);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}


	private void startOutputServerRequestMessageFile(String filePath, String templateName) {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		try {
			cfg.setDirectoryForTemplateLoading(new File("./" + TEMPLATE_FOLDER_NAME));
			Template temp = cfg.getTemplate(templateName);
			for (int i = 0; i < DataManager.getInstance().getSendMessages().size(); i++) {
				MessageVO messageVO = DataManager.getInstance().getSendMessages().get(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("messageVO", messageVO);
				map.put("typeDef", new TypeDef());
				String path = "./" + filePath + "/" + messageVO.getName() + EXT_JAVA;
				File file = getLogFile(path);
				Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			
				temp.process(map, out);
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}

	private void startOutputServerResponseMessageFile(String filePath, String templateName) {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		try {
			cfg.setDirectoryForTemplateLoading(new File("./" + TEMPLATE_FOLDER_NAME));
			Template temp = cfg.getTemplate(templateName);
			for (int i = 0; i < DataManager.getInstance().getBackMessages().size(); i++) {
				MessageVO messageVO = DataManager.getInstance().getBackMessages().get(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("messageVO", messageVO);
				map.put("typeDef", new TypeDef());
				String path = "./" + filePath + "/" + messageVO.getName() + EXT_JAVA;
				File file = getLogFile(path);
				Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			
				temp.process(map, out);
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}


	public static File getLogFile(String fileName) throws IOException {
		File file = new File(fileName);
		if(!file.exists()) {
			List<File> tList = new ArrayList<File>();
			while(!file.exists()) {
				tList.add(file);
				file = file.getParentFile();
			}
			for(int i = tList.size() - 1; i >= 0; i--) {
				if(i > 0) {
					tList.get(i).mkdir();
				}
				if(i == 0) {
					tList.get(i).createNewFile();
				}
			}
			file = tList.get(0);
		}
		return file;
	}

		

}
