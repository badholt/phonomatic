import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class PhonomaticApplet {
	//Strings:
	static String intro = "<body style=\"margin:10;padding:10\"><center>Welcome to the <br></br><b>Phon-o-matic</b>!<br></br>For more information on Phon-o-matic"
	    		+ " functions, please enter 'info' in the text area to your left.</center></body>";
	 
	//Colors:
	static Color khaki = new Color(0xF8F2DA);
	static Color bavarian_cream = new Color(0xE5D6A7);
	 
	//Fonts:
	static Font modern = new Font("Calibri", Font.PLAIN, 24);
	 
	//Reference:
	actionL ear;
	 
	//TO-DO
 	static ArrayList<ArrayList<Entry<String, Integer>>> characters;
    static ArrayList<Entry<String, Integer>> features;
    static ArrayList<String> out = new ArrayList<String>();
    static ArrayList<String> err = new ArrayList<String>();
    static ArrayList<Integer> order = new ArrayList<Integer>();
    
	public static class Entry<String, Integer> {
		String feature; int value; String character;
		
		Entry (String f, int v, String c) {
			this.feature = f;
			this.value = v;
			this.character = c;
		}
		
        @SuppressWarnings("unchecked")
		public boolean equals(Object that) {
            return (that instanceof Entry)
                && (this.feature.equals(((Entry<String, Integer>) that).feature));
        }
	}
		
	public static void loadDictionary()
            throws FileNotFoundException {
        	Phonomatic dictionary = new Phonomatic();
            
            File file = new File("Features");
            Scanner scanner = new Scanner(file);
            
            Entry<String, Integer> e;
            int count = 0;
            while (scanner.hasNextLine()) {
            	String feature = scanner.nextLine().trim().toLowerCase();
            	e = new Entry<String, Integer>(feature, count, "unknown");
            	features.add(e);
                count += 1;
            }
            scanner.close();
            
            File file2 = new File("Characters");
            scanner = new Scanner(file2, "UTF-8");
            while (scanner.hasNextLine()) {
            	String character = scanner.next().trim();
            	ArrayList<Entry<String, Integer>> clone = 
            			new ArrayList<Entry<String, Integer>>();
            	
            	for (int i = 0; i < features.size(); i++) {
            		String value = scanner.next().trim();
            		Integer intValue = Integer.parseInt(value);
            		e = new Entry<String, Integer>(features.get(i).feature, intValue, character);
            		clone.add(e);
            	}
            	
                characters.add(clone);
                count += 1;
            }            
            scanner.close();
        }


  public static int indexThis(String f, int p, String c) {
	  Entry<String, Integer> fake = new Entry<String, Integer>(f, p, c);
	  int index =	features.indexOf(fake);
	  return index;
  }
	
  public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
	//Frame:
    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    //UIManager.put("nimbusBase", khaki);
    //UIManager.put("control", bavarian_cream);
    JFrame frame = new JFrame("Phon-o-matic");
    frame.setLayout(new FlowLayout());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //frame.setUndecorated(true);
    //frame.setOpacity(0.75f);
    //frame.setResizable(false);
    
    //Text Area 1:
    JTextArea textArea1 = new JTextArea("", 30, 40);
    textArea1.setPreferredSize(new Dimension(30, 40));
    textArea1.setEditable(true);
    textArea1.setFont(modern);
    textArea1.setLineWrap(true);
    textArea1.setWrapStyleWord(true);
    JScrollPane scrollPane1 = new JScrollPane(textArea1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
    		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
    //Text Area 2:
    JTextPane textArea2 = new JTextPane();
    textArea2.setContentType("text/html; charset=utf-16");
    textArea2.setPreferredSize(new Dimension(300, 900));
    textArea2.setEditable(false);
    textArea2.setBackground(khaki);
    textArea2.setFont(modern);
    textArea2.setText(intro);
    JScrollPane scrollPane2 = new JScrollPane(textArea2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
    		JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    
    StyledDocument doc = textArea2.getStyledDocument();
    SimpleAttributeSet center = new SimpleAttributeSet();
    StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
    doc.setParagraphAttributes(0, doc.getLength(), center, false);
    
    //Action Listener Set-up:
    actionL ear = new actionL();
    textArea1.addKeyListener(ear);
    ear.outputTo(textArea2, doc, center);
    
    //Additions to Layout:
    frame.add(scrollPane1);
    frame.add(scrollPane2);
    frame.pack();
    frame.setVisible(true);
    
    //Phon-o-matic Functions:
    characters = new ArrayList<>();
    features = new ArrayList<>();
    loadDictionary();
  }
}