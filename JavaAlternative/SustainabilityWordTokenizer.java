import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import opennlp.tools.tokenize.WhitespaceTokenizer;

public class SustainabilityWordTokenizer {

	private static void outputToCsv(Map<String, Integer> hm) {
		try {
			File myObj = new File("\\JavaWords.csv");
			myObj.createNewFile();
			FileWriter fw = new FileWriter(myObj.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("word, count\n");

			for (Map.Entry<String, Integer> val : hm.entrySet()) {
				bw.write(val.getKey() + ", " + val.getValue() + "\n");
			}
			bw.close();
		} catch (IOException e) {
		}
	}

	public static void main(String[] args) {

		try {

			WhitespaceTokenizer wordBreaker = WhitespaceTokenizer.INSTANCE;
			Map<String, Integer> hm = new HashMap<String, Integer>();
			File directoryPath = new File("\\TextFiles");
			
			String contents[] = directoryPath.list();
			for (int k = 0; k < contents.length; k++) {
				FileInputStream fis = new FileInputStream(
						"TextFiles\\" + contents[k]);
				Scanner sc = new Scanner(fis,StandardCharsets.UTF_8);
				while (sc.hasNextLine()) {
					String sentence = sc.nextLine();
					sentence = sentence.toLowerCase();
					sentence = sentence.replaceAll("[^A-Za-z—\\-\\'\\’\\ ]", " ").replaceAll("\\s+", " ");
					String[] words = wordBreaker.tokenize(sentence);

					for (int i = 0; i < words.length; i++) {
						Integer j = hm.get(words[i]);
						hm.put(words[i], (j == null) ? 1 : j + 1);
					}
				}
				sc.close();
			}

			outputToCsv(hm);
		} catch (IOException e) {
		}
	}
}
