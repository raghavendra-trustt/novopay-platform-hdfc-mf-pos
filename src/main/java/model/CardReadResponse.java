package model;

import com.morefun.mpos.sdk.result.ReadCardResult;

public class CardReadResponse {

	private String statusCode;
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public ReadCardResult getData() {
		return data;
	}
	public void setData(ReadCardResult data) {
		this.data = data;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	private ReadCardResult data;
	private String cardType;
	private String responseMessage;
}
