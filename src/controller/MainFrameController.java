package controller;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
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
		this.top_dbLabel.setGraphic(
				ImageGenerator.createImageView(ImageGenerator.DATABASE_CONNECTION, 40, 40)
		);
		// 设置图片显示在文字上面
		this.top_dbLabel.setContentDisplay(ContentDisplay.TOP);
		// 添加点击事件
		this.top_dbLabel.setOnMouseClicked(event -> {
			_LOG.info("弹出【新建连接】窗口");
			BaseController controller = this.createDialog(
					getCurrStage(), 
					"新建连接", 
					"/ui/NewConnection.fxml"
			);
			// 创建一个线程公用的Map空间存储变量
			final Map<String, Object> threadMap = new HashMap<String, Object>();
			threadMap.put("controller", controller);
			// 弹出窗口添加关闭监听
			controller.getCurrStage().setOnHidden(closeEvent -> {
				// 读取dto中的值，并赋值到左侧的数据库显示树状表中
				this.loadTreeView(controller.getDto());
			});
		});
		
		// 设置设置标签图片
		this.top_config.setGraphic(
				ImageGenerator.createImageView(ImageGenerator.CONFIG, 40, 40));
		// 设置图片显示在文字上面
		this.top_config.setContentDisplay(ContentDisplay.TOP);
		
		
	}
	
	/**
	 * TODO 这里有好多硬编码
	 * 安装窗口左侧数据库信息显示树状表
	 * 
	 * 读取dto中的值，并赋值到左侧的数据库显示树状表中
	 * 
	 * @param dto [Map<Object, Object>]子窗口层传输过来的数据
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void loadTreeView(Map<Object, Object> dto) {
		if(dto == null) return;
		// 读取根目录名称
		String rootName = ((String) dto.get("connectionName")).length() <= 0 ? 
				(String) dto.get("databaseName") : 
					(String) dto.get("connectionName");
		// 创建根根节点
		TreeItem<String> rootNode = new TreeItem<String>(
				rootName, 
				ImageGenerator.createImageView(ImageGenerator.DATABASE, 15, 15)
		);
		// 读取表名
		List<String> tables = (List<String>) dto.get("tables");
		// 根据表名创建对应的TreeItem添加到根节点中
		TreeItem<String> tmpItem = null;
		for(String table : tables) {
			// 创建子节点
			tmpItem = new TreeItem<String>(
					table, 
					ImageGenerator.createImageView(ImageGenerator.TABLE, 15, 15)
			);
			// 插到根节点中
			rootNode.getChildren().add(tmpItem);
		}
		// 设置默认展开根节点
		rootNode.setExpanded(true);
		// 把根节点安装到TreeView中显示
		this.center_dbTreeView.setRoot(rootNode);
	}
	
}
