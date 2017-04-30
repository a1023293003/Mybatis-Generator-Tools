package util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Interface.Alert;
import javafx.application.Platform;
import javafx.stage.Stage;

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
	 * slf4j日志配置
	 */
	private static final Logger _LOG = LoggerFactory.getLogger(ConfigProxyReader.class);

	
	/**
	 * 默认配置文件（基础配置文件）
	 */
	private static ConfigParser BaseConfig = null;
	
	/**
	 * 页面信息配置文件
	 */
	private static ConfigParser PageConfig = null;
	
	static {
		try {
			// 读取默认配置文件
			BaseConfig = new ConfigParser("config.properties");
			// 读取页面配置文件
			PageConfig = new ConfigParser(BaseConfig.getValue("page.configPath"));
		} catch (Exception e) {
			_LOG.error("配置文件读取失败！");
			e.printStackTrace();
			// 退出应用程序
			Platform.exit();
			
		}
	}
	
	/**
	 * 读取数据库类型
	 * @return
	 */
	public static List<String> getSqlTypes() {
		return getListValue(BaseConfig, "connection.sqlTypes", ",");
	}
	
	/**
	 * 读取编码类型
	 * @return
	 */
	public static List<String> getCodes() {
		return getListValue(BaseConfig, "connection.codes", ",");
	}
	
	/**
	 * MySQL默认IP
	 * @return
	 */
	public static String getMysqlDefaultIP() {
		return BaseConfig.getValue("mysql.ip");
	}
	
	/**
	 * MySQL默认端口
	 * @return
	 */
	public static String getMySQLDefaultPort() {
		return BaseConfig.getValue("mysql.port");
	}
	
	/**
	 * MySQL默认用户名
	 * @return
	 */
	public static String getMySQLDefaultUserName() {
		return BaseConfig.getValue("mysql.userName");
	}
	
	/**
	 * 读取默认提示框fxml路径
	 * @return
	 */
	public static String getDefaultFxmlPath() {
		return PageConfig.getValue("defaultAlert.fxmlPath");
	}
	
	/**
	 * 读取配置文件中指定键的值，并指定分隔符，把结果分割成list
	 * @param config [ConfigParser]配置文件拓展类对象
	 * @param key [String]键
	 * @param separator [String]分隔符
	 * @return [List<String>]转成List的结果集合
	 */
	private static List<String> getListValue(ConfigParser config, String key, String separator) {
		// 读取配置信息，并分割成字符串数组
		String[] values = config.getValue(key).split(separator);
		// 字符串数组转换成List并返回
		List<String> listValues = new ArrayList<String>(values.length);
		for(String value : values) {
			listValues.add(value);
		}
		return listValues;
	}
}
