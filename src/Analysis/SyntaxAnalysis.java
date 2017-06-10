package Analysis;

import java.util.Vector;

import Errors.ErrorList;
import Errors.CurrentErrors;
import Fragments.Directive;
import Fragments.Fragment;
import Fragments.Instruction;
import Fragments.Label;
import Fragments.Variable;
import Root.Global;
import Root.SupportedInstructions;

public class SyntaxAnalysis 
{
	private boolean syntaxAnalysisSucceeded = false;
	
	private String code = null;
	
	private CurrentErrors errors = CurrentErrors.get();
	private ErrorList eList = ErrorList.get();
	
	private Vector<Fragment> fragments = null;

	public SyntaxAnalysis(String currentCode)
	{
		this.code = currentCode;

		code = code.replaceAll("\r", " ");
		code = code.replaceAll("\n", " \n ");

		fragments = new Vector<Fragment>();
		
		syntaxAnalysisSucceeded = Fragmentation();
	}
	
	//Fragmentation tum kod parcasini tekil buyruklar satiri haline donusturmeye calisir.
	//continuousTokenGroup bir token'dan sonra baska bir token gerektigi durumlarda kullanilir.
	private String continuousTokenGroup = null;
	private int continuousTokenType = -1;
	private int continuousTokenLoopCounter = -1;
	private Vector<String> continuousTokenFlag1 = new Vector<String>();
	private Vector<Integer> continuousTokenFlag2 = new Vector<Integer>();
	private String continuousTokenExtraFlagStr = null;
	private int continuousTokenExtraFlagInt = -1;
	private boolean expectingSpace = false;
	private boolean expectingComma = false;
	
	private void ResetFragmentation()
	{
		continuousTokenType = -1;
		continuousTokenLoopCounter = -1;
		continuousTokenGroup = "";
		continuousTokenFlag1 = null;
		continuousTokenFlag2 = null;
		continuousTokenFlag1 = new Vector<String>();
		continuousTokenFlag2 = new Vector<Integer>();
		continuousTokenExtraFlagStr = "";
		continuousTokenExtraFlagInt = -1;
		expectingSpace = false;
		expectingComma = false;
	}
	private boolean Fragmentation()
	{
		boolean result = true;
		
		//Eger kod bossa
		int len = code.length();
		if(len == 0)
		{
			ResetToken();
			errors.addError(eList.GetError(eList.CODE_IS_EMPTY));//Kuyruga yeni hatayi at.
			result = false;
		}
		else//Eger kod bos degilse
		{
			String remainedTokens = code;
			
			boolean getTheNextToken = true;
			
			while(remainedTokens.length() > 0)
			{
				if(getTheNextToken == true)//Eger sonraki token'a gecebilirsek
				{
					remainedTokens = NextToken(remainedTokens);
				}
				getTheNextToken = true;//Her durumda bu noktada getTheNextToken true olmalidir.
				
				//Eger degismis olmasi gereken degiskenler ayni kaldiysa
				//Ya bir hata olusmustur, ya da beklenmedik bir hata soz konusudur.
				if(nextToken == null || nextTokenType == -1)
				{
					/*errors.addError(eList.GetError(eList.UNEXPECTED_SYNTAX_ERROR));//Kuyruga yeni hatayi at.
					result = false;*/
					break;
				}
				//Eger bir hata yoksa
				else
				{
					//Eger hem virgul, hem bosluk gelebilirse, kosul ikisinden birinin mutlaka olmasi.
					if(expectingSpace == true && expectingComma == true)
					{
						if(nextTokenType != Global.space && nextTokenType != Global.newLine && nextTokenType != Global.comma)
						{
							errors.addError(eList.GetError(eList.EXPECTING_SPACE_OR_COMMA));//Kuyruga yeni hatayi at.
							result = false;
							break;
						}
						expectingComma = false;
						expectingSpace = false;
					}
					//Eger bu token'da bosluk bekleniyorsa
					else if(expectingSpace == true)
					{
						//Ve token bosluk degilse
						if(nextTokenType != Global.space && nextTokenType != Global.newLine)
						{
							errors.addError(eList.GetError(eList.EXPECTING_SPACE));//Kuyruga yeni hatayi at.
							result = false;
							break;
						}
						expectingSpace = false;
					}
					//Eger bu token'da virgul bekleniyorsa
					else if(expectingComma == true)
					{
						//Ve token virgul degilse
						if(nextTokenType != Global.comma)
						{
							char nextChar = nextToken.charAt(0);
							int unnecessaryPart = 0;
							boolean exitLoop = false;
							while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
							{
								if(nextChar == '\n')
								{
									Global.currentLine++;
								}
								unnecessaryPart++;
								if(unnecessaryPart < nextToken.length())
								{
									nextChar = nextToken.charAt(unnecessaryPart);
								}
								else
								{
									exitLoop = true;
								}
							}
							nextToken = nextToken.substring(unnecessaryPart);
							
							if(nextToken.length() == 0)//Eger nextToken bossa
							{
								expectingComma = true;
							}
							else if(nextToken != ",")//Eger bos degilse ve virgul degilse
							{
								if(continuousTokenType == Global.variable)
								{
									expectingComma = false;
									getTheNextToken = false;
								}
								else
								{
									errors.addError(eList.GetError(eList.EXPECTING_COMMA));//Kuyruga yeni hatayi at.
									result = false;
									break;
								}
							}
							else//Eger virgulse
							{
								expectingComma = false;
							}
						}
						else//Eger beklenildigi gibi virgul girildiyse
						{
							expectingComma = false;
						}
					}
					else
					{
						//Eger bosluk ya da yeni satir beklenmemesine karsin bunlar kullanildiysa
						if(nextTokenType == Global.space || nextTokenType == Global.newLine)
						{
							//Sonraki token'a bakalim.
							//Burasi bos kalabilir.
						}
						//Eger continuousTokenType instruction gosteriyorsa
						else if(continuousTokenType == Global.instruction)
						{
							//Eger buyrugun operandlari islenmeye devam ediliyorsa
							if(continuousTokenLoopCounter > 0)
							{
								//Egerki sonraki token opcode ise
								//Bir opcode'dan sonra baska bir opcode kullanmak yalnizca operandsiz opcode'larda olabilir.
								if(nextTokenType == Global.instruction)
								{
									//Eger mevcut buyrugun ilk operandi opcode ise ve daha islenecek baska operandlar da varsa
									if(continuousTokenLoopCounter != 1)
									{
										errors.addError(eList.GetError(eList.WRONG_CHAIN_INSTRUCTION_USAGE));//Kuyruga yeni hatayi at.
										result = false;
										break;
									}
									else
									{
										//Eger buyrugun operand'i yoksa dogrudur.
										if(SupportedInstructions.get().InstructionOperandCount(nextToken) == 0)
										{
											fragments.add(new Instruction(continuousTokenGroup + " " + nextToken, continuousTokenGroup, nextToken, Global.opcode));
											continuousTokenGroup = continuousTokenGroup + " " + nextToken;
											
											//Tek operand olabilecegi icin devamliligi bitirebiliriz.
											ResetFragmentation();
										}
										else
										{
											errors.addError(eList.GetError(eList.WRONG_CHAIN_INSTRUCTION_USAGE));//Kuyruga yeni hatayi at.
											result = false;
											break;
										}
									}
								}
								//Eger nextToken opcode degilse
								//nextToken bu asamadan sonra ancak;
								//Yazmac, adres, anlik, degisken ya da label olabilir.
								//Label olup olamayacagini bilmiyoruz o yuzden degisken olup olmadigina bakmamiz yeterli.
								else
								{
									//Eger yazmac, anlik ya da label ise
									if(nextTokenType == Global.register || nextTokenType == Global.variable || nextTokenType == Global.binaryNumber || nextTokenType == Global.decimalNumber || nextTokenType == Global.hexadecimalNumber)
									{
										continuousTokenFlag1.add(nextToken);
										continuousTokenFlag2.add(nextTokenType);
										continuousTokenGroup = continuousTokenGroup + " " + nextToken;
									}
									//Eger adres ise
									else if(nextTokenType == Global.onlyAddress || nextTokenType == Global.addressRegister || nextTokenType == Global.addressAddress || nextTokenType == Global.onlyRegister || nextTokenType == Global.registerRegister || nextTokenType == Global.registerAddress)
									{
										if(nextTokenReserved == null)//Eger adres tek elemanli ise
										{
											continuousTokenFlag1.add(nextToken);
											continuousTokenFlag2.add(nextTokenType);
											continuousTokenGroup = continuousTokenGroup + " " + "[" + nextToken + "]";
										}
										else//Eger adres iki elemanli ise
										{
											continuousTokenFlag1.add(nextToken + "+" + nextTokenReserved);
											continuousTokenFlag2.add(nextTokenType);
											continuousTokenGroup = continuousTokenGroup + " " + "[" + nextToken + "+" + nextTokenReserved + "]";
										}
									}
									//Eger offset ise
									else if(nextTokenType == Global.offset)
									{
										continuousTokenFlag1.add(nextToken);
										continuousTokenFlag2.add(nextTokenType);
										continuousTokenGroup = continuousTokenGroup + " " + "OFFSET " + nextToken;
									}
									//Eger segmentInitialization ise
									else if(nextTokenType == Global.segmentInitializion)
									{
										continuousTokenFlag1.add(nextToken);
										continuousTokenFlag2.add(nextTokenType);
										continuousTokenGroup = continuousTokenGroup + " " + "@" + nextToken;
									}
									//Eger BYTE PTR, WORD PTR, DWORD PTR ya da QWORD PTR direktiflerinden birisi ise
									else if(nextTokenType == Global.bytePtrOnlyAddress || nextTokenType == Global.bytePtrOnlyRegister)
									{
										continuousTokenFlag1.add(nextToken);
										continuousTokenFlag2.add(nextTokenType);
										continuousTokenGroup = continuousTokenGroup + " " + "BYTE PTR[" + nextToken + "]";
									}
									else if(nextTokenType == Global.wordPtrOnlyAddress || nextTokenType == Global.wordPtrOnlyRegister)
									{
										continuousTokenFlag1.add(nextToken);
										continuousTokenFlag2.add(nextTokenType);
										continuousTokenGroup = continuousTokenGroup + " " + "WORD PTR[" + nextToken + "]";
									}
									else if(nextTokenType == Global.dWordPtrOnlyAddress || nextTokenType == Global.dWordPtrOnlyRegister)
									{
										continuousTokenFlag1.add(nextToken);
										continuousTokenFlag2.add(nextTokenType);
										continuousTokenGroup = continuousTokenGroup + " " + "DWORD PTR[" + nextToken + "]";
									}
									else if(nextTokenType == Global.qWordPtrOnlyAddress || nextTokenType == Global.qWordPtrOnlyRegister)
									{
										continuousTokenFlag1.add(nextToken);
										continuousTokenFlag2.add(nextTokenType);
										continuousTokenGroup = continuousTokenGroup + " " + "QWORD PTR[" + nextToken + "]";
									}
									else if(nextTokenType == Global.bytePtrAddressAddress || nextTokenType == Global.bytePtrAddressRegister || nextTokenType == Global.bytePtrRegisterAddress || nextTokenType == Global.bytePtrRegisterRegister)
									{
										continuousTokenFlag1.add(nextToken + "+" + nextTokenReserved);
										continuousTokenFlag2.add(nextTokenType);
										continuousTokenGroup = continuousTokenGroup + " " + "BYTE PTR[" + nextToken + "+" + nextTokenReserved + "]";
									}
									else if(nextTokenType == Global.wordPtrAddressAddress || nextTokenType == Global.wordPtrAddressRegister || nextTokenType == Global.wordPtrRegisterAddress || nextTokenType == Global.wordPtrRegisterRegister)
									{
										continuousTokenFlag1.add(nextToken + "+" + nextTokenReserved);
										continuousTokenFlag2.add(nextTokenType);
										continuousTokenGroup = continuousTokenGroup + " " + "WORD PTR[" + nextToken + "+" + nextTokenReserved + "]";
									}
									else if(nextTokenType == Global.dWordPtrAddressAddress || nextTokenType == Global.dWordPtrAddressRegister || nextTokenType == Global.dWordPtrRegisterAddress || nextTokenType == Global.dWordPtrRegisterRegister)
									{
										continuousTokenFlag1.add(nextToken + "+" + nextTokenReserved);
										continuousTokenFlag2.add(nextTokenType);
										continuousTokenGroup = continuousTokenGroup + " " + "DWORD PTR[" + nextToken + "+" + nextTokenReserved + "]";
									}
									else if(nextTokenType == Global.qWordPtrAddressAddress || nextTokenType == Global.qWordPtrAddressRegister || nextTokenType == Global.qWordPtrRegisterAddress || nextTokenType == Global.qWordPtrRegisterRegister)
									{
										continuousTokenFlag1.add(nextToken + "+" + nextTokenReserved);
										continuousTokenFlag2.add(nextTokenType);
										continuousTokenGroup = continuousTokenGroup + " " + "QWORD PTR[" + nextToken + "+" + nextTokenReserved + "]";
									}
									
									//Eger hicbiri bulunamadiysa hata var demektir.
									else
									{
										errors.addError(eList.GetError(eList.UNEXPECTED_OPERAND));//Kuyruga yeni hatayi at.
										result = false;
										break;
									}
									
									//Buraya geldiyse birinden biri bulundu demektir.
									continuousTokenLoopCounter--;
									expectingComma = true;
									
									if(continuousTokenLoopCounter == 0)//Eger operand sayisi bittiyse
									{
										//Eger operand sayisi Global.maxOperandOfInstruction'dan buyukse hata var demektir.
										//Looplar arasinda her bir isleneni flag arraylerine attik.
										if(continuousTokenFlag1.size() != continuousTokenFlag2.size())
										{
											errors.addError(eList.GetError(eList.SYSTEM_ERROR_1));//Kuyruga yeni hatayi at.
											result = false;
											break;
										}
										else if(continuousTokenFlag1.size() > Global.maxOperandOfInstruction)
										{
											errors.addError(eList.GetError(eList.OPERAND_LIMIT_EXCEEDED));//Kuyruga yeni hatayi at.
											result = false;
											break;
										}
										else
										{
											int operandCount = continuousTokenFlag1.size();
											if(operandCount == 1)
											{
												fragments.add(new Instruction(continuousTokenGroup, continuousTokenExtraFlagStr, continuousTokenFlag1.get(0), continuousTokenFlag2.get(0)));//Buyrugu ekle.
											}
											else if(operandCount == 2)
											{
												fragments.add(new Instruction(continuousTokenGroup, continuousTokenExtraFlagStr, continuousTokenFlag1.get(0), continuousTokenFlag2.get(0), continuousTokenFlag1.get(1), continuousTokenFlag2.get(1)));//Buyrugu ekle.
											}
											
											ResetFragmentation();
										}
									}
								}
							}
							else//Eger continuousTokenLoopCounter 0'den buyukse ve ayni baglamdaki degiskenler sifirlanmadiysa
							{
								//Bu degiskenleri sifirlayalim.
								ResetFragmentation();
							}
						}
						//Eger continuousTokenType variable gosteriyorsa
						else if(continuousTokenType == Global.variable)
						{
							//Eger variable tanimlanma dongusunun henuz ilk asamasiysa
							if(continuousTokenLoopCounter == 1)
							{
								//Token db, dw gibi bir direktif olmalidir.
								if(nextTokenType == Global.initializerByte || nextTokenType == Global.initializerSByte || nextTokenType == Global.initializerDWord || nextTokenType == Global.initializerSDWord || nextTokenType == Global.initializerWord || nextTokenType == Global.initializerSWord)	
								{
									continuousTokenExtraFlagInt = nextTokenType;
									continuousTokenGroup = continuousTokenGroup + " " + nextToken;
									continuousTokenLoopCounter++;
									expectingSpace = true;
								}
								else
								{
									errors.addError(eList.GetError(eList.UNEXPECTED_VARIABLE_SIZE_DEFINER));//Kuyruga yeni hatayi at.
									result = false;
									break;
								}
							}
							//Eger variable tanimlanma dongusunun ikinci asamasiysa
							else if(continuousTokenLoopCounter == 2)
							{
								//Token sayi olabilir.
								if(nextTokenType == Global.binaryNumber || nextTokenType == Global.hexadecimalNumber || nextTokenType == Global.decimalNumber || nextTokenType == Global.questionMark)
								{
									continuousTokenFlag1.add(nextToken);
									continuousTokenFlag2.add(nextTokenType);
									continuousTokenGroup = continuousTokenGroup + " " + nextToken;
									continuousTokenLoopCounter++;
									expectingSpace = true;
									expectingComma = true;
								}	
								//Son ihtimal string olabilir.
								else if(nextTokenType == Global.string)
								{
									continuousTokenFlag1.add(nextToken);
									continuousTokenFlag2.add(nextTokenType);
									continuousTokenGroup = continuousTokenGroup + " " + "'" + nextToken + "'";
									continuousTokenLoopCounter++;
									expectingSpace = true;
									expectingComma = true;
								}
								else
								{
									errors.addError(eList.GetError(eList.UNEXPECTED_VARIABLE_NUMBER));//Kuyruga yeni hatayi at.
									result = false;
									break;
								}
							}
							//Ucuncu variable asamasi olmayabilir.
							//Eger bu asama varsa DUP ya da array default degerleri olabilir.
							else if(continuousTokenLoopCounter == 3)
							{
								if(nextTokenType == Global.dupOperatorNumber || nextTokenType == Global.dupOperatorString || nextTokenType == Global.dupOperatorQuestion)
								{
									continuousTokenGroup = continuousTokenGroup + " " + "DUP(" + nextToken + ")";
									fragments.add(new Variable(continuousTokenGroup, continuousTokenExtraFlagStr, continuousTokenExtraFlagInt, continuousTokenFlag1.lastElement(), continuousTokenFlag2.lastElement(), nextToken, nextTokenType));//Degiskeni ekle.
									ResetFragmentation();
								}
								else if(nextTokenType == Global.binaryNumber || nextTokenType == Global.hexadecimalNumber || nextTokenType == Global.decimalNumber || nextTokenType == Global.string || nextTokenType == Global.questionMark)
								{
									continuousTokenFlag1.add(nextToken);
									continuousTokenFlag2.add(nextTokenType);
									continuousTokenGroup = continuousTokenGroup + "," + nextToken;
									continuousTokenLoopCounter++;
									expectingComma = true;
								}
								else//Eger hicbiri degilse normal bir degisken tanimlanmasidir.
								{
									fragments.add(new Variable(continuousTokenGroup, continuousTokenExtraFlagStr, continuousTokenExtraFlagInt, continuousTokenFlag1, continuousTokenFlag2));//Degiskeni ekle.
									ResetFragmentation();
									
									//Sonraki token alinmasin, bu tekrar islensin.
									getTheNextToken = false;
								}
							}
							//Eger array ise
							else if(continuousTokenLoopCounter > 3)
							{
								if(nextTokenType == Global.binaryNumber || nextTokenType == Global.hexadecimalNumber || nextTokenType == Global.decimalNumber || nextTokenType == Global.string || nextTokenType == Global.questionMark)
								{
									continuousTokenFlag1.add(nextToken);
									continuousTokenFlag2.add(nextTokenType);
									continuousTokenGroup = continuousTokenGroup + "," + nextToken;
									continuousTokenLoopCounter++;
									expectingComma = true;
								}
								else//Array bittiyse
								{
									fragments.add(new Variable(continuousTokenGroup, continuousTokenExtraFlagStr, continuousTokenExtraFlagInt, continuousTokenFlag1, continuousTokenFlag2));//Degiskeni ekle.
									ResetFragmentation();
									
									//Sonraki token alinmasin, bu tekrar islensin.
									getTheNextToken = false;
								}
							}
						}
						//Eger continuousTokenType'lik bir durum yoksa
						else if(nextTokenType == Global.instruction)//Egerki nextToken buyruk opcode'u ise
						{
							//Eger bulunan opcode'un operand'i yoksa
							if(SupportedInstructions.get().InstructionOperandCount(nextToken) == 0)
							{
								fragments.add(new Instruction(nextToken,nextToken));//Buyrugu ekle.
							}
							else//Eger operand varsa
							{
								continuousTokenGroup = nextToken;
								continuousTokenType = Global.instruction;
								continuousTokenExtraFlagStr = nextToken;
								continuousTokenLoopCounter = SupportedInstructions.get().InstructionOperandCount(nextToken);
								expectingSpace = true;
							}
						}
						//Eger nextToken direktifse ve code segmentini isaret ediyorsa
						else if(nextTokenType == Global.directiveCode)
						{
							fragments.add(new Directive("." + nextToken, Global.directiveCode));//Direktifi ekle.
							expectingSpace = true;
						}
						//Eger nextToken direktifse ve model segmentini isaret ediyorsa
						else if(nextTokenType == Global.directiveModel)
						{
							fragments.add(new Directive("." + nextToken + " " + nextTokenReserved, Global.directiveModel, nextTokenReserved));//Direktifi ekle.
							expectingSpace = true;
						}
						//Eger nextToken direktifse ve data segmentini isaret ediyorsa
						else if(nextTokenType == Global.directiveData)
						{
							fragments.add(new Directive("." + nextToken, Global.directiveData));//Direktifi ekle.
							expectingSpace = true;
						}
						//Eger nextToken direktifse ve stack segmentini isaret ediyorsa
						else if(nextTokenType == Global.directiveStack)
						{
							fragments.add(new Directive("." + nextToken + " " + nextTokenReserved, Global.directiveStack, nextTokenReserved));//Direktifi ekle.
							expectingSpace = true;
						}
						//Eger nextToken direktifse ve startup segmentini isaret ediyorsa
						else if(nextTokenType == Global.directiveStartup)
						{
							fragments.add(new Directive("." + nextToken, Global.directiveStartup));//Direktifi ekle.
							expectingSpace = true;
						}
						//Eger nextToken direktifse ve exit segmentini isaret ediyorsa
						else if(nextTokenType == Global.directiveExit)
						{
							fragments.add(new Directive("." + nextToken, Global.directiveExit));//Direktifi ekle.
							expectingSpace = true;
						}
						//Eger nextToken end direktifi ise
						else if(nextTokenType == Global.directiveEnd)
						{
							fragments.add(new Directive("." + nextToken + " " + nextTokenReserved, Global.directiveEnd, nextTokenReserved));//Direktifi ekle.
							expectingSpace = true;
						}
						//Eger nextToken variable ise
						else if(nextTokenType == Global.variable)
						{
							continuousTokenGroup = nextToken;
							continuousTokenType = Global.variable;
							continuousTokenExtraFlagStr = nextToken;
							continuousTokenLoopCounter = 1;//En az iki kez loop edilmesi gerek.
							expectingSpace = true;
						}
						//Eger nextToken Label tanimlanmasi ise, ornegin Burak: ...
						else if(nextTokenType == Global.label)
						{
							fragments.add(new Label(nextToken + ":", nextToken));//Label'i ekle
						}
					}
				}
				if(getTheNextToken == true)
				{
					ResetToken();//Token degiskenlerini sifirlayalim.
				}
			}
			ResetToken();//Token degiskenlerini sifirlayalim.
			ResetFragmentation();
		}
		return result;
	}
	
	//Verilen string icerisinde besembly token tanimina uygun bir sonraki terimi nextToken'a 
	//Token'in tipini nextTokenType'a atar.
	//Analiz sirasindaki satir donulur.
	//Token'dan sonraki string donulur.
	private int nextTokenType = -1;
	private String nextToken = null;
	private String nextTokenReserved = null;
	private String NextToken(String input)
	{
		String result = input;//Eger hata olusursa girdinin aynen cikmasi icin kendisini sonuca kopyalayalim.
		
		//Eger input bos degilse
		if(input.length() > 0)
		{
			//Eger sonraki token end of line karakteri ise \n
			if(input.charAt(0) == '\n')
			{
				//Satir basina gec ve recursive olarak yeniden tarama yap.
				Global.currentLine++;//Analiz edilen satir degiskenini bir artiralim.
				nextToken = "\n";
				result = input.substring(1);
				nextTokenType = Global.newLine;
			}	
			//Eger string'in startsWith'i space'e tekabul ediyorsa
			else if(input.charAt(0) == ' ' || input.charAt(0) == '\t')
			{
				nextToken = " ";
				result = input.substring(1);
				nextTokenType = Global.space;
			}
			//Eger sonraki token virgul ise
			else if(input.charAt(0) == ',')
			{
				nextToken = ",";
				result = input.substring(1);
				nextTokenType = Global.comma;
			}
			//? compile'da degil runtime'da initialize edilecek olan, 
			//fakat bellekte kendileri icin yer ayrilan degiskenler icin kullanilir.
			else if(input.charAt(0) == '?')
			{
				nextToken = "?";
				result = input.substring(1);
				nextTokenType = Global.questionMark;
			}
			
			//COMMENT buyruksusunu kullanarak COMMENT [delimiter] yorum [delimiter] seklinde coklusatir yorumlama yapabiliriz.
			else if(input.startsWith("COMMENT") || input.startsWith("comment") )
			{
				String tmp = input.substring(7);//COMMENT'in hemen sonrasini kopyalayalim.
				if(tmp.charAt(0) != ' ' && tmp.charAt(0) != '\n' && tmp.charAt(0) != '\t')//Eger COMMENT'dan hemen sonra bosluk kullanilmamissa
				{	
					ResetToken();
					errors.addError(eList.GetError(eList.INSTRUCTIVE_COMMENT_IS_WRONGLY_USED));//Kuyruga yeni hatayi at.
				}
				else//Bosluk yada yeni satir kullanildiysa
				{
					//COMMENT'den hemen sonra kullanici istedigi kadar bosluk yada yeni satir karakteri kullanabilir.
					int unnecessaryPart = 0;
					char nextChar = tmp.charAt(0);
					boolean exitLoop = false;
					while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
					{
						if(nextChar == '\n')
						{
							Global.currentLine++;
						}
						unnecessaryPart++;
						if(unnecessaryPart < tmp.length())
						{
							nextChar = tmp.charAt(unnecessaryPart);
						}
						else
						{
							exitLoop = true;
						}
					}
					tmp = input.substring(unnecessaryPart+1);

					String delimiter = tmp.substring(0,1);//Delimiter olmasi gereken karakteri kopyalayalim.
					if(delimiter.matches("[0-9a-zA-Z]+") == false && delimiter.matches("[?.@_$%]+") == false)//Delimiter hataliysa
					{
						ResetToken();
						errors.addError(eList.GetError(eList.INSTRUCTIVE_COMMENT_ILLEGAL_DELIMITER));//Kuyruga yeni hatayi at.
					}
					else//Delimiter hatali degilse
					{
						tmp = tmp.substring(1);//Delimiter'dan sonraki kismi kopyalayalim.
						if(tmp.charAt(0) != ' ' && tmp.charAt(0) != '\n' && tmp.charAt(0) != '\t' && tmp.startsWith(delimiter) == false)//Eger delimiter tek karakter degilse
						{
							ResetToken();
							errors.addError(eList.GetError(eList.INSTRUCTIVE_COMMENT_WRONG_LENGTH));//Kuyruga yeni hatayi at.
						}
						else//Delimiter'dan sonra bosluk, yeni satir yada bitirme delimiter'i kullanildiysa
						{
							//Yorum kismi baslamis demektir.
							int endOfComment = tmp.indexOf(delimiter);
							if(endOfComment == -1)//Eger delimiter tekrar kullanilmadiysa
							{
								ResetToken();
								errors.addError(eList.GetError(eList.INSTRUCTIVE_COMMENT_DELIMITER_MISSING));//Kuyruga yeni hatayi at.
							}
							else//Delimiter kullanildiysa
							{
								//Eger COMMENT kullanimi sirasinda satir atlanildiysa
								for(int x=0; x<endOfComment; x++)
								{
									if(tmp.charAt(x) == '\n')//Eger new line'a rastlanildiysa
									{
										Global.currentLine++;
									}
								}
								
								nextToken = tmp.substring(0,endOfComment);
								nextTokenType = Global.commentCOMMENT;
								result = tmp.substring(endOfComment+1);								
							}
						}
					}
				}
			}
			//Eger sonraki token yorum tag'i ise
			else if(input.startsWith("/*"))
			{
				String tmp = input.substring(2);

				int closingTag = tmp.indexOf("*/");
				if(closingTag == -1)//Eger yorum tag'i acildi ve kapatilmadiysa
				{
					ResetToken();
					errors.addError(eList.GetError(eList.COMMENT_LINE_NOT_CLOSED));//Kuyruga yeni hatayi at.
				}
				else//Eger kapatildiysa
				{
					//Eger /* */ arasinda satir atlanildiysa
					for(int x=0; x<closingTag; x++)
					{
						if(tmp.charAt(x) == '\n')//Eger new line'a rastlanildiysa
						{
							Global.currentLine++;
						}
					}

					//Token /* ile */ arasindaki yorumu doner.
					nextToken = tmp.substring(0,closingTag);
					nextTokenType = Global.commentSlashStar;
					result = tmp.substring(closingTag+1);
				}
			}
			//Eger */ ile yorum satiri kapatilmaya calisiliyor fakat /* ile henuz acilmadiysa.
			else if(input.startsWith("*/"))
			{
				ResetToken();
				errors.addError(eList.GetError(eList.COMMENT_LINE_NOT_OPENED));//Kuyruga yeni hatayi at.
			}
			else if(input.startsWith("//"))
			{
				String tmp = input.substring(2);
				int closingTag = tmp.indexOf('\n');//Sonraki satir basini ara
				if(closingTag == -1)//Eger tek yorum satiri var ise
				{
					ResetToken();
					errors.addError(eList.GetError(eList.CONTENT_CONTAINS_ONLY_COMMENT));//Kuyruga yeni hatayi at.
				}
				else//Eger baska satirlar varsa
				{
					//Token // ile baslayan satiri doner.
					nextToken = tmp.substring(0,closingTag);
					nextTokenType = Global.commentSlashSlash;
					result = tmp.substring(closingTag+1);
					Global.currentLine++;
				}
			}
			else if(input.charAt(0) == '#')
			{
				String tmp = input.substring(1);
				int closingTag = tmp.indexOf('\n');//Sonraki satir basini ara
				if(closingTag == -1)//Eger tek yorum satiri var ise
				{
					ResetToken();
					errors.addError(eList.GetError(eList.CONTENT_CONTAINS_ONLY_COMMENT));//Kuyruga yeni hatayi at.
				}
				else//Eger baska satirlar varsa
				{
					//Token # ile baslayan satiri doner.
					nextToken = tmp.substring(0,closingTag);
					nextTokenType = Global.commentSharp;
					result = tmp.substring(closingTag+1);
					Global.currentLine++;
				}
			}
			else if(input.charAt(0) == ';')
			{
				String tmp = input.substring(1);
				int closingTag = tmp.indexOf('\n');//Sonraki satir basini ara
				if(closingTag == -1)//Eger tek yorum satiri var ise
				{
					ResetToken();
					errors.addError(eList.GetError(eList.CONTENT_CONTAINS_ONLY_COMMENT));//Kuyruga yeni hatayi at.
				}
				else//Eger baska satirlar varsa
				{
					//Token ; ile baslayan satiri doner.
					nextToken = tmp.substring(0,closingTag);
					nextTokenType = Global.commentSemiColon;
					result = tmp.substring(closingTag+1);
					Global.currentLine++;
				}
			}
			//Eger @ ile basliyorsa segment initialization olabilir.
			else if(input.charAt(0) == '@')
			{
				String tmp = input.substring(1);//Noktadan sonraki kismi kopyalayalim.
				if(tmp.startsWith("data") || tmp.startsWith("DATA"))
				{
					nextToken = "DATA";
					nextTokenType = Global.segmentInitializion;
					result = tmp.substring(4);
				}
				else
				{
					ResetToken();
					errors.addError(eList.GetError(eList.WRONG_SEGMENT_INITIALIZION));//Kuyruga yeni hatayi at.
				}
			}
			//Eger nokta ile basliyorsa direktif olabilir.
			else if(input.charAt(0) == '.')
			{
				String tmp = input.substring(1);//Noktadan sonraki kismi kopyalayalim.
				if(tmp.startsWith("code") || tmp.startsWith("CODE"))
				{
					nextToken = "CODE";
					nextTokenType = Global.directiveCode;
					result = tmp.substring(4);
				}
				else if(tmp.startsWith("model") || tmp.startsWith("MODEL"))
				{
					String nextField = tmp.substring(5);
					
					//Model'den sonraki kisim;
					//SMALL, LARGE
					//Degerlerinden birisini almak zorundadir.
					char nextChar = nextField.charAt(0);
					int unnecessaryPart = 0;
					boolean exitLoop = false;
					while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
					{
						if(nextChar == '\n')
						{
							Global.currentLine++;
						}
						unnecessaryPart++;
						if(unnecessaryPart < nextField.length())
						{
							nextChar = nextField.charAt(unnecessaryPart);
						}
						else
						{
							exitLoop = true;
						}
					}
					nextField = nextField.substring(unnecessaryPart);
					
					//Gereksiz kisim tiraslandi. Simdi sonraki kelime 
					//SMALL, LARGE
					//Seceneklerinden birisi mi diye kontrol edecegiz.
					boolean found = true;
					if(nextField.startsWith("SMALL") || nextField.startsWith("small"))
					{
						nextTokenReserved = "SMALL";
						result = nextField.substring(5);
					}
					else if(nextField.startsWith("LARGE") || nextField.startsWith("large"))
					{
						nextTokenReserved = "LARGE";
						result = nextField.substring(5);
					}
					else
					{
						found = false;
						ResetToken();
						errors.addError(eList.GetError(eList.WRONG_MODEL_DIRECTIVE));//Kuyruga yeni hatayi at.
					}
					
					if(found == true)
					{
						nextToken = "MODEL";
						nextTokenType = Global.directiveModel;
					}
				}
				else if(tmp.startsWith("stack") || tmp.startsWith("STACK"))
				{
					String nextField = tmp.substring(5);
					
					//Stack'ten sonraki kisim yalnizca sayi olabilir.
					char nextChar = nextField.charAt(0);
					int unnecessaryPart = 0;
					boolean exitLoop = false;
					while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
					{
						if(nextChar == '\n')
						{
							Global.currentLine++;
						}
						unnecessaryPart++;
						if(unnecessaryPart < nextField.length())
						{
							nextChar = nextField.charAt(unnecessaryPart);
						}
						else
						{
							exitLoop = true;
						}
					}
					nextField = nextField.substring(unnecessaryPart);
					
					//Simdi de sonraki kismin bir sonraki bosluk veya satir basina kadar ne kadar devam ettigine bakacagiz.
					nextChar = nextField.charAt(0);
					int necessaryPart = 0;
					exitLoop = false;
					while((nextChar != ' ' && nextChar != '\n' && nextChar != '\t') && exitLoop == false)
					{
						necessaryPart++;
						if(necessaryPart < nextField.length())
						{
							nextChar = nextField.charAt(necessaryPart);
						}
						else
						{
							exitLoop = true;
						}
					}
					String field = nextField.substring(0, necessaryPart);
					
					//Eger dogru kullanildiysa
					if(IsTokenValidNumeric(field, false) != -1)
					{
						nextToken = "STACK";
						nextTokenType = Global.directiveStack;
						nextTokenReserved = field;
						result = nextField.substring(necessaryPart);
					}
					else
					{
						ResetToken();
						errors.addError(eList.GetError(eList.WRONG_STACK_DIRECTIVE));//Kuyruga yeni hatayi at.
					}
				}
				else if(tmp.startsWith("data") || tmp.startsWith("DATA"))
				{
					nextToken = "DATA";
					nextTokenType = Global.directiveData;
					result = tmp.substring(4);
				}
				else if(tmp.startsWith("startup") || tmp.startsWith("STARTUP"))
				{
					nextToken = "STARTUP";
					nextTokenType = Global.directiveStartup;
					result = tmp.substring(7);
				}
				else if(tmp.startsWith("exit") || tmp.startsWith("EXIT"))
				{
					nextToken = "EXIT";
					nextTokenType = Global.directiveExit;
					result = tmp.substring(4);
				}
			}
			
			//Eger offset tanimlaniyorsa
			else if(input.startsWith("offset") || input.startsWith("OFFSET"))
			{
				String tmp = input.substring(6);
				
				char nextChar = tmp.charAt(0);
				int unnecessaryPart = 0;
				boolean exitLoop = false;
				while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
				{
					if(nextChar == '\n')
					{
						Global.currentLine++;
					}
					unnecessaryPart++;
					if(unnecessaryPart < tmp.length())
					{
						nextChar = tmp.charAt(unnecessaryPart);
					}
					else
					{
						exitLoop = true;
					}
				}
				tmp = tmp.substring(unnecessaryPart);
				//Gereksiz kisim tiraslandi.
				
				int firstSpace = tmp.indexOf(' ');
				int firstTab = tmp.indexOf('\t');
				if((firstTab < firstSpace && firstTab != -1) || (firstSpace < firstTab && firstSpace == -1))
				{
					firstSpace = firstTab;
				}
				
				int firstEndOfLine = tmp.indexOf('\n');

				if(firstSpace != -1 || firstEndOfLine != -1)//Birisinden birisinin bulunmus olmasi gerekiyor.
				{
					int len = -1;
					if(firstSpace < firstEndOfLine && firstSpace != -1)//Eger bosluk ile sonu belirtildiyse
					{
						len = firstSpace;
					}
					else if(firstEndOfLine < firstSpace && firstEndOfLine == -1)
					{
						len = firstSpace;
					}
					else if(firstEndOfLine < firstSpace && firstEndOfLine != -1)//Eger satir ile sonu belirtildiyse
					{
						len = firstEndOfLine;
					}
					else if(firstSpace < firstEndOfLine && firstSpace == -1)
					{
						len = firstEndOfLine;
					}
					
					String tmpVal = tmp.substring(0, len);
					
					boolean isErrorDetected = false;
					
					if(tmpVal.length() < 1 && tmpVal.length() > 31)//Label ismi 1 ile 31 karakter arasinda olabilir.
					{
						errors.addError(eList.GetError(eList.VARIABLE_NAME_LENGTH_IS_INCORRECT));//Kuyruga yeni hatayi at.
						isErrorDetected = true;
					}
					if(Character.isDigit(tmpVal.charAt(0)))//Degisken ismi sayi ile baslayamaz.
					{
						errors.addError(eList.GetError(eList.VARIABLE_NAME_STARTS_WITH_NUMBER));//Kuyruga yeni hatayi at.
						isErrorDetected = true;
					}
					//Degisken isminde nokta varsa bu yalnizca en basta olabilir.
					int dotIndex = tmpVal.lastIndexOf('.');
					if(dotIndex != -1 && dotIndex != 1)//Eger Degisken bulunduysa ve bu da ilk karakterde degilse
					{
						errors.addError(eList.GetError(eList.VARIABLE_NAME_STARTS_WITH_NUMBER));//Kuyruga yeni hatayi at.
						isErrorDetected = true;
					}
					//Degisken ismi ozel karakter olarak yalnizca ? .  @  _  $  % icerebilir.
					if(tmpVal.matches("[0-9a-zA-Z]+") == false && tmpVal.matches("[?.@_$%]+") == false)
					{
						errors.addError(eList.GetError(eList.VARIABLE_NAME_CONTAINS_ILLEGAL_CHARACTER));//Kuyruga yeni hatayi at.
						isErrorDetected = true;
					}
					
					if(isErrorDetected == false)
					{
						nextToken = tmpVal;
						nextTokenType = Global.offset;
						result = tmp.substring(len);
					}
					else//Eger hata varsa
					{
						ResetToken();
					}
				}
				else
				{
					ResetToken();
					errors.addError(eList.GetError(eList.WRONG_OFFSET_USAGE));//Kuyruga yeni hatayi at.
				}
			}
			
			//Eger BYTE PTR, WORD PTR, DWORD PTR ya da QWORD PTR direktiflerinden birisi ise
			else if(input.startsWith("BYTE PTR") || input.startsWith("WORD PTR") || input.startsWith("DWORD PTR") || input.startsWith("QWORD PTR") ||
					input.startsWith("byte ptr") || input.startsWith("word ptr") || input.startsWith("dword ptr") || input.startsWith("qword ptr"))
			{
				int tmpDirectiveType = -1;
				String tmp = null;
				if(input.startsWith("BYTE PTR") || input.startsWith("byte ptr"))
				{
					tmp = input.substring(8);
					tmpDirectiveType = Global.bytePtr;
				}
				else if(input.startsWith("WORD PTR") || input.startsWith("word ptr"))
				{
					tmp = input.substring(8);
					tmpDirectiveType = Global.wordPtr;
				}
				else if(input.startsWith("DWORD PTR") || input.startsWith("dword ptr"))
				{
					tmp = input.substring(9);
					tmpDirectiveType = Global.dWordPtr;
				}
				else if(input.startsWith("QWORD PTR") || input.startsWith("qword ptr"))
				{
					tmp = input.substring(9);
					tmpDirectiveType = Global.qWordPtr;
				}
				
				char nextChar = tmp.charAt(0);
				int unnecessaryPart = 0;
				boolean exitLoop = false;
				while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
				{
					if(nextChar == '\n')
					{
						Global.currentLine++;
					}
					unnecessaryPart++;
					if(unnecessaryPart < tmp.length())
					{
						nextChar = tmp.charAt(unnecessaryPart);
					}
					else
					{
						exitLoop = true;
					}
				}
				tmp = tmp.substring(unnecessaryPart);
				//Gereksiz kisim tiraslandi.
				
				//Indirect adresleme direktifi kullanildiysa bosluktan sonra [ kullanilmasi gereklidir.
				if(tmp.charAt(0) == '[')
				{
					String insideOfAddressTags = tmp.substring(1);
					tmp = insideOfAddressTags;//Sonraki karakterden itibaren tutmaya baslayalim.
					int tagClosing = tmp.indexOf(']');
					if(tagClosing == -1)//Eger acilan tag kapatilmadiysa
					{
						ResetToken();
						errors.addError(eList.GetError(eList.ADDRESS_TAG_IS_NOT_CLOSED));//Kuyruga yeni hatayi at.
					}
					else//Eger kapatildiysa
					{
						tmp = tmp.substring(0, tagClosing);//[ ile ] arasindaki sayilari kopyala.
						tmp = tmp.toUpperCase();//Kiyaslama yapmak uzere icerigi upper-case yapalim.
						
						int found = -1;
						for(int k=0; k<Global.registerList.length; k++)
						{
							if(Global.registerList[k].equals(tmp))
							{
								found = k;
								break;
							}
						}
						
						if(found == -1)//Eger [ ve ] icerisindeki token dogrudan bir yazmaca karsilik gelmediyse
						{
							//Eger yazmac degilse fakat dogrudan address ise
							if(IsTokenValidNumeric(tmp,false) != -1)
							{
								nextToken = tmp;
								if(tmpDirectiveType == Global.bytePtr)
								{
									nextTokenType = Global.bytePtrOnlyAddress;
								}
								else if(tmpDirectiveType == Global.wordPtr)
								{
									nextTokenType = Global.wordPtrOnlyAddress;
								}
								else if(tmpDirectiveType == Global.dWordPtr)
								{
									nextTokenType = Global.dWordPtrOnlyAddress;
								}
								else if(tmpDirectiveType == Global.qWordPtr)
								{
									nextTokenType = Global.qWordPtrOnlyAddress;
								}
								nextTokenReserved = null;
								
								result = insideOfAddressTags.substring(tagClosing+1);
							}
							else if(tmp.contains("+"))//Eger + karakteri barindiriyorsa
							{
								//Eger birden fazla + kullanildiysa
								if(Global.CountOccurrences(tmp, '+') > 1)
								{
									ResetToken();
									errors.addError(eList.GetError(eList.TOO_MUCH_PLUS_OCCURRENCE_BETWEEN_ADDRESS_TAGS));//Kuyruga yeni hatayi at.
								}
								else//Eger bir tek + kullanildiysa
								{
									int plusPosition = tmp.indexOf('+');//+'nin yer aldigi index'i tutalim.
									
									//[  AX   + 1234H] gibi kullanilabilir. Formlarini hesaba katalim.
									unnecessaryPart = 0;
									exitLoop = false;
									nextChar = tmp.charAt(0);
									while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
									{
										if(nextChar == '\n')
										{
											Global.currentLine++;
										}
										unnecessaryPart++;
										if(unnecessaryPart < tmp.length())
										{
											nextChar = tmp.charAt(unnecessaryPart);
										}
										else
										{
											exitLoop = true;
										}
									}
									//['den sonra gereksiz karakterlerden sonra anlam ifade eden kismi kaydedelim.
									tmp = tmp.substring(unnecessaryPart);
									
									//tmp'i gereksiz part kadar ileri goturdugumuz icin artinin pozisyonunu da geri cekmeliyiz.
									String beforePlus = tmp.substring(0, plusPosition-unnecessaryPart);//Artidan onceki kismi aldik.
									
									//Simdi de sondan itibaren beforePlus'i tiraslayalim.
									unnecessaryPart = 0;
									exitLoop = false;
									int beforePlusLen = beforePlus.length();
									nextChar = tmp.charAt(beforePlusLen-1);//beforePlus'in son karakterinden baslayalim.
									while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
									{
										if(nextChar == '\n')
										{
											Global.currentLine++;
										}
										unnecessaryPart++;
										if((beforePlusLen-1-unnecessaryPart) > 0)
										{
											nextChar = tmp.charAt(beforePlusLen-1-unnecessaryPart);
										}
										else
										{
											exitLoop = true;
										}
									}
									//Bu islemden sonra beforePlus yalnizca onemli olan kismi tutar.
									beforePlus = beforePlus.substring(0,beforePlusLen-unnecessaryPart);
									
									//Eger beforePlus sayi degilse
									if(IsTokenValidNumeric(beforePlus,false) == -1)
									{
										int subFound = -1;
										
										beforePlus = beforePlus.toUpperCase();
										for(int n=0; n<Global.registerList.length; n++)
										{
											if(Global.registerList[n].equals(beforePlus))
											{
												subFound = n;
												break;
											}
										}
										
										if(subFound == -1)//Eger hatali adresleme yapildiysa
										{
											ResetToken();
											errors.addError(eList.GetError(eList.ILLEGAL_ADDRESING));//Kuyruga yeni hatayi at.
										}
										else//Eger bir yazmaca karsilik geldiyse
										{
											nextToken = beforePlus;
											//afterPlus henuz incelenmedigi icin gecici olarak onlyRegister olarak belirtiyoruz.
											if(tmpDirectiveType == Global.bytePtr)
											{
												nextTokenType = Global.bytePtrOnlyRegister;
											}
											else if(tmpDirectiveType == Global.wordPtr)
											{
												nextTokenType = Global.wordPtrOnlyRegister;
											}
											else if(tmpDirectiveType == Global.dWordPtr)
											{
												nextTokenType = Global.dWordPtrOnlyRegister;
											}
											else if(tmpDirectiveType == Global.qWordPtr)
											{
												nextTokenType = Global.qWordPtrOnlyRegister;
											}
											
											nextTokenReserved = null;
											
											result = insideOfAddressTags.substring(tagClosing);
										}
									}
									else//Eger beforePlus bir sayi ise
									{
										nextToken = beforePlus;
										//afterPlus henuz incelenmedigi icin gecici olarak onlyAddress olarak belirtiyoruz.
										if(tmpDirectiveType == Global.bytePtr)
										{
											nextTokenType = Global.bytePtrOnlyAddress;
										}
										else if(tmpDirectiveType == Global.wordPtr)
										{
											nextTokenType = Global.wordPtrOnlyAddress;
										}
										else if(tmpDirectiveType == Global.dWordPtr)
										{
											nextTokenType = Global.dWordPtrOnlyAddress;
										}
										else if(tmpDirectiveType == Global.qWordPtr)
										{
											nextTokenType = Global.qWordPtrOnlyAddress;
										}
										
										nextTokenReserved = null;
										
										result = insideOfAddressTags.substring(tagClosing);
									}
									
									//+'dan sonraki kismi alalim.
									String afterPlus = tmp.substring(plusPosition+1);
									
									//tmp'i gereksiz part kadar ileri goturdugumuz icin artinin pozisyonunu da geri cekmeliyiz.
									//String afterPlus = tmp.substring(0, tagClosing);//Artidan sonraki kismi aldik.
									
									//Simdi de sondan itibaren beforePlus'i tiraslayalim.
									unnecessaryPart = 0;
									exitLoop = false;
									int afterPlusLen = afterPlus.length();
									nextChar = afterPlus.charAt(0);//afterPlus'in ilk karakterinden baslayalim.
									while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
									{
										if(nextChar == '\n')
										{
											Global.currentLine++;
										}
										unnecessaryPart++;
										if(unnecessaryPart < afterPlus.length())
										{
											nextChar = afterPlus.charAt(unnecessaryPart);
										}
										else
										{
											exitLoop = true;
										}
									}
									//Bu islemden sonra onemli kisma kadar tum gereksiz karakterler temizlendi.
									afterPlus = afterPlus.substring(unnecessaryPart);
									//afterPlus'in son uzunlugunu kaydedelim.
									afterPlusLen = afterPlus.length();
									
									unnecessaryPart = 0;
									exitLoop = false;
									nextChar = afterPlus.charAt(afterPlusLen-1);//afterPlus'in son karakterinden baslayalim.
									while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
									{
										if(nextChar == '\n')
										{
											Global.currentLine++;
										}
										unnecessaryPart++;
										if((afterPlusLen-1-unnecessaryPart) > 0)
										{
											nextChar = afterPlus.charAt(afterPlusLen-1-unnecessaryPart);
										}
										else
										{
											exitLoop = true;
										}
									}
									//Bu islemden sonra afterPlus yalnizca onemli olan kismi tutar.
									afterPlus = afterPlus.substring(0,afterPlusLen-unnecessaryPart).toUpperCase();
									
									//Eger afterPlus sayi degilse
									if(IsTokenValidNumeric(afterPlus,false) == -1)
									{
										int subFound = -1;
										afterPlus = afterPlus.toUpperCase();
										for(int n=0; n<Global.registerList.length; n++)
										{
											if(Global.registerList[n].equals(afterPlus))
											{
												subFound = n;
												break;
											}
										}
										
										if(subFound == -1)//Eger hatali adresleme yapildiysa
										{
											ResetToken();
											errors.addError(eList.GetError(eList.ILLEGAL_ADDRESING));//Kuyruga yeni hatayi at.
										}
										else//Eger bir yazmaca karsilik geldiyse
										{
											if(tmpDirectiveType == Global.bytePtr)
											{
												if(nextTokenType == Global.bytePtrOnlyAddress)//Eger beforePlus Address ise
												{
													nextTokenType = Global.bytePtrAddressRegister;
												}
												else if(nextTokenType == Global.bytePtrOnlyRegister)//Eger beforePlus yazmac ise
												{
													nextTokenType = Global.bytePtrRegisterRegister;
												}
											}
											else if(tmpDirectiveType == Global.wordPtr)
											{
												if(nextTokenType == Global.wordPtrOnlyAddress)//Eger beforePlus Address ise
												{
													nextTokenType = Global.wordPtrAddressRegister;
												}
												else if(nextTokenType == Global.wordPtrOnlyRegister)//Eger beforePlus yazmac ise
												{
													nextTokenType = Global.wordPtrRegisterRegister;
												}
											}
											else if(tmpDirectiveType == Global.dWordPtr)
											{
												if(nextTokenType == Global.dWordPtrOnlyAddress)//Eger beforePlus Address ise
												{
													nextTokenType = Global.dWordPtrAddressRegister;
												}
												else if(nextTokenType == Global.dWordPtrOnlyRegister)//Eger beforePlus yazmac ise
												{
													nextTokenType = Global.dWordPtrRegisterRegister;
												}
											}
											else if(tmpDirectiveType == Global.qWordPtr)
											{
												if(nextTokenType == Global.qWordPtrOnlyAddress)//Eger beforePlus Address ise
												{
													nextTokenType = Global.qWordPtrAddressRegister;
												}
												else if(nextTokenType == Global.qWordPtrOnlyRegister)//Eger beforePlus yazmac ise
												{
													nextTokenType = Global.qWordPtrRegisterRegister;
												}
											}
											
											nextTokenReserved = afterPlus;
											result = insideOfAddressTags.substring(tagClosing+1);
										}
									}
									else//Eger afterPlus bir sayi ise
									{
										if(tmpDirectiveType == Global.bytePtr)
										{
											if(nextTokenType == Global.bytePtrOnlyAddress)//Eger beforePlus Address ise
											{
												nextTokenType = Global.bytePtrAddressAddress;
											}
											else if(nextTokenType == Global.bytePtrOnlyRegister)//Eger beforePlus yazmac ise
											{
												nextTokenType = Global.bytePtrRegisterAddress;
											}
										}
										else if(tmpDirectiveType == Global.wordPtr)
										{
											if(nextTokenType == Global.wordPtrOnlyAddress)//Eger beforePlus Address ise
											{
												nextTokenType = Global.wordPtrAddressAddress;
											}
											else if(nextTokenType == Global.wordPtrOnlyRegister)//Eger beforePlus yazmac ise
											{
												nextTokenType = Global.wordPtrRegisterAddress;
											}
										}
										else if(tmpDirectiveType == Global.dWordPtr)
										{
											if(nextTokenType == Global.dWordPtrOnlyAddress)//Eger beforePlus Address ise
											{
												nextTokenType = Global.dWordPtrAddressAddress;
											}
											else if(nextTokenType == Global.dWordPtrOnlyRegister)//Eger beforePlus yazmac ise
											{
												nextTokenType = Global.dWordPtrRegisterAddress;
											}
										}
										else if(tmpDirectiveType == Global.qWordPtr)
										{
											if(nextTokenType == Global.qWordPtrOnlyAddress)//Eger beforePlus Address ise
											{
												nextTokenType = Global.qWordPtrAddressAddress;
											}
											else if(nextTokenType == Global.qWordPtrOnlyRegister)//Eger beforePlus yazmac ise
											{
												nextTokenType = Global.qWordPtrRegisterAddress;
											}
										}
										
										nextTokenReserved = afterPlus;
										result = insideOfAddressTags.substring(tagClosing+1);
									}
								}
							}
							else//Eger [  yazmac    ] yada [    000100b  ] gibi bir ihtimal varsa
							{
								unnecessaryPart = 0;
								exitLoop = false;
								nextChar = tmp.charAt(0);
								while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
								{
									if(nextChar == '\n')
									{
										Global.currentLine++;
									}
									unnecessaryPart++;
									if(unnecessaryPart < tmp.length())
									{
										nextChar = tmp.charAt(unnecessaryPart);
									}
									else
									{
										exitLoop = true;
									}
								}
								tmp = tmp.substring(unnecessaryPart);
								
								found = -1;

								//Yazmac mi degil mi testi icin ilk 2 karakteri alalim.
								String tmpTest = tmp.substring(0, 2);
								tmpTest = tmpTest.toUpperCase();
								for(int k=0; k<Global.registerList.length; k++)
								{
									if(Global.registerList[k].equals(tmpTest))
									{
										found = k;
										break;
									}
								}
								
								//Eger yazmac bulunduysa
								if(found != -1)
								{
									String irregularRegister = Global.registerList[found];
									
									tmp = tmp.substring(irregularRegister.length());
									
									if(tmp.length() > 0)
									{
										//Yazmactan sonraki gereksiz kismi tiraslayalim.
										unnecessaryPart = 0;
										exitLoop = false;
										nextChar = tmp.charAt(0);
										while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
										{
											if(nextChar == '\n')
											{
												Global.currentLine++;
											}
											unnecessaryPart++;
											if(unnecessaryPart < tmp.length())
											{
												nextChar = tmp.charAt(unnecessaryPart);
											}
											else
											{
												exitLoop = true;
											}
										}
										tmp = tmp.substring(unnecessaryPart);
									}	
									//Eger tiraslamayi yaptiktan sonra da baska karakterler kaldiysa syntax hatasi var demektir.
									if(tmp.length() > 0)
									{
										ResetToken();
										errors.addError(eList.GetError(eList.UNEXPECTED_SYNTAX_ERROR));//Kuyruga yeni hatayi at.
									}
									else
									{
										nextToken = irregularRegister;
										if(tmpDirectiveType == Global.bytePtr)
										{
											nextTokenType = Global.bytePtrOnlyRegister;
										}
										else if(tmpDirectiveType == Global.wordPtr)
										{
											nextTokenType = Global.wordPtrOnlyRegister;
										}
										else if(tmpDirectiveType == Global.dWordPtr)
										{
											nextTokenType = Global.dWordPtrOnlyRegister;
										}
										else if(tmpDirectiveType == Global.qWordPtr)
										{
											nextTokenType = Global.qWordPtrOnlyRegister;
										}
										
										nextTokenReserved = null;
										
										result = insideOfAddressTags.substring(tagClosing+1);
									}
								}
								else//Eger yazmac bulunamadiysa
								{
									unnecessaryPart = 0;
									exitLoop = false;
									nextChar = tmp.charAt(0);
									while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
									{
										if(nextChar == '\n')
										{
											Global.currentLine++;
										}
										unnecessaryPart++;
										if(unnecessaryPart < tmp.length())
										{
											nextChar = tmp.charAt(unnecessaryPart);
										}
										else
										{
											exitLoop = true;
										}
									}
									tmp = tmp.substring(unnecessaryPart);
									
									int firstSpace = tmp.indexOf(' ');
									int firstTab = tmp.indexOf('\t');
									if((firstTab < firstSpace && firstTab != -1) || (firstSpace < firstTab && firstSpace == -1))
									{
										firstSpace = firstTab;
									}
									int firstEndOfLine = tmp.indexOf('\n');
									
									int tmpDelimiter = -1;
									if(firstSpace != -1 || firstEndOfLine != -1)
									{
										if(firstSpace < firstEndOfLine && firstSpace != -1)
										{
											tmpDelimiter = firstSpace;
										}
										else if(firstEndOfLine < firstSpace && firstEndOfLine == -1)
										{
											tmpDelimiter = firstSpace;
										}
										else if(firstEndOfLine < firstSpace && firstEndOfLine != -1)
										{
											tmpDelimiter = firstEndOfLine;
										}
										else if(firstSpace < firstEndOfLine && firstSpace == -1)
										{
											tmpDelimiter = firstEndOfLine;
										}
									}
									
									//Eger delimiter yoksa
									if(tmpDelimiter == -1)
									{
										//Eger address ise
										if(IsTokenValidNumeric(tmp,false) != -1)
										{
											nextToken = tmp;
											if(tmpDirectiveType == Global.bytePtr)
											{
												nextTokenType = Global.bytePtrOnlyAddress;
											}
											else if(tmpDirectiveType == Global.wordPtr)
											{
												nextTokenType = Global.wordPtrOnlyAddress;
											}
											else if(tmpDirectiveType == Global.dWordPtr)
											{
												nextTokenType = Global.dWordPtrOnlyAddress;
											}
											else if(tmpDirectiveType == Global.qWordPtr)
											{
												nextTokenType = Global.qWordPtrOnlyAddress;
											}
											result = insideOfAddressTags.substring(tagClosing+1);
										}
									}
									//Eger delimiter varsa
									else
									{							
										tmp = tmp.substring(0, tmpDelimiter);
										
										//Eger address ise
										if(IsTokenValidNumeric(tmp,false) != -1)
										{
											nextToken = tmp;
											if(tmpDirectiveType == Global.bytePtr)
											{
												nextTokenType = Global.bytePtrOnlyAddress;
											}
											else if(tmpDirectiveType == Global.wordPtr)
											{
												nextTokenType = Global.wordPtrOnlyAddress;
											}
											else if(tmpDirectiveType == Global.dWordPtr)
											{
												nextTokenType = Global.dWordPtrOnlyAddress;
											}
											else if(tmpDirectiveType == Global.qWordPtr)
											{
												nextTokenType = Global.qWordPtrOnlyAddress;
											}
											result = insideOfAddressTags.substring(tagClosing+1);
										}
										//Eger hicbiri olmuyorsa
										else
										{
											ResetToken();
											errors.addError(eList.GetError(eList.UNEXPECTED_SYNTAX_ERROR));//Kuyruga yeni hatayi at.
										}
									}
								}
							}
						}
						else//Dogrudan bir yazmaca tekabul ediyorsa
						{
							nextToken = tmp;
							if(tmpDirectiveType == Global.bytePtr)
							{
								nextTokenType = Global.bytePtrOnlyRegister;
							}
							else if(tmpDirectiveType == Global.wordPtr)
							{
								nextTokenType = Global.wordPtrOnlyRegister;
							}
							else if(tmpDirectiveType == Global.dWordPtr)
							{
								nextTokenType = Global.dWordPtrOnlyRegister;
							}
							else if(tmpDirectiveType == Global.qWordPtr)
							{
								nextTokenType = Global.qWordPtrOnlyRegister;
							}
							
							nextTokenReserved = null;
							
							result = insideOfAddressTags.substring(tagClosing+1);
						}
					}
				}
				//Eger [ ile baslamadiysa
				else
				{
					ResetToken();
					errors.addError(eList.GetError(eList.WRONG_INDIRECT_ADDRESSING_DIRECTIVE_USAGE));//Kuyruga yeni hatayi at.
				}
			}
	
			//DUP buyruksusunu kullanarak coklu degisken byte yada word atamasi yapilabilir.
			else if(input.startsWith("DUP") || input.startsWith("dup"))
			{
				//Normal kullanim DUP(5)'dir.
				//Fakat DUP    (    5        ) gibi kullanimlari da dusunmemiz gerekir.
				String tmp = input.substring(3);//DUP'dan hemen sonraki kismi alalim.
				
				int currentChar = 0;
				while(tmp.charAt(0) == ' ' || tmp.charAt(0) == '\n' || tmp.charAt(0) == '\t')
				{
					if(tmp.charAt(0) == '\n')//Eger satir atlandiysa
					{
						Global.currentLine++;
					}
					currentChar++;
					tmp = tmp.substring(currentChar);
				}
				//Artik elimizde (    5  ) gibi bir ifade yer almasi gerekiyor.
				
				if(tmp.charAt(0) != '(')//Eger ( ile baslamadiysa
				{
					ResetToken();
					errors.addError(eList.GetError(eList.DUP_IS_WRONGLY_USED));//Kuyruga yeni hatayi at.
				}
				else//Eger duzgun kullanildiysa
				{
					tmp = tmp.substring(1);// ('den sonraki kismi alalim.
					
					currentChar = 0;
					while(tmp.charAt(0) == ' ' || tmp.charAt(0) == '\n' || tmp.charAt(0) == '\t')
					{
						if(tmp.charAt(0) == '\n')//Eger satir atlandiysa
						{
							Global.currentLine++;
						}
						currentChar++;
						tmp = tmp.substring(currentChar);
					}
					//Artik elimizde bir sayi ve ) olmasi gerekiyor.
					
					int endOfParentheses = tmp.indexOf(')');
					
					//Eger DUP icin acilan parantez kapatilmadiysa
					if(endOfParentheses == -1)
					{
						ResetToken();
						errors.addError(eList.GetError(eList.DUP_PARENTHESES_IS_NOT_CLOSED));//Kuyruga yeni hatayi at.
					}
					//Eger parantez acildigi gibi kapatildiysa
					else if(endOfParentheses == 0)
					{
						ResetToken();
						errors.addError(eList.GetError(eList.DUP_PARENTHESES_IS_EMPTY));//Kuyruga yeni hatayi at.
					}
					else//Eger duzgun kapatildiysa
					{
						String insideOfParentheses = tmp.substring(0, endOfParentheses);
						
						char nextChar = insideOfParentheses.charAt(0);
						int unnecessaryPart = 0;
						boolean exitLoop = false;
						while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
						{
							if(nextChar == '\n')
							{
								Global.currentLine++;
							}
							unnecessaryPart++;
							if(unnecessaryPart < insideOfParentheses.length())
							{
								nextChar = insideOfParentheses.charAt(unnecessaryPart);
							}
							else
							{
								exitLoop = true;
							}
						}
						insideOfParentheses = insideOfParentheses.substring(unnecessaryPart);
						//Baslangictaki gereksiz kisim tiraslandi. 
						
						int firstSpace = insideOfParentheses.indexOf(' ');
						int firstTab = insideOfParentheses.indexOf('\t');
						if((firstTab < firstSpace && firstTab != -1) || (firstSpace < firstTab && firstSpace == -1))
						{
							firstSpace = firstTab;
						}
						int firstLine = insideOfParentheses.indexOf('\n');
						String possibleNumber = null;
						
						if(firstSpace < firstLine && firstSpace != -1)//Eger once bosluk geldiyse
						{
							possibleNumber = insideOfParentheses.substring(firstSpace);
						}
						else if(firstLine < firstSpace && firstLine == -1)
						{
							possibleNumber = insideOfParentheses.substring(firstSpace);
						}
						else if(firstLine < firstSpace && firstLine != -1)//Eger once yeni satir geldiyse
						{
							possibleNumber = insideOfParentheses.substring(firstLine);
						}
						else if(firstSpace < firstLine && firstSpace == -1)
						{
							possibleNumber = insideOfParentheses.substring(firstLine);
						}
						else//Eger ikisi de degilse hem firstSpace hem firstLine -1'dir, yani bulunamamistir.
						{
							possibleNumber = insideOfParentheses;
						}
						
						if(firstSpace != -1 || firstLine != -1)//Eger bosluk yada satir varsa
						{
							//Simdi possibleNumber'dan sonraki kisim bosluk yada yeni satirdan mi olusuyor kontrolu yapmamiz gerekiyor.
							String tmpControl = insideOfParentheses.substring(firstSpace);
							for (int b=0; b<tmpControl.length(); b++)
							{
								if(tmpControl.charAt(b) != ' ' && tmpControl.charAt(b) != '\n' && tmpControl.charAt(b) != '\t')
								{
									//Eger bosluk ya da yeni satirdan baska bir karakter varsa
									errors.addError(eList.GetError(eList.DUP_PARENTHESIS_INSIDE_CONTAINS_WRONG_CHAR));//Kuyruga yeni hatayi at.
									break;
								}
							}
						}
						
						//Artik possibleNumber'in numara olup olmadigini kontrol edebiliriz.
						if(IsTokenValidNumeric(possibleNumber, false) != -1)//Eger dogru kullanildiysa
						{
							nextToken = possibleNumber;
							nextTokenType = Global.dupOperatorNumber;
							result = tmp.substring(endOfParentheses+1);
						}
						else if(IsTokenValidString(possibleNumber, false) != null)
						{
							nextToken = IsTokenValidString(possibleNumber, false);
							nextTokenType = Global.dupOperatorString;
							result = tmp.substring(endOfParentheses+1);
						}
						else if(possibleNumber.equals("?"))
						{
							nextToken = possibleNumber;
							nextTokenType = Global.dupOperatorQuestion;
							result = tmp.substring(endOfParentheses+1);
						}
						else
						{
							ResetToken();
							errors.addError(eList.GetError(eList.DUP_PARENTHESIS_CONTAINS_NON_NUMERIC_VALUE));//Kuyruga yeni hatayi at.
						}
					}
				}
			}
			
			//Eger sonraki token buyruklar olabilirse
			else
			{
				//Buyruk operand'siz da olabilir. O zaman kullanici buyruktan hemen sonra bosluk koymak yerine
				//Yeni bir satira gecebilir. Bunun icin iki ihtimal dusunmemiz gerekiyor.
				boolean instructionFound = false;
				
				int firstSpace = input.indexOf(' ');
				int firstTab = input.indexOf('\t');
				if((firstTab < firstSpace && firstTab != -1) || (firstSpace < firstTab && firstSpace == -1))
				{
					firstSpace = firstTab;
				}
				int firstEndOfLine = input.indexOf('\n');

				if(firstSpace != -1 || firstEndOfLine != -1)//Birisinden birisinin bulunmus olmasi gerekiyor.
				{
					String[] possibleOpcode = new String[2];//Iki ihtimal var.
					
					if(firstSpace != -1) 
					{
						possibleOpcode[0] = input.substring(0,firstSpace);//Boslukla ayrilmis olasi buyrugu alalim.
					}
					else if(firstEndOfLine != -1)  
					{
						possibleOpcode[1] = input.substring(0,firstEndOfLine);//Yeni satirla ayrilmis olasi buyrugu alalim.
					}
					
					int whichIs = -1;
					
					if(firstSpace != -1)//Eger bosluk ile sonu belirtilen bir opcode ise
					{
						if(firstEndOfLine == -1 || firstSpace < firstEndOfLine)
						{
							whichIs = 0;
						}
					}
					else if(firstEndOfLine != -1)//Eger satir ile sonu belirtilen bir opcode ise 
					{
						if(firstSpace == -1 || firstEndOfLine < firstSpace)
						{
							whichIs = 1;
							Global.currentLine++;
						}
					}
					
					//Eger Opcode olabilecek uzunluga sahipse
					int opcLen = possibleOpcode[whichIs].length();
					if(opcLen >= SupportedInstructions.minLengthOfAnOpcode && opcLen <= SupportedInstructions.maxLengthOfAnOpcode)
					{
						if(possibleOpcode[whichIs].matches("[a-zA-Z]+"))//Buyruk opcode'lari yalnizca harf icerebilir.
						{
							for(int i=0; i<SupportedInstructions.get().InstructionCount(); i++)
							{
								String tmpOPC = possibleOpcode[whichIs].toUpperCase();//OPCODE'un karakterlerini buyuk harfe cevirelim.
								
								SupportedInstructions si = SupportedInstructions.get();
								if(si.isInstructionInList(tmpOPC))
								{
									//Eger aldigimiz kelime bir buyruk opcode'u ise ise
									nextToken = tmpOPC;
									nextTokenType = Global.instruction;
									instructionFound = true;
									if(whichIs == 0)
									{
										result = input.substring(firstSpace);
									}
									else
									{
										result = input.substring(firstEndOfLine);
									}
									break;
								}
							}
						}
					}
				}
				
				if(instructionFound == false)//Eger dongu icerisinde buyruk opcode'u bulunamadiysa
				{
					//Eger sonraki token label olabilirse
					if(input.length() > 2)//Label olabilmesi icin en az bir karakter ve bir : olmasi gerekir.
					{
						int firstColon = input.indexOf(':');
						if(firstColon != -1)//Eger bulunduysa
						{
							String tmpLabel = input.substring(0, firstColon);//Iki nokta ust uste'yi dahil etmememiz gerekir.
							
							int space = tmpLabel.indexOf(' ');
							int tab = tmpLabel.indexOf('\t');
							if(tab < space && tab != -1)
							{
								space = tab;
							}
							int line = tmpLabel.indexOf('\n');
							//Eger label ise baslangictan :'a kadar bosluk ya da yeni satir olmamali.
							if(space == -1 && line == -1)
							{
								//Label ismi besembly yazim kurallarina uymuyorsa
								boolean isErrorDetected = false;
								
								if(tmpLabel.length() < 1 && tmpLabel.length() > 31)//Label ismi 1 ile 31 karakter arasinda olabilir.
								{
									errors.addError(eList.GetError(eList.LABEL_NAME_LENGTH_IS_INCORRECT));//Kuyruga yeni hatayi at.
									isErrorDetected = true;
								}
								if(Character.isDigit(tmpLabel.charAt(0)))//Label ismi sayi ile baslayamaz.
								{
									errors.addError(eList.GetError(eList.LABEL_NAME_STARTS_WITH_NUMBER));//Kuyruga yeni hatayi at.
									isErrorDetected = true;
								}
								//Label isminde nokta varsa bu yalnizca en basta olabilir.
								int dotIndex = tmpLabel.lastIndexOf('.');
								if(dotIndex != -1 && dotIndex != 1)//Eger nokta bulunduysa ve bu da ilk karakterde degilse
								{
									errors.addError(eList.GetError(eList.LABEL_NAME_STARTS_WITH_NUMBER));//Kuyruga yeni hatayi at.
									isErrorDetected = true;
								}
								//Label ismi ozel karakter olarak yalnizca ? .  @  _  $  % icerebilir.
								if(tmpLabel.matches("[0-9a-zA-Z]+") == false && tmpLabel.matches("[?.@_$%]+") == false)
								{
									errors.addError(eList.GetError(eList.LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER));//Kuyruga yeni hatayi at.
									isErrorDetected = true;
								}
								
								if(isErrorDetected == false)
								{
									nextToken = tmpLabel;
									nextTokenType = Global.label;
									result = input.substring(firstColon+1);
								}
								else//Eger hata varsa
								{
									ResetToken();
								}
							}
						}
					}
				}
				
				if(nextTokenType == -1 || nextToken == null)//Eger yapilan islemler sonucunda nextToken degiskenleri default degerlere sahipse
				{
					if(input.charAt(0) == '[')//Eger yazmac'in tuttugu adresi ifade edebilme olanagi varsa
					{
						String tmp = input.substring(1);//Sonraki karakterden itibaren tutmaya baslayalim.
						int tagClosing = tmp.indexOf(']');
						if(tagClosing == -1)//Eger acilan tag kapatilmadiysa
						{
							ResetToken();
							errors.addError(eList.GetError(eList.ADDRESS_TAG_IS_NOT_CLOSED));//Kuyruga yeni hatayi at.
						}
						else//Eger kapatildiysa
						{
							tmp = tmp.substring(0, tagClosing);//[ ile ] arasindaki sayilari kopyala.
							tmp = tmp.toUpperCase();//Kiyaslama yapmak uzere icerigi upper-case yapalim.
							
							int found = -1;
							for(int k=0; k<Global.registerList.length; k++)
							{
								if(Global.registerList[k].equals(tmp))
								{
									found = k;
									break;
								}
							}
							
							if(found == -1)//Eger [ ve ] icerisindeki token dogrudan bir yazmaca karsilik gelmediyse
							{
								//Eger yazmac degilse fakat dogrudan address ise
								if(IsTokenValidNumeric(tmp,false) != -1)
								{
									nextToken = tmp;
									nextTokenReserved = null;
									nextTokenType = Global.onlyAddress;
									result = input.substring(tagClosing+2);
								}
								else if(tmp.contains("+"))//Eger + karakteri barindiriyorsa
								{
									//Eger birden fazla + kullanildiysa
									if(Global.CountOccurrences(tmp, '+') > 1)
									{
										ResetToken();
										errors.addError(eList.GetError(eList.TOO_MUCH_PLUS_OCCURRENCE_BETWEEN_ADDRESS_TAGS));//Kuyruga yeni hatayi at.
									}
									else//Eger bir tek + kullanildiysa
									{
										int plusPosition = tmp.indexOf('+');//+'nin yer aldigi index'i tutalim.
										
										//[  AX   + 1234H] gibi kullanilabilir. Formlarini hesaba katalim.
										int unnecessaryPart = 0;
										boolean exitLoop = false;
										char nextChar = tmp.charAt(0);
										while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
										{
											if(nextChar == '\n')
											{
												Global.currentLine++;
											}
											unnecessaryPart++;
											if(unnecessaryPart < tmp.length())
											{
												nextChar = tmp.charAt(unnecessaryPart);
											}
											else
											{
												exitLoop = true;
											}
										}
										//['den sonra gereksiz karakterlerden sonra anlam ifade eden kismi kaydedelim.
										tmp = tmp.substring(unnecessaryPart);
										
										//tmp'i gereksiz part kadar ileri goturdugumuz icin artinin pozisyonunu da geri cekmeliyiz.
										String beforePlus = tmp.substring(0, plusPosition-unnecessaryPart);//Artidan onceki kismi aldik.
										
										//Simdi de sondan itibaren beforePlus'i tiraslayalim.
										unnecessaryPart = 0;
										exitLoop = false;
										int beforePlusLen = beforePlus.length();
										nextChar = tmp.charAt(beforePlusLen-1);//beforePlus'in son karakterinden baslayalim.
										while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
										{
											if(nextChar == '\n')
											{
												Global.currentLine++;
											}
											unnecessaryPart++;
											if((beforePlusLen-1-unnecessaryPart) > 0)
											{
												nextChar = tmp.charAt(beforePlusLen-1-unnecessaryPart);
											}
											else
											{
												exitLoop = true;
											}
										}
										//Bu islemden sonra beforePlus yalnizca onemli olan kismi tutar.
										beforePlus = beforePlus.substring(0,beforePlusLen-unnecessaryPart);
										
										int subFound = -1;
										beforePlus = beforePlus.toUpperCase();
										for(int n=0; n<Global.registerList.length; n++)
										{
											if(Global.registerList[n].equals(beforePlus))
											{
												subFound = n;
												break;
											}
										}
										if(subFound != -1)//Eger bir yazmaca karsilik geldiyse
										{
											nextToken = beforePlus;
											nextTokenReserved = null;
											//afterPlus henuz incelenmedigi icin gecici olarak onlyRegister olarak belirtiyoruz.
											nextTokenType = Global.onlyRegister;
											result = input.substring(tagClosing+2);
										}
										else if(IsTokenValidNumeric(beforePlus,false) != -1)//Eger beforePlus bir sayi ise
										{
											nextToken = beforePlus;
											nextTokenReserved = null;
											//afterPlus henuz incelenmedigi icin gecici olarak onlyAddress olarak belirtiyoruz.
											nextTokenType = Global.onlyAddress;
											result = input.substring(tagClosing+2);
										}
										else//Eger hatali adresleme yapildiysa
										{
											ResetToken();
											errors.addError(eList.GetError(eList.ILLEGAL_ADDRESING));//Kuyruga yeni hatayi at.
										}

										//+'dan sonraki kismi alalim.
										String afterPlus = tmp.substring(plusPosition+1);
										
										//tmp'i gereksiz part kadar ileri goturdugumuz icin artinin pozisyonunu da geri cekmeliyiz.
										//String afterPlus = tmp.substring(0, tagClosing);//Artidan sonraki kismi aldik.
										
										//Simdi de sondan itibaren beforePlus'i tiraslayalim.
										unnecessaryPart = 0;
										exitLoop = false;
										int afterPlusLen = afterPlus.length();
										nextChar = afterPlus.charAt(0);//afterPlus'in ilk karakterinden baslayalim.
										while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
										{
											if(nextChar == '\n')
											{
												Global.currentLine++;
											}
											unnecessaryPart++;
											if(unnecessaryPart < afterPlus.length())
											{
												nextChar = afterPlus.charAt(unnecessaryPart);
											}
											else
											{
												exitLoop = true;
											}
										}
										//Bu islemden sonra onemli kisma kadar tum gereksiz karakterler temizlendi.
										afterPlus = afterPlus.substring(unnecessaryPart);
										//afterPlus'in son uzunlugunu kaydedelim.
										afterPlusLen = afterPlus.length();
										
										unnecessaryPart = 0;
										exitLoop = false;
										nextChar = afterPlus.charAt(afterPlusLen-1);//afterPlus'in son karakterinden baslayalim.
										while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
										{
											if(nextChar == '\n')
											{
												Global.currentLine++;
											}
											unnecessaryPart++;
											if((afterPlusLen-1-unnecessaryPart) > 0)
											{
												nextChar = afterPlus.charAt(afterPlusLen-1-unnecessaryPart);
											}
											else
											{
												exitLoop = true;
											}
										}
										//Bu islemden sonra afterPlus yalnizca onemli olan kismi tutar.
										afterPlus = afterPlus.substring(0,afterPlusLen-unnecessaryPart).toUpperCase();
										
										//Eger afterPlus sayi degilse
										if(IsTokenValidNumeric(afterPlus,false) == -1)
										{
											subFound = -1;
											for(int n=0; n<Global.registerList.length; n++)
											{
												afterPlus = afterPlus.toUpperCase();
												if(Global.registerList[n].equals(afterPlus))
												{
													subFound = n;
													break;
												}
											}
											
											if(subFound == -1)//Eger hatali adresleme yapildiysa
											{
												ResetToken();
												errors.addError(eList.GetError(eList.ILLEGAL_ADDRESING));//Kuyruga yeni hatayi at.
											}
											else//Eger bir yazmaca karsilik geldiyse
											{
												if(nextTokenType == Global.onlyAddress)//Eger beforePlus Address ise
												{
													nextTokenType = Global.addressRegister;
												}
												else if(nextTokenType == Global.onlyRegister)//Eger beforePlus yazmac ise
												{
													nextTokenType = Global.registerRegister;
												}
												nextTokenReserved = afterPlus;
												result = input.substring(tagClosing+2);
											}
										}
										else//Eger afterPlus bir sayi ise
										{
											if(nextTokenType == Global.onlyAddress)//Eger beforePlus Address ise
											{
												nextTokenType = Global.addressAddress;
											}
											else if(nextTokenType == Global.onlyRegister)//Eger beforePlus yazmac ise
											{
												nextTokenType = Global.registerAddress;
											}
											nextTokenReserved = afterPlus;
											result = input.substring(tagClosing+2);
										}
									}
								}
								else//Eger [  yazmac    ] yada [    000100b  ] gibi bir ihtimal varsa
								{
									int unnecessaryPart = 0;
									char nextChar = tmp.charAt(0);
									boolean exitLoop = false;
									while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
									{
										if(nextChar == '\n')
										{
											Global.currentLine++;
										}
										unnecessaryPart++;
										if(unnecessaryPart < tmp.length())
										{
											nextChar = tmp.charAt(unnecessaryPart);
										}
										else
										{
											exitLoop = true;
										}
									}
									tmp = tmp.substring(unnecessaryPart);
									
									found = -1;
									String tmpTest = tmp.substring(0, 2);
									tmpTest = tmpTest.toUpperCase();
									for(int k=0; k<Global.registerList.length; k++)
									{
										if(Global.registerList[k].equals(tmpTest))
										{
											found = k;
											break;
										}
									}
									
									//Eger yazmac bulunduysa
									if(found != -1)
									{
										String irregularRegister = Global.registerList[found];
										
										tmp = tmp.substring(irregularRegister.length());
										
										if(tmp.length() > 0)
										{
										
											//Yazmactan sonraki gereksiz kismi tiraslayalim.
											unnecessaryPart = 0;
											exitLoop = false;
											nextChar = tmp.charAt(0);
											while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
											{
												if(nextChar == '\n')
												{
													Global.currentLine++;
												}
												unnecessaryPart++;
												if(unnecessaryPart < tmp.length())
												{
													nextChar = tmp.charAt(unnecessaryPart);
												}
												else
												{
													exitLoop = true;
												}
											}
											tmp = tmp.substring(unnecessaryPart);
										}
										
										//Eger tiraslamayi yaptiktan sonra da baska karakterler kaldiysa syntax hatasi var demektir.
										if(tmp.length() > 0)
										{
											ResetToken();
											errors.addError(eList.GetError(eList.UNEXPECTED_SYNTAX_ERROR));//Kuyruga yeni hatayi at.
										}
										else
										{
											nextToken = irregularRegister;
											nextTokenReserved = null;
											nextTokenType = Global.onlyRegister;
											result = input.substring(tagClosing+2);
										}
									}
									else//Eger yazmac bulunamadiysa
									{
										unnecessaryPart = 0;
										exitLoop = false;
										nextChar = tmp.charAt(0);
										while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
										{
											if(nextChar == '\n')
											{
												Global.currentLine++;
											}
											unnecessaryPart++;
											if(unnecessaryPart < tmp.length())
											{
												nextChar = tmp.charAt(unnecessaryPart);
											}
											else
											{
												exitLoop = true;
											}
										}
										tmp = tmp.substring(unnecessaryPart);
										
										firstSpace = tmp.indexOf(' ');
										firstTab = tmp.indexOf('\t');
										if((firstTab < firstSpace && firstTab != -1) || (firstSpace < firstTab && firstSpace == -1))
										{
											firstSpace = firstTab;
										}
										firstEndOfLine = tmp.indexOf('\n');
										
										int tmpDelimiter = -1;
										if(firstSpace != -1 || firstEndOfLine != -1)
										{
											if(firstSpace < firstEndOfLine && firstSpace != -1)
											{
												tmpDelimiter = firstSpace;
											}
											else if(firstEndOfLine < firstSpace && firstEndOfLine == -1)
											{
												tmpDelimiter = firstSpace;
											}
											else if(firstEndOfLine < firstSpace && firstEndOfLine != -1)
											{
												tmpDelimiter = firstEndOfLine;
											}
											else if(firstSpace < firstEndOfLine && firstSpace == -1)
											{
												tmpDelimiter = firstEndOfLine;
											}
										}
										
										//Eger delimiter yoksa
										if(tmpDelimiter == -1)
										{
											//Eger address ise
											if(IsTokenValidNumeric(tmp,false) != -1)
											{
												nextToken = tmp;
												nextTokenReserved = null;
												nextTokenType = Global.onlyAddress;
												result = input.substring(tagClosing+2);
											}
										}
										//Eger delimiter varsa
										else
										{							
											tmp = tmp.substring(0, tmpDelimiter);
											
											//Eger address ise
											if(IsTokenValidNumeric(tmp,false) != -1)
											{
												nextToken = tmp;
												nextTokenReserved = null;
												nextTokenType = Global.onlyAddress;
												result = input.substring(tagClosing+2);
											}
											//Eger hicbiri olmuyorsa
											else
											{
												ResetToken();
												errors.addError(eList.GetError(eList.UNEXPECTED_SYNTAX_ERROR));//Kuyruga yeni hatayi at.
											}
										}
									}
								}
							}
							else//Dogrudan bir yazmaca tekabul ediyorsa
							{
								nextToken = tmp;
								nextTokenReserved = null;
								nextTokenType = Global.onlyRegister;
								result = input.substring(tagClosing+2);
							}
						}
					}
					
					if(nextTokenType == -1 || nextToken == null)//Eger yapilan islemler sonucunda nextToken degiskenleri default degerlere sahipse
					{
						boolean isFound = false;
						//Eger sonraki token direktifse 
						//Eger variable'in byte oldugunu tanitan gostergec ise
						if(input.startsWith("DB") || input.startsWith("db") || input.startsWith("byte") || input.startsWith("BYTE"))
						{
							//Eger byte tanitici 2 karakter ise mutlaka d ile baslamak zorundadir.
							if(input.charAt(0) == 'd' || input.charAt(0) == 'D')
							{
								//Direktiften sonraki karakter bosluk yada satir basi olmalidir.
								String tmp = input.substring(2);
								if(tmp.charAt(0) == ' ' || tmp.charAt(0) == '\t')//Eger bosluksa
								{
									isFound = true;
								}
								else if(tmp.charAt(0) == '\n')//Eger satir basiysa
								{
									Global.currentLine++;
									isFound = true;
								}
								
								//Eger tarife uyuyorsa
								if(isFound == true)
								{
									result = tmp;//Islemi tamamla.
									nextToken = "BYTE";
									nextTokenType = Global.initializerByte;
								}
							}
							//Eger byte yada BYTE ise
							else
							{
								//Direktiften sonraki karakter bosluk yada satir basi olmalidir.
								String tmp = input.substring(4);
								if(tmp.charAt(0) == ' ' || tmp.charAt(0) == '\t')//Eger bosluksa
								{
									isFound = true;
								}
								else if(tmp.charAt(0) == '\n')//Eger satir basiysa
								{
									Global.currentLine++;
									isFound = true;
								}
								
								//Eger tarife uyuyorsa
								if(isFound == true)
								{
									result = tmp;//Islemi tamamla.
									nextToken = "BYTE";
									nextTokenType = Global.initializerByte;
								}
							}
						}
						//Eger variable'in signed byte oldugunu tanitan gostergec ise
						if(input.startsWith("sbyte") || input.startsWith("SBYTE"))
						{
							
							//Direktiften sonraki karakter bosluk yada satir basi olmalidir.
							String tmp = input.substring(5);
							if(tmp.charAt(0) == ' ' || tmp.charAt(0) == '\t')//Eger bosluksa
							{
								isFound = true;
							}
							else if(tmp.charAt(0) == '\n')//Eger satir basiysa
							{
								Global.currentLine++;
								isFound = true;
							}
							
							//Eger tarife uyuyorsa
							if(isFound == true)
							{
								result = tmp;//Islemi tamamla.
								nextToken = "SBYTE";
								nextTokenType = Global.initializerSByte;
							}
						}
						//Eger variable'in word oldugunu tanitan gostergec ise
						else if(input.startsWith("dw") || input.startsWith("DW") || input.startsWith("word") || input.startsWith("WORD"))
						{
							//Eger byte tanitici 2 karakter ise mutlaka d ile baslamak zorundadir.
							if(input.charAt(0) == 'd' || input.charAt(0) == 'D')
							{
								//Direktiften sonraki karakter bosluk yada satir basi olmalidir.
								String tmp = input.substring(2);
								if(tmp.charAt(0) == ' ' || tmp.charAt(0) == '\t')//Eger bosluksa
								{
									isFound = true;
								}
								else if(tmp.charAt(0) == '\n')//Eger satir basiysa
								{
									Global.currentLine++;
									isFound = true;
								}
								
								//Eger tarife uyuyorsa
								if(isFound == true)
								{
									result = tmp;//Islemi tamamla.
									nextToken = "WORD";
									nextTokenType = Global.initializerWord;
								}
							}
							//Eger word yada WORD ise
							else
							{
								//Direktiften sonraki karakter bosluk yada satir basi olmalidir.
								String tmp = input.substring(4);
								if(tmp.charAt(0) == ' ' || tmp.charAt(0) == '\t')//Eger bosluksa
								{
									isFound = true;
								}
								else if(tmp.charAt(0) == '\n')//Eger satir basiysa
								{
									Global.currentLine++;
									isFound = true;
								}
								
								//Eger tarife uyuyorsa
								if(isFound == true)
								{
									result = tmp;//Islemi tamamla.
									nextToken = "WORD";
									nextTokenType = Global.initializerWord;
								}
							}
						}
						//Eger variable'in signed word oldugunu tanitan gostergec ise
						if(input.startsWith("sword") || input.startsWith("SWORD"))
						{
							
							//Direktiften sonraki karakter bosluk yada satir basi olmalidir.
							String tmp = input.substring(5);
							if(tmp.charAt(0) == ' ' || tmp.charAt(0) == '\t')//Eger bosluksa
							{
								isFound = true;
							}
							else if(tmp.charAt(0) == '\n')//Eger satir basiysa
							{
								Global.currentLine++;
								isFound = true;
							}
							
							//Eger tarife uyuyorsa
							if(isFound == true)
							{
								result = tmp;//Islemi tamamla.
								nextToken = "SWORD";
								nextTokenType = Global.initializerSWord;
							}
						}
						//Eger variable'in double word oldugunu tanitan gostergec ise
						else if(input.startsWith("dd") || input.startsWith("DD") || input.startsWith("dword") || input.startsWith("DWORD"))
						{
							//Eger byte tanitici 2 karakter ise mutlaka d ile baslamak zorundadir.
							if(input.charAt(0) == 'd' || input.charAt(0) == 'D')
							{
								//Direktiften sonraki karakter bosluk yada satir basi olmalidir.
								String tmp = input.substring(2);
								if(tmp.charAt(0) == ' ' || tmp.charAt(0) == '\t')//Eger bosluksa
								{
									isFound = true;
								}
								else if(tmp.charAt(0) == '\n')//Eger satir basiysa
								{
									Global.currentLine++;
									isFound = true;
								}
								
								//Eger tarife uyuyorsa
								if(isFound == true)
								{
									result = tmp;//Islemi tamamla.
									nextToken = "DWORD";
									nextTokenType = Global.initializerDWord;
								}
							}
							//Eger word yada DWORD ise
							else
							{
								//Direktiften sonraki karakter bosluk yada satir basi olmalidir.
								String tmp = input.substring(5);
								if(tmp.charAt(0) == ' ' || tmp.charAt(0) == '\t')//Eger bosluksa
								{
									isFound = true;
								}
								else if(tmp.charAt(0) == '\n')//Eger satir basiysa
								{
									Global.currentLine++;
									isFound = true;
								}
								
								//Eger tarife uyuyorsa
								if(isFound == true)
								{
									result = tmp;//Islemi tamamla.
									nextToken = "DWORD";
									nextTokenType = Global.initializerDWord;
								}
							}
						}
						//Eger variable'in signed word oldugunu tanitan gostergec ise
						if(input.startsWith("sdword") || input.startsWith("SDWORD"))
						{
							
							//Direktiften sonraki karakter bosluk yada satir basi olmalidir.
							String tmp = input.substring(6);
							if(tmp.charAt(0) == ' ' || tmp.charAt(0) == '\t')//Eger bosluksa
							{
								isFound = true;
							}
							else if(tmp.charAt(0) == '\n')//Eger satir basiysa
							{
								Global.currentLine++;
								isFound = true;
							}
							
							//Eger tarife uyuyorsa
							if(isFound == true)
							{
								result = tmp;//Islemi tamamla.
								nextToken = "SDWORD";
								nextTokenType = Global.initializerSDWord;
							}
						}
					}
				}
				if(nextTokenType == -1 || nextToken == null)//Eger yapilan islemler sonucunda nextToken degiskenleri default degerlere sahipse
				{
					//Kalan tek bir ihtimal vardir o da input'un degisken ismi barindirabilir olmasidir.
					int nextSpace = input.indexOf(' ');
					int nextTab = input.indexOf('\t');
					if((nextTab < nextSpace && nextTab != -1) || (nextSpace < nextTab && nextSpace == -1))
					{
						nextSpace = nextTab;
					}
					int nextLine = input.indexOf('\n');
					int nextComma = input.indexOf(',');

					int point = input.length();
					
					if(nextSpace != -1 || nextLine != -1 || nextComma != -1)//Eger birinden birini bulabildiysek.
					{
						//Eger hepsi varsa
						if(nextSpace != -1 && nextLine != -1 && nextComma != -1)
						{
							if((nextSpace < nextLine && nextSpace < nextComma))//Eger en once bosluk varsa
							{
								point = nextSpace;
							}
							else if((nextLine < nextSpace && nextLine < nextComma))//Eger en once satir basi varsa
							{
								point = nextLine;
								Global.currentLine++;
							}
							else if((nextComma < nextLine && nextComma < nextSpace))//Eger en once virgul varsa
							{
								point = nextComma;
							}
						}
						//Eger sadece space varsa
						else if(nextSpace != -1 && nextLine == -1 && nextComma == -1)
						{
							point = nextSpace;
						}
						//Eger sadece satir varsa
						else if(nextLine != -1 && nextSpace == -1 && nextComma == -1)
						{
							point = nextLine;
							Global.currentLine++;
						}
						//Eger sadece virgul varsa
						else if(nextComma != -1 && nextSpace == -1 && nextLine == -1)
						{
							point = nextComma;
						}
						//Eger 2 tanesi var birisi yoksa
						else if(nextSpace != -1 && nextLine != -1 && nextComma == -1)
						{
							if(nextSpace < nextLine)
							{
								point = nextSpace;
							}
							else
							{
								point = nextLine;
								Global.currentLine++;
							}
						}
						else if(nextLine != -1 && nextComma != -1 && nextSpace == -1)
						{
							if(nextLine < nextComma)
							{
								point = nextLine;
								Global.currentLine++;
							}
							else
							{
								point = nextComma;
							}
						}
						else if(nextSpace != -1 && nextComma != -1 && nextLine == -1)
						{
							if(nextSpace < nextComma)
							{
								point = nextSpace;
							}
							else
							{
								point = nextComma;
							}
						}

						String tmp = input.substring(0, point);
						
						//Eger deger Anlik ya da Adres ise
						if(IsTokenValidNumeric(tmp, false) != -1)
						{
							nextToken = tmp;
							nextTokenType = IsTokenValidNumeric(tmp, false);
							result = input.substring(point);
						}
						//Eger deger string'se
						else if(IsTokenValidString(tmp, false) != null)
						{
							nextToken = IsTokenValidString(tmp, false);
							nextTokenType = Global.string;
							result = input.substring(point);
						}
						//Eger end direktifi ise
						else if(tmp.equals("end") || tmp.equals("END"))
						{
							String tmpEnd = input.substring(point);
							char nextChar = tmpEnd.charAt(0);
							int unnecessaryPart = 0;
							boolean exitLoop = false;
							while((nextChar == ' ' || nextChar == '\n' || nextChar == '\t') && exitLoop == false)
							{
								if(nextChar == '\n')
								{
									Global.currentLine++;
								}
								unnecessaryPart++;
								if(unnecessaryPart < tmpEnd.length())
								{
									nextChar = tmpEnd.charAt(unnecessaryPart);
								}
								else
								{
									exitLoop = true;
								}
							}
							tmpEnd = tmpEnd.substring(unnecessaryPart);
							//Gereksiz kisim tiraslandi.
							
							firstSpace = tmpEnd.indexOf(' ');
							firstTab = tmpEnd.indexOf('\t');
							if((firstTab < firstSpace && firstTab != -1) || (firstSpace < firstTab && firstSpace == -1))
							{
								firstSpace = firstTab;
							}
							firstEndOfLine = tmp.indexOf('\n');

							if(firstSpace != -1 || firstEndOfLine != -1)//Birisinden birisinin bulunmus olmasi gerekiyor.
							{
								int len = -1;
								if(firstSpace < firstEndOfLine && firstSpace != -1)
								{
									len = firstSpace;
								}
								else if(firstEndOfLine < firstSpace && firstEndOfLine == -1)
								{
									len = firstSpace;
								}
								else if(firstEndOfLine < firstSpace && firstEndOfLine != -1)
								{
									len = firstEndOfLine;
								}
								else if(firstSpace < firstEndOfLine && firstSpace == -1)
								{
									len = firstEndOfLine;
								}
								
								tmpEnd = tmpEnd.substring(0, len);
								//Label ismi besembly yazim kurallarina uymuyorsa
								boolean isErrorDetected = false;
								
								if(tmpEnd.length() < 1 && tmpEnd.length() > 31)//Label ismi 1 ile 31 karakter arasinda olabilir.
								{
									errors.addError(eList.GetError(eList.LABEL_NAME_LENGTH_IS_INCORRECT));//Kuyruga yeni hatayi at.
									isErrorDetected = true;
								}
								if(Character.isDigit(tmpEnd.charAt(0)))//Label ismi sayi ile baslayamaz.
								{
									errors.addError(eList.GetError(eList.LABEL_NAME_STARTS_WITH_NUMBER));//Kuyruga yeni hatayi at.
									isErrorDetected = true;
								}
								//Label isminde nokta varsa bu yalnizca en basta olabilir.
								int dotIndex = tmpEnd.lastIndexOf('.');
								if(dotIndex != -1 && dotIndex != 1)//Eger nokta bulunduysa ve bu da ilk karakterde degilse
								{
									errors.addError(eList.GetError(eList.LABEL_NAME_STARTS_WITH_NUMBER));//Kuyruga yeni hatayi at.
									isErrorDetected = true;
								}
								//Label ismi ozel karakter olarak yalnizca ? .  @  _  $  % icerebilir.
								if(tmpEnd.matches("[0-9a-zA-Z]+") == false && tmpEnd.matches("[?.@_$%]+") == false)
								{
									errors.addError(eList.GetError(eList.LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER));//Kuyruga yeni hatayi at.
									isErrorDetected = true;
								}
								
								if(isErrorDetected == false)
								{
									nextToken = "END";
									nextTokenReserved = tmpEnd;
									nextTokenType = Global.directiveEnd;
									result = tmpEnd.substring(len);
								}
								else//Eger hata varsa
								{
									ResetToken();
								}
							}
							else
							{
								ResetToken();
								errors.addError(eList.GetError(eList.WRONG_END_DIRECTIVE_USAGE));//Kuyruga yeni hatayi at.
							}
						}
						else if(tmp.length() == 2)//Yazmac olabilirse
						{
							boolean registerFound = true;
						
							if(tmp.equals("AX") || tmp.equals("ax")) nextToken = "AX";
							else if(tmp.equals("AH") || tmp.equals("ah")) nextToken = "AH";
							else if(tmp.equals("AL") || tmp.equals("al")) nextToken = "AL";
							else if(tmp.equals("BX") || tmp.equals("bx")) nextToken = "BX";
							else if(tmp.equals("BH") || tmp.equals("bh")) nextToken = "BH";
							else if(tmp.equals("BL") || tmp.equals("bl")) nextToken = "BL";
							else if(tmp.equals("CX") || tmp.equals("cx")) nextToken = "CX";
							else if(tmp.equals("CH") || tmp.equals("ch")) nextToken = "CH";
							else if(tmp.equals("CL") || tmp.equals("cl")) nextToken = "CL";
							else if(tmp.equals("DX") || tmp.equals("dx")) nextToken = "DX";
							else if(tmp.equals("DH") || tmp.equals("dh")) nextToken = "DH";
							else if(tmp.equals("DL") || tmp.equals("dl")) nextToken = "DL";
							else if(tmp.equals("CS") || tmp.equals("cs")) nextToken = "CS";
							else if(tmp.equals("IP") || tmp.equals("ip")) nextToken = "IP";
							else if(tmp.equals("DS") || tmp.equals("ds")) nextToken = "DS";
							else if(tmp.equals("SI") || tmp.equals("si")) nextToken = "SI";
							else if(tmp.equals("ES") || tmp.equals("es")) nextToken = "ES";
							else if(tmp.equals("DI") || tmp.equals("di")) nextToken = "DI";
							else if(tmp.equals("SS") || tmp.equals("ss")) nextToken = "SS";
							else if(tmp.equals("SP") || tmp.equals("sp")) nextToken = "SP";
							else if(tmp.equals("BP") || tmp.equals("bp")) nextToken = "BP";
							else registerFound = false;
								
							if(registerFound == true)
							{
								nextTokenType = Global.register;
								result = input.substring(2);
							}
						}
						else//Variable olma ihtimali devam ediyorsa
						{
							//Degisken ismi besembly yazim kurallarina uymuyorsa
							boolean isErrorDetected = false;
							
							if(tmp.length() < 3 && tmp.length() > 31)//Degisken ismi 1 ile 31 karakter arasinda olabilir.
							{
								errors.addError(eList.GetError(eList.VARIABLE_NAME_LENGTH_IS_INCORRECT));//Kuyruga yeni hatayi at.
								isErrorDetected = true;
							}
							if(Character.isDigit(tmp.charAt(0)))//Degisken ismi sayi ile baslayamaz.
							{
								errors.addError(eList.GetError(eList.VARIABLE_NAME_STARTS_WITH_NUMBER));//Kuyruga yeni hatayi at.
								isErrorDetected = true;
							}
							//Degisken isminde nokta varsa bu yalnizca en basta olabilir.
							int dotIndex = tmp.lastIndexOf('.');
							if(dotIndex != -1 && dotIndex != 1)//Eger nokta bulunduysa ve bu da ilk karakterde degilse
							{
								errors.addError(eList.GetError(eList.VARIABLE_NAME_STARTS_WITH_NUMBER));//Kuyruga yeni hatayi at.
								isErrorDetected = true;
							}
							//Degisken ismi ozel karakter olarak yalnizca ? .  @  _  $  % icerebilir.
							if(tmp.matches("[0-9a-zA-Z]+") == false && tmp.matches("[?.@_$%]+") == false)
							{
								errors.addError(eList.GetError(eList.VARIABLE_NAME_CONTAINS_ILLEGAL_CHARACTER));//Kuyruga yeni hatayi at.
								isErrorDetected = true;
							}
							
							if(isErrorDetected == false)
							{
								nextToken = tmp;
								nextTokenType = Global.variable;
								result = input.substring(point);
							}
							else//Eger hata varsa
							{
								ResetToken();
							}
						}
					}
				}
				
				//Eger yapilan islemler sonucunda nextToken degiskenleri default degerlere sahipse
				//Tum ihtimaller denenmis ve hata var demektir.
				if(nextTokenType == -1 || nextToken == null)
				{
					ResetToken();
					//errors.addError(eList.GetError(eList.UNEXPECTED_SYNTAX_ERROR));//Kuyruga yeni hatayi at.
				}
			}
		}
		return result;
	}
	
	//Input ile gonderilen token'in sayi olup olmadigini, 
	//Binary, hex veya decimalligini ve hatalari kontrol eder.
	//giveError parametresi true ise, eger sayilarin formunda hata varsa hata kuyruguna node ekler.
	private int IsTokenValidNumeric(String input, boolean giveError)
	{
		int result = -1;
		input = input.toUpperCase();
		
		if(input.length() > 0)
		{
			if(input.endsWith("B"))//Eger input binary olabilecek durumdaysa
			{
				String tmp = input.substring(0,input.length()-1);//b gostergeci haric kalan bolumu kopyala.
				if(tmp.matches("[01]+"))//Eger yalnizca binary sayilar var ise
				{
					result = Global.binaryNumber;
				}
				else//Hata olusmus demektir.
				{
					if(giveError) 
					{
						ResetToken();
						errors.addError(eList.GetError(eList.WRONG_BINARY_NUMBER));//Kuyruga yeni hatayi at.
					}
				}
			}
			else if(input.endsWith("H"))//Eger input hex olabilecek durumdaysa
			{
				//AH, BH, CH, DH ise sikinti olusur.
				if(input.equals("AH") || input.equals("BH") || input.equals("CH") || input.equals("DH"))
				{
					if(giveError) 
					{
						ResetToken();
						errors.addError(eList.GetError(eList.WRONG_HEXADECIMAL_NUMBER));//Kuyruga yeni hatayi at.
					}
				}
				else
				{
					String tmp = input.substring(0,input.length()-1);//h gostergeci haric kalan bolumu kopyala.

					if(tmp.matches("[0-9a-fA-F]+"))//Eger yalnizca hex karakterler var ise
					{
						result = Global.hexadecimalNumber;
					}
					else//Hata olusmus demektir.
					{
						if(giveError) 
						{
							ResetToken();
							errors.addError(eList.GetError(eList.WRONG_HEXADECIMAL_NUMBER));//Kuyruga yeni hatayi at.
						}
					}
				}
			}
			else if(input.endsWith("D"))//Eger input decimal olabilecek durumdaysa
			{
				String tmp = input.substring(0,input.length()-1);//d gostergeci haric kalan bolumu kopyala.
				if(tmp.matches("[0-9]+"))//Eger yalnizca decimal karakterler var ise
				{
					result = Global.decimalNumber;
				}
				else//Hata olusmus demektir.
				{
					if(giveError)
					{
						ResetToken();
						errors.addError(eList.GetError(eList.WRONG_DECIMAL_NUMBER));//Kuyruga yeni hatayi at.
					}
				}
			}
			//Sayinin sonu b, d yada h ile bitmeyebilir, fakat yalnizca rakam iceriyorsa yine de decimal olabilir.
			//Decimal sayi icin d gostergeci elzem degildir.
			else
			{
				if(input.matches("[0-9]+"))//Yalnizca rakam varsa
				{
					result = Global.decimalNumber;
				}
				else//Eger hicbir ihtimal gecerli degilse
				{
					if(giveError)
					{
						ResetToken();
						errors.addError(eList.GetError(eList.WRONG_NUMBER));//Kuyruga yeni hatayi at.
					}
				}
			}
		}
		
		return result;
	}
	
	//IsTokenValidString input ile verilen string'in besembly'nin
	//String tanimina uyup uymadigini kontrol eder.
	//Sonuc olarak delimiterlari silinmis yeni bir string doner.
	private String IsTokenValidString(String input, boolean giveError)
	{
		String result = null;
		
		char delimiter = ' ';
		
		if(input.length() > 0)
		{
			//Delimiter " yada ' olabilir.
			boolean found = false;
			if(input.charAt(0) == '\"')
			{
				delimiter = '\"';
				found = true;
			}
			else if(input.charAt(0) == '\'')
			{
				delimiter = '\"';
				found = true;
			}
			//Eger input iki delimiter'dan birisini kullaniyorsa
			//Kullanilan delimiter'i kaydettik.
			
			//Eger string'in basindaki delimiter bulunduysa
			if(found == true)
			{
				//Input'un ayni delimiter ile bitirilip bitirilmedigini kontrol etmemiz gerekiyor.
				if(input.charAt(input.length()-1) == delimiter)//Eger son karakter delimiter ise
				{
					result = input.substring(1, input.length()-1);
				}
			}
			//Eger string delimitersiz kullanildiysa
			else
			{
				if(giveError)
				{
					//Hata verdirmemiz istendiyse
					result = null;
					ResetToken();
					errors.addError(eList.GetError(eList.WRONG_STRING_USAGE));//Kuyruga yeni hatayi at.
				}
			}
		}
		
		return result;
	}
	
	private void ResetToken()
	{
		nextToken = null;
		nextTokenType = -1;
	}
	
	public Vector<Fragment> getFragmentationList()
	{
		return fragments;
	}
	
	public boolean isSyntaxAnalysisSucceeded()
	{
		return syntaxAnalysisSucceeded;
	}
}