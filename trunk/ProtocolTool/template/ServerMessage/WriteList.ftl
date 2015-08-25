${""?left_pad(var_tab_count,"   ")}src.writeShort((short)${var_listName}.length);
${""?left_pad(var_tab_count,"   ")}for(int ${var_listCurrentName}Index = 0; ${var_listCurrentName}Index < ${var_listName}.length; ${var_listCurrentName}Index++){
<#assign var_tab_count = var_tab_count + 1/>
<#--引入写属性的模板-->
<#assign var_attributeDefType = var_listDefType/>
<#assign var_attributeName = var_listName + '[' + var_listCurrentName + 'Index]'/>
<#assign var_attributeLength = var_listChildLength/>
<#assign var_typeDef = var_typeDef/>
<#include "/ServerMessage/WriteAttribute.ftl">
<#assign var_tab_count = var_tab_count - 1/>
${""?left_pad(var_tab_count,"   ")}}