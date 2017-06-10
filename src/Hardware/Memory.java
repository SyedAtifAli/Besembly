package Hardware;

import java.util.Arrays;
import java.util.Vector;

public class Memory implements Runnable
{
	//Bellek boyutu
	public int memorySize = 0;
	private Vector<Boolean[]> memoryContent;
	
	@Override
	public void run() 
	{
		this.memoryContent = new Vector<Boolean[]>(memorySize);
		for(int i=0; i<memorySize; i++)
		{
			memoryContent.add(new Boolean[8]);
		}
	}

	public Memory(int memSize)
	{
		this.memorySize = memSize;
	}

	//Fiziksel address uzerinden islem yapilir. Program adresi ile ilgilenmez.
	public boolean setMem(int address, boolean[] param)
	{
		boolean result = false;
		
		Boolean[] content = new Boolean[param.length];
		for(int i=0; i<param.length; i++)
		{
			content[i] = param[i];
		}
		
		int turn = 1;
		//Eger gelen icerik 1 byte'dan buyukse
		if(content.length > 8)
		{
			turn = content.length/8;
		}
		
		if(address < memorySize && address >= 0)
		{
			for(int i=0; i<turn; i++)
			{			
				memoryContent.set(address + i,Arrays.copyOfRange(content, i*8, i*8 + 8));
				for(int j=0; j<listeners.size(); j++)
				{
					listeners.get(j).UpdateMemoryCell(address + i);
				}
			}
			result = true;
		}
		return result;
	}
	//Fiziksel address uzerinden islem yapilir. Program adresi ile ilgilenmez.
	public boolean[] getMem(int address, int byteRequest)
	{
		boolean[] result = null;
		
		if(address < memorySize && address >= 0)
		{
			result = new boolean[byteRequest*8];
			for(int i=0; i<byteRequest; i++)
			{
				for(int j=0; j<8; j++)
				{
					if(memoryContent.get(address + i)[j] != null)
					{
						result[i*8 + j] = memoryContent.get(address + i)[j];
					}
					else
					{
						result[i*8 + j] = false;
					}
				}
			}
		}
		return result;
	}
	
	public void resetMemory()
	{
		this.memoryContent = new Vector<Boolean[]>(memorySize);
		for(int i=0; i<memorySize; i++)
		{
			memoryContent.add(new Boolean[8]);
		}
		
		for(int j=0; j<memorySize; j++)
		{
			for(int k=0; k<listeners.size(); k++)
			{
				listeners.get(k).UpdateMemoryCell(j);
			}
		}
	}
	public Vector<Gui.MemoryArea> listeners = new Vector<Gui.MemoryArea>();
	public void addListener(Gui.MemoryArea memArea)
	{
		listeners.add(memArea);
	}
	public void makeListenerUpdated()
	{
		for(int j=0; j<listeners.size(); j++)
		{
			listeners.get(j).UpdateArea();
		}
	}
}
