package jvn;

public enum JvnState {

	NL("no_lock", 0), RC("read_lock_cached", 1), WC("write_lock_cached", 2), R("read_lock_taken",
			3), W("write_lock_taken", 4), RWC("read_write_lock_cached", 5);

	private String value;
	private int number;

	JvnState(String value, int number) {
		this.setValue(value);
	}

	// the valueOfMethod
	public static JvnState fromString(String value) {
		switch (value) {
		case "no_lock":
			return NL;
		case "read_lock_cached":
			return RC;
		case "write_lock_cached":
			return WC;
		case "read_lock_taken":
			return R;
		case "write_lock_taken":
			return W;
		case "read_write_lock_cached":
			return RWC;
		default:
			return null;
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
