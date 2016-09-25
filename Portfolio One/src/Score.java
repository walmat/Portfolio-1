package portfolio1;

import java.io.Serializable;

public class Score implements Serializable{

	public int port;
	public String username;
	public int score;
	
	public Score(int port_, String username_, int score_) {
		port = port_;
		username = username_;
		score = score_;
	}
	
	/**
	 * copy constructor 
	 * @param a - answer to be copied
	 */
	public Score(Score s) {
		this(s.port, s.username, s.score);
	}
	
	@Override
	public String toString(){
		return port + " | " + username + " | " + score + " | ";
	}
	

}
	
