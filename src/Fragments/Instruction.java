package Fragments;

import Root.Global;

public class Instruction extends Fragment implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private String opcode = null;
	
	private String firstOperand = null;
	private int firstOperandType = -1;
	
	private String secondOperand = null;
	private int secondOperandType = -1;
	
	private int operandCount = -1;
	
	private int instructionAddress = -1;
	
	public Instruction()
	{
		super(Global.instruction);
	}
	
	//Overload edilmis Buyruk constructor'larini tanimlayalim.
	
	//Buyruk => OPCODE
	public Instruction(String instructionString, String opcode)
	{
		super(Global.instruction);
		this.fragmentString = instructionString;
		this.opcode = opcode;
		this.operandCount = 0;
	}
	//Buyruk => OPCODE OPCODE/LABEL/YAZMAC/ADRES/ANLIK
	public Instruction(String instructionString, String opcode, String firstOperand, int firstOperandType)
	{
		super(Global.instruction);
		this.fragmentString = instructionString;
		this.opcode = opcode;
		this.firstOperand = firstOperand;
		this.firstOperandType = firstOperandType;
		this.operandCount = 1;
	}
	//Buyruk => OPCODE YAZMAC/ADRES/LABEL, ADRES/YAZMAC/ANLIK/LABEL
	public Instruction(String instructionString, String opcode, String firstOperand, int firstOperandType, String secondOperand, int secondOperandType)
	{
		super(Global.instruction);
		this.fragmentString = instructionString;
		this.opcode = opcode;
		this.firstOperand = firstOperand;
		this.firstOperandType = firstOperandType;
		this.secondOperand = secondOperand;
		this.secondOperandType = secondOperandType;
		this.operandCount = 2;
	}
	
	//Getter'lar
	public String getOpcode() 
	{
		return opcode;
	}
	public String getFirstOperand() 
	{
		return firstOperand;
	}
	public int getFirstOperandType() 
	{
		return firstOperandType;
	}
	public String getSecondOperand() 
	{
		return secondOperand;
	}
	public int getSecondOperandType() 
	{
		return secondOperandType;
	}
	public int getOperandCount() 
	{
		return operandCount;
	}
	//Setter'lar
	public void setOpcode(String opc) 
	{
		opcode = opc;
	}
	public void setFirstOperand(String operand) 
	{
		firstOperand = operand;
	}
	public void setFirstOperandType(int operandType) 
	{
		firstOperandType = operandType;
	}
	public void setSecondOperand(String operand) 
	{
		secondOperand = operand;
	}
	public void setSecondOperandType(int operandType) 
	{
		secondOperandType = operandType;
	}	
	public void setInstructionAddress(int address)
	{
		instructionAddress = address;
	}
	public int getInstructionAddress()
	{
		return instructionAddress;
	}
	public String getInstructionString()
	{
		return fragmentString;
	}
}
