package bang.scavengerhunt;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


/**
 * @author Alex Decker
 *
 */
public class LocationList extends ListActivity {
	ScavengerHunt myApp;
	TextView pointView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		myApp = (ScavengerHunt) getApplication();

		ListView lv = getListView();
		LinearLayout layout = new LinearLayout(this);
		pointView = new TextView(this);
		pointView.setText("Total Points: "+myApp.points);
		layout.addView(pointView);
		lv.addFooterView(layout);
  
	 }
	
	@Override
	protected void onResume()
	{
		super.onResume();
		ScavengerHunt myApp = (ScavengerHunt) getApplication();		
		this.setListAdapter(new DestinationArrayAdapter(this, myApp.getDestinations())); 
		
		pointView.setText("Total Points: "+myApp.points);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Get the item that was clicked
		Intent intent = new Intent(LocationList.this,Hints.class);
    	intent.putExtra("destID", position);
		startActivity(intent);
	}

	  
}
