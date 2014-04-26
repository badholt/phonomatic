package src;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Scanner;


public class Phonomatic {
	
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
    
    private static void loadDictionary()
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
    	                    plusminus = "+ ";
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
    
    public static String searchByFeature(String sign, String feature) {
    	ListIterator<ArrayList<Entry<String, Integer>>> it = Phonomatic.characters.listIterator();
		int parameter = 0;
    	if (sign.equals("+")) {
			parameter = 1;
		} else if (sign.equals("-")) {
			parameter = -1;
		} else if (sign.equals("0")) {
			parameter = 0;
		} else {
			System.out.println(sign + "is not a valid value for " + feature);
		}
    	Entry<String, Integer> fake = new Entry<String, Integer>(feature, parameter);
    	int index = Phonomatic.features.indexOf(fake);
    	if (index != -1) {
    		String list = "[";
        	while (it.hasNext()) {
    			Entry<String, Integer> e = it.next().get(index);
    			if (e.value == parameter) {
    				list += e.character + ", ";
    			};
    		}
        	//Trim -2
        	list += "]";
    		return list;
    	} else {
    		return "The feature '" + feature + "' is not present within the Phonomatic database.\n";
    	}    	
    }
    
    public static void main(String[] args) throws FileNotFoundException {
    	loadDictionary();
			
	    Scanner inputScan = new Scanner(System.in);
        System.out.println("Welcome to the Phonomatic. For more information, enter 'info'");
        while (inputScan.hasNext()) {            
            String input = inputScan.nextLine();
            String[] line = input.split(" ");
            String command = line[0];
            if (command.equals("info")) {
                System.out.println("Enter the following commands as needed:"
                        + "\n  \u2022  'find' - will search by character, returning stored data values"
                        + "\n  \u2022  'search' - will search by feature"
                        + "\n  \u2022  'catalog' - will print entire loaded dictionary");
            } else if (command.equals("catalog")) {
            } else if (command.equals("search")) {
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
