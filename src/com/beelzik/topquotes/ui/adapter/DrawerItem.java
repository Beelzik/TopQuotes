package com.beelzik.topquotes.ui.adapter;

public class DrawerItem {

	String ItemName;
	Integer imgResID;
	String title;


	public DrawerItem(String itemName, Integer imgResID) {
		ItemName = itemName;
		this.imgResID = imgResID;
	}

	

	public DrawerItem(String title) {
		this(null, 0);
		this.title = title;
	}

	public String getItemName() {
		return ItemName;
	}

	public void setItemName(String itemName) {
		ItemName = itemName;
	}

	public Integer getImgResID() {
		return imgResID;
	}

	public void setImgResID(int imgResID) {
		this.imgResID = imgResID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


}
