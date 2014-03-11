package com.bq.robotic.robopad_plusplus.drag_drop_grid;

import android.view.View;

public interface DragDropGridAdapter {

    // Automatic child distribution
	public final static int AUTOMATIC = -1; 
	
	// Delete drop zone location TOP
	public final static int TOP = 1;
	
	// Delete drop zone location BOTTOM
	public final static int BOTTOM = 2;
	
	
	/**
	 * Returns the count of items
	 * 
	 * @return item count
	 */
	public int itemCount();
	
	/**
	 * Returns the view for the item
	 * 
	 * @param item index
	 * @return the view 
	 */
	public View view(int index);
	
	/**
	 * The fixed row count (AUTOMATIC for automatic computing)
	 * 
	 * @return row count or AUTOMATIC
	 */
	public int rowCount();
	
	/**
	 * The fixed column count (AUTOMATIC for automatic computing)
	 * 
	 * @return column count or AUTOMATIC
	 */
	public int columnCount();

	/**
	 * Prints the layout in Log.d();
	 */
	public void printLayout();

	/**
	 * Swaps two items in the item list
	 * 
	 * @param itemIndexA
	 * @param itemIndexB
	 */
	public void swapItems(int itemIndexA, int itemIndexB);
	
	/**
	 * deletes the item at position
	 * 
	 * @param itemIndex
	 */
	public void deleteItem(int itemIndex);

	/** 
	 * Returns the delete drop zone location.  
	 * 
	 * @return TOP or BOTTOM. 
	 */
    public int deleteDropZoneLocation();

    /**
     * Tells the grid to show or not the remove drop zone when moving an item
     */
    public boolean showRemoveDropZone();

    /**
     * Tells the grid, the page defined width
     * If page width is zero, display width is taken
     * @return the page width
     */
    public int getPageWidth();

    /**
     * Gets the item displayed by the datasource
     * 
     * @param index the index of the datasource
     * @return your object
     */
     public Object getItemAt(int index);


}
