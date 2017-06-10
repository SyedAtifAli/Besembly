package Hardware;

import java.util.Vector;

public class HardDisk
{
	int hardDiskSize = 0;
	Vector<String> hardDiskFiles = null;
	Vector<String> hardDiskContent = null;
	
	public HardDisk(int hddSize)
	{
		this.hardDiskSize = hddSize;
		this.hardDiskContent = new Vector<String>(hardDiskSize);
		this.hardDiskFiles = new Vector<String>(hardDiskSize);
	}
	
	public int AddNewFile(String fileName, String content)
	{
		hardDiskFiles.add(fileName);
		hardDiskContent.add(content);
		
		return hardDiskFiles.size()-1;
	}
	public String GetFile(int index)
	{
		return hardDiskContent.get(index);
	}
	public String SearchFile(String fileName)
	{
		int index = hardDiskFiles.indexOf(fileName);
		
		return hardDiskContent.get(index);
	}
	
	public void resetDisk()
	{
		this.hardDiskContent = new Vector<String>(hardDiskSize);
		this.hardDiskFiles = new Vector<String>(hardDiskSize);
	}
}
