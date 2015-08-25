${""?left_pad(var_tab_count,"	")}src.writeShort((short)${var_listName}.length);
${""?left_pad(var_tab_count,"	")}for(int ${var_listCurrentName}Index = 0; ${var_listCurrentName}Index < ${var_listName}.length; ${var_listCurrentName}Index++){
<#assign var_tab_count = var_tab_count + 1/>
<#--引入写数据的模板-->
<#assign var_objectTitle = var_listName + '[' + var_listCurrentName + 'Index]'/>
${""?left_pad(var_tab_count,"	")}src.writeBytes(${var_objectTitle}.encode());
<#assign var_tab_count = var_tab_count - 1/>
${""?left_pad(var_tab_count,"	")}}