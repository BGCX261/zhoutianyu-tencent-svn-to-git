import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import vo.MessageContentVO;
import vo.MessageVO;

public class GetMessageXML {

	public static String CLIENT_MESSAGE_FOLDER_NAME = "message";

	// 获得当前目录下的xml文件，存入dataManager中
	public void getMessageXMLInCurrentPath() throws Exception {
		File clientFolder = new File("./" + CLIENT_MESSAGE_FOLDER_NAME)
				.getCanonicalFile();
		System.out.println("打开目录: " + clientFolder.getCanonicalPath());
		File[] clientFiles = clientFolder.listFiles();
		if (clientFiles == null) {
			throw new Exception("目录内没有文件 " + clientFolder.getCanonicalPath());
		} else {
			this.parseClientXML(clientFiles);
		}
	}

	private void parseClientXML(File[] files) throws Exception {
		for (int i = 0; i < files.length; i++) {
			File currentFile = (File) files[i];
			if (!currentFile.isFile()) {
				// 如果不是一个文件 则跳过
				continue;
			} else {
				// 是一个文件那么开始解析当他是一个XML
				SAXReader reader = new SAXReader();
				Document document = reader.read(currentFile.getCanonicalPath());
				this.parse(currentFile, document);
			}
		}
		// 开始输出
		OutputFile outputFile = new OutputFile();
		outputFile.clientStart();
	}

	private void parse(File currentFile, Document doc) throws Exception {
		System.out.println("正在解析文件: " + currentFile.getCanonicalPath());
		Element root = doc.getRootElement();
		for (Iterator<Element> i = root.elementIterator(); i.hasNext();) {
			Element element = (Element) i.next();
			MessageVO messageVO = new MessageVO();
			int messageType = 0;
			// 判断是什么类型的消息
			// 发送类型
			if (element.getQName().getName().equals(MessageType.SEND_MESSAGE)) {
				messageType = MessageType.SEND_MESSAGE_ID;

				// 返回类型
			} else if (element.getQName().getName()
					.equals(MessageType.BACK_MESSAGE)) {
				messageType = MessageType.BACK_MESSAGE_ID;
			} else {
				throw new Exception("消息类型XML填写错误："
						+ currentFile.getCanonicalPath() + "\n"
						+ element.getQName().getName());
			}
			messageVO.setMessageTypeID(messageType);
			// 获得ID
			messageVO.setId(Integer.parseInt(element.attribute(MessageType.ID)
					.getValue()));
			// 获取文件名 后面再加上Message
			messageVO.setName(StringUtil.toUpperCaseFirstChart(element
					.attribute(MessageType.NAME).getValue()
					+ MessageType.MESSAGE_CLASS_LAST_STRING));
			// 获得TYPE
			String name = element.attribute(MessageType.NAME).getValue();

			messageVO.setType(name.substring(name.length() - 4).toUpperCase()
					+ "_" + name.substring(0, name.length() - 4).toUpperCase());
			System.out.println("正在解析消息：" + messageVO.getType());
			// 获取注释
			messageVO.setComment(element.attribute(MessageType.COMMENT)
					.getValue());
			// 获取handler
			messageVO
					.setHandler(element.attribute(MessageType.NAME).getValue());
			// 自动生成handler的包名
			if (!(element.attribute(MessageType.HANDLER_PACKAGE) == null)) {
				messageVO.setHandlerPackage(element.attribute(
						MessageType.HANDLER_PACKAGE).getValue());
			}
			// 获取是否自动生成
			if (!(element.attribute(MessageType.IS_AUTO_CREATE) == null)) {
				messageVO.setIsAutoCreate(Integer.parseInt(element.attribute(
						MessageType.IS_AUTO_CREATE).getValue()));
			}

			// 遍历内部
			for (Iterator e = element.elementIterator(); e.hasNext();) {
				Element elementItem = (Element) e.next();
				messageVO.getChildren().add(
						this.parseMessageContentVO(elementItem));
			}
			// 加入集合中
			DataManager.getInstance().getMessages().add(messageVO);
			if (messageVO.getMessageTypeID() == MessageType.SEND_MESSAGE_ID) {
				DataManager.getInstance().getSendMessages().add(messageVO);
			}
			if (messageVO.getMessageTypeID() == MessageType.BACK_MESSAGE_ID) {
				DataManager.getInstance().getBackMessages().add(messageVO);
			}
		}
	}

	// 解析消息内容 递归解析
	private MessageContentVO parseMessageContentVO(Element element) {
		MessageContentVO messageContentVO = new MessageContentVO();
		messageContentVO.setContentType(element.getQName().getName().toLowerCase());
		messageContentVO.setName(element.attributeValue(MessageType.NAME));
		if (!(element.attribute(MessageType.LENGTH) == null)) {
			messageContentVO.setLength(element.attributeValue(MessageType.LENGTH));
		}
		if (!(element.attribute(MessageType.COMMENT) == null)) {
			messageContentVO.setComment(element.attributeValue(MessageType.COMMENT));
		}
		if (element.hasContent()) {
			messageContentVO.setHasChildren(true);
			messageContentVO.setChildren(new ArrayList<MessageContentVO>());
			
			for (Iterator e = element.elementIterator(); e.hasNext();) {
				Element elementItem = (Element) e.next();
				messageContentVO.getChildren().add(this.parseMessageContentVO(elementItem));
			}
		}
		// 如果是对象 或者是一个list对象集合,那么它的type值就是一个类名了，需要把首字母变为大写并存储
		// 如果不是type值就是一个基本类型，就需要定义它的defType并且把类型名小写化存储
		if (messageContentVO.getContentType().equals(MessageType.OBJECT) || (messageContentVO.getContentType().equals(MessageType.LIST) && messageContentVO.getHasChildren())) {
			messageContentVO.setType(StringUtil.toUpperCaseFirstChart(element.attributeValue(MessageType.TYPE)));
			// messageContentVO.setDefType(messageContentVO.getType());
		} else {
			messageContentVO.setDefType(element.attributeValue(MessageType.TYPE).toLowerCase());
			
			messageContentVO.setType(TypeUtil.parseDefTypeToRealType(messageContentVO.getDefType()));
			messageContentVO.setServerType(TypeUtil.parseDefTypeToRealServerType(messageContentVO.getDefType()));
			messageContentVO.setJavaType(TypeUtil.parseDefTypeToJavaServerType(messageContentVO.getDefType()));
		}
		// 如果是个对象类型
		if (messageContentVO.getContentType().equals(MessageType.OBJECT)) {
			// 加入对象集合中
			DataManager.getInstance().addObject(messageContentVO);
		}
		//如果是个对象集合
		if(messageContentVO.getContentType().equals(MessageType.LIST)&&messageContentVO.getHasChildren()){
			DataManager.getInstance().addObject(messageContentVO);
		}
		return messageContentVO;
	}
}
