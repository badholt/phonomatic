import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.xml.crypto.Data;

class actionL implements KeyListener {
	ArrayList<String> a = new ArrayList<String>();
	ArrayList<String> columns = new ArrayList<String>();
	ArrayList<String> headers = new ArrayList<String>();
	JTextArea ta;
	JTextPane tp;
	StyledDocument doc;
	SimpleAttributeSet left = new SimpleAttributeSet();
	SimpleAttributeSet subtle = new SimpleAttributeSet();
	String s = "";
	String imageTag = "<img src=\"" + "http://wrzk.com/locallinks/wp-content/uploads/2013/11/Line-Divider.png" + "\" width= \"" + 100 + "\"; />";
	String previous = PhonomaticApplet.intro;
	
	boolean found = true;
	
	@Override
	public void keyPressed(KeyEvent e)
	{	
		ta = (JTextArea) e.getSource();
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			if (s.length() != 0) {
				s = s.substring(0, s.length()-1);
			}
		}
		else if (e.getKeyCode() == KeyEvent.VK_UP)
		{
			if (!a.isEmpty())
			{
				tp.setCaretPosition(tp.getText().length());
				//ta.setText(a.get(a.size()-1));
			}
		}
		else if (e.getKeyChar() == 10)
		{
			if (s.toLowerCase().equals("info"))
			{
				previous = tp.getText();
				tp.setText("<body style=\"margin:10;padding:10\"><center>Enter the following commands as needed:</center>" + "<center>" + imageTag + "</center>"
                        + "\u2022 'find' - will search by character, returning stored data values"
                        + "<br><br> &nbsp \u2022  'search' - will search by feature</br></br>"
                        + "<br><br> &nbsp \u2022  'catalog' - will print entire loaded dictionary</br></br><br></br><center>" + imageTag
                        + "<br><br><br>{enter 'close' to exit the info screen}</center></br></br></br></body>");
			    
				StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
			    Style style = doc.addStyle("SimpleDividers", null);
			    StyleConstants.setIcon(style, new ImageIcon("Divider"));
				subtle.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.TRUE);
				subtle.addAttribute(StyleConstants.CharacterConstants.FontSize, 10);
				subtle.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.LIGHT_GRAY);
				doc.setCharacterAttributes(200, 45, subtle, false);
			}
			else if (s.toLowerCase().equals("close"))
			{
				tp.setText(previous);
			}
			else if (s.toLowerCase().contains("find"))
			{
				String[] line = s.toLowerCase().split(" ");
				if (line.length < 2)
				{
					if (!tp.getText().contains("Welcome"))
					{
						tp.setText("Please enter at least one character with the 'find' command.");
					} else
					{
						easyInsert("Please enter at least one character with the 'find' command.");
					}
                } else
                {
                    if (!tp.getText().contains("Welcome")) {
                    	easyInsert("\n");
                    	tp.add(getTableComponent(line));
                        
                        if (headers.size() != line.length - 1)
                        {
                            for (String s : PhonomaticApplet.err)
                            {
                            	easyInsert(s);
                            }
                        }
                        System.setProperty("swing.aatext", "true");
                    	tp.insertComponent(getTableComponent(line));
                    } else {
                    	tp.setText("");
                        
                        if (headers.size() != line.length - 1)
                        {
                            for (String s : PhonomaticApplet.err)
                            {
                            	easyInsert(s);
                            }
                        }
                        System.setProperty("swing.aatext", "true");
                    	tp.insertComponent(getTableComponent(line));
                    }
                }
            }            
			else if (s.toLowerCase().contains("search"))
			{
				String[] line = s.toLowerCase().split(" ");
				if (line.length < 3) {
                    tp.setText("Please enter at least one feature with the 'search' command.");
                } else {
                    if (line.length%2 - 1 != 0) {
                        tp.setText("Each feature must be accompanied by a desired value.\nexample: \"+ voice\"");
                    }
                    tp.setText(searchByFeature(line));
                }
			}
			s += e.getKeyChar();
			a.add(s);
			
			if (!a.isEmpty())
			{
					s = s.substring(s.lastIndexOf("\n") + 1, s.length());
			}
		}
		else if (e.getKeyCode() != KeyEvent.VK_SHIFT)
		{
			s += e.getKeyChar();
		}
	}
	
	public void outputTo (JTextPane out, StyledDocument settings, SimpleAttributeSet alignment)
	{
		tp = out;
		doc = settings;
	}
	
	private void easyInsert(String s)
	{
    	try
		{
			doc.insertString(doc.getLength(), s, left);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}
	
	private JScrollPane getTableComponent(String[] line)
	{
		PhonomaticApplet.err.clear();
		String[] fill = searchByCharacterX(line, 1);
		if (headers.size() < 1) {
			for (String s : PhonomaticApplet.err) {
				easyInsert("The character '" + s + "' was not found in the Phonomatic database.\n");
			}
			return new JScrollPane();
		}
		
		final String[] columnNames = new String[headers.size()];
		for (int i = 0; i < headers.size(); i++) {
			columnNames[i] = headers.get(i).toUpperCase();
		}
		int divide = (fill.length)/(PhonomaticApplet.features.size());
        final String[][] data = new String[headers.size()][PhonomaticApplet.features.size()];
        
        for (int i = 0; i < divide; i ++) {
        	for (int j = 0; j < PhonomaticApplet.features.size(); j++) {
            	data[i][j] = fill[j + (i*PhonomaticApplet.features.size())];
            }
        }
        DefaultTableModel model = new DefaultTableModel(data[0].length, columnNames.length);
		JTable table = new JTable(model) {
		    public int getRowCount() { return data[0].length; }
		    public int getColumnCount() { return columnNames.length; }
		    public Object getValueAt(int row, int col) {
		        return data[col][row];
		    }
		};
		
		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn c = table.getTableHeader().getColumnModel().getColumn(i);
			c.setHeaderValue(headers.get(i).toUpperCase());
		}
		
		Dimension d = table.getPreferredSize();
		d.width = 400;
		table.setPreferredScrollableViewportSize(d);
		//table.setFont(PhonomaticApplet.modern);
		for (String s : PhonomaticApplet.err) {
			//easyInsert("The character '" + s + "' was not found in the Phonomatic database.\n");
		}
		headers.clear();
		columns.clear();
		PhonomaticApplet.err.clear();
		return new JScrollPane(table);
	}
	
	public String[] searchByCharacterX(String[] line, int index)
	{
		if (index < line.length)
        {
            String s = line[index];
            found = false;
            //Search through characters in "grid":
        	for (ArrayList<PhonomaticApplet.Entry<String, Integer>> a : PhonomaticApplet.characters)
        	{
    			//Once the charcter's ArrayList is identified, it prints the entire feature list:
    			if (a.get(0).character.equals(s))
    			{
    				headers.add(s);
    				found = true;
    			    String plusminus;
    			    //Translates each Entry in the features list to a string and adds it to s:
    		        for (PhonomaticApplet.Entry<String, Integer> e : a)
    		        {
    	                if (e.value > 0)
    	                {
    	                	if (e.value != 2)
    	                	{
    	                		plusminus = "+ ";
    	                	} else
    	                	{
    	                		plusminus = "+/-";
    	                	}    	                    
    	                } else if (e.value < 0)
    	                {
    	                    plusminus = "- ";
    	                } else {
    	                    plusminus = "0 ";
    	                }
    	                columns.add(plusminus + e.feature);
                    }
    			}
    		}
        	if (!found)
        	{
                PhonomaticApplet.err.add(line[index]);
            } else if (index < line.length - 1) {
            	index ++;
                searchByCharacterX(line, index);
            }
        }
        return columns.toArray(new String[columns.size()]);
    }
	
    private void createStyles(StyledDocument doc) {
    	//System.out.println(doc.getLength());
        Style baseStyle = doc.addStyle("base", null);
        StyleConstants.setFontFamily(baseStyle, "Calibri");
        StyleConstants.setFontSize(baseStyle, 18);
        StyleConstants.setFirstLineIndent(baseStyle, 20f);
        StyleConstants.setLeftIndent(baseStyle, 10f);
 
        Style style = doc.addStyle("bold", baseStyle);
        //StyleConstants.setBold(style, true);
 
        style = doc.addStyle("italic", baseStyle);
        StyleConstants.setItalic(style, true);
 
        style = doc.addStyle("blue", baseStyle);
        StyleConstants.setForeground(style, Color.blue);
 
        style = doc.addStyle("underline", baseStyle);
        StyleConstants.setUnderline(style, true);
 
        style = doc.addStyle("green", baseStyle);
        StyleConstants.setForeground(style, Color.green.darker());
        StyleConstants.setUnderline(style, true);
 
        style = doc.addStyle("highlight", baseStyle);
        StyleConstants.setForeground(style, Color.yellow);
        StyleConstants.setBackground(style, Color.black);
 
        style = doc.addStyle("table", null);
        //StyleConstants.setComponent(style, getTableComponent(line));
 
        style = doc.addStyle("tableParagraph", null);
        StyleConstants.setLeftIndent(style, 35f);
        StyleConstants.setRightIndent(style, 35f);
        StyleConstants.setSpaceAbove(style, 15f);
        StyleConstants.setSpaceBelow(style, 15f);
    }
 
    private void styleContent(StyledDocument doc) {
        //Style style = doc.getStyle("base");
        //doc.setLogicalStyle(doc.getLength(), style);
        //style = doc.getStyle("underline");
        //doc.setCharacterAttributes(doc.getLength() + 22, 10, style, false);
        //style = doc.getStyle("highlight");
        //doc.setCharacterAttributes(doc.getLength() + 62, 26, style, false);
 
        //Style logicalStyle = doc.getLogicalStyle(0);
        //style = doc.getStyle("tableParagraph");
        //doc.setParagraphAttributes(doc.getLength() + 90, 1, style, false);
        //style = doc.getStyle("table");
        //doc.setCharacterAttributes(doc.getLength() + 90, 1, style, false);
        //doc.setLogicalStyle(doc.getLength() + 92, logicalStyle);
 
        //style = doc.getStyle("blue");
        //doc.setCharacterAttributes(118, 13, style, false);
        //style = doc.getStyle("italic");
        //doc.setCharacterAttributes(166, 18, style, false);
        //style = doc.getStyle("green");
        //doc.setCharacterAttributes(235, 9, style, false);
        //doc.setCharacterAttributes(248, 9, style, false);
        //style = doc.getStyle("bold");
        //doc.setCharacterAttributes(263, 10, style, false);
        //doc.setCharacterAttributes(278, 6, style, false);
    }
    
	public String searchByFeature(String[] line)
	{
	    	ArrayList<PhonomaticApplet.Entry<String, Integer>> list = new ArrayList<PhonomaticApplet.Entry<String, Integer>>();
	    	int a = 1;
	    	int b = 2;
	    	for (int i = 1; i < line.length/2 + 1; i++)
	    	{
	    		String sign = line[a];
	    		String feature = line[b];
	    		int parameter = 0;
	        	if (sign.equals("+"))
	        	{
	    			parameter = 1;
	    		} else if (sign.equals("-"))
	    		{
	    			parameter = -1;
	    		} else if (sign.equals("0"))
	    		{
	    			parameter = 0;
	    		} else
	    		{
	    			tp.setText(sign + " is not a valid value for " + feature);
	    		}
	        	
	        	int index = PhonomaticApplet.indexThis(feature, parameter, "a");
	        	if (index != -1)
	        	{
	        		if (i == 1)
	        		{
	            		ListIterator<ArrayList<PhonomaticApplet.Entry<String, Integer>>> it = PhonomaticApplet.characters.listIterator();
	                	while (it.hasNext())
	                	{
	            			PhonomaticApplet.Entry<String, Integer> e = it.next().get(index);
	            			if (parameter != 0)
	            			{
	            				if (e.value == parameter | e.value == 2)
	            				{
	                				list.add(e);
	                			};
	            			} else
	            			{
	            				if (e.value == parameter)
	            				{
	                				list.add(e);
	                			};
	            			}
	            		}
	            	} else {
	            		int l = 0;
	            		while (l < list.size()) {
	            			PhonomaticApplet.Entry<String, Integer> e = list.get(l);
		            			int j = 0;
		            			while (j < PhonomaticApplet.characters.size())
		            			{
		            				ArrayList<PhonomaticApplet.Entry<String, Integer>> character = PhonomaticApplet.characters.get(j);
		            				if (character.get(0).character.equals(e.character))
		            				{
		            					if (character.get(index).value != parameter)
		            					{
		                    				list.remove(l);
		                    				l--;
		                    			}
		            					j = PhonomaticApplet.characters.size();
		            				}
		            				j += 1;
		            			}
		            		l ++;
	            		}
	            	} 
	        	} else
	        	{
	        		return "The feature '" + feature + "' is not present within the Phonomatic database.\n";
	        	}
	        	a += 2;
	        	b += 2;
	        }
	    	String out = "[";
	    	for (PhonomaticApplet.Entry<String, Integer> e : list)
	    	{
				out += e.character + ", ";
			}
	    	if (out.length() > 1)
	    	{
	    		out = out.substring(0, out.length() - 2);
	    	}    	
	    	out += "]";
	    	return out;
	    }
	
	public ArrayList<String> searchByCharacter(String[] line, int index)
	{
        if (index < line.length)
        {
            String s = line[index];
            //Search through characters in "grid":
        	for (ArrayList<PhonomaticApplet.Entry<String, Integer>> a : PhonomaticApplet.characters)
        	{
    			//Once the charcter's ArrayList is identified, it prints the entire feature list:
    			if (a.get(0).character.equals(s))
    			{
    			    String plusminus;
    			    PhonomaticApplet.out.add("-----------------");
    			    PhonomaticApplet.out.add("|       " + a.get(0).character + "\t|");
    			    PhonomaticApplet.out.add("-----------------");
    			    //Translates each Entry in the features list to a string and adds it to s:
    		        for (PhonomaticApplet.Entry<String, Integer> e : a)
    		        {
    	                if (e.value > 0)
    	                {
    	                	if (e.value != 2)
    	                	{
    	                		plusminus = "+ ";
    	                	} else
    	                	{
    	                		plusminus = "+/-";
    	                	}    	                    
    	                } else if (e.value < 0)
    	                {
    	                    plusminus = "- ";
    	                } else {
    	                    plusminus = "0 ";
    	                }
    	                PhonomaticApplet.out.add("|  " + plusminus + e.feature + "\t|");
                    };
                    PhonomaticApplet.out.add("-----------------");                    
                    
                    if (index < line.length - 1)
                    {
                        index ++;
                        searchByCharacter(line, index);  
                    } else
                    {
                        return PhonomaticApplet.out;
                    }
    			};
    		}
        	if (index < line.length)
        	{         
                PhonomaticApplet.out.add("The character '" + line[index] + "' was not found in the Phonomatic database.\n");
                for (int i = 0; i < PhonomaticApplet.features.size() + 3; i++)
                {
                    PhonomaticApplet.out.add(null);
                }
                index ++;
                searchByCharacter(line, index);            
            }
        }
        return PhonomaticApplet.out;
    }
	
	
	
	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
