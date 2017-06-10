package Fragments;

import java.util.Vector;

import Root.Global;

public class Variable extends Fragment implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	//Hepsi icin gerekli olanlar
	private int variableTypeDirective = -1;
	private String variableName = null;
	
	//DUP icin gerekli olanlar
	private String dupParameter = null;
	private int dupParamaterType = -1;
	private String dupDefinerValue = null;
	private int dupDefinerValueType = -1;
	
	//Array ve tekil tanimlamalar icin gerekli olanlar
	private int parameterCount = -1;
	private String[] parameters;
	private Integer[] parameterTypes;
	
	//VARIABLE_NAME DB 30 DUP(?)
	public Variable(String variableString, String variableName, int variableTypeDirective, String value, int valueType, String dupParameter, int dupParameterType)
	{
		super(Global.variable);
		this.fragmentString = variableString;
		this.variableName = variableName;
		this.variableTypeDirective = variableTypeDirective;
		this.dupParameter = dupParameter;
		this.dupParamaterType = dupParameterType;
		this.dupDefinerValue = value;
		this.dupDefinerValueType = valueType;
	}
	//VARIABLE_NAME DB 30 ya da VARIABLE_NAME DB 30,40,50,60
	public Variable(String variableString, String variableName, int variableTypeDirective, Vector<String> values, Vector<Integer> valueTypes)
	{
		super(Global.variable);
		this.fragmentString = variableString;
		this.variableName = variableName;
		this.variableTypeDirective = variableTypeDirective;
	
		this.parameterCount = values.size();
		
		this.parameters = new String[parameterCount];
		this.parameterTypes = new Integer[parameterCount];

		for(int i=0; i<values.size(); i++)
		{
			this.parameters[i] = values.get(i);
			this.parameterTypes[i] = valueTypes.get(i);
		}
	}
	
	//Getter'lar
	public int getVariableTypeDirective() 
	{
		return variableTypeDirective;
	}
	public String getVariableName() 
	{
		return variableName;
	}
	
	public String getDupParameter() 
	{
		return dupParameter;
	}
	public int getDupParameterType() 
	{
		return dupParamaterType;
	}
	public String getDupDefinerValue() 
	{
		return dupDefinerValue;
	}
	public int getDupDefinerValueType() 
	{
		return dupDefinerValueType;
	}
	
	public int getParameterCount() 
	{
		return parameterCount;
	}
	public String[] getParameters() 
	{
		return parameters;
	}
	public Integer[] getParameterTypes() 
	{
		return parameterTypes;
	}
}
