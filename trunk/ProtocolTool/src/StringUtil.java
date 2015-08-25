public class StringUtil {
	// 将第一个字符变为大写
	public static String toUpperCaseFirstChart(String source) {
		return Character.toUpperCase(source.charAt(0)) + source.substring(1);
	}
}
