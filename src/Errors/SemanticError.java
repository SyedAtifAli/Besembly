package Errors;

public class SemanticError extends Error
{
	public SemanticError(int ErrorCode, String ErrorDescription, String ErrorSolution)
	{
		super(ErrorCode,ErrorDescription,ErrorSolution);
		
		this.errorDescription = "Semantic Error: " + this.errorDescription;
	}
}
