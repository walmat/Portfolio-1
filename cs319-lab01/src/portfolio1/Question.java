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
	
	public static ArrayList<Question> questions = new ArrayList<Question>(); //ArrayList for pulling questions
	
	//globals for access outside of Questions.java
	public String category;
	public String question;
	public String rightAnswer;

	/*
	 * Empty Constructor 
	 */
	public Question() { 
		
	}
	
	public Question(String category, String question, String rightAnswer) {
		this.category = category;
		this.question = question;
		this.rightAnswer = rightAnswer;
	}
	
	/*
	 * helper method for determining the categories
	 */
	
	public void fillQuestionList () {
		String site = "http://www.classicweb.com/usr/jseng/trivi.htm";
		try {
			//get the site and set the timeout to 10 seconds
			Document doc = setupConnection(site);	
			
			//build the Arraylist based on the "pre" element
			ArrayList<String> list = buildList(doc);
			
			//parse the category, question, and answer into a Question object and store it in list
			parseData(list);
			
		} catch (IOException e){
			System.out.println("Cannot fetch html from " + e.getMessage());
		}	
	}
	
	private static boolean isAllUpper(String s) {
	    for(char c : s.toCharArray()) {
	       if(Character.isLetter(c) && Character.isLowerCase(c)) {
	           return false;
	        }
	    }
	    return true;
	}
	
	static Document setupConnection(String site) throws IOException{
		return Jsoup.connect(site).timeout(10*1000).get();
	}
	
	static ArrayList<String> buildList(Document d){
		ArrayList<String> list = new ArrayList<String>();
		Elements pre = d.select("pre");
		String q = "";
		for (Element pres:pre) {
			q = pres.text();
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
	
	static void parseData(ArrayList<String> list) {
		int cat_index = 0;
		//int index = 0;
		for (int i = 1; i < list.size();i++){
			do {
				String str = list.get(i);
				String rightAnswer = ""; 
				String question = "";
				//parse the answer and question from line read in from each list element
				for (int j = 0; j < str.length(); j++){
					if (str.charAt(j) == '?'){
						while (j < str.length()){
							rightAnswer += str.charAt(j++);
						}
					}
					else {
						question += str.charAt(j);
					}	
				}
				//remove extraneous characters from the answer
				rightAnswer = rightAnswer.replaceAll("[^A-Za-z0-9 ]", "").trim();
				
				//TODO -- figure out why it's rewriting previous instances of a Question();
				questions.add(new Question(list.get(cat_index), question.trim() + "?", rightAnswer)); //rewriting previous question for some reason??
				/* //testing purposes
				System.out.println(questions.get(index).category);
				System.out.println(questions.get(index).question);
				System.out.println(questions.get(index).rightAnswer);
				*/
				i++; //index++;
				if (i >= list.size()){
					break;
				}
			} while (!isAllUpper(list.get(i)));
			cat_index = i;
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
