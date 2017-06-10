package Root;

import java.util.Vector;

public class InstructionSyntax 
{
	private String instructionName;
	private int operantCount;
	private Vector<Integer> firstOperand = new Vector<Integer>();
	private Vector<Integer> secondOperand = new Vector<Integer>();
	
	public InstructionSyntax(String inInstructionName, int inOperandCount)
	{
		this.instructionName = inInstructionName;
		this.operantCount = inOperandCount;
	}
	
	public void FillFirstOperand(int[] operand)
	{
		if(firstOperand.size() == 0 && operantCount > 0)
		{
			for(int i: operand)
			{
				firstOperand.add(i);
			}
		}
	}
	public void FillSecondOperand(int[] operand)
	{
		if(secondOperand.size() == 0 && operantCount == 2)
		{
			for(int i: operand)
			{
				secondOperand.add(i);
			}
		}
	}
	public String getName()
	{
		return this.instructionName;
	}
	public boolean verifyAccuracy(int... input)
	{
		boolean result = false;
		
		if(this.operantCount > 0)
		{
			if(input.length == this.operantCount)
			{
				Vector<Integer> found = new Vector<Integer>();
				for(int i=0; i<firstOperand.size(); i++)
				{
					if(firstOperand.get(i) == input[0])
					{
						if(operantCount == 2)
						{
							found.add(secondOperand.get(i));
						}
						else
						{
							found.add(-1);
						}
					}
				}
				
				if(this.operantCount == 2 && found.size() > 0)
				{
					for(int i = 0; i<found.size(); i++)
					{
						if(found.get(i) == input[1])
						{
							result = true;
						}
					}
				}
				else
				{
					if(found.size() > 0)
					{
						result = true;
					}
				}
			}
		}
		return result;
	}
}
