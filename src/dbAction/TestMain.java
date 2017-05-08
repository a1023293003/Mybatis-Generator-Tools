package dbAction;

import java.util.List;

import util.ConfigProxyReader;
import util.TypeConverter;

public class TestMain {
	
	public static void add(List<String> array, int i) {
		array.add("abc" + i);
	}
	
	public static void main(String[] args) throws Exception {
		String[][] result = ConfigProxyReader.getMapperMethodsAndComments();
		for(int i = 0; i < result[0].length; i ++) {
			System.out.println("方法：[" + result[0][i] + "]");
			System.out.println("注释：[" + result[1][i] + "]");
		}
//		System.out.println(TypeConverter.jdbcTypeToJavaTypeQualifiedName("int"));
//		System.out.println(ConfigProxyReader.getCustomizedTableFieldsFixedCellSize());
//		Map<String, List<String>> array = new HashMap<String, List<String>>(0);
//		array.put("array", new ArrayList<String>(10));
//		for(int i = 1; i < 11; i ++) {
//			add(array.get("array"), i);
//		}
//		for(Entry<String, List<String>> entry : array.entrySet()) {
//			System.out.println(entry.getKey() + " : " + entry.getValue());
//		}
//		File root = new File(System.getProperty("user.dir"));
//		for(String file : root.list()) {
//			System.out.println(file);
//		}
//		MySQLAction action = new MySQLAction();
//		System.out.println("数据库=====================");
//		List<String> dbs = action.getDatabases();
//		for(String element : dbs) {
//			System.out.println(element);
//		}
//		System.out.println("表=====================");
//		dbs = action.getTables("hotel");
//		for(String element : dbs) {
//			System.out.println(element);
//		}
//		System.out.println("字段=====================");
//		List<TableField> fields = action.getField("hotel", "room");
//		for(TableField element : fields) {
//			System.out.println(element.toString());
//		}
//		action.free();
//		ConfigParser.getConfigParser().printAll();
//		ConfigParser.getConfigParser().updateToProperties("test3", "22212312333");;
//		ConfigParser.getConfigParser().printAll();
	}
}
