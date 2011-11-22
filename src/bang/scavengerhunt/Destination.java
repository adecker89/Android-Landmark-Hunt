package bang.scavengerhunt;

import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.location.Location;
import android.location.LocationManager;

/**
 * Class for holding and accessing various data about a location,
 * such as, the name, associated hints, and questions.
 * @author Alex Decker
 */
public class Destination {
	private static final int DEFAULT_HINT_POINTS = 10;
	private static final int DEFAULT_QUESTION_POINTS = 10;
	
	public Destination(int id, String name, double latitude, double longitude, int radius) {
		this.id = id;
		this.name = name;
		
		location = new Location(LocationManager.GPS_PROVIDER);
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		this.radius = radius;
	}
	
	private int id;
	private String name;
	
	private Location location;
	private int radius;
	
	private boolean discovered = false;
	
	private Vector<Hint> hints = new Vector<Hint>();
	private int hintsRevealed = 1;
	
	private Vector<Question> questions = new Vector<Question>();
	private int currentQuestion = 0;
	
	//accessors
	public String getName() {return name;}
	public int getID() {return id;}
	public Location getLocation() {return location;}
	public double getLatitude() {return location.getLatitude();}
	public double getLongitude() {return location.getLongitude();}
	
	public boolean isDiscovered() { return discovered; }
	public void setDiscovered() { discovered = true; }
	public void reveal(boolean reveal) { this.discovered = reveal; }
	
	public class Hint {
		public Hint(String type, String data, int points) {
			this.type=type;this.data=data;this.points=points;
		}
		private String type;
		private String data;
		private int points;
		private boolean visible = false;
		
		public String getType() { return type; }
		public String getData() { return data; }
		public int getPoints() { return points; }
		
		public void setVisible(boolean visible) { this.visible = visible; }
		public boolean getVisible() { return visible; }
		public int getMaxPoints() {
			return 16; //yep
		}
	}
	
	public int getHintCount() { return hints.size(); }
	
	public void addHint(String type, String data, int points) {
		//trim off any extension if they happened to be given
		if(type.equals("imageHint"))data = data.substring(0, data.lastIndexOf('.'));
		
		hints.add(new Hint(type,data,points));
		if(getHintCount() == 1) getHint(0).setVisible(true);
	}
	public Hint getHint(int index) {return hints.get(index);}
	
	public Hint getCurrentHint() { return getHint(hintsRevealed-1); }
	
	public int revealNextHint() {
		if(hintsRevealed < getHintCount()) {
			hintsRevealed++;
			getCurrentHint().setVisible(true);			
		}
		return hintsRevealed-1;
	}
	
	private class Question 
	{
		public Question(String prompt, String answer, int points) {
			this.prompt=prompt;this.answer=answer;this.points=points;
		}
		public String prompt;
		public String answer;
		public int points;
	}
	
	public void addQuestion(String prompt, String answer, int points) 
	{ 
		questions.add(new Question(prompt,answer,points));
	}
	
	public int getNextQuestionIndex() { return currentQuestion; }
	
	public Question getQuestion() { return questions.get(currentQuestion); }
	
	public int answerQuestion(String guess) 
	{
		Pattern pattern = Pattern.compile(guess, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(guess);
		
		Question question = getQuestion();
		if(matcher.matches() && currentQuestion < getQuestionCount()) 
		{
			currentQuestion++;
			return question.points;
		}
		else
			return 0;
		
	}
	
	public int getQuestionIndex() { return currentQuestion; }
	public int getQuestionCount() { return questions.size(); }	
	
	public String getQuestionPrompt(int index) {
		if(index >= 0 && index < questions.size()) return questions.get(index).prompt;
		else return "";
	}
	
	public String getQuestionPrompt() { return getQuestionPrompt(currentQuestion); }

	@Override
	public String toString() {
		String ret = String.format("Destination %2d: %s\n",id,name);
		ret += "   Hints:\n";
		for(int i=0; i < hints.size(); i++)
			ret += String.format("       %s: %s\n",hints.get(i).type,hints.get(i).data);
		ret += "   Questions:\n";
		for(int i=0; i < questions.size(); i++)
			ret += String.format("       %s: %s\n",questions.get(i).prompt,questions.get(i).answer);
		
		return ret;
	}
	
	/**
	 * Populates the Destination class with information parsed from an xml resource file
	 * 
	 * @param xpr XmlResourceParser likely obtained from getXml() function
	 * @return List of populated Destination Objects
	 */
	public static Destination[] parseDestinations(XmlResourceParser xpr ) {	
		Vector<Destination> dests = new Vector<Destination>();		
		int event = 0;
		try {
			event = xpr.getEventType();
	    	while(event != XmlPullParser.END_DOCUMENT) {
	    		if(event == XmlPullParser.START_TAG) {
	    			String name = xpr.getName();
	    			if(name.equals("destination")) {
	    				dests.add(new Destination(dests.size(), //size() is used as a numeric id
	    						xpr.getAttributeValue(null,"name"),
	    						xpr.getAttributeFloatValue(null, "latitude", 0),
	    						xpr.getAttributeFloatValue(null, "longitude", 0),
	    						xpr.getAttributeIntValue(null, "radius", 0)));    				
	    			}
	    			else if(name.equals("hintList")) 		{ hintParser(xpr,dests); }
	    			else if(name.equals("questionList")) 	{ questionParser(xpr,dests); }    			
	    		}
	    		event = xpr.next();
	    	}
	    	xpr.close();
		} catch (Exception e) {
			/* Display Failure to Parse Message*/
			e.printStackTrace();
		}
		Destination[] ret = new Destination[dests.size()];
		dests.toArray(ret);
		return ret;
    }

	/**
	 * Used by parseDestination. Handles adding hints.
	 * @throws XmlPullParserException
	 * @throws IOException
	 **/
	private static void hintParser(XmlResourceParser xpr, Vector<Destination> dests) throws XmlPullParserException, IOException {
		int event = xpr.next();
		while( !(event == XmlPullParser.END_TAG && xpr.getName().equals("hintList")) ) {
			if(event == XmlPullParser.START_TAG) {
				String name = xpr.getName();
				int points = xpr.getAttributeIntValue(null, "points", DEFAULT_HINT_POINTS);
				if(points <= 0) points = DEFAULT_HINT_POINTS;
				if(name.equals("textHint")) {
					dests.lastElement().addHint(name,xpr.nextText(),points);
				}
				else if(name.equals("imageHint")) {
					dests.lastElement().addHint(name,xpr.getAttributeValue(null, "src"),points);
				}
				/*else if(name.equals("radarHint")) {
					dests.lastElement().addHint(name,"",points);
				}*/
			}
			event = xpr.next();
		}		
	}
	
	/**
	 * Used by parseDestination. Handles adding questions.
	 * @throws XmlPullParserException
	 * @throws IOException
	 **/
	private static void questionParser(XmlResourceParser xpr, Vector<Destination> dests) throws XmlPullParserException, IOException {
		int event = xpr.next();
		String prompt = "";
		String answer = "";
		
		while( !(event == XmlPullParser.END_TAG && xpr.getName().equals("questionList")) ) {
			if(event == XmlPullParser.START_TAG) {
				String name = xpr.getName();
				if(name.equals("prompt")) {
					prompt = xpr.nextText();
				}
				else if(name.equals("answer")) {
					answer = xpr.nextText();
				}
			}
			else if(event == XmlPullParser.END_TAG && xpr.getName().equals("question")) {
				int points = xpr.getAttributeIntValue(null, "points", DEFAULT_QUESTION_POINTS);
				if(points <= 0) points = DEFAULT_QUESTION_POINTS;
				dests.lastElement().addQuestion(prompt, answer,points);
				prompt = "";
				answer = "";
			}
			event = xpr.next();
		}		
		
	}
	public int getPoints() {
		int points = 0;
		if(getHintCount() > 0 && isDiscovered()) points += getCurrentHint().points;
		if(getQuestionCount() > 0) 
		{
			for(int i=0; i<getQuestionIndex(); i++)
				points += questions.get(i).points;
		}
		return points;
	}
	public int getMaxPoints() {
		int points = 0;
		if(getHintCount() > 0) points += getCurrentHint().getMaxPoints();
		if(getQuestionCount() > 0) 
		{
			for(int i=0; i<getQuestionCount(); i++)
				points += questions.get(i).points;
		}
		return points;
	}
	public int getRadius() { return radius;	}
}