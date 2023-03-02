package dashboard;

class Data {
	private String date;
	private String time;
	private String content;
	
	public Data(String date, String time, String content) {
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
