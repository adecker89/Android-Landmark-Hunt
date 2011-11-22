package bang.scavengerhunt;

import java.util.Vector;

import bang.scavengerhunt.Destination.Hint;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Activity for displaying hints for the given location to the user.
 * This activity expects the intent to have putExtra("destID",<int>) which specifies
 * the index into the global destination array that this instance represents.
 * @author Alex Decker
 */
public class Hints extends Activity {
	
	ScavengerHunt myApp;
	Destination dest;
	Vector<View> hintViews = new Vector<View>();
	
	PendingIntent pendingIntent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hints);
           
        int id = getIntent().getIntExtra("destID", 0);
        myApp = (ScavengerHunt) getApplication();
        dest = myApp.getDestination(id);
        
        setupLocation(id, dest.getRadius());
        
        //set title
        ((TextView)findViewById(R.id.hints_title)).setText("Location #"+(id+1));
        
        //set points
        updatePoints(dest.getCurrentHint());
        
        //add each hint to the layout
        LinearLayout layout = (LinearLayout) findViewById(R.id.hintLayout);        
        for(int i=0; i < dest.getHintCount(); i++) {
        	View v = getView(dest.getHint(i));
        	if(v !=  null) {
	        	layout.addView(v);
	        	hintViews.add(v);
        	}
        }        
        
        //New Hint Button
        Button button = (Button) findViewById(R.id.new_hint_button);      
        button.setOnClickListener(new OnClickListener() {      
	        public void onClick(View v){
	        	int next = dest.revealNextHint();
	        	setVisibility(hintViews.get(next),true);
	        	updatePoints(dest.getCurrentHint());
	        }
	    });
    }
	
	@Override
	protected void onStop() {
		super.onStop();
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		lm.removeProximityAlert(pendingIntent);
		pendingIntent.cancel();
	}

	private void setupLocation(int id, int radius) {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        //create a pending intent to launch the questions activity
        Intent intent = new Intent(Hints.this,Questions.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("destID", id);
        pendingIntent = PendingIntent.getActivity(this, 0, 
        		intent, PendingIntent.FLAG_ONE_SHOT);
        
        //if we are already at the location launch the intent
        Location current_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(dest.isDiscovered())
        {
        	try { 
        		pendingIntent.send();
        		finish();
        	}
        	catch (CanceledException e) {e.printStackTrace();}
        }
        else if(current_loc != null && current_loc.distanceTo(dest.getLocation()) < radius)
        {
			try {
				pendingIntent.send();
				finish();
			} catch (CanceledException e) {e.printStackTrace();}
        }
        else //otherwise hand it off to a proximity alert
        	lm.addProximityAlert(dest.getLatitude(), dest.getLongitude(), 
        			radius, -1, pendingIntent);
	}
	
	public View getView(Hint hint) {
		View view = null;
		if(hint.getType().equals("textHint")) {
			LinearLayout layout = new LinearLayout(this);
			TextView tv = new TextView(this);
			tv.setText(hint.getData());
			tv.setGravity(Gravity.LEFT);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			
			TextView bullet = new TextView(this);
			bullet.setText("�");
			LinearLayout.LayoutParams lm = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT );
			lm.setMargins(0, 0, 10, 0);
			bullet.setLayoutParams(lm);
			bullet.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			
			layout.addView(bullet);
			layout.addView(tv);
			view = layout;
		}
		if(hint.getType().equals("imageHint")) {
			ImageView iv = new ImageView(this);
			iv.setScaleType(ScaleType.CENTER_INSIDE);
			iv.setPadding(5, 5, 5, 5);
			iv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			Resources res = this.getResources();
			int id = res.getIdentifier(hint.getData(), "drawable", "bang.scavengerhunt");
			iv.setImageResource(id);
			view = iv;
		}
		setVisibility(view,hint.getVisible());
	
		return view;
	}
	
	private void setVisibility(View v, boolean visibility) {
		if(v == null) return;
		if(visibility)
			v.setVisibility(View.VISIBLE);
		else
			v.setVisibility(View.INVISIBLE);
	}
	
	private void updatePoints(Hint hint)
	{
		((TextView)findViewById(R.id.hints_points_remaining)).setText("Points Remaining: "+hint.getPoints()+"/"+hint.getMaxPoints());
		((TextView)findViewById(R.id.hints_point_total)).setText("Total Points: "+myApp.points);
	}
}
