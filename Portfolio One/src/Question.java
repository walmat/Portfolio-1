package portfolio1;

/**
 * author - Matthew Wall © 2016
 * email: mwall@iastate.edu
 */

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Question implements Serializable {
<<<<<<< HEAD:Portfolio One/src/Question.java

	private static final long serialVersionUID = 1L;

=======
	
>>>>>>> Donavan:cs319-lab01/src/portfolio1/Question.java
	public static ArrayList<Question> questions = new ArrayList<Question>(); //ArrayList for pulling questions
	
	//globals for access outside of Questions.java
	public String category;
	public String question;
	public String rightAnswer;

	/**
	 * Empty constructor
	 */
	public Question() { 
		
	}
	
	/**
	 * Constructor providing a category, question, and right answer
	 * @param category - question's category
	 * @param question - question string
	 * @param rightAnswer - the answer to the provided question
	 */
	public Question(String category, String question, String rightAnswer) {
		this.category = category;
		this.question = question;
		this.rightAnswer = rightAnswer;
	}
	
	/*
	 * helper method for determining the categories
	 */
	
	/**
	 * Fetches the data from the site, builds an ArrayList and then parses the Questions
	 * from that ArrayList.
	 */
	public void fillQuestionList () {
		String site = "http://www.classicweb.com/usr/jseng/trivi.htm";
		try {
			//get the site and set the timeout to 10 seconds
			Document doc = setupConnection(site);	
			
			//build the Arraylist based on the "pre" element
			ArrayList<String> list = buildList(doc, "pre");
			
			//parse the category, question, and answer into a Question object and store it in list
			parseData(list);
			
		} catch (IOException e){
			System.out.println("Cannot fetch html from " + e.getMessage());
		}	
	}
	
	/**
	 * helped method to determine if the given string is all uppercase
	 * @param s - string to be checked
	 * @return - true if the string is all uppercase, else false
	 */
	private static boolean isAllUpper(String s) {
	    for(char c : s.toCharArray()) {
	       if(Character.isLetter(c) && Character.isLowerCase(c)) {
	           return false;
	        }
	    }
	    return true;
	}
	
	/**
	 * grabs a connection to a particular site
	 * @param site - HTML site to connect to
	 * @return - the DOM at the given site
	 * @throws IOException - if we couldn't connect to the site
	 */
	static Document setupConnection(String site) throws IOException{
		return Jsoup.connect(site).timeout(10*1000).get();
	}
	
	/**
	 * builds a new ArrayList based on the "pre" element in the site
	 * @param d - the document to access
	 * @param element - the specified element to fetch from
	 * @return - an ArrayList structured from the parsed text in the document's element
	 */
	static ArrayList<String> buildList(Document d, String element){
		ArrayList<String> list = new ArrayList<String>();
		Elements e = d.select(element);
		String q = "";
		for (Element parse : e) {
			q = parse.text();
		}
		Scanner s = new Scanner(q);
		
		//handle fetching questions and sorting into categories and questions
		
		while (s.hasNextLine()){
			String line = s.nextLine();
			if (line.equals("")){
				continue;
			}
			if (line.contains("?")){
				list.add(line.trim());
			}
			if(isAllUpper(line)){
				list.add(line.trim());
			}
		}
		s.close();
		return list;
	}
	
	/**
	 * for each element in the ArrayList, seperate the category,
	 * question, and answer
	 * @param list - the ArrayList to parse data from
	 */
	static void parseData(ArrayList<String> list) {
		int cat_index = 0;
		for (int i = 1; i < list.size();i++){ //will skip over the categories because i increments twice 
											  // – once inside – another outside of the do{}while();
			do {
				String str = list.get(i); //grabs each question
				String rightAnswer = ""; 
				String question = "";
				for (int j = 0; j < str.length(); j++){ //parse the answer and question from the line read in from each list element
					if (str.charAt(j) == '?'){
						while (j < str.length()){
							rightAnswer += str.charAt(j++);
						}
					}
					else {
						question += str.charAt(j);
					}	
				}
				rightAnswer = rightAnswer.replaceAll("[^A-Za-z0-9 ]", "").trim(); //remove extraneous characters from the answer
				
				questions.add(new Question(list.get(cat_index), question.trim() + "?", rightAnswer));

				i++; //increment to next question
				if (i >= list.size()){
					break;
				}
			} while (!isAllUpper(list.get(i)));
			cat_index = i; //go to next category
		}
	}
	
	
	public String toString()
	{
		return category + " | " + question + " | " + rightAnswer;
		
	}
	
	/*
	 * getters and setters for Question
	 */
	
///////////////////////////////////////////////////

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question_) {
		question = question_;
	}

	public String getRightAnswer() {
		return rightAnswer;
	}
	
	public void setRightAnswer(String rightAnswer_) {
		rightAnswer = rightAnswer_;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category_) {
		category = category_;
	}
}
