import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Scanner;
import java.lang.Character.UnicodeBlock;
import java.awt.*;
import java.applet.Applet;

import javax.swing.JTextArea;


public class Phonomatic extends Applet {
	
	private static class Entry<String, Integer> {
		String feature; int value; String character;
		
		Entry (String f, int v) {
			this.feature = f;
			this.value = v;
			this.character = null;
		}
		
        @SuppressWarnings("unchecked")
		public boolean equals(Object that) {
            return (that instanceof Entry)
                && (this.feature.equals(((Entry<String, Integer>) that).feature));
        }
	}
	
	private static ArrayList<ArrayList<Entry<String, Integer>>> characters;
    private static ArrayList<Entry<String, Integer>> features;
    private static ArrayList<String> out = new ArrayList<String>();
    private static ArrayList<String> err = new ArrayList<String>();
    private static ArrayList<Integer> order = new ArrayList<Integer>();
    
    public Phonomatic() {
        Phonomatic.characters = new ArrayList<>();
        Phonomatic.features = new ArrayList<>();
    };
    
    public static void paintString (Graphics g, String s) {
    	Font myFont = new Font("TimesRoman", Font.PLAIN, 12);
    	g.setFont(myFont);
    	g.drawString(s, 20, 20);
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
            	e = new Entry<String, Integer>(feature, count);
            	Phonomatic.features.add(e);
                count += 1;
            }
            scanner.close();
            
            File file2 = new File("Characters");
            scanner = new Scanner(file2, "UTF-8");
            while (scanner.hasNextLine()) {
            	String character = scanner.next().trim();
            	try {
            		Integer.parseInt(character);
            		character = "\\u" + character;
            	} catch (NumberFormatException notInt) {
            		//do nothing
            	}
            	ArrayList<Entry<String, Integer>> clone = 
            			new ArrayList<Entry<String, Integer>>();
            	
            	for (int i = 0; i < Phonomatic.features.size(); i++) {
            		String value = scanner.next().trim();
            		Integer intValue = Integer.parseInt(value);
            		e = new Entry<String, Integer>(Phonomatic.features.get(i).feature, intValue);
            		e.character = character;
            		clone.add(e);
            	}
            	
                Phonomatic.characters.add(clone);
                count += 1;
            }            
            scanner.close();
        }
    
    public static ArrayList<String> searchByCharacter(String[] line, int index) {
        if (index < line.length) {
            String s = line[index];
            //Search through characters in "grid":
        	for (ArrayList<Entry<String, Integer>> a : Phonomatic.characters) {
    			//Once the charcter's ArrayList is identified, it prints the entire feature list:
    			if (a.get(0).character.equals(s)) {
    			    String plusminus;
    			    out.add("-----------------");
    			    out.add("|       " + a.get(0).character + "\t|");
    			    out.add("-----------------");
    			    //Translates each Entry in the features list to a string and adds it to s:
    		        for (Entry<String, Integer> e : a) {
    	                if (e.value > 0) {
    	                	if (e.value != 2) {
    	                		plusminus = "+ ";
    	                	} else {
    	                		plusminus = "+/-";
    	                	}    	                    
    	                } else if (e.value < 0) {
    	                    plusminus = "- ";
    	                } else {
    	                    plusminus = "0 ";
    	                }
    	                out.add("|  " + plusminus + e.feature + "\t|");
                    };
                    out.add("-----------------");                    
                    
                    if (index < line.length - 1){
                        index ++;
                        searchByCharacter(line, index);  
                    } else {
                        return out;
                    }
    			};
    		}
        	if (index < line.length) {         
                out.add("The character '" + line[index] + "' was not found in the Phonomatic database.\n");
                for (int i = 0; i < Phonomatic.features.size() + 3; i++) {
                    out.add(null);
                }
                index ++;
                searchByCharacter(line, index);            
            }
        }
        return out;
    }
    
    public static String searchByFeature(String[] line) {
    	ArrayList<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>();
    	int a = 1;
    	int b = 2;
    	for (int i = 1; i < line.length/2 + 1; i++) {
    		String sign = line[a];
    		String feature = line[b];
    		int parameter = 0;
        	if (sign.equals("+")) {
    			parameter = 1;
    		} else if (sign.equals("-")) {
    			parameter = -1;
    		} else if (sign.equals("0")) {
    			parameter = 0;
    		} else {
    			System.out.println(sign + " is not a valid value for " + feature);
    		}
        			
        	Entry<String, Integer> fake = new Entry<String, Integer>(feature, parameter);
        	int index =	Phonomatic.features.indexOf(fake);
        	if (index != -1) {
        		if (i == 1) {
            		ListIterator<ArrayList<Entry<String, Integer>>> it = Phonomatic.characters.listIterator();
                	while (it.hasNext()) {
            			Entry<String, Integer> e = it.next().get(index);
            			if (parameter != 0) {
            				if (e.value == parameter | e.value == 2) {
                				list.add(e);
                			};
            			} else {
            				if (e.value == parameter) {
                				list.add(e);
                			};
            			}
            		}
            	} else {
            		int l = 0;
            		while (l < list.size()) {
            			Entry<String, Integer> e = list.get(l);
	            			int j = 0;
	            			while (j < Phonomatic.characters.size()) {
	            				ArrayList<Entry<String, Integer>> character = Phonomatic.characters.get(j);
	            				if (character.get(0).character.equals(e.character)) {
	            					if (character.get(index).value != parameter) {
	                    				list.remove(l);
	                    				l--;
	                    			}
	            					j = Phonomatic.characters.size();
	            				}
	            				j += 1;
	            			}
	            		l ++;
            		}
            	} 
        	} else {
        		return "The feature '" + feature + "' is not present within the Phonomatic database.\n";
        	}
        	a += 2;
        	b += 2;
        }
    	String out = "[";
    	for (Entry<String, Integer> e : list) {
			out += e.character + ", ";
		}
    	if (out.length() > 1) {
    		out = out.substring(0, out.length() - 2);
    	}    	
    	out += "]";
    	return out;
    }
    
    public static void main(String[] args) throws FileNotFoundException {
    	loadDictionary();
			
	    Scanner inputScan = new Scanner(System.in);
        System.out.println("Welcome to the Phonomatic. For more information, enter 'info'");
        while (inputScan.hasNext()) {            
            String input = inputScan.nextLine();
            String[] line = input.toLowerCase().split(" ");
            String command = line[0];
            if (command.equals("info")) {
                System.out.println("Enter the following commands as needed:"
                        + "\n  \u2022  'find' - will search by character, returning stored data values"
                        + "\n  \u2022  'search' - will search by feature"
                        + "\n  \u2022  'catalog' - will print entire loaded dictionary");
            } else if (command.equals("catalog")) {
            } else if (command.equals("search")) {
                if (line.length < 3) {
                    System.out.println("Please enter at least one feature with the 'search' command.");
                } else {
                    if (line.length%2 - 1 != 0) {
                        System.out.println("Each feature must be accompanied by a desired value.\nexample: \"+ voice\"");
                    }
                    System.out.println(searchByFeature(line));
                }
            } else if (command.equals("find")) {
                if (line.length < 2) {
                    System.out.println("Please enter at least one character with the 'search' command.");
                } else {
                    ArrayList<String> results = searchByCharacter(line, 1);
                    int len = Phonomatic.features.size() + 4;
                    int num = line.length - 1;
                    //retrieval array:
                    for (int i = 0; i < len; i++) {
                        for (int j = 0; j < num; j++) {
                            order.add(j * len + i);
                        }
                    }
                    Iterator<Integer> it = order.iterator();
                    while (it.hasNext()) {                        
                        int j = 0;
                        String s = "";
                        while (j < num) {                            
                            j ++;
                            int i = it.next();
                            s = results.get(i);
                            if (s != null) {                                
                                if (s.contains("was not found")) {
                                    err.add(s);
                                } else {
                                    System.out.print(results.get(i));                                    
                                }
                            }                            
                        }
                        if (err.size() * len != out.size()) {
                            System.out.println();
                        }                        
                        j = 0;
                    }
                    if (err.size() != 0) {
                        for (String s : err) {
                            System.out.print(s);
                        }
                    }
                    out.clear();
                    order.clear();
                    err.clear();
                }                
            }            
        }
        inputScan.close();
    }    
}
