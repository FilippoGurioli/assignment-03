package roomService;

public class ESPEmulator {

	private boolean PIR = false;
	private int PR = 0;
	private String photoR = "BLACK";
	
	public String getCommandPIR() {
		PIR = !PIR;
		if (PIR) {
			return "PEOPLE";
		} else {
			return "NOONE";
		}
	}
	
	public String getCommandPR() {
		PR++;
		if (PR % 4 == 0) {
			PR = 0;
			if (photoR.equals("WHITE")) {
				photoR = "BLACK";
			} else {
				photoR = "WHITE";
			}
		}
		return photoR;
	}
}
