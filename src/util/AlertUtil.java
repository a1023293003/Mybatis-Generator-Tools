package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Interface.Alert;
import controller.BaseController;
import javafx.stage.Stage;

/**
 * 提示框工具类
 * 
 * 用于简化提示框操作代码，便于其它类使用
 * 
 * @author 随心
 *
 */
public class AlertUtil {

	/**
	 * slf4j日志配置
	 */
	private static final Logger _LOG = LoggerFactory.getLogger(AlertUtil.class);
	
	/**
	 * 提示框标题
	 */
	private static String title = null;
	
	/**
	 * 提示框实现界面默认路径
	 */
	private static String defaultFxmlPath = null;
	
	static {
		// 从配置文件中读取默认路径
		defaultFxmlPath = ConfigProxyReader.getDefaultAlertFxmlPath();
		// 从配置文件中读取默认标题
		title = ConfigProxyReader.getAlertTitle();
	}
	
	/**
	 * 弹出警告提示框
	 * @param parentsStage [Stage]父类窗口层
	 * @param text [String]提示内容
	 * @return [Alert]提示框层的控制器
	 */
	public static Alert getWarningAlert(Stage parentsStage, String text) {
		return createAlertWithStyle(parentsStage, text, Alert.CONFIRM, Alert.WARNING);
	}
	
	/**
	 * 弹出询问提示框
	 * @param parentsStage [Stage]父类窗口层
	 * @param text [String]提示内容
	 * @return [Alert]提示框层的控制器
	 */
	public static Alert getAskAlert(Stage parentsStage, String text) {
		return createAlertWithStyle(parentsStage, text, Alert.CONFIRM_CANCEL, Alert.ASK);
	}
	
	/**
	 * 弹出等待提示框
	 * @param parentsStage [Stage]父类窗口层
	 * @param text [String]提示内容
	 * @return [Alert]提示框层的控制器
	 */
	public static Alert getLoadingAlert(Stage parentsStage, String text) {
		return createAlertWithStyle(parentsStage, text, Alert.NONE, Alert.LOADING);
	}
	
	/**
	 * 弹出OK提示框
	 * @param parentsStage [Stage]父类窗口层
	 * @param text [String]提示内容
	 * @return [Alert]提示框层的控制器
	 */
	public static Alert getOkAlert(Stage parentsStage, String text) {
		return createAlertWithStyle(parentsStage, text, Alert.CONFIRM, Alert.OK);
	}
	
	/**
	 * 创建一个提示框
	 * @param parentsStage [Stage]父类窗口层
	 * @param text [String]提示内容
	 * @param buttonStyle [integer]按钮模式
	 * @param imageStyle [Integer]图片模式
	 * @return [Alert]提示框层的控制器
	 */
	public static Alert createAlertWithStyle(Stage parentsStage, String text, 
			Integer buttonStyle, Integer imageStyle) {
		// 新建一个提示窗口
		Alert alert = createAlert(parentsStage, text);
		// 设置提示框图片类型和按钮类型
		alert.setAlertStyle(text, buttonStyle, imageStyle);
		return alert;
	}
	
	/**
	 * 创建一个提示窗口并返回窗口的控制器(没有设置按钮类型和图片类型)
	 * @param parentsStage [Stage]父类窗口层
	 * @param text [String]提示内容
	 * @return [Alert]提示框层的控制器
	 */
	public static Alert createAlert(Stage parentsStage, String text) {
		return (Alert) new BaseController().createDialog(parentsStage, title, defaultFxmlPath);
	}
	
}
