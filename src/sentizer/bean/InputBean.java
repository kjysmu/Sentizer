package sentizer.bean;

import java.util.List;

public class InputBean {
	private String query;

	//private String searchMode;
	private boolean isSuccess;
	private boolean isSimilarity;
	
	private String errorMsg;
	
	private List<ResultBean> resultList;
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}

	
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public List<ResultBean> getResultList() {
		return resultList;
	}
	
	public void setResultList(List<ResultBean> resultList) {
		this.resultList = resultList;
	}
	
	public boolean getIsSimilarity() {
		return isSimilarity;
	}

	public void setIsSimilarity(boolean isSimilarity) {
		this.isSimilarity = isSimilarity;
	}
	
	
	
}

