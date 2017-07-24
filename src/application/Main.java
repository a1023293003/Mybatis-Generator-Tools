package application;
	
import controller.BaseController;
import javafx.application.Application;
import javafx.stage.Stage;
import util.ConfigProxyReader;
import util.GlobalDto;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			// 把primaryStage传入全局数据传输对象
			GlobalDto.getDto().put("primaryStage", primaryStage);
			// 创建主界面
			new BaseController().createDialog(
					primaryStage, 
					ConfigProxyReader.getMainFrameTitle(), 
					ConfigProxyReader.getDefaultMainFrameFxmlPath()
			);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
