package test;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dbAction.MySQLAction;
import util.SQLProxyAction;

public class TestDemo {

	@Before
	public void init() {
	}
	
	@Test
	public void testDbAction() throws Exception {
		MySQLAction action = 
				SQLProxyAction.getMySQLAction("localhost", "3306", "test", "root", "root", "UTF-8");
		List<String> databases = action.getDatabases();
		System.out.println("共计" + databases.size() + "个数据库！");
		for(String database : databases) {
			System.out.println(database);
			List<String> tables = action.getTables(database);
			System.out.println("共计" + tables.size() + "个表！");
			for(String table : tables) {
				System.out.println(table);
			}
		}
	}
}
