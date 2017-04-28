package controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import util.ImageGenerator;

public class MainFrameController extends BaseController {
	
	/**
	 * slf4j日志配置
	 */
	private static final Logger _LOG = LoggerFactory.getLogger(MainFrameController.class);
	
	/**
	 * 数据库显示栏
	 */
	@FXML
	private TreeView<String> center_dbTreeView;
	
	/**
	 * 数据库连接标签
	 */
	@FXML
	private Label top_dbLabel;
	
	/**
	 * 配置标签
	 */
	@FXML
	private Label top_config;
	
	/**
	 * 继承自接口的界面初始化方法
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 使用日志输出一下
		_LOG.info("进入【测试界面控制器】初始化方法！");
		
		// 设置数据库连接标签图片
		this.top_dbLabel.setGraphic(ImageGenerator.createImageView(ImageGenerator.DATABASE_CONNECTION, 40, 40));
		// 设置图片显示在文字上面
		this.top_dbLabel.setContentDisplay(ContentDisplay.TOP);
		// 添加点击事件
		this.top_dbLabel.setOnMouseClicked(event -> {
			_LOG.info("弹出【新建连接】窗口");
			this.createDialog(getCurrStage(), "新建连接", "/ui/NewConnection.fxml");
		});
		
		// 设置设置标签图片
		this.top_config.setGraphic(ImageGenerator.createImageView(ImageGenerator.CONFIG, 40, 40));
		// 设置图片显示在文字上面
		this.top_config.setContentDisplay(ContentDisplay.TOP);
		
		// 设置数据库显示栏不显示根标签
		this.center_dbTreeView.setShowRoot(false);
		
	}
}
