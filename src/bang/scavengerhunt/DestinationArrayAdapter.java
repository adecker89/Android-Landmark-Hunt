package bang.scavengerhunt;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DestinationArrayAdapter extends ArrayAdapter<Destination> {
	private final Activity context;
	private final Destination[] dests;

	public DestinationArrayAdapter(Activity context, Destination[] dests) {
		super(context, R.layout.location_row_layout, dests);
		this.context = context;
		this.dests = dests;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.location_row_layout, null, true);
		
		((TextView)rowView.findViewById(R.id.list_item_number)).setText((position+1)+".");
		
		TextView points = ((TextView)rowView.findViewById(R.id.list_item_points));
		points.setText(dests[position].getPoints()+"/"+dests[position].getMaxPoints());
		
		if(dests[position].isDiscovered())
			((TextView)rowView.findViewById(R.id.list_item_label)).setText(dests[position].getName());
		else
			((TextView)rowView.findViewById(R.id.list_item_label)).setText("????????");

		
		return rowView;
	}
		
}
