package dbAction;

import java.io.File;

import util.ConfigParser;

public class TestMain {
	public static void main(String[] args) throws Exception {
		File root = new File(System.getProperty("user.dir"));
		for(String file : root.list()) {
			System.out.println(file);
		}
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
