package madrid.apiFactory.core.util.core;




import madrid.apiFactory.core.util.utils.JSONUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class CommandResponseImpl<T> implements ICommandResponse{

	private int resultCode = HttpStatusCode.SUCCESS.getValue();

	private String resultDesc = HttpStatusCode.SUCCESS.name();

	// 如果无异常，该属性为空字符
	private String exception = "";

	// 如果执行select或者SP/function，则该属性值为-1
	private int affectedRows = -1;

	// 如果执行Update/Insert/Delete,该属性为空数组
	protected String[] columnNames;

	// 如果执行Update/Insert/Delete,该属性为空数组
	protected String[][] rows;
	
	private Map<String,String> outParams = new HashMap<String, String>();

	private T resultValue;
	
	public CommandResponseImpl() {
	}

	public CommandResponseImpl(int resultCode) {
		super();
		this.resultCode = resultCode;
		if (resultCode == HttpStatusCode.NOT_FOUND.getValue()) {
			this.resultDesc = HttpStatusCode.NOT_FOUND.name();
		} else if (resultCode == HttpStatusCode.AUTH_FAIL.getValue()) {
			this.resultDesc = HttpStatusCode.AUTH_FAIL.name();
		} else if (resultCode == HttpStatusCode.EXCEPTION.getValue()) {
			this.resultDesc = HttpStatusCode.EXCEPTION.name();
		}
	}

	public String getCell(int x, int y) {
		if (rows.length > x && rows[x] != null && rows[x].length > y) {
			return rows[x][y];
		} else {
			return null;
		}
	}

	public int getRowCount() {
		return this.rows == null ? 0 : this.rows.length;
	}

	public int getResultCode() {
		return resultCode;
	}

	public boolean isSuccess() {
		return HttpStatusCode.SUCCESS.getValue() == resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultDesc() {
		return resultDesc;
	}

	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public int getAffectedRows() {
		return affectedRows;
	}

	public void setAffectedRows(int affectedRows) {
		this.affectedRows = affectedRows;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	public String[][] getRows() {
		return this.rows;
	}

	public void setRows(String[][] rows) {
		this.rows = rows;
	}
	
	public Map<String, String> getOutParams() {
		return outParams;
	}

	public void setOutParams(Map<String, String> outParams) {
		this.outParams = outParams;
	}
	
	public T getResultValue() {
		return resultValue;
	}

	public void setResultValue(T resultValue) {
		this.resultValue = resultValue;
	}

	public String toString() {
		String result = JSONUtils.encode(this);
		return result;
	}
}
