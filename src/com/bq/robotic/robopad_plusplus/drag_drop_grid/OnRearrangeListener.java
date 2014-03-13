package com.bq.robotic.robopad_plusplus.drag_drop_grid;

public interface OnRearrangeListener {
	
	public abstract void onRearrange(int oldIndex, int newIndex);
	
	public abstract void onRearrange(boolean isDraggedDeleted, int draggedDeletedIndex);
}
