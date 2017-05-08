package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	/**
	 * 类型映射配置文件
	 */
	private static ConfigParser TypeReflectConfig = null;
	
	/**
	 * 类型映射配置文件
	 */
	private static ConfigParser CodeDetailsConfig = null;
	
	static {
		try {
			// 读取默认配置文件
			BaseConfig = new ConfigParser("config.properties");
			// 读取页面配置文件
			PageConfig = new ConfigParser(BaseConfig.getValue("page.configPath"));
			// 读取mysql类型映射配置文件
			TypeReflectConfig = new ConfigParser(BaseConfig.getValue("type.configPath"));
			// 读取代码生成细则配置文件路径
			CodeDetailsConfig = new ConfigParser(BaseConfig.getValue("code.configPath"));
//			CodeDetailsConfig = new ConfigParser("codeDetails.properties");
		} catch (Exception e) {
			_LOG.error("配置文件读取失败！");
			e.printStackTrace();
			// 退出应用程序
			Platform.exit();
			
		}
	}
	
	/**
	 * 获取mysql类型映射配置文件中所有键值对
	 * 
	 * @return [Map<String, String>]mysql类型映射配置文件中所有键值对
	 */
	public static Map<String, String> getTypeReflectMap() {
		return TypeReflectConfig.getAllKeyValues();
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
	 * 获取定制属性页面表视图显示中所有列对齐方式
	 * @return
	 */
	public static String getCustomizedTableFieldsTableColumnAlignment() {
		return BaseConfig.getValue("customizedTableFields.tableColumnAlignment");
	}
	
	/**
	 * 获取定制属性界面表示图数据行高度
	 * @return
	 */
	public static Double getCustomizedTableFieldsFixedCellSize() {
		try {
			return Double.parseDouble(BaseConfig.getValue("customizedTableFields.fixedCellSize"));
		} catch (Exception e) {
			return -1.0;
		}
	}
	
	/**
	 * 获取主界面标题
	 * @return
	 */
	public static String getMainFrameTitle() {
		return BaseConfig.getValue("mainFrame.title");
	}
	
	/**
	 * 获取新建连接界面标题
	 * @return
	 */
	public static String getNewConnectionTitle() {
		return BaseConfig.getValue("newConnection.title");
	}
	
	/**
	 * 获取定制属性界面标题
	 * @return
	 */
	public static String getCustomizedTableFieldsTitle() {
		return BaseConfig.getValue("customizedTableFields.title");
	}
	
	/**
	 * 获取提示框界面标题
	 * @return
	 */
	public static String getAlertTitle() {
		return BaseConfig.getValue("alert.title");
	}
	
	/**
	 * 读取默认提示框fxml路径
	 * @return
	 */
	public static String getDefaultAlertFxmlPath() {
		return PageConfig.getValue("defaultAlert.fxmlPath");
	}
	
	/**
	 * 读取默认主界面fxml路径
	 * @return
	 */
	public static String getDefaultMainFrameFxmlPath() {
		return PageConfig.getValue("defaultMainFrame.fxmlPath");
	}
	
	/**
	 * 读取默认新建连接界面fxml路径
	 * @return
	 */
	public static String getDefaultNewConnectionFxmlPath() {
		return PageConfig.getValue("defaultNewConnection.fxmlPath");
	}
	
	/**
	 * 读取默认定制属性界面fxml路径
	 * @return
	 */
	public static String getDefaultCustomizedTableFieldsFxmlPath() {
		return PageConfig.getValue("defaultCustomizedTableFields.fxmlPath");
	}
	
	/**
	 * TODO 关于使用list和数组的优化
	 * 读取配置文件中mapper类中的方法和注释
	 * @return [String[][]]返回方法字符串数组和注释字符串数组组成的二维数组
	 */
	public static String[][] getMapperMethodsAndComments() {
		// 返回结果
		String[][] result = new String[2][];
		// 读取配置文件中所有键值对
		Map<String, String> keyValues = CodeDetailsConfig.getAllKeyValues();
		// 方法和注释存储数组
		String[] methods = new String[keyValues.size()];
		String[] comments = new String[keyValues.size()];
		// 数组下标
		int index = 0;
		// 遍历map，提取出mapper的方法和注释
		for(Entry<String, String> entry : keyValues.entrySet()) {
			if(entry.getKey().startsWith("mapper.")) {
				// 存储方法
				methods[index] = entry.getValue();
				// 取出方法之上的注释
				String[] commentsBuf = CodeDetailsConfig.getComment(entry.getKey()).split("\n");
				// 存储最后一条注释
				comments[index] = commentsBuf.length > 0 ? commentsBuf[commentsBuf.length - 1] : "";
				index ++;
			}
		}
		// 存储到返回结果中
		result[0] = methods;
		result[1] = comments;
		return result;
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
