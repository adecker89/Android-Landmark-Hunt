package bang.scavengerhunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Main splash screen with a couple options
 * @author Alex Decker
 */
public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);
        
        Button newHunt = (Button) findViewById(R.id.title_new_button);
        newHunt.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		ScavengerHunt myApp = (ScavengerHunt) getApplication();
        		myApp.onCreate();
        		Intent intent = new Intent(Main.this,LocationList.class);
        		startActivity(intent);
        	}
        });
        Button continueHunt = (Button) findViewById(R.id.title_continue_button);
        continueHunt.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent intent = new Intent(Main.this,LocationList.class);
        		startActivity(intent);
        	}
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.debug:
    		startActivity(new Intent(Main.this,Debug.class));
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);	
    	}
    }
}