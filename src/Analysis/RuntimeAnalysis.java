package Analysis;

import java.io.IOException;
import java.util.Vector;

import Errors.CurrentErrors;
import Errors.ErrorList;
import Fragments.VarFactor;
import Fragments.Instruction;
import Hardware.*;
import Processes.InstructionEffect;
import Processes.LoadAndSave;
import Root.Global;
import Root.SupportedInstructions;

public class RuntimeAnalysis 
{
	private boolean runtimeAnalysisSucceeded;
	
	private LoadAndSave loadSave = null;
	private Memory memory = null;
	private String fileName;
	
	private ErrorList eList = ErrorList.get();
	private CurrentErrors errors = CurrentErrors.get();
	
	private int singleStep = -1;

	private Vector<Instruction> instructionList = null;
	private int stackSize = Global.defaultStackSize;
	private InstructionEffect iEffect = null;
	
	public RuntimeAnalysis(String fileName, HardDisk hdd, Memory memory, int sStep)
	{
		this.loadSave = LoadAndSave.get(hdd);
		this.fileName = fileName;
		this.memory = memory;
		this.singleStep = sStep;
		
		this.runtimeAnalysisSucceeded = Start();
	}
	
	@SuppressWarnings("unchecked")
	public boolean Start()
	{
		if(loadSave != null)
		{
			Vector<Object> objList = null;
			try 
			{
				//Dosyayi sanal tekerden getir.
				objList = loadSave.Load(fileName);
			} 
			catch (IOException e) 
			{
				errors.addError(eList.GetError(eList.WRONG_FILE_CONTENT));//Kuyruga yeni hatayi at.
				return false;
			}
			
			//Eger herhangi bir dosya problemi olusmadiysa
			if(objList != null)
			{
				if(singleStep == -1 || singleStep == 0)
				{
					//3 nesneyi ayri parcalara bolelim.
					//Eger 3 parca yoksa
					if(objList.size() != 3)
					{
						errors.addError(eList.GetError(eList.WRONG_FILE_CONTENT));//Kuyruga yeni hatayi at.
						return false;
					}

					//Eger 3 parca bulunduysa
					stackSize = (Integer)objList.get(0);
					Vector<VarFactor> dataList = (Vector<VarFactor>)objList.get(1);
					
					instructionList = (Vector<Instruction>)objList.get(2);
					
					//Eger hlt ya da ret ile bitirilmediyse bitirelim.
					if(singleStep == -1)
					{
						if(!instructionList.lastElement().getOpcode().equals("HLT") && !instructionList.lastElement().getOpcode().equals("RET"))
						{
							Instruction newInstruction = new Instruction("HLT", "HLT");
							newInstruction.setInstructionAddress(instructionList.lastElement().getInstructionAddress()+2);
							instructionList.add(newInstruction);
						}
					}
										
					Global.SetRegister(Global.ss, OperatingSystem.lowerLimitForCurrentProgram);
					Global.SetRegister(Global.ds, OperatingSystem.lowerLimitForCurrentProgram + stackSize);
					Global.SetRegister(Global.cs, OperatingSystem.lowerLimitForCurrentProgram + stackSize + dataList.size()*4);//4 katina kadar tanimlanmis olabilir, dd yada dw kullanimlari.
					Global.SetRegister(Global.es, OperatingSystem.lowerLimitForCurrentProgram + stackSize + dataList.size()*4 + instructionList.size()*2);//4 katina kadar tanimlanmis olabilir, dd yada dw kullanimlari. Ayrica her buyruk 2 byte.

					//Memory'e atilacak tum fragment'lar dataList'te tutuluyor.
					for(int i=0; i<dataList.size(); i++)
					{
						memory.setMem(Global.GetRegister(Global.ds) + dataList.get(i).getAddress(), dataList.get(i).getContent());
					}
					//Simdi de instructionList'teki tum buyruklari memory'e atalim.
					for(int i=0; i<instructionList.size(); i++)
					{				
						memory.setMem(Global.GetRegister(Global.cs) + instructionList.get(i).getInstructionAddress(), Global.IntToBits(i, 2));
					}
				
					//Artik buyruklari islemeye baslayabiliriz.
					Global.SetRegister(Global.ip, 0);//Instruction pointer'i sifirlayalim.
				}
				boolean onlyOnePass = false;
				while(!onlyOnePass)
				{
					int index = Global.BitsToUnsignedInt(memory.getMem(Global.GetRegister(Global.cs) + Global.GetRegister(Global.ip), 2));
					//Eger memory'den icerik hatali geldiyse ya da hlt ile code segment tamamlandiysa
					if(index < 0 || index >= instructionList.size() || instructionList.get(index).getOpcode().equals("HLT")) break;
					if(instructionList.get(index).getOpcode().equals("HLT")) break;
					if(instructionList.get(index).getOpcode().equals("RET")) break;
					
					//Eger buyruk operand sayisi tutmadiysa
					if(SupportedInstructions.get().InstructionOperandCount(instructionList.get(index).getOpcode()) != instructionList.get(index).getOperandCount())
					{
						errors.addError(eList.GetError(eList.INSTRUCTION_OPERAND_NUMBER_IS_WRONG));//Kuyruga yeni hatayi at.
						return false;
					}
					
					iEffect = InstructionEffect.get(memory, stackSize);
					boolean jumpNormally = iEffect.InstructionProcess((instructionList.get(index)));
					
					//Eger IP'a yonelik bir degisiklik yapilmadiysa jump gibi, 2 byte artir.
					if(jumpNormally)
					{
						//IP yazmaci sonraki instruction'i tutar.
						Global.SetRegister(Global.ip, Global.GetRegister(Global.ip) + 2);
					}
					
					if(singleStep != -1)
					{
						onlyOnePass = true;
					}
				}
			}
		}
		return true;
	}
	
	public boolean isRuntimeAnalysisSucceeded()
	{
		return runtimeAnalysisSucceeded;
	}
}
