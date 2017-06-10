package Errors;

public class Error 
{
	protected int errorCode;
	protected String errorDescription = null;
	protected String errorSolution = null;
	
	public Error(int ErrorCode, String ErrorDescription, String ErrorSolution)
	{
		this.errorCode = ErrorCode;
		this.errorDescription = ErrorDescription;
		this.errorSolution = ErrorSolution;
	}

	protected int GetErrorCode() { return this.errorCode; }

	protected String GetErrorDescription() { return this.errorDescription; }
	
	protected String GetErrorSolution() { return this.errorSolution; }
}
