package roomService;

public class Peripherals {

	private final int servo;
	private final boolean led;
	
	public Peripherals(final int servo, final boolean led) {
		if (servo > 180) {
			this.servo = 180;
		} else if (servo < 0) {
			this.servo = 0;
		} else {			
			this.servo = servo;
		}
		this.led = led;
	}
	
	public int getServo() {
		return this.servo;
	}
	
	public boolean getLed() {
		return this.led;
	}
	
}
