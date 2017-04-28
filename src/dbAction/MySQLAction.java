package dbAction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



/**
 * MySql数据库操作
 * @author 随心
 *
 */
public class MySQLAction {

	/**
	 * 主机名或IP地址
	 */
	private String ip = "localhost";
	/**
	 * 端口号
	 */
	private String port = "3306";
	/**
	 * 数据库
	 */
	private String database = "mysql";
	/**
	 * 用户名
	 */
	private String user = "root";
	/**
	 * 密码
	 */
	private String password = "root";
	/**
	 * 编码
	 */
	private String code = "useUnicode=true&characterEncoding=utf8&useSSL=true&rewriteBatchedStatements=true";
	/**
	 * 连接
	 */
	private Connection connection = null;
	
	/**
	 * TODO 测试用构造方法，待删除
	 * @throws Exception 
	 */
	public MySQLAction() throws Exception {
		this.init();
	}
	
	/**
	 * 带参数的构造方法
	 * @param ip [String]主机名或IP地址
	 * @param port [String]端口号
	 * @param database [String]数据库
	 * @param user [String]用户名
	 * @param password [String]密码
	 * @param code [String]编码格式
	 * @throws Exception 
	 */
	public MySQLAction(String ip, String port, String database, String user, String password, String code) throws Exception {
		super();
		this.ip = ip;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
		this.code = code;
		// 初始化操作
		this.init();
	}


	/**
	 * 整合连接语句
	 * @return
	 */
	private String uniteUrl(){
		System.out.println("正在构建url语句...");
		return "jdbc:mysql://"+ip+":"+port+"/"+database+"?user="+user+"&password="+password+"&"+code;
	}
	
	/**
	 * 初始化
	 * @throws Exception 
	 */
	private void init() throws Exception {
		System.out.println("正在加载数据库驱动...");
		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("正在连接数据库...");
		this.connection = DriverManager.getConnection(uniteUrl());
		System.out.println("数据库连接成功!");
	}
	
	/**
	 * 获取数据库中所有数据库名
	 * @return [List<String>]数据库名列表
	 */
	public List<String> getDatabases() {
		return this.resultSetToList(this.executeQuery("mysql", "show databases"), "database");
	}
	
	/**
	 * 获取指定数据库、指定表的所有字段
	 * @param database [String]自定数据库
	 * @param table [String]指定表
	 * @return [List<String>]字段列表
	 */
	public List<String> getField(String database, String table) {
		return this.resultSetToList(this.executeQuery(database, "describe " + table), "field");
	}
	
	/**
	 * 读取指定数据库的所有表名
	 * @param database [String]数据库名
	 * @return [List<String>]表名列表
	 */
	public List<String> getTables(String database) {
		return this.resultSetToList(this.executeQuery(database, "show tables"), "tables_in_" + database);
	}
	
	/**
	 * 指定数据库进行有返回值的数据库操作
	 * @param database [String]操作数据库
	 * @param sql [String]sql语句
	 * @return [ResultSet]返回结果集
	 */
	private ResultSet executeQuery(String database, String sql) {
		try {
			// 传入数据库不能为空
			String useDatabase = "use " + this.isNull(database);
			// 选定操作数据库
			PreparedStatement pstmt = this.isNull(this.connection).prepareStatement(useDatabase);
			pstmt.execute();
			// 执行操作语句并返回结果集合
			pstmt = this.isNull(this.connection).prepareStatement(sql);
			return pstmt.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * ResultSet转List<String>
	 * @param rs [ResultSet]待转换结果集
	 * @param column [String]待转换字段
	 * @return [List<String>]结果
	 */
	private List<String> resultSetToList(ResultSet rs, String column) {
		try {
			List<String> res = new ArrayList<String>();
			// 遍历ResultSet
			while(rs.next()) {
					res.add(rs.getString(column));
			}
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 判断传入对象是否为空
	 * @param obj [Object]传入对象
	 * @return [Object]传入对象
	 * @throws Exception 空指针异常
	 */
	private <T> T isNull(T obj) throws Exception {
		if(obj == null) {
			throw new NullPointerException("数据为NULL异常！");
		}
		return obj;
	}
	
	/**
	 * 释放connection
	 */
	public void free() {
		try {
			if(this.connection != null) {
				// 关闭数据库连接
				this.connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
