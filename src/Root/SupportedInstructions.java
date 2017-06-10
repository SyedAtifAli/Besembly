package Root;
import java.util.Arrays;
import java.util.Vector;

public class SupportedInstructions 
{
	private static SupportedInstructions instance = null;
	
    public static SupportedInstructions get() 
    {
    	if(instance == null) 
    	{
    		instance = new SupportedInstructions();
	    }
	    return instance;
	}
    
	//maxLengthOfAnInstruction ve minLengthOfAnInstruction tanimli olan buyruklarin opcode'unun en az ve en fazla
	//Kacar karaktere sahip olabileceklerini ifade eder.
	public static final int maxLengthOfAnOpcode = 6;
	public static final int minLengthOfAnOpcode = 2;
	
	private static Vector<String> instructions;
	private static Vector<InstructionSyntax> insyntaxes;
	private static final String[] list = 
	{ 
		"ADC","IMUL","JNC","JZ","NOT","SBB",
		"ADD","INC","JNE","LEA","OR","SCASB",
		"AND","JA","JNG","LODSB","POP","SCASW",
		"CBW","JAE","JNGE","LODSW","PUSH","SHL",
		"CLC","JB","JNL","LOOP","RCL","SHR",
		"CLD","JBE","JNLE","LOOPE","RCR","STC",
		"CMC","JC","JNO","LOOPNE","REP","STD",
		"CMP","JCXZ","JNP","LOOPNZ","REPE","STOSB",
		"CMPSB","JE","JNS","LOOPZ","REPNE","STOSW",
		"CMPSW","JG","JNZ","MOV","REPNZ","SUB",
		"CWD","JGE","JO","MOVSB","REPZ","TEST",
		"DEC","JL","JP","MOVSW","ROL","XCHG",
		"DIV","JLE","JPE","MUL","ROR","XOR",
		"HLT","JMP","JPO","NEG","SAL",
		"IDIV","JNBE","JS","NOP","SAR",
		"RET",//RET return buyruksusudur.
		"NAME",//NAME name buyruksusudur.
		"ORG"//ORG org buyruksusudur.
	};

	private static final int[] operandCountList = 
	{ 
		2,1,1,1,1,2,
		2,1,1,2,2,0,
		2,1,1,0,1,0,
		0,1,1,0,1,2,
		0,1,1,1,2,2,
		0,1,1,1,2,0,
		0,1,1,1,1,0,
		2,1,1,1,1,0,
		0,1,1,1,1,0,
		0,1,1,2,1,2,
		0,1,1,0,1,2,
		1,1,1,0,2,2,
		1,1,1,1,2,2,
		0,1,1,1,2,
		1,1,1,0,2,
		0,//RET return buyruksusudur.
		1,//NAME name buyruksusudur.
		1//ORG org buyruksusudur.
	};
	
	private static int[][] fOpr = new int[list.length][9];
	private static int[][] sOpr = new int[list.length][9];

	protected SupportedInstructions()
	{
		FillSyntax();
		FillVector();
	}
	
	private void FillVector()
	{
		instructions = new Vector<String>(Arrays.asList(list));
		insyntaxes = new Vector<InstructionSyntax>();
		
		InstructionSyntax tmpSyntax = null;
		for(int i=0; i<instructions.size(); i++)
		{
			tmpSyntax = new InstructionSyntax(instructions.get(i), operandCountList[i]);
			
			if(operandCountList[i] > 0)
			{
				tmpSyntax.FillFirstOperand(fOpr[i]);
				if(operandCountList[i] == 2)
				{
					tmpSyntax.FillSecondOperand(sOpr[i]);
				}
			}
			
			insyntaxes.add(tmpSyntax);
		}
	}
	
	//Buyrugun desteklenen buyruklar listesinde olup olmadigini kontrol eder.
	//Sonuc boolean cinsinden donulur.
	public boolean isInstructionInList(String opcode)
	{
		boolean result = false;
		String opcodeToCheck = opcode.toUpperCase();
		
		if(instructions.contains(opcodeToCheck))
		{
			result = true;
		}
		return result;
	}
	
	//Girdi olarak buyruk opcode'u gelir.
	//Buyruk opcode'unun operand sayisi donulur.
	public int InstructionOperandCount(String opcode)
	{
		int result = -1;
		
		String opcodeToCheck = opcode.toUpperCase();
		
		int instructionIndex = instructions.indexOf(opcodeToCheck);
		if(instructionIndex != -1)//Eger opcode bulunduysa
		{
			result = operandCountList[instructionIndex];
		}
		
		return result;
	}
	
	//Desteklenen buyruklarin sayisini doner
	public int InstructionCount()
	{
		return instructions.size();
	} 
	
	public boolean areOperandsLegal(String opcode, int... operands)
	{
		boolean result = false;
		
		for(int i=0; i<instructions.size(); i++)
		{
			if(insyntaxes.get(i).getName().equals(opcode))
			{
				if(operands.length == operandCountList[i])
				{
					result = insyntaxes.get(i).verifyAccuracy(operands);
				}
				break;
			}
		}
		return result;
	}
	
	private void FillSyntax()
	{
		/*ADC*/ //6Tuple
		fOpr[0][0]=Global.register; sOpr[0][0]=Global.register;
		fOpr[0][1]=Global.register; sOpr[0][1]=Global.onlyRegister;
		fOpr[0][2]=Global.bytePtrOnlyRegister; sOpr[0][2]=Global.register;
		fOpr[0][3]=Global.wordPtrOnlyRegister; sOpr[0][3]=Global.register;
		fOpr[0][4]=Global.dWordPtrOnlyRegister; sOpr[0][4]=Global.register;
		fOpr[0][5]=Global.qWordPtrOnlyRegister; sOpr[0][5]=Global.register;
		
		/*IMUL*/
		fOpr[1][0]=Global.register;
		fOpr[1][1]=Global.bytePtrOnlyRegister;
		fOpr[1][2]=Global.wordPtrOnlyRegister;
		fOpr[1][3]=Global.dWordPtrOnlyRegister;
		fOpr[1][4]=Global.qWordPtrOnlyRegister;
		/*JNC*/
		fOpr[2][0]=Global.label;
		fOpr[2][1]=Global.onlyRegister;
		/*JZ*/
		fOpr[3][0]=Global.label;
		fOpr[3][1]=Global.onlyRegister;
		/*NOT*/
		fOpr[4][0]=Global.register;
		fOpr[4][1]=Global.bytePtrOnlyRegister;
		fOpr[4][2]=Global.wordPtrOnlyRegister;
		fOpr[4][3]=Global.dWordPtrOnlyRegister;
		fOpr[4][4]=Global.qWordPtrOnlyRegister;
		/*SBB*/ //6Tuple
		fOpr[5][0]=Global.register; sOpr[5][0]=Global.register;
		fOpr[5][1]=Global.register; sOpr[5][1]=Global.onlyRegister;
		fOpr[5][2]=Global.bytePtrOnlyRegister; sOpr[5][2]=Global.register;
		fOpr[5][3]=Global.wordPtrOnlyRegister; sOpr[5][3]=Global.register;
		fOpr[5][4]=Global.dWordPtrOnlyRegister; sOpr[5][4]=Global.register;
		fOpr[5][5]=Global.qWordPtrOnlyRegister; sOpr[5][5]=Global.register;
		/*ADD*/ //6Tuple
		fOpr[6][0]=Global.register; sOpr[6][0]=Global.register;
		fOpr[6][1]=Global.register; sOpr[6][1]=Global.decimalNumber;
		fOpr[6][2]=Global.register; sOpr[6][2]=Global.onlyRegister;
		fOpr[6][3]=Global.bytePtrOnlyRegister; sOpr[6][3]=Global.register;
		fOpr[6][4]=Global.wordPtrOnlyRegister; sOpr[6][4]=Global.register;
		fOpr[6][5]=Global.dWordPtrOnlyRegister; sOpr[6][5]=Global.register;
		fOpr[6][6]=Global.qWordPtrOnlyRegister; sOpr[6][6]=Global.register;
		/*INC*/
		fOpr[7][0]=Global.register;
		fOpr[7][1]=Global.bytePtrOnlyRegister;
		fOpr[7][2]=Global.wordPtrOnlyRegister;
		fOpr[7][3]=Global.dWordPtrOnlyRegister;
		fOpr[7][4]=Global.qWordPtrOnlyRegister;
		/*JNE*/
		fOpr[8][0]=Global.label;
		fOpr[8][1]=Global.onlyRegister;
		/*LEA*/
		fOpr[9][0]=Global.register; sOpr[9][0]=Global.offset;
		fOpr[9][1]=Global.register; sOpr[9][1]=Global.onlyRegister;
		/*OR*/ //6Tuple
		fOpr[10][0]=Global.register; sOpr[10][0]=Global.register;
		fOpr[10][1]=Global.register; sOpr[10][1]=Global.onlyRegister;
		fOpr[10][2]=Global.bytePtrOnlyRegister; sOpr[10][2]=Global.register;
		fOpr[10][3]=Global.wordPtrOnlyRegister; sOpr[10][3]=Global.register;
		fOpr[10][4]=Global.dWordPtrOnlyRegister; sOpr[10][4]=Global.register;
		fOpr[10][5]=Global.qWordPtrOnlyRegister; sOpr[10][5]=Global.register;
		/*SCASB*/
		//Operand Yok.
		/*AND*/ //6Tuple
		fOpr[12][0]=Global.register; sOpr[12][0]=Global.register;
		fOpr[12][1]=Global.register; sOpr[12][1]=Global.onlyRegister;
		fOpr[12][2]=Global.bytePtrOnlyRegister; sOpr[12][2]=Global.register;
		fOpr[12][3]=Global.wordPtrOnlyRegister; sOpr[12][3]=Global.register;
		fOpr[12][4]=Global.dWordPtrOnlyRegister; sOpr[12][4]=Global.register;
		fOpr[12][5]=Global.qWordPtrOnlyRegister; sOpr[12][5]=Global.register;
		/*JA*/
		fOpr[13][0]=Global.label;
		fOpr[13][1]=Global.onlyRegister;
		/*JNG*/
		fOpr[14][0]=Global.label;
		fOpr[14][1]=Global.onlyRegister;
		/*LODSB*/
		//Operand Yok.
		/*POP*/
		fOpr[16][0]=Global.register;
		fOpr[16][1]=Global.bytePtrOnlyRegister;
		fOpr[16][2]=Global.wordPtrOnlyRegister;
		fOpr[16][3]=Global.dWordPtrOnlyRegister;
		fOpr[16][4]=Global.qWordPtrOnlyRegister;
		/*SCASW*/
		//Operand Yok.
		/*CBW*/
		//Operand Yok.
		/*JAE*/
		fOpr[19][0]=Global.label;
		fOpr[19][1]=Global.onlyRegister;
		/*JNGE*/
		fOpr[20][0]=Global.label;
		fOpr[20][1]=Global.onlyRegister;
		/*LODSW*/
		//Operand Yok.
		/*PUSH*/
		fOpr[22][0]=Global.register;
		fOpr[22][1]=Global.bytePtrOnlyRegister;
		fOpr[22][2]=Global.wordPtrOnlyRegister;
		fOpr[22][3]=Global.dWordPtrOnlyRegister;
		fOpr[22][4]=Global.qWordPtrOnlyRegister;
		/*SHL*/
		fOpr[23][0]=Global.register; sOpr[23][0]=Global.register;
		fOpr[23][1]=Global.bytePtrOnlyRegister; sOpr[23][1]=Global.register;
		fOpr[23][2]=Global.wordPtrOnlyRegister; sOpr[23][2]=Global.register;
		fOpr[23][3]=Global.dWordPtrOnlyRegister; sOpr[23][3]=Global.register;
		fOpr[23][4]=Global.qWordPtrOnlyRegister; sOpr[23][4]=Global.register;
		/*CLC*/
		//Operand Yok.
		/*JB*/
		fOpr[25][0]=Global.label;
		fOpr[25][1]=Global.onlyRegister;
		/*JNL*/
		fOpr[26][0]=Global.label;
		fOpr[26][1]=Global.onlyRegister;
		/*LOOP*/
		fOpr[27][0]=Global.label;
		fOpr[27][1]=Global.onlyRegister;
		/*RCL*/
		fOpr[28][0]=Global.register; sOpr[28][0]=Global.register;
		fOpr[28][1]=Global.bytePtrOnlyRegister; sOpr[28][1]=Global.register;
		fOpr[28][2]=Global.wordPtrOnlyRegister; sOpr[28][2]=Global.register;
		fOpr[28][3]=Global.dWordPtrOnlyRegister; sOpr[28][3]=Global.register;
		fOpr[28][4]=Global.qWordPtrOnlyRegister; sOpr[28][4]=Global.register;
		/*SHR*/
		fOpr[29][0]=Global.register; sOpr[29][0]=Global.register;
		fOpr[29][1]=Global.bytePtrOnlyRegister; sOpr[29][1]=Global.register;
		fOpr[29][2]=Global.wordPtrOnlyRegister; sOpr[29][2]=Global.register;
		fOpr[29][3]=Global.dWordPtrOnlyRegister; sOpr[29][3]=Global.register;
		fOpr[29][4]=Global.qWordPtrOnlyRegister; sOpr[29][4]=Global.register;
		/*CLD*/
		//Operand Yok.
		/*JBE*/
		fOpr[31][0]=Global.label;
		fOpr[31][1]=Global.onlyRegister;
		/*JNLE*/
		fOpr[32][0]=Global.label;
		fOpr[32][1]=Global.onlyRegister;
		/*LOOPE*/
		fOpr[33][0]=Global.label;
		fOpr[33][1]=Global.onlyRegister;
		/*RCR*/
		fOpr[34][0]=Global.register; sOpr[34][0]=Global.register;
		fOpr[34][1]=Global.bytePtrOnlyRegister; sOpr[34][1]=Global.register;
		fOpr[34][2]=Global.wordPtrOnlyRegister; sOpr[34][2]=Global.register;
		fOpr[34][3]=Global.dWordPtrOnlyRegister; sOpr[34][3]=Global.register;
		fOpr[34][4]=Global.qWordPtrOnlyRegister; sOpr[34][4]=Global.register;
		/*STC*/
		//Operand Yok.
		/*CMC*/
		//Operand Yok.
		/*JC*/
		fOpr[37][0]=Global.label;
		fOpr[37][1]=Global.onlyRegister;
		/*JNO*/
		fOpr[38][0]=Global.label;
		fOpr[38][1]=Global.onlyRegister;
		/*LOOPNE*/
		fOpr[39][0]=Global.label;
		fOpr[39][1]=Global.onlyRegister;
		/*REP*/
		fOpr[40][0]=Global.opcode;
		/*STD*/
		//Operand Yok.
		/*CMP*/ //6Tuple
		fOpr[42][0]=Global.register; sOpr[42][0]=Global.register;
		fOpr[42][1]=Global.register; sOpr[42][1]=Global.onlyRegister;
		fOpr[42][2]=Global.bytePtrOnlyRegister; sOpr[42][2]=Global.register;
		fOpr[42][3]=Global.wordPtrOnlyRegister; sOpr[42][3]=Global.register;
		fOpr[42][4]=Global.dWordPtrOnlyRegister; sOpr[42][4]=Global.register;
		fOpr[42][5]=Global.qWordPtrOnlyRegister; sOpr[42][5]=Global.register;
		/*JCXZ*/
		fOpr[43][0]=Global.label;
		fOpr[43][1]=Global.onlyRegister;
		/*JNP*/
		fOpr[44][0]=Global.label;
		fOpr[44][1]=Global.onlyRegister;
		/*LOOPNZ*/
		fOpr[45][0]=Global.label;
		fOpr[45][1]=Global.onlyRegister;
		/*REPE*/
		fOpr[46][0]=Global.opcode;
		/*STOSB*/
		//Operand Yok.
		/*CMPSB*/
		//Operand Yok.
		/*JE*/
		fOpr[49][0]=Global.label;
		fOpr[49][1]=Global.onlyRegister;
		/*JNS*/
		fOpr[50][0]=Global.label;
		fOpr[50][1]=Global.onlyRegister;
		/*LOOPZ*/
		fOpr[51][0]=Global.label;
		fOpr[51][1]=Global.onlyRegister;
		/*REPNE*/
		fOpr[52][0]=Global.opcode;
		/*STOSW*/
		//Operand Yok.
		/*CMPSW*/
		//Operand Yok.
		/*JG*/
		fOpr[55][0]=Global.label;
		fOpr[55][1]=Global.onlyRegister;
		/*JNZ*/
		fOpr[56][0]=Global.label;
		fOpr[56][1]=Global.onlyRegister;
		/*MOV*/
		fOpr[57][0]=Global.register; sOpr[57][0]=Global.register;
		fOpr[57][1]=Global.register; sOpr[57][1]=Global.decimalNumber;
		fOpr[57][2]=Global.register; sOpr[57][2]=Global.offset;
		fOpr[57][3]=Global.register; sOpr[57][3]=Global.segmentInitializion;
		fOpr[57][4]=Global.register; sOpr[57][4]=Global.onlyRegister;
		fOpr[57][5]=Global.bytePtrOnlyRegister; sOpr[57][5]=Global.register;
		fOpr[57][6]=Global.wordPtrOnlyRegister; sOpr[57][6]=Global.register;
		fOpr[57][7]=Global.dWordPtrOnlyRegister; sOpr[57][7]=Global.register;
		fOpr[57][8]=Global.qWordPtrOnlyRegister; sOpr[57][8]=Global.register;
		/*REPNZ*/
		fOpr[58][0]=Global.opcode;
		/*SUB*/ //6Tuple
		fOpr[59][0]=Global.register; sOpr[59][0]=Global.register;
		fOpr[59][1]=Global.register; sOpr[59][1]=Global.onlyRegister;
		fOpr[59][2]=Global.bytePtrOnlyRegister; sOpr[59][2]=Global.register;
		fOpr[59][3]=Global.wordPtrOnlyRegister; sOpr[59][3]=Global.register;
		fOpr[59][4]=Global.dWordPtrOnlyRegister; sOpr[59][4]=Global.register;
		fOpr[59][5]=Global.qWordPtrOnlyRegister; sOpr[59][5]=Global.register;
		/*CWD*/
		//Operand Yok.
		/*JGE*/
		fOpr[61][0]=Global.label;
		fOpr[61][1]=Global.onlyRegister;
		/*JO*/
		fOpr[62][0]=Global.label;
		fOpr[62][1]=Global.onlyRegister;
		/*MOVSB*/
		//Operand Yok.
		/*REPZ*/
		fOpr[64][0]=Global.opcode;
		/*TEST*/ //6Tuple
		fOpr[65][0]=Global.register; sOpr[65][0]=Global.register;
		fOpr[65][1]=Global.register; sOpr[65][1]=Global.onlyRegister;
		fOpr[65][2]=Global.bytePtrOnlyRegister; sOpr[65][2]=Global.register;
		fOpr[65][3]=Global.wordPtrOnlyRegister; sOpr[65][3]=Global.register;
		fOpr[65][4]=Global.dWordPtrOnlyRegister; sOpr[65][4]=Global.register;
		fOpr[65][5]=Global.qWordPtrOnlyRegister; sOpr[65][5]=Global.register;
		/*DEC*/
		fOpr[66][0]=Global.register;
		fOpr[66][1]=Global.bytePtrOnlyRegister;
		fOpr[66][2]=Global.wordPtrOnlyRegister;
		fOpr[66][3]=Global.dWordPtrOnlyRegister;
		fOpr[66][4]=Global.qWordPtrOnlyRegister;
		/*JL*/
		fOpr[67][0]=Global.label;
		fOpr[67][1]=Global.onlyRegister;
		/*JP*/
		fOpr[68][0]=Global.label;
		fOpr[68][1]=Global.onlyRegister;
		/*MOVSW*/
		//Operand Yok.
		/*ROL*/
		fOpr[70][0]=Global.register; sOpr[70][0]=Global.register;
		fOpr[70][1]=Global.bytePtrOnlyRegister; sOpr[70][1]=Global.register;
		fOpr[70][2]=Global.wordPtrOnlyRegister; sOpr[70][2]=Global.register;
		fOpr[70][3]=Global.dWordPtrOnlyRegister; sOpr[70][3]=Global.register;
		fOpr[70][4]=Global.qWordPtrOnlyRegister; sOpr[70][4]=Global.register;
		/*XCHG*/ //6Tuple
		fOpr[71][0]=Global.register; sOpr[71][0]=Global.register;
		fOpr[71][1]=Global.register; sOpr[71][1]=Global.onlyRegister;
		fOpr[71][2]=Global.bytePtrOnlyRegister; sOpr[71][2]=Global.register;
		fOpr[71][3]=Global.wordPtrOnlyRegister; sOpr[71][3]=Global.register;
		fOpr[71][4]=Global.dWordPtrOnlyRegister; sOpr[71][4]=Global.register;
		fOpr[71][5]=Global.qWordPtrOnlyRegister; sOpr[71][5]=Global.register;
		/*DIV*/
		fOpr[72][0]=Global.register;
		fOpr[72][1]=Global.bytePtrOnlyRegister;
		fOpr[72][2]=Global.wordPtrOnlyRegister;
		fOpr[72][3]=Global.dWordPtrOnlyRegister;
		fOpr[72][4]=Global.qWordPtrOnlyRegister;
		/*JLE*/
		fOpr[73][0]=Global.label;
		fOpr[73][1]=Global.onlyRegister;
		/*JPE*/
		fOpr[74][0]=Global.label;
		fOpr[74][1]=Global.onlyRegister;
		/*MUL*/
		fOpr[75][0]=Global.register;
		fOpr[75][1]=Global.bytePtrOnlyRegister;
		fOpr[75][2]=Global.wordPtrOnlyRegister;
		fOpr[75][3]=Global.dWordPtrOnlyRegister;
		fOpr[75][4]=Global.qWordPtrOnlyRegister;
		/*ROR*/
		fOpr[76][0]=Global.register; sOpr[76][0]=Global.register;
		fOpr[76][1]=Global.bytePtrOnlyRegister; sOpr[76][1]=Global.register;
		fOpr[76][2]=Global.wordPtrOnlyRegister; sOpr[76][2]=Global.register;
		fOpr[76][3]=Global.dWordPtrOnlyRegister; sOpr[76][3]=Global.register;
		fOpr[76][4]=Global.qWordPtrOnlyRegister; sOpr[76][4]=Global.register;
		/*XOR*/ //6Tuple
		fOpr[77][0]=Global.register; sOpr[77][0]=Global.register;
		fOpr[77][1]=Global.register; sOpr[77][1]=Global.onlyRegister;
		fOpr[77][2]=Global.bytePtrOnlyRegister; sOpr[77][2]=Global.register;
		fOpr[77][3]=Global.wordPtrOnlyRegister; sOpr[77][3]=Global.register;
		fOpr[77][4]=Global.dWordPtrOnlyRegister; sOpr[77][4]=Global.register;
		fOpr[77][5]=Global.qWordPtrOnlyRegister; sOpr[77][5]=Global.register;
		/*HLT*/
		//Operand Yok.
		/*JMP*/
		fOpr[79][0]=Global.label;
		fOpr[79][1]=Global.onlyRegister;
		/*JPO*/
		fOpr[80][0]=Global.label;
		fOpr[80][1]=Global.onlyRegister;
		/*NEG*/
		fOpr[81][0]=Global.register;
		fOpr[81][1]=Global.bytePtrOnlyRegister;
		fOpr[81][2]=Global.wordPtrOnlyRegister;
		fOpr[81][3]=Global.dWordPtrOnlyRegister;
		fOpr[81][4]=Global.qWordPtrOnlyRegister;
		/*SAL*/
		fOpr[82][0]=Global.register; sOpr[82][0]=Global.register;
		fOpr[82][1]=Global.bytePtrOnlyRegister; sOpr[82][1]=Global.register;
		fOpr[82][2]=Global.wordPtrOnlyRegister; sOpr[82][2]=Global.register;
		fOpr[82][3]=Global.dWordPtrOnlyRegister; sOpr[82][3]=Global.register;
		fOpr[82][4]=Global.qWordPtrOnlyRegister; sOpr[82][4]=Global.register;
		/*IDIV*/
		fOpr[83][0]=Global.register;
		fOpr[83][1]=Global.bytePtrOnlyRegister;
		fOpr[83][2]=Global.wordPtrOnlyRegister;
		fOpr[83][3]=Global.dWordPtrOnlyRegister;
		fOpr[83][4]=Global.qWordPtrOnlyRegister;
		/*JNBE*/
		fOpr[84][0]=Global.label;
		fOpr[84][1]=Global.onlyRegister;
		/*JS*/
		fOpr[85][0]=Global.label;
		fOpr[85][1]=Global.onlyRegister;
		/*NOP*/
		//Operand Yok.
		/*SAR*/
		fOpr[87][0]=Global.register; sOpr[87][0]=Global.register;
		fOpr[87][1]=Global.bytePtrOnlyRegister; sOpr[87][1]=Global.register;
		fOpr[87][2]=Global.wordPtrOnlyRegister; sOpr[87][2]=Global.register;
		fOpr[87][3]=Global.dWordPtrOnlyRegister; sOpr[87][3]=Global.register;
		fOpr[87][4]=Global.qWordPtrOnlyRegister; sOpr[87][4]=Global.register;
		/*RET*/
		//Operand Yok.
		/*NAME*/
		fOpr[89][0]=Global.string;
		/*ORG*/
		sOpr[90][0]=Global.decimalNumber;
	}
}