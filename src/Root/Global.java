package Root;

import java.util.Arrays;

public final class Global 
{
	public static Gui.RegistersArea guiRegPatch = null;
	public static Gui.FlagsArea guiFlagPatch = null;
	
	public static int currentLine = 1;
	
	public static final boolean BACKUP_LOG_FILE = true;
	
	//maxOperandOfInstruction desteklenen buyruklarin alabilecegi en fazla islenen sayisini tutar.
	public static final int maxOperandOfInstruction = 2;
	
	public static final int commentSlashStar = 1;
	public static final int commentSlashSlash = 2;
	public static final int commentSharp = 3;
	public static final int commentSemiColon = 4;
	public static final int commentCOMMENT = 5;
	public static final int instruction = 6;
	public static final int label = 7;
	public static final int space = 8;
	public static final int newLine = 9;
	public static final int comma = 10;
	
	public static final int onlyAddress = 11;
	public static final int addressRegister = 12;
	public static final int addressAddress = 13;
	public static final int onlyRegister = 14;
	public static final int registerRegister = 15;
	public static final int registerAddress = 16;
	
	public static final int binaryNumber = 17;
	public static final int decimalNumber = 18;
	public static final int hexadecimalNumber = 19;
	
	public static final int initializerByte = 20;
	public static final int initializerSByte = 21;
	public static final int initializerWord = 22;
	public static final int initializerSWord = 23;
	public static final int initializerDWord = 24;
	public static final int initializerSDWord = 25;
	
	public static final int directiveCode = 26;
	public static final int directiveModel = 27;
	public static final int directiveStack = 28;
	public static final int directiveData = 29;
	public static final int directiveStartup = 30;
	public static final int directiveExit = 31;
	public static final int directiveEnd = 1999;
	
	public static final int questionMark = 32;
	
	public static final int dupOperatorNumber = 33;
	public static final int dupOperatorString = 34;
	public static final int dupOperatorQuestion = 35;
	
	public static final int segmentInitializion = 36;
	
	public static final int variable = 37;
	public static final int register = 38;
	public static final int string = 39;
	
	public static final int opcode = 40;
	
	public static final int offset = 42;
	public static final int bytePtr = 43;
	public static final int wordPtr = 44;
	public static final int dWordPtr = 45;
	public static final int qWordPtr = 46;
	public static final int bytePtrOnlyAddress = 47;
	public static final int wordPtrOnlyAddress = 48;
	public static final int dWordPtrOnlyAddress = 49;
	public static final int qWordPtrOnlyAddress = 50;
	public static final int bytePtrOnlyRegister = 51;
	public static final int wordPtrOnlyRegister = 52;
	public static final int dWordPtrOnlyRegister = 53;
	public static final int qWordPtrOnlyRegister = 54;
	public static final int bytePtrAddressAddress = 55;
	public static final int bytePtrAddressRegister = 56;
	public static final int bytePtrRegisterAddress = 57;
	public static final int bytePtrRegisterRegister = 58;
	public static final int wordPtrAddressAddress = 59;
	public static final int wordPtrAddressRegister = 60;
	public static final int wordPtrRegisterAddress = 61;
	public static final int wordPtrRegisterRegister = 62;
	public static final int dWordPtrAddressAddress = 63;
	public static final int dWordPtrAddressRegister = 64;
	public static final int dWordPtrRegisterAddress = 65;
	public static final int dWordPtrRegisterRegister = 66;
	public static final int qWordPtrAddressAddress = 67;
	public static final int qWordPtrAddressRegister = 68;
	public static final int qWordPtrRegisterAddress = 69;
	public static final int qWordPtrRegisterRegister = 70;
	
	public static final int directive = 1001;
	
	//Model tipleri
	public static final int small = 2001;
	public static final int large = 2002;
	
	//Program ayarlari
	public static int defaultModelType = large;
	public static int defaultStackSize = 200;
	public static int defaultMemorySize = 4096;//Byte cinsinden - 1 kb
	public static int defaultDiskSize = 1048576;//Byte cinsinden - 1 mb
	
	//Yazmac indisleri
	//Not: Siralar degistigi zaman sistem bozulacaktir.
	public static final int ax = 0;
	public static final int bx = 1;
	public static final int cx = 2;
	public static final int dx = 3;
	public static final int ah = 4;
	public static final int al = 5;
	public static final int bh = 6;
	public static final int bl = 7;
	public static final int ch = 8;
	public static final int cl = 9;
	public static final int dh = 10;
	public static final int dl = 11;
	public static final int cs = 12;
	public static final int ip = 13;
	public static final int ss = 14;
	public static final int sp = 15;
	public static final int bp = 16;
	public static final int si = 17;
	public static final int di = 18;
	public static final int ds = 19;
	public static final int es = 20;
	//Yazmac Isimleri
	public static final String[] registerList = 
	{
		"AX","BX","CX","DX","AH","AL","BH","BL","CH","CL","DH","DL",
		"CS","IP","SS","SP","BP","SI","DI","DS","ES"
	};
	//Low ve High olarak ayrilabilen yazmaclar
	public static final String[] parentRegisters = 
	{
		"AX","BX","CX","DX"
	};
	//High Cocuklar
	public static final String[] highChildRegisters = 
	{
		"AH","BH","CH","DH"
	};
	//Low Cocuklar
	public static final String[] lowChildRegisters = 
	{
		"AL","BL","CL","DL"
	};
	//Ebeveyn olmayan yazmaclar
	public static final String[] normalRegisters = 
	{
		"CS","IP","SS","SP","BP","SI","DI","DS","ES"
	};
	//Yazmac Isimleri
	public static final int[] registerSizes = 
	{
		2,2,2,2,1,1,1,1,1,1,1,1,
		2,2,2,2,2,2,2,2,2
	};
	//Yazmac Degerleri
	private static int[] registerValues = 
	{
		0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
		0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0
	};
	//Yazmac Ebeveynlikleri
	public static final int[] registerChildren = 
	{
		ah,bh,ch,dh,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1
	};
	//Yazmac Cocukluklari
	public static final int[] registerParent = 
	{
		-1,-1,-1,-1,ax,ax,bx,bx,cx,cx,dx,dx,
		-1,-1,-1,-1,-1,-1,-1,-1,-1
	};
	public static final int GetRegister(final int inReg)
	{
		return registerValues[inReg];
	}
	public static final void SetRegister(final int inReg, final int value)
	{
		boolean isNegative = false;
		if(value < 0) isNegative = true;
		
		registerValues[inReg] = value;
		if(registerSizes[inReg] == 2)
		{
			if(registerChildren[inReg] != -1)
			{
				if(isNegative)
				{
					registerValues[registerChildren[inReg]+1] = BitsToSignedInt(Arrays.copyOfRange(IntToBits(value, 2), 0, 8));//Low Bitler
					registerValues[registerChildren[inReg]] = BitsToSignedInt(Arrays.copyOfRange(IntToBits(value, 2), 8, 16));//High Bitler
				}
				else
				{
					registerValues[registerChildren[inReg]+1] = BitsToUnsignedInt(Arrays.copyOfRange(IntToBits(value, 2), 0, 8));//Low Bitler
					registerValues[registerChildren[inReg]] = BitsToUnsignedInt(Arrays.copyOfRange(IntToBits(value, 2), 8, 16));//High Bitler
				}
			}
		}
		else//Eger 1 byte ise ah,al,bh,bl,ch,cl,dh yada dl dir.
		{
			boolean isHigh = false;
			if(inReg%2 == 0) isHigh = true;
			
			boolean[] lowBits, highBits;
			
			if(isHigh)
			{
				lowBits = Arrays.copyOfRange(IntToBits(registerValues[registerParent[inReg]], 2), 0, 8);
				highBits = IntToBits(value, 1);
			}
			else
			{
				highBits = Arrays.copyOfRange(IntToBits(registerValues[registerParent[inReg]], 2), 8, 16);
				lowBits = IntToBits(value, 1);
			}
			
			boolean[] twoByte = new boolean[16];
			for(int i=0; i<8; i++) twoByte[i] = lowBits[i];
			for(int i=8; i<16; i++)  twoByte[i] = highBits[i-8];
			
			registerValues[registerParent[inReg]] = BitsToSignedInt(twoByte);
		}
		if(guiRegPatch != null)
		{
			guiRegPatch.UpdateRegister(inReg);
			if(registerParent[inReg] != -1)
			{
				guiRegPatch.UpdateRegister(registerParent[inReg]);
			}
		}
	}
	
	//6 Flag Desteklenecek.
	//CF ZF SF OF PF DF
	public static final int flagsCount = 6;
	
	public static final int cf = 0;
	public static final int zf = 1;
	public static final int sf = 2;
	public static final int of = 3;
	public static final int pf = 4;
	public static final int df = 5;
	private static boolean[] flags = new boolean[6];
	public static void setFlag(int inFlag, boolean value)
	{
		flags[inFlag] = value;
		if(guiFlagPatch != null)
		{
			guiFlagPatch.UpdateFlag(inFlag);
		}
	}
	public static boolean getFlag(int inFlag)
	{
		return flags[inFlag];
	}
	//Flag Isimleri
	public static final String[] flagList = 
	{
		"CF","ZF","SF","OF","PF","DF"
	};
	
	public static final int CountOccurrences(String haystack, char needle)
	{
	    int count = 0;
	    for (int i=0; i < haystack.length(); i++)
	    {
	        if (haystack.charAt(i) == needle)
	        {
	             count++;
	        }
	    }
	    return count;
	}
	public static final boolean[] IntToBits(final int b, final int len) 
	{
		boolean[] result = null;
		if(len == 1)
		{
			result = new boolean[] 
			{  
				(b &    1) != 0,(b &    2) != 0,(b &    4) != 0,(b &    8) != 0,(b & 0x10) != 0,(b & 0x20) != 0,(b & 0x40) != 0,(b & 0x80) != 0
			};
		}
		else if(len == 2)
		{
			result = new boolean[] 
			{  
				(b &    1) != 0,(b &    2) != 0,(b &    4) != 0,(b &    8) != 0,(b & 0x10) != 0,(b & 0x20) != 0,(b & 0x40) != 0,(b & 0x80) != 0,
				(b & 0x100) != 0,(b & 0x200) != 0,(b & 0x400) != 0,	(b & 0x800) != 0,(b & 0x1000) != 0,(b & 0x2000) != 0,(b & 0x4000) != 0,(b & 0x8000) != 0
			};
		}
		else if(len == 4)
		{
			result = new boolean[] 
			{  
				(b &    1) != 0,(b &    2) != 0,(b &    4) != 0,(b &    8) != 0,(b & 0x10) != 0,(b & 0x20) != 0,(b & 0x40) != 0,(b & 0x80) != 0,
				(b & 0x100) != 0,(b & 0x200) != 0,(b & 0x400) != 0,	(b & 0x800) != 0,(b & 0x1000) != 0,(b & 0x2000) != 0,(b & 0x4000) != 0,(b & 0x8000) != 0,
				(b & 0x10000) != 0,(b & 0x20000) != 0,(b & 0x40000) != 0,(b & 0x80000) != 0,(b & 0x100000) != 0,(b & 0x200000) != 0,(b & 0x400000) != 0,(b & 0x800000) != 0,
				(b & 0x1000000) != 0,(b & 0x2000000) != 0,(b & 0x4000000) != 0,(b & 0x8000000) != 0,(b & 0x10000000) != 0,(b & 0x20000000) != 0,(b & 0x40000000) != 0,(b & 0x80000000) != 0
			};
		}
		return result;
	}
	public static final int BitsToUnsignedInt(boolean[] bits)
	{
		int result = 0;
		
		for(int i=0; i<bits.length; i++)
		{
			if(bits[i] == true)
			{
				result += Math.pow(2, i);
			}
		}
		return result;
	}
	public static final int BitsToSignedInt(boolean[] bits)
	{
		int result = 0;
		
		for(int i=0; i<bits.length; i++)
		{
			if(bits[i] == true)
			{
				result += Math.pow(2, i);
			}
		}
		return result;
		/*int result = 0;
		
		//Once bitleri ters cevirelim.
		for(int i=0; i<bits.length; i++)
		{
			if(bits[i] == true) bits[i]  = false;
			else bits[i]  = true;
		}
		
		//Sonra 1 ekleyelim.
		boolean carry = false;
		bits[0] = (bits[0] ^ true);//Ilk biti, 1 ile ex-or yapalim.
		if(bits[0] == false)//Carry var demektir.
		{
			carry = true;
			int k=1;
			while(k<8 && carry == true)//Ilk bit haric diger 7 biti carry ile ex-or'layalim.
			{
				bits[k] = (bits[k] ^ carry);
				if(bits[k] == false) carry = true;
				else carry = false;	
				k++;
			}
		}
		//Carry yoksa baska islem yapmamiza gerek yok.
		
		result = BitsToUnsignedInt(bits);
		result = result*(-1);
		
		return result;*/
	}
	public static boolean ParityCheck(int value)
	{
		int size;
		if(value >= -128 && value < 256) size = 1;
		else if(value >= -32768 && value < 65536) size = 2;
		else size = 4;
		
		boolean[] valInBits = Global.IntToBits(value, size);
		if(valInBits.length > 8)
		{
			boolean[] tmpBits = new boolean[8];
			
			int j=8;
			for(int i=0; i<8; i++)
			{
				valInBits[j-i-1] = valInBits[valInBits.length-i-1];
			}
			valInBits = tmpBits;
		}
		
		int count = 0;
		for(int i=0; i<8; i++)
		{
			if(valInBits[i] == true)
			{
				count++;
			}
		}
		
		boolean result = false;
		if(count%2 == 0)
		{
			result = true;
		}
		
		return result;
	}
}
