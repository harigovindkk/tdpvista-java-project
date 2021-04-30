package model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Port;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SpeechRecognizerMain {
	
	String lang_code="en-us";
	int input;
	
	// Necessary
	private LiveSpeechRecognizer recognizer;
	
	// Logger
	private Logger logger = Logger.getLogger(getClass().getName());
	
	/**
	 * This String contains the Result that is coming back from SpeechRecognizer
	 */
	private String speechRecognitionResult;
	
	//-----------------Lock Variables-----------------------------
	
	/**
	 * This variable is used to ignore the results of speech recognition cause actually it can't be stopped...
	 * 
	 * <br>
	 * Check this link for more information: <a href=
	 * "https://sourceforge.net/p/cmusphinx/discussion/sphinx4/thread/3875fc39/">https://sourceforge.net/p/cmusphinx/discussion/sphinx4/thread/3875fc39/</a>
	 */
	private boolean ignoreSpeechRecognitionResults = false;
	
	/**
	 * Checks if the speech recognise is already running
	 */
	private boolean speechRecognizerThreadRunning = false;
	
	/**
	 * Checks if the resources Thread is already running
	 */
	private boolean resourcesThreadRunning;
	
	//---
	
	/**
	 * This executor service is used in order the playerState events to be executed in an order
	 */
	private ExecutorService eventsExecutorService = Executors.newFixedThreadPool(2);
	
	//------------------------------------------------------------------------------------
	
	/**
	 * Constructor
	 */
	public SpeechRecognizerMain() {
		
		// Loading Message
		logger.log(Level.INFO, "Loading Speech Recognizer...\n");
		
		// Configuration
		Configuration configuration = new Configuration();
		
		try {
			BufferedReader ob=new BufferedReader(new InputStreamReader(System.in));
			System.out.println("List of available languages are : \n1. US English\n2. Indian English\n3. Hindi\n4. Italian\n5. German\n6. French\n7. Dutch\n8. Catalan\n9. Spanish\n10. Portuguese\n\nEnter the language number : ");
			
			input=Integer.parseInt(ob.readLine());
			if(input==1)
				lang_code="en-us";
			else if(input==2)
				lang_code="en-in";
			else if(input==3)
				lang_code="hi";
			else if(input==4)
				lang_code="it";
			else if(input==5)
				lang_code="de";
			else if(input==6)
				lang_code="fr";
			else if(input==7)
				lang_code="nl";
			else if(input==8)
				lang_code="ca";
			else if(input==9)
				lang_code="es";
			else if(input==10)
				lang_code="pt";
			else
				{
				System.out.println("Invalid Language Code");
				System.exit(0);
				}
			System.out.println("You have opted for "+lang_code);
		}
		catch(Exception exc)
		{
			System.out.println("Unexpected Error");
		}
		
		// Load model from the jar
		//English Language
		
		if(lang_code.equals("en-us")) {
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
		}
		
		else if(lang_code.equals("en-in")) {
		//for Indian-English
		configuration.setAcousticModelPath("resource:/indianEng_acoustic/");
		configuration.setDictionaryPath("resource:/indianEng_lm/en_in.dic");
		configuration.setLanguageModelPath("resource:/indianEng_lm/en-us.lm.bin");
		}
		
		else if(lang_code.equals("it")) {
		//Italic
		configuration.setAcousticModelPath("resource:/italic_acoustic/");
		configuration.setDictionaryPath("resource:/italic_lm/it.dic");
		configuration.setLanguageModelPath("resource:/italic_lm/voxforge_it_sphinx.lm");
		}
		
		else if(lang_code.equals("de")) {
//		//German
		configuration.setAcousticModelPath("resource:/german_acoustic/");
		configuration.setDictionaryPath("resource:/german_lm/german.dic");
		configuration.setLanguageModelPath("resource:/german_lm/cmusphinx-voxforge-de.lm.bin");
		}
		
		else if(lang_code.equals("hi")) {
		//Hindi
		configuration.setAcousticModelPath("resource:/hindi_acoustic/");
		configuration.setDictionaryPath("resource:/hindi_lm/hindi.dic");
		configuration.setLanguageModelPath("resource:/hindi_lm/hindi.lm");
		}
		

		else if(lang_code.equals("fr")) {
		//French
		configuration.setAcousticModelPath("resource:/french_acoustic/");
		configuration.setDictionaryPath("resource:/french_lm/fr.dict");
		configuration.setLanguageModelPath("resource:/french_lm/fr-small.lm.bin");
		}
		
		else if(lang_code.equals("nl")) {
		//Dutch
		configuration.setAcousticModelPath("resource:/dutch_acoustic/");
		configuration.setDictionaryPath("resource:/dutch_lm/voxforge_nl_sphinx.dic");
		configuration.setLanguageModelPath("resource:/dutch_lm/voxforge_nl_sphinx.lm");
		}
		
		else if(lang_code.equals("ca")) {
		//Catalan
		configuration.setAcousticModelPath("resource:/catalan_acoustic/");
		configuration.setDictionaryPath("resource:/catalan_lm/pronounciation-dictionary.dict");
		configuration.setLanguageModelPath("resource:/catalan_lm/language-model.lm.bin");
		}
		
		else if(lang_code.equals("es")) {
		//Spanish
		configuration.setAcousticModelPath("resource:/spanish_acoustic/");
		configuration.setDictionaryPath("resource:/spanish_lm/voxforge_es_sphinx.dic");
		configuration.setLanguageModelPath("resource:/spanish_lm/es-20k.lm");
		}
		
		
		else if(lang_code.equals("pt")) {
		//Portuguese
		configuration.setAcousticModelPath("resource:/portuguese_acoustic/");
		configuration.setDictionaryPath("resource:/portuguese_lm/br-pt.dic");
		//configuration.setLanguageModelPath("resource:/spanish_lm/es-20k.lm");
		configuration.setGrammarPath("resource:/grammars");
		configuration.setGrammarName("grammar-pt");
		configuration.setUseGrammar(true);
		}
		//====================================================================================
		//=====================READ THIS!!!===============================================
		//Uncomment this line of code if you want the recognizer to recognize every word of the language 
		//you are using , here it is English for example	
		//====================================================================================
		//configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
		
		//====================================================================================
		//=====================READ THIS!!!===============================================
		//If you don't want to use a grammar file comment below 3 lines and uncomment the above line for language model	
		//====================================================================================
		
		// Grammar
		//configuration.setGrammarPath("resource:/grammars");
		//configuration.setGrammarName("grammar-en");
		//configuration.setGrammarName("grammar-pt");
		//configuration.setUseGrammar(true);
		
		try {
			recognizer = new LiveSpeechRecognizer(configuration);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
		
		// Start recognition process pruning previously cached data.
		// recognizer.startRecognition(true);
		
		//Check if needed resources are available
		startResourcesThread();
		//Start speech recognition thread
		startSpeechRecognition();
	}
	//-----------------------------------------------------------------------------------------------
	
		/**
		 * Language Conversion
		 */
	
	
	private static String translate(String langFrom, String langTo, String text) throws IOException {
        // INSERT YOU URL HERE
        String urlStr = "https://script.google.com/macros/s/AKfycbx1aqm1-NwzaawdsEbAB49yTPxXpn50c47_1rgRMK5nJAKSuzPHzjs-3lAnTDPlf-nW/exec" +
                "?q=" + URLEncoder.encode(text, "UTF-8") +
                "&target=" + langTo +
                "&source=" + langFrom;
        URL url = new URL(urlStr);
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
	
	//-----------------------------------------------------------------------------------------------
	
	/**
	 * Starts the Speech Recognition Thread
	 */
	public synchronized void startSpeechRecognition() {
		
		//Check lock
		if (speechRecognizerThreadRunning)
			logger.log(Level.INFO, "Speech Recognition Thread already running...\n");
		else
			//Submit to ExecutorService
			eventsExecutorService.submit(() -> {
				
				//locks
				speechRecognizerThreadRunning = true;
				ignoreSpeechRecognitionResults = false;
				//Start Recognition
				recognizer.startRecognition(true);
				
				//Information			
				logger.log(Level.INFO, "You can start to speak...\n");
				try {
				
					while (speechRecognizerThreadRunning) {
						/*
						 * This method will return when the end of speech is reached. Note that the end pointer will determine the end of speech.
						 */
						SpeechResult speechResult = recognizer.getResult();
						
						//Check if we ignore the speech recognition results
						if (!ignoreSpeechRecognitionResults) {
							
							//Check the result
							if (speechResult == null)
								logger.log(Level.INFO, "I can't understand what you said.\n");
							else {
								
								//Get the hypothesis
								speechRecognitionResult = speechResult.getHypothesis();
								
								//You said?
								System.out.println("You said: [" + speechRecognitionResult + "]\n");
								
								//Call the appropriate method 
								makeDecision(speechRecognitionResult, speechResult.getWords());
								
							}
						} else
							logger.log(Level.INFO, "Ingoring Speech Recognition Results...");
						
					}
				} catch (Exception ex) {
					logger.log(Level.WARNING, null, ex);
					speechRecognizerThreadRunning = false;
				}
				
				logger.log(Level.INFO, "SpeechThread has exited...");
				
			});
	}
	
	/**
	 * Stops ignoring the results of SpeechRecognition
	 */
	public synchronized void stopIgnoreSpeechRecognitionResults() {
		
		//Stop ignoring speech recognition results
		ignoreSpeechRecognitionResults = false;
	}
	
	/**
	 * Ignores the results of SpeechRecognition
	 */
	public synchronized void ignoreSpeechRecognitionResults() {
		
		//Instead of stopping the speech recognition we are ignoring it's results
		ignoreSpeechRecognitionResults = true;
		
	}
	
	//-----------------------------------------------------------------------------------------------
	
	/**
	 * Starting a Thread that checks if the resources needed to the SpeechRecognition library are available
	 */
	public void startResourcesThread() {
		
		//Check lock
		if (resourcesThreadRunning)
			logger.log(Level.INFO, "Resources Thread already running...\n");
		else
			//Submit to ExecutorService
			eventsExecutorService.submit(() -> {
				try {
					
					//Lock
					resourcesThreadRunning = true;
					
					// Detect if the microphone is available
					while (true) {
						
						//Is the Microphone Available
						if (!AudioSystem.isLineSupported(Port.Info.MICROPHONE))
							//logger.log(Level.INFO, "Microphone is not available.\n");
						
						// Sleep some period
						Thread.sleep(350);
					}
					
				} catch (InterruptedException ex) {
					logger.log(Level.WARNING, null, ex);
					resourcesThreadRunning = false;
				}
			});
	}
	
	/**
	 * Takes a decision based on the given result
	 * 
	 * @param speechWords
	 * @throws IOException 
	 */
	public void makeDecision(String speech , List<WordResult> speechWords) throws IOException {
		if(input>3)
		System.out.println("Translated text: " + translate(lang_code, "en", speech));
		//System.out.println(speech);
		
	}
	
	public boolean getIgnoreSpeechRecognitionResults() {
		return ignoreSpeechRecognitionResults;
	}
	
	public boolean getSpeechRecognizerThreadRunning() {
		return speechRecognizerThreadRunning;
	}
	
	/**
	 * Main Method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new SpeechRecognizerMain();
	}
}
