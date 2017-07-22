package core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import po.TableConfig;
import po.TableField;
import util.ConfigProxyReader;
import util.Tools;
import util.TypeConverter;

/**
 * po类生成器
 * 
 * @author 随心
 *
 */
public class CodeGenerator {

	/**
	 * slf4j日志配置
	 */
	private static final Logger _LOG = LoggerFactory.getLogger(CodeGenerator.class);
	
	/**
	 * mapper方法和注释
	 */
	private static String[][] mapperMethodsAndComments = null;
	
	/**
	 * mapper.xml对应mapper中每个方法对应的sql标签
	 */
	private static String[] mapperXmlSqlTags = null;
	
	static {
		// 从配置文件中读取mapper的方法名和配置信息
		mapperMethodsAndComments = ConfigProxyReader.getMapperMethodsAndComments();
		// 从配置文件中读取mapper.xml对应mapper中每个方法对应的sql标签
		mapperXmlSqlTags = ConfigProxyReader.getMapperXmlSqlTags();
	}
	
	/**
	 * 生成代码
	 * 
	 * @param tableConfigs [Map<String, TableConfig>]所有表的配置信息
	 * @throws Exception 
	 */
	public static synchronized void generatingCode(Map<String, TableConfig> tableConfigs) throws Exception {
		_LOG.info("开始生成代码...");
		// 生成po类
		generatingPoClass(tableConfigs);
		// 生成mapper类
		generatingMapperClass(tableConfigs);
		// 生成xml文件
		generatingMapperXml(tableConfigs);
		// 生成example类
		generatingExampleClass(tableConfigs);
	}
	
	/**
	 * 生成example类
	 * 
	 * @param tableConfigs [Map<String, TableConfig>]所有表的配置信息
	 * @throws Exception 
	 */
	private static void generatingExampleClass(Map<String, TableConfig> tableConfigs) throws Exception {
		_LOG.info("开始生成example类");
		try {
			// 遍历所有表的配置，每张表生成一个example类
			for(TableConfig tableConfig : tableConfigs.values()) {
				// 判断是否生成example
				if(!tableConfig.isBuildNote()) continue;
				// 获取文件输出流
				PrintWriter writer = getPrintWriter(tableConfig.getPojoPath() + "\\" + tableConfig.getPojoClassName() + "Example.java");
				// 设置自身包名
				writer.println("package " + tableConfig.getPojoPackage() + ";\n");
				// 获取需要引入的类的全路径
				writer.print(getImportFieldsJavaTypeQualifiedName(tableConfig));
				writer.println("import java.util.ArrayList;");
				writer.println("import java.util.List;");
				// 类名
				writer.print("\npublic class " + tableConfig.getPojoClassName() + "Example {\n");
				// 读取基础模板，判断是否保留注释
				String exampleBaseTemplate = ConfigProxyReader.getExampleClassBaseTemplate();
				if(!tableConfig.isBuildNote()) exampleBaseTemplate.replaceAll("[\t ]*?/\\*\\*[\\d\\D]*?\\*/\n", "");
				// 写入基础模板，替换example类名
				writer.println(exampleBaseTemplate.replace("$ExampleName", tableConfig.getPojoClassName() + "Example"));
				// example类中的GeneratedCriteria类
				writer.print(generatingGeneratedCriteriaClass(tableConfig));
				// 是否保留注释
				// 类结束右括号
				writer.print("\n}"); 
				// 强制输出，清空缓存
				writer.flush();
				// 关闭输出流
				writer.close();
				_LOG.info("成功生成{}Example类！", tableConfig.getPojoClassName());
			}
		} catch(Exception e) {
			_LOG.error("生成example类出错！");
			// 抛出异常
			throw e;
		}
	}
	
	/**
	 * 生成mapper.xml配置文件
	 * 
	 * @param tableConfigs [Map<String, TableConfig>]所有表的配置信息
	 * @throws Exception 
	 */
	private static void generatingMapperXml(Map<String, TableConfig> tableConfigs) throws Exception {
		_LOG.info("开始生成xml文件");
		try {
			// 遍历所有表的配置，每张表生成一个mapper.xml配置文件
			for(TableConfig tableConfig : tableConfigs.values()) {
				// 获取文件输出流
				PrintWriter writer = getPrintWriter(tableConfig.getMapperPath() + "\\" + tableConfig.getMapperClassName() + ".xml");
				// xml头部信息
				writer.println(ConfigProxyReader.getMapperXmlHeader());
				// 根标签
				writer.println("<mapper namespace=\"" + tableConfig.getMapperPackage() + "." + tableConfig.getMapperClassName() + "\" >\n");
				// ResultMap
				writer.append(generatingResultMap(tableConfig));
				// 配置文件中读取自定义条件读取代码块
				String criteria = ConfigProxyReader.getMapperXmlCriteria();
				// 判断是否生成注释
				if(!tableConfig.isBuildNote()) criteria = criteria.replaceAll("<!-- .*? -->\n", "");
				// 通用的自定义条件标签
				writer.println(criteria.replaceAll(
						"(.*?)\\$CustomizedId([\\d\\D]*?)\\$CustomizedCollection(.*?)", 
						"$1Example_Where_Clause$2oredCriteria$3"
				));
				// update专用的自定义条件标签
				writer.println(criteria.replaceAll(
						"(.*?)\\$CustomizedId([\\d\\D]*?)\\$CustomizedCollection(.*?)", 
						"$1Update_By_Example_Where_Clause$2example.oredCriteria$3"
				));
				// 基础字段
				writer.print(replaceKeyWords0(tableConfig, ConfigProxyReader.getMapperXmlBaseColunmList()));
				// 各个方法对应的sql标签
				writer.print(generatingSqlTags(tableConfig));
				// 根标签结束
				writer.print("</mapper>");
				// 强制输出，清空缓存
				writer.flush();
				// 关闭输出流
				writer.close();
				_LOG.info("成功生成{}.xml文件！", tableConfig.getMapperClassName());
			}
		} catch(Exception e) {
			_LOG.error("生成xml文件出错！");
			// 抛出异常
			throw e;
		}
	}

	
	/**
	 * 生成mapper类
	 * 
	 * @param tableConfigs [Map<String, TableConfig>]所有表的配置信息
	 * @throws Exception 
	 */
	private static void generatingMapperClass(Map<String, TableConfig> tableConfigs) throws Exception {
		_LOG.info("开始生成mapper类");
		try {
			// 遍历所有表的配置，每张表生成一个mapper类
			for(TableConfig tableConfig : tableConfigs.values()) {
				// 获取文件输出流
				PrintWriter writer = getPrintWriter(tableConfig.getMapperPath() + "\\" + tableConfig.getMapperClassName() + ".java");
				// 设置自身包名
				writer.println("package " + tableConfig.getMapperPackage() + ";\n");
				// 需要引入类的全路径
				writer.println("import " + tableConfig.getPojoPackage() + 
						(tableConfig.getPojoPackage() != null ? "." : "") + tableConfig.getPojoClassName() + ";");
				if(tableConfig.isBuildExample()) {
					writer.println("import " + tableConfig.getPojoPackage() + 
							(tableConfig.getPojoPackage() != null ? "." : "") + tableConfig.getPojoClassName() + "Example;");					
				}
				// 获取需要引入的类的全路径
				writer.print(getImportFieldsJavaTypeQualifiedName(tableConfig));
				writer.println("import java.util.List;");
				// mybatis参数注解解析包名
				writer.println("import org.apache.ibatis.annotations.Param;");
				// 接口名
				writer.print("\npublic interface " + tableConfig.getMapperClassName() + " {\n");
				// 方法
				writer.print(generatingMapperMethods(tableConfig));
				// 类结束右括号
				writer.print("\n}");
				// 强制输出，清空缓存
				writer.flush();
				// 关闭输出流
				writer.close();
				_LOG.info("成功生成{}类！", tableConfig.getMapperClassName());
			}
		} catch(Exception e) {
			_LOG.error("生成mapper类出错！");
			// 抛出异常
			throw e;
		}
	}

	/**
	 * 生成pojo类
	 * 
	 * @param tableConfigs [Map<String, TableConfig>]所有表的配置信息
	 * @throws Exception 
	 */
	private static void generatingPoClass(Map<String, TableConfig> tableConfigs) throws Exception {
		_LOG.info("开始生成pojo类...");
		try {
			// 遍历所有表的配置，每张表都生成一个po类
			for(TableConfig tableConfig : tableConfigs.values()) {
				// 获取文件输出流
				PrintWriter writer = getPrintWriter(tableConfig.getPojoPath() + "\\" + tableConfig.getPojoClassName() + ".java");
				// 属性字段
				StringBuffer properties = new StringBuffer("");
				// getter和setter方法
				StringBuffer getterAndSetter = new StringBuffer("");
				// 设置自身包名
				writer.println("package " + tableConfig.getPojoPackage() + ";\n");
				// 获取需要引入的类的全路径
				writer.print(getImportFieldsJavaTypeQualifiedName(tableConfig));
				// 生成属性和对应的getterhesetter方法
				for(TableField field : tableConfig.getFields()) {
					// 插入属性字段
					properties.append(generatingProperty(field.getJavaType(), field.getCustomizedField(), field.getComment(), tableConfig.isBuildNote()));
					// 生成getter和setter方法
					getterAndSetter.append(generatingGetterAndSetterMethod(field.getJavaType(), field.getCustomizedField()));
				}
				// 类名
				writer.print("\npublic class " + tableConfig.getPojoClassName() + " {\n");
				// 属性
				writer.print(properties.toString());
				// getter和setter方法
				writer.print(getterAndSetter.toString());
				// 类结束右括号
				writer.print("\n}");
				// 强制输出，清空缓存
				writer.flush();
				// 关闭输出流
				writer.close();
				_LOG.info("成功生成{}类！", tableConfig.getPojoClassName());
			}
			_LOG.info("pojo类生成完毕！");
		} catch(Exception e) {
			_LOG.error("生成pojo类出错！");
			// 抛出异常
			throw e;
		}
	}
	
	/**
	 * 传入路径获取该文件对象的输出流，如果文件不存在则会先创建文件
	 * 
	 * @param path [String]文件路径
	 * @return [PrintWriter]文件输出流
	 * @throws IOException 
	 */
	private static PrintWriter getPrintWriter(String path) throws IOException {
		// 判断文件是否已经存在，如果不存在则创建
		File file = new File(path);
		if(!file.exists()) file.createNewFile();
		return new PrintWriter(file, "UTF-8");
	}
	
	/**
	 * 获取所有属性需要引入的import语句
	 * @param tableConfig [TableConfig]表的配置信息
	 * @return [String]可以直接插入的所有属性的import语句的字符串
	 */
	private static String getImportFieldsJavaTypeQualifiedName(TableConfig tableConfig) {
		// 带缓存的返回结果字符串
		StringBuffer result = new StringBuffer();
		// set集合用于去重
		Set<String> filter = new HashSet<String>();
		// 获取需要引入的类的全路径
		for(TableField field : tableConfig.getFields()) {
			// 获取java类型类的全路径
			String typeQualifiedClass = TypeConverter.getClassNameOrQualifiedName(field.getJavaType());
			// java.lang.*不需要引入，程序会自动引入
			if(!typeQualifiedClass.startsWith("java.lang")) {
				filter.add("import " + typeQualifiedClass + ";\n");
			}
		}
		// 拼接字符串
		for(String element : filter) {
			result.append(element);
		}
		return result.toString();
	}
	
	/**
	 * 生成example中的GeneratedCriteria类
	 * 
	 * @param tableConfig [TableConfig]表的配置信息
	 * @return [String]可以直接插入PrintWriter中的GeneratedCriteria类
	 */
	private static String generatingGeneratedCriteriaClass(TableConfig tableConfig) {
		// 读取criteria生成类基础模块
		String baseTemplate = ConfigProxyReader.getExampleGeneratedCriteriaBaseTemplate();
		// 读取criteria生成类各个属性模板方法
		String fieldMethods = ConfigProxyReader.getExampleGeneratedCriteriaFieldMethods();
		// 判断是否需要滤掉注释
		if(!tableConfig.isBuildNote()) {
			baseTemplate = baseTemplate.replaceAll("[\t ]*?/\\*\\*[\\d\\D]*?\\*/\n", "");
			fieldMethods = fieldMethods.replaceAll("[\t ]*?/\\*\\*[\\d\\D]*?\\*/\n", "");
		}
		// 基础模板中的占位符$ExampleCriteriaMethods替换成各个属性的模板方法
		return baseTemplate.replace(
				"$ExampleCriteriaMethods", 
				concatBaseTemplate(tableConfig, fieldMethods, true, true, "", "", "", "")
		);
	}
	
	/**
	 * 生成mapper.xml对应mapper中每个方法对应的sql标签
	 * 
	 * @param tableConfig [TableConfig]表的配置信息
	 * @return [String]可以直接插入PrintWriter中的sql标签
	 */
	private static String generatingSqlTags(TableConfig tableConfig) {
		// 存储sql标签的带缓存的字符串
		StringBuffer sqlTags = new StringBuffer();
		// 把所有方法的sql标签拼接起来
		for(String sqlTag : mapperXmlSqlTags) {
			// 不存在主键则把主键方法都过滤掉
			if(!tableConfig.isExistsPrimaryKey() && sqlTag.contains("ByPrimaryKey")) continue;
			// 不生成example的滤掉涉及Example的方法
			if(!tableConfig.isBuildExample() && sqlTag.contains("ByExample")) continue;
			sqlTags.append(sqlTag + "\n");
		}
		// 是否生成注释
		if(!tableConfig.isBuildNote()) sqlTags = new StringBuffer(sqlTags.toString().replaceAll("<!-- \\.+? -->\n", ""));
		// 替换字符串中的关键字
		return replaceKeyWords0(tableConfig, sqlTags.toString());
	}
	
	/**
	 * 生成mapper.xml中的关于表的字段和实体类属性的resultMap映射
	 * 
	 * @param tableConfig [TableConfig]表的配置信息
	 * @return [String]可以直接插入PrintWriter中的ResultMap字符串
	 */
	private static String generatingResultMap(TableConfig tableConfig) {
		// 存储resultMap的带缓存的字符串
		StringBuffer resultMap = new StringBuffer();
		// 标识符
		StringBuffer ids = new StringBuffer();
		// 其它字段
		StringBuffer results = new StringBuffer();
		// 判断是否需要添加注释
		if(tableConfig.isBuildNote()) {
			resultMap.append("	<!-- pojo类属性和对应表的结果集映射关系 -->\n");
		}
		// ResultMap根标签
		resultMap.append("	<resultMap id=\"BaseResultMap\" type=\"" + tableConfig.getPojoPackage() + "." + tableConfig.getPojoClassName() + "\">\n");
		// 遍历所有属性
		for(TableField field : tableConfig.getFields()) {
			if(field.getKey().equals("PRI")) {
				// 主键作为标识符
				ids.append("		<id column=\"" + 
						field.getField() + "\" property=\"" + 
						field.getCustomizedField() + "\" jdbcType=\"" + 
						field.getJdbcType() + "\" />\n"
				);
			} else {
				// 其它字段
				results.append("		<result column=\"" + 
						field.getField() + "\" property=\"" + 
						field.getCustomizedField() + "\" jdbcType=\"" + 
						field.getJdbcType() + "\" />\n"
				);
			}
		}
		// ResultMap子标签
		resultMap.append(ids);
		resultMap.append(results);
		// ResultMap结束根标签
		resultMap.append("	</resultMap>\n");
		
		
		return resultMap.toString();
	}
	
	/**
	 * 生成mapper方法
	 * 
	 * @param tableConfig [TableConfig]mapper对应表的所有配置
	 * @return [String]可以直接写入PrintWriter的mapper方法字符串
	 */
	private static String generatingMapperMethods(TableConfig tableConfig) {
		// 存储方法的带缓存的字符串
		StringBuffer methods = new StringBuffer("");
		// 遍历mapper方法和注释信息
		String method = null;
		String comment = null;
		// 生成方法
		for(int i = 0; i < mapperMethodsAndComments[0].length; i ++) {
			// 读取方法和注释
			method = mapperMethodsAndComments[0][i];
			comment = mapperMethodsAndComments[1][i];
			// 判断是否需要滤掉example
			if(!tableConfig.isBuildExample() && method.contains("Example")) continue;
			// 如果表中没有主键，滤掉有关主键的操作
			if(!tableConfig.isExistsPrimaryKey() && method.contains("PrimaryKey")) continue;
			// 替换关键字
			method = repalceKeyWords(tableConfig, method);
			comment = repalceKeyWords(tableConfig, comment);
			// 插入换行
			methods.append("\n");
			// 判断是否需要注释
			if(tableConfig.isBuildNote()) methods.append(generatingComment(tableConfig, comment));
			// 插入方法
			methods.append("	" + method + "\n");
		}
		
		return methods.toString();
	}
	
	/**
	 * mapper.xml配置内容替换字符串中的关键字
	 * 
	 * @param tableConfig [TableConfig]表的配置信息
	 * @param data [String]待替换字符串
	 * @return [String]替换关键字后的字符串
	 */
	private static String replaceKeyWords0(TableConfig tableConfig, String data) {
//		_LOG.info("替换前：{}", data);
		// 替换example类的全路径
		data = data.replace("$ExampleQualifiedName", 
				tableConfig.getPojoPackage() + "." + tableConfig.getPojoClassName() + "Example");
		// 替换pojo类的全路径
		data = data.replace("$PojoQualifiedName", 
				tableConfig.getPojoPackage() + "." + tableConfig.getPojoClassName());
		// 替换表名
		data = data.replace("$Table", tableConfig.getTableName());
		// 替换where中的主键名
		if(data.contains("$WherePrimaryKeys")) {
			// 占位符替换成基础模板
			data = getPrefixSpaceData("$WherePrimaryKeys", 
					concatBaseTemplate(tableConfig, "\t$Field = #{$CustomizedField, jdbcType=$JdbcType} and\n", 
							true, false, "", "", "", "and\n"), 
					data
			);
		}
		// 插入语句的字段
		if(data.contains("$InsertFields")) {
			// 占位符替换成基础模板
			data = getPrefixSpaceData("$InsertFields", 
					concatBaseTemplate(tableConfig, "\t$Field,\n", 
							true, true, "", "", "", ",\n"), 
					data
			);
		}
		// 插入语句的value
		if(data.contains("$InsertValues")) {
			// 占位符替换成基础模板
			data = getPrefixSpaceData("$InsertValues", 
					concatBaseTemplate(tableConfig, "\t#{$CustomizedField, jdbcType=$JdbcType},\n", 
							true, true, "", "", "", ",\n"), 
					data
			);
		}
		// 有选择的插入语句的字段
		if(data.contains("$InsertSelectiveFields")) {
			// 占位符替换成基础模板
			data = getPrefixSpaceData("$InsertSelectiveFields", 
					concatBaseTemplate(tableConfig, 
							"\t<if test=\"$CustomizedField != null\">\n" +
							"\t	$CustomizedField,\n" +
							"\t</if>\n", 
							true, true, "", "", "", "\n"), 
					data
			);
		}
		// 有选择的插入语句的value
		if(data.contains("$InsertSelectiveValues")) {
			// 占位符替换成基础模板
			data = getPrefixSpaceData("$InsertSelectiveValues", 
					concatBaseTemplate(tableConfig, 
							"\t<if test=\"$CustomizedField != null\">\n" +
							"\t	#{$CustomizedField, jdbcType=$JdbcType},\n" +
							"\t</if>\n", 
							true, true, "", "", "", "\n"), 
					data
			);
		}
		// 有选择的更新语句的字段
		if(data.contains("$UpdateSelectiveFields")) {
			// 占位符替换成基础模板
			data = getPrefixSpaceData("$UpdateSelectiveFields", 
					concatBaseTemplate(tableConfig, 
							"\t<if test=\"record.$CustomizedField != null\">\n" +
							"\t	$CustomizedField = #{record.$CustomizedField, jdbcType=$JdbcType},\n" +
							"\t</if>\n", 
							true, true, "", "", "", "\n"), 
					data
			);
		}
		// 有选择的更新除主键之外的字段
		if(data.contains("$UpdateNotPrimaryKeySelectiveFields")) {
			// 占位符替换成基础模板
			data = getPrefixSpaceData("$UpdateNotPrimaryKeySelectiveFields", 
					concatBaseTemplate(tableConfig, 
							"\t<if test=\"$CustomizedField != null\">\n" +
							"\t	$CustomizedField = #{$CustomizedField, jdbcType=$JdbcType},\n" +
							"\t</if>\n", 
							false, true, "", "", "", "\n"), 
					data
			);
		}
		// 更新的所有字段
		if(data.contains("$UpdateFields")) {
			// 占位符替换成基础模板
			data = getPrefixSpaceData("$UpdateFields", 
					concatBaseTemplate(tableConfig, "\t$CustomizedField = #{$CustomizedField, jdbcType=$JdbcType},\n", 
							true, true, "", "", "", ",\n"), 
					data
			);
		}
		// 更新除主键之外的所有字段
		if(data.contains("$UpdateNotPrimaryKeyFields")) {
			// 占位符替换成基础模板
			data = getPrefixSpaceData("$UpdateNotPrimaryKeyFields", 
					concatBaseTemplate(tableConfig, "\t$CustomizedField = #{$CustomizedField, jdbcType=$JdbcType},\n", 
							false, true, "", "", "", ",\n"), 
					data
			);
		}
//		_LOG.info("替换后：{}", data);
		return data;
	}
	
	/**
	 * 生成占位符替换的基础模板
	 * 
	 * <pre>
	 * 通过传入的baseTemplate（基础模板中每一行的模板），遍历表的所有字段，每个字段都生成一个对应模板的内容，然后拼接成基础模板。
	 * 传入的baseTemplate中可以替换的内容：
	 * $Field : 数据库字段名
	 * $CustomizedField : 用户自定义字段名
	 * $CapitalFirstCharCustomizedField : 首字母大写的用户自定义字段名
	 * $JdbcType : 字段的jdbc类型
	 * $JavaType : 字段的java类型
	 * </pre>
	 * 
	 * @param tableConfig [TableConfig]表的配置信息
	 * @param baseTemplate [String]基础模板中每一行的模板
	 * @param isIncludePrimaryKey [boolean]是否包含主键字段
	 * @param isIncludeCommonField [boolean]是否包含主键之外的字段
	 * @param prefix [String]基础模板前缀
	 * @param suffix [String]基础模板后缀
	 * @param prefixOverrides [String]基础模板生成后去除一个前缀
	 * @param suffixOverrides [String]基础模板生成后去除一个后缀
	 * @return [String]可以直接用于替换占位符的基础模板字符串
	 */
	private static String concatBaseTemplate(TableConfig tableConfig, String baseTemplate, 
			boolean isIncludePrimaryKey, boolean isIncludeCommonField, 
			String prefix, String suffix, String prefixOverrides, String suffixOverrides) {
		// 存储结果的带缓存的字符串
		StringBuffer buf = new StringBuffer(prefix);
		// 返回值
		String result = null;
		// 构建占位符替换的基础模板
		boolean isPrimaryKey = false;
		String temp = null;
		for(TableField field : tableConfig.getFields()) {
			isPrimaryKey = field.getKey().equals("PRI");
			// 判断是否需要滤掉主键
			if(!isIncludePrimaryKey && isPrimaryKey) continue;
			// 判断是否需要滤掉普通字段
			if(!isIncludeCommonField && !isPrimaryKey) continue;
			// 表中字段名
			temp = baseTemplate.replace("$Field", field.getField());
			// 字段对应的java属性名
			temp = temp.replace("$CustomizedField", field.getCustomizedField());
			// 字段对应的首字母大写的java属性名
			temp = temp.replace("$CapitalFirstCharCustomizedField", Tools.capitalFirstChar(field.getCustomizedField()));
			// 字段的jdbc类型
			temp = temp.replace("$JdbcType", field.getJdbcType());
			// 字段的java类型
			temp = temp.replace("$JavaType", field.getJavaType());
			// 追加数据
			buf.append(temp);
		}
		// 后缀
		buf.append(suffix);
		// 赋值结果
		result = buf.toString();
		// 去掉前缀
		if(prefixOverrides.length() > 0 && result.startsWith(prefix + prefixOverrides)) {
			result = result.replaceAll("^(" + prefix + ")" + prefixOverrides + "(.*)", "$1$2");
		}
		// 去掉后缀
		if(suffixOverrides.length() > 0 && result.endsWith(suffix + suffixOverrides)) {
			result = result.replaceAll("(.*?)" + suffixOverrides + "(" + suffix + ")$", "$1$2");
		}
		return result;
	}
	
	/**
	 * 获取指定占位符的前置空格个数类型，
	 * 然后根据传入的基础占位符替换模板，规格化替换总数据中的占位符，
	 * 使得一行占位符替换多行数据时，每一行都有相同的前置空格。
	 * 主要用于占位符代表多行数据时规格化输出。
	 * 
	 * <pre>
	 * 例如$WherePrimaryKeys占位符前面有1个\t的情况，和2个\t的情况，
	 * 于是数据中\t$WherePrimaryKeys和\t\t$WherePrimaryKeys，分别替换成前置空格为\t和\t\t的基础模板数据。
	 * 
	 * \t$WherePrimaryKeys替换为：
	 * \t#id = #{id, jdbcType=bigint} and
	 * \t#name = #{name, jdbcType=varchar}
	 * 
	 * \t\t$WherePrimaryKeys替换为：
	 * \t\t#id = #{id, jdbcType=bigint} and
	 * \t\t#name = #{name, jdbcType=varchar}
	 * </pre>
	 * 
	 * @param placeholder [String]待检测占位符，以$开头
	 * @param baseTemplate [String]占位符所表示的内容的基础模板，用\t代表各个前置空格的类型
	 * @param data [String]待查询数据
	 * @return [Set<String>]查询数据中指定占位符前置空格的个数类型集合
	 */
	private static String getPrefixSpaceData(String placeholder, String baseTemplate, String data) {
		// 预定义一个set来过滤重复元素
		Set<String> tags = new HashSet<String>();
		// 定义正则匹配规则
		Pattern pattern = Pattern.compile(".*?(	*?)\\" + placeholder);
		// 定义一个matcher来做匹配
		Matcher matcher = pattern.matcher(data);
		// 如果找到了
		boolean isFind = matcher.find();
		// 使用循环将句子里符合规则的子序列截取出来，存储到set集合里去重
		while(isFind) {
			tags.add(matcher.group(1));
			// 更新标记
			isFind = matcher.find();
		}
		// 遍历所有前置空格类型，替换掉
		for(String space : tags) {
			// 第一行的\t
			baseTemplate = baseTemplate.replaceAll("^\t", space);
			// 后面的\t
			data = data.replaceAll(".*?(" + space + "\\" + placeholder + ")", baseTemplate.replace("\n\t", "\n" + space));
		}
		return data;
	}
	
	/**
	 * mapper类替换字符串中的关键字
	 * 
	 * @param tableConfig [TableConfig]表的配置信息
	 * @param data [String]待替换字符串
	 * @return [String]替换关键字后的字符串
	 */
	private static String repalceKeyWords(TableConfig tableConfig, String data) {
//		_LOG.info("替换前：{}", data);
		// 替换example的类名
		data = data.replace("$ExampleName", tableConfig.getPojoClassName() + "Example");
		// 替换pojo类名
		data = data.replace("$PojoName", tableConfig.getPojoClassName());
		// 替换方法字符串中的主键参数
		if(data.contains("$PrimaryKeys") || data.contains("$PrimaryKeyComments")) {
			// 参数中的主键属性
			StringBuffer params = new StringBuffer();
			// 注释中的主键属性注释
			StringBuffer comments = new StringBuffer();
			// 前面是否已经有录入过属性的标记
			boolean prefixExistsParam = false;
			// 遍历所有属性，找出主键属性，录入
			for(TableField field : tableConfig.getFields()) {
				// 主键
				if(field.getKey().equals("PRI")) {
					// 不是拼接第一个属性
					if(prefixExistsParam) {
						params.append(", ");
					}
					// 参数属性拼接
					params.append("@Param(\"" + field.getCustomizedField() + "\") " + field.getJavaType() + " " + field.getCustomizedField());
					// 主键注释拼接
					comments.append(field.getCustomizedField() + " [" + field.getJavaType() + "](主键属性)" + field.getComment());
					// 修改标记
					prefixExistsParam = true;
				}
			}
			// 替换参数
			data = data.replace("$PrimaryKeys", params.toString());
			// 替换注释
			data = data.replace("$PrimaryKeyComments", comments.toString());
		}
//		_LOG.info("替换后：{}", data);
		return data;
	}
	
	/**
	 * 生成注释
	 * 
	 * @param tableConfig [TableConfig]表的配置信息
	 * @param commentDatas [String]注释信息字符串:注释,描述,返回值类型及其说明,...若干参数类型及其说明
	 * @return [String]可以直接写入PrintWriter的注释字段
	 */
	private static String generatingComment(TableConfig tableConfig, String commentDatas) {
		System.out.println("generatingComment生成注释信息：[" + commentDatas + "]");
		// 注释数据无效
		if(!Tools.isValid(commentDatas)) return "";
		// 分割注释信息
		String[] commentsBuf = commentDatas.split(",");
		// 数据不合法异常
		if(commentsBuf.length < 4) {
			_LOG.error("mapper注释不合法异常！");
			return "";
		}
		// 若干参数类型及其说明
		String params[] = new String[commentsBuf.length - 3];
		// 提取出若干参数类型及其说明
		for(int i = 3; i < commentsBuf.length; i ++) {
			params[i - 3] = commentsBuf[i];
		}
		// 生成注释并返回
		return generatingComment(commentsBuf[0], commentsBuf[1], commentsBuf[2], params);
	}
	
	/**
	 * 生成属性
	 * 
	 * @param type [String]属性类型
	 * @param name [String]属性名
	 * @param comment [String]注释
	 * @param isBuildNote [String]是否生成注释
	 * @return [String]可以直接写入PrintWriter的属性字符串
	 */
	private static String generatingProperty(String type, String name, String comment, boolean isBuildNote) {
		// 存储属性的带缓存的字符串
		StringBuffer property = new StringBuffer("\n");
		// 判断是否需要生成注释
		if(isBuildNote) {
			property.append(generatingComment(comment, null, null, null));
		}
		// 属性
		property.append("	private " + type + " " + name + " = null;\n");
		return property.toString();
	}
	
	/**
	 * 生成注释
	 * 
	 * @param comment [String]注释首行，简述方法功能
	 * @param describe [String]方法描述，额外添加一些方法说明
	 * @param returnDescribe [String]返回值类型及其含义说明
	 * @param params [String[]]各属性说明
	 * @return [String]可以直接写入PrintWriter的注释字段
	 */
	private static String generatingComment(String comment, String describe, String returnDescribe, String[] params) {
		// 没有注释
		if(!Tools.isValid(comment)) return "";
		// 属性注释和返回值注释前置换行标记
		boolean newLine = true;
		// 存储生成注释的带缓存的字符串
		StringBuffer comments = new StringBuffer("");
		// 注释
		comments.append(
				"	/**\n" + 
				"	 * " + comment + "\n"
		);
		// 描述
		if(Tools.isValid(describe)) {
			comments.append(
					"	 *\n" +
					"	 * <pre>\n" +
					"	 * " + describe + "\n" +
					"	 * </pre>\n"
			);
		}
		// 各属性说明
		if(params != null) {
			for(String param : params) {
				if(Tools.isValid(param)) {
					// 判断是否需要加上前置换行
					if(newLine) {
						comments.append("	 *\n");
						newLine = false;
					}
					comments.append("	 * @param " + param + "\n");
				}
			}
		}
		// 返回值类型及其含义说明
		if(Tools.isValid(returnDescribe)) {
			// 判断是否需要加上前置换行
			if(newLine) {
				comments.append("	 *\n");
				newLine = false;
			}
			comments.append("	 * @return " + returnDescribe + "\n");
		}
		// 注释结尾
		comments.append("	 */\n");
		return comments.toString();
	}
	
	/**
	 * 生成对应属性的getter和setter方法
	 * 
	 * @param type [String]属性类型
	 * @param name [String]属性名
	 * @return [String]可以直接写入PrintWriter的getter和setter方法字符串
	 */
	private static String generatingGetterAndSetterMethod(String type, String name) {
		// 存储生成的getter和setter方法的带缓存的字符串
		StringBuffer getterAndSetter = new StringBuffer("");
		
		getterAndSetter.append(
				"\n	public void set" + Tools.capitalFirstChar(name) + 
				"(" + type + " " + name + ") {\n" +
				"		this." + name + " = " + name + ";\n" + 
				"	}\n"
		);
		// boolean和Boolean的get方法是is开头
		getterAndSetter.append(
				"\n	public " + type + 
				// 这里可以用type.endsWith("oolean")代替，然而却总感觉不安全
				((type.endsWith("boolean") || type.equals("Boolean")) ? " is" : " get") + 
				Tools.capitalFirstChar(name) + "() {\n" +
				"		return " + "this." + name + ";\n" + 
				"	}\n"
		);
		
		return getterAndSetter.toString();
	}
	
}
