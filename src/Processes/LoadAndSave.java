package Processes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import javax.xml.bind.DatatypeConverter;

import Hardware.HardDisk;

public final class LoadAndSave 
{
	private HardDisk hdd;
	
	private static Vector<LoadAndSave> instances = new Vector<LoadAndSave>();
	
    public static LoadAndSave get(HardDisk disk) 
    {
    	LoadAndSave instance = null;
    	
    	if(instances.contains(disk) == false) 
    	{
    		instance = new LoadAndSave(disk);
    		instances.add(instance);
	    }
	    return instance;
	}
	
	protected LoadAndSave(HardDisk disk)
	{
		this.hdd = disk;
	}
	
	public final int Save(String fileName, Vector<Object> list) throws IOException
	{	
		//Byte Output Stream'i olusturalim.
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		
		//Output Stream'i byte output'a ayarlayalim.
		ObjectOutputStream outputStream = new ObjectOutputStream(byteOutput);
		
		//Listeyi yazdiralim.
		outputStream.writeObject(list);
		outputStream.flush();
		
		//Olusan byteOutput'un son halini byte array haline getirelim.
		byte[] binary = byteOutput.toByteArray();
		
		//64'luk tabanda stringify edelim.
		String content = DatatypeConverter.printHexBinary(binary); 
		
		//Isme gore sanal teker'e dosyayi gondermemiz gerekiyor.
		int fileIndex = hdd.AddNewFile(fileName, content);
		
		return fileIndex;
	}
	public Vector<Object> Load(String fileName) throws IOException
	{
		String fileContent = hdd.SearchFile(fileName);
		
		Vector<Object> list = null;
		
		if(fileContent != null)
		{
			byte[] resultBinary = DatatypeConverter.parseHexBinary(fileContent);
			ByteArrayInputStream resultBarr = new ByteArrayInputStream(resultBinary);
			ObjectInputStream objIn = new ObjectInputStream(resultBarr); 
			Object obj = null;
			try 
			{
				obj = objIn.readObject();
			} 
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
			@SuppressWarnings("unchecked")
			Vector<Object> iList = (Vector<Object>) obj;
			if(iList != null)
			{
				list = iList;
			}
		}
		return list;
	}
}
