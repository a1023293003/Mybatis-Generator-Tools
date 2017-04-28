package util;

import java.sql.Connection;

import dbAction.MySQLAction;

/**
 * MySQL数据库操作代理类
 * @author 随心
 *
 */
public class SQLProxyAction {
	
	/**
	 * 创建一个MySQL数据库操作对象
	 * @param ip [String]主机名或IP地址
	 * @param port [String]端口号
	 * @param database [String]数据库
	 * @param user [String]用户名
	 * @param password [String]密码
	 * @param code [String]编码格式
	 * @throws Exception 
	 */
	public static MySQLAction getMySQLAction(String ip, String port, String database, String user, String password, String code) throws Exception {
		code = "useUnicode=true&characterEncoding=" + code + "&useSSL=true&rewriteBatchedStatements=true";
		return new MySQLAction(ip, port, database, user, password, code);
	}
	
}
