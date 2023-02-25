package roomService;

public class TimeThread extends Thread {

	private boolean morning = false;
	
	@Override
	public void run() {
		while(true) {			
			try {
				Thread.sleep(30_000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			morning = !morning;
		}
	}
	
	public boolean isMorning() {
		return morning;
	}
}
