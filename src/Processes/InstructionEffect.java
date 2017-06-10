package Processes;

import java.util.Arrays;
import java.util.Vector;

import Fragments.Instruction;
import Hardware.Memory;
import Root.Global;

public class InstructionEffect 
{
	private Memory memory;
	
	private static Vector<InstructionEffect> instances = new Vector<InstructionEffect>();
	
	private int stackSize = Global.defaultStackSize;
	private int iType1;
	private int iType2;
	private String i1;
	private String i2;
	private int firstOperand, secondOperand, result;
	private boolean isReg = false;
	private int size = 0;
	
    public static InstructionEffect get(Memory mem, int sSize) 
    {
    	InstructionEffect instance = null;
    	
    	if(instances.contains(mem) == false) 
    	{
    		instance = new InstructionEffect(mem, sSize);
    		instances.add(instance);
	    }
	    return instance;
	}
	protected InstructionEffect(Memory mem, int sSize)
	{
		this.memory = mem;
		this.stackSize = sSize;
	}
	public boolean InstructionProcess(Instruction instruction)
	{
		//Sinifsal sifirlamalar
		iType1 = 0;
		iType2 = 0;
		i1 = "";
		i2 = "";
		firstOperand = 0;
		secondOperand = 0;
		result = 0;
		isReg = false;
		size = 0;

		int firstOperandType = instruction.getFirstOperandType();
		int secondOperandType = instruction.getFirstOperandType();
		if(firstOperandType == Global.register || firstOperandType == Global.bytePtrOnlyAddress || firstOperandType == Global.wordPtrOnlyRegister || firstOperandType == Global.dWordPtrOnlyRegister || firstOperandType == Global.qWordPtrOnlyRegister || firstOperandType == Global.onlyRegister)
		{
			for(int i=0; i<Global.registerList.length; i++)
			{
				if(Global.registerList[i].equals(instruction.getFirstOperand()))
				{
					instruction.setFirstOperand(Integer.toString(i));
					break;
				}
			}
		}
		if(secondOperandType == Global.register || secondOperandType == Global.onlyRegister)
		{
			for(int i=0; i<Global.registerList.length; i++)
			{
				if(Global.registerList[i].equals(instruction.getSecondOperand()))
				{
					instruction.setSecondOperand(Integer.toString(i));
					break;
				}
			}
		}
		
		iType1 = instruction.getFirstOperandType();
		iType2 = instruction.getSecondOperandType();
		i1 = instruction.getFirstOperand();
		i2 = instruction.getSecondOperand();
			
		if(instruction.getOpcode().equals("ADC")) return ADC(instruction); else if(instruction.getOpcode().equals("IMUL")) return IMUL(instruction); else if(instruction.getOpcode().equals("JNC")) return JNC(instruction);   
		else if(instruction.getOpcode().equals("JZ")) return JZ(instruction);  else if(instruction.getOpcode().equals("NOT")) return NOT(instruction);  else if(instruction.getOpcode().equals("SBB")) return SBB(instruction);   
		else if(instruction.getOpcode().equals("ADD")) return ADD(instruction);   else if(instruction.getOpcode().equals("INC")) return INC(instruction);   else if(instruction.getOpcode().equals("JNE")) return JNE(instruction);   
		else if(instruction.getOpcode().equals("LEA")) return LEA(instruction);   else if(instruction.getOpcode().equals("OR")) return OR(instruction);   else if(instruction.getOpcode().equals("SCASB")) return SCASB(instruction);   
		else if(instruction.getOpcode().equals("AND")) return AND(instruction);  else if(instruction.getOpcode().equals("JA")) return JA(instruction);   else if(instruction.getOpcode().equals("JNG")) return JNG(instruction);   
		else if(instruction.getOpcode().equals("LODSB")) return LODSB(instruction);   else if(instruction.getOpcode().equals("POP")) return POP(instruction);   else if(instruction.getOpcode().equals("SCASW")) return SCASW(instruction);   
		else if(instruction.getOpcode().equals("CBW")) return CBW(instruction);   else if(instruction.getOpcode().equals("JAE")) return JAE(instruction);   else if(instruction.getOpcode().equals("JNGE")) return JNGE(instruction);   
		else if(instruction.getOpcode().equals("LODSW")) return LODSW(instruction);  else if(instruction.getOpcode().equals("PUSH")) return PUSH(instruction);   else if(instruction.getOpcode().equals("SHL")) return SHL(instruction);   
		else if(instruction.getOpcode().equals("CLC")) return CLC(instruction);   else if(instruction.getOpcode().equals("JB")) return JB(instruction);   else if(instruction.getOpcode().equals("JNL")) return JNL(instruction);   
		else if(instruction.getOpcode().equals("LOOP")) return LOOP(instruction);   else if(instruction.getOpcode().equals("RCL")) return RCL(instruction);   else if(instruction.getOpcode().equals("SHR")) return SHR(instruction);   
		else if(instruction.getOpcode().equals("CLD")) return CLD(instruction);   else if(instruction.getOpcode().equals("JBE")) return JBE(instruction);  else if(instruction.getOpcode().equals("JNLE")) return JNLE(instruction);   
		else if(instruction.getOpcode().equals("LOOPE")) return LOOPE(instruction);   else if(instruction.getOpcode().equals("RCR")) return RCR(instruction);  else if(instruction.getOpcode().equals("STC")) return STC(instruction);   
		else if(instruction.getOpcode().equals("CMC")) return CMC(instruction);   else if(instruction.getOpcode().equals("JC")) return JC(instruction);   else if(instruction.getOpcode().equals("JNO")) return JNO(instruction);   
		else if(instruction.getOpcode().equals("LOOPNE")) return LOOPNE(instruction);   else if(instruction.getOpcode().equals("REP")) return REP(instruction);   else if(instruction.getOpcode().equals("STD")) return STD(instruction);   
		else if(instruction.getOpcode().equals("CMP")) return CMP(instruction);   else if(instruction.getOpcode().equals("JCXZ")) return JCXZ(instruction);   else if(instruction.getOpcode().equals("JNP")) return JNP(instruction);   
		else if(instruction.getOpcode().equals("LOOPNZ")) return LOOPNZ(instruction);   else if(instruction.getOpcode().equals("REPE")) return REPE(instruction);   else if(instruction.getOpcode().equals("STOSB")) return STOSB(instruction);   
		else if(instruction.getOpcode().equals("CMPSB")) return CMPSB(instruction);   else if(instruction.getOpcode().equals("JE")) return JE(instruction);   else if(instruction.getOpcode().equals("JNS")) return JNS(instruction);   
		else if(instruction.getOpcode().equals("LOOPZ")) return LOOPZ(instruction);   else if(instruction.getOpcode().equals("REPNE")) return REPNE(instruction);   else if(instruction.getOpcode().equals("STOSW")) return STOSW(instruction);   
		else if(instruction.getOpcode().equals("CMPSW")) return CMPSW(instruction);   else if(instruction.getOpcode().equals("JG")) return JG(instruction);   else if(instruction.getOpcode().equals("JNZ")) return JNZ(instruction);   
		else if(instruction.getOpcode().equals("MOV")) return MOV(instruction);   else if(instruction.getOpcode().equals("REPNZ")) return REPNZ(instruction);  else if(instruction.getOpcode().equals("SUB")) return SUB(instruction);   
		else if(instruction.getOpcode().equals("CWD")) return CWD(instruction);   else if(instruction.getOpcode().equals("JGE")) return JGE(instruction);   else if(instruction.getOpcode().equals("JO")) return JO(instruction);   
		else if(instruction.getOpcode().equals("MOVSB")) return MOVSB(instruction);  else if(instruction.getOpcode().equals("REPZ")) return REPZ(instruction);   else if(instruction.getOpcode().equals("TEST")) return TEST(instruction);   
		else if(instruction.getOpcode().equals("DEC")) return DEC(instruction);   else if(instruction.getOpcode().equals("JL")) return JL(instruction);   else if(instruction.getOpcode().equals("JP")) return JP(instruction);   
		else if(instruction.getOpcode().equals("MOVSW")) return MOVSW(instruction);   else if(instruction.getOpcode().equals("ROL")) return ROL(instruction);   else if(instruction.getOpcode().equals("XCHG")) return XCHG(instruction);   
		else if(instruction.getOpcode().equals("DIV")) return DIV(instruction);   else if(instruction.getOpcode().equals("JLE")) return JLE(instruction);   else if(instruction.getOpcode().equals("JPE")) return JPE(instruction);   
		else if(instruction.getOpcode().equals("MUL")) return MUL(instruction);   else if(instruction.getOpcode().equals("ROR")) return ROR(instruction);   else if(instruction.getOpcode().equals("XOR")) return XOR(instruction);   
		else if(instruction.getOpcode().equals("HLT")) return HLT(instruction);   else if(instruction.getOpcode().equals("JMP")) return JMP(instruction);   else if(instruction.getOpcode().equals("JPO")) return JPO(instruction);   
		else if(instruction.getOpcode().equals("NEG")) return NEG(instruction);   else if(instruction.getOpcode().equals("SAL")) return SAL(instruction);   else if(instruction.getOpcode().equals("IDIV")) return IDIV(instruction);   
		else if(instruction.getOpcode().equals("JNBE")) return JNBE(instruction);   else if(instruction.getOpcode().equals("JS")) return JS(instruction);   else if(instruction.getOpcode().equals("NOP")) return NOP(instruction);   
		else if(instruction.getOpcode().equals("SAR")) return SAR(instruction);   else if(instruction.getOpcode().equals("RET")) return RET(instruction);   else if(instruction.getOpcode().equals("NAME")) return NAME(instruction);   
		else if(instruction.getOpcode().equals("ORG")) return ORG(instruction);   

		return true;
	}
	
	//Islem tipleri islenenlerine gore belli baslidir.
	//SixTuple -> Genellikle Aritmetik Islemlerdir.
	//FiveTuple -> Genellikle Bitsel Islemlerdir.
	private void SixTuple()
	{
		if(iType1 == Global.register)//Yazmac'tir.
		{
			size = Global.registerSizes[Integer.parseInt(i1)];
			
			if(iType2 == Global.register)//OPCODE Yazmac, Yazmac ise
			{
				firstOperand = Global.GetRegister(Integer.parseInt(i1));
				secondOperand = Global.GetRegister(Integer.parseInt(i2));
			}
			else if(iType2 == Global.onlyRegister)//Yazmac, onlyRegister ise
			{
				firstOperand = Global.GetRegister(Integer.parseInt(i1));
				secondOperand = Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i2)), Global.registerSizes[Integer.parseInt(i2)]));
			}
			else//Decimal number ise
			{
				firstOperand = Global.GetRegister(Integer.parseInt(i1));
				secondOperand = Integer.parseInt(i2);
			}
			isReg = true;
		}
		else//Pointer, Yazmac'tir.
		{
			if(iType1 == Global.bytePtrOnlyRegister) size = 1;
			else if(iType1 == Global.wordPtrOnlyRegister) size = 2;
			else if(iType1 == Global.dWordPtrOnlyRegister) size = 4;
			else if(iType1 == Global.qWordPtrOnlyRegister) size = 8;
			
			firstOperand = Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), size)); 
			secondOperand = Global.GetRegister(Integer.parseInt(i2));
			isReg = false;
		}
	}
	
	/*
	 * Buyruklar
	 */
	
	//ADD with Carry
	//operand1 = operand1 + operand2 + CF 
	private boolean ADC(Instruction instruction)
	{
		SixTuple();
		
		//Algoritma
		int carry = (Global.getFlag(Global.cf)) ? 1 : 0;
		result = firstOperand + secondOperand + carry;
		
		//Eger overflow ve carry varsa
		if(result >= Math.pow(2, size*8))
		{
			Global.setFlag(Global.of, true);
			Global.setFlag(Global.cf, true);
			result = (int)(Math.pow(2, size*8)-1);
		}
		//Eger sonuc negatifse
		if(result < 0) Global.setFlag(Global.sf, true);
		//Eger sonuc sifirsa
		else if(result == 0) Global.setFlag(Global.zf, true);
		//ParityCheck yapalim.
		Global.setFlag(Global.pf, Global.ParityCheck(result));
			
		//Sonuc yazimi
		if(isReg)
			Global.SetRegister(Integer.parseInt(i1), result);
		else
			memory.setMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), Global.IntToBits(result, size));
		return true;
	}

	//Signed Multiply
	//AX = AL * operand
	//(DX AX) = AX * operand
	//CF=OF=0 when result fits into operand of IMUL.
	private boolean IMUL(Instruction instruction)
	{ 
		size = -1;
		if(iType1 == Global.bytePtrOnlyRegister) {size = 1; isReg = false;}
		else if(iType1 == Global.wordPtrOnlyRegister) {size = 2; isReg = false;}
		else if(iType1 == Global.dWordPtrOnlyRegister) {size = 4; isReg = false;}
		else if(iType1 == Global.qWordPtrOnlyRegister) {size = 8; isReg = false;}
		else {size = Global.registerSizes[Integer.parseInt(i1)]; isReg = true;}
	
		if(size == 1)
		{
			if(isReg) Global.SetRegister(Global.ax, Global.GetRegister(Global.al) * Global.GetRegister(Integer.parseInt(i1)));
			else Global.SetRegister(Global.ax, Global.GetRegister(Global.al) * Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Integer.parseInt(i1), size)));
			
			if(Global.GetRegister(Global.ax) > -128 && Global.GetRegister(Global.ax) < 127)//Eger 1 byte'a sigdiysa
			{
				Global.setFlag(Global.cf, false);
				Global.setFlag(Global.of, false);
			}
			else//Sigmadiysa
			{
				Global.setFlag(Global.cf, true);
				Global.setFlag(Global.of, true);
			}
		}
		else
		{
			int tmp;
			if(isReg) tmp = Global.GetRegister(Global.ax) * Global.GetRegister(Integer.parseInt(i1));
			else tmp = Global.GetRegister(Global.ax) * Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Integer.parseInt(i1), size));
			
			if(tmp > -32768 && tmp < 32767)//Eger 2 byte'a sigdiysa
			{
				Global.setFlag(Global.cf, false);
				Global.setFlag(Global.of, false);
				Global.SetRegister(Global.ax, tmp);
			}
			else//Sigmadiysa
			{
				Global.setFlag(Global.cf, true);
				Global.setFlag(Global.of, true);
				Global.SetRegister(Global.ax, Global.BitsToSignedInt(Arrays.copyOfRange(Global.IntToBits(tmp, 4), 0, 16)));
				Global.SetRegister(Global.dx, Global.BitsToSignedInt(Arrays.copyOfRange(Global.IntToBits(tmp, 4), 16, 32)));
			}
		}
		return true; 
	}   
	
	/*JNC*/ 
	//if CF = 0 then jump
	private boolean JNC(Instruction instruction)
	{ 
		if(Global.getFlag(Global.cf) == false)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*JZ*/ 
	private boolean JZ(Instruction instruction)
	{ 
		if(Global.getFlag(Global.zf) == true)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*NOT*/ 
	private boolean NOT(Instruction instruction)
	{ 
		boolean[] bits = null;
		
		//Eger register ise yalnizca icerigi ters cevir.
		if(iType1 == Global.register)
		{
			bits = Global.IntToBits(Global.GetRegister(Integer.parseInt(i1)), Global.registerSizes[Integer.parseInt(i1)]); 
			isReg = true;
		}
		//Eger adres ise
		else
		{
			size = 0;
			if(iType1 == Global.bytePtrOnlyRegister) {size = 1;}
			else if(iType1 == Global.wordPtrOnlyRegister) {size = 2;}
			else if(iType1 == Global.dWordPtrOnlyRegister) {size = 4;}
			else if(iType1 == Global.qWordPtrOnlyRegister) {size = 8;}
			
			bits = memory.getMem(Global.GetRegister(Global.ds) + Integer.parseInt(i1), size);
			isReg = false;
		}
		
		if(bits != null)
		{
			for(int i=0; i<bits.length; i++)
			{
				if(bits[i] == true) bits[i] = false;
				else bits[i] = true;
			}
		}
		
		if(isReg)
		{
			Global.SetRegister(Integer.parseInt(i1), Global.BitsToSignedInt(bits));
		}
		else
		{
			memory.setMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), bits);
		}
		return true; 
	}   
	
	/*SBB*/ 
	//operand1 = operand1 - operand2 - CF 
	private boolean SBB(Instruction instruction)
	{ 
		SixTuple();
		
		//Algoritma
		int carry = (Global.getFlag(Global.cf)) ? 1 : 0;
		result = firstOperand - secondOperand - carry;
		
		//Eger overflow ve carry varsa
		if(result >= Math.pow(2, size*8) || result < Math.pow(2, size*8-1)*(-1))
		{
			Global.setFlag(Global.of, true);
			Global.setFlag(Global.cf, true);
			result = (int)(Math.pow(2, size*8)-1);
		}
		//Eger sonuc negatifse
		if(result < 0) Global.setFlag(Global.sf, true);
		//Eger sonuc sifirsa
		else if(result == 0) Global.setFlag(Global.zf, true);
		//ParityCheck yapalim.
		Global.setFlag(Global.pf, Global.ParityCheck(result));
		
		//Sonuc yazimi
		if(isReg)
			Global.SetRegister(Integer.parseInt(i1), result);
		else
			memory.setMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), Global.IntToBits(result, size));
		return true; 
	}  
	
	/*ADD*/ 
	private boolean ADD(Instruction instruction)
	{ 
		SixTuple();
		
		//Algoritma
		result = firstOperand + secondOperand;
		
		//Eger overflow ve carry varsa
		if(result >= Math.pow(2, size*8))
		{
			Global.setFlag(Global.of, true);
			Global.setFlag(Global.cf, true);
			result = (int)(Math.pow(2, size*8)-1);
		}
		//Eger sonuc negatifse
		if(result < 0) Global.setFlag(Global.sf, true);
		//Eger sonuc sifirsa
		else if(result == 0) Global.setFlag(Global.zf, true);
		//ParityCheck yapalim.
		Global.setFlag(Global.pf, Global.ParityCheck(result));
			
		//Sonuc yazimi
		if(isReg)
			Global.SetRegister(Integer.parseInt(i1), result);
		else
			memory.setMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), Global.IntToBits(result, size));
		return true;
	}   
	
	/*INC*/ 
	private boolean INC(Instruction instruction)
	{
		if(iType1 == Global.register)
		{
			result = Global.GetRegister(Integer.parseInt(i1))+1;
			size = Global.registerSizes[Integer.parseInt(i1)];
			isReg = true;
		}
		//Eger adres ise
		else
		{
			if(iType1 == Global.bytePtrOnlyRegister) {size = 1;}
			else if(iType1 == Global.wordPtrOnlyRegister) {size = 2;}
			else if(iType1 == Global.dWordPtrOnlyRegister) {size = 4;}
			else {size = 8;}
			
			result = Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Integer.parseInt(i1), size))+1;
			
			isReg = false;
		}
		
		//Eger overflow varsa
		if(result >= Math.pow(2, size*8))
		{
			Global.setFlag(Global.of, true);
			result = (int)(Math.pow(2, size*8)-1);
		}
		//Eger sonuc negatifse
		if(result < 0) Global.setFlag(Global.sf, true);
		//Eger sonuc sifirsa
		else if(result == 0) Global.setFlag(Global.zf, true);
		//ParityCheck yapalim.
		Global.setFlag(Global.pf, Global.ParityCheck(result));
		
		//Sonuc yazimi
		if(isReg)
			Global.SetRegister(Integer.parseInt(i1), result);
		else
			memory.setMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), Global.IntToBits(result, size));
		return true; 
	}   
	
	/*JNE*/ 
	private boolean JNE(Instruction instruction)
	{ 
		if(Global.getFlag(Global.zf) == false)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}  
	
	/*LEA*/ 
	private boolean LEA(Instruction instruction)
	{ 
		//Mov'a cevirilmistir. (Mov Offset)
		return true; 
	}   
	
	/*OR*/ 
	private boolean OR(Instruction instruction)
	{ 
		SixTuple();
		
		//Algoritma
		result = firstOperand | secondOperand;
		
		Global.setFlag(Global.of, false);
		Global.setFlag(Global.cf, false);
		
		//Eger sonuc negatifse
		if(result < 0) Global.setFlag(Global.sf, true);
		//Eger sonuc sifirsa
		else if(result == 0) Global.setFlag(Global.zf, true);
		//ParityCheck yapalim.
		Global.setFlag(Global.pf, Global.ParityCheck(result));
			
		//Sonuc yazimi
		if(isReg)
			Global.SetRegister(Integer.parseInt(i1), result);
		else
			memory.setMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), Global.IntToBits(result, size));
		return true;
	}   
	
	/*SCASB*/ 
	private boolean SCASB(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*AND*/ 
	private boolean AND(Instruction instruction)
	{ 
		SixTuple();
		
		//Algoritma
		result = firstOperand & secondOperand;
		
		Global.setFlag(Global.of, false);
		Global.setFlag(Global.cf, false);
		
		//Eger sonuc negatifse
		if(result < 0) Global.setFlag(Global.sf, true);
		//Eger sonuc sifirsa
		else if(result == 0) Global.setFlag(Global.zf, true);
		//ParityCheck yapalim.
		Global.setFlag(Global.pf, Global.ParityCheck(result));
			
		//Sonuc yazimi
		if(isReg)
			Global.SetRegister(Integer.parseInt(i1), result);
		else
			memory.setMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), Global.IntToBits(result, size));
		return true;
	}   
	
	/*JA*/ 
	private boolean JA(Instruction instruction)
	{ 
		if(Global.getFlag(Global.cf) == false && Global.getFlag(Global.zf) == false)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*JNG*/ 
	private boolean JNG(Instruction instruction)
	{ 
		if(Global.getFlag(Global.zf) == true && Global.getFlag(Global.sf) != Global.getFlag(Global.of))
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
		}
		return true; 
	}   
	
	/*LODSB*/ 
	private boolean LODSB(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*POP*/ 
	private boolean POP(Instruction instruction)
	{ 
		if(Global.GetRegister(Global.sp) > 0)
		{
			size = -1;
			if(iType1 == Global.bytePtrOnlyRegister) {size = 1; isReg = false;}
			else if(iType1 == Global.wordPtrOnlyRegister) {size = 2; isReg = false;}
			else if(iType1 == Global.dWordPtrOnlyRegister) {size = 4; isReg = false;}
			else if(iType1 == Global.qWordPtrOnlyRegister) {size = 8; isReg = false;}
			else {size = Global.registerSizes[Integer.parseInt(i1)]; isReg = true;}
			
			if(Global.GetRegister(Global.sp)-size >= 0)
			{
				if(isReg)
				{
					Global.SetRegister(Integer.parseInt(i1), Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ss) + Global.GetRegister(Global.sp) - size, size)));					
				}
				else
				{	
					memory.setMem(Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), size)), memory.getMem(Global.GetRegister(Global.ss) + Global.GetRegister(Global.sp)-size, size));
				}
				Global.SetRegister(Global.sp, Global.GetRegister(Global.sp)-size);
			}
		}
		return true; 
	}   
	
	/*SCASW*/ 
	private boolean SCASW(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*CBW*/ 
	private boolean CBW(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*JAE*/ 
	private boolean JAE(Instruction instruction)
	{ 
		if(Global.getFlag(Global.cf) == false)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*JNGE*/ 
	private boolean JNGE(Instruction instruction)
	{ 
		if(Global.getFlag(Global.sf) != Global.getFlag(Global.of))
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*LODSW*/ 
	private boolean LODSW(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*PUSH*/ 
	private boolean PUSH(Instruction instruction)
	{ 
		size = -1;
		if(iType1 == Global.bytePtrOnlyRegister) {size = 1; isReg = false;}
		else if(iType1 == Global.wordPtrOnlyRegister) {size = 2; isReg = false;}
		else if(iType1 == Global.dWordPtrOnlyRegister) {size = 4; isReg = false;}
		else if(iType1 == Global.qWordPtrOnlyRegister) {size = 8; isReg = false;}
		else {size = Global.registerSizes[Integer.parseInt(i1)]; isReg = true;}
		
		if(Global.GetRegister(Global.sp)+size <= stackSize)
		{
			if(isReg)
			{
				memory.setMem(Global.GetRegister(Global.ss) + Global.GetRegister(Global.sp), Global.IntToBits(Global.GetRegister(Integer.parseInt(i1)), size));
			}
			else
			{	
				memory.setMem(Global.GetRegister(Global.ss) + Global.GetRegister(Global.sp), memory.getMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), size));
			}
			Global.SetRegister(Global.sp, Global.GetRegister(Global.sp)+size);
		}
		return true; 
	}   
	
	/*SHL*/ 
	private boolean SHL(Instruction instruction)
	{ 
		size = -1;
		if(iType1 == Global.bytePtrOnlyRegister) {size = 1; isReg = false;}
		else if(iType1 == Global.wordPtrOnlyRegister) {size = 2; isReg = false;}
		else if(iType1 == Global.dWordPtrOnlyRegister) {size = 4; isReg = false;}
		else if(iType1 == Global.qWordPtrOnlyRegister) {size = 8; isReg = false;}
		else {size = Global.registerSizes[Integer.parseInt(i1)]; isReg = true;}
		
		int value = 0;
		int floatingNo = Global.GetRegister(Integer.parseInt(i2))%(8*size);
		if(isReg)
		{
			value = Global.GetRegister(Integer.parseInt(i1));
		}
		else
		{
			value = Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), size));
		}

		//Eger kaydirma miktari 0'dan buyukse
		if(floatingNo > 0)
		{
			boolean isNegative = false;
			//Eger yazmacin degistirilmeden onceki degeri negatif ise
			if(value < 0)
			{
				//Bunu not edelim.
				isNegative = true;
			}
			
			boolean[] bits = Global.IntToBits(value, size); 
			
			//Eger son bit 1 ise
			if(bits[floatingNo-1]==true)
			{
				Global.setFlag(Global.cf, true);
			}
			else
			{
				Global.setFlag(Global.cf, false);
			}
			
			int number = value << floatingNo;
			bits = Global.IntToBits(number, size); 

			if(isReg)
			{
				Global.SetRegister(Integer.parseInt(i1), Global.BitsToSignedInt(bits));
			}
			else
			{
				memory.setMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), bits);
			}
			
			//Yazmacin degistirildikten sonraki degeri tersine donduyse overflow flag'i set edelim.
			if((Global.BitsToSignedInt(bits) < 0 && isNegative == false) || (Global.BitsToSignedInt(bits) > 0 && isNegative == true))
			{
				Global.setFlag(Global.of, true);
			}
			else
			{
				Global.setFlag(Global.of, false);
			}
		}
		return true; 
	}   
	
	/*CLC*/ 
	private boolean CLC(Instruction instruction)
	{ 
		Global.setFlag(Global.cf, false);
		return true; 
	}   
	
	/*JB*/ 
	private boolean JB(Instruction instruction)
	{ 
		if(Global.getFlag(Global.cf) == true)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*JNL*/ 
	private boolean JNL(Instruction instruction)
	{ 
		if(Global.getFlag(Global.sf) == Global.getFlag(Global.of))
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}  
	
	/*LOOP*/ 
	private boolean LOOP(Instruction instruction)
	{ 
		//Once cx'i 1 azaltir. (Dec)
		//Sonra jnz yapilir.
		//Flag'ler set edilmez.
		int tmp = Global.GetRegister(Global.cx);//Cx'i getir.
		tmp--;//1 azalttik.
		Global.SetRegister(Global.cx, tmp);//Yeni degeri set edelim.
		
		if(tmp != 0)//tmp 0'dan farkliysa atla
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		//Eger 0'sa normal devam et.
		return true; 
	}  
	
	/*RCL*/ 
	private boolean RCL(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*SHR*/ 
	private boolean SHR(Instruction instruction)
	{ 
		size = -1;
		if(iType1 == Global.bytePtrOnlyRegister) {size = 1; isReg = false;}
		else if(iType1 == Global.wordPtrOnlyRegister) {size = 2; isReg = false;}
		else if(iType1 == Global.dWordPtrOnlyRegister) {size = 4; isReg = false;}
		else if(iType1 == Global.qWordPtrOnlyRegister) {size = 8; isReg = false;}
		else {size = Global.registerSizes[Integer.parseInt(i1)]; isReg = true;}
		
		int value = 0;
		int floatingNo = Global.GetRegister(Integer.parseInt(i2))%(8*size);
		if(isReg)
		{
			value = Global.GetRegister(Integer.parseInt(i1));
		}
		else
		{
			value = Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), size));
		}

		//Eger kaydirma miktari 0'dan buyukse
		if(floatingNo > 0)
		{
			boolean isNegative = false;
			//Eger yazmacin degistirilmeden onceki degeri negatif ise
			if(value < 0)
			{
				//Bunu not edelim.
				isNegative = true;
			}
			
			boolean[] bits = Global.IntToBits(value, size); 
			
			//Eger ilk bit 1 ise
			if(bits[bits.length-floatingNo]==true)
			{
				Global.setFlag(Global.cf, true);
			}
			else
			{
				Global.setFlag(Global.cf, false);
			}
			
			int number = value >>> floatingNo;
			bits = Global.IntToBits(number, size); 

			if(isReg)
			{
				Global.SetRegister(Integer.parseInt(i1), Global.BitsToSignedInt(bits));
			}
			else
			{
				memory.setMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), bits);
			}
			
			//Yazmacin degistirildikten sonraki degeri tersine donduyse overflow flag'i set edelim.
			if((Global.BitsToSignedInt(bits) < 0 && isNegative == false) || (Global.BitsToSignedInt(bits) > 0 && isNegative == true))
			{
				Global.setFlag(Global.of, true);
			}
			else
			{
				Global.setFlag(Global.of, false);
			}
		}
		return true; 
	}    
	
	/*CLD*/ 
	private boolean CLD(Instruction instruction)
	{ 
		Global.setFlag(Global.df, false);
		return true; 
	}   
	
	/*JBE*/ 
	private boolean JBE(Instruction instruction)
	{ 
		if(Global.getFlag(Global.cf) == true || Global.getFlag(Global.zf) == true)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*JNLE*/ 
	private boolean JNLE(Instruction instruction)
	{ 
		if(Global.getFlag(Global.sf) == Global.getFlag(Global.of) && Global.getFlag(Global.zf) == false)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*LOOPE*/ 
	private boolean LOOPE(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*RCR*/ 
	private boolean RCR(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*STC*/ 
	private boolean STC(Instruction instruction)
	{ 
		Global.setFlag(Global.cf, true);
		return true; 
	}  
	
	/*CMC*/ 
	private boolean CMC(Instruction instruction)
	{ 
		return true; 
	}  
	
	/*JC*/ 
	private boolean JC(Instruction instruction)
	{ 
		if(Global.getFlag(Global.cf) == true)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}  
	
	/*JNO*/ 
	private boolean JNO(Instruction instruction)
	{ 
		if(Global.getFlag(Global.of) == false)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*LOOPNE*/ 
	private boolean LOOPNE(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*REP*/ 
	private boolean REP(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*STD*/ 
	private boolean STD(Instruction instruction)
	{ 
		Global.setFlag(Global.df, true);
		return true; 
	}  
	
	/*CMP*/ 
	private boolean CMP(Instruction instruction)
	{ 
		SixTuple();
		
		//Algoritma
		result = firstOperand - secondOperand;
		
		//Eger overflow ve carry varsa
		if(result >= Math.pow(2, size*8) || result < Math.pow(2, size*8-1)*(-1))
		{
			Global.setFlag(Global.of, true);
			Global.setFlag(Global.cf, true);
			result = (int)(Math.pow(2, size*8)-1);
		}
		//Eger sonuc negatifse
		if(result < 0) Global.setFlag(Global.sf, true);
		//Eger sonuc sifirsa
		else if(result == 0) Global.setFlag(Global.zf, true);
		//ParityCheck yapalim.
		Global.setFlag(Global.pf, Global.ParityCheck(result));
		
		result = 0;
		return true; 
	}   
	
	/*JCXZ*/ 
	private boolean JCXZ(Instruction instruction)
	{ 
		if(Global.GetRegister(Global.cx) == 0)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*JNP*/ 
	private boolean JNP(Instruction instruction)
	{ 
		if(Global.getFlag(Global.pf) == false)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*LOOPNZ*/ 
	private boolean LOOPNZ(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*REPE*/ 
	private boolean REPE(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*STOSB*/ 
	private boolean STOSB(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*CMPSB*/ 
	private boolean CMPSB(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*JE*/ 
	private boolean JE(Instruction instruction)
	{ 
		if(Global.getFlag(Global.zf) == true)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*JNS*/ 
	private boolean JNS(Instruction instruction)
	{ 
		if(Global.getFlag(Global.sf) == false)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*LOOPZ*/ 
	private boolean LOOPZ(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*REPNE*/ 
	private boolean REPNE(Instruction instruction)
	{ 
		return true; 
	}  
	
	/*STOSW*/
	private boolean STOSW(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*CMPSW*/
	private boolean CMPSW(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*JG*/
	private boolean JG(Instruction instruction)
	{ 
		if(Global.getFlag(Global.sf) == Global.getFlag(Global.of) && Global.getFlag(Global.zf) == false)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}  
	
	/*JNZ*/
	private boolean JNZ(Instruction instruction)
	{ 
		if(Global.getFlag(Global.zf) == false)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*MOV*/ 
	private boolean MOV(Instruction instruction)
	{ 
		//Eger ilk operand yazmac ise
		if(iType1 == Global.register)
		{
			if(iType2 == Global.register)
			{
				Global.SetRegister(Integer.parseInt(i1), Global.GetRegister(Integer.parseInt(i2)));
			}
			else if(iType2 == Global.decimalNumber)
			{
				Global.SetRegister(Integer.parseInt(i1), Integer.parseInt(i2));
			}
			else if(iType2 == Global.offset)
			{
				Global.SetRegister(Integer.parseInt(i1), Integer.parseInt(i2));
			}
			else if(iType2 == Global.segmentInitializion)
			{
				//Zaten en basta ataniyor.
			}
			else
			{
				int memLocation = Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i2));
				int memValue = Global.BitsToSignedInt(memory.getMem(memLocation, Global.registerSizes[Integer.parseInt(i1)]));
				Global.SetRegister(Integer.parseInt(i1), memValue);
			}
		}
		else
		{
			size = 0;
			boolean isPtr = false;
			if(iType1 == Global.bytePtrOnlyRegister) {size = 1; isPtr = true;}
			else if(iType1 == Global.wordPtrOnlyRegister) {size = 2; isPtr = true;}
			else if(iType1 == Global.dWordPtrOnlyRegister) {size = 4; isPtr = true;}
			else if(iType1 == Global.qWordPtrOnlyRegister) {size = 8; isPtr = true;}
			
			if(isPtr)
			{
				int found1 = -1;
				int found2 = -1;
				for(int i=0; i<Global.registerList.length; i++)
				{
					if(Global.registerList[i].equals(i1))
					{
						found1 = i;
					}
					if(Global.registerList[i].equals(i2))
					{
						found2 = i;
					}
					
					if(found1 != -1 && found2 != -1)
					{
						break;
					}
				}
				if(found1 != -1 && found2 != -1)
				{
					memory.setMem(Global.GetRegister(Global.ds) + Global.GetRegister(found1), Global.IntToBits(Global.GetRegister(found2), size));
				}
			}
			else
			{
				memory.setMem(Global.GetRegister(Global.ds) + Integer.parseInt(i1), Global.IntToBits(Integer.parseInt(i2), size));
			}
		}
		return true; 
	}   
	
	/*REPNZ*/ 
	private boolean REPNZ(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*SUB*/ 
	//operand1 = operand1 - operand2
	private boolean SUB(Instruction instruction)
	{ 
		SixTuple();
		
		//Algoritma
		result = firstOperand - secondOperand;
		
		//Eger overflow ve carry varsa
		if(result >= Math.pow(2, size*8) || result < Math.pow(2, size*8-1)*(-1))
		{
			Global.setFlag(Global.of, true);
			Global.setFlag(Global.cf, true);
			result = (int)(Math.pow(2, size*8)-1);
		}
		//Eger sonuc negatifse
		if(result < 0) Global.setFlag(Global.sf, true);
		//Eger sonuc sifirsa
		else if(result == 0) Global.setFlag(Global.zf, true);
		//ParityCheck yapalim.
		Global.setFlag(Global.pf, Global.ParityCheck(result));
		
		//Sonuc yazimi
		if(isReg)
			Global.SetRegister(Integer.parseInt(i1), result);
		else
			memory.setMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), Global.IntToBits(result, size));
		return true; 
	}   
	
	/*CWD*/
	private boolean CWD(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*JGE*/
	private boolean JGE(Instruction instruction)
	{ 
		if(Global.getFlag(Global.sf) == Global.getFlag(Global.of))
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*JO*/ 
	private boolean JO(Instruction instruction)
	{ 
		if(Global.getFlag(Global.of) == true)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*MOVSB*/
	private boolean MOVSB(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*REPZ*/ 
	private boolean REPZ(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*TEST*/
	private boolean TEST(Instruction instruction)
	{ 
		return true; 
	}  
	
	/*DEC*/
	private boolean DEC(Instruction instruction)
	{ 
		if(iType1 == Global.register)
		{
			result = Global.GetRegister(Integer.parseInt(i1))-1;
			isReg = true;
		}
		//Eger adres ise
		else
		{
			size = 0;
			if(iType1 == Global.bytePtrOnlyRegister) {size = 1;}
			else if(iType1 == Global.wordPtrOnlyRegister) {size = 2;}
			else if(iType1 == Global.dWordPtrOnlyRegister) {size = 4;}
			else if(iType1 == Global.qWordPtrOnlyRegister) {size = 8;}
			
			result = Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Integer.parseInt(i1), size))-1;
			
			isReg = false;
		}
		
		//Eger overflow varsa
		if(result < 0)
		{
			if(result*(-1) >= Math.pow(2, size*8))
			{
				Global.setFlag(Global.of, true);
				result = (int)(Math.pow(2, size*8)+1);
			}
		}
		
		//Eger sonuc negatifse
		if(result < 0) Global.setFlag(Global.sf, true);
		//Eger sonuc sifirsa
		else if(result == 0) Global.setFlag(Global.zf, true);
		//ParityCheck yapalim.
		Global.setFlag(Global.pf, Global.ParityCheck(result));
		
		//Sonuc yazimi
		if(isReg)
			Global.SetRegister(Integer.parseInt(i1), result);
		else
			memory.setMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), Global.IntToBits(result, size));
		return true; 
	}   
	
	/*JL*/ 
	private boolean JL(Instruction instruction)
	{ 
		if(Global.getFlag(Global.sf) != Global.getFlag(Global.of))
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*JP*/ 
	private boolean JP(Instruction instruction)
	{ 
		if(Global.getFlag(Global.pf) == true)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*MOVSW*/ 
	private boolean MOVSW(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*ROL*/ 
	private boolean ROL(Instruction instruction)
	{ 
		size = -1;
		if(iType1 == Global.bytePtrOnlyRegister) {size = 1; isReg = false;}
		else if(iType1 == Global.wordPtrOnlyRegister) {size = 2; isReg = false;}
		else if(iType1 == Global.dWordPtrOnlyRegister) {size = 4; isReg = false;}
		else if(iType1 == Global.qWordPtrOnlyRegister) {size = 8; isReg = false;}
		else {size = Global.registerSizes[Integer.parseInt(i1)]; isReg = true;}
		
		int value = 0;
		int floatingNo = Global.GetRegister(Integer.parseInt(i2))%(8*size);
		if(isReg)
		{
			value = Global.GetRegister(Integer.parseInt(i1));
		}
		else
		{
			value = Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), size));
		}

		//Eger kaydirma miktari 0'dan buyukse
		if(floatingNo > 0)
		{
			boolean isNegative = false;
			//Eger yazmacin degistirilmeden onceki degeri negatif ise
			if(value < 0)
			{
				//Bunu not edelim.
				isNegative = true;
			}
			
			boolean[] bits = Global.IntToBits(value, size); 
			
			//Eger son bit 1 ise
			if(bits[floatingNo-1]==true)
			{
				Global.setFlag(Global.cf, true);
			}
			else
			{
				Global.setFlag(Global.cf, false);
			}
			
			int number = (value << floatingNo) | (value >> (8*size - floatingNo));
			bits = Global.IntToBits(number, size); 

			if(isReg)
			{
				Global.SetRegister(Integer.parseInt(i1), Global.BitsToSignedInt(bits));
			}
			else
			{
				memory.setMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), bits);
			}
			
			//Yazmacin degistirildikten sonraki degeri tersine donduyse overflow flag'i set edelim.
			if((Global.BitsToSignedInt(bits) < 0 && isNegative == false) || (Global.BitsToSignedInt(bits) > 0 && isNegative == true))
			{
				Global.setFlag(Global.of, true);
			}
			else
			{
				Global.setFlag(Global.of, false);
			}
		}
		return true; 
	}   
	
	/*XCHG*/
	private boolean XCHG(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*DIV*/
	private boolean DIV(Instruction instruction)
	{ 
		size = -1;
		if(iType1 == Global.bytePtrOnlyRegister) {size = 1; isReg = false;}
		else if(iType1 == Global.wordPtrOnlyRegister) {size = 2; isReg = false;}
		else if(iType1 == Global.dWordPtrOnlyRegister) {size = 4; isReg = false;}
		else if(iType1 == Global.qWordPtrOnlyRegister) {size = 8; isReg = false;}
		else {size = Global.registerSizes[Integer.parseInt(i1)]; isReg = true;}
	
		if(size == 1)
		{
			if(isReg)
			{
				Global.SetRegister(Global.ax, Global.GetRegister(Global.ax) / Global.GetRegister(Integer.parseInt(i1)));
				Global.SetRegister(Global.ah, Global.GetRegister(Global.ax) % Global.GetRegister(Integer.parseInt(i1)));
			}
			else
			{
				Global.SetRegister(Global.ax, Global.GetRegister(Global.ax) / Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Integer.parseInt(i1), size)));
				Global.SetRegister(Global.ah, Global.GetRegister(Global.ax) % Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Integer.parseInt(i1), size)));
			}
			
			if(Global.GetRegister(Global.ax) > -128 && Global.GetRegister(Global.ax) < 127)//Eger 1 byte'a sigdiysa
			{
				Global.setFlag(Global.cf, false);
				Global.setFlag(Global.of, false);
			}
			else//Sigmadiysa
			{
				Global.setFlag(Global.cf, true);
				Global.setFlag(Global.of, true);
			}
		}
		else
		{
			int tmp = (Global.GetRegister(Global.dx) << 16) + Global.GetRegister(Global.ax);
			if(isReg) 
			{
				tmp = tmp / Global.GetRegister(Integer.parseInt(i1));
				Global.SetRegister(Global.dx, tmp % Global.GetRegister(Integer.parseInt(i1)));
			}
			else 
			{
				tmp = Global.GetRegister(Global.ax) / Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Integer.parseInt(i1), size));
				Global.SetRegister(Global.dx, tmp % Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Integer.parseInt(i1), size)));
			}
			
			if(tmp > -32768 && tmp < 32767)//Eger 2 byte'a sigdiysa
			{
				Global.setFlag(Global.cf, false);
				Global.setFlag(Global.of, false);
				Global.SetRegister(Global.ax, tmp);
			}
			else//Sigmadiysa
			{
				Global.setFlag(Global.cf, true);
				Global.setFlag(Global.of, true);
				Global.SetRegister(Global.ax, Global.BitsToSignedInt(Arrays.copyOfRange(Global.IntToBits(tmp, 4), 0, 16)));
			}
		}
		return true; 
	}   
	
	/*JLE*/
	private boolean JLE(Instruction instruction)
	{ 
		if(Global.getFlag(Global.sf) != Global.getFlag(Global.of) && Global.getFlag(Global.zf) == true)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*JPE*/ 
	private boolean JPE(Instruction instruction)
	{ 
		if(Global.getFlag(Global.pf) == true)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*MUL*/ 
	private boolean MUL(Instruction instruction)
	{ 
		size = -1;
		if(iType1 == Global.bytePtrOnlyRegister) {size = 1; isReg = false;}
		else if(iType1 == Global.wordPtrOnlyRegister) {size = 2; isReg = false;}
		else if(iType1 == Global.dWordPtrOnlyRegister) {size = 4; isReg = false;}
		else if(iType1 == Global.qWordPtrOnlyRegister) {size = 8; isReg = false;}
		else {size = Global.registerSizes[Integer.parseInt(i1)]; isReg = true;}
	
		if(size == 1)
		{
			if(isReg) Global.SetRegister(Global.ax, Global.GetRegister(Global.al) * Global.GetRegister(Integer.parseInt(i1)));
			else Global.SetRegister(Global.ax, Global.GetRegister(Global.al) * Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Integer.parseInt(i1), size)));
			
			if(Global.GetRegister(Global.ax) > -128 && Global.GetRegister(Global.ax) < 127)//Eger 1 byte'a sigdiysa
			{
				Global.setFlag(Global.cf, false);
				Global.setFlag(Global.of, false);
			}
			else//Sigmadiysa
			{
				Global.setFlag(Global.cf, true);
				Global.setFlag(Global.of, true);
			}
		}
		else
		{
			int tmp;
			if(isReg) tmp = Global.GetRegister(Global.ax) * Global.GetRegister(Integer.parseInt(i1));
			else tmp = Global.GetRegister(Global.ax) * Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Integer.parseInt(i1), size));
			
			if(tmp > -32768 && tmp < 32767)//Eger 2 byte'a sigdiysa
			{
				Global.setFlag(Global.cf, false);
				Global.setFlag(Global.of, false);
				Global.SetRegister(Global.ax, tmp);
			}
			else//Sigmadiysa
			{
				Global.setFlag(Global.cf, true);
				Global.setFlag(Global.of, true);
				Global.SetRegister(Global.ax, Global.BitsToSignedInt(Arrays.copyOfRange(Global.IntToBits(tmp, 4), 0, 16)));
				Global.SetRegister(Global.dx, Global.BitsToSignedInt(Arrays.copyOfRange(Global.IntToBits(tmp, 4), 16, 32)));
			}
		}
		return true; 
	}   
	
	/*ROR*/
	private boolean ROR(Instruction instruction)
	{ 
		size = -1;
		if(iType1 == Global.bytePtrOnlyRegister) {size = 1; isReg = false;}
		else if(iType1 == Global.wordPtrOnlyRegister) {size = 2; isReg = false;}
		else if(iType1 == Global.dWordPtrOnlyRegister) {size = 4; isReg = false;}
		else if(iType1 == Global.qWordPtrOnlyRegister) {size = 8; isReg = false;}
		else {size = Global.registerSizes[Integer.parseInt(i1)]; isReg = true;}
		
		int value = 0;
		int floatingNo = Global.GetRegister(Integer.parseInt(i2))%(8*size);
		if(isReg)
		{
			value = Global.GetRegister(Integer.parseInt(i1));
		}
		else
		{
			value = Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), size));
		}

		//Eger kaydirma miktari 0'dan buyukse
		if(floatingNo > 0)
		{
			boolean isNegative = false;
			//Eger yazmacin degistirilmeden onceki degeri negatif ise
			if(value < 0)
			{
				//Bunu not edelim.
				isNegative = true;
			}
			
			boolean[] bits = Global.IntToBits(value, size); 
			
			//Eger ilk bit 1 ise
			if(bits[bits.length-floatingNo]==true)
			{
				Global.setFlag(Global.cf, true);
			}
			else
			{
				Global.setFlag(Global.cf, false);
			}
			
			int number = (value >>> floatingNo) | (value << (size*8 - floatingNo));
			bits = Global.IntToBits(number, size); 

			if(isReg)
			{
				Global.SetRegister(Integer.parseInt(i1), Global.BitsToSignedInt(bits));
			}
			else
			{
				memory.setMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), bits);
			}
			
			//Yazmacin degistirildikten sonraki degeri tersine donduyse overflow flag'i set edelim.
			if((Global.BitsToSignedInt(bits) < 0 && isNegative == false) || (Global.BitsToSignedInt(bits) > 0 && isNegative == true))
			{
				Global.setFlag(Global.of, true);
			}
			else
			{
				Global.setFlag(Global.of, false);
			}
		}
		return true; 
	}   
	
	/*XOR*/ 
	private boolean XOR(Instruction instruction)
	{ 
		SixTuple();
		
		//Algoritma
		result = firstOperand ^ secondOperand;
		
		Global.setFlag(Global.of, false);
		Global.setFlag(Global.cf, false);
		
		//Eger sonuc negatifse
		if(result < 0) Global.setFlag(Global.sf, true);
		//Eger sonuc sifirsa
		else if(result == 0) Global.setFlag(Global.zf, true);
		//ParityCheck yapalim.
		Global.setFlag(Global.pf, Global.ParityCheck(result));
			
		//Sonuc yazimi
		if(isReg)
			Global.SetRegister(Integer.parseInt(i1), result);
		else
			memory.setMem(Global.GetRegister(Global.ds) + Global.GetRegister(Integer.parseInt(i1)), Global.IntToBits(result, size));
		return true;
	}   
	/*HLT*/ 
	private boolean HLT(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*JMP*/ 
	private boolean JMP(Instruction instruction)
	{ 
		Global.SetRegister(Global.ip, Integer.parseInt(i1));
		return false;
	}   
	
	/*JPO*/ 
	private boolean JPO(Instruction instruction)
	{ 
		if(Global.getFlag(Global.pf) == false)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*NEG*/ 
	private boolean NEG(Instruction instruction)
	{ 
		//NOT ve ADD'e donusturuldu.
		return true; 
	}   
	
	/*SAL*/
	private boolean SAL(Instruction instruction)
	{ 
		return true; 
	}  
	
	/*IDIV*/ 
	private boolean IDIV(Instruction instruction)
	{ 
		size = -1;
		if(iType1 == Global.bytePtrOnlyRegister) {size = 1; isReg = false;}
		else if(iType1 == Global.wordPtrOnlyRegister) {size = 2; isReg = false;}
		else if(iType1 == Global.dWordPtrOnlyRegister) {size = 4; isReg = false;}
		else if(iType1 == Global.qWordPtrOnlyRegister) {size = 8; isReg = false;}
		else {size = Global.registerSizes[Integer.parseInt(i1)]; isReg = true;}
	
		if(size == 1)
		{
			if(isReg)
			{
				Global.SetRegister(Global.ax, Global.GetRegister(Global.ax) / Global.GetRegister(Integer.parseInt(i1)));
				Global.SetRegister(Global.ah, Global.GetRegister(Global.ax) % Global.GetRegister(Integer.parseInt(i1)));
			}
			else
			{
				Global.SetRegister(Global.ax, Global.GetRegister(Global.ax) / Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Integer.parseInt(i1), size)));
				Global.SetRegister(Global.ah, Global.GetRegister(Global.ax) % Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Integer.parseInt(i1), size)));
			}
			
			if(Global.GetRegister(Global.ax) > -128 && Global.GetRegister(Global.ax) < 127)//Eger 1 byte'a sigdiysa
			{
				Global.setFlag(Global.cf, false);
				Global.setFlag(Global.of, false);
			}
			else//Sigmadiysa
			{
				Global.setFlag(Global.cf, true);
				Global.setFlag(Global.of, true);
			}
		}
		else
		{
			int tmp = (Global.GetRegister(Global.dx) << 16) + Global.GetRegister(Global.ax);
			if(isReg) 
			{
				tmp = tmp / Global.GetRegister(Integer.parseInt(i1));
				Global.SetRegister(Global.dx, tmp % Global.GetRegister(Integer.parseInt(i1)));
			}
			else 
			{
				tmp = Global.GetRegister(Global.ax) / Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Integer.parseInt(i1), size));
				Global.SetRegister(Global.dx, tmp % Global.BitsToSignedInt(memory.getMem(Global.GetRegister(Global.ds) + Integer.parseInt(i1), size)));
			}
			
			if(tmp > -32768 && tmp < 32767)//Eger 2 byte'a sigdiysa
			{
				Global.setFlag(Global.cf, false);
				Global.setFlag(Global.of, false);
				Global.SetRegister(Global.ax, tmp);
			}
			else//Sigmadiysa
			{
				Global.setFlag(Global.cf, true);
				Global.setFlag(Global.of, true);
				Global.SetRegister(Global.ax, Global.BitsToSignedInt(Arrays.copyOfRange(Global.IntToBits(tmp, 4), 0, 16)));
			}
		}
		return true; 
	}   
	
	/*JNBE*/ 
	private boolean JNBE(Instruction instruction)
	{ 	
		if(Global.getFlag(Global.cf) == false && Global.getFlag(Global.zf) == false)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	} 
	
	/*JS*/ 
	private boolean JS(Instruction instruction)
	{ 
		if(Global.getFlag(Global.sf) == true)
		{
			Global.SetRegister(Global.ip, Integer.parseInt(i1));
			return false;
		}
		return true; 
	}   
	
	/*NOP*/
	private boolean NOP(Instruction instruction)
	{ 
		for(int i=0; i<3; i++)
		{
			//Hic bir sey yapma.
		}
		return true; 
	}   
	
	/*SAR*/ 
	private boolean SAR(Instruction instruction)
	{ 
		return true; 
	}  
	
	/*RET*/ 
	private boolean RET(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*NAME*/ 
	private boolean NAME(Instruction instruction)
	{ 
		return true; 
	}   
	
	/*ORG*/ 
	private boolean ORG(Instruction instruction)
	{ 
		return true; 
	}   
}
