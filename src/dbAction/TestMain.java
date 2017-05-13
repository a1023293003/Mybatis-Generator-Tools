package dbAction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.ConfigProxyReader;
import util.TypeConverter;

public class TestMain {
	
	public static void add(List<String> array, int i) {
		array.add("abc" + i);
	}
	
	public static void main(String[] args) throws Exception {
		String test = 
				"\n	<!-- 注释 -->\n" +
				"	<sql id=\"$CustomizedId\">\n" +
				"		$CustomizedCollection\n" +
				"	<\\sql>";
		String test2 = ConfigProxyReader.getMapperXmlCriteria();
		System.out.println(test.replaceAll("(.*?)\\$CustomizedId([\\d\\D]*?)\\$CustomizedCollection(.*?)", "$1lalalalal$2kkkkk$3"));
		System.out.println("test2 : \n[" + test2.replaceAll("(.*?)\\$CustomizedId([\\d\\D]*?)\\$CustomizedCollection(.*?)", "$1lalalalal$2kkkkk$3") + "]");
//		String space = "			";
//		String test = 
//				"\t<if test=\"$CustomizedField != null\">\n" +
//				"\t	$CustomizedField,\n" +
//				"\t</if>\n";
//		System.out.println("替换结果：\n[" + test.replaceAll("\n*?(\t|^\t)",  space) + "]");
//		// 预定义一个set来过滤重复元素
//		Set<String> tags = new HashSet<String>();
//		// 定义正则匹配规则
//		Pattern pattern = Pattern.compile(".*?(	*?\\$WherePrimaryKeys.*?)");
//		String[] mapperXmlSqlTags = ConfigProxyReader.getMapperXmlSqlTags();
//		System.out.println("lalala");
//		for(String mapperXmlSqlTag : mapperXmlSqlTags) {
//			// 定义一个matcher来做匹配
//			Matcher matcher = pattern.matcher(mapperXmlSqlTag);
//			// 如果找到了
//			boolean isFind = matcher.find();
//			// 使用循环将句子里符合规则的子序列截取出来，存储到set集合里去重
//			while(isFind) {
//				System.out.println("检索到的前置空格类型：[" + matcher.group(1) + "]");
//				tags.add(matcher.group(1));
//				// 更新标记
//				isFind = matcher.find();
//			}
////			System.out.println("对应sql标签：[" + mapperXmlSqlTag + "]\n");
//		}
//		System.out.println("header = [" + ConfigProxyReader.getMapperXmlHeader() + "]");
//		String[][] result = ConfigProxyReader.getMapperMethodsAndComments();
//		result = ConfigProxyReader.getMapperMethodsAndComments();
//		result = ConfigProxyReader.getMapperMethodsAndComments();
//		for(int i = 0; i < result[0].length; i ++) {
//			System.out.println("方法：[" + result[0][i] + "]");
//			System.out.println("注释：[" + result[1][i] + "]");
//		}
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
