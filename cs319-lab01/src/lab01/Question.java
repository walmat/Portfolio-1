package portfolio1;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Question {
	
	private String category;
	private String question;
	private String rightAnswer;
	private String wrongAnswer1;
	private String wrongAnswer2;
	private static ArrayList<Question> questions = new ArrayList<Question>();
	
	
	public Question(String category, String question, String rightAnswer,
			String wrongAnswer1, String wrongAnswer2) {
		this.category = category;
		this.question = question;
		this.rightAnswer = rightAnswer;
		this.wrongAnswer1 = wrongAnswer1;
		this.wrongAnswer2 = wrongAnswer2;
	}

	public static void main(String[] args) {

		String site = "http://www.classicweb.com/usr/jseng/trivi.htm";
		try {
			Document doc = Jsoup.connect(site).timeout(10*1000).get();
			ArrayList<String> list = new ArrayList<String>();
			
			Elements pre = doc.select("pre");
			String q = "";
			for (Element pres:pre) {
				q = pres.text();
			}
			Scanner s = new Scanner(q);
			//handle fetching questions and sorting into categories
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
			int cat_index = 0;
			for (int i = 1; i < list.size(); i++){
				do {
					questions.add(new Question(list.get(cat_index), list.get(i),"","","'"));
				} while (!isAllUpper(list.get(i)));
				cat_index = i;
			}
			
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
}

