package po;

import java.util.List;

/**
 * 每张表的配置信息bean
 * @author 随心
 *
 */
public class TableConfig {

	/**
	 * 表名
	 */
	private String tableName = null;
	
	/**
	 * 表的字段信息
	 */
	private List<TableField> fields = null;
	
	/**
	 * 是否生成example类
	 */
	private boolean isBuildExample = true;
	
	/**
	 * 是否生成注释
	 */
	private boolean isBuildNote = true;
	
	/**
	 * 是否存在主键
	 */
	private boolean isExistsPrimaryKey = false;
	
	/**
	 * table对应的pojo类名
	 */
	private String pojoClassName = null;
	
	/**
	 * mapper类名
	 */
	private String mapperClassName = null;
	
	/**
	 * pojo类生成路径
	 */
	private String pojoPath = null;
	
	/**
	 * pojo类所在包名
	 */
	private String pojoPackage = null;
	
	/**
	 * mapper类生成路径
	 */
	private String mapperPath = null;
	
	/**
	 * mapper类所在包名
	 */
	private String mapperPackage = null;
	
	public boolean isExistsPrimaryKey() {
		return isExistsPrimaryKey;
	}

	public void setExistsPrimaryKey(boolean isExistsPrimaryKey) {
		this.isExistsPrimaryKey = isExistsPrimaryKey;
	}

	public List<TableField> getFields() {
		return fields;
	}

	public void setFields(List<TableField> fields) {
		this.fields = fields;
	}

	public boolean isBuildExample() {
		return isBuildExample;
	}

	public void setBuildExample(boolean isBuildExample) {
		this.isBuildExample = isBuildExample;
	}

	public boolean isBuildNote() {
		return isBuildNote;
	}

	public void setBuildNote(boolean isBuildNote) {
		this.isBuildNote = isBuildNote;
	}

	public String getPojoClassName() {
		return pojoClassName;
	}

	public void setPojoClassName(String pojoClassName) {
		this.pojoClassName = pojoClassName;
	}

	public String getMapperClassName() {
		return mapperClassName;
	}

	public void setMapperClassName(String mapperClassName) {
		this.mapperClassName = mapperClassName;
	}
	
	public String getPojoPath() {
		return pojoPath;
	}

	public void setPojoPath(String pojoPath) {
		this.pojoPath = pojoPath;
	}

	public String getMapperPath() {
		return mapperPath;
	}

	public void setMapperPath(String mapperPath) {
		this.mapperPath = mapperPath;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getPojoPackage() {
		return pojoPackage;
	}

	public void setPojoPackage(String pojoPackage) {
		this.pojoPackage = pojoPackage;
	}

	public String getMapperPackage() {
		return mapperPackage;
	}

	public void setMapperPackage(String mapperPackage) {
		this.mapperPackage = mapperPackage;
	}
	
}
