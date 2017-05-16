package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于JDBC类型和Java类型之间的互相转换
 * 
 * @author 随心
 *
 */
public class TypeConverter {

	/**
	 * slf4j日志配置
	 */
	private static final Logger _LOG = LoggerFactory.getLogger(TypeConverter.class);

	
	/**
	 * jdbc和java类型映射表
	 */
	private static Map<String, String> typeReflectMap = null;
	
	/**
	 * java类型列表，用于定制属性界面下拉框显示所有java类型
	 */
	private static List<String> javaTypes = null;
	
	/**
	 * java数据类型映射表
	 * <pre>
	 * 用于通过java数据类型类名获取类的全路径，
	 * 或者通过类的全路径获取类名。
	 * </pre>
	 */
	private static Map<String, String> javaTypeReflectMap = null;
	
	/**
	 * 用于打表的静态代码块
	 * 
	 * mysql数据类型分为三类：数值类型、日期和时间类型、字符串类型
	 * 
	 */
	static {
		// 从配置文件中读取映射数据
		typeReflectMap = ConfigProxyReader.getTypeReflectMap();
		// 创建java类型列表
		javaTypes = new ArrayList<String>();
		// 创建java类型映射map
		javaTypeReflectMap = new HashMap<String, String>();
		// 遍历映射数据所有value，获取所有的java数据类型
		for(String value : typeReflectMap.values()) {
			// 判断类型是否已经存储过了
			if(javaTypeReflectMap.get(value) == null) {
				// 类的全路径作为ket
				javaTypeReflectMap.put(value, getClassName(value));
				// 类名作为key
				javaTypeReflectMap.put(javaTypeReflectMap.get(value), value);
				// 存储到java类型列表中
				javaTypes.add(javaTypeReflectMap.get(value));
			}
		}
	}
	
	/**
	 * jdbc类型转换成java类型类名
	 * 
	 * @param jdbcType
	 * @return
	 */
	public static String jdbcTypeToJavaTypeClassName(String jdbcType) {
		// 通过java类型映射表获取java类型的类名
		return javaTypeReflectMap.get(
					// 获取jdbc类型映射的java类型类的全路径
					typeReflectMap.get(
						// 获取jdbc和java类型映射表key
						getKey(jdbcType)));
	}
	
	/**
	 * jdbc类型转换成java类型类的全路径
	 * 
	 * @param jdbcType
	 * @return
	 */
	public static String jdbcTypeToJavaTypeQualifiedName(String jdbcType) {
		// 返回jdbc类型映射的java类型类的全路径
		return typeReflectMap.get(
					// 获取jdbc和java类型映射表key
					getKey(jdbcType));
	}
	
	/**
	 * 获取可选的java数据类型类名
	 * 
	 * @return [List<String>]java类型类名列表
	 */
	public static List<String> getJavaTypes() {
		return javaTypes;
	}
	
	/**
	 * java数据类型类名和类的全路径转换器
	 * 
	 * <pre>
	 * 传入java数据类型类名返回对应的类的全路径，
	 * 传入java数据类型类的全路径返回类名。
	 * </pre>
	 * 
	 * @param name [String]传入参数:类名、类的全路径
	 * @return [String]类的全路径、类名、传入参数不合法返回null
	 */
	public static String getClassNameOrQualifiedName(String name) {
		return javaTypeReflectMap.get(name);
	}
	
	/**
	 * 传入类的全路径，从中截取类名，出现异常则不做处理直接返回字符串
	 * @param qualifiedName [String]类的全路径
	 * @return [String]类名
	 */
	private static String getClassName(String qualifiedName) {
		try {
			// 截取类名
			return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
		} catch(Exception e) {
			// 出现异常则不做处理直接返回字符串
			return qualifiedName;
		}
	}
	
	/**
	 * 把jdbc类型字符串转换成类型映射map中的key
	 * @param jdbcType [String]jdbc类型
	 * @return [String]类型映射map中的key
	 */
	private static String getKey(String jdbcType) {
		// 创建key缓存字符串
		StringBuffer key = new StringBuffer("");
		// 转换成小写
		jdbcType = Tools.toLowerCaseLetters(jdbcType);
		// 判断是否无符号
		key.append(jdbcType.contains("unsigned") ? "unsigned." : "signed.");
		// 左括号第一次出现的位置
		int endIndex = jdbcType.indexOf("(");
		// 计算出截取字符串的终点，排除数据越界异常
		endIndex = endIndex == -1 ? jdbcType.length() : endIndex;
		// 截取key
		key.append(jdbcType.substring(0, endIndex));
		return key.toString();
	}
	
}
