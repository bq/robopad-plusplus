
package com.bq.robotic.robopad_plusplus.drag_drop_grid;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bq.robotic.robopad_plusplus.R;

public class RobotControlsDragDropGridAdapter implements DragDropGridAdapter {

	private Context context;
	private DragDropGrid gridview;
	
	List<DragDropGridItem> items = new ArrayList<DragDropGridItem>();
	
	public RobotControlsDragDropGridAdapter(Context context, DragDropGrid gridview) {
		super();
		this.context = context;
		this.gridview = gridview;
		
		items.add(new DragDropGridItem(1, "2 segundos", R.drawable.up_button));
		items.add(new DragDropGridItem(2, "2 segundos", R.drawable.down_button));
		items.add(new DragDropGridItem(3, "2 segundos", R.drawable.left_button));
		items.add(new DragDropGridItem(3, "2 segundos", R.drawable.up_button));
	
	}


	public List<DragDropGridItem> getItems() {
		return items;
	}

    @Override
	public View view(int index) {
		
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		ImageView icon = new ImageView(context);
		DragDropGridItem item = getItem(index);
		icon.setImageResource(item.getDrawable());
		icon.setPadding(15, 15, 15, 15);
		
		layout.addView(icon);
		
		TextView label = new TextView(context);
		label.setTag("text");
		label.setText(item.getName());	
		label.setTextColor(Color.BLACK);
		label.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
	
		label.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

		layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		setViewBackground(layout);
		layout.setClickable(true);
		layout.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return gridview.onLongClick(v);
            }
		});
		

		layout.addView(label);
		return layout;
	}

    private void setViewBackground(LinearLayout layout) {
    	layout.setBackgroundResource(R.drawable.abc_list_selector_holo_light);
    }

	private DragDropGridItem getItem(int index) {
		return items.get(index);
	}

	@Override
	public int rowCount() {
		return AUTOMATIC;
	}

	@Override
	public int columnCount() {
		return AUTOMATIC;
	}

	@Override
	public int itemCount() {
		return items.size();
	}

	public void printLayout() {			
		for (DragDropGridItem item : items) {
			Log.d("Item", Long.toString(item.getId()));
		}
	}

	@Override
	public void swapItems(int itemIndexA, int itemIndexB) {
		swapItems(itemIndexA, itemIndexB);
	}

	@Override
	public void deleteItem(int itemIndex) {
		deleteItem(itemIndex);
	}

    @Override
    public int deleteDropZoneLocation() {        
        return BOTTOM;
    }

    @Override
    public boolean showRemoveDropZone() {
        return true;
    }

	@Override
	public int getPageWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItemAt(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
