package com.bonree.android.data.parse.module;

public class SourceData {
	private int id;
	private int type;
	
	public SourceData() {
	}
	
	public SourceData(int id, int type) {
		super();
		this.id = id;
		this.type = type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}
