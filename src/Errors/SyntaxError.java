package Errors;

public class SyntaxError extends Error
{
	public SyntaxError(int ErrorCode, String ErrorDescription, String ErrorSolution)
	{
		super(ErrorCode,ErrorDescription,ErrorSolution);
		
		this.errorDescription = "Syntax Error: " + this.errorDescription;
	}
}
