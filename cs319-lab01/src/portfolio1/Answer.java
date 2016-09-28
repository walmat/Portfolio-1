package portfolio1;

/**
 * @author Matt Wall
 */
import java.io.Serializable;

public class Answer implements Serializable{

	public String answer;
	public String port;
	
	public Answer(String answer, String port) {
		this.answer = answer;
		this.port = port;
	}
	
	/**
	 * copy constructor 
	 * @param a - answer to be copied
	 */
	public Answer(Answer a) {
		this(a.answer, a.port);
	}
	
	@Override
	public String toString(){
		return answer;
	}
	
}	
