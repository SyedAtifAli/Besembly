package Gui;

import javax.swing.JLabel;

import Root.Global;

//Singleton Pattern'a gore dizayn edilmistir.
public class RegistersArea extends JLabel implements Runnable
{
	private static final long serialVersionUID = 2629399817857366698L;
	
	@Override
	public void run() 
	{
		for(int i=0; i<count; i++)
    	{
    		UpdateRegister(i);
    	}
    	UpdateArea();
	}
	
	private static final int count = Global.registerList.length;
	
	private String finalString;
	private String[] registerStrings = new String[count];
	
	//Pattern fonksiyonlari
	private static RegistersArea instance = null;
	public static RegistersArea get()
	{
		if(instance == null)
		{		
			instance = new RegistersArea();
		}
		return instance;
	}
	
    protected RegistersArea() 
    {
    	super();
    	GetPatched();
	}

    public void UpdateRegister(final int register)
    {
    	if(Global.registerSizes[register] == 2)//Yalnizca 2 byte'liklar gosterilsin.
		{
			if(Global.registerChildren[register] != -1)
			{
				registerStrings[register] = "<tr><td><font size=4>" + Global.registerList[register] + "</font></td><td>" + Global.GetRegister(Global.registerChildren[register]) + "</td><td>" + Integer.toHexString(Global.GetRegister(Global.registerChildren[register]+1)) + "</td></tr>";
			}
			else
			{
				registerStrings[register] = "<tr><td><font size=4>" + Global.registerList[register] + "</font></td><td>" + Integer.toHexString(Global.GetRegister(register)) + "</td></tr>";
			}
		}
    	UpdateArea();
	}
	private void UpdateArea()
	{
		finalString = "<html>";
		finalString += "<table border='0' width='100%' cellpadding='5' cellspacing='5'>";
		finalString += "<tr><td><font size=5>Name</font></td><td><font size=5>High</font></td><td><font size=5>Low</font></td></tr>";
		
		boolean state = false;
		for(int i=0; i<count; i++)
		{
			if(Global.registerSizes[i] == 2)
			{
				if(Global.registerChildren[i] == -1)
				{
					if(!state)
					{
						finalString += "<tr></tr><tr></tr><tr><td><font size=5>Name</font></td><td><font size=5>Value</font></td></tr>";
						state = true;
					}
				}
				finalString += registerStrings[i];
			}
		}
		finalString += "</table></html>";
			
		this.setText(finalString);
	}
	private void GetPatched()
	{
		Global.guiRegPatch = this;
	}
}