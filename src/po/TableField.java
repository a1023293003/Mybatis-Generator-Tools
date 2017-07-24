package po;

/**
 * 数据库表的字段对象
 * 
 * @author 随心
 *
 */
public class TableField {
	
	/**
	 * 字段名
	 */
	private String field = null;
	
	/**
	 * 字段类型
	 */
	private String type = null;
	
	/**
	 * 字段所对应的jdbc类型
	 */
	private String jdbcType = null;
	
	/**
	 * 键值类型:主键PRI、外键MUL、一般键为空
	 */
	private String key = null;
	
	/**
	 * 用户定制属性名
	 */
	private String customizedField = null;
	
	/**
	 * 字段类型对应的java类型
	 */
	private String javaType = null;

	/**
	 * 注释
	 */
	private String comment = null;
	
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCustomizedField() {
		return customizedField;
	}

	public String getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}

	public void setCustomizedField(String customizedField) {
		this.customizedField = customizedField;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "TableField [field=" + field + ", type=" + type + ", jdbcType=" + jdbcType + ", key=" + key
				+ ", customizedField=" + customizedField + ", javaType=" + javaType + ", comment=" + comment + "]";
	}
	
}
