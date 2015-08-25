
public class TypeUtil {
	//	用户输入的类型和实际类型的转换 比如属于 Short 那么Short小写化之后转换int
	public static String parseDefTypeToRealType(String defType) {
		String str = "";
		String def = defType;
		TypeDef td = new TypeDef();
		if(def.equals(td.NUMBER)) {
			str = "Number";
		} else if (def.equals(td.INT)) {
			str = "int";
		} else if (def.equals(td.SHORT)) {
			str = "int";
		} else if (def.equals(td.STRING)) {
			str = "String";
		} else if (def.equals(td.FLOAT)) {
			str = "Number";
		} else if (def.equals(td.BYTE)) {
			str = "int";
		} else if (def.equals(td.LONG)) {
			str = "String";
		} 
		if(str == "") {
			try {
				throw new Exception("基本类型填入错误：" + defType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return str;
	}
	
	//	用户输入的类型和服务器中实际类型的转换 比如 属于short 那么把short小写化之后转换为short
	public static String parseDefTypeToRealServerType(String defType) {
		String str = "";
		String def = defType;
		TypeDef td = new TypeDef();
		if(def.equals(td.NUMBER)) {
			str = "Number";
		} else if (def.equals(td.INT)) {
			str = "int";
		} else if (def.equals(td.SHORT)) {
			str = "short";
		} else if (def.equals(td.STRING)) {
			str = "char";
		} else if (def.equals(td.FLOAT)) {
			str = "float";
		} else if (def.equals(td.BYTE)) {
			str = "byte";
		} else if (def.equals(td.LONG)) {
			str = "long";
		} 
		if(str == "") {
			try {
				throw new Exception("基本类型填入错误：" + defType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return str;
	}
	
	//	用户输入的类型和java服务器中实际类型的转换 比如 属于short 那么把short小写化之后转换为short
	public static String parseDefTypeToJavaServerType(String defType) {
		String str = "";
		String def = defType;
		TypeDef td = new TypeDef();
		if(def.equals(td.NUMBER)) {
			str = "Number";
		} else if (def.equals(td.INT)) {
			str = "int";
		} else if (def.equals(td.SHORT)) {
			str = "short";
		} else if (def.equals(td.STRING)) {
			str = "string";
		} else if (def.equals(td.FLOAT)) {
			str = "float";
		} else if (def.equals(td.BYTE)) {
			str = "byte";
		} else if (def.equals(td.LONG)) {
			str = "long";
		} 
		if(str == "") {
			try {
				throw new Exception("基本类型填入错误：" + defType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return str;
	}
}
