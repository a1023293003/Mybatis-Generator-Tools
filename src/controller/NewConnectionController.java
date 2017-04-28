package controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbAction.MySQLAction;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.ConfigProxyReader;
import util.SQLProxyAction;
import util.Tools;

/**
 * 新建连接界面
 * 
 * 返回值存储在dto中，包括三个部分
 * 
 * @author 随心
 *
 */
public class NewConnectionController extends BaseController {

	/**
	 * slf4j日志配置
	 */
	private static final Logger _LOG = LoggerFactory.getLogger(MainFrameController.class);

	/**
	 * 容器
	 */
	@FXML
	private AnchorPane container;
	
	/**
	 * 数据库类型
	 */
	@FXML
	private ComboBox<String> sqlTypes;
	
	/**
	 * 编码类型
	 */
	@FXML
	private ComboBox<String> codes;
	
	/**
	 * 测试连接按钮
	 */
	@FXML
	private Button testConnect;
	
	/**
	 * 确认按钮
	 */
	@FXML
	private Button confirm;
	
	/**
	 * 取消按钮
	 */
	@FXML
	private Button cancel;
	
	/**
	 * 提示标签
	 */
	@FXML
	private Label promptLabel;
	
	/**
	 * 连接名
	 */
	@FXML
	private TextField connectionName;
	
	/**
	 * 主机名或IP地址
	 */
	@FXML
	private TextField ip;
	
	/**
	 * 端口
	 */
	@FXML
	private TextField port;
	
	/**
	 * 用户名
	 */
	@FXML
	private TextField userName;
	
	/**
	 * 密码
	 */
	@FXML
	private TextField password;
	
	/**
	 * 数据库名
	 */
	@FXML
	private TextField databaseName;
	
	/**
	 * 继承自接口的界面初始化方法
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// 使用日志输出一下
		_LOG.info("进入【新建连接界面控制器】初始化方法！");
		
		// 读取并设置SQL类型数据
		this.sqlTypes.getItems().setAll(ConfigProxyReader.getSqlTypes());
		// 设置SQL类型默认值
		this.sqlTypes.getSelectionModel().select(0);
		
		// 读取并设置编码类型数据
		this.codes.getItems().setAll(ConfigProxyReader.getCodes());
		// 设置编码类型默认值
		this.codes.getSelectionModel().select(0);
		
		// 设置默认IP
		this.ip.setText(ConfigProxyReader.getMysqlDefaultIP());
		
		// 设置默认端口
		this.port.setText(ConfigProxyReader.getMySQLDefaultPort());
		
		// 设置默认用户
		this.userName.setText(ConfigProxyReader.getMySQLDefaultUserName());
		
		// 测试连接按钮添加鼠标单击事件监听
		this.testConnect.setOnMouseClicked(event -> {
			// 测试连接
			testConnectAction();
		});
		
		// 确认按钮添加鼠标单击事件监听
		this.confirm.setOnMouseClicked(event -> {
			// 尝试从数据库中获取数据，并存储到dto中
			this.tryToGetDtoFromDatabase();
			// 关闭窗口
//			this.closeCurrStage();
		});
		
		// 取消按钮添加鼠标单击事件监听
		this.cancel.setOnMouseClicked(event -> {
			// 关闭窗口
			this.closeCurrStage();
		});
		
	}
	
	/**
	 * 尝试从数据库中获取数据，并存储到dto中
	 */
	private void tryToGetDtoFromDatabase() {
		MySQLAction dao = null;
		try {
			// 尝试获取一个数据库操作对象
			dao = this.connectSQLAction();
			// 读取数据并存储到dto中
			// 获取数据库中所有表
			List tables = Tools.isNull(dao.getTables(this.databaseName.getText()));
		} catch (Exception e) {
			final AlertUtilController alert = (AlertUtilController) this.createDialog(getCurrStage(), "提示", "/ui/AlertUtil.fxml");
			this.exceptionAction(alert, e);
			Thread test = new Thread() {
				
				@Override
				public void run() {
					super.run();
					while(alert.getCurrStage().isShowing());
				}
			};
			test.start();
			
		} finally {
			try {
				if(dao != null) {
					// 释放资源
					dao.free();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	/**
	 * 测试连接操作
	 * @return
	 */
	private boolean testConnectAction() {
		// 弹出提示窗口
		final AlertUtilController alert = (AlertUtilController) this.createDialog(getCurrStage(), "提示", "/ui/AlertUtil.fxml");
		// TODO 存在线程堵塞，用多线程优化 
		final String msg = "连接到 " + this.ip.getText() + ":" + this.port.getText();
//		alert.setAlertStyle(msg, AlertUtilController.NONE, AlertUtilController.LOADING);

		new Thread() {
			
			@Override
			public void run() {
				super.run();
				// 设置窗口类型为等待提示
				alert.setAlertStyle(msg, AlertUtilController.NONE, AlertUtilController.LOADING);

			}
		}.start();
		try {
			// 尝试获取数据库操作对象
			this.connectSQLAction().free();
			// 更新提示
			alert.setAlertStyle("连接成功！", AlertUtilController.CONFIRM, AlertUtilController.OK);
		} catch (Exception e) {
			_LOG.error("连接数据库失败:{}", e.getMessage());
			// 弹出警告
//			alert.setAlertStyle(e.getMessage(), AlertUtilController.CONFIRM, AlertUtilController.WARNING);
			this.exceptionAction(alert, e);
			return false;
		}
		return true;
	}
	
	/**
	 * 连接数据库操作，获取一个MySQL数据库操作对象，用完之后记得销毁
	 * @return [MySQLAction]MySQL数据库操作对象
	 * @throws Exception 
	 */
	private MySQLAction connectSQLAction() throws Exception {
		// 获取一个MySQL数据库操作对象
		return SQLProxyAction.getMySQLAction(this.ip.getText(), this.port.getText(), this.databaseName.getText(), this.userName.getText(), this.password.getText(), this.codes.getSelectionModel().getSelectedItem());
	}

	/**
	 * 异常统一处理方法
	 * @param e
	 */
	private void exceptionAction(AlertUtilController alert, Exception e) {
		if(e instanceof NullPointerException) {
			_LOG.error("空指针异常！");
			alert.setAlertStyle("空指针异常！\n" + e.getMessage(), AlertUtilController.CONFIRM, AlertUtilController.WARNING);
			e.printStackTrace();
		} else if(e instanceof SQLException) {
			_LOG.error("数据库操作异常:{}", e.getMessage());
			alert.setAlertStyle("数据库操作异常！\n" + e.getMessage(), AlertUtilController.CONFIRM, AlertUtilController.WARNING);
			e.printStackTrace();
		} else {
			_LOG.warn("未知错误！");
			alert.setAlertStyle("未知错误！\n" + e.getMessage(), AlertUtilController.CONFIRM, AlertUtilController.WARNING);
			e.printStackTrace();
		}
	}
	
}
