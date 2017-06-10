package Errors;

import java.util.concurrent.ConcurrentLinkedQueue;

import android.widget.TextView;

public class CurrentErrors extends ConcurrentLinkedQueue<Error>
{
	private static final long serialVersionUID = 1L;
	
	private static CurrentErrors instance = null;

	private ConsoleArea console = ConsoleArea.get();
	private TextView console = null;
	
    public static CurrentErrors get() 
    {
    	if(instance == null) 
    	{
    		instance = new CurrentErrors();
	    }
	    return instance;
	}
	protected CurrentErrors()
	{
		super();
		
		console = 
	}
	
	public void addError(Error newError)
	{
		console.
		console.AddConsoleLine("Error Code: " + newError.GetErrorCode());
		console.AddConsoleLine(newError.GetErrorDescription());
		console.AddConsoleLine(newError.GetErrorSolution());
		this.add(newError);
	}
}
