<#assign var_tab_count = var_tab_count/>
<#switch var_attributeDefType>
<#case var_typeDef.NUMBER>
${""?left_pad(var_tab_count,"	")}src.writeDouble(${var_attributeName});
<#break>
<#case var_typeDef.INT>
${""?left_pad(var_tab_count,"	")}src.writeInt(${var_attributeName});
<#break>
<#case var_typeDef.SHORT>
${""?left_pad(var_tab_count,"	")}src.writeShort(${var_attributeName});
<#break>
<#case var_typeDef.STRING>
${""?left_pad(var_tab_count,"	")}ByteUtil.putString2Buff(src, ${var_attributeName});
<#break>
<#case var_typeDef.FLOAT>
${""?left_pad(var_tab_count,"	")}src.writeFloat(${var_attributeName});
<#break>
<#case var_typeDef.BYTE>
${""?left_pad(var_tab_count,"	")}src.writeByte(${var_attributeName});
<#break>
<#case var_typeDef.LONG>
${""?left_pad(var_tab_count,"	")}src.writeLong(${var_attributeName});
<#break>
</#switch>