package portfolio1;

import java.io.Serializable;

public class Answer implements Serializable{

	private static final long serialVersionUID = 1L;
	public String answer;
	public String port;
	
	/**
	 * default constructor for Answer
	 * @param answer - client's answer
	 * @param port - client's port number
	 */
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
	
	/**
	 * allows us to view the answer for each client
	 */
	@Override
	public String toString(){
		return answer;
	}
	
}	
