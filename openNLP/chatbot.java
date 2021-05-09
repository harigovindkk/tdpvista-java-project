package openNLP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import opennlp.tools.doccat.*;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.*;
import opennlp.tools.util.model.*;

/**
 * Train categorizer model as per the category sample training data we created.
 * 
 * @return
 * @throws FileNotFoundException
 * @throws IOException
 * 
 * 
 */
public class chatBot{
	private static Map<String, String> commandMap = new HashMap<>();
	static String category;
	/*
	 * Define answers for each given category.
	 */
	static {		
				commandMap.put("greeting", "Hello, how can I help you?");
				commandMap.put("directory-creation", "The command to create directory is mkdir");
				commandMap.put("directory-removal", "The command to remove directory is rmdir");
				commandMap.put("show-date", "You have queried about the date and time");
				commandMap.put("open-file", "The command to open a file is cat");
				commandMap.put("password", "Be sure to choose a strong password!");
				commandMap.put("logged-users", "Here is the list of all users logged in this system");
				commandMap.put("user-list", "There can be many users in a system");
				commandMap.put("memory-available", "I have a good memory capacity!");
				commandMap.put("space-available", "Please delete the unwanted files");
				commandMap.put("super-user", "Calling the system admin!!");
				commandMap.put("hardware-info", "My internal details");
				commandMap.put("battery-info", "My battery details");
				commandMap.put("file-list", "You have created a number of files ");
				commandMap.put("system-info", "I will say about the system information");
				commandMap.put("current-dir", "Every process are running from a specified directory");
				commandMap.put("process-status", "There are a lots of processes in the system");
				commandMap.put("conversation-complete", "Nice chatting with you. Bye.");
	}
	
	
	
	
	
	
	private static String commandResult(String cmd) throws IOException, InterruptedException {
        Runtime run = Runtime.getRuntime();
        Process pr = run.exec(cmd);
        pr.waitFor();
        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line = "";
        String result="";
        while ((line=buf.readLine())!=null) {
                result = result+"\n"+line;
        }
        return result+"\nRUN Successfully...";
        
    }
	
	
	
	
	
	

	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {

		// Train categorizer model to the training data we created.
		DoccatModel model = trainCategorizerModel();

		// Take chat inputs from console (user) in a loop.
		Scanner scanner = new Scanner(System.in);
		while (true) {

			// Get chat input from user.
			System.out.println("##### You:");
			String userInput = scanner.nextLine();

			// Break users chat input into sentences using sentence detection.
			String[] sentences = breakSentences(userInput);

			String answer = "";
			boolean conversationComplete = false;

			// Loop through sentences.
			for (String sentence : sentences) {

				// Separate words from each sentence using tokenizer.
				String[] tokens = tokenizeSentence(sentence);

				// Tag separated words with POS tags to understand their gramatical structure.
				String[] posTags = detectPOSTags(tokens);

				// Lemmatize each word so that its easy to categorize.
				String[] lemmas = lemmatizeTokens(tokens, posTags);

				// Determine BEST category using lemmatized tokens used a mode that we trained
				// at start.
				category = detectCategory(model, lemmas);

				// Get predefined answer from given category & add to answer.
				answer = answer + " " + commandMap.get(category);

				// If category conversation-complete, we will end chat conversation.
				if ("conversation-complete".equals(category)) {
					conversationComplete = true;
				}
			}

			// Print answer back to user. If conversation is marked as complete, then end
			// loop & program.
			System.out.println("##### Chat Bot: " + answer);
			
			
			if(category.equals("greeting")==false && category.equals("conversation-complete")==false )
			{
			String commands[]= {"show-date","open-file","password","user-list","logged-users","memory-available","space-available","directory-creation","directory-removal","super-user","hardware-info","battery-info","file-list","system-info","current-dir","process-status"};
			String tercmd[]= {"date","cat --help","passwd --help","users","w","free","df","mkdir --help","rmdir --help","sudo","lshw","upower -i /org/freedesktop/UPower/devices/battery_BAT0","ls","lscpu","pwd","ps"};
			int flagvar=1;
			for(int vari=0;vari<commands.length;vari++)
			{
				if(category.equals(commands[vari]))
				{
					System.out.println("\nThe most appropriate command recognised is : "+tercmd[vari]+"\nIts output is:-\n "+commandResult(tercmd[vari]));
					flagvar=0;
				break;
				}
			}
			if(flagvar==1)
				System.out.println("\nNo terminal command found for the recognised text");
			
			}	
			
			if (conversationComplete) {
				break;
			}
			

		}
		scanner.close();

	}

	/**
	 * Train categorizer model as per the category sample training data we created.
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static DoccatModel trainCategorizerModel() throws FileNotFoundException, IOException {
		// faq-categorizer.txt is a custom training data with categories as per our chat
		// requirements.
		InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File("openNLP/terminal-commands.txt"));
		ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
		ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

		DoccatFactory factory = new DoccatFactory(new FeatureGenerator[] { new BagOfWordsFeatureGenerator() });

		TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
		params.put(TrainingParameters.CUTOFF_PARAM, 0);

		// Train a model with classifications from above file.
		DoccatModel model = DocumentCategorizerME.train("en", sampleStream, params, factory);
		return model;
	}

	/**
	 * Detect category using given token. Use categorizer feature of Apache OpenNLP.
	 * 
	 * @param model
	 * @param finalTokens
	 * @return
	 * @throws IOException
	 */
	private static String detectCategory(DoccatModel model, String[] finalTokens) throws IOException {

		// Initialize document categorizer tool
		DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);

		// Get best possible category.
		double[] probabilitiesOfOutcomes = myCategorizer.categorize(finalTokens);
		String category = myCategorizer.getBestCategory(probabilitiesOfOutcomes);
		System.out.println("Category: " + category);

		return category;

	}

	/**
	 * Break data into sentences using sentence detection feature of Apache OpenNLP.
	 * 
	 * @param data
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static String[] breakSentences(String data) throws FileNotFoundException, IOException {
		// Better to read file once at start of program & store model in instance
		// variable. but keeping here for simplicity in understanding.
		try (InputStream modelIn = new FileInputStream("openNLP/en-sent.bin")) {

			SentenceDetectorME myCategorizer = new SentenceDetectorME(new SentenceModel(modelIn));

			String[] sentences = myCategorizer.sentDetect(data);
			System.out.println("Sentence Detection: " + Arrays.stream(sentences).collect(Collectors.joining(" | ")));

			return sentences;
		}
	}

	/**
	 * Break sentence into words & punctuation marks using tokenizer feature of
	 * Apache OpenNLP.
	 * 
	 * @param sentence
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static String[] tokenizeSentence(String sentence) throws FileNotFoundException, IOException {
		// Better to read file once at start of program & store model in instance
		// variable. but keeping here for simplicity in understanding.
		try (InputStream modelIn = new FileInputStream("openNLP/en-token.bin")) {

			// Initialize tokenizer tool
			TokenizerME myCategorizer = new TokenizerME(new TokenizerModel(modelIn));

			// Tokenize sentence.
			String[] tokens = myCategorizer.tokenize(sentence);
			System.out.println("Tokenizer : " + Arrays.stream(tokens).collect(Collectors.joining(" | ")));

			return tokens;

		}
	}

	/**
	 * Find part-of-speech or POS tags of all tokens using POS tagger feature of
	 * Apache OpenNLP.
	 * 
	 * @param tokens
	 * @return
	 * @throws IOException
	 */
	private static String[] detectPOSTags(String[] tokens) throws IOException {
		// Better to read file once at start of program & store model in instance
		// variable. but keeping here for simplicity in understanding.
		try (InputStream modelIn = new FileInputStream("openNLP/en-pos-maxent.bin")) {

			// Initialize POS tagger tool
			POSTaggerME myCategorizer = new POSTaggerME(new POSModel(modelIn));

			// Tag sentence.
			String[] posTokens = myCategorizer.tag(tokens);
			System.out.println("POS Tags : " + Arrays.stream(posTokens).collect(Collectors.joining(" | ")));

			return posTokens;

		}

	}

	/**
	 * Find lemma of tokens using lemmatizer feature of Apache OpenNLP.
	 * 
	 * @param tokens
	 * @param posTags
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	private static String[] lemmatizeTokens(String[] tokens, String[] posTags)
			throws InvalidFormatException, IOException {
		// Better to read file once at start of program & store model in instance
		// variable. but keeping here for simplicity in understanding.
		try (InputStream modelIn = new FileInputStream("openNLP/en-lemmatizer.bin")) {

			// Tag sentence.
			LemmatizerME myCategorizer = new LemmatizerME(new LemmatizerModel(modelIn));
			String[] lemmaTokens = myCategorizer.lemmatize(tokens, posTags);
			System.out.println("Lemmatizer : " + Arrays.stream(lemmaTokens).collect(Collectors.joining(" | ")));

			return lemmaTokens;

		}
	}

}

