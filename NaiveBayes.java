/*
* Kate Evans
* Project 1 - Naive Bayes Classifier
* 02/24/2016
*/

import java.util.*;
import java.io.*;

public class NaiveBayes{

	private String [] dictionary = new String[0];
	private HashMap<String,Integer> positive = new HashMap<String,Integer>();
	private HashMap<String,Integer> negative = new HashMap<String,Integer>();
	private int positiveReviews = 0;
	private int negativeReviews = 0;
	private int totalReviews = 0;
	

	public static void main(String[] args) throws IOException{

    Scanner scan = new Scanner(System.in);
    String testSplit = "";
    String trainingSplit = "";
    boolean evidentialLearning = false;
    ArrayList<String[]> results = new ArrayList<String[]>();
    PrintWriter writer = new PrintWriter("output.txt", "UTF-8");

    System.out.println("Choose an option A or B: \n\tA) 50/50\n\tB) 80/20");
    String input = scan.nextLine();
    if(input.equals("A") || input.equals("a")){
       testSplit = "50";
       trainingSplit = "50";
    }
    else if(input.equals("B") || input.equals("b")){
       testSplit = "20";
       trainingSplit = "80";
       System.out.println("Evidential learning? Y/N: ");
       String evidential = scan.nextLine();
       if(evidential.equals("Y") || evidential.equals("y")){
          evidentialLearning = true;
       }
    }
    else{
      System.out.println("Invalid input.\n Choose an option A or B: \n\tA) 50/50\n\tB) 80/20");
      input = scan.nextLine();
    }


		NaiveBayes nb = new NaiveBayes();
		ArrayList<String[]> table = nb.readTraining(trainingSplit);
		ArrayList<HashMap<String,Double>> probabilities = nb.train(table);
    
    if(evidentialLearning){ 
      System.out.println("Initial Training Table:");
      writer.println("Initial Training Table:");
      for(int h = 0; h < table.size(); h++){
        String[] t = table.get(h);
        System.out.println(t[0] + "\t" + t[1]);
        writer.println(t[0] + "\t" + t[1]);
      }
      Scanner fileScan = new Scanner(new File(testSplit + "Test.txt"));
      while(fileScan.hasNextLine()){
          String line = fileScan.nextLine();
          String [] newEntry = nb.test(probabilities.get(0),probabilities.get(1), line);
          results.add(newEntry);
          table.add(newEntry);
          if(newEntry[1].equals("P")){
            nb.positiveReviews++;
          }
          else if(newEntry[1].equals("N")){
            nb.negativeReviews++;
          }
          else{
            System.out.println("########ERROR LINE 76########");
          }
          nb.totalReviews++;
          probabilities = nb.train(table);
      }
      System.out.println("\nFinal Training Table:");
      writer.println("\nFinal Training Table:");
      for(int i = 0; i < table.size(); i++){
        String[] t = table.get(i);
        System.out.println(t[0] + "\t" + t[1]);
        writer.println(t[0] + "\t" + t[1]);
      }
      System.out.println("\nResults Table:\nWords\tPrediction\tAnswer");
      writer.println("\nResults Table:\nWords\tPrediction\tAnswer");
      for(int x = 0; x < results.size(); x++){
        String[] t = results.get(x);
        System.out.println(t[0] + "\t" + t[1] + "\t" + t[2]);
        writer.println(t[0] + "\t" + t[1] + "\t" + t[2]);
      }
      writer.close();
    }
    else{
      Scanner fileScan = new Scanner(new File(testSplit + "Test.txt"));
      while(fileScan.hasNextLine()){
          String line = fileScan.nextLine();
          results.add(nb.test(probabilities.get(0),probabilities.get(1), line));
      }
      System.out.println("Training Table:");
      writer.println("Training Table:");
      for(int j = 0; j < table.size(); j++){
        String[] t = table.get(j);
        System.out.println(t[0] + "\t" + t[1]);
        writer.println(t[0] + "\t" + t[1]);
      }
      System.out.println("\nResults Table:\nWords\tPrediction\tAnswer");
      writer.println("\nResults Table:\nWords\tPrediction\tAnswer");
      for(int x = 0; x < results.size(); x++){
        String[] t = results.get(x);
        System.out.println(t[0] + "\t" + t[1] + "\t" + t[2]);
        writer.println(t[0] + "\t" + t[1] + "\t" + t[2]);
      }
      writer.close();
    }
    

	}//end main

	private ArrayList<String[]> readTraining(String trainingSplit)throws IOException{
		System.out.println("Reading dictionary..."); 
		Scanner dictionaryScan = new Scanner(new File("dictionary.txt"));
		dictionary = dictionaryScan.nextLine().split(",");
		
		for(int z = 0; z < dictionary.length; z++){
			positive.put(dictionary[z],0);
			negative.put(dictionary[z],0);
		}

		ArrayList<String[]> trainingTable = new ArrayList<String[]>();

		System.out.println("Reading file...");
	    Scanner fileScan = new Scanner(new File(trainingSplit + "Training.txt"));

        while(fileScan.hasNextLine()){
          String line = fileScan.nextLine();
          String bag= "";
          String[] parts = line.split("@");
          totalReviews++;
          if(parts[1].trim().equals("P")){
          	positiveReviews++;
          }
          else if(parts[1].trim().equals("N")){
          	negativeReviews++;
          }
          else{
          	System.out.println("########ERROR LINE 54########");
          }

          Scanner lineScan = new Scanner(parts[0]);
          while(lineScan.hasNext()){
            String word = lineScan.next();
            for(int i = 0; i < dictionary.length; i++){
          	  if(word.toLowerCase().equals(dictionary[i])){
            		bag = bag + word.toLowerCase() + ",";
          	  }
            }
         }
         trainingTable.add(new String[] {bag, parts[1]});
      }
	    System.out.println("totalReviews: " + totalReviews);
      return trainingTable;
	}//end readTraining

	private ArrayList<HashMap<String,Double>> train(ArrayList<String[]> table){
	  System.out.println("Calculating prior probabilities and conditional independence");
      for(int i = 0; i < table.size(); i++){
        String[] s = table.get(i);
        String[] w = s[0].split(",");
        for(int j = 0; j < w.length; j++){
          if(s[1].trim().equals("P")){
            
             //System.out.println(positive.containsKey(w[j].toLowerCase()) + "\t" + w[j].toLowerCase());
             positive.put(w[j].toLowerCase(), positive.get(w[j].toLowerCase()) + 1);
          }
          else if(s[1].trim().equals("N")){
            negative.put(w[j].toLowerCase(), negative.get(w[j].toLowerCase()) + 1);
          }
          else{
            System.out.println("########ERROR LINE 90#########");
          }
        }
      }
      int uniqueWords = dictionary.length;
      int countNeg = 0;
      int countPos = 0;
      for (int val : negative.values()) {
        countNeg = countNeg + val;
      }
      for (int val : positive.values()) {
        countPos = countPos + val;
      }

    HashMap<String,Double> posProbs = new HashMap<String,Double>();
	  HashMap<String,Double> negProbs = new HashMap<String,Double>();

      for(int c = 0; c < dictionary.length; c++){
          posProbs.put(dictionary[c], (positive.get(dictionary[c]) + 1)/(double)(countPos + uniqueWords));
          negProbs.put(dictionary[c], (negative.get(dictionary[c]) + 1)/(double)(countNeg + uniqueWords));
      }
      ArrayList<HashMap<String,Double>> r = new ArrayList<HashMap<String,Double>>();
      r.add(posProbs);
      r.add(negProbs);
      return r;
	}//end train

	private String[] test(HashMap<String,Double> pos, HashMap<String,Double> neg, String line)throws IOException{
		
		double positiveOutcome = (double) positiveReviews/totalReviews;
		double negativeOutcome = (double) negativeReviews/totalReviews;
		Scanner fileScan = new Scanner(new File("50Test.txt"));
		int count = 0;
    String classification = "";
		//while(fileScan.hasNextLine()){
          
          String[] parts = line.split("@");
          String bag= "";
          count++;

          Scanner lineScan = new Scanner(parts[0]);
          while(lineScan.hasNext()){
            String word = lineScan.next();
            for(int i = 0; i < dictionary.length; i++){
          	  if(word.toLowerCase().equals(dictionary[i])){
                bag = bag + word.toLowerCase() + ",";
          	  	//System.out.println(word.toLowerCase());
          		  positiveOutcome = positiveOutcome * pos.get(word.toLowerCase());
          		  negativeOutcome = negativeOutcome * neg.get(word.toLowerCase());
          	  }
            }
          }
         // System.out.println("Count: " + count);
         // System.out.println("Pos: " + positiveOutcome);
         // System.out.println("Neg: " + negativeOutcome);
          if(positiveOutcome > negativeOutcome){
            classification = "P";
          }
          else{
            classification = "N";
          }
         // System.out.println("Prediction: " + classification + "\t Answer: " + parts[1] + "\n");
        //}
    return new String[] {bag, classification, parts[1]};
	}//end test

}