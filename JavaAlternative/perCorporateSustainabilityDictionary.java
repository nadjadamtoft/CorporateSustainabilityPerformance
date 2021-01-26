import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.tokenize.WhitespaceTokenizer;

public class perCorporateSustainabilityDictionary {
	private static void outputListToCsv(ArrayList<CorporateSustainabilityLine> resultLines) {
		try {
			File myObj = new File("\\Counts\\JavaCorporateSustainabilityDictionary.csv");
			myObj.createNewFile();
			FileWriter fw = new FileWriter(myObj.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("word, count\n");

			for (int i=0; i< resultLines.size(); i++) {
				bw.write(resultLines.get(i).name + ", " + resultLines.get(i).ecoValue + ", " + resultLines.get(i).envValue + ", " + resultLines.get(i).socValue + "\n");
			}
			bw.close();
		} catch (IOException e) {
		}
	}
	public static void main(String[] args) {
		FileInputStream fis;
		Scanner sc;
		ArrayList<String> ecoWords = new ArrayList<>();
		ArrayList<String> envWords = new ArrayList<>();
		ArrayList<String> socWords = new ArrayList<>();
		ArrayList<CorporateSustainabilityLine> results = new ArrayList<>();
		try {
			//full path to dictionary
			fis = new FileInputStream("\\SustainabilityDictionaries\\Economicdictionary.csv");
			sc = new Scanner(fis);
			while (sc.hasNextLine()) {
				ecoWords.add(sc.nextLine().replace("\"", ""));
			}
			sc.close();
			fis = new FileInputStream("\\SustainabilityDictionaries\\Environmentaldictionary.csv");
			sc = new Scanner(fis);
			while (sc.hasNextLine()) {
				envWords.add(sc.nextLine().replace("\"", ""));
			}
			sc.close();
			fis = new FileInputStream("\\SustainabilityDictionaries\\Socialdictionary.csv");
			sc = new Scanner(fis);
			while (sc.hasNextLine()) {
				socWords.add(sc.nextLine().replace("\"", ""));
			}
			sc.close();
			String ecoString= "\\b" + String.join("\\b|\\b", ecoWords) + "\\b";
			String envString = "\\b" + String.join("\\b|\\b", envWords)+ "\\b";
			String socString = "\\b" + String.join("\\b|\\b", socWords)+ "\\b";
			Pattern ecoPattern = Pattern.compile(ecoString);
			Pattern envPattern = Pattern.compile(envString);
			Pattern socPattern = Pattern.compile(socString);
			WhitespaceTokenizer wordBreaker = WhitespaceTokenizer.INSTANCE;
			File directoryPath = new File("\\TextFiles");
			String contents[] = directoryPath.list();
			for (int k = 0; k < contents.length; k++) {
				String sentence = Files.readString(Path.of("\\TextFiles\\" + contents[k]));
				sentence = sentence.toLowerCase();
				sentence = sentence.replaceAll("[^A-Za-z—\\-\\'\\’\\ ]", " ").replaceAll("\\s+", " ");
				
				sc = new Scanner(fis,StandardCharsets.UTF_8);
				float normalizationFactor;
				ArrayList<String> ecoMatches = new ArrayList<String>();
				ArrayList<String> envMatches = new ArrayList<String>();
				ArrayList<String> socMatches = new ArrayList<String>();
				ArrayList<String> words = new ArrayList<String>();
				words.addAll(Arrays.asList(wordBreaker.tokenize(sentence)));	
				Matcher ecoMatcher = ecoPattern.matcher(sentence);	
				Matcher envMatcher = envPattern.matcher(sentence);	
				Matcher socMatcher = socPattern.matcher(sentence);	
				while (ecoMatcher.find()) {
					
					ecoMatches.add(ecoMatcher.group());
			    }
				while (envMatcher.find()) {
					envMatches.add(envMatcher.group());
			    }
				while (socMatcher.find()) {
					socMatches.add(socMatcher.group());
			    }
				sc.close();
				normalizationFactor = (float) (words.size()/500.0);
				CorporateSustainabilityLine line = new CorporateSustainabilityLine(contents[k],ecoMatches.size()/normalizationFactor,envMatches.size()/normalizationFactor,socMatches.size()/normalizationFactor);
				results.add(line);
			}
			outputListToCsv(results);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
