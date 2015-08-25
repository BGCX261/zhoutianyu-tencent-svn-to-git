<#list var_children as child>
	<#switch child.contentType>
	<#case typeDef.ATTRIBUTE>
	<#--引入写属性的模板-->
	<#assign var_attributeDefType = child.defType/>
	<#assign var_attributeNameTitle = var_attributeNameTitle/>
	<#assign var_attributeName = var_attributeNameTitle + '.' + child.name/>
	<#assign var_attributeLength = child.length/>
	<#assign var_typeDef = typeDef/>
	<#include "/ServerMessage/ReadAttribute.ftl">
	<#break>
	<#case typeDef.OBJECT>
	<#--引入写对象的模板,这里就是自身-->
	<#assign var_children = child.children/>
	<#assign var_objectNameTitle = var_objectNameTitle/>
	<#assign var_objectName = var_objectNameTitle + '.' + child.name/>
	${""?left_pad(var_tab_count,"	")}${var_objectName}.decode(src);
	<#break>
	<#case typeDef.LIST>
	<#if child.hasChildren>
	<#--引入写对象集合的模板-->
	<#assign var_list = child/>
	<#assign var_listNameTitle = var_listNameTitle/>
	<#assign var_listName = var_listNameTitle + "." + child.name/>
	<#assign var_listCurrentName = child.name/>
	<#assign var_listType = child.type/>
	<#include "/ServerMessage/ReadObjectList.ftl">
	<#else>
	<#--引入写普通集合的模板-->
	<#assign var_listNameTitle = var_listNameTitle/>
	<#assign var_listName = var_listNameTitle + "." + child.name/>
	<#assign var_listCurrentName = child.name/>
	<#assign var_listDefType = child.defType/>
	<#assign var_listType = child.javaType/>
	<#assign var_typeDef = typeDef/>
	<#include "/ServerMessage/ReadList.ftl">
	<#break>
	</#if>
	</#switch>
</#list>