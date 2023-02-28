package roomService;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeThread extends Thread {

	private boolean morning = false;
	private final SimpleDateFormat sdf = new SimpleDateFormat("HH");
	private static final int TEN_MIN = 1000 * 60 * 1; //1000 millisec times 60 sec times 1 mins, it should be higher to simulate the context better
	
	@Override
	public void run() {
		while(true) {
			int tmp = Integer.parseInt(sdf.format(new Date()));
			if ((tmp >= 8 && tmp <= 19) && !this.morning) {
				morning = true;
			} else if ((tmp < 8 || tmp > 19) && this.morning) {
				morning = false;
			}
			try {
				Thread.sleep(TEN_MIN);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isMorning() {
		return morning;
	}
	
	public String toString() {
		return (morning ? "Day" : "Night");
	}
}
