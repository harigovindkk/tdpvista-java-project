/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Recognise;
import edu.cmu.sphinx.api.*;
import java.io.IOException;
/**
 *
 * @author Kavya
 */
public class GrammarRecognise {
    public static void main(String args[]) throws IOException{
        Configuration con=new Configuration();
        con.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        con.setDictionaryPath("http://www.speech.cs.cmu.edu/tools/product/1620007584_16366/6443.dic");
        con.setLanguageModelPath("http://www.speech.cs.cmu.edu/tools/product/1620007584_16366/6443.lm");
        LiveSpeechRecognizer recog=new LiveSpeechRecognizer(con);
        System.out.println("Start speakiing");
        recog.startRecognition(true);
        System.out.println("Start ");
       
        SpeechResult result;
        while ((result = recog.getResult()) != null) {
           
            String text=result.getHypothesis();
           System.out.println("T:"+text);
            

	}
       recog.stopRecognition();

    }
    
}
