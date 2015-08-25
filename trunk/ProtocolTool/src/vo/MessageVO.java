package vo;

import java.util.ArrayList;
import java.util.List;

public class MessageVO {
    private int messageTypeID;
    private int id;
    private String type;
    private String name;
    private String comment;
    private String handler;
    private String handlerPackage;
    private int isAutoCreate = 1;
    private List<MessageContentVO> children = new ArrayList<MessageContentVO>();
	
    public int getMessageTypeID() {
		return messageTypeID;
	}
	public void setMessageTypeID(int messageTypeID) {
		this.messageTypeID = messageTypeID;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getHandler() {
		return handler;
	}
	public void setHandler(String handler) {
		this.handler = handler;
	}
	public String getHandlerPackage() {
		return handlerPackage;
	}
	public void setHandlerPackage(String handlerPackage) {
		this.handlerPackage = handlerPackage;
	}
	public int getIsAutoCreate() {
		return isAutoCreate;
	}
	public void setIsAutoCreate(int isAutoCreate) {
		this.isAutoCreate = isAutoCreate;
	}
	public List<MessageContentVO> getChildren() {
		return children;
	}
	public void setChildren(List<MessageContentVO> children) {
		this.children = children;
	}

    
}