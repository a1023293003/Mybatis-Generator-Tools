package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import po.TableField;
import util.AlertUtil;
import util.ConfigProxyReader;
import util.GlobalDto;
import util.TypeConverter;

/**
 * 定制表属性界面控制器
 * 
 * @author 随心
 *
 */
public class CustomizedTableFieldsController extends BaseController {

	/**
	 * slf4j日志配置
	 */
	private static final Logger _LOG = LoggerFactory.getLogger(CustomizedTableFieldsController.class);

	/**
	 * 表格显示区域
	 */
	@FXML
	private TableView<TableField> tableFields;
	
	/**
	 * 字段名
	 */
	@FXML
	private TableColumn<TableField, String> columnName;
	
	/**
	 * 字段对应jdbc类型
	 */
	@FXML
	private TableColumn<TableField, String> jdbcType;
	
	/**
	 * 字段对应pojo属性名
	 */
	@FXML
	private TableColumn<TableField, String> propertyName;
	
	/**
	 * 字段对应java类型
	 */
	@FXML
	private TableColumn<TableField, String> javaType;
	
	/**
	 * 保存设置按钮
	 */
	@FXML
	private Button saveConfigButton;
	
	/**
	 * 取消按钮
	 */
	@FXML
	private Button cancelButton;
	
	/**
	 * 界面初始化方法
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// 获取可选java类型列表
		ObservableList<String> javaTypeList = FXCollections.observableArrayList(TypeConverter.getJavaTypes());
		// 映射到TableField中对应的属性
		this.columnName.setCellValueFactory(
				new PropertyValueFactory<TableField, String>("field"));
		this.jdbcType.setCellValueFactory(
				new PropertyValueFactory<TableField, String>("type"));
		this.propertyName.setCellValueFactory(
				new PropertyValueFactory<TableField, String>("customizedField"));
		this.javaType.setCellValueFactory(
				new PropertyValueFactory<TableField, String>("javaType"));
		// 设置单元工厂，用于修改内容时弹出对应组件
		this.propertyName.setCellFactory(new Callback<TableColumn<TableField,String>, TableCell<TableField,String>>() {

			@Override
			public TableCell<TableField, String> call(TableColumn<TableField, String> param) {
				// 创建自定义文本单元
				return new TextFieldTableCellImpl();
			}
		});
		// 添加修改事件
		this.propertyName.setOnEditCommit((CellEditEvent<TableField, String> event) -> {
			// 当用户修改TableView中的值的时候同步更新数据源
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setCustomizedField(event.getNewValue());
		});
		
		// 设置单元工厂，用于修改内容时弹出对应组件
		this.javaType.setCellFactory(ComboBoxTableCell.forTableColumn(javaTypeList));
		// 添加修改事件
		this.javaType.setOnEditCommit((CellEditEvent<TableField, String> event) -> {
			// 当用户修改TableView中的值的时候同步更新数据源
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setJavaType(event.getNewValue());
		});
		
		// 全局dto中获取并设置表格显示内容
		this.tableFields.setItems((ObservableList<TableField>) GlobalDto.getDto().get("tableFields"));
		
		// 表格显示区域默认选中第一行数据，因为mysql数据库表至少要有一个字段
		this.tableFields.getSelectionModel().select(0);
		// 设置TableView的高度
		this.tableFields.setFixedCellSize(ConfigProxyReader.getCustomizedTableFieldsFixedCellSize());
		// 设置TableView中列的对齐方式
		this.setTableViewAlignment(ConfigProxyReader.getCustomizedTableFieldsTableColumnAlignment());
		
		// 取消按钮添加单击时间
		this.cancelButton.setOnMouseClicked(event -> {
			// 关闭当前窗口
			this.closeCurrStage();
		});
		
		// 保存设置按钮添加单击事件
		this.saveConfigButton.setOnMouseClicked(evnet -> {
			// 保存配置到dto中
			this.saveConfigIntoDto();
			// 关闭窗口
			this.closeCurrStage();
		});
	}
	
	/**
	 * 设置TableView的对齐方式
	 * @param alignment [String]对齐css
	 */
	private void setTableViewAlignment(String alignment) {
		// 获取TableView中的所有列
		ObservableList<TableColumn<TableField, ?>> tableColumns = this.tableFields.getColumns();
		// 设置列的对齐方式
		for(TableColumn<TableField, ?> tableColumn : tableColumns) {
			tableColumn.setStyle(alignment);
		}
	}
	
	/**
	 * 保存配置到dto中
	 */
	private void saveConfigIntoDto() {
		// 读取当前table中的数据
		List<TableField> tableFields = this.tableFields.getItems();
		// 判断dto是否需要创建
		if(this.getDto() == null) this.setDto(new HashMap<Object, Object>());
		// 数据存储到dto中
		this.getDto().put("tableFields", tableFields);
	}
	
	/**
	 * 内部自定义文本行TableCell类，用于自定义文本编辑TableItem操作
	 * @author 随心
	 *
	 */
	private final class TextFieldTableCellImpl extends TableCell<TableField, String> {
		/**
		 * 编辑文本行
		 */
		private TextField textField;
		
		/**
		 * 当前文本类
		 */
		private String currText = "";
		
		public TextFieldTableCellImpl() {}
		
		@Override  
		public void startEdit() {
			this.currText = this.getString();
			super.startEdit();  
			// 文本行对象为空则创建
			if (textField == null) {  
				createTextField();  
			}
			// 设置label的内容为空
			setText(null);
			// 把文本行安装到label上
			setGraphic(textField);
			// 选中文本行中所有内容
			textField.selectAll();  
		}  

		@Override  
		public void cancelEdit() {
			super.cancelEdit();
			// 给label设置原先Cell中的值
			setText(this.currText);
			// label中设置TreeItem的默认样式
			setGraphic(getTableColumn().getGraphic());  
		}  

		/**
		 * <p>如果不覆盖这个方法，遇到空白的cell可能出现意想不到的结果</p>
		 * 
		 * <pre>
		 * It is very important that subclasses of Cell override the updateItem method properly, 
		 * as failure to do so will lead to issues such as blank cells or cells with unexpected content appearing within them.
		 * </pre>
		 */
		@Override  
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setText(null);
				setGraphic(null);  
			} else { 
				// 正在编辑
				if (isEditing()) {  
					if (textField != null) {  
						textField.setText(getString());  
					}
					// 设置label内容为空
					setText(null);  
					setGraphic(textField);  
				} else {
					// 编辑完成
					setText(getString());
					// 还原column显示层
					setGraphic(getTableColumn().getGraphic());  
				}  
			}  
		}  

		/**
		 * 创建文本行
		 */
		private void createTextField() {
			// 创建文本行
			textField = new TextField(getString());  
			// 添加键盘释放时的监听事件
			textField.setOnKeyReleased((KeyEvent t) -> {  
				if (t.getCode() == KeyCode.ENTER) {  
					// 按下了回车、提交修改
					commitEdit(textField.getText());  
				} else if (t.getCode() == KeyCode.ESCAPE) { 
					// 按下了ESC、取消修改
					cancelEdit();  
				}  
			});  
		}  

		private String getString() {  
			return getItem() == null ? "" : getItem().toString();  
		}  

	}
	
}
