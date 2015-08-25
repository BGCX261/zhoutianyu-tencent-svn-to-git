${""?left_pad(var_tab_count,"	")}short ${var_listCurrentName}Length = src.readShort();
${""?left_pad(var_tab_count,"	")}${var_listName} = new ${var_listType}[${var_listCurrentName}Length];
${""?left_pad(var_tab_count,"	")}for(int ${var_listCurrentName}Index = 0; ${var_listCurrentName}Index < ${var_listCurrentName}Length; ${var_listCurrentName}Index++){
<#assign var_tab_count = var_tab_count + 1/>
${""?left_pad(var_tab_count,"	")}${var_listType} temp${var_listCurrentName}Item = new ${var_listType}();
${""?left_pad(var_tab_count,"	")}temp${var_listCurrentName}Item.decode(src);
${""?left_pad(var_tab_count,"	")}${var_listName}[${var_listCurrentName}Index] = temp${var_listCurrentName}Item;
<#assign var_tab_count = var_tab_count - 1/>
${""?left_pad(var_tab_count,"	")}}