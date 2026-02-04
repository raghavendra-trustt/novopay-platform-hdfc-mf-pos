package model;

import java.util.ArrayList;

public class ReadCardModel {
	
	private byte cardTimeout = 60;
    public byte getCardTimeout() {
		return cardTimeout;
	}

	public void setCardTimeout(byte cardTimeout) {
		this.cardTimeout = cardTimeout;
	}

	public String getTransName() {
		return transName;
	}

	public void setTransName(String transName) {
		this.transName = transName;
	}

	public byte getCardmode() {
		return cardmode;
	}

	public void setCardmode(byte cardmode) {
		this.cardmode = cardmode;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getAuthAmount() {
		return authAmount;
	}

	public void setAuthAmount(String authAmount) {
		this.authAmount = authAmount;
	}

	public String getOtherAmount() {
		return otherAmount;
	}

	public void setOtherAmount(String otherAmount) {
		this.otherAmount = otherAmount;
	}

	public ArrayList<String> getTags() {
		//System.out.print("Tags: "+Arrays.toString(tags));
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		//System.out.print("Tags: "+Arrays.toString(tags));
		this.tags = tags;
	}

	public Integer getTransType() {
		return transType;
	}

	public void setTransType(Integer transType) {
		this.transType = transType;
	}

	public boolean isForceonline() {
		return forceonline;
	}

	public void setForceonline(boolean forceonline) {
		this.forceonline = forceonline;
	}

	public int getPinInput() {
		return pinInput;
	}

	public void setPinInput(int pinInput) {
		this.pinInput = pinInput;
	}

	public byte getPinMaxLen() {
		return pinMaxLen;
	}

	public void setPinMaxLen(byte pinMaxLen) {
		this.pinMaxLen = pinMaxLen;
	}

	public byte getPinTimeout() {
		return pinTimeout;
	}

	public void setPinTimeout(byte pinTimeout) {
		this.pinTimeout = pinTimeout;
	}

	public String getLsh() {
		return lsh;
	}

	public void setLsh(String lsh) {
		this.lsh = lsh;
	}

	public byte getRequiretype() {
		return requiretype;
	}

	public void setRequiretype(byte requiretype) {
		this.requiretype = requiretype;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public int getAmountTimeout() {
		return amountTimeout;
	}

	public void setAmountTimeout(int amountTimeout) {
		this.amountTimeout = amountTimeout;
	}

	public long getPinPassAmt() {
		return pinPassAmt;
	}

	public void setPinPassAmt(long pinPassAmt) {
		this.pinPassAmt = pinPassAmt;
	}

	public int getEmvTransactionType() {
		return emvTransactionType;
	}

	public void setEmvTransactionType(int emvTransactionType) {
		this.emvTransactionType = emvTransactionType;
	}

	public boolean isAllowfallback() {
		return allowfallback;
	}

	public void setAllowfallback(boolean allowfallback) {
		this.allowfallback = allowfallback;
	}

	private String transName;
    private byte cardmode;
    private String amount;
    private String authAmount;
    private String otherAmount;
    private ArrayList<String> tags = new  ArrayList<String>();
    private Integer transType;
    boolean forceonline;
    private int pinInput;
    private byte pinMaxLen;
    private byte pinTimeout;
    private String lsh;
    private byte requiretype;
    private String orderid;
    private int amountTimeout;
    private long pinPassAmt;
    private int emvTransactionType;
    private boolean allowfallback;

}
