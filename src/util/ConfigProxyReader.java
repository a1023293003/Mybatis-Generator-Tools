package util;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置信息代理读取器
 * 
 * 用于统一管理配置信息的读取
 * 
 * @author 随心
 *
 */
public class ConfigProxyReader {

	/**
	 * 读取数据库类型
	 * @return
	 */
	public static List<String> getSqlTypes() {
		return getListValue("connection.sqlTypes", ",");
	}
	
	/**
	 * 读取编码类型
	 * @return
	 */
	public static List<String> getCodes() {
		return getListValue("connection.codes", ",");
	}
	
	/**
	 * MySQL默认IP
	 * @return
	 */
	public static String getMysqlDefaultIP() {
		return ConfigParser.getConfigParser().getValue("mysql.ip");
	}
	
	/**
	 * MySQL默认端口
	 * @return
	 */
	public static String getMySQLDefaultPort() {
		return ConfigParser.getConfigParser().getValue("mysql.port");
	}
	
	/**
	 * MySQL默认用户名
	 * @return
	 */
	public static String getMySQLDefaultUserName() {
		return ConfigParser.getConfigParser().getValue("mysql.userName");
	}
	
	/**
	 * 读取配置文件中指定键的值，并指定分隔符，把结果分割成list
	 * @param key [String]键
	 * @param separator [String]分隔符
	 * @return [List<String>]转成List的结果集合
	 */
	private static List<String> getListValue(String key, String separator) {
		// 读取配置信息，并分割成字符串数组
		String[] values = ConfigParser.getConfigParser().getValue(key).split(separator);
		// 字符串数组转换成List并返回
		List<String> listValues = new ArrayList<String>(values.length);
		for(String value : values) {
			listValues.add(value);
		}
		return listValues;
	}
}
