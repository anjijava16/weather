package com.crossover.trial.weather.domain;

public enum DST {
	E("Europe"), A("US/Canada"), S("South America"), O("Australia"), Z("New Zealand"), N("None"), U("Unknown");

	private String name;

	private DST(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}