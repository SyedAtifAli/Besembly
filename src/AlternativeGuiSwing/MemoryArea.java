package Gui;

import javax.swing.JLabel;

import Hardware.Memory;
import Root.Global;

//Singleton Pattern'a gore dizayn edilmistir.
public class MemoryArea extends JLabel implements Runnable 
{
	private static final long serialVersionUID = 3619399817857366698L;
	
	private String finalString;
	private String[] memoryString = new String[Global.defaultMemorySize];
	private MemoryArea instance = null;
	private Memory memory;
	
	public void run() 
	{
    	for(int i=0; i<Global.defaultMemorySize; i++)
    	{
    		UpdateMemoryCell(i);
    	}
    	UpdateArea();
	}

    public MemoryArea(Memory mem) 
    {
    	super();
    	this.memory = mem;
    	this.memory.addListener(this);
    	this.instance = this;
	}
    public void UpdateMemoryCell(final int address)
    {
    	memoryString[address] = "<tr><td><font size=5>" + address + "</font></td><td>" + Global.BitsToUnsignedInt(memory.getMem(address, 1)) + "</td></tr>";
    }
	public void UpdateArea()
	{
		new Thread() 
		{
	        @Override
	        public void run() 
	        {
	        	finalString = "<html>";
	    		finalString += "<table border='0'>";

	    		for(int i=0; i<Global.defaultMemorySize; i++)
	    		{
	    			finalString +=  memoryString[i];
	    		}
	    		finalString += "</table></html>";
	    		
	    		instance.setText(finalString);
	        }
	    }.run();
	}
}