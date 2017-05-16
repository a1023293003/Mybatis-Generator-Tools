package Interface;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import controller.BaseController;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import util.ImageGenerator;

/**
 * 提示框控制器接口
 * 
 * 提示框控制器接口类，提供了提示框控制器的基础方法
 * 
 * @author 随心
 *
 */
public interface Alert {

	/**
	 * 不显示任何按钮
	 */
	public static final int NONE = 0;
	
	/**
	 * 只包含取消按钮
	 */
	public static final int CANCEL = 1;

	/**
	 * 只包含确定按钮
	 */
	public static final int CONFIRM = 2;
	
	/**
	 * 包含控制和取消按钮
	 */
	public static final int CONFIRM_CANCEL = 3;
	
	
	/**
	 * 等待类型
	 */
	public static final int LOADING = 0;
	
	/**
	 * 询问类型
	 */
	public static final int ASK = 1;
	
	/**
	 * 警告类型
	 */
	public static final int WARNING = 2;
	
	/**
	 * ok类型
	 */
	public static final int OK = 3;
	
	/**
	 * 设置弹出提示框信息内容
	 * @param message [String]提示信息
	 */
	public void setText(String message);
	
	/**
	 * 设置图片
	 * @param style [Integer]类型
	 */
	public void setImage(Integer style);
	
	/**
	 * 设置按钮模式
	 * @param style [Integer]类型
	 */
	public void setButtonStyle(Integer style);
	
	/**
	 * 设置弹出提示框的显示类型
	 * @param text [String]提示内容
	 * @param buttonStyle [Integer]按钮类型
	 * @param imageStyle [Integer]图片类型
	 */
	public void setAlertStyle(String text, Integer buttonStyle, Integer imageStyle);
	
	/**
	 * 获取当前页面所在的层
	 * @return
	 */
	public Stage getCurrStage();
	
	/**
	 * 获取父级页面所在的层
	 * @return
	 */
	public Stage getParentsStage();
	
	/**
	 * 设置当前页面所在的层
	 * @param currStage
	 */
	public void setCurrStage(Stage currStage);
	
	/**
	 * 设置父级页面所在的层
	 * @param parentsStage
	 */
	public void setParentsStage(Stage parentsStage);
	
	/**
	 * 关闭当前页面显示层
	 */
	public void closeCurrStage();
	
	/**
	 * 关闭父级页面显示层
	 */
	public void closeParentsStage();
	
	/**
	 * 显示当前页面层
	 */
	public void showCurrStage();
	
	/**
	 * 显示父级页面层
	 */
	public void showParentsStage();
	
	/**
	 * 获取数据传输对象
	 * @return
	 */
	public Map<Object, Object> getDto();
	
	/**
	 * 设置数据传输对象
	 * @param dto
	 */
	public void setDto(Map<Object, Object> dto);
	
	/**
	 * 通过传入的父级层、标题、fxml路径以及fxml对应的控制器的类的全路径创建一个弹出窗口，并返回弹出窗口的控制器
	 * 
	 * @param parentsStage [Stage]父页面层
	 * @param title [String]界面标题
	 * @param fxmlPath [String]fxml相对与项目的绝对路径
	 * @return [BaseController]查询成功返回controller，查询失败返回null
	 */
	public BaseController createDialog(Stage parentsStage, String title, String fxmlPath);
	
	/**
	 * 初始化界面
	 * @param location
	 * @param resources
	 */
	public void initialize(URL location, ResourceBundle resources);
}
