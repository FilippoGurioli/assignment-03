package roomService;

public class Peripherals {

	private final int servo;
	private final Led led;
	public static final int MAX_DEG = 180;
	public static final int MIN_DEG = 0;
	
	public Peripherals(final int servo, final Led led) {
		if (servo > MAX_DEG) {
			this.servo = MAX_DEG;
		} else if (servo < MIN_DEG) {
			this.servo = MIN_DEG;
		} else {			
			this.servo = servo;
		}
		this.led = led;
	}
	
	public int getServo() {
		return this.servo;
	}
	
	public Led getLed() {
		return this.led;
	}
	
	public String toString() {
		return "Servo: " + this.servo + "\nLED: " + this.led + "\n";
	}
}
