package util;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>全局数据传输对象<p>
 * 
 * <pre>
 * 1、用于界面之间夸界面传输，可以说是对于界面控制器中的dto的一个补充
 * 2、适用于对非界面类进行数据传输，比如为为非界面类提供parentsStage，用于弹出异常提示
 * </pre>
 * 
 * @author 随心
 *
 */
public class GlobalDto {

	/**
	 * 数据传输Map
	 */
	private Map<Object, Object> dto = new HashMap<Object, Object>();
	
	/**
	 * 单例化
	 */
	private static GlobalDto globalDto = new GlobalDto();
	
	private GlobalDto(){};
	
	/**
	 * 获取数据传输Map
	 */
	public static Map<Object, Object> getDto() {
		return globalDto.dto;
	}
	
}
