package roomService;

public class Peripherals {

	private int servo;
	private Led led;
	private boolean presence;
	private boolean brightness;
	public static final int MAX_DEG = 180;
	public static final int MIN_DEG = 0;
	
	public Peripherals(final int servo, final Led led, final boolean presence, final boolean brightness) {
		if (servo > MAX_DEG) {
			this.servo = MAX_DEG;
		} else if (servo < MIN_DEG) {
			this.servo = MIN_DEG;
		} else {			
			this.servo = servo;
		}
		this.led = led;
	}
	
	public Peripherals() {
		this.servo = MAX_DEG;
		this.led = Led.OFF;
	}

	public int getServo() {
		return this.servo;
	}
	
	public Led getLed() {
		return this.led;
	}
	
	public boolean isBright() {
		return this.brightness;
	}
	
	public boolean isPresent() {
		return this.presence;
	}
	
	public void setServo(final int servo) {
		if (servo > MAX_DEG) {
			this.servo = MAX_DEG;
		} else if (servo < MIN_DEG) {
			this.servo = MIN_DEG;
		} else {			
			this.servo = servo;
		}
	}
	
	public void setLed(final Led led) {
		this.led = led;
	}
	
	public void setBrightness(final boolean brightness) {
		this.brightness = brightness;
	}
	
	public void setPresence(final boolean presence) {
		this.presence = presence;
	}
	
	public String toString() {
		return "Presence: " + this.presence + " Bright: " + this.brightness
				+ " Servo: " + this.servo + "  LED: " + this.led;
	}
}
