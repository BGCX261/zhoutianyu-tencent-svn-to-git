package com.cyou.wg.jwgDemo.gs.app.demo.protocol.vo;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.cyou.wg.wgf.util.ByteUtil;

/**
 * name：${messageContentVO.type}.java
 * description：${messageContentVO.comment}
 */
public class ${messageContentVO.type}
{
	<#--要输出的基本属性-->
	<#list messageContentVO.children as child>
	<#switch child.contentType>
		<#case typeDef.ATTRIBUTE>
		
	/**${child.comment}*/
	public ${child.javaType} ${child.name};
	
	public void set${child.name?cap_first}(${child.javaType} ${child.name}){
		this.${child.name} = ${child.name};
	}
	
	public ${child.javaType} get${child.name?cap_first}(){
		return ${child.name};
	}
		<#break>
		<#case typeDef.OBJECT>
		
	/**${child.comment}*/
	public ${child.type} ${child.name};
	
	public void set${child.name?cap_first}(${child.type} ${child.name}){
		this.${child.name} = ${child.name};
	}

	public ${child.type} get${child.name?cap_first}(){
		return ${child.name};
	}
		<#break>
		<#case typeDef.LIST>
		<#if child.hasChildren>
		
	/**${child.comment}*/
	public ${child.type}[] ${child.name} = new ${child.type}[0];
	
	public void set${child.name?cap_first}(${child.type}[] ${child.name}){
		this.${child.name} = ${child.name};
	}

	public ${child.type}[] get${child.name?cap_first}(){
		return ${child.name};
	}
		<#else>
		
	/**${child.comment}*/
	public ${child.javaType}[] ${child.name} = new ${child.javaType}[0];
	
	public void set${child.name?cap_first}(${child.javaType}[] ${child.name}){
		this.${child.name} = ${child.name};
	}
	
	public ${child.javaType}[] get${child.name?cap_first}(){
		return ${child.name};
	}
	public ${child.type}[] get${child.name?cap_first}(){
		return ${child.name};
	}
		<#else>
		
	/**${child.comment}*/
	public ${child.javaType}[] ${child.name} = new ${child.javaType}[0];
	
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
	public void decode(ChannelBuffer src) {
		try {
			/* read data */
			<#assign var_tab_count = 3/>
			<#assign var_children = messageContentVO.children/>
			<#assign var_attributeNameTitle = 'this'/>
			<#assign var_listNameTitle = 'this'/>
			<#assign var_objectNameTitle = 'this'/>
			<#include "/ServerMessage/ReadByteArray.ftl">

		} catch (Exception e) {
			e.printStackTrace();
			throw new CyouSysException("Request Protocol VO: ${messageContentVO.type} Decode Failed");
		}
	}

	public byte[] encode() throws Exception {
		ChannelBuffer src = ChannelBuffers.dynamicBuffer(64);
		/* write data */
		<#assign var_tab_count = 2/>
		<#assign var_children = messageContentVO.children/>
		<#assign var_objectNameTitle = 'this'/>
		<#assign var_attributeNameTitle = 'this'/>
		<#assign var_listNameTitle = 'this'/>
		<#assign var_listName = 'this'/>
		<#include "/ServerMessage/WriteByteArray.ftl">

		return ByteUtil.buff2Array(src);
	}
	
}