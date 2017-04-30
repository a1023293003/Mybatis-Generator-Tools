package util;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Interface.Alert;
import controller.AlertPaneController;
import dbAction.MySQLAction;

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
	
}
