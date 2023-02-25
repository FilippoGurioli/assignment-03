package roomService;

public class ESPEmulator {

	private boolean PIR = false;
	private boolean PR = false;
	
	public String getCommandPIR() {
		PIR = !PIR;
		if (PIR) {
			return "PEOPLE";
		} else {
			return "NOONE";
		}
	}
	
	public String getCommandPR() {
		PR = !PR;
		if (PR) {
			return "WHITE";
		} else {
			return "BLACK";
		}
	}
}
