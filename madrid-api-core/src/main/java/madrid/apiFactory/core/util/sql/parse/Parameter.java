package madrid.apiFactory.core.util.sql.parse;



import madrid.apiFactory.core.util.utils.BeanUtils;
import madrid.apiFactory.core.util.utils.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Parameter {
	private Map<String, Integer> typeName2TypeMap = new HashMap<String, Integer>();
	  private int ORACLE_TYPES_CURSOR = -10;
	  private String name;
	  private String typeName;
	  private Integer typeCode;
	  private String mode;
	  private Object value;

	  public boolean isOutParam()
	  {
	    return "out".equalsIgnoreCase(this.mode);
	  }

	  public boolean isCursor() {
	    return this.typeCode.intValue() == this.ORACLE_TYPES_CURSOR;
	  }

	  public void registerType(String name, int type) {
	    this.typeName2TypeMap.put(name, Integer.valueOf(type));
	  }

	  public Parameter(Object value)
	  {
	    this.typeName2TypeMap.put("boolean", Integer.valueOf(16));
	    this.typeName2TypeMap.put("string", Integer.valueOf(12));
	    this.typeName2TypeMap.put("nubmer", Integer.valueOf(2));
	    this.typeName2TypeMap.put("cursor", Integer.valueOf(this.ORACLE_TYPES_CURSOR));

	    this.typeName2TypeMap.put("int", Integer.valueOf(4));
	    this.typeName2TypeMap.put("integer", Integer.valueOf(4));
	    this.typeName2TypeMap.put("long", Integer.valueOf(-5));
	    this.typeName2TypeMap.put("float", Integer.valueOf(6));
	    this.typeName2TypeMap.put("double", Integer.valueOf(8));
	    this.typeName2TypeMap.put("date", Integer.valueOf(91));
	    this.typeName2TypeMap.put("clob", Integer.valueOf(2005));
	    this.typeName2TypeMap.put("blob", Integer.valueOf(2004));

	    this.typeName2TypeMap.put("date", Integer.valueOf(91));

	    this.typeName = "string";
	    this.typeCode = Integer.valueOf(-1);

	    this.mode = "in";

	    this.value = value;
	    this.typeName = "object";
	  }

	  public static Parameter newOutParam(String name, int type) {
	    return new Parameter(name, true, Integer.valueOf(type), null);
	  }

	  public static Parameter newInputParam(Object value, int type) {
	    return new Parameter(null, false, Integer.valueOf(type), value);
	  }

	  public static Parameter newStringInputParam(String value) {
	    return new Parameter(null, false, Integer.valueOf(12), value);
	  }

	  public static Parameter newIntInputParam(Integer value) {
	    return new Parameter(null, false, Integer.valueOf(4), value);
	  }

	  public Parameter(String name, boolean isOutParam, Integer typeCode, Object value)
	  {
	    this.typeName2TypeMap.put("boolean", Integer.valueOf(16));
	    this.typeName2TypeMap.put("string", Integer.valueOf(12));
	    this.typeName2TypeMap.put("nubmer", Integer.valueOf(2));
	    this.typeName2TypeMap.put("cursor", Integer.valueOf(this.ORACLE_TYPES_CURSOR));

	    this.typeName2TypeMap.put("int", Integer.valueOf(4));
	    this.typeName2TypeMap.put("integer", Integer.valueOf(4));
	    this.typeName2TypeMap.put("long", Integer.valueOf(-5));
	    this.typeName2TypeMap.put("float", Integer.valueOf(6));
	    this.typeName2TypeMap.put("double", Integer.valueOf(8));
	    this.typeName2TypeMap.put("date", Integer.valueOf(91));
	    this.typeName2TypeMap.put("clob", Integer.valueOf(2005));
	    this.typeName2TypeMap.put("blob", Integer.valueOf(2004));

	    this.typeName2TypeMap.put("date", Integer.valueOf(91));

	    this.typeName = "string";
	    this.typeCode = Integer.valueOf(-1);

	    this.mode = "in";

	    this.name = name;
	    this.typeCode = typeCode;
	    this.mode = (isOutParam ? "out" : "in");
	    this.value = value;
	  }

	  public Parameter(String pnameModeType, Object context)
	  {
	    this.typeName2TypeMap.put("boolean", Integer.valueOf(16));
	    this.typeName2TypeMap.put("string", Integer.valueOf(12));
	    this.typeName2TypeMap.put("nubmer", Integer.valueOf(2));
	    this.typeName2TypeMap.put("cursor", Integer.valueOf(this.ORACLE_TYPES_CURSOR));

	    this.typeName2TypeMap.put("int", Integer.valueOf(4));
	    this.typeName2TypeMap.put("integer", Integer.valueOf(4));
	    this.typeName2TypeMap.put("long", Integer.valueOf(-5));
	    this.typeName2TypeMap.put("float", Integer.valueOf(6));
	    this.typeName2TypeMap.put("double", Integer.valueOf(8));
	    this.typeName2TypeMap.put("date", Integer.valueOf(91));
	    this.typeName2TypeMap.put("clob", Integer.valueOf(2005));
	    this.typeName2TypeMap.put("blob", Integer.valueOf(2004));

	    this.typeName2TypeMap.put("date", Integer.valueOf(91));

	    this.typeName = "string";
	    this.typeCode = Integer.valueOf(-1);

	    this.mode = "in";

	    if (pnameModeType.indexOf(':') == -1) {
	      this.name = pnameModeType;
	    } else {
	      String[] nameModeType = pnameModeType.split(":", 3);
	      this.name = nameModeType[0];
	      if (("in".equalsIgnoreCase(nameModeType[1])) || ("out".equalsIgnoreCase(nameModeType[1]))) {
	        this.mode = nameModeType[1].toLowerCase();
	      } else {
	        this.mode = "in";
	        this.typeName = nameModeType[1].toLowerCase();
	        this.typeCode = ((Integer)this.typeName2TypeMap.get(this.typeName));
	      }

	      if (nameModeType.length > 2) {
	        this.typeName = nameModeType[2].toLowerCase();
	        this.typeCode = ((Integer)this.typeName2TypeMap.get(this.typeName));
	      }
	    }
	    if (!"out".equalsIgnoreCase(this.mode))
	      this.value = getParam(context, this.name);
	  }

//	  public Parameter(String pname, Object context, Environment env, String principalId)
//	  {
//	    this.typeName2TypeMap.put("boolean", Integer.valueOf(16));
//	    this.typeName2TypeMap.put("string", Integer.valueOf(12));
//	    this.typeName2TypeMap.put("nubmer", Integer.valueOf(2));
//	    this.typeName2TypeMap.put("cursor", Integer.valueOf(this.ORACLE_TYPES_CURSOR));
//
//	    this.typeName2TypeMap.put("int", Integer.valueOf(4));
//	    this.typeName2TypeMap.put("integer", Integer.valueOf(4));
//	    this.typeName2TypeMap.put("long", Integer.valueOf(-5));
//	    this.typeName2TypeMap.put("float", Integer.valueOf(6));
//	    this.typeName2TypeMap.put("double", Integer.valueOf(8));
//	    this.typeName2TypeMap.put("date", Integer.valueOf(91));
//	    this.typeName2TypeMap.put("clob", Integer.valueOf(2005));
//	    this.typeName2TypeMap.put("blob", Integer.valueOf(2004));
//
//	    this.typeName2TypeMap.put("date", Integer.valueOf(91));
//
//	    this.typeName = "string";
//	    this.typeCode = Integer.valueOf(-1);
//
//	    this.mode = "in";
//
//	    if (pname.indexOf(':') == -1) {
//	      this.name = pname;
//	    } else {
//	      String[] nameModeType = pname.split(":");
//	      this.name = nameModeType[0];
//	      if (("in".equalsIgnoreCase(nameModeType[1])) || ("out".equalsIgnoreCase(nameModeType[1]))) {
//	        this.mode = nameModeType[1].toLowerCase();
//	      } else {
//	        this.mode = "in";
//	        this.typeName = nameModeType[1].toLowerCase();
//	        this.typeCode = ((Integer)this.typeName2TypeMap.get(this.typeName));
//	      }
//	      if (nameModeType.length > 2) {
//	        this.typeName = nameModeType[2].toLowerCase();
//	        this.typeCode = ((Integer)this.typeName2TypeMap.get(this.typeName));
//	      }
//	    }
//	    if (!"out".equalsIgnoreCase(this.mode)) {
//	      try {
//	        this.value = getParam(context, this.name);
//	      } catch (RuntimeException e) {
//	        this.value = null;
//	      }
//	      if (this.value == null)
//	        this.value = (principalId == null ? null : env.getVarByPrincipal(principalId, this.name));
//	    }
//	  }

	  public boolean isNull()
	  {
	    if (isOutParam()) {
	      return false;
	    }
	    return (this.value == null) || (this.value.getClass().getSimpleName().equals("Null"));
	  }

	  public boolean isNotNull() {
	    return !isNull();
	  }
	  public boolean isNotEmpty() {
	    return !isEmpty();
	  }
	  public boolean isEmpty() {
	    if (isOutParam()) {
	      return false;
	    }
	    return (this.value == null) || (this.value.toString().trim().length() == 0);
	  }

	  public Integer getTypeCode() {
	    if (this.typeCode == null) {
	      Integer type = (Integer)this.typeName2TypeMap.get(this.typeCode);
	      if (type != null) {
	        this.typeCode = type;
	      }
	    }
	    return this.typeCode;
	  }

	  public Class<?> getTypeClass() {
	    Class<?> type = Object.class;
	    if (StringUtils.isNotEmpty(this.typeName))
	    {
	      if ("boolean".equals(this.typeName))
	        type = Boolean.class;
	      else if ("string".equals(this.typeName))
	        type = String.class;
	      else if (("int".equals(this.typeName)) || ("integer".equals(this.typeName)))
	        type = Integer.class;
	      else if ("long".equals(this.typeName))
	        type = Long.class;
	      else if ("float".equals(this.typeName))
	        type = Float.class;
	      else if ("double".equals(this.typeName))
	        type = Double.class;
	      else if ("date".equals(this.typeName))
	        type = Date.class;
	    }
	    return type;
	  }
	  
	  public void setTypeCode(Integer typeCode) {
	    this.typeCode = typeCode;
	  }

	  public Object getValue()
	  {
	    if ((this.value != null) && (this.typeName != null) && (!"object".equals(this.typeName))) {
	      Class<?> type = getTypeClass();
	      if ((type != Object.class) && (!type.isInstance(this.value))) {
	        this.value = BeanUtils.convert(type, this.value);
	      }
	    }
	    return this.value;
	  }
	  public void setValue(Object value) {
	    this.value = value;
	  }
	  public String getTypeName() {
	    return this.typeName;
	  }
	  public void setTypeName(String typeName) {
	    this.typeName = typeName;
	  }
	  public String getMode() {
	    return this.mode;
	  }
	  public void setMode(String mode) {
	    this.mode = mode;
	  }
	  public String getName() {
	    return this.name;
	  }
	  public void setName(String name) {
	    this.name = name;
	  }

	  protected Object getParam(Object context, String paramName) {
	    return BeanUtils.getParam(context, paramName);
	  }

	  public boolean isDateType() {
	    return "date".equals(this.typeName);
	  }

	  public boolean isIntType() {
	    return ("int".equals(this.typeName)) || ("integer".equals(this.typeName));
	  }

	  public boolean isFloatType() {
	    return "float".equals(this.typeName);
	  }

	  public boolean isStringType() {
	    return "string".equals(this.typeName);
	  }

	  public boolean isBooleanType() {
	    return "boolean".equals(this.typeName);
	  }
}
