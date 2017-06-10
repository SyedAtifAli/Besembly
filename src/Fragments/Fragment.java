package Fragments;

public class Fragment implements java.io.Serializable 
{
	private static final long serialVersionUID = 1L;

	private int fragmentType;
	
	//Butun fragment'larda olmasi gereken fragmentString'i protected olarak atayalim.
	protected String fragmentString = null;
	
	//Subclass'lardan bagimsiz olarak bu sinifin calistirilamamasi icin constructor protected'dir.
	protected Fragment(int type)
	{
		this.fragmentType = type;
	}
	
	public int getFragmentType()
	{
		return fragmentType;
	}
}
