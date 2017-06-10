package Gui;

import javax.swing.JTextPane;

//Singleton Pattern'a gore dizayn edilmistir.
public class CodeArea extends JTextPane
{
	private static final long serialVersionUID = 2619399817857366698L;
	
	private static CodeArea instance = null;
    protected CodeArea() 
    {
    	super();
	}
    public static CodeArea get() 
    {
    	if(instance == null) 
    	{
    		instance = new CodeArea();
	    }
	    return instance;
	}
	public void ResetCode()
	{
		this.setText("");
	}
	public void SetText(String text)
	{
		this.setText(text);
	}
}