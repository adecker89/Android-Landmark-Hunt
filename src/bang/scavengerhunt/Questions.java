package bang.scavengerhunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Questions extends Activity {
	int id;
	ScavengerHunt myApp;
	Destination dest;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.questions);
        
        id = getIntent().getIntExtra("destID", 0);
        myApp = (ScavengerHunt) getApplication();
        dest = myApp.getDestination(id);
        if(!dest.isDiscovered()) {
        	myApp.points += dest.getCurrentHint().getPoints();
        	dest.setDiscovered();
        }
        
        //set activity title
        ((TextView) findViewById(R.id.Question_Location)).setText(dest.getName());
        updateActivity();
        
        final EditText answer = (EditText) findViewById(R.id.Answer);
        answer.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) 
                {
                	//if correct go to the next question
                	int points = dest.answerQuestion(answer.getText().toString());
                	if(points > 0) 
                	{
                		myApp.points += points;
                		updateActivity();
                	}
                	else
                	{
                		Toast.makeText(Questions.this, 
                				"Sorry, that's incorrect.\nTry Again.", 
                				Toast.LENGTH_SHORT).show();
                	}
                  return true;
                }
                return false;
            }
        });
        
        answer.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				/* TODO consumes touch event to prevent software keyboard popup
				 * should probably remove later
				 */
				return true; 
			}
         });
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        Intent intent = new Intent(Questions.this,LocationList.class);
	        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void updateActivity()
	{
		if(dest.getQuestionIndex() == dest.getQuestionCount())
		{
			Intent intent = new Intent(Questions.this,LocationList.class);
	        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
			finish();
		}
		else
		{
			((TextView) findViewById(R.id.Prompt)).setText(dest.getQuestionPrompt());
			((TextView) findViewById(R.id.Answer)).setText("");
		}
	}
}
