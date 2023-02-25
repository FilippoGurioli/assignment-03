package esiot.module_lab_3_2;

class DataPoint {
	private String date;
	private String time;
	private String content;
	
	public DataPoint(String date, String time, String content) {
		this.date = date;
		this.time = time;
		this.content = content;
	}
	
	public String getDate() {
		return this.date;
	}
	
	public String getTime() {
		return this.time;
	}
	
	public String getContent() {
		return this.content;
	}
}
