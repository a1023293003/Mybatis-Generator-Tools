package controller;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * 基础控制器
 * 
 * 1、控制器在所对应的页面在显示出来的时候必定要先传入父级页面所在层（parentsStage），
 * 并以此来生成当前页面所在的层（currStage）
 * 
 * 2、用HashMap类型的属性dto作为返回值，如果dto值为null，说明该界面并没有返回值
 * 
 * @author 随心
 *
 */
public class BaseController implements Initializable {

	/**
	 * slf4j日志配置
	 */
	private static final Logger _LOG = LoggerFactory.getLogger(BaseController.class);

	
	/**
	 * 父级页面所在的层（主要用于控制父级页面跳转）
	 */
	private Stage parentsStage = null;
	
	
	/**
	 * 当前页面所在的层（主要用于弹出子窗口）
	 */
	private Stage currStage = null;

	/**
	 * 数据传输对象，用于界面与界面之间的数据传输
	 */
	private Map<Object, Object> dto = null;
	
	/**
	 * 通过传入的父级层、标题、fxml路径以及fxml对应的控制器的类的全路径创建一个弹出窗口，并返回弹出窗口的控制器
	 * 
	 * @param parentsStage [Stage]父页面层
	 * @param title [String]界面标题
	 * @param fxmlPath [String]fxml相对与项目的绝对路径
	 * @return [BaseController]查询成功返回controller，查询失败返回null
	 */
	public BaseController createDialog(Stage parentsStage, String title, String fxmlPath) {
		Stage currStage = new Stage();
		try {
			// 设置标题
			currStage.setTitle(title);
			// 子页面实现时父页面堵塞
			currStage.initModality(Modality.APPLICATION_MODAL);
			// 设置父页面层
			currStage.initOwner(parentsStage);
			// 读取fxml
			FXMLLoader leader = new FXMLLoader(getClass().getResource(fxmlPath));
			Parent root = leader.load();
			// 设置弹出页面的内容
			currStage.setScene(new Scene(root));
			// 不允许改变窗口大小
			currStage.setMaximized(false);
			currStage.setResizable(false);
			// 显示
			currStage.show();
			// 获取弹出页面的controller
			BaseController controller = leader.getController();
			// 设置父页面层
			controller.setParentsStage(parentsStage);
			// 设置子页面层
			controller.setCurrStage(currStage);
			// 返回结果
			return controller;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 设置数据传输对象
	 * @param dto
	 */
	public void setDto(Map<Object, Object> dto) {
		this.dto = dto;
	}
	
	/**
	 * 获取数据传输对象
	 * @return
	 */
	public Map<Object, Object> getDto() {
		return dto;
	}
	
	/**
	 * 显示父级页面层
	 */
	public void showParentsStage() {
		if(this.parentsStage != null) {
			this.parentsStage.show();
		}
	}
	
	/**
	 * 显示当前页面层
	 */
	public void showCurrStage() {
		if(this.currStage != null) {
			this.currStage.show();
		}
	}
	
	/**
	 * 关闭父级页面显示层
	 */
	public void closeParentsStage() {
		if(this.parentsStage != null) {
			this.parentsStage.close();
		}
	}
	
	/**
	 * 关闭当前页面显示层
	 */
	public void closeCurrStage() {
		if(this.currStage != null) {
			this.currStage.close();
		}
	}
	
	/**
	 * 设置父级页面所在的层
	 * @param parentsStage
	 */
	public void setParentsStage(Stage parentsStage) {
		this.parentsStage = parentsStage;
	}
	
	/**
	 * 设置当前页面所在的层
	 * @param currStage
	 */
	public void setCurrStage(Stage currStage) {
		this.currStage = currStage;
	}
	
	/**
	 * 获取父级页面所在的层
	 * @return
	 */
	public Stage getParentsStage() {
		return this.parentsStage;
	}
	
	/**
	 * 获取当前页面所在的层
	 * @return
	 */
	public Stage getCurrStage() {
		return this.currStage;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	
	
}
