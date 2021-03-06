package controller;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.CodeGenerator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import po.TableConfig;
import po.TableField;
import util.AlertUtil;
import util.ConfigProxyReader;
import util.GlobalDto;
import util.ImageGenerator;
import util.Tools;

public class MainFrameController extends BaseController {
	
	/**
	 * slf4j日志配置
	 */
	private static final Logger _LOG = LoggerFactory.getLogger(MainFrameController.class);
	
	/**
	 * 最后一次使用文件选择器打开的路径
	 */
	private String lastOpenPath = null;
	
	/**
	 * 所有表的配置信息
	 */
	private Map<String, TableConfig> tableConfigs = null;
	
	/**
	 * 当前TreeView是否选中根目录
	 */
	private boolean isRoot = true;
	
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
	 * 表名
	 */
	@FXML
	private TextField tableName;
	
	/**
	 * pojo类名
	 */
	@FXML
	private TextField pojoClassName;
	
	/**
	 * 定制属性按钮
	 */
	@FXML
	private Button customizedFieldsButton;
	
	/**
	 * mapper类名
	 */
	@FXML
	private TextField mapperClassName;
	
	/**
	 * 选择pojo类生成路径按钮
	 */
	@FXML
	private Button choosePojoPathButton;
	
	/**
	 * 选择mapper类生成路径按钮
	 */
	@FXML
	private Button chooseMapperPathButton;
	
	/**
	 * pojo类生成路径
	 */
	@FXML
	private TextField pojoPath;
	
	/**
	 * pojo所在包名
	 */
	@FXML
	private TextField pojoPackage;
	
	/**
	 * mapper类生成路径
	 */
	@FXML
	private TextField mapperPath;
	
	/**
	 * mapper所在包名
	 */
	@FXML
	private TextField mapperPackage;
	
	/**
	 * 是否生成example
	 */
	@FXML
	private CheckBox isBuildExample;
	
	/**
	 * 是否生成注释
	 */
	@FXML
	private CheckBox isBuildNotes;
	
	/**
	 * 生成代码按钮
	 */
	@FXML
	private Button buildButton;
	
	/**
	 * 同步设置按钮
	 */
	@FXML
	private Button syncSettings;
	
	/**
	 * 保存设置按钮
	 */
	@FXML
	private Button saveConfigButton;
	
	/**
	 * 继承自接口的界面初始化方法
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 使用日志输出一下
		_LOG.info("进入【测试界面控制器】初始化方法！");
		
		// 给树形面板安装事件监听
		this.loadTreeViewListenerEvent();
		
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
					ConfigProxyReader.getNewConnectionTitle(), 
					ConfigProxyReader.getDefaultNewConnectionFxmlPath()
			);
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
		// 添加点击事件
		this.top_config.setOnMouseClicked(event -> {
			_LOG.info("弹出【设置】窗口");
			BaseController controller = this.createDialog(
					getCurrStage(), 
					ConfigProxyReader.getSettingTitle(), 
					ConfigProxyReader.getDefaultSettingFxmlPath()
			);
			// 弹出窗口添加关闭监听
			controller.getCurrStage().setOnHidden(closeEvent -> {
				// 读取dto中的值
				this.settingAction(controller.getDto());
			});
		});
		
		// 保存设置添加点击事件
		this.saveConfigButton.setOnMouseClicked(event -> {
			// 不是根目录才有效
			if(!isRoot) {
				// 保存配置
				this.saveConfigFromUI(
						this.center_dbTreeView.getSelectionModel().getSelectedItem().getValue());
			}
		});
		
		// 同步设置添加点击事件
		this.syncSettings.setOnMouseClicked(event -> {
			// 不是根目录才有效
			if(!isRoot) {
				// 保存配置
				this.saveConfigFromUI(
						this.center_dbTreeView.getSelectionModel().getSelectedItem().getValue());
				// 同步配置
				this.syncConfigIntoTables(
						this.center_dbTreeView.getSelectionModel().getSelectedItem().getValue());
			}
		});
		
		// 选择pojo类生成路径按钮添加点击事件
		this.choosePojoPathButton.setOnMouseClicked(event -> {
			// 不是根目录才有效
			if(!isRoot) {
				// 选择pojo类生成路径
				String path = this.choosePath(
						this.center_dbTreeView.getSelectionModel().getSelectedItem().getValue(),
						this.lastOpenPath != null ? new File(this.lastOpenPath) : null
				);
				System.out.println("选择的文件路径：" + path);
				// 不为空则赋值到界面
				if(path != null) {
					System.out.println("赋值到界面上");
					this.pojoPath.setText(path);
					// 更新最后一次选择的路径
					this.lastOpenPath = path;
					// 尝试截取以src为根目录的包名
					String classPackage = this.interceptPackage(path);
					if(classPackage != null) {
						this.pojoPackage.setText(classPackage);
					}
				}
				
			}
		});
		
		// 选择mapper类生成路径按钮添加点击事件
		this.chooseMapperPathButton.setOnMouseClicked(event -> {
			// 不是根目录才有效
			if(!isRoot) {
				// 选择mapper类生成路径
				String path = this.choosePath(
						this.center_dbTreeView.getSelectionModel().getSelectedItem().getValue(),
						this.lastOpenPath != null ? new File(this.lastOpenPath) : null
				);
				// 不为空则赋值到界面
				if(path != null) {
					this.mapperPath.setText(path);
					// 更新最后一次选择的路径
					this.lastOpenPath = path;
					// 尝试截取以src为根目录的包名
					String classPackage = this.interceptPackage(path);
					if(classPackage != null) {
						this.mapperPackage.setText(classPackage);
					}
				}
				
			}
		});
		
		// 定制属性按钮单击事件监听
		this.customizedFieldsButton.setOnMouseClicked(event -> {
			// 不在根目录
			if(!isRoot) {
				// 通过全局dto传输字段配置
				TableConfig tableConfig = this.tableConfigs.get(this.center_dbTreeView.getSelectionModel().getSelectedItem().getValue());
				GlobalDto.getDto().put("tableFields", FXCollections.observableArrayList(tableConfig.getFields()));
				// 弹出定制属性窗口
				BaseController controller = this.createDialog(
						this.getCurrStage(), 
						ConfigProxyReader.getCustomizedTableFieldsTitle(), 
						ConfigProxyReader.getDefaultCustomizedTableFieldsFxmlPath()
				);
				// 为弹出窗口添加关闭监听
				controller.getCurrStage().setOnCloseRequest(closeEvent -> {
					// 从dto中读取表字段的配置信息
					this.readTableFieldConfigFromDto(controller.getDto());
				});
				// 为弹出窗口添加隐藏监听
				controller.getCurrStage().setOnHidden(hideEvent -> {
					// 从dto中读取表字段的配置信息
					this.readTableFieldConfigFromDto(controller.getDto());
				});
			}
		});
		
		// 开始生成代码按钮添加点击事件
		this.buildButton.setOnMouseClicked(event -> {
			// 不在根目录
			if(!isRoot) {
				// 数据是否有效标记
				boolean isValid = true;
				// 所有表的所有配置都不能为空
				for(TableConfig tableConfig : this.tableConfigs.values()) {
					// 检测表配置输入数据是否合法
					if(!Tools.checkDatasValidity(
							getCurrStage(), 
							tableConfig.getTableName(), 
							new String[]{
									"pojoClassName", "mapperClassName", "pojoPath", 
									"pojoPackage", "mapperPath", "mapperPackage"
							}, 
							new String[]{
									"Java POJO 类名", "Mapper 类名", "Java POJO 生成路径", 
									"POJO 所在包名", "Mapper 生成路径", "Mapper 所在包名"
							}, 
							tableConfig
					)) {
						// 更新标记
						isValid = false;
						break;
					}
					for(TableField tableField : tableConfig.getFields()) {
						// 检测表字段定制属性名是否合法
						if(!Tools.checkDatasValidity(
								getCurrStage(), 
								tableConfig.getTableName(), 
								new String[]{"customizedField"}, 
								new String[]{"用户定制属性名"}, 
								tableField
						)) {
							// 更新标记
							isValid = false;
							break;
						}
					}
				}
				// 数据验证有效
				if(isValid) {
					try {
						// 生成代码
						CodeGenerator.generatingCode(this.tableConfigs);
						// 代码生成成功提示
						AlertUtil.getOkAlert(this.getCurrStage(), "代码生成完毕！");
					} catch (Exception e) {
						// 代码生成失败提示
						AlertUtil.getWarningAlert(this.getCurrStage(), "代码生成过程中出现错误！\n" + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		});
		
		// 提示信息的设置
		this.setTooltip();
		
	}
	
	/**
	 * 所有pojo,mapper类名添加前后缀
	 * @param prefix [String]
	 */
	private void addPrefixAndPostfix(String prefix, String postfix) {
		prefix = Tools.isNotEmpy(prefix) ? prefix : "";
		postfix = Tools.isNotEmpy(postfix) ? postfix : "";
		// 遍历所有表的所有配置
		for(TableConfig tableConfig : this.tableConfigs.values()) {
			tableConfig.setPojoClassName(prefix + tableConfig.getPojoClassName() + postfix);
			tableConfig.setMapperClassName(prefix + tableConfig.getMapperClassName() + postfix);
		}
	}
	
	/**
	 * 删除前缀
	 * @param target [String]目标字符串
	 * @param prefix [String]前缀
	 * @return
	 */
	private static final String delPrefix(String target, String prefix) {
		if(target.startsWith(prefix)) {
			return target.substring(prefix.length());
		} else {
			return target;
		}
	}
	
	/**
	 * 删除后缀
	 * @param target [String]目标字符串
	 * @param postfix [String]后缀
	 * @return
	 */
	private static final String delPostfix(String target, String postfix) {
		if(target.endsWith(postfix)) {
			return target.substring(0, target.length() - postfix.length());
		} else {
			return target;
		}
	}
	
	/**
	 * 所有pojo,mapper类名删除前后缀
	 * @param prefix [String]
	 */
	private void delPrefixAndPostfix(String prefix, String postfix) {
		prefix = Tools.isNotEmpy(prefix) ? prefix : "";
		postfix = Tools.isNotEmpy(postfix) ? postfix : "";
		// 遍历所有表的所有配置
		for(TableConfig tableConfig : this.tableConfigs.values()) {
			tableConfig.setPojoClassName(
					delPrefix(tableConfig.getPojoClassName(), prefix));
			tableConfig.setPojoClassName(
					delPostfix(tableConfig.getPojoClassName(), postfix));
			tableConfig.setMapperClassName(
					delPrefix(tableConfig.getMapperClassName(), prefix));
			tableConfig.setMapperClassName(
					delPostfix(tableConfig.getMapperClassName(), postfix));
		}
	}
	
	/**
	 * 对所有pojo,mapper类名执行正则替换操作
	 * @param regex [String]正则表达式
	 * @param replacement [String]正则替换式
	 */
	private void regexClassName(String regex, String replacement) {
		if(!Tools.isNotEmpy(regex) || !Tools.isNotEmpy(replacement)) {
			return;
		} else {
			// 遍历所有表的所有配置
			for(TableConfig tableConfig : this.tableConfigs.values()) {
				tableConfig.setPojoClassName(
						tableConfig.getPojoClassName().replaceAll(regex, replacement));
				tableConfig.setMapperClassName(
						tableConfig.getMapperClassName().replaceAll(regex, replacement));
			}
		}
	}
	
	/**
	 * 设置界面响应操作
	 * @param dto [Map<Object, Object>]设置界面返回值
	 */
	private void settingAction(Map<Object, Object> dto) {
		if(this.tableConfigs == null) {
			_LOG.debug("未连接数据库！");
			return;
		}
		else if((boolean) dto.get("hasResult")) {
			// 添加前后缀
			addPrefixAndPostfix((String) dto.get("addPrefix"), 
					(String) dto.get("addPostfix"));
			// 删除前后缀
			delPrefixAndPostfix((String) dto.get("delPrefix"), 
					(String) dto.get("delPostfix"));
			// 正则匹配
			regexClassName((String) dto.get("regex"), 
					(String) dto.get("replacement"));
			TreeItem<String> item = center_dbTreeView.getSelectionModel().selectedItemProperty().get();
			if(item != center_dbTreeView.getRoot()) {
				// 显示表的配置内容
				showConfigIntoUI(item.getValue());
			}
		} else {
			_LOG.debug("设置界面无返回值");
		}
	}

	/**
	 * 通过路径截取包名
	 * @param path [String]路径
	 * @return [String]截取的包名
	 */
	private String interceptPackage(String path) {
		// 找到根目录
		int index = path.indexOf("src");
		if(index == -1) return null;
		// 特判处理maven项目结构
		int tmp = path.indexOf("src\\main\\java\\");
		if(tmp != -1) {
			index += 14;
		} else {
			index += 4;
		}
		path = path.substring(index);
		_LOG.info("截取出来的路径：" + path);
		return path.replace("\\", ".");
	}
	
	/**
	 * 提示信息的设置
	 */
	private void setTooltip() {
		Tooltip tooltip = new Tooltip("package仅仅用于生成代码，并不会校验路径。");
		
		this.pojoPackage.setTooltip(tooltip);
		this.mapperPackage.setTooltip(tooltip);
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
		// 没有返回值
		if(dto == null) return;
		// 初始化配置界面
		this.clearConfigInUI();
		// 默认在根目录
		this.isRoot = true;
		try {
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
			// 新建表的配置信息列表
			this.tableConfigs = new HashMap<String, TableConfig>();
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
				// 读取该表的数据存储到tableConfigs中
				this.readConfigFromDto(table, dto);
			}
			// 设置默认展开根节点
			rootNode.setExpanded(true);
			// 把根节点安装到TreeView中显示
			this.center_dbTreeView.setRoot(rootNode);
		} catch(Exception e) {
			_LOG.error("安装数据库表数据到TreeView出错！{}", e);
			AlertUtil.getWarningAlert(
					this.getCurrStage(), 
					"安装数据库表数据到TreeView出错！\n" + e.getMessage()
			);
		}
	
	}
	
	/**
	 * 给树形面板安装事件监听
	 */
	private void loadTreeViewListenerEvent() {
		// 当树形图选择的内容发生改变时
		this.center_dbTreeView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<TreeItem<String>>(){

			@Override
			public void changed(ObservableValue<? extends TreeItem<String>> observable, 
					TreeItem<String> oldValue, TreeItem<String> newValue) {
				// 如果选中根节点
				if(newValue == center_dbTreeView.getRoot()) {
					// 跟新标记
					isRoot = true;
					// 清空面板内容
					clearConfigInUI();
					// newValue != null 一定要加上去，不然在安装监听器的时候会报错
				} else if(newValue != null) {
					// 更新标记
					isRoot = false;
					// 显示表的配置内容
					showConfigIntoUI(newValue.getValue());
				}
			}

		});

	}
	
	/**
	 * 从属性定制页面传回来的dto中读取表的字段的配置
	 * 
	 * @param dto [Map<Object, Object>]定制属性页面传回来的数据传输对象
	 */
	@SuppressWarnings("unchecked")
	private void readTableFieldConfigFromDto(Map<Object, Object> dto) {
		// 没有返回数据
		if(dto == null || dto.get("tableFields") == null) return;
		// 读取dto中返回的数据
		List<TableField> tableFields = (List<TableField>) dto.get("tableFields");
		// 获取当前所在表的配置对象
		TableConfig tableConfig = this.tableConfigs.get(this.center_dbTreeView.getSelectionModel().getSelectedItem().getValue());
		// 把表字段的的配置信息更新到表的配置对象中
		tableConfig.setFields(tableFields);
	}
	
	/**
	 * 选择路径
	 * 
	 * <pre>
	 * 该方法在用户点击选择径按钮的时候触发，调用时已经确保非根目录，
	 * 在没读数据库，读取数据库没有表和读取数据库有表三种情况中都不存在异常，所以不进行数据校验
	 * </pre>
	 * 
	 * @param table [String]表名
	 * @param initDirectory [File]默认路径
	 * @return [String]读取到文件夹返回路径、没读取到返回null
	 */
	private String choosePath(String table, File initDirectory) {
		// 文件夹选择器
		DirectoryChooser directoryChooser = new DirectoryChooser();
		// 设置默认路径
		if(initDirectory != null) directoryChooser.setInitialDirectory(initDirectory);
		// 设置父父窗口层，子窗口弹出时父窗口堵塞
		File selectedFolder = directoryChooser.showDialog(this.getCurrStage());
		return selectedFolder != null ? selectedFolder.getAbsolutePath() : null;
	}
	
	/**
	 * 当且仅当数据有效时，
	 * 同步当前表的pojo类生成路径和mapper类生成路径到其它表的配置中。
	 * 
	 * <pre>
	 * 该方法在用户点击同步配置按钮的时候触发，调用时已经确保非根目录，
	 * 在没读数据库，读取数据库没有表和读取数据库有表三种情况中都不存在异常，所以不进行数据校验
	 * </pre>
	 * 
	 * @param table [String]表名
	 */
	private void syncConfigIntoTables(String table) {
		// 先保存
		this.saveConfigFromUI(table);
		// 获取当前表的配置
		TableConfig currTableConfig = this.tableConfigs.get(table);
		// 要检测赋值的字段:pojo类生成路径、pojo类所在包名、mapper生成路径、mapper所在包名
		String[] fields = new String[]{"pojoPath", "pojoPackage", "mapperPath", "mapperPackage"};
		// 当前配置文件的配置信息
		String[] datas = new String[]{
				currTableConfig.getPojoPath(), currTableConfig.getPojoPackage(), 
				currTableConfig.getMapperPath(), currTableConfig.getMapperPackage()
		};
		// 遍历所有表的配置，为没有设置类生成路径的配置同步配置
		for(TableConfig tableConfig : this.tableConfigs.values()) {
			try {
				// 通过反射读取tableConfig中需要检测的字段进行检测
				for(int i = 0; i < fields.length; i ++) {
					// 反射获取字段
					Field data = tableConfig.getClass().getDeclaredField(fields[i]);
					// 因为可能修改属性，所以关闭java访问检测
					data.setAccessible(true);
					// 如果属性值为空，或全为空格，则同步配置
					String dataStr = (String) data.get(tableConfig);
					if(dataStr == null || dataStr.trim().length() <= 0) {
						// 同步配置
						data.set(tableConfig, datas[i]);
					}
				}
			} catch(Exception e) {
				_LOG.error("同步配置读取属性异常！");
				AlertUtil.getWarningAlert(this.getCurrStage(), "同步配置读取属性异常！");
				e.printStackTrace();
				// 结束方法
				return;
			}
		}
	}
	
	/**
	 * TODO 代码冗余
	 * 保存当前表的配置
	 * 
	 * <pre>
	 * 该方法在用户点击保存配置按钮的时候触发，调用时已经确保非根目录，
	 * 在没读数据库，读取数据库没有表和读取数据库有表三种情况中都不存在异常，所以不进行数据校验
	 * </pre>
	 * 
	 * @param table [String]表名
	 */
	private void saveConfigFromUI(String table) {
		// 获取当前表的原始配置
		TableConfig tableConfig = this.tableConfigs.get(table);
		// pojo类名
		tableConfig.setPojoClassName(this.pojoClassName.getText());
		// mapper类名
		tableConfig.setMapperClassName(this.mapperClassName.getText());
		// pojo类生成路径
		tableConfig.setPojoPath(this.pojoPath.getText());
		// pojo类所在包名
		tableConfig.setPojoPackage(this.pojoPackage.getText());
		// mapper类生成路径
		tableConfig.setMapperPath(this.mapperPath.getText());
		// mapper类所在包名
		tableConfig.setMapperPackage(this.mapperPackage.getText());
		// 是否生成example类
		tableConfig.setBuildExample(this.isBuildExample.isSelected());
		// 是否生成注释
		tableConfig.setBuildNote(this.isBuildNotes.isSelected());
	}
	
	/**
	 * 读取新建连接界面传输回来的dto数据，并存储到tableConfigs中
	 * 
	 * <pre>
	 * 在界面安装TreeView的时候才会调用这个方法，
	 * 上级方法已经校验过dto，dto中只有存在table才会调用这个方法，
	 * 而且，在调用这个方法之前已经创建了tableConfigs，所以不需要数据校验
	 * </pre>
	 * @param table [String]表
	 * @param dto [Map<Object, Object>]子页面传输过来的数据
	 */
	@SuppressWarnings("unchecked")
	private void readConfigFromDto(String table, Map<Object, Object> dto) {
		// 创建table配置对象
		TableConfig tmpConfig = new TableConfig();
		// 获取table中字段的信息，存储到配置信息Map中
		tmpConfig = new TableConfig();
		// 设置字段信息
		tmpConfig.setFields((List<TableField>) dto.get(table));
		// 判断是否有主键
		for(TableField field : tmpConfig.getFields()) {
			// 判断是否存在主键
			if(field.getKey().equals("PRI")) {
				tmpConfig.setExistsPrimaryKey(true);
				break;
			}
		}
		// 设置表名,用于生成sql语句
		tmpConfig.setTableName(table);
		// 表名就是默认pojo类名，首字母大写
		tmpConfig.setPojoClassName(
				Tools.removeUnderlineAndcapitalNextChar(Tools.capitalFirstChar(table)));
		// 表名+Mapper就是mapper名， 首字母大写
		tmpConfig.setMapperClassName(
				Tools.removeUnderlineAndcapitalNextChar(Tools.capitalFirstChar(table + "Mapper")));
		// 存储到表配置Map中
		this.tableConfigs.put(table, tmpConfig);
	}
	
	/**
	 * TODO 代码冗余
	 * 显示配置信息
	 * 
	 * <pre>
	 * 该方法在TreeView的change事件监听器中调用，
	 * 上级方法已经确保一定能选择到有效值TreeItem，但不保证TreeItem.getValue不回返回空值，所以校验table，
	 * TODO 至于tableConfigs以及通过tableConfigs获取的对象都不保证能够获得非null值，所以都进行校验
	 * </pre>
	 * 
	 * @param table [String]表名
	 */
	private void showConfigIntoUI(String table) {
		// 校验数据
		if(table == null || this.tableConfigs == null || this.tableConfigs.get(table) == null) 
			return;
		// 初始化
		this.clearConfigInUI();
		// 获取当前页面的配置文件
		TableConfig tableConfig = this.tableConfigs.get(table);
		// 表名
		this.tableName.setText(this.getString(tableConfig.getTableName()));
		// pojo类名
		this.pojoClassName.setText(this.getString(tableConfig.getPojoClassName()));
		// mapper类名
		this.mapperClassName.setText(this.getString(tableConfig.getMapperClassName()));
		// pojo类生成路径
		this.pojoPath.setText(this.getString(tableConfig.getPojoPath()));
		// pojo类所在包名
		this.pojoPackage.setText(this.getString(tableConfig.getPojoPackage()));
		// mapper类生成路径
		this.mapperPath.setText(this.getString(tableConfig.getMapperPath()));
		// mapper类所在包名
		this.mapperPackage.setText(this.getString(tableConfig.getMapperPackage()));
		// 是否生成example类
		this.isBuildExample.setSelected(tableConfig.isBuildExample());
		// 是否生成注释
		this.isBuildNotes.setSelected(tableConfig.isBuildNote());
	}
	
	/**
	 * 获取字符串，滤掉null
	 * @param str [String]待获取字符串
	 * @return
	 */
	private String getString(String str) {
		return str == null ? "" : str;
	}
	
	
	/**
	 * TODO 代码冗余
	 * 清空表配置面板的内容
	 */
	private void clearConfigInUI() {
		// 表名
		this.tableName.setText("");
		// pojo类名
		this.pojoClassName.setText("");
		// mapper类名
		this.mapperClassName.setText("");
		// pojo类生成路径
		this.pojoPath.setText("");
		// pojo类所在包名
		this.pojoPackage.setText("");
		// mapper类生成路径
		this.mapperPath.setText("");
		// mapper类所在包名
		this.mapperPackage.setText("");
	}
	
	
	
}
