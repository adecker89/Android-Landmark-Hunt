package bang.scavengerhunt;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Activity for displaying various debug information. Prints out the global
 * destination array. Should be removed/password protected/well hidden for
 * real-world usage
 * 
 * @author Alex Decker
 */
public class Debug extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ScrollView sv = new ScrollView(this);
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);

		ScavengerHunt myApp = (ScavengerHunt) getApplication();

		for (int i = 0; i < myApp.getDestCount(); i++) {
			TextView tv = new TextView(this);
			tv.setText(myApp.getDestination(i).toString());
			layout.addView(tv);
		}

		sv.addView(layout, lp);
		setContentView(sv, lp);
	}
}
