package Gui;

import javax.swing.JLabel;

import Root.Global;

//Singleton Pattern'a gore dizayn edilmistir.
public class FlagsArea extends JLabel
{
	private static final long serialVersionUID = 2629499817857366698L;
	
	private static final int count = Global.flagsCount;
	
	private String finalString;
	private String[] flagStrings = new String[count];
	
	private static FlagsArea instance = null;
    protected FlagsArea() 
    {
    	super();
    	for(int i=0; i<count; i++)
    	{
    		UpdateFlag(i);
    	}
    	UpdateArea();
    	GetPatched();
	}
    public static FlagsArea get() 
    {
    	if(instance == null) 
    	{
    		instance = new FlagsArea();
	    }
	    return instance;
	}
    
    public void UpdateFlag(final int flag)
    {
    	flagStrings[flag] = "<table border='0'><tr><font size=5>" + Global.flagList[flag] + "</font></tr><tr>" + Global.getFlag(flag) + "</tr></table>";
    	UpdateArea();
	}
	private void UpdateArea()
	{
		finalString = "<html>";
		finalString += "<table border='0'>";

		for(int i=0; i<count; i++)
		{
			finalString +=  "<td>" + flagStrings[i] + "</td>";
		}
		finalString += "</table></html>";
		
		this.setText(finalString);
	}
	private void GetPatched()
	{
		Global.guiFlagPatch = this;
	}
}