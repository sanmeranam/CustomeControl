package com.wafone.customlibrary;

public class TimeEntity {
	private int	HOUR;
	private int	MINUTE;

	public TimeEntity() {
		this.HOUR = 0;
		this.MINUTE = 0;
	}

	public TimeEntity(int HOUR, int MINUTE) {
		this.HOUR = HOUR;
		this.MINUTE = MINUTE;
	}

	public int getHOUR() {
		return HOUR;
	}

	public void setHOUR(int hOUR) {
		HOUR = hOUR > 23 ? 23 : hOUR < 0 ? 0 : hOUR;
	}

	public int getMINUTE() {
		return MINUTE;
	}

	public void setMINUTE(int mINUTE) {
		MINUTE = mINUTE > 60 ? 60 : mINUTE < 0 ? 0 : mINUTE;
	}
}
