package bang.scavengerhunt;

import android.app.Application;

/**
 * Overriding the Application class in order to add global variables
 * that can be shared across multiple Activities.
 * @author Alex Decker
 */
public class ScavengerHunt extends Application {
	//global variables
	private Destination destinations[];
	public int points = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		//parse the xml when the application loads
		destinations = Destination.parseDestinations(
				this.getResources().getXml(R.xml.destinations));
		points = 0;
	}
	
	public Destination[] getDestinations() { return destinations; }
	public Destination getDestination(int index) {return destinations[index];}
	public int getDestCount() {return destinations.length;}
}
