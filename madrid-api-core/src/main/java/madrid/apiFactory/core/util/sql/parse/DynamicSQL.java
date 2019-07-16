package madrid.apiFactory.core.util.sql.parse;

import java.util.ArrayList;
import java.util.List;

public class DynamicSQL {
	private String sql;
	private List<Parameter> parameters = new ArrayList<Parameter>();

	public DynamicSQL() {
	}

	public int getOutParamCount() {
		int i = 0;
		for (Parameter p : this.parameters) {
			if (p.isOutParam()) {
				i++;
			}
		}
		return i;
	}

	public DynamicSQL(String sql, Object[] array) {
		this.sql = sql;
		if (array != null)
			for (Object p : array)
				this.parameters.add(new Parameter(p));
	}

	public List<Parameter> getParameters() {
		return this.parameters;
	}

	public Object[] getParams() {
		Object[] params = new Object[this.parameters.size()];
		int i = 0;
		for (Parameter p : this.parameters) {
			params[(i++)] = p.getValue();
		}
		return params;
	}

	public void addParam(Object param) {
		this.parameters.add(new Parameter(param));
	}

	public void addParams(Object[] params) {
		for (Object p : params)
			this.parameters.add(new Parameter(p));
	}

	public String getSql() {
		return this.sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void addParameter(Parameter p) {
		this.parameters.add(p);
	}

//	public String showDetail() {
//		return "SQL : " + SQLFormatter.format(this.sql) + "\nPARAMS : " + BeanUtils.join(getParams(), ",");
//	}
//
//	public String toString() {
//		return SQLFormatter.format(JdbcUtils.doLogSQL(this.sql, getParams()));
//	}
}
