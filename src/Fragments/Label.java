package Fragments;

import Root.Global;

public class Label extends Fragment
{
	private static final long serialVersionUID = 1L;
	
	private String labelName = null;
	
	public Label(String labelString, String labelName)
	{
		super(Global.label);
		this.fragmentString = labelString;
		this.labelName = labelName;
	}
	
	public String getLabelName()
	{
		return labelName;
	}
}
