package controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Interface.Alert;
import dbAction.MySQLAction;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import po.TableField;
import util.AlertUtil;
import util.ConfigProxyReader;
import util.SQLProxyAction;
import util.Tools;
import util.TypeConverter;

/**
 * 新建连接界面
 * 返回值存储在dto中，包括三个部分
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
			this.tryToGetDtoFromDatabase(false);
		});
		
		// 确认按钮添加鼠标单击事件监听
		this.confirm.setOnMouseClicked(event -> {
			// 尝试从数据库中获取数据，并存储到dto中
			this.tryToGetDtoFromDatabase(true);
		});
		
		// 取消按钮添加鼠标单击事件监听
		this.cancel.setOnMouseClicked(event -> {
			// 关闭窗口
			this.closeCurrStage();
		});
		
		// 数据库输入行添加键盘回车监听
		this.databaseName.setOnKeyPressed(event -> {
			// 按下了回车键
			if(event.getCode() == KeyCode.ENTER) {
				// 相当于按了确认键，尝试从数据库中获取数据，并存储到dto中
				this.tryToGetDtoFromDatabase(true);
			}
		});
		
	}
	
	/**
	 * 尝试从数据库中获取数据，并存储到dto中
	 * 
	 * @param actionType [boolean]操作类型，
	 * 		当actionType为false的时候进行测试连接操作、当actionType为true的时候为连接并读取数据操作
	 */
	private void tryToGetDtoFromDatabase(boolean actionType) {
		// 弹出等待提示框
		Alert alert = AlertUtil.getLoadingAlert(
				getCurrStage(), "连接到 " + this.ip.getText() + ":" + this.port.getText()
		);
		// 创建一个线程公用的Map空间存储变量
		final Map<String, Object> threadMap = new HashMap<String, Object>();
		threadMap.put("alert", alert);
		// 新建一个线程尝试连接数据库
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				// 主线程弹出的提示框
				Alert alert = (Alert) threadMap.get("alert");
				// MySQL数据库操作对象
				MySQLAction dao = null;
				try {
					// 尝试连接数据库
					dao = connectSQLAction();
					// 判断操作类型
					if(!actionType) {
						// 释放资源
						dao.free();
						// 更改提示框
						alert.setAlertStyle("连接成功", Alert.CONFIRM, Alert.OK);
						return;
					}
					// 把数据安装到dto中
					loadDto(dao);
					// 关闭提示框
					alert.closeCurrStage();
					// 关闭当前窗口层
					closeCurrStage();
				} catch (Exception e) {
					// 异常提示
					Tools.exceptionAction(alert, e);
				} finally {
					if(dao != null) {
						// 释放资源
						dao.free();
					}
				}
				
			}
		});
	}
	
	/**
	 * 把数据安装到dto中
	 * 
	 * @param dao [MySQLAction]mysql操作对象
	 */
	private void loadDto(MySQLAction dao) {
		// 创建dto对象
		setDto(new HashMap<Object, Object>());
		// 连接名
		getDto().put("connectionName", connectionName.getText());
		// 数据库名
		getDto().put("databaseName", databaseName.getText());
		// 读取数据库中所有表名
		List<String> tables = dao.getTables(databaseName.getText());
		getDto().put("tables", tables);
		// 读取所有表名的字段
		for(String table : tables) {
			// 设置表字段配置中的默认映射属性名和java类型
			List<TableField> tableFields = dao.getField(databaseName.getText(), table);
			for(TableField tableField : tableFields) {
				// 默认映射属性名为表名
				tableField.setCustomizedField(tableField.getField());
				// 默认java类型 
				tableField.setJavaType(TypeConverter.jdbcTypeToJavaTypeClassName(tableField.getType()));
			}
			// 表字段配置存储到dto中
			getDto().put(table, tableFields);
		}
	}
	
	/**
	 * 连接数据库操作，获取一个MySQL数据库操作对象，用完之后记得销毁
	 * 
	 * @return [MySQLAction]MySQL数据库操作对象
	 * @throws Exception 
	 */
	public MySQLAction connectSQLAction() throws Exception {
		// 获取一个MySQL数据库操作对象
		return SQLProxyAction.getMySQLAction(
				this.ip.getText(), 
				this.port.getText(), 
				this.databaseName.getText(), 
				this.userName.getText(), 
				this.password.getText(), 
				this.codes.getSelectionModel().getSelectedItem());
	}
}
