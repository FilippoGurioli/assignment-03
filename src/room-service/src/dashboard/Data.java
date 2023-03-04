package dashboard;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Data {
	private String date;
	private String time;
	private String content;
	
	public Data(String content) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
		this.date = formatter.format(new Date());
		formatter = new SimpleDateFormat("HH:mm:ss");
		this.time = formatter.format(new Date());
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
