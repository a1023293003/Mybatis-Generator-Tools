package util;

public class Tools {
	/**
	 * 判断数据是否为空
	 * @param data [<T>]待判断数据
	 * @return [<T>]data待判断数据
	 * @throws Exception 空指针异常
	 */
	public static <T> T isNull(T data) throws Exception {
		if(data == null) throw  new NullPointerException("数据为NULL");
		return data;
	}
}
