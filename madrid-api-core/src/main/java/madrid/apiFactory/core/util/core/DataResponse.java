package madrid.apiFactory.core.util.core;

import java.util.HashMap;
import java.util.Map;

public class DataResponse<T> extends CommandResponseImpl<T> {

	private DataResponse(Builder<T> b) {
		this.setResultCode(b.resultCode);
		this.setResultDesc(b.resultDesc);
		this.setAffectedRows(b.affectedRows);
		this.setException(b.exception);
		this.setResultValue(b.resultValue);
		this.setOutParams(b.outParams);
		this.columnNames = b.columnNames;
		this.rows = b.rows;
	}

	public static class Builder<T> {
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

		private Map<String, String> outParams = new HashMap<String, String>();

		private T resultValue;

		public Builder() {

		}

		public Builder(int resultCode) {
			this.resultCode = resultCode;
		}

		public Builder<T> setDesc(String desc) {
			this.resultDesc = desc;
			return this;
		}

		public Builder<T> setException(String exception) {
			this.resultCode = HttpStatusCode.EXCEPTION.getValue();
			this.resultDesc = HttpStatusCode.EXCEPTION.name();
			this.exception = exception;
			return this;
		}

		public Builder<T> setAffectedRows(int affectedRow) {
			this.affectedRows = affectedRow;
			return this;
		}

		public Builder<T> setColumns(String[] columnNames) {
			this.columnNames = columnNames;
			return this;
		}

		public Builder<T> setRows(String[][] rows) {
			this.rows = rows;
			return this;
		}

		public void registerParam(String key, String value) {
			this.outParams.put(key, value);
		}

		public Builder<T> setResultValue(T resultValue) {
			this.resultValue = resultValue;
			return this;
		}

		public DataResponse<T> build() {
			return new DataResponse<T>(this);
		}
	}

}
