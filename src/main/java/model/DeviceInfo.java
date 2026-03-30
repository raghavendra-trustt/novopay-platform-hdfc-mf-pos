package model;

import com.morefun.mpos.sdk.result.ReadPosInfoResult;

public class DeviceInfo {
	private String statusCode;
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public ReadPosInfoResult getData() {
		return data;
	}
	public void setData(ReadPosInfoResult data) {
		this.data = data;
	}
	private String responseMessage;
	private ReadPosInfoResult data;

	@Override
	public String toString() {
		return "DeviceInfo{" +
				"statusCode='" + statusCode + '\'' +
				", responseMessage='" + responseMessage + '\'' +
				", data=" + data +
				'}';
	}
}
