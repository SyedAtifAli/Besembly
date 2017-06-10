package Errors;

public class RuntimeError extends Error
{
	public RuntimeError(int ErrorCode, String ErrorDescription, String ErrorSolution)
	{
		super(ErrorCode,ErrorDescription,ErrorSolution);
		
		this.errorDescription = "Runtime Error: " + this.errorDescription;
	}
}
