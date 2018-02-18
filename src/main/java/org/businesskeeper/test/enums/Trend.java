package org.businesskeeper.test.enums;

public enum Trend {
	DESCENDING("descending"),
	ASCENDING("ascending"),
	CONSTANT("constant"),
	UNDEFINED("undefined");
	
	private String trend;

	private Trend(String trend) {
		this.trend = trend;
	}

	public String getTrend() {
		return trend;
	}
	
}
