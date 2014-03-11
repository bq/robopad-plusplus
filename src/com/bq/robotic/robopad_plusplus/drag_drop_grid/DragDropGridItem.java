
package com.bq.robotic.robopad_plusplus.drag_drop_grid;

public class DragDropGridItem {

	private long id;
	private String name;
	private int drawable;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDrawable() {
		return drawable;
	}
	public void setDrawable(int drawable) {
		this.drawable = drawable;
	}
	public DragDropGridItem(long id, String name, int drawable) {
		super();
		this.id = id;
		this.name = name;
		this.drawable = drawable;
	}
}
