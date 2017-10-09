package jvn;

public enum JvnState {

	NL("no_lock", 0), RC("read_lock_cached", 1), WC("write_lock_cached", 2), R("read_lock_taken",
			3), W("write_lock_taken", 4), RWC("read_write_lock_cached", 5);

	private String value;
	private int number;

	JvnState(String value, int number) {
		this.value=value;
		this.number=number;
	}

	public String getValue() {
		return value;
	}

	public int getNumber() {
		return number;
	}

}
