package Fragments;

//Bu sinif; duzenlenmis variable listesini olusturacak elemanlari tanimlar.
//Adres ve adres icerigini tutar.
public class VarFactor implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int address;
	private boolean[] content;
	
	public VarFactor(int inAddress, boolean[] inContent)
	{
		this.address = inAddress;
		this.content = inContent;
	}
	
	//Getter'lar
	public int getAddress()
	{
		return address;
	}
	public boolean[] getContent()
	{
		return content;
	}
}
