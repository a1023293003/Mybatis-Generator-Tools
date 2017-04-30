package application;
	
import controller.BaseController;
import controller.MainFrameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import util.GlobalDto;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			// 把primaryStage传入全局数据传输对象
			GlobalDto.getDto().put("primaryStage", primaryStage);
			// 创建主界面
			new BaseController().createDialog(primaryStage, "MBG配置工具", "/ui/MainFrame.fxml");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
