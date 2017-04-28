package util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageGenerator {
	
	/**
	 * 数据库图标
	 */
	public static final String DATABASE = "/database.png";
	
	/**
	 * 数据库连接图标
	 */
	public static final String DATABASE_CONNECTION = "/database-connection.png";
	
	/**
	 * sql图标
	 */
	public static final String SQL = "/mysql.png";
	
	/**
	 * 表图标
	 */
	public static final String TABLE = "/table.png";
	
	/**
	 * 配置图标
	 */
	public static final String CONFIG = "/config.png";
	
	/**
	 * 等待图标
	 */
	public static final String LOADING = "/loading.gif";
	
	/**
	 * 询问图标
	 */
	public static final String ASK = "/ask.png";
	
	/**
	 * 警告图标
	 */
	public static final String WARNING = "/warning.png";
	
	/**
	 * ok图标
	 */
	public static final String OK = "/ok.png";
	
	/**
	 * 生成指定规格的图片
	 * @param path [String]图片路径
	 * @param width [int]图片宽度
	 * @param height [int]图片高度
	 * @return [ImageView]图片对象
	 */
	public static ImageView createImageView(String path, int width, int height) {
		ImageView imageView = new ImageView(new Image(path));
		imageView.setFitWidth(width);
		imageView.setFitHeight(height);
		return imageView;
	}
	
}
