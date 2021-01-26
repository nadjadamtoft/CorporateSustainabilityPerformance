import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


//extract noun phrases from a single sentence using OpenNLP

/*
public class Main {

	static String sentence = "Due to the highly volatile and competitive nature of the industries in which the Company competes, the Company must continually introduce new products, services and technologies, enhance existing products and services, and effectively stimulate customer demand for new and upgraded products.";

	static Set<String> nounPhrases = new HashSet<>();

	public static void main(String[] args) {

		InputStream modelInParse = null;
		try {
			// load chunking model
			modelInParse = new FileInputStream(
					"C:\\Users\\Claudiu\\Desktop\\apache-opennlp-1.9.3\\en-parser-chunking.bin"); // from
																									// http://opennlp.sourceforge.net/models-1.5/
			ParserModel model = new ParserModel(modelInParse);

			// create parse tree
			Parser parser = ParserFactory.create(model);
			Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);

			// call subroutine to extract noun phrases
			for (Parse p : topParses)
				getNounPhrases(p,0);

			// print noun phrases
			for (String s : nounPhrases)
				System.out.println(s);

			// The Call
			// the Wild?
			// The Call of the Wild? //punctuation remains on the end of sentence
			// the author of The Call of the Wild?
			// the author
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelInParse != null) {
				try {
					modelInParse.close();
				} catch (IOException e) {
				}
			}
		}
	}

	// recursively loop through tree, extracting noun phrases
	public static void getNounPhrases(Parse p, int count) {

		if (p.getType().equals("NP")) { // NP=noun phrase
			if(count>7) {
			nounPhrases.add(p.getCoveredText());}
		}
		for (Parse child : p.getChildren())
			getNounPhrases(child, count++);
		
	}
}*/

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import java.util.HashMap;

import java.util.Map;
import java.util.Scanner;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;

/**
 *
 * Extracts noun phrases from a sentence. To create sentences using OpenNLP use
 * the SentenceDetector classes.
 */
public class NPDictionary {

	private static void outputToCsv(Map<String, Integer> hm) {
		try {
			File myObj = new File("\\Counts\\JavaNP.csv");
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
			String modelPath = "\\apache-opennlp-1.9.3\\";
			WhitespaceTokenizer wordBreaker = WhitespaceTokenizer.INSTANCE;
			POSModel pm = new POSModel(new FileInputStream(new File(modelPath + "en-pos-maxent.bin")));
			POSTaggerME posme = new POSTaggerME(pm);
			InputStream modelIn = new FileInputStream(modelPath + "en-chunker.bin");
			ChunkerModel chunkerModel = new ChunkerModel(modelIn);
			ChunkerME chunkerME = new ChunkerME(chunkerModel);
			Map<String, Integer> hm = new HashMap<String, Integer>();
			File directoryPath = new File("\\TextFiles");
			String contents[] = directoryPath.list();
			System.out.println("List of files and directories in the specified directory:");
			for (int k = 0; k < contents.length; k++) {
				System.out.println(contents[k]);
				FileInputStream fis= new FileInputStream("\\TextFiles\\"+contents[k]);       
				Scanner sc = new Scanner(fis,StandardCharsets.UTF_8);
				while(sc.hasNextLine())  
				{  
				String sentence = sc.nextLine();
				sentence = sentence.toLowerCase();
				sentence = sentence.replaceAll("[^A-Za-z—\\-\\'\\’\\ ]", " ").replaceAll("\s+", " ");
				String[] words = wordBreaker.tokenize(sentence);
				String[] posTags = posme.tag(words);
				Span[] chunks = chunkerME.chunkAsSpans(words, posTags);
				String[] chunkStrings = Span.spansToStrings(chunks, words);
				
				for (int i = 0; i < chunks.length; i++) {
					if (chunks[i].getType().equals("NP")) {
						Integer j = hm.get(chunkStrings[i]);
						hm.put(chunkStrings[i], (j == null) ? 1 : j + 1);
					}
				}
				}
				sc.close();
			}
			
			outputToCsv(hm);
		} catch (IOException e) {
		}
	}

}