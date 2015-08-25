<#if messageVO.messageTypeID == 1>package com.cyou.wg.jwgDemo.protocol.request;<#else>package com.cyou.wg.jwgDemo.protocol.response;</#if>

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.cyou.wg.wgf.exception.CyouSysException;
import com.cyou.wg.wgf.protocol.base.BaseRequestProtocol;
import com.cyou.wg.wgf.protocol.base.response.IResponseProtocol;
import com.cyou.wg.wgf.protocol.base.BaseResponseProtocol;
import com.cyou.wg.wgf.util.ByteUtil;

import com.cyou.wg.jwgDemo.protocol.vo.*;

/**
 * name：${messageVO.name}.java
 * description：${messageVO.comment}
 */
public class ${messageVO.name} extends <#if messageVO.messageTypeID == 1>BaseRequestProtocol<#else>BaseResponseProtocol</#if>{
	<#if messageVO.messageTypeID == 1>
	
	private final static String handler = "${messageVO.handler}Handler";
	</#if>
	<#--要输出的基本属性-->
	<#list messageVO.children as child>
	<#switch child.contentType>
		<#case typeDef.ATTRIBUTE>
		
	/** ${child.comment} */
	private ${child.javaType} ${child.name};
	
	public void set${child.name?cap_first}(${child.javaType} ${child.name}){
		this.${child.name} = ${child.name};
	}
	
	public ${child.javaType} get${child.name?cap_first}(){
		return ${child.name};
	}
		<#break>
		<#case typeDef.OBJECT>
		
	/** ${child.comment} */
	private ${child.type} ${child.name};
	
	public void set${child.name?cap_first}(${child.type} ${child.name}){
		this.${child.name} = ${child.name};
	}
	
	public ${child.type} get${child.name?cap_first}(){
		return ${child.name};
	}
		<#break>
		<#case typeDef.LIST>
		<#if child.hasChildren>
		
	/** ${child.comment} */
	private ${child.type}[] ${child.name} = new ${child.type}[0];
	
	public void set${child.name?cap_first}(${child.type}[] ${child.name}){
		this.${child.name} = ${child.name};
	}
	
	public ${child.type}[] get${child.name?cap_first}(){
		return ${child.name};
	}

		<#else>

	/** ${child.comment} */
	private ${child.javaType}[] ${child.name} = new ${child.javaType}[0];
	
	public void set${child.name?cap_first}(${child.javaType}[] ${child.name}){
		this.${child.name} = ${child.name};
	}
	
	public ${child.javaType}[] get${child.name?cap_first}(){
		return ${child.name};
	}
		</#if>
		<#break>
	</#switch>
	</#list>
	
	<#if messageVO.messageTypeID == 1>
	
	@Override
	public void decode(ChannelBuffer src) {
		try {
			/* read data */
			<#assign var_tab_count = 3/>
			<#assign var_children = messageVO.children/>
			<#assign var_attributeNameTitle = 'this'/>
			<#assign var_listNameTitle = 'this'/>
			<#assign var_objectNameTitle = 'this'/>
			<#include "/ServerMessage/ReadByteArray.ftl">		
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new CyouSysException("Send Message Wrong: Protocol " + this.getProtocolId() + " Decode Failed");
		}
	}
	</#if>
	
	public short getProtocolId() {
	    return ${messageVO.id?string.computer};
	}
	<#if messageVO.messageTypeID == 2>
	
	@Override
	public byte[] encode() throws Exception {
		ChannelBuffer src = ChannelBuffers.dynamicBuffer(64);
		src.writeShort(super.getProtocolId());
		
		/* write data*/
		<#assign var_tab_count = 2/>
		<#assign var_children = messageVO.children/>
		<#assign var_objectNameTitle = 'this'/>
		<#assign var_attributeNameTitle = 'this'/>
		<#assign var_listNameTitle = 'this'/>
		<#assign var_listName = 'this'/>
		<#include "/ServerMessage/WriteByteArray.ftl">

		return ByteUtil.buff2Array(src);
	}
	</#if>
	<#if messageVO.messageTypeID == 1>
	
	public IResponseProtocol execute() {
		return super.execute(this,handler);
	}
	</#if>
	<#if messageVO.messageTypeID == 1>
	
	@Override
	public BaseRequestProtocol newInstance() {
		return new ${messageVO.name}();
	}
	</#if>
	
}