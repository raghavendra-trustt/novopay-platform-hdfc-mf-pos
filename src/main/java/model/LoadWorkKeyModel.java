package model;

public class LoadWorkKeyModel {

	private String pinKey;
	public String getPinKey() {
		return pinKey;
	}
	public void setPinKey(String pinKey) {
		this.pinKey = pinKey;
	}
	public String getMacKey() {
		return macKey;
	}
	public void setMacKey(String macKey) {
		this.macKey = macKey;
	}
	public String getTdkKey() {
		return tdkKey;
	}
	public void setTdkKey(String tdkKey) {
		this.tdkKey = tdkKey;
	}
	private String macKey;
	private String tdkKey;
	private String pinKCV;
	private String tdkKcv;
	public void setPinKcv(String pinKcv)
	{
		this.pinKCV=pinKcv;
	}
	public String getPinKCV() {
		
		return pinKCV;
	}
	public void setTdkKcv(String tdkKcv)
	{
		this.tdkKcv=tdkKcv;
	}
	public String getTdkKcv() {
	
		return tdkKcv;
	}
}
