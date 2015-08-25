import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import vo.MessageContentVO;
import vo.MessageVO;

public class DataManager {

	private static DataManager instance;
	
	private List<MessageVO> messages = new LinkedList<MessageVO>();
	
	private List<MessageVO> sendMessages = new LinkedList<MessageVO>();
	
	private List<MessageVO> backMessages = new LinkedList<MessageVO>();

	private List<MessageContentVO> objects = new ArrayList<MessageContentVO>();
	
	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}
	
	public void addObject(MessageContentVO messageContentVO){
		Boolean value=false;
		for(int i=0 ; i<this.objects.size() ; i++){
			MessageContentVO oldMessageContentVO=this.objects.get(i);
			if(oldMessageContentVO.getType().equals(messageContentVO.getType())){
				if(oldMessageContentVO.getHasChildren()&&messageContentVO.getHasChildren()){
					if(messageContentVO.getChildren().size()>oldMessageContentVO.getChildren().size()){
						oldMessageContentVO.setName(messageContentVO.getName());
						oldMessageContentVO.setLength(messageContentVO.getLength());
						oldMessageContentVO.setComment(messageContentVO.getComment());
						oldMessageContentVO.setChildren(messageContentVO.getChildren());
					}
					value=true;
					break;
				}
			}
		}
	
		if(!value){
			MessageContentVO newMessageContentVO=new MessageContentVO();
			newMessageContentVO.setContentType(messageContentVO.getContentType());
			newMessageContentVO.setName(messageContentVO.getName());
			newMessageContentVO.setType(messageContentVO.getType());
			newMessageContentVO.setDefType(messageContentVO.getDefType());
			newMessageContentVO.setServerType(messageContentVO.getServerType());
			newMessageContentVO.setJavaType(messageContentVO.getJavaType());
			newMessageContentVO.setLength(messageContentVO.getLength());
			newMessageContentVO.setComment(messageContentVO.getComment());
			newMessageContentVO.setHasChildren(messageContentVO.getHasChildren());
			newMessageContentVO.setChildren(messageContentVO.getChildren());
			this.objects.add(newMessageContentVO);
		}
	}

	public List<MessageVO> getMessages() {
		return messages;
	}

	public void setMessages(List<MessageVO> messages) {
		this.messages = messages;
	}

	public List<MessageVO> getSendMessages() {
		return sendMessages;
	}

	public void setSendMessages(List<MessageVO> sendMessages) {
		this.sendMessages = sendMessages;
	}

	public List<MessageVO> getBackMessages() {
		return backMessages;
	}

	public void setBackMessages(List<MessageVO> backMessages) {
		this.backMessages = backMessages;
	}

	public List<MessageContentVO> getObjects() {
		return objects;
	}

	public void setObjects(List<MessageContentVO> objects) {
		this.objects = objects;
	}
}
