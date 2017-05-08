package dbAction;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import po.TableField;
import util.Tools;



/**
 * MySql数据库操作
 * @author 随心
 *
 */
public class MySQLAction {

	/**
	 * slf4j日志配置
	 */
	private static final Logger _LOG = LoggerFactory.getLogger(MySQLAction.class);
	
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
	private String code = "useUnicode=true&" + 
			"characterEncoding=utf8&" +
			"useSSL=true&" +
			"rewriteBatchedStatements=true";
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
	public MySQLAction(String ip, String port, String database, 
			String user, String password, String code) throws Exception {
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
		return "jdbc:mysql://" + 
				ip + ":" + 
				port + "/" + 
				database + "?user=" + 
				user + "&password=" + 
				password + "&" + 
				code;
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
		return this.resultSetToList(
				this.executeQuery("mysql", "show databases"), 
				"database"
		);
	}
	
	/**
	 * 获取指定数据库、指定表的所有字段
	 * @param database [String]自定数据库
	 * @param table [String]指定表
	 * @return [List<TableField>]字段对象列表
	 */
	public List<TableField> getField(String database, String table) {
		return this.resultSetToList(
				this.executeQuery(database, "show full columns from " + table), 
				TableField.class, 
				new String[]{"field", "type", "key", "comment"}
		);
	}
	
	/**
	 * 读取指定数据库的所有表名
	 * @param database [String]数据库名
	 * @return [List<String>]表名列表
	 */
	public List<String> getTables(String database) {
		return this.resultSetToList(
				this.executeQuery(database, "show tables"), 
				"tables_in_" + database
		);
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
			String useDatabase = "use " + Tools.isNull(database);
			// 选定操作数据库
			PreparedStatement pstmt = Tools.isNull(
					this.connection).prepareStatement(useDatabase
			);
			pstmt.execute();
			// 执行操作语句并返回结果集合
			pstmt = Tools.isNull(this.connection).prepareStatement(sql);
			return pstmt.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 单个字段的ResultSet转List<String>
	 * @param rs [ResultSet]待转换结果集
	 * @param column [String]待转换字段
	 * @return [List<String>]结果
	 */
	private List<String> resultSetToList(ResultSet rs, String column) {
		try {
			// 指针跳转到ResultSet的最后一行数据，用于获取ResultSet中结果个数
			rs.last();
			// rs.getRow()获取当前行号
			List<String> res = new ArrayList<String>(rs.getRow());
			// 指针跳转到ResultSet的第一行之前
			rs.beforeFirst();
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
	 * ResultSet转List<T>
	 * @param rs [ResultSet]待转换结果集
	 * @param clazz [Class<T>]实体类对象的class
	 * @param column [String[]]待转换字段数组
	 * @return [List<T>]结果
	 */
	private <T> List<T> resultSetToList(ResultSet rs, Class<T> clazz, String[] columns) {
		try {
			// 指针跳转到ResultSet的最后一行数据，用于获取ResultSet中结果个数
			rs.last();
			// rs.getRow()获取当前行号
			List<T> resList = new ArrayList<T>(rs.getRow());
			// 指针跳转到ResultSet的第一行之前
			rs.beforeFirst();
			// 遍历ResultSet
			T entity = null;
			// 遍历ResultSet所有行
			while(rs.next()) {
				// 反射创建一个新的实体对象
				entity = clazz.newInstance();
				// 遍历所有字段
				for(String column : columns) {
					// 获取实体类中的属性的class，这里用getDeclaredField，因为属性字段是private修饰的
					Class<? extends Object> entityClass = clazz.getDeclaredField(column).getType();
					// 获取实体类中对应属性的set方法
					Method setter = null;
					try {
						// Integer.class
						setter = clazz.getMethod(
								"set" + Tools.capitalFirstChar(column), 
								entityClass
						);
					} catch(Exception e) {
						_LOG.info("查询实体类set{}({})失败", 
								Tools.capitalFirstChar(column), 
								entityClass
						);
						// 转成int.class
						setter = clazz.getDeclaredMethod(
								"set" + Tools.capitalFirstChar(column), 
								entityClass == Integer.class ? int.class : entityClass
						);
					}
					// 获取ResultSet中的get方法
					Method getter = rs.getClass().getMethod(
							this.typeToMethodString("get", entityClass.getTypeName()), 
							String.class
					);
					// 把ResultSet中get方法中获取的值set到实体对象中
					setter.invoke(entity, getter.invoke(rs, column));
				}
				// 最后把生成的对象存储到list中
				resList.add(entity);
			}
			return resList;
		} catch (Exception e) {
			_LOG.error("resultSetToList方法反射异常");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 通过基本类的全路径字符串转换成对应的set/get方法名
	 * 
	 * 例如给一个java.lang.String类的全路径，则会转换成getString
	 * 以此类推，唯一例外的是，java.lang.Integer会转成个getInt
	 * 
	 * @param prefix[String]方法名前缀：get/set
	 * @param fieldType [String]基本类的全路径
	 * @return [String]PreparedStatement中的set方法名
	 */
	private String typeToMethodString(String prefix, String fieldType) {
		// 类型转换字符串
		fieldType = fieldType.substring(fieldType.lastIndexOf(".") + 1);
		// 类型字符串转换成方法名
		fieldType = 
				prefix + 
				(fieldType.equals("Integer") || fieldType.equals("int") ? "Int" : fieldType);
		return fieldType;
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
