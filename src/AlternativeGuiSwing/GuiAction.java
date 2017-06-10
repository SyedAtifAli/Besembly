package Gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import Fragments.Instruction;
import Hardware.HardDisk;
import Hardware.Memory;
import Root.Global;
import Analysis.*;

public class GuiAction 
{
	private ConsoleArea console = ConsoleArea.get();
	private CodeArea codeArea = CodeArea.get();
	
	private String fileName = "";
	private String fileLocation = "";
	
	private Memory memory;
	private HardDisk disk;
	
	public GuiAction(Memory mem, HardDisk hdd)
	{
		this.memory = mem;
		this.disk = hdd;
	}
	public Memory getMemory()
	{
		return memory;
	}
	public HardDisk getHardDisk()
	{
		return disk;
	}
	
	public void OpenFile()
	{ 
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("ASM & BSM", "asm", "bsm");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) 
	    {
	    	fileName = chooser.getSelectedFile().getName();
	    	fileLocation = chooser.getSelectedFile().getAbsolutePath();
	    	try 
    		{
		    	if(fileName.endsWith("asm") || fileName.endsWith("bsm"))//Duzenlenebilir bir dosyadir.
		    	{
	    			FileReader fr = new FileReader(chooser.getSelectedFile());
				        	
		        	String fileContents = "";
					int i;
					while((i = fr.read()) != -1)
					{
						char ch = (char)i; 
						fileContents = fileContents + ch; 
					}
					fileContents += " ";
		            fr.close();
		            
		            codeArea.SetText(fileContents);
		    	}
    		} 
    		catch (FileNotFoundException e) 
			{
				console.AddConsoleLine("Error: File cannot be opened.");
			} 
    		catch (IOException e) 
    		{
    			console.AddConsoleLine("Error: File cannot be opened.");
			}
	    }
	    else
	    {
	    	console.AddConsoleLine("Error: File cannot be opened.");
	    }
	}
	
	public void Run() throws IOException
	{ 
		if(codeArea.getText().length() == 0)
		{
			console.AddConsoleLine("Error: Code is empty.");
		}
		else
		{
			Reload();
			
			long tStart = System.currentTimeMillis();
			
			//Oncelikle syntax analizini tamamlayalim.
			SyntaxAnalysis syntax = new SyntaxAnalysis(codeArea.getText() + " \n");
			
			//Eger syntax analizi basarili olduysa
			if(syntax.isSyntaxAnalysisSucceeded() == true)
			{
				//Semantic analize gecelim.
				SemanticAnalysis semantic = new SemanticAnalysis(syntax.getFragmentationList());
				
				//Eger semantic analiz basarili olduysa
				if(semantic.isSemanticAnalysisSucceeded() == true)
				{						
					//.be dosyasini olusturabiliriz.
					Processes.LoadAndSave loadSave = Processes.LoadAndSave.get(disk);
					
					Vector<Object> objList = new Vector<Object>();
					objList.add(new Integer(semantic.getStackSize()));
					objList.add(semantic.getDataSegmentAddressList());
					objList.add(semantic.getInstructionList());
					
					loadSave.Save(fileName, objList);
					
					//Runtime analize gecelim.
					RuntimeAnalysis runtime = new RuntimeAnalysis(fileName, disk, memory, -1);
					
					if(runtime.isRuntimeAnalysisSucceeded() == true)
					{
						long tEnd = System.currentTimeMillis();
						long tDelta = tEnd - tStart;
						double elapsedSeconds = tDelta / 1000.0;
						
						console.AddConsoleLine("Successful!");
						console.AddConsoleLine("Time Elapsed in Seconds: " + elapsedSeconds);
						new Thread(new Runnable() 
						{
							public void run() 
			                {
								memory.makeListenerUpdated();
			                }   
				        }).start();
					}
				}
			}
		}
	}
	
	private int currentStep = 0;
	private Vector<Instruction> instructions = null;
	private SyntaxAnalysis syxAnalysis = null;
	private SemanticAnalysis semAnalysis = null;
	private Processes.LoadAndSave loSa = null;
	private Vector<Object> objectList = null;
	private RuntimeAnalysis runtime = null;
	public void SingleStep()
	{ 
		if(codeArea.getText().length() == 0)
		{
			console.AddConsoleLine("Error: Code is empty.");
		}
		else
		{
			if(fileName.equals(""))
			{
				fileName = "Untitled.asm";
			}
			
			//Eger henuz ilk single step kullanimi ise
			if(currentStep == 0)
			{
				//Oncelikle syntax analizini tamamlayalim.
				syxAnalysis = new SyntaxAnalysis(codeArea.getText());
				
				//Semantic analize gecelim.
				semAnalysis = new SemanticAnalysis(syxAnalysis.getFragmentationList());
				
				//.be dosyasini olusturabiliriz.
				loSa = Processes.LoadAndSave.get(disk);
				
				objectList = new Vector<Object>();
				objectList.add(new Integer(semAnalysis.getStackSize()));
				objectList.add(semAnalysis.getDataSegmentAddressList());
				objectList.add(semAnalysis.getInstructionList());
				
				instructions = semAnalysis.getInstructionList();
				
				//Eger semantic analiz basarili olduysa
				if(semAnalysis.isSemanticAnalysisSucceeded() == true)
				{		
					try
					{
						loSa.Save(fileName, objectList);
					} 
					catch (IOException e) 
					{
						console.AddConsoleLine("Error: An error has occurred in single step invoking.");
					}
				}
				
				//Runtime analize gecelim.
				runtime = new RuntimeAnalysis(fileName, disk, memory, currentStep);
			}
			
			if(currentStep < instructions.size())
			{
				//Eger semantic analiz basarili olduysa
				if(semAnalysis.isSemanticAnalysisSucceeded() == true)
				{		
					boolean succeed = runtime.Start();
					if(succeed == true)
					{
						new Thread(new Runnable() 
						{
							public void run() 
			                {
								memory.makeListenerUpdated();
			                }   
				        }).start();

						console.AddConsoleLine("Success: " + instructions.get(currentStep).getInstructionString() + " instruction has successfully invoked.");
						currentStep++;
					}
				}
			}
			else
			{
				console.AddConsoleLine("Error: Instructions have been finished.");
			}
		}
	}
	public void StepBack()
	{ 
		if(codeArea.getText().length() == 0)
		{
			console.AddConsoleLine("Error: Code is empty.");
		}
		else
		{
			if(currentStep == 0)
			{
				console.AddConsoleLine("Error: You have not started to invoke any instructions before or no steps remained to get back.");
			}
			else
			{
				//Eger semantic analiz basarili olduysa
				if(semAnalysis.isSemanticAnalysisSucceeded() == true)
				{		
					
				}
			}
		}
	}
	public void Reload()
	{ 
		if(fileName.equals(""))
		{
			fileName = "Untitled.asm";
		}
		
		memory.resetMemory();
		disk.resetDisk();
		for(int i=0; i<Global.flagsCount; i++)
		{
			Global.setFlag(i, false);
		}
		for(int i=0; i<Global.registerList.length; i++)
		{
			Global.SetRegister(i, 0);
		}
			
		Global.currentLine = 1;
		console.ResetConsole();
	}
	public void NewFile()
	{ 
		Global.currentLine = 1;

		codeArea.SetText("");
		
		fileName = "Untitled.asm";
		fileLocation = "";
		
		console.ResetConsole();
		console.AddConsoleLine("New file has opened.");
	}
	public void SaveFile()
	{ 
		//Eger dosya ismi tanimli ise o dosya uzerine islem yapalim.
		if(!fileName.equals("") && !fileLocation.equals(""))
		{
			Writer writer = null;
			try 
			{
				File file = new File(fileLocation);
			    writer = new BufferedWriter(new FileWriter(file));
			    writer.write(codeArea.getText());
			    writer.close();
			} 
			catch (IOException ex) 
			{
				console.AddConsoleLine("The file to save cannot be opened.");
			} 
		}
		//Eger dosya ismi yoksa
		else
		{
			SaveFileAs();
		}
	}
	public void SaveFileAs()
	{ 
		if(codeArea.getText().length() > 0)
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Specify a file to save");    

			int userSelection = fileChooser.showSaveDialog(null);

			if (userSelection == JFileChooser.APPROVE_OPTION) 
			{
				if(fileChooser.getSelectedFile().getName().endsWith(".asm") || fileChooser.getSelectedFile().getName().endsWith(".bsm"))
				{
				    File fileToSave = fileChooser.getSelectedFile();
				    try 
				    {
				    	fileName = fileChooser.getSelectedFile().getName();
				    	fileLocation = fileChooser.getSelectedFile().getAbsolutePath();
				    	File file = new File(fileToSave.getAbsolutePath());
					    BufferedWriter output = new BufferedWriter(new FileWriter(file));
					    output.write(codeArea.getText());
					    output.close();
				    } 
				    catch ( IOException e ) 
				    {
				    	console.AddConsoleLine("The file to save cannot be opened.");
				    }
				}
				else
				{
					console.AddConsoleLine("Error: File name should be ended with .asm or .bsm.");
				}
			}
		}
		else
		{
			console.AddConsoleLine("Error: Code is empty.");
		}
	}
}
