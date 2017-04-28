package dbAction;

import java.util.List;

import util.ConfigParser;


public class TestMain {
	public static void main(String[] args) throws Exception {
		MySQLAction action = new MySQLAction();
		System.out.println("数据库=====================");
		List<String> dbs = action.getDatabases();
		for(String element : dbs) {
			System.out.println(element);
		}
		System.out.println("表=====================");
		dbs = action.getTables("hotel");
		for(String element : dbs) {
			System.out.println(element);
		}
		System.out.println("字段=====================");
		dbs = action.getField("hotel", "room");
		for(String element : dbs) {
			System.out.println(element);
		}
		action.free();
//		ConfigParser.getConfigParser().printAll();
//		ConfigParser.getConfigParser().updateToProperties("test3", "22212312333");;
//		ConfigParser.getConfigParser().printAll();
	}
}
