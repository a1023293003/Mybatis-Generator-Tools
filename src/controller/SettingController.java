package controller;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Interface.Alert;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import util.AlertUtil;
import util.Tools;

/**
 * 设置界面控制器
 * <pre>
 * · 用于统一设置一些生成配置参数
 * </pre>
 * @author 随心
 *
 */
public class SettingController extends BaseController {

	/**
	 * slf4j日志配置
	 */
	private static final Logger _LOG = LoggerFactory.getLogger(SettingController.class);

	/** 所有类名添加前缀 */
	@FXML
	private TextField addClassNamePrefix;
	
	/** 所有类名添加后缀 */
	@FXML
	private TextField addClassNamePostfix;
	
	/** 所有类名删除前缀 */
	@FXML
	private TextField delClassNamePrefix;
	
	/** 所有类名删除后缀 */
	@FXML
	private TextField delClassNamePostfix;
	
	/** 所有类名执行的正则表达式 */
	@FXML
	private TextField regexForClassName;
	
	/** 所有类名执行正则表达式的替换值 */
	@FXML
	private TextField replacementForClassName;
	/** 确认按钮 */
	@FXML
	private Button confirm;
	
	/** 取消按钮 */
	@FXML
	private Button cancel;
	
	/**
	 * 继承自接口的界面初始化方法
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 使用日志输出一下
		_LOG.info("进入【设置界面控制器】初始化方法！");
		
		// 创建dto
		setDto(new HashMap<Object, Object>(7));
		
		// 确定按钮监听事件
		this.confirm.setOnMouseClicked(event -> {
			this.loadDto();
		});
		
		// 取消按钮添加鼠标单击事件监听
		this.cancel.setOnMouseClicked(event -> {
			// 关闭窗口
			this.closeCurrStage();
		});
	}
	
	/**
	 * 获取用户输入并存入dto中,用以给父级界面返回值
	 */
	private void loadDto() {
		try {
			boolean flag = false;
			// 前后缀和合法判断以及存入dto
			if(isValidPrefix(this.addClassNamePrefix.getText(), "待添加")) {
				getDto().put("addPrefix", this.addClassNamePrefix.getText());
				flag = true;
			} else if(isValidPostfix(this.addClassNamePostfix.getText(), "待添加")) {
				getDto().put("addPostfix", this.addClassNamePostfix.getText());
				flag = true;
			} else if(isValidPrefix(this.delClassNamePrefix.getText(), "待删除")) {
				getDto().put("delPrefix", this.delClassNamePrefix.getText());
				flag = true;
			} else if(isValidPostfix(this.delClassNamePostfix.getText(), "待删除")) {
				getDto().put("delPostfix", this.delClassNamePostfix.getText());
				flag = true;
			}
			// 正则表达式
			String regex = this.regexForClassName.getText().trim();
			String replacement = this.replacementForClassName.getText().trim();
			if(Tools.isNotEmpy(regex) && Tools.isNotEmpy(replacement)) {
				getDto().put("regex", regex);
				getDto().put("replacement", replacement);
				flag = true;
			} else if(Tools.isNotEmpy(regex)) {
				AlertUtil.getWarningAlert(getCurrStage(), 
						"缺少正则替换式！");
				throw new IllegalArgumentException("缺少正则替换式！");
			} else if(Tools.isNotEmpy(replacement)) {
				AlertUtil.getWarningAlert(getCurrStage(), 
						"缺少正则表达式！");
				throw new IllegalArgumentException("缺少正则表达式！");
			}
			// 是否有返回值标记
			getDto().put("hasResult", flag);
			// 关闭窗口
			this.closeCurrStage();
		} catch (IllegalArgumentException e) {
			_LOG.error("数据输入异常！" + e.getMessage());
		}
	}
	
	/**
	 * 判断前缀是否合法
	 * <pre>
	 * 符合命名规范(字母,数字,下划线和美元符号,首字母不能为数字)
	 * </pre>
	 * @param prefix [String]待判断前缀
	 * @param info [String]报错提示信息
	 * @return
	 */
	private boolean isValidPrefix(String prefix, String info) {
		if(Tools.isNotEmpy(prefix)) {
			char c = prefix.charAt(0);
			// 符合命名规范(字母,数字,下划线和美元符号,首字母不能为数字)
			if(Tools.validNameStr(prefix) && !Tools.isNumber(c) && Tools.isUpperLetter(c)) {
				return true;
			} else {
				AlertUtil.getWarningAlert(getCurrStage(), 
						info + "前缀不符合命名规范\n(字母,数字,下划线和美元符号,首字母不能为数字)!");
				throw new IllegalArgumentException(info + "前缀不合法！");
			}
		}
		return false;
	}
	
	/**
	 * 判断后缀是否合法
	 * <pre>
	 * 符合命名规范(字母,数字,下划线和美元符号)
	 * </pre>
	 * @param postfix [String]待判断后缀
	 * @param info [String]报错提示信息
	 * @return
	 */
	private boolean isValidPostfix(String postfix, String info) {
		if(Tools.isNotEmpy(postfix)) {
			// 符合命名规范(字母,数字,下划线和美元符号)
			if(Tools.validNameStr(postfix)) {
				return true;
			} else {
				AlertUtil.getWarningAlert(getCurrStage(), 
						info + "后缀不符合命名规范\n(字母,数字,下划线和美元符号)!");
				throw new IllegalArgumentException(info + "后缀不合法！");
			}
		}
		return false;
	}
	
}
