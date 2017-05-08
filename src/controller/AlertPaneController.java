package controller;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Interface.Alert;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import util.ImageGenerator;

/**
 * 提示窗口控制器
 * 
 * 可以弹出提示给用户，最多提供两个选择按钮(确认、取消)，
 * 
 * 会根据用户选择把值存储在dto中
 * key是"userChoice"，
 * value是Boolean，true：用户点击确认、false：用户点击取消、dto值为null表示无返回值
 * 
 * @author 随心
 *
 */
public class AlertPaneController extends BaseController implements Alert {
	
	/**
	 * slf4j日志配置
	 */
	private static final Logger _LOG = LoggerFactory.getLogger(AlertPaneController.class);
	
	/**
	 * 提示信息标签
	 */
	@FXML
	private Label promptLabel;
	
	/**
	 * 取消按钮
	 */
	@FXML
	private Button cancel;
	
	/**
	 * 确定按钮
	 */
	@FXML
	private Button confirm;
	
	/**
	 * 容器面板
	 */
	@FXML
	private AnchorPane container;
	
	/**
	 * 设置弹出提示框信息内容
	 * @param message [String]提示信息
	 */
	public void setText(String message) {
		this.promptLabel.setText(message != null ? message : "");
	}
	
	/**
	 * 设置图片
	 * @param style [Integer]类型
	 */
	public void setImage(Integer style) {
		if(style == null) return;
		this.promptLabel.setGraphic(imageStyle[style]);
	}
	
	/**
	 * 设置按钮模式
	 * @param style [Integer]类型
	 */
	public void setButtonStyle(Integer style) {
		if(style == null) return;
		// 设置两个按钮的是否可见属性
		this.confirm.setVisible(this.buttonStyle[style][0]);
		this.cancel.setVisible(this.buttonStyle[style][1]);
		// 设置按钮位置
		if(this.buttonStyle[style][2] != null && 
				this.confirm.getLayoutX() < this.cancel.getLayoutX() != this.buttonStyle[style][2]) {
			// 交换两个按钮的位置
			Double confirmX = this.confirm.getLayoutX();
			Double cancelX = this.cancel.getLayoutX();
			this.confirm.setLayoutX(cancelX);
			this.cancel.setLayoutX(confirmX);
		}
		
	}
	
	/**
	 * 设置弹出提示框的显示类型
	 * @param text [String]提示内容
	 * @param buttonStyle [Integer]按钮类型
	 * @param imageStyle [Integer]图片类型
	 */
	public void setAlertStyle(String text, Integer buttonStyle, Integer imageStyle) {
		// 设置提示内容
		this.setText(text != null ? text : "");
		// 设置按钮visiable类型
		this.setButtonStyle(buttonStyle);
		// 设置图片类型
		this.setImage(imageStyle);
	}
	
	/**
	 * 初始化方法
	 */
	@Override
	@SuppressWarnings("static-access")
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
		// 初始化提示面板
		this.setAlertStyle("", this.NONE, null);
		
		// 确认按钮添加鼠标单击时间监听
		this.confirm.setOnMouseClicked(event -> {
			// 创建dto
			this.setDto(new HashMap<Object, Object>(1));
			// 赋值
			this.getDto().put("userChoice", true);
			// 关闭窗口
			this.closeCurrStage();
		});
		
		// 取消按钮添加鼠标单击事件监听
		this.cancel.setOnMouseClicked(event -> {
			// 创建dto
			this.setDto(new HashMap<Object, Object>(1));
			// 赋值
			this.getDto().put("userChoice", false);
			// 关闭窗口
			this.closeCurrStage();
		});
	};
	
	/**
	 * 按钮的visiable设置类型
	 * 第一个布尔表示确认按钮是否显示: true : 显示、 false : 不显示
	 * 第二个布尔表示取消按钮是否显示: true : 显示、 false : 不显示
	 * 第三个布尔表示确认按钮在取消按钮的左边还是右边: true : 左边、false : 右边、null : 无所谓
	 */
	private final Boolean[][] buttonStyle = {
		{false, false, null},
		{false, true, true},
		{true, false, false},
		{true, true, true}
	};
	
	/**
	 * 图标的样式
	 */
	private final ImageView[] imageStyle = {
		ImageGenerator.createImageView(ImageGenerator.LOADING, 40, 40),
		ImageGenerator.createImageView(ImageGenerator.ASK, 40, 40),
		ImageGenerator.createImageView(ImageGenerator.WARNING, 40, 40),
		ImageGenerator.createImageView(ImageGenerator.OK, 40, 40)
	};
	
}
