package util;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Interface.Alert;
import controller.AlertPaneController;
import dbAction.MySQLAction;
import javafx.stage.Stage;

/**
 * 工具类
 * 
 * 为其他类提供一些静态方法
 * 
 * @author 随心
 *
 */
public class Tools {
	
	/**
	 * slf4j日志配置
	 */
	private static final Logger _LOG = LoggerFactory.getLogger(Tools.class);

	
	/**
	 * 判断数据是否为空
	 * @param data [<T>]待判断数据
	 * @return [<T>]data待判断数据
	 * @throws Exception 空指针异常
	 */
	public static <T> T isNull(T data) throws Exception {
		if(data == null) throw  new NullPointerException("数据为NULL");
		return data;
	}
	
	/**
	 * 异常统一处理方法
	 * 
	 * 分析异常类型，并弹出提示框
	 * @param alert [Alert]提示框控制器
	 * @param e [Exception]异常
	 */
	public static void exceptionAction(Alert alert, Exception e) {
		if(alert == null || e == null) return;
		if(e instanceof NullPointerException) {
			_LOG.error("空指针异常！");
			alert.setAlertStyle(
					"空指针异常！\n" + e.getMessage(), 
					AlertPaneController.CONFIRM, 
					AlertPaneController.WARNING
			);
		} else if(e instanceof SQLException) {
			_LOG.error("数据库操作异常:{}", e.getMessage());
			alert.setAlertStyle(
					"数据库操作异常！\n" + e.getMessage(), 
					AlertPaneController.CONFIRM, 
					AlertPaneController.WARNING
			);
		} else {
			_LOG.warn("未知错误！");
			alert.setAlertStyle(
					"未知错误！\n" + e.getMessage(), 
					AlertPaneController.CONFIRM, 
					AlertPaneController.WARNING
			);
		}
		e.printStackTrace();
	}
	
	/**
	 * 通过传入的数据库字段名获取映射的属性名
	 * <pre>
	 * 把字段名中的'_'去掉，并让'_'字符后一个字母大写
	 * </pre>
	 * @see controller.NewConnectionController#loadDto(MySQLAction)
	 * @see controller.MainFrameController#readConfigFromDto(String, Map)
	 * @param field [String]字段名
	 * @return [String]属性名
	 */
	public static String removeUnderlineAndcapitalNextChar(String field) {
		if(field == null) throw new RuntimeException();
		char[] s = field.toCharArray();
		int underlineNum = 0;
		for(int i = 0; i < s.length; i ++) {
			if(s[i] == '_') {
				underlineNum ++;
			}
		}
		if(underlineNum > 0) {
			char[] res = new char[s.length - underlineNum];
			for(int i = 0, k = 0; i < s.length; i ++) {
				if(s[i] == '_') {
					int tmp = i + 1;
					if(tmp < s.length && s[tmp] >= 97 && s[tmp] <= 122) {
						res[k ++] = (char) (s[++ i] - 32);
					}
				} else {
					res[k ++] = s[i];
				}
			}
			return String.valueOf(res);
		} else {
			return String.valueOf(s);
		}
	}
	
	/**
	 * 首字母大写
	 * @param str [String]待转换字符串
	 * @return [String]首字母大写之后的字符串
	 */
	public static String capitalFirstChar(String str) {
		if(str == null) return null;
		char[] s = str.toCharArray();
		// 首字母是小写字母
		if(s[0] >= 97 && s[0] <= 123) {
			// s[0] -= 32;这里可以用位运算代替
			s[0] ^= 32;
		}
		return String.valueOf(s);
	}
	
	/**
	 * 把传入字符串中所有大写字母转换成小写字母
	 * @param str [String]传入字符串
	 * @return [String]转换后的字符串
	 */
	public static String toLowerCaseLetters(String str) {
		if(str == null) return null;
		char[] s = str.toCharArray();
		// 把所有大写字母转换成小写字母
		for(int i = 0; i < s.length; i ++) {
			// a - z : 97 - 122、 A - Z : 65 - 90
			if(s[i] >= 65 && s[i] <= 90) {
				s[i] += 32;
			}
		}
		return String.valueOf(s);
	}
	
	/**
	 * 把传入字符串中所有小写字母转换成大写字母
	 * @param str [String]传入字符串
	 * @return [String]转换后的字符串
	 */
	public static String toUpperCaseLetters(String str) {
		if(str == null) return null;
		char[] s = str.toCharArray();
		// 把所有大写字母转换成小写字母
		for(int i = 0; i < s.length; i ++) {
			// a - z : 97 - 122、 A - Z : 65 - 90
			if(s[i] >= 97 && s[i] <= 122) {
				s[i] -= 32;
			}
		}
		return String.valueOf(s);
	}
	
	/**
	 * 判断字符串内容的有效性
	 * 
	 * <pre>
	 * 字符串不为空，且不全为空格。
	 * </pre>
	 * 
	 * @param str [string]待判断字符串
	 * @return [boolean]true : 有效、false : 无效
	 */
	public static boolean isValid(String str) {
		return !(str == null || str.trim().length() <= 0);
	}
	
	/**
	 * 用于监测某个对象种指定属性的数据是否有效。
	 * 判断数据是否有效，数据不可为空，也不可以全为空格
	 * 
	 * <pre>
	 * 在主界面控制器中调用，用于检查各个表的配置信息是否完整。
	 * </pre>
	 * 
	 * @param table [String]数据所在表
	 * @param datas [String[]]待判断数据名
	 * @param describes [String[]]数据描述
	 * @param entity [Object]数据所在对象
	 * @return [Boolean]数据有效返回true、数据无效返回false
	 */
	public static boolean checkDatasValidity(Stage parentsStage, String table, String[] datas, String[] describes, Object entity) {
		try {
			// 遍历数据进行检测
			for(int i = 0; i < datas.length; i ++) {
				// 通过反射从对象中取出数据
				Field data = entity.getClass().getDeclaredField(datas[i]);
				// 因为只是获取对应属性值，所以开启java访问检测，不设置该值的话会抛出异常java.lang.IllegalAccessException
				data.setAccessible(true);
				// 检测数据合法性
				if(!dataIsValid(parentsStage, table, (String) data.get(entity), describes[i])) return false;
			}
			return true;
		} catch(Exception e) {
			_LOG.error("检测数据合法性出现异常！");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 用于监测某个对象种指定属性的数据是否有效。
	 * 判断数据是否有效，数据不可为空，也不可以全为空格
	 * 
	 * @param table [String]数据所在表
	 * @param data [String]待判断数据
	 * @param describe [String]数据描述
	 * @return [Boolean]数据有效返回true、数据无效返回false
	 */
	private static boolean dataIsValid(Stage parentsStage, String table, String data, String describe) {
		// 数据不可为空，也不能全为空格
		if(data == null || data.trim().length() <= 0) {
			AlertUtil.getWarningAlert(
					parentsStage, 
					table + "表中" + describe + "不能为空或全为空格！"
			);
			return false;
		}
		return true;
	}
	
	/**
	 * 正则匹配模板
	 * @param str 待匹配内容
	 * @param patternStr 匹配规则
	 * @return 匹配成功返回匹配结果，否则返回""
	 */
	public static ArrayList<String> RegexString(String str, String patternStr) {
		// 预定义一个ArrayList来存储结果
		ArrayList<String> results = new ArrayList<String>();
		// 定义正则匹配规则
		Pattern pattern = Pattern.compile(patternStr);
		// 定义一个matcher来做匹配
		Matcher matcher = pattern.matcher(str);
		// 如果找到了
		boolean isFind = matcher.find();
		// 使用循环将句子里符合规则的子序列截取出来，存储到results里
		while(isFind) {
			results.add(matcher.group(1));
			// 更新标记
			isFind = matcher.find();
		}
		// 返回找到的结果
		return results;
	}

	
}
