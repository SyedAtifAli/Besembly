package Gui;

import javax.swing.JTextArea;

//Singleton Pattern'a gore dizayn edilmistir.
public class ConsoleArea extends JTextArea
{
	private static final long serialVersionUID = 2609399817857366698L;
	
	private static ConsoleArea instance = null;
    protected ConsoleArea() 
    {
    	super();
	}
    public static ConsoleArea get() 
    {
    	if(instance == null) 
    	{
    		instance = new ConsoleArea();
	    }
	    return instance;
	}
	public void AddConsoleLine(String line)
	{
		this.append(line+"\n");
		
		this.setCaretPosition(this.getDocument().getLength());
	}
	public void ResetConsole()
	{
		this.setText("");
		
		this.setCaretPosition(this.getDocument().getLength());
	}
}