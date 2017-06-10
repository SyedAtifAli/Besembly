package Fragments;

import Root.Global;

public class Directive extends Fragment
{
	private static final long serialVersionUID = 1L;
	
	private int directiveType = -1;
	private String directiveParameter = null;
	
	//Overload edilmis Buyruk constructor'larini tanimlayalim.
	
	//.CODE .DATA .STARTUP .EXIT
	public Directive(String directiveString, int directiveType)
	{
		super(Global.directive);
		this.fragmentString = directiveString;
		this.directiveType = directiveType;
	}
	
	//.MODEL .STACK .END
	public Directive(String directiveString, int directiveType, String directiveParameter)
	{
		super(Global.directive);
		this.fragmentString = directiveString;
		this.directiveType = directiveType;
		this.directiveParameter = directiveParameter;
	}
	
	//Getter'lar
	public int getDirectiveType()
	{
		return directiveType;
	}
	public String directiveParameter()
	{
		return directiveParameter;
	}
}
