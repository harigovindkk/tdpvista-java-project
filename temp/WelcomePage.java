package model;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Port;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
//import org.codehaus.groovy.tools.shell.commands.ExitCommand;

/**
 *
 * @author anshu_
 */
public class WelcomePage extends javax.swing.JFrame {
	private Logger logger = Logger.getLogger(getClass().getName());
	private String result;
	Thread	speechThread;
	Thread	resourcesThread;
	private LiveSpeechRecognizer recognizer;
	public void loadRecognizer() {

		logger.log(Level.INFO, "Loading...\n");
		Configuration configuration = new Configuration();
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		//configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		// configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin")
		// Grammar
		//configuration.setGrammarPath("resource:/grammars");
		//configuration.setGrammarName("grammar");
		//configuration.setUseGrammar(true);
		
		configuration.setDictionaryPath("http://www.speech.cs.cmu.edu/tools/product/1620007584_16366/6443.dic");
		configuration.setLanguageModelPath("http://www.speech.cs.cmu.edu/tools/product/1620007584_16366/6443.lm");

		try {
			recognizer = new LiveSpeechRecognizer(configuration);
		} catch (IOException ex) {
			//logger.log(Level.SEVERE, null, ex);
		}

		recognizer.startRecognition(true);

		startSpeechThread();
		startResourcesThread();
	}

	
	protected void startSpeechThread() {

			if (speechThread != null && speechThread.isAlive())
			return;
		speechThread = new Thread(() -> {
			logger.log(Level.INFO, "You can start to speak...\n");
			try {
				while (true) {
					SpeechResult speechResult = recognizer.getResult();
					if (speechResult != null) {

						result = speechResult.getHypothesis();
						System.out.println("You said:) [" + result + "]\n");
						String op="You said:) [" + result + "]\n";
						//String replyResult = "";
						if(result.equals("")==false)
						{
						String commands[]= {"LIST FILES","CLEAR SCREEN","PRESENT WORKING DIRECTORY","PROCESS STATUS","SHUTDOWN","UPTIME","USER","HISTORY","HELP","DIRECTORY"};
						String tercmd[]= {"ls","clear","pwd","ps","shutdown","uptime","w","history","help","dir"};
						int flagvar=1;
						for(int vari=0;vari<commands.length;vari++)
						{
							if(result.contains(commands[vari]))
							{
								op+="\nThe most appropriate command recognised is : "+tercmd[vari]+"\nIts output is:-\n "+commandResult(tercmd[vari]);
								flagvar=0;
							break;
							}
						}
						if(flagvar==1)
							op+="\nNo terminal command found for the recognised text";
						textArea.setText(op);
						}
						else
							textArea.setText("Could not understand!");
						
						
						//makeDecision(result);
//						String replyResult = "";
//						reply r = new reply();
//						if(result.equals("hi")|| result.equals("hello") || result.equals("hey") || result.equals("what's up") || result.equals("how are you"))
//							replyResult=r.commonReply(0);
//						else if(result.equals("show system information"))
//							replyResult = r.cmdResult("lscpu");
//						else if(result.equals("show my current directory"))
//							replyResult = r.cmdResult("pwd");
//						else if(result.equals("show battery information"))
//							replyResult = r.cmdResult("upower -i /org/freedesktop/UPower/devices/battery_BAT0");
//						else if(result.equals("show hardware information"))
//							replyResult = r.cmdResult("lshw");
//						else if(result.equals("tell me today's date"))
//							replyResult = r.commonReply(1);
//						else if(result.equals("tell me a joke"))
//							replyResult = r.commonReply(2);
//						else if(result.equals("tell me a another joke"))
//							replyResult = r.commonReply(3);
//						else if(result.equals("what's your name"))
//							replyResult = r.commonReply(4);
//						else if(result.equals("what is your name"))
//							replyResult = r.commonReply(4);
//						else if(result.equals("how's the traffic"))
//							replyResult = r.commonReply(5);
//						else if(result.equals("tell me about my day"))
//							replyResult = r.commonReply(6);
//						else if(result.equals("are you happy"))
//							replyResult = r.commonReply(7);
//						else if(result.equals("exit the program"))
//							replyResult = r.commonReply(8);
//						else if(result.equals("open cmd"))
//							replyResult =r.commonReply(9);
//						else if(result.equals("open browser"))
//							replyResult = r.commonReply(10);
							
							

						//System.out.println(replyResult);

						//textArea.setText("You said:-> [" + result + "]\n"+replyResult);

					}

				}
			} catch (Exception ex) {
				logger.log(Level.WARNING, null, ex);
			}

			logger.log(Level.INFO, "SpeechThread has exited...");
		});

		// Start
		speechThread.start();

	}
	protected void startResourcesThread() {

		// alive?
		if (resourcesThread != null && resourcesThread.isAlive())
			return;

		resourcesThread = new Thread(() -> {
			try {

				while (true) {
					if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
					} else {

					}

					Thread.sleep(350);
				}

			} catch (InterruptedException ex) {
				logger.log(Level.WARNING, null, ex);
				resourcesThread.interrupt();
			}
		});

		// Start
		resourcesThread.start();
	}

	
	
	public void endSpeech() {
		logger.log(Level.INFO, "Stop Successfully\n");
		recognizer.stopRecognition();
	}

	private String commandResult(String cmd) throws IOException, InterruptedException {
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
	//***********************************************************************************
    public WelcomePage() {
        initComponents();
    }
    

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        backpanel = new javax.swing.JPanel();
        headerpanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        header2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        body = new javax.swing.JPanel();
        panel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        RunBtn = new javax.swing.JButton();
        StopBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        backpanel.setBackground(new java.awt.Color(255, 255, 255));

        headerpanel.setBackground(new java.awt.Color(57, 57, 57));

        jLabel1.setBackground(new java.awt.Color(45, 52, 60));
        jLabel1.setFont(new java.awt.Font("C059", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(35, 41, 47));
        jLabel1.setText("TDP Vista");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(25, 25, 25))
        );

        jLabel2.setFont(new java.awt.Font("Abyssinica SIL", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(238, 238, 238));
        jLabel2.setText("Contacts Us");

        jLabel3.setFont(new java.awt.Font("Abyssinica SIL", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(238, 238, 238));
        jLabel3.setText("Home");

        jLabel4.setFont(new java.awt.Font("Abyssinica SIL", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(238, 238, 238));
        jLabel4.setText("About Us");

        header2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setFont(new java.awt.Font("Abyssinica SIL", 1, 24)); // NOI18N
        jLabel6.setText("Welcome to HOME page");
        jLabel6.setToolTipText("");
        jLabel6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        javax.swing.GroupLayout header2Layout = new javax.swing.GroupLayout(header2);
        header2.setLayout(header2Layout);
        header2Layout.setHorizontalGroup(
            header2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, header2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(386, 386, 386))
        );
        header2Layout.setVerticalGroup(
            header2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(header2Layout.createSequentialGroup()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout headerpanelLayout = new javax.swing.GroupLayout(headerpanel);
        headerpanel.setLayout(headerpanelLayout);
        headerpanelLayout.setHorizontalGroup(
            headerpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerpanelLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(131, 131, 131)
                .addComponent(jLabel3)
                .addGap(32, 32, 32)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(header2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        headerpanelLayout.setVerticalGroup(
            headerpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerpanelLayout.createSequentialGroup()
                .addGroup(headerpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(headerpanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(headerpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(header2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67))
        );

        body.setBackground(new java.awt.Color(255, 255, 255));

        panel.setBackground(new java.awt.Color(73, 75, 76));

        jLabel7.setFont(new java.awt.Font("Abyssinica SIL", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(231, 224, 224));
        jLabel7.setText("Speech Recorder");

        RunBtn.setBackground(new java.awt.Color(31, 219, 252));
        RunBtn.setForeground(new java.awt.Color(27, 163, 203));
        RunBtn.setText("RUN");
        RunBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RunBtnActionPerformed(evt);
            }
        });

        StopBtn.setBackground(new java.awt.Color(31, 219, 252));
        StopBtn.setForeground(new java.awt.Color(27, 163, 203));
        StopBtn.setText("STOP");
        StopBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StopBtnActionPerformed(evt);
            }
        });

        textArea.setColumns(40);
        textArea.setRows(5);
        textArea.setLineWrap(true);
        jScrollPane1.setViewportView(textArea);

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(88, 88, 88)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(StopBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RunBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65))
            .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelLayout.createSequentialGroup()
                    .addGap(261, 261, 261)
                    .addComponent(jLabel7)
                    .addContainerGap(182, Short.MAX_VALUE)))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(114, 114, 114)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(RunBtn)
                        .addGap(32, 32, 32)
                        .addComponent(StopBtn)))
                .addContainerGap(77, Short.MAX_VALUE))
            .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelLayout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(jLabel7)
                    .addContainerGap(268, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout bodyLayout = new javax.swing.GroupLayout(body);
        body.setLayout(bodyLayout);
        bodyLayout.setHorizontalGroup(
            bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bodyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(96, Short.MAX_VALUE))
        );
        bodyLayout.setVerticalGroup(
            bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout backpanelLayout = new javax.swing.GroupLayout(backpanel);
        backpanel.setLayout(backpanelLayout);
        backpanelLayout.setHorizontalGroup(
            backpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backpanelLayout.createSequentialGroup()
                .addGroup(backpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(headerpanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(backpanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(body, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );
        backpanelLayout.setVerticalGroup(
            backpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backpanelLayout.createSequentialGroup()
                .addComponent(headerpanel, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(body, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(backpanel, javax.swing.GroupLayout.PREFERRED_SIZE, 755, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(backpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        
    
    private void RunBtnActionPerformed(java.awt.event.ActionEvent evt) {        
    	loadRecognizer();
    	textArea.setText("Start Successfully\nSpeak...");
    	

    }                                      
    
    private void StopBtnActionPerformed(java.awt.event.ActionEvent evt) {                                        
        // TODO add your handling code here:
		textArea.setText("Stop Successfully");
		recognizer.stopRecognition();
		
    }                                       

	/**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(WelcomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(WelcomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WelcomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WelcomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WelcomePage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton RunBtn;
    private javax.swing.JButton StopBtn;
    private javax.swing.JPanel backpanel;
    private javax.swing.JPanel body;
    private javax.swing.JPanel header2;
    private javax.swing.JPanel headerpanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panel;
    private javax.swing.JTextArea textArea;
    // End of variables declaration   
}
