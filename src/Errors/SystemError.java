package Errors;

public class SystemError extends Error
{
	public SystemError(int ErrorCode, String ErrorDescription, String ErrorSolution)
	{
		super(ErrorCode,ErrorDescription,ErrorSolution);
		
		this.errorDescription = "System Error: " + this.errorDescription;
	}
}
