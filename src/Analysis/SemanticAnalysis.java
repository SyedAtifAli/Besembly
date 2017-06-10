package Analysis;

import java.util.Arrays;
import java.util.Vector;

import Errors.CurrentErrors;
import Errors.ErrorList;
import Fragments.*;
import Root.Global;
import Root.SupportedInstructions;

public class SemanticAnalysis 
{
	private Vector<Fragment> fragments = null;
	
	private boolean semanticAnalysisSucceeded;

	private ErrorList eList = ErrorList.get();
	private CurrentErrors errors = CurrentErrors.get();

	//Semantic analizdeki ilk pass'de kullanilacak olan gecici dizileri taninlayalin.
	private Vector<String> tmpVariableNames = new Vector<String>();
	private Vector<Integer> tmpVariableAddresses = new Vector<Integer>();
	private Vector<String> tmpLabelNames = new Vector<String>();
	private Vector<Integer> tmpLabelAddresses = new Vector<Integer>();
	private Vector<Instruction> instructionList = new Vector<Instruction>();
	private Vector<VarFactor> dataSegmentAddressList = new Vector<VarFactor>();
	
	private SupportedInstructions si = SupportedInstructions.get();

	public SemanticAnalysis(Vector<Fragment> fragmentList)
	{
		this.fragments = fragmentList;
		semanticAnalysisSucceeded = Start();
	}
	
	/*
	 * Data Segment Global'leri
	 */
	private int startupDirectiveFound = 0;
	private int exitDirectiveFound = 0;
	
	private int dataPointer = 0;
	private int instructionPointer = 0;
	
	private Fragment nextFragment = null;
	private int stackSize = -1;
	private boolean Start()
	{	
		int memoryType = -1;
		stackSize = Global.defaultStackSize;
		
		//Isleme baslayalim.
		boolean directiveBased = false;

		//Eger fragment'larin icinde model direktifi varsa bu en basta olmalidir.
		Fragment tmpFrag = null;
		
		int modelDirectiveFound = 0;
		int stackDirectiveFound = 0;
		
		int codeDirectiveFound = 0;
		
		int dataDirectiveFound = 0;

		for(int i=0; i<fragments.size(); i++)
		{
			tmpFrag = fragments.get(i);
			
			//Eger direktifse
			if(tmpFrag.getFragmentType() == Global.directive)
			{
				directiveBased = true;
				//Eger direktif model ise
				if(((Directive) tmpFrag).getDirectiveType() == Global.directiveModel)
				{
					modelDirectiveFound++;
				}
				//Eger direktif stack ise
				else if(((Directive) tmpFrag).getDirectiveType() == Global.directiveStack)
				{
					//Eger code segmenti stack segmentinden evvel tanimlandiysa
					if(codeDirectiveFound > 0)
					{
						errors.addError(eList.GetError(eList.CODE_STACK_USED_BEFORE_STACK));//Kuyruga yeni hatayi at.
						return false;
					}
					
					int parameter;
					String parameterStr = ((Directive) tmpFrag).directiveParameter();
					//Eger hex ise
					if(parameterStr.endsWith("h") || parameterStr.endsWith("H"))
					{
						parameterStr = parameterStr.substring(0, parameterStr.length()-1);
						parameter = Integer.parseInt(parameterStr, 16);
					}
					//Eger binary ise
					else if(parameterStr.endsWith("b") || parameterStr.endsWith("B"))
					{
						parameterStr = parameterStr.substring(0, parameterStr.length()-1);
						parameter = Integer.parseInt(parameterStr, 2);
					}
					//Eger dec ise
					else if(parameterStr.endsWith("d") || parameterStr.endsWith("D"))
					{
						parameterStr = parameterStr.substring(0, parameterStr.length()-1);
						parameter = Integer.parseInt(parameterStr);
					}
					else//Yine dec'dir.
					{
						if(parameterStr.matches("[0-9]+"))
						{
							parameter = Integer.parseInt(parameterStr);
						}
						else
						{
							errors.addError(eList.GetError(eList.STACK_PARAMETER_IS_NOT_NUMERIC));//Kuyruga yeni hatayi at.
							return false;
						}
					}
					stackSize = parameter;
					stackDirectiveFound++;
				}
				//Eger direktif data ise
				else if(((Directive) tmpFrag).getDirectiveType() == Global.directiveData)
				{
					//Eger code segmenti data segmentinden evvel tanimlandiysa
					if(codeDirectiveFound > 0)
					{
						errors.addError(eList.GetError(eList.CODE_STACK_USED_BEFORE_DATA));//Kuyruga yeni hatayi at.
						return false;
					}
					dataDirectiveFound++;
				}
				//Eger direktif code ise
				else if(((Directive) tmpFrag).getDirectiveType() == Global.directiveCode)
				{
					codeDirectiveFound++;
				}
				//Eger direktif startup ise
				else if(((Directive) tmpFrag).getDirectiveType() == Global.directiveStartup)
				{
					//Eger startup'tan once code segmenti tanimlanmadiysa hata var demektir.
					if(codeDirectiveFound == 0)
					{
						errors.addError(eList.GetError(eList.STARTUP_USAGE_OUTSIDE_OF_CS));//Kuyruga yeni hatayi at.
						return false;
					}
					startupDirectiveFound++;
				}
				//Eger direktif exit ise
				else if(((Directive) tmpFrag).getDirectiveType() == Global.directiveExit)
				{
					//Eger startup'tan once code segmenti tanimlanmadiysa hata var demektir.
					if(codeDirectiveFound == 0)
					{
						errors.addError(eList.GetError(eList.EXIT_USAGE_OUTSIDE_OF_CS));//Kuyruga yeni hatayi at.
						return false;
					}
					//Eger exit'ten once startup yoksa hata var demektir.
					if(startupDirectiveFound == 0)
					{
						errors.addError(eList.GetError(eList.EXIT_USAGE_BEFORE_STARTUP_DECLARATION));//Kuyruga yeni hatayi at.
						return false;
					}
					exitDirectiveFound++;
				}
			}
		}
		tmpFrag = null;
		
		//Segment direktifleri yalnizca 1 defa kullanilmis olmalidir.
		if(modelDirectiveFound > 1)
		{
			errors.addError(eList.GetError(eList.MEMORY_DIRECTIVE_USED_MORE_THAN_ONCE));//Kuyruga yeni hatayi at.
			return false;
		}
		if(codeDirectiveFound > 1)
		{
			errors.addError(eList.GetError(eList.CODE_DIRECTIVE_USED_MORE_THAN_ONCE));//Kuyruga yeni hatayi at.
			return false;
		}
		if(stackDirectiveFound > 1)
		{
			errors.addError(eList.GetError(eList.STACK_DIRECTIVE_USED_MORE_THAN_ONCE));//Kuyruga yeni hatayi at.
			return false;
		}
		if(dataDirectiveFound > 1)
		{
			errors.addError(eList.GetError(eList.DATA_DIRECTIVE_USED_MORE_THAN_ONCE));//Kuyruga yeni hatayi at.
			return false;
		}
		if(startupDirectiveFound > 1)
		{
			errors.addError(eList.GetError(eList.MULTIPLE_STARTUP_DECLARATION));//Kuyruga yeni hatayi at.
			return false;
		}
		if(exitDirectiveFound > 1)
		{
			errors.addError(eList.GetError(eList.MULTIPLE_EXIT_DECLARATION));//Kuyruga yeni hatayi at.
			return false;
		}
		
		//Sonraki fragment'i getirelim.
		nextFragment = fragments.firstElement();
		fragments.remove(0);
		boolean doNotSnatch = false;
		
		//Eger segment direktifleri var ise ve model direktifi bulunduysa, ilk direktif model direktifi olmak zorundadir.
		if(directiveBased == true && modelDirectiveFound == 1)
		{
			//Eger ilk fragment direktif ise
			if(nextFragment.getFragmentType() == Global.directive)
			{
				//Eger ilk direktif model direktifi ise
				if(((Directive) nextFragment).getDirectiveType() == Global.directiveModel)
				{
					if(((Directive) nextFragment).directiveParameter().equals("SMALL"))
					{
						memoryType = Global.small;
						errors.addError(eList.GetError(eList.SMALL_MODEL_IS_NOT_SUPPORTED_YET));//Kuyruga yeni hatayi at.
						return false;
					}
					else if(((Directive) nextFragment).directiveParameter().equals("LARGE"))
					{
						memoryType = Global.large;
					}
					else
					{
						errors.addError(eList.GetError(eList.UNEXPECTED_SEMANTIC_ERROR));//Kuyruga yeni hatayi at.
						return false;
					}
				}
				else//Model degilse
				{
					errors.addError(eList.GetError(eList.MODEL_DIRECTIVE_IS_NOT_AT_FIRST));//Kuyruga yeni hatayi at.
					return false;
				}
			}
			else//Direktif degilse
			{
				errors.addError(eList.GetError(eList.FIRST_FRAGMENT_IS_NOT_DIRECTIVE));//Kuyruga yeni hatayi at.
				return false;
			}
		}
		//Eger kodda segment direktifi varsa ancak model direktifi kullanilmadiysa
		//Default model tipi atanir.
		else if(directiveBased == true && modelDirectiveFound == 0)
		{
			memoryType = Global.defaultModelType;
			doNotSnatch = true;
		}
		//Eger herhangi bir segment direktifi kullanilmadiysa
		//Small olmalidir.
		else if(directiveBased == false)
		{
			memoryType = Global.small;
			errors.addError(eList.GetError(eList.SMALL_MODEL_IS_NOT_SUPPORTED_YET));//Kuyruga yeni hatayi at.
			return false;
		}
		
		//Eger code ve data segmentleri ayri ayri ise
		if(memoryType == Global.large)
		{
			//Fakat data segment direktifi bulunamadiysa
			if(dataDirectiveFound == 0)
			{
				errors.addError(eList.GetError(eList.DATA_DIRECTIVE_NOT_FOUND));//Kuyruga yeni hatayi at.
				return false;
			}
			//Ya da code segment direktifi bulunamadiysa
			if(codeDirectiveFound == 0)
			{
				errors.addError(eList.GetError(eList.CODE_DIRECTIVE_NOT_FOUND));//Kuyruga yeni hatayi at.
				return false;
			}
		}
		//Eger bellek tipi small ise tek segment olmalidir, o segment de code segmentidir.
		else if(memoryType == Global.small)
		{
			//Fakat data segment direktifi varsa
			if(dataDirectiveFound > 0)
			{
				errors.addError(eList.GetError(eList.DATA_DIRECTIVE_FOUND));//Kuyruga yeni hatayi at.
				return false;
			}
			//Ya da code segment direktifi bulunamadiysa
			if(codeDirectiveFound == 0)
			{
				errors.addError(eList.GetError(eList.CODE_DIRECTIVE_NOT_FOUND));//Kuyruga yeni hatayi at.
				return false;
			}
		}
		
		//Buraya kadar geldiyse bellek modeli ve stack boyutu tanimlama islemi basariyla tamamlanmis demektir.
		
		//Eger data direktifi tanimlandiysa
		if(directiveBased == true && memoryType == Global.large)
		{
			if(!doNotSnatch)
			{
				//Sonraki fragment'i getirelim.
				nextFragment = fragments.firstElement();
				fragments.remove(0);
			}

			if(nextFragment.getFragmentType() == Global.directive)
			{
				//Eger stack definer ise devam edelim. Cunku zaten stack definer'i analiz ettik.
				if(((Directive) nextFragment).getDirectiveType() == Global.directiveStack)
				{
					nextFragment = fragments.firstElement();
					fragments.remove(0);
				}
				
				//Eger data segment tanimlandiysa
				if(((Directive) nextFragment).getDirectiveType() == Global.directiveData)
				{
					/*
					 * Data Segment Semantik Analiz
					 */
					boolean dsAnalysis = DataSegmentAnalysis();
					
					if(dsAnalysis == false)
					{
						return false;
					}
					
					//Eger data segment tamamlandiysa ve bir problem olusmadiysa
					//Islenecek nextFragment zaten tanimlanmis demektir.
					//Yeniden poll etmemeliyiz.
					//Buradan code segment'e devam edelim.
					
					/*
					 * Code Segment Semantik Analiz
					 */
					boolean csAnalysis = CodeSegmentAnalysis();
					
					if(csAnalysis == false)
					{
						return false;
					}
				}
				else
				{
					errors.addError(eList.GetError(eList.DATA_DIRECTIVE_USED_IN_WRONG_PLACE));//Kuyruga yeni hatayi at.
					return false;
				}
			}
		}
		
		return true;
	}
	private boolean DataSegmentAnalysis()
	{
		nextFragment = fragments.firstElement();
		fragments.remove(0);
		
		//Sonraki segment'in tanimlanmasi gelmeyene dek devam et.
		while(nextFragment.getFragmentType() != Global.directive)
		{
			//Data segment'te yalnizca variable'lar olabilir.
			if(nextFragment.getFragmentType() != Global.variable)
			{
				errors.addError(eList.GetError(eList.DATA_SEGMENT_CONTAINS_NON_VARIABLE_FRAGMENT));//Kuyruga yeni hatayi at.
				return false;
			}
			
			//Eger sonraki fragment variable ise
			String varName = ((Variable) nextFragment).getVariableName();
			int varType = ((Variable) nextFragment).getVariableTypeDirective();
			
			//Eger bu variable isminde tanimlanmis baska bir variable varsa
			if(tmpVariableNames.contains(varName))
			{
				errors.addError(eList.GetError(eList.VARIABLE_NAME_IS_ALREADY_DEFINED));//Kuyruga yeni hatayi at.
				return false;
			}
			
			//Variable tipine gore bellekten alacagimiz byte miktarini secelim.
			int byteCount = 0;
			if(varType == Global.initializerByte || varType == Global.initializerSByte) byteCount = 1;
			else if(varType == Global.initializerWord || varType == Global.initializerSWord) byteCount = 2;
			else if(varType == Global.initializerDWord || varType == Global.initializerSDWord) byteCount = 4;
			
			//Eger variable dup iceriyorsa
			//VARIABLE_NAME DB 30 DUP(?)
			if(((Variable) nextFragment).getDupParameter() != null)
			{
				String varDupParam = ((Variable) nextFragment).getDupParameter();
				int varDupParamType = ((Variable) nextFragment).getDupParameterType();
				String varElementCount = ((Variable) nextFragment).getDupDefinerValue();
				int varElementCountType = ((Variable) nextFragment).getDupDefinerValueType();
				
				int arraySize = 0;

				//Eger arraySize hex tipindeyse
				if(varElementCountType == Global.hexadecimalNumber)
				{
					if(varElementCount.endsWith("h") || varElementCount.endsWith("H"))
					{
						varElementCount = varElementCount.substring(0, varElementCount.length()-1);
					}
					arraySize = Integer.parseInt(varElementCount, 16);
				}
				//Eger arraySize decimal tipindeyse
				else if(varElementCountType == Global.decimalNumber)
				{
					if(varElementCount.endsWith("d") || varElementCount.endsWith("D"))
					{
						varElementCount = varElementCount.substring(0, varElementCount.length()-1);
					}
					arraySize = Integer.parseInt(varElementCount);
				}
				//Eger arraySize binary tipindeyse
				else if(varElementCountType == Global.binaryNumber)
				{
					if(varElementCount.endsWith("b") || varElementCount.endsWith("B"))
					{
						varElementCount = varElementCount.substring(0, varElementCount.length()-1);
					}
					arraySize = Integer.parseInt(varElementCount, 2);
				}

				//Eger dup parametresi string ise
				if(varDupParamType == Global.dupOperatorString)
				{
					tmpVariableNames.add(varName);
					tmpVariableAddresses.add(dataPointer);

					int tmpChar;
					boolean[] tmpBitSet;
					for(int i=0; i<arraySize; i++)
					{
						for(int j=0; j<varDupParam.length(); j++)
						{
							tmpChar = (int)varDupParam.charAt(j);
							
							tmpBitSet = Global.IntToBits(tmpChar, 1);					
							dataSegmentAddressList.add(new VarFactor(dataPointer, tmpBitSet));
							dataPointer++;
							//Eger programcinin bosa gitmesini istedigi alan varsa o alan kadar sifir koyalim.
							boolean[] zero = new boolean[8];
							for(int k=0; k<8; k++)
							{
								zero[k] = false;
							}	
							for(int k=0; k<byteCount-1; k++)
							{
								dataSegmentAddressList.add(new VarFactor(dataPointer, zero));
								dataPointer++;
							}
						}
					}
				}
				//Eger dup parametresi ? ise
				else if(varDupParamType == Global.dupOperatorQuestion)
				{
					tmpVariableNames.add(varName);
					tmpVariableAddresses.add(dataPointer);
					//freeSegmentSize = //freeSegmentSize - byteCount*arraySize;
					
					//Byte olarak sifir
					boolean[] zero = new boolean[8];
					for(int k=0; k<8; k++)
					{
						zero[k] = false;
					}	
					
					for(int j=0; j<arraySize; j++)
					{
						for(int k=0; k<byteCount; k++)
						{
							dataSegmentAddressList.add(new VarFactor(dataPointer,zero));
							dataPointer++;
						}
					}
				}
				//Eger dup parametresi sayi ise
				else if(varDupParamType == Global.dupOperatorNumber)
				{
					//byteCount word yada dword oldugu zamanlarda sayinin tek byte'a sigmayip diger byte'larin kullanilmasi tasarlanmali.
					
					int varDup = -1;
					//Eger varDup hex tipindeyse
					if(varDupParam.endsWith("h") || varDupParam.endsWith("H"))
					{
						varDupParam = varDupParam.substring(0, varDupParam.length()-1);
						varDup = Integer.parseInt(varDupParam, 16);
					}
					//Eger arraySize binary tipindeyse
					else if(varDupParam.endsWith("b") || varDupParam.endsWith("B"))
					{
						varDupParam = varDupParam.substring(0, varDupParam.length()-1);
						varDup = Integer.parseInt(varDupParam, 2);
					}
					//Eger arraySize decimal tipindeyse
					else
					{
						if(varDupParam.endsWith("d") || varDupParam.endsWith("D"))
						{
							varDupParam = varDupParam.substring(0, varDupParam.length()-1);
							varDup = Integer.parseInt(varDupParam);
						}
						varDup = Integer.parseInt(varDupParam);
					}
					
					//Eger parametre, sinirlar dogrultusunda buyukse
					if(varType == Global.initializerSByte && (varDup > 127 || varDup < -128)
						|| (varType == Global.initializerByte && (varDup > 255 || varDup < 0))
						|| (varType == Global.initializerSWord && (varDup > 32767 || varDup < -32768))
						|| (varType == Global.initializerWord && (varDup > 65536 || varDup < 0))
						|| (varType == Global.initializerSDWord && (varDup > 2147483647 || varDup < -2147483648))
						|| (varType == Global.initializerDWord && (varDup > 2147483647 || varDup < 0)))
					{
						errors.addError(eList.GetError(eList.VARIABLE_VALUE_TOO_LARGE));//Kuyruga yeni hatayi at.
						return false;
					}

					tmpVariableNames.add(varName);
					tmpVariableAddresses.add(dataPointer);
					
					boolean[] tmpBitSet = null;
					tmpBitSet = Global.IntToBits(varDup, byteCount);
					
					for(int j=0; j<arraySize; j++)
					{
						for(int k=0; k<byteCount; k++)
						{
							dataSegmentAddressList.add(new VarFactor(dataPointer,Arrays.copyOfRange(tmpBitSet, k*8, k*8 + 8)));
							dataPointer++;
						}
					}
				}
			}
			//Eger dup degilse
			//VARIABLE_NAME DB 30 ya da VARIABLE_NAME DB 30,40,50,60
			else
			{
				String[] parameters = ((Variable) nextFragment).getParameters();
				Integer[] parameterTypes = ((Variable) nextFragment).getParameterTypes();
				int parameterCount = ((Variable) nextFragment).getParameterCount();
				
				tmpVariableNames.add(varName);
				tmpVariableAddresses.add(dataPointer);
				//freeSegmentSize = //freeSegmentSize - byteCount*parameterCount;

				//Ozel Durumlar->
				//Eger 1 parametre varsa
				//msg db "Hello, World!" gibi ozel bir durum olusabilir.
				if(parameterCount == 1)
				{
					if(parameterTypes[0] == Global.string)
					{
						int tmpChar;
						boolean[] tmpBitSet;
						for(int j=0; j<parameters[0].length(); j++)
						{
							tmpChar = (int)parameters[0].charAt(j);
							
							tmpBitSet = Global.IntToBits(tmpChar, 1);			
							dataSegmentAddressList.add(new VarFactor(dataPointer, tmpBitSet));
							dataPointer++;
							//Eger programcinin bosa gitmesini istedigi alan varsa o alan kadar sifir koyalim.
							boolean[] zero = new boolean[8];
							for(int k=0; k<8; k++)
							{
								zero[k] = false;
							}	
							for(int k=0; k<byteCount-1; k++)
							{
								dataSegmentAddressList.add(new VarFactor(dataPointer, zero));
								dataPointer++;
							}
						}
					}
					else//Eger normal ise
					{
						boolean[] tmpBitSet = null;
						tmpBitSet = Global.IntToBits(Integer.parseInt(parameters[0]), byteCount);
						
						for(int i=0; i<byteCount; i++)
						{
							dataSegmentAddressList.add(new VarFactor(dataPointer, Arrays.copyOfRange(tmpBitSet, i*8, i*8 + 8)));
							dataPointer++;
						}
					}
				}
				else
				{
					//Genel Durumlar->
					for(int b=0; b<parameterCount; b++)
					{
						int parameter = 0;
						int paramType = parameterTypes[b].intValue();
						
						//Eger string ise yalnizca karakter olabilir.
						if(paramType == Global.string)
						{
							//Eger 1 karakterden fazla varsa
							if(parameters[b].length() > 1)
							{
								errors.addError(eList.GetError(eList.ARRAY_DEFINITION_CANNOT_BE_FORMED_BY_STRING));//Kuyruga yeni hatayi at.
								return false;
							}
							//Eger karakter ise
							else
							{
								parameter = (int)parameters[b].charAt(0);
							}
						}
						//Eger parametre hex tipindeyse
						else if(paramType == Global.hexadecimalNumber)
						{
							if(parameters[b].endsWith("h") || parameters[b].endsWith("H"))
							{
								parameters[b] = parameters[b].substring(0, parameters[b].length()-1);
							}
							parameter = Integer.parseInt(parameters[b], 16);
						}
						//Eger parametre decimal tipindeyse
						else if(paramType == Global.decimalNumber)
						{
							if(parameters[b].endsWith("d") || parameters[b].endsWith("D"))
							{
								parameters[b] = parameters[b].substring(0, parameters[b].length()-1);
							}
							parameter = Integer.parseInt(parameters[b]);
						}
						//Eger parametre binary tipindeyse
						else if(paramType == Global.binaryNumber)
						{
							if(parameters[b].endsWith("b") || parameters[b].endsWith("B"))
							{
								parameters[b] = parameters[b].substring(0, parameters[b].length()-1);
							}
							parameter = Integer.parseInt(parameters[b], 2);
						}
	
						if(paramType == Global.binaryNumber || paramType == Global.hexadecimalNumber || paramType == Global.decimalNumber || paramType == Global.string)
						{
							//Eger parametre, sinirlar dogrultusunda buyukse
							if(varType == Global.initializerSByte && (parameter > 127 || parameter < -128)
								|| (varType == Global.initializerByte && (parameter > 255 || parameter < 0))
								|| (varType == Global.initializerSWord && (parameter > 32767 || parameter < -32768))
								|| (varType == Global.initializerWord && (parameter > 65536 || parameter < 0))
								|| (varType == Global.initializerSDWord && (parameter > 2147483647 || parameter < -2147483648))
								|| (varType == Global.initializerDWord && (parameter > 2147483647 || parameter < 0)))
							{
								errors.addError(eList.GetError(eList.VARIABLE_VALUE_TOO_LARGE));//Kuyruga yeni hatayi at.
								return false;
							}

							boolean[] tmpBitSet = null;
							tmpBitSet = Global.IntToBits(parameter, byteCount);
							
							for(int k=0; k<byteCount; k++)
							{
								dataSegmentAddressList.add(new VarFactor(dataPointer,Arrays.copyOfRange(tmpBitSet, k*8, k*8 + 8)));
								dataPointer++;
							}
						}
					}
				}
			}
			
			//Devam edelim.
			nextFragment = fragments.firstElement();
			fragments.remove(0);
		}
		
		return true;
	}
	
	private boolean CodeSegmentAnalysis()
	{
		//Islenecek nextFragment zaten tanimlanmis demektir.
		//Yeniden poll etmemeliyiz.
		
		//Eger code segment analysis'e gelmisse ve fragment direktif degilse hata var demektir.
		if(nextFragment.getFragmentType() != Global.directive)
		{
			errors.addError(eList.GetError(eList.UNEXPECTED_SEMANTIC_ERROR));//Kuyruga yeni hatayi at.
			return false;
		}
		
		//Eger stack definer ise devam edelim. Cunku zaten stack definer'i analiz ettik.
		if(((Directive) nextFragment).getDirectiveType() == Global.directiveStack)
		{
			nextFragment = fragments.firstElement();
			fragments.remove(0);
		}
		
		//Eger fragment code segment direktifi degilse hata var demektir.
		if(((Directive) nextFragment).getDirectiveType() != Global.directiveCode)
		{
			errors.addError(eList.GetError(eList.UNEXPECTED_SEMANTIC_ERROR));//Kuyruga yeni hatayi at.
			return false;
		}
		
		//Eger .startup tanimlandiysa segmentin o direktiften sonraki kismi onemlidir.
		if(startupDirectiveFound == 1)
		{
			boolean continueLoop = true;
			while(continueLoop == true)
			{
				nextFragment = fragments.firstElement();
				fragments.remove(0);
				if(nextFragment.getFragmentType() == Global.directive)
				{
					if(((Directive) nextFragment).getDirectiveType() == Global.directiveStartup)
					{
						continueLoop = false;
					}
				}
			}
		}
		
		//Code segmentin sonu olmayacagi icin fragment'lar bitene dek devam edecegiz.
		
		while(fragments.size() != 0)
		{
			nextFragment = fragments.firstElement();
			fragments.remove(0);
			
			/*
			 * Oncelikle olmamasi gereken durumlari ve cikis noktasini belirleyelim.
			 */
			if(nextFragment.getFragmentType() == Global.variable)
			{
				errors.addError(eList.GetError(eList.VARIABLE_DECLARATION_IN_CODE_SEGMENT));//Kuyruga yeni hatayi at.
				return false;
			}
			else if(nextFragment.getFragmentType() == Global.directive)
			{
				//Eger bu direktif exit ise donguden cikabiliriz.
				if(((Directive) nextFragment).getDirectiveType() == Global.directiveExit)
				{
					break;
				}
				else if(((Directive) nextFragment).getDirectiveType() == Global.directiveEnd)
				{
					if(tmpLabelNames.contains(((Directive) nextFragment).directiveParameter()))
					{
					}
					else
					{
						errors.addError(eList.GetError(eList.UNDEFINED_LABEL_NAME));//Kuyruga yeni hatayi at.
						return false;
					}
				}
				else
				{
					//Eger code segmentin icinde code segment deklarasyonu varsa
					if(((Directive) nextFragment).getDirectiveType() == Global.directiveCode)
					{
						errors.addError(eList.GetError(eList.SEGMENT_DECLARATION_IN_CODE_SEGMENT));//Kuyruga yeni hatayi at.
						return false;
					}
					//Eger code segmentin icinde data segment deklarasyonu varsa
					else if(((Directive) nextFragment).getDirectiveType() == Global.directiveData)
					{
						errors.addError(eList.GetError(eList.SEGMENT_DECLARATION_IN_CODE_SEGMENT));//Kuyruga yeni hatayi at.
						return false;
					}
					//Eger code segmentin icinde stack deklarasyonu varsa
					else if(((Directive) nextFragment).getDirectiveType() == Global.directiveStack)
					{
						errors.addError(eList.GetError(eList.STACK_DECLARATION_IN_CODE_SEGMENT));//Kuyruga yeni hatayi at.
						return false;
					}
					//Eger code segmentin icinde stack deklarasyonu varsa
					else if(((Directive) nextFragment).getDirectiveType() == Global.directiveModel)
					{
						errors.addError(eList.GetError(eList.MODEL_DECLARATION_IN_CODE_SEGMENT));//Kuyruga yeni hatayi at.
						return false;
					}
					else
					{
						errors.addError(eList.GetError(eList.UNDEFINED_DIRECTIVE_USAGE));//Kuyruga yeni hatayi at.
						return false;
					}
				}
			}
			
			/*
			 * Simdi mumkun olan durumlari belirleyelim.
			 */

			//Instruction, end ya da label olabilir.
			//Label ise
			if(nextFragment.getFragmentType() == Global.label)
			{
				String labelName = ((Label) nextFragment).getLabelName();

				//Eger bu label isminde tanimlanmis baska bir variable varsa
				if(tmpLabelNames.contains(labelName))
				{
					errors.addError(eList.GetError(eList.LABEL_NAME_IS_ALREADY_DEFINED));//Kuyruga yeni hatayi at.
					return false;
				}
				tmpLabelNames.add(labelName);
				tmpLabelAddresses.add(instructionPointer);
			}
			//End ise
			else if(nextFragment.getFragmentType() == Global.directive)
			{
				if(((Directive) nextFragment).getDirectiveType() != Global.directiveEnd)
				{
					errors.addError(eList.GetError(eList.UNDEFINED_DIRECTIVE_USAGE));//Kuyruga yeni hatayi at.
					return false;
				}
			}
			//Buyruk ise
			else if(nextFragment.getFragmentType() == Global.instruction)
			{
				//Her bir buyruk 16 bit'tir. Yani 2 byte'dir.

				//Mevcut adresi buyruk listesinin ilgili degiskenine atalim.
				((Instruction) nextFragment).setInstructionAddress(instructionPointer);
				
				//Buyrugu gecici bir listeye atalim.
				instructionList.add((Instruction) nextFragment);

				//Code segment'in mevcut adresini buyruk boyutu kadar artiralim.
				instructionPointer += 2;
			}
			//Eger baska birsey ise hata var demektir.
			else
			{
				errors.addError(eList.GetError(eList.UNEXPECTED_SEMANTIC_ERROR));//Kuyruga yeni hatayi at.
				return false;
			}
		}
		
		//Eger code segment'ten cikildiysa fakat hala fragment kaldiysa
		if(fragments.size() > 0)
		{
			errors.addError(eList.GetError(eList.WRONG_EXIT_USAGE));//Kuyruga yeni hatayi at.
			return false;
		}
		
		//Eger bu noktaya kadar gelindiyse
		//Code segment'teki variable ve label'lari adresler ile degistirmek uzere
		//Ikinci pass'e gecebiliriz.

		//Eger tmp listelerin iliskili nesnelerinde eleman sayisi birbirini tutmuyorsa
		if(tmpLabelNames.size() != tmpLabelAddresses.size())
		{
			errors.addError(eList.GetError(eList.UNEXPECTED_SEMANTIC_ERROR));//Kuyruga yeni hatayi at.
			return false;
		}
		if(tmpVariableNames.size() != tmpVariableAddresses.size())
		{
			errors.addError(eList.GetError(eList.UNEXPECTED_SEMANTIC_ERROR));//Kuyruga yeni hatayi at.
			return false;
		}
	
		//Gecici instructionList listesindeki buyruklar bitene kadar devam edelim.
		Instruction currentInstruction = null;
		for(int index = 0; index < instructionList.size(); index++)
		{
			currentInstruction = instructionList.get(index);
			
			//Islem yapilabilmesi icin buyrugun en az 1 operandinin olmasi gerekmektedir.
			if(currentInstruction.getOperandCount() > 0)
			{
				/*
				 * Ilk operand'da olmamasi gereken durumlar
				 */
				if(currentInstruction.getFirstOperandType() == Global.offset)
				{
					errors.addError(eList.GetError(eList.WRONG_OFFSET_USAGE_SEM));//Kuyruga yeni hatayi at.
					return false;
				}
				else if(currentInstruction.getFirstOperandType() == Global.segmentInitializion)
				{
					errors.addError(eList.GetError(eList.WRONG_SEGMENT_INIT_USAGE_SEM));//Kuyruga yeni hatayi at.
					return false;
				}
				else if(currentInstruction.getFirstOperandType() == Global.onlyAddress || 
						currentInstruction.getFirstOperandType() == Global.addressRegister ||
						currentInstruction.getFirstOperandType() == Global.addressAddress ||
						currentInstruction.getFirstOperandType() == Global.registerAddress || 
						currentInstruction.getFirstOperandType() == Global.registerRegister)
				{
					errors.addError(eList.GetError(eList.WRONG_ADDRESS_USAGE_SEM));//Kuyruga yeni hatayi at.
					return false;
				}
				
				/*
				 * Ilk operand'da olmasi muhtemel durumlar
				 */
				//Eger operand anlik ise
				else if(currentInstruction.getFirstOperandType() == Global.binaryNumber)
				{
					String tmpImmediate = currentInstruction.getFirstOperand();
					int decNum;
					if(tmpImmediate.endsWith("b") || tmpImmediate.endsWith("B"))
					{
						tmpImmediate = tmpImmediate.substring(0, tmpImmediate.length()-1);
					}
					decNum = Integer.parseInt(tmpImmediate, 2);
					currentInstruction.setFirstOperand(Integer.toString(decNum));
					currentInstruction.setFirstOperandType(Global.decimalNumber); 
				}
				else if(currentInstruction.getFirstOperandType() == Global.hexadecimalNumber)
				{
					String tmpImmediate = currentInstruction.getFirstOperand();
					int decNum;
					if(tmpImmediate.endsWith("h") || tmpImmediate.endsWith("H"))
					{
						tmpImmediate = tmpImmediate.substring(0, tmpImmediate.length()-1);
					}
					decNum = Integer.parseInt(tmpImmediate, 16);
					currentInstruction.setFirstOperand(Integer.toString(decNum));
					currentInstruction.setFirstOperandType(Global.decimalNumber); 
				}
				else if(currentInstruction.getFirstOperandType() == Global.decimalNumber)
				{
					String tmpImmediate = currentInstruction.getFirstOperand();
					if(tmpImmediate.endsWith("d") || tmpImmediate.endsWith("D"))
					{
						tmpImmediate = tmpImmediate.substring(0, tmpImmediate.length()-1);
					}
					currentInstruction.setFirstOperand(tmpImmediate);
				}
				
				//Eger operand degisken ise
				else if(currentInstruction.getFirstOperandType() == Global.variable)
				{
					//Once buyrukta bahsedilen degisken data segment'te tanimli mi, ona bakalim.
					int indexOfToken = -1;
					int address = -1;
					for(int i=0; i<tmpVariableNames.size(); i++)
					{
						if(tmpVariableNames.get(i).equals(currentInstruction.getFirstOperand()))
						{
							indexOfToken = i;
							if(currentInstruction.getFirstOperandType() == Global.variable)
							{
								currentInstruction.setFirstOperandType(Global.label);
							}
							break;
						}
					}
					//Eger tanimli variable'lar arasinda bulunamadiysa
					if(indexOfToken == -1)
					{
						//Label olma ihtimali bulunuyor.
						for(int i=0; i<tmpLabelNames.size(); i++)
						{
							if(tmpLabelNames.get(i).equals(currentInstruction.getFirstOperand()))
							{
								indexOfToken = i;
								if(currentInstruction.getFirstOperandType() == Global.variable)
								{
									currentInstruction.setFirstOperandType(Global.label);
								}
								break;
							}
						}
						//Eger tanimli label'lar arasinda da yoksa
						if(indexOfToken == -1)
						{
							errors.addError(eList.GetError(eList.UNDEFINED_VARIABLE));//Kuyruga yeni hatayi at.
							return false;
						}
						//Eger label olarak bulunduysa
						else
						{
							address = tmpLabelAddresses.get(indexOfToken);
						}
					}
					//Eger degisken olarak bulunduysa
					else
					{
						address = tmpVariableAddresses.get(indexOfToken);
					}

					Instruction newInstruction = null;
					Instruction newCurrentInstruction = null;
					
					newInstruction = new Instruction("PUSH BP", "PUSH", "BP", Global.register);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress());
					instructionList.insertElementAt(newInstruction, index);
					newCurrentInstruction = newInstruction;
					
					newInstruction = new Instruction("MOV BP "+Integer.toString(address), "MOV", "BP", Global.register, Integer.toString(address), Global.decimalNumber);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+2);
					instructionList.insertElementAt(newInstruction, index+1);
					
					newInstruction = new Instruction("POP BP", "POP", "BP", Global.register);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+6);
					instructionList.insertElementAt(newInstruction, index+3);
					
					currentInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+4);
					
					//Simdi geri kalan buyruklari sirasiyla 6 byte ileri atalim.
					//Bu buyruktan sonraki buyruklarin eger label operand'i varsa onlar da 6 byte ileri atilmali.
					for(int i=index+4; i < instructionList.size(); i++)
					{
						instructionList.get(i).setInstructionAddress(instructionList.get(i).getInstructionAddress()+6);
						if(instructionList.get(i).getFirstOperandType() == Global.label)
						{
							instructionList.get(i).setFirstOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getFirstOperand()) + 6));
						}
						else if(instructionList.get(i).getFirstOperandType() == Global.variable)
						{
							for(int j=0; j<tmpLabelNames.size(); j++)
							{
								if(tmpLabelNames.get(j).equals(instructionList.get(i).getFirstOperand()))
								{
									instructionList.get(i).setFirstOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
									tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
									instructionList.get(i).setFirstOperandType(Global.label);
								}
							}
						}
						if(instructionList.get(i).getSecondOperandType() == Global.label)
						{
							instructionList.get(i).setSecondOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getSecondOperand()) + 6));
						}
						else if(instructionList.get(i).getSecondOperandType() == Global.variable)
						{
							for(int j=0; j<tmpLabelNames.size(); j++)
							{
								if(tmpLabelNames.get(j).equals(instructionList.get(i).getSecondOperand()))
								{
									instructionList.get(i).setSecondOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
									tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
									instructionList.get(i).setSecondOperandType(Global.label);
								}
							}
						}
					}
					
					currentInstruction.setFirstOperand(Integer.toString(address));
					currentInstruction.setFirstOperandType(Global.onlyRegister);
					
					//Suanki buyruk sonraki durumuna geldigi icin currentInstruction'i duzenlememiz gerekiyor.
					currentInstruction = newCurrentInstruction;
				}
				//Eger operand label ise
				else if(currentInstruction.getFirstOperandType() == Global.label)
				{
					//Once buyrukta bahsedilen label code segment'te tanimli mi, ona bakalim.
					int indexOfLabel = -1;
					for(int i=0; i<tmpLabelNames.size(); i++)
					{
						if(tmpLabelNames.get(i).equals(currentInstruction.getFirstOperand()))
						{
							indexOfLabel = i;
							break;
						}
					}
					//Eger tanimli label'lar arasinda bulunamadiysa
					if(indexOfLabel == -1)
					{
						//Eger numerik degil ise
						if(!currentInstruction.getFirstOperand().matches("[-+]?\\d*\\.?\\d+"))
						{
							errors.addError(eList.GetError(eList.UNDEFINED_LABEL));//Kuyruga yeni hatayi at.
							return false;
						}
					}
					else
					{
						//Eger bulunduysa
						currentInstruction.setFirstOperand(tmpLabelAddresses.get(indexOfLabel).toString());
					}
				}

				else if(currentInstruction.getFirstOperandType() == Global.bytePtrOnlyAddress || currentInstruction.getFirstOperandType() == Global.wordPtrOnlyAddress ||  
						currentInstruction.getFirstOperandType() == Global.dWordPtrOnlyAddress || currentInstruction.getFirstOperandType() == Global.qWordPtrOnlyAddress )
				{
					int address;
					String tmpAddress = currentInstruction.getFirstOperand();
					//Eger hex ise
					if(tmpAddress.endsWith("h") || tmpAddress.endsWith("H"))
					{
						tmpAddress = tmpAddress.substring(0, tmpAddress.length()-1);
						address = Integer.parseInt(tmpAddress, 16);
					}
					//Eger binary ise
					else if(tmpAddress.endsWith("b") || tmpAddress.endsWith("B"))
					{
						tmpAddress = tmpAddress.substring(0, tmpAddress.length()-1);
						address = Integer.parseInt(tmpAddress, 2);
					}
					//Eger dec ise
					else if(tmpAddress.endsWith("d") || tmpAddress.endsWith("D"))
					{
						tmpAddress = tmpAddress.substring(0, tmpAddress.length()-1);
						address = Integer.parseInt(tmpAddress);
					}
					else//Yine dec'dir.
					{
						address = Integer.parseInt(tmpAddress);
					}
					
					Instruction newInstruction = null;
					Instruction newCurrentInstruction = null;
					
					newInstruction = new Instruction("PUSH BP", "PUSH", "BP", Global.register);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress());
					instructionList.insertElementAt(newInstruction, index);
					newCurrentInstruction = newInstruction;
					
					newInstruction = new Instruction("MOV BP "+Integer.toString(address), "MOV", "BP", Global.register, Integer.toString(address), Global.decimalNumber);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+2);
					instructionList.insertElementAt(newInstruction, index+1);
					
					newInstruction = new Instruction("POP BP", "POP", "BP", Global.register);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+6);
					instructionList.insertElementAt(newInstruction, index+3);
					
					currentInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+4);
					
					//Simdi geri kalan buyruklari sirasiyla 6 byte ileri atalim.
					for(int i=index+4; i < instructionList.size(); i++)
					{
						instructionList.get(i).setInstructionAddress(instructionList.get(i).getInstructionAddress() + 6);
						if(instructionList.get(i).getFirstOperandType() == Global.label)
						{
							instructionList.get(i).setFirstOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getFirstOperand()) + 6));
						}
						else if(instructionList.get(i).getFirstOperandType() == Global.variable)
						{
							for(int j=0; j<tmpLabelNames.size(); j++)
							{
								if(tmpLabelNames.get(j).equals(instructionList.get(i).getFirstOperand()))
								{
									instructionList.get(i).setFirstOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
									tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
									instructionList.get(i).setFirstOperandType(Global.label);
								}
							}
						}
						if(instructionList.get(i).getSecondOperandType() == Global.label)
						{
							instructionList.get(i).setSecondOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getSecondOperand()) + 6));
						}
						else if(instructionList.get(i).getSecondOperandType() == Global.variable)
						{
							for(int j=0; j<tmpLabelNames.size(); j++)
							{
								if(tmpLabelNames.get(j).equals(instructionList.get(i).getSecondOperand()))
								{
									instructionList.get(i).setSecondOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
									tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
									instructionList.get(i).setSecondOperandType(Global.label);
								}
							}
						}
					}
						
					currentInstruction.setFirstOperand("BP");
					if(currentInstruction.getFirstOperandType() == Global.bytePtrOnlyAddress) currentInstruction.setFirstOperandType(Global.bytePtrOnlyRegister);
					else if(currentInstruction.getFirstOperandType() == Global.wordPtrOnlyAddress) currentInstruction.setFirstOperandType(Global.wordPtrOnlyRegister);
					else if(currentInstruction.getFirstOperandType() == Global.dWordPtrOnlyAddress) currentInstruction.setFirstOperandType(Global.dWordPtrOnlyRegister);
					else if(currentInstruction.getFirstOperandType() == Global.qWordPtrOnlyAddress) currentInstruction.setFirstOperandType(Global.qWordPtrOnlyRegister);
					
					//Suanki buyruk sonraki durumuna geldigi icin currentInstruction'i duzenlememiz gerekiyor.
					currentInstruction = newCurrentInstruction;
				}
				
				//bytePtrOnlyRegister, wordPtrOnlyRegister, dWordPtrOnlyRegister , qWordPtrOnlyRegister
				//Oldugu zaman buyruk analizi yapildiginda register kontrolu de yapilmali.
				//Run-time'da register iceriginin degisme ihtimali vardir.
				
				else if(currentInstruction.getFirstOperandType() == Global.bytePtrAddressAddress || currentInstruction.getFirstOperandType() == Global.wordPtrAddressAddress ||
						currentInstruction.getFirstOperandType() == Global.dWordPtrAddressAddress || currentInstruction.getFirstOperandType() == Global.qWordPtrAddressAddress)
				{
					String[] adReg = currentInstruction.getFirstOperand().split("+");
					
					int[] addresses = new int[2];
					//Eger hex ise
					for(int i=0; i<2; i++)
					{
						if(adReg[i].endsWith("h") || adReg[i].endsWith("H"))
						{
							adReg[i] = adReg[i].substring(0, adReg[i].length()-1);
							addresses[i] = Integer.parseInt(adReg[i], 16);
						}
						//Eger binary ise
						else if(adReg[0].endsWith("b") || adReg[0].endsWith("B"))
						{
							adReg[i] = adReg[i].substring(0, adReg[i].length()-1);
							addresses[i] = Integer.parseInt(adReg[i], 2);
						}
						//Eger dec ise
						else if(adReg[0].endsWith("d") || adReg[0].endsWith("D"))
						{
							adReg[i] = adReg[i].substring(0, adReg[i].length()-1);
							addresses[i] = Integer.parseInt(adReg[i]);
						}
						else//Yine dec'dir.
						{
							addresses[i] = Integer.parseInt(adReg[i]);
						}
					}
					int address = addresses[0] + addresses[1];
					
					Instruction newInstruction = null;
					Instruction newCurrentInstruction = null;
					
					newInstruction = new Instruction("PUSH BP", "PUSH", "BP", Global.register);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress());
					instructionList.insertElementAt(newInstruction, index);
					newCurrentInstruction = newInstruction;
					
					newInstruction = new Instruction("MOV BP "+Integer.toString(address), "MOV", "BP", Global.register, Integer.toString(address), Global.decimalNumber);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+2);
					instructionList.insertElementAt(newInstruction, index+1);
					
					newInstruction = new Instruction("POP BP", "POP", "BP", Global.register);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+6);
					instructionList.insertElementAt(newInstruction, index+3);
					
					currentInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+4);
					
					//Simdi geri kalan buyruklari sirasiyla 6 byte ileri atalim.
					for(int i=index+4; i < instructionList.size(); i++)
					{
						instructionList.get(i).setInstructionAddress(instructionList.get(i).getInstructionAddress()+6);
						if(instructionList.get(i).getFirstOperandType() == Global.label)
						{
							instructionList.get(i).setFirstOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getFirstOperand()) + 6));
						}
						else if(instructionList.get(i).getFirstOperandType() == Global.variable)
						{
							for(int j=0; j<tmpLabelNames.size(); j++)
							{
								if(tmpLabelNames.get(j).equals(instructionList.get(i).getFirstOperand()))
								{
									instructionList.get(i).setFirstOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
									tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
									instructionList.get(i).setFirstOperandType(Global.label);
								}
							}
						}
						if(instructionList.get(i).getSecondOperandType() == Global.label)
						{
							instructionList.get(i).setSecondOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getSecondOperand()) + 6));
						}
						else if(instructionList.get(i).getSecondOperandType() == Global.variable)
						{
							for(int j=0; j<tmpLabelNames.size(); j++)
							{
								if(tmpLabelNames.get(j).equals(instructionList.get(i).getSecondOperand()))
								{
									instructionList.get(i).setSecondOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
									tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
									instructionList.get(i).setSecondOperandType(Global.label);
								}
							}
						}
					}
					
					currentInstruction.setFirstOperand("BP");
					if(currentInstruction.getFirstOperandType() == Global.bytePtrAddressAddress) currentInstruction.setFirstOperandType(Global.bytePtrOnlyRegister);
					else if(currentInstruction.getFirstOperandType() == Global.wordPtrAddressAddress) currentInstruction.setFirstOperandType(Global.wordPtrOnlyRegister);
					else if(currentInstruction.getFirstOperandType() == Global.dWordPtrAddressAddress) currentInstruction.setFirstOperandType(Global.dWordPtrOnlyRegister);
					else if(currentInstruction.getFirstOperandType() == Global.qWordPtrAddressAddress) currentInstruction.setFirstOperandType(Global.qWordPtrOnlyRegister);
					
					//Suanki buyruk sonraki durumuna geldigi icin currentInstruction'i duzenlememiz gerekiyor.
					currentInstruction = newCurrentInstruction;
				}
				else if(currentInstruction.getFirstOperandType() == Global.bytePtrAddressRegister || currentInstruction.getFirstOperandType() == Global.wordPtrAddressRegister ||
						currentInstruction.getFirstOperandType() == Global.dWordPtrAddressRegister || currentInstruction.getFirstOperandType() == Global.qWordPtrAddressRegister)
				{
					String[] adReg = currentInstruction.getFirstOperand().split("+");
					
					int address;
					//Eger hex ise
					if(adReg[0].endsWith("h") || adReg[0].endsWith("H"))
					{
						adReg[0] = adReg[0].substring(0, adReg[0].length()-1);
						address = Integer.parseInt(adReg[0], 16);
					}
					//Eger binary ise
					else if(adReg[0].endsWith("b") || adReg[0].endsWith("B"))
					{
						adReg[0] = adReg[0].substring(0, adReg[0].length()-1);
						address = Integer.parseInt(adReg[0], 2);
					}
					//Eger dec ise
					else if(adReg[0].endsWith("d") || adReg[0].endsWith("D"))
					{
						adReg[0] = adReg[0].substring(0, adReg[0].length()-1);
						address = Integer.parseInt(adReg[0]);
					}
					else//Yine dec'dir.
					{
						address = Integer.parseInt(adReg[0]);
					}
					
					Instruction newInstruction = null;
					Instruction newCurrentInstruction = null;
					
					newInstruction = new Instruction("PUSH "+adReg[1], "PUSH", adReg[1], Global.register);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress());
					instructionList.insertElementAt(newInstruction, index);
					newCurrentInstruction = newInstruction;
					
					newInstruction = new Instruction("ADD "+adReg[1]+" "+Integer.toString(address), "ADD", adReg[1], Global.register, Integer.toString(address), Global.decimalNumber);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+2);
					instructionList.insertElementAt(newInstruction, index+1);
					
					newInstruction = new Instruction("POP "+adReg[1], "POP", adReg[1], Global.register);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+6);
					instructionList.insertElementAt(newInstruction, index+3);
					
					currentInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+4);
					
					//Simdi geri kalan buyruklari sirasiyla 6 byte ileri atalim.
					for(int i=index+4; i < instructionList.size(); i++)
					{
						instructionList.get(i).setInstructionAddress(instructionList.get(i).getInstructionAddress()+6);
						if(instructionList.get(i).getFirstOperandType() == Global.label)
						{
							instructionList.get(i).setFirstOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getFirstOperand()) + 6));
						}
						else if(instructionList.get(i).getFirstOperandType() == Global.variable)
						{
							for(int j=0; j<tmpLabelNames.size(); j++)
							{
								if(tmpLabelNames.get(j).equals(instructionList.get(i).getFirstOperand()))
								{
									instructionList.get(i).setFirstOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
									tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
									instructionList.get(i).setFirstOperandType(Global.label);
								}
							}
						}
						if(instructionList.get(i).getSecondOperandType() == Global.label)
						{
							instructionList.get(i).setSecondOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getSecondOperand()) + 6));
						}
						else if(instructionList.get(i).getSecondOperandType() == Global.variable)
						{
							for(int j=0; j<tmpLabelNames.size(); j++)
							{
								if(tmpLabelNames.get(j).equals(instructionList.get(i).getSecondOperand()))
								{
									instructionList.get(i).setSecondOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
									tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
									instructionList.get(i).setSecondOperandType(Global.label);
								}
							}
						}
					}
					
					currentInstruction.setFirstOperand(adReg[1]);
					if(currentInstruction.getFirstOperandType() == Global.bytePtrAddressRegister) currentInstruction.setFirstOperandType(Global.bytePtrOnlyRegister);
					else if(currentInstruction.getFirstOperandType() == Global.wordPtrAddressRegister) currentInstruction.setFirstOperandType(Global.wordPtrOnlyRegister);
					else if(currentInstruction.getFirstOperandType() == Global.dWordPtrAddressRegister) currentInstruction.setFirstOperandType(Global.dWordPtrOnlyRegister);
					else if(currentInstruction.getFirstOperandType() == Global.qWordPtrAddressRegister) currentInstruction.setFirstOperandType(Global.qWordPtrOnlyRegister);
				
					//Suanki buyruk sonraki durumuna geldigi icin currentInstruction'i duzenlememiz gerekiyor.
					currentInstruction = newCurrentInstruction;
				}
				else if(currentInstruction.getFirstOperandType() == Global.bytePtrRegisterAddress || currentInstruction.getFirstOperandType() == Global.wordPtrRegisterAddress ||
						currentInstruction.getFirstOperandType() == Global.dWordPtrRegisterAddress || currentInstruction.getFirstOperandType() == Global.qWordPtrRegisterAddress)
				{
					String[] adReg = currentInstruction.getFirstOperand().split("+");

					int address;
					//Eger hex ise
					if(adReg[1].endsWith("h") || adReg[1].endsWith("H"))
					{
						adReg[1] = adReg[1].substring(0, adReg[1].length()-1);
						address = Integer.parseInt(adReg[1], 16);
					}
					//Eger binary ise
					else if(adReg[1].endsWith("b") || adReg[1].endsWith("B"))
					{
						adReg[1] = adReg[1].substring(0, adReg[1].length()-1);
						address = Integer.parseInt(adReg[1], 2);
					}
					//Eger dec ise
					else if(adReg[1].endsWith("d") || adReg[1].endsWith("D"))
					{
						adReg[1] = adReg[1].substring(0, adReg[1].length()-1);
						address = Integer.parseInt(adReg[1]);
					}
					else//Yine dec'dir.
					{
						address = Integer.parseInt(adReg[1]);
					}
					
					Instruction newInstruction = null;
					Instruction newCurrentInstruction = null;
					
					newInstruction = new Instruction("PUSH "+adReg[0], "PUSH", adReg[0], Global.register);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress());
					instructionList.insertElementAt(newInstruction, index);
					newCurrentInstruction = newInstruction;
					
					newInstruction = new Instruction("ADD "+adReg[0]+" "+Integer.toString(address), "ADD", adReg[0], Global.register, Integer.toString(address), Global.decimalNumber);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+2);
					instructionList.insertElementAt(newInstruction, index+1);
					
					newInstruction = new Instruction("POP "+adReg[0], "POP", adReg[0], Global.register);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+6);
					instructionList.insertElementAt(newInstruction, index+3);
					
					currentInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+4);
					
					//Simdi geri kalan buyruklari sirasiyla 6 byte ileri atalim.
					for(int i=index+4; i < instructionList.size(); i++)
					{
						instructionList.get(i).setInstructionAddress(instructionList.get(i).getInstructionAddress()+6);
						if(instructionList.get(i).getFirstOperandType() == Global.label)
						{
							instructionList.get(i).setFirstOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getFirstOperand()) + 6));
						}
						else if(instructionList.get(i).getFirstOperandType() == Global.variable)
						{
							for(int j=0; j<tmpLabelNames.size(); j++)
							{
								if(tmpLabelNames.get(j).equals(instructionList.get(i).getFirstOperand()))
								{
									instructionList.get(i).setFirstOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
									tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
									instructionList.get(i).setFirstOperandType(Global.label);
								}
							}
						}
						if(instructionList.get(i).getSecondOperandType() == Global.label)
						{
							instructionList.get(i).setSecondOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getSecondOperand()) + 6));
						}
						else if(instructionList.get(i).getSecondOperandType() == Global.variable)
						{
							for(int j=0; j<tmpLabelNames.size(); j++)
							{
								if(tmpLabelNames.get(j).equals(instructionList.get(i).getSecondOperand()))
								{
									instructionList.get(i).setSecondOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
									tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
									instructionList.get(i).setSecondOperandType(Global.label);
								}
							}
						}
					}
					
					currentInstruction.setFirstOperand(adReg[0]);
					if(currentInstruction.getFirstOperandType() == Global.bytePtrRegisterAddress) currentInstruction.setFirstOperandType(Global.bytePtrOnlyRegister);
					else if(currentInstruction.getFirstOperandType() == Global.wordPtrRegisterAddress) currentInstruction.setFirstOperandType(Global.wordPtrOnlyRegister);
					else if(currentInstruction.getFirstOperandType() == Global.dWordPtrRegisterAddress) currentInstruction.setFirstOperandType(Global.dWordPtrOnlyRegister);
					else if(currentInstruction.getFirstOperandType() == Global.qWordPtrRegisterAddress) currentInstruction.setFirstOperandType(Global.qWordPtrOnlyRegister);
					
					//Suanki buyruk sonraki durumuna geldigi icin currentInstruction'i duzenlememiz gerekiyor.
					currentInstruction = newCurrentInstruction;
				}
				else if(currentInstruction.getFirstOperandType() == Global.bytePtrRegisterRegister || currentInstruction.getFirstOperandType() == Global.wordPtrRegisterRegister ||
						currentInstruction.getFirstOperandType() == Global.dWordPtrRegisterRegister || currentInstruction.getFirstOperandType() == Global.qWordPtrRegisterRegister)
				{
					String[] adReg = currentInstruction.getFirstOperand().split("+");
					
					Instruction newInstruction = null;
					Instruction newCurrentInstruction = null;
					
					newInstruction = new Instruction("PUSH "+adReg[0], "PUSH", adReg[0], Global.register);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress());
					instructionList.insertElementAt(newInstruction, index);
					newCurrentInstruction = newInstruction;
					
					newInstruction = new Instruction("ADD "+adReg[0]+" "+adReg[1], "ADD", adReg[0], Global.register, adReg[1], Global.register);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+2);
					instructionList.insertElementAt(newInstruction, index+1);
					
					newInstruction = new Instruction("POP "+adReg[0], "POP", adReg[0], Global.register);
					newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+6);
					instructionList.insertElementAt(newInstruction, index+3);
					
					currentInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+4);
					
					//Simdi geri kalan buyruklari sirasiyla 6 byte ileri atalim.
					for(int i=index+4; i < instructionList.size(); i++)
					{
						instructionList.get(i).setInstructionAddress(instructionList.get(i).getInstructionAddress()+6);
						if(instructionList.get(i).getFirstOperandType() == Global.label)
						{
							instructionList.get(i).setFirstOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getFirstOperand()) + 6));
						}
						else if(instructionList.get(i).getFirstOperandType() == Global.variable)
						{
							for(int j=0; j<tmpLabelNames.size(); j++)
							{
								if(tmpLabelNames.get(j).equals(instructionList.get(i).getFirstOperand()))
								{
									instructionList.get(i).setFirstOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
									tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
									instructionList.get(i).setFirstOperandType(Global.label);
								}
							}
						}
						if(instructionList.get(i).getSecondOperandType() == Global.label)
						{
							instructionList.get(i).setSecondOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getSecondOperand()) + 6));
						}
						else if(instructionList.get(i).getSecondOperandType() == Global.variable)
						{
							for(int j=0; j<tmpLabelNames.size(); j++)
							{
								if(tmpLabelNames.get(j).equals(instructionList.get(i).getSecondOperand()))
								{
									instructionList.get(i).setSecondOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
									tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
									instructionList.get(i).setSecondOperandType(Global.label);
								}
							}
						}
					}
					
					currentInstruction.setFirstOperand(adReg[0]);
					if(currentInstruction.getFirstOperandType() == Global.bytePtrRegisterRegister) currentInstruction.setFirstOperandType(Global.bytePtrOnlyRegister);
					else if(currentInstruction.getFirstOperandType() == Global.wordPtrRegisterRegister) currentInstruction.setFirstOperandType(Global.wordPtrOnlyRegister);
					else if(currentInstruction.getFirstOperandType() == Global.dWordPtrRegisterRegister) currentInstruction.setFirstOperandType(Global.dWordPtrOnlyRegister);
					else if(currentInstruction.getFirstOperandType() == Global.qWordPtrRegisterRegister) currentInstruction.setFirstOperandType(Global.qWordPtrOnlyRegister);
				
					//Suanki buyruk sonraki durumuna geldigi icin currentInstruction'i duzenlememiz gerekiyor.
					currentInstruction = newCurrentInstruction;
				}
				
				//Eger iki operand'li ise
				/*
				 * Ikinci operand icin
				 */
				if(currentInstruction.getOperandCount() == 2)
				{
					/*
					 * Ikinci operand'da olmasi muhtemel durumlar
					 */
					//Eger operand degisken ise
					if(currentInstruction.getSecondOperandType() == Global.variable)
					{
						//Once buyrukta bahsedilen degisken data segment'te tanimli mi, ona bakalim.
						int indexOfToken = -1;
						int address = -1;
						for(int i=0; i<tmpVariableNames.size(); i++)
						{
							if(tmpVariableNames.get(i).equals(currentInstruction.getSecondOperand()))
							{
								indexOfToken = i;
								if(currentInstruction.getSecondOperandType() == Global.variable)
								{
									currentInstruction.setSecondOperandType(Global.label);
								}
								break;
							}
						}
						//Eger tanimli variable'lar arasinda bulunamadiysa
						if(indexOfToken == -1)
						{
							//Label olma ihtimali bulunuyor.
							for(int i=0; i<tmpLabelNames.size(); i++)
							{
								if(tmpLabelNames.get(i).equals(currentInstruction.getSecondOperand()))
								{
									indexOfToken = i;
									break;
								}
							}
							//Eger tanimli label'lar arasinda da yoksa
							if(indexOfToken == -1)
							{
								errors.addError(eList.GetError(eList.UNDEFINED_VARIABLE));//Kuyruga yeni hatayi at.
								return false;
							}
							//Eger label olarak bulunduysa
							else
							{
								address = tmpLabelAddresses.get(indexOfToken);
							}
						}
						//Eger degisken olarak bulunduysa
						else
						{
							address = tmpVariableAddresses.get(indexOfToken);
						}

						Instruction newInstruction = null;
						Instruction newCurrentInstruction = null;
						
						newInstruction = new Instruction("PUSH BP", "PUSH", "BP", Global.register);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress());
						instructionList.insertElementAt(newInstruction, index);
						newCurrentInstruction = newInstruction;
						
						newInstruction = new Instruction("MOV BP "+Integer.toString(address), "MOV", "BP", Global.register, Integer.toString(address), Global.decimalNumber);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+2);
						instructionList.insertElementAt(newInstruction, index+1);
						
						newInstruction = new Instruction("POP BP", "POP", "BP", Global.register);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+6);
						instructionList.insertElementAt(newInstruction, index+3);
						
						currentInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+4);
						
						//Simdi geri kalan buyruklari sirasiyla 6 byte ileri atalim.
						for(int i=index+4; i < instructionList.size(); i++)
						{
							instructionList.get(i).setInstructionAddress(instructionList.get(i).getInstructionAddress()+6);
							if(instructionList.get(i).getFirstOperandType() == Global.label)
							{
								instructionList.get(i).setFirstOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getFirstOperand()) + 6));
							}
							else if(instructionList.get(i).getFirstOperandType() == Global.variable)
							{
								for(int j=0; j<tmpLabelNames.size(); j++)
								{
									if(tmpLabelNames.get(j).equals(instructionList.get(i).getFirstOperand()))
									{
										instructionList.get(i).setFirstOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
										tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
										instructionList.get(i).setFirstOperandType(Global.label);
									}
								}
							}
							if(instructionList.get(i).getSecondOperandType() == Global.label)
							{
								instructionList.get(i).setSecondOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getSecondOperand()) + 6));
							}
							else if(instructionList.get(i).getSecondOperandType() == Global.variable)
							{
								for(int j=0; j<tmpLabelNames.size(); j++)
								{
									if(tmpLabelNames.get(j).equals(instructionList.get(i).getSecondOperand()))
									{
										instructionList.get(i).setSecondOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
										tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
										instructionList.get(i).setSecondOperandType(Global.label);
									}
								}
							}
						}
						
						currentInstruction.setSecondOperand(Integer.toString(address));
						currentInstruction.setSecondOperandType(Global.onlyRegister);
						
						//Suanki buyruk sonraki durumuna geldigi icin currentInstruction'i duzenlememiz gerekiyor.
						currentInstruction = newCurrentInstruction;
					}
					//Eger operand label ise
					else if(currentInstruction.getSecondOperandType() == Global.label)
					{
						//Once buyrukta bahsedilen label code segment'te tanimli mi, ona bakalim.
						int indexOfLabel = -1;
						for(int i=0; i<tmpLabelNames.size(); i++)
						{
							if(tmpLabelNames.get(i).equals(currentInstruction.getSecondOperand()))
							{
								indexOfLabel = i;
								break;
							}
						}
						//Eger tanimli label'lar arasinda bulunamadiysa
						if(indexOfLabel == -1)
						{
							//Eger numerik degil ise
							if(!currentInstruction.getSecondOperand().matches("[-+]?\\d*\\.?\\d+"))
							{
								errors.addError(eList.GetError(eList.UNDEFINED_LABEL));//Kuyruga yeni hatayi at.
								return false;
							}
						}
						else
						{
							//Eger bulunduysa
							currentInstruction.setSecondOperand(tmpLabelAddresses.get(indexOfLabel).toString());	
						}
					}
					else if(currentInstruction.getSecondOperandType() == Global.addressRegister)
					{
						String[] adReg = currentInstruction.getSecondOperand().split("+");
						
						int address;
						//Eger hex ise
						if(adReg[0].endsWith("h") || adReg[0].endsWith("H"))
						{
							adReg[0] = adReg[0].substring(0, adReg[0].length()-1);
							address = Integer.parseInt(adReg[0], 16);
						}
						//Eger binary ise
						else if(adReg[0].endsWith("b") || adReg[0].endsWith("B"))
						{
							adReg[0] = adReg[0].substring(0, adReg[0].length()-1);
							address = Integer.parseInt(adReg[0], 2);
						}
						//Eger dec ise
						else if(adReg[0].endsWith("d") || adReg[0].endsWith("D"))
						{
							adReg[0] = adReg[0].substring(0, adReg[0].length()-1);
							address = Integer.parseInt(adReg[0]);
						}
						else//Yine dec'dir.
						{
							address = Integer.parseInt(adReg[0]);
						}
						
						Instruction newInstruction = null;
						Instruction newCurrentInstruction = null;
						
						newInstruction = new Instruction("PUSH "+adReg[1], "PUSH", adReg[1], Global.register);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress());
						instructionList.insertElementAt(newInstruction, index);
						newCurrentInstruction = newInstruction;
						
						newInstruction = new Instruction("ADD "+adReg[1]+" "+Integer.toString(address), "ADD", adReg[1], Global.register, Integer.toString(address), Global.decimalNumber);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+2);
						instructionList.insertElementAt(newInstruction, index+1);
						
						newInstruction = new Instruction("POP "+adReg[1], "POP", adReg[1], Global.register);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+6);
						instructionList.insertElementAt(newInstruction, index+3);
						
						currentInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+4);
						
						//Simdi geri kalan buyruklari sirasiyla 6 byte ileri atalim.
						for(int i=index+4; i < instructionList.size(); i++)
						{
							instructionList.get(i).setInstructionAddress(instructionList.get(i).getInstructionAddress()+6);
							if(instructionList.get(i).getFirstOperandType() == Global.label)
							{
								instructionList.get(i).setFirstOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getFirstOperand()) + 6));
							}
							else if(instructionList.get(i).getFirstOperandType() == Global.variable)
							{
								for(int j=0; j<tmpLabelNames.size(); j++)
								{
									if(tmpLabelNames.get(j).equals(instructionList.get(i).getFirstOperand()))
									{
										instructionList.get(i).setFirstOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
										tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
										instructionList.get(i).setFirstOperandType(Global.label);
									}
								}
							}
							if(instructionList.get(i).getSecondOperandType() == Global.label)
							{
								instructionList.get(i).setSecondOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getSecondOperand()) + 6));
							}
							else if(instructionList.get(i).getSecondOperandType() == Global.variable)
							{
								for(int j=0; j<tmpLabelNames.size(); j++)
								{
									if(tmpLabelNames.get(j).equals(instructionList.get(i).getSecondOperand()))
									{
										instructionList.get(i).setSecondOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
										tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
										instructionList.get(i).setSecondOperandType(Global.label);
									}
								}
							}
						}
						
						currentInstruction.setSecondOperand(adReg[1]);
						currentInstruction.setSecondOperandType(Global.onlyRegister);
						
						//Suanki buyruk sonraki durumuna geldigi icin currentInstruction'i duzenlememiz gerekiyor.
						currentInstruction = newCurrentInstruction;
					}
					else if(currentInstruction.getSecondOperandType() == Global.addressAddress)
					{
						String[] adReg = currentInstruction.getSecondOperand().split("+");
						
						int[] addresses = new int[2];
						//Eger hex ise
						for(int i=0; i<2; i++)
						{
							if(adReg[i].endsWith("h") || adReg[i].endsWith("H"))
							{
								adReg[i] = adReg[i].substring(0, adReg[i].length()-1);
								addresses[i] = Integer.parseInt(adReg[i], 16);
							}
							//Eger binary ise
							else if(adReg[0].endsWith("b") || adReg[0].endsWith("B"))
							{
								adReg[i] = adReg[i].substring(0, adReg[i].length()-1);
								addresses[i] = Integer.parseInt(adReg[i], 2);
							}
							//Eger dec ise
							else if(adReg[0].endsWith("d") || adReg[0].endsWith("D"))
							{
								adReg[i] = adReg[i].substring(0, adReg[i].length()-1);
								addresses[i] = Integer.parseInt(adReg[i]);
							}
							else//Yine dec'dir.
							{
								addresses[i] = Integer.parseInt(adReg[i]);
							}
						}
						int address = addresses[0] + addresses[1];
						
						Instruction newInstruction = null;
						Instruction newCurrentInstruction = null;
						
						newInstruction = new Instruction("PUSH BP", "PUSH", "BP", Global.register);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress());
						instructionList.insertElementAt(newInstruction, index);
						newCurrentInstruction = newInstruction;
						
						newInstruction = new Instruction("MOV BP "+Integer.toString(address), "MOV", "BP", Global.register, Integer.toString(address), Global.decimalNumber);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+2);
						instructionList.insertElementAt(newInstruction, index+1);
						
						newInstruction = new Instruction("POP BP", "POP", "BP", Global.register);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+6);
						instructionList.insertElementAt(newInstruction, index+3);
						
						currentInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+4);
						
						//Simdi geri kalan buyruklari sirasiyla 6 byte ileri atalim.
						for(int i=index+4; i < instructionList.size(); i++)
						{
							instructionList.get(i).setInstructionAddress(instructionList.get(i).getInstructionAddress()+6);
							if(instructionList.get(i).getFirstOperandType() == Global.label)
							{
								instructionList.get(i).setFirstOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getFirstOperand()) + 6));
							}
							else if(instructionList.get(i).getFirstOperandType() == Global.variable)
							{
								for(int j=0; j<tmpLabelNames.size(); j++)
								{
									if(tmpLabelNames.get(j).equals(instructionList.get(i).getFirstOperand()))
									{
										instructionList.get(i).setFirstOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
										tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
										instructionList.get(i).setFirstOperandType(Global.label);
									}
								}
							}
							if(instructionList.get(i).getSecondOperandType() == Global.label)
							{
								instructionList.get(i).setSecondOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getSecondOperand()) + 6));
							}
							else if(instructionList.get(i).getSecondOperandType() == Global.variable)
							{
								for(int j=0; j<tmpLabelNames.size(); j++)
								{
									if(tmpLabelNames.get(j).equals(instructionList.get(i).getSecondOperand()))
									{
										instructionList.get(i).setSecondOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
										tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
										instructionList.get(i).setSecondOperandType(Global.label);
									}
								}
							}
						}
						
						currentInstruction.setSecondOperand("BP");
						currentInstruction.setSecondOperandType(Global.onlyRegister);
						
						//Suanki buyruk sonraki durumuna geldigi icin currentInstruction'i duzenlememiz gerekiyor.
						currentInstruction = newCurrentInstruction;
					}
					else if(currentInstruction.getSecondOperandType() == Global.registerAddress)
					{
						String[] adReg = currentInstruction.getSecondOperand().split("+");

						int address;
						//Eger hex ise
						if(adReg[1].endsWith("h") || adReg[1].endsWith("H"))
						{
							adReg[1] = adReg[1].substring(0, adReg[1].length()-1);
							address = Integer.parseInt(adReg[1], 16);
						}
						//Eger binary ise
						else if(adReg[1].endsWith("b") || adReg[1].endsWith("B"))
						{
							adReg[1] = adReg[1].substring(0, adReg[1].length()-1);
							address = Integer.parseInt(adReg[1], 2);
						}
						//Eger dec ise
						else if(adReg[1].endsWith("d") || adReg[1].endsWith("D"))
						{
							adReg[1] = adReg[1].substring(0, adReg[1].length()-1);
							address = Integer.parseInt(adReg[1]);
						}
						else//Yine dec'dir.
						{
							address = Integer.parseInt(adReg[1]);
						}
						
						Instruction newInstruction = null;
						Instruction newCurrentInstruction = null;
						
						newInstruction = new Instruction("PUSH "+adReg[0], "PUSH", adReg[0], Global.register);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress());
						instructionList.insertElementAt(newInstruction, index);
						newCurrentInstruction = newInstruction;
						
						newInstruction = new Instruction("ADD "+adReg[0]+" "+Integer.toString(address), "ADD", adReg[0], Global.register, Integer.toString(address), Global.decimalNumber);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+2);
						instructionList.insertElementAt(newInstruction, index+1);
						
						newInstruction = new Instruction("POP "+adReg[0], "POP", adReg[0], Global.register);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+6);
						instructionList.insertElementAt(newInstruction, index+3);
						
						currentInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+4);
						
						//Simdi geri kalan buyruklari sirasiyla 6 byte ileri atalim.
						for(int i=index+4; i < instructionList.size(); i++)
						{
							instructionList.get(i).setInstructionAddress(instructionList.get(i).getInstructionAddress()+6);
							if(instructionList.get(i).getFirstOperandType() == Global.label)
							{
								instructionList.get(i).setFirstOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getFirstOperand()) + 6));
							}
							else if(instructionList.get(i).getFirstOperandType() == Global.variable)
							{
								for(int j=0; j<tmpLabelNames.size(); j++)
								{
									if(tmpLabelNames.get(j).equals(instructionList.get(i).getFirstOperand()))
									{
										instructionList.get(i).setFirstOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
										tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
										instructionList.get(i).setFirstOperandType(Global.label);
									}
								}
							}
							if(instructionList.get(i).getSecondOperandType() == Global.label)
							{
								instructionList.get(i).setSecondOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getSecondOperand()) + 6));
							}
							else if(instructionList.get(i).getSecondOperandType() == Global.variable)
							{
								for(int j=0; j<tmpLabelNames.size(); j++)
								{
									if(tmpLabelNames.get(j).equals(instructionList.get(i).getSecondOperand()))
									{
										instructionList.get(i).setSecondOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
										tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
										instructionList.get(i).setSecondOperandType(Global.label);
									}
								}
							}
						}
						
						currentInstruction.setSecondOperand(adReg[0]);
						currentInstruction.setSecondOperandType(Global.onlyRegister);
						
						//Suanki buyruk sonraki durumuna geldigi icin currentInstruction'i duzenlememiz gerekiyor.
						currentInstruction = newCurrentInstruction;
					}
					else if(currentInstruction.getSecondOperandType() == Global.registerRegister)
					{
						String[] adReg = currentInstruction.getSecondOperand().split("+");
						
						Instruction newInstruction = null;
						Instruction newCurrentInstruction = null;
						
						newInstruction = new Instruction("PUSH "+adReg[0], "PUSH", adReg[0], Global.register);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress());
						instructionList.insertElementAt(newInstruction, index);
						newCurrentInstruction = newInstruction;
						
						newInstruction = new Instruction("ADD "+adReg[0]+" "+adReg[1], "ADD", adReg[0], Global.register, adReg[1], Global.register);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+2);
						instructionList.insertElementAt(newInstruction, index+1);
						
						newInstruction = new Instruction("POP "+adReg[0], "POP", adReg[0], Global.register);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+6);
						instructionList.insertElementAt(newInstruction, index+3);
						
						currentInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+4);
						
						//Simdi geri kalan buyruklari sirasiyla 6 byte ileri atalim.
						for(int i=index+4; i < instructionList.size(); i++)
						{
							instructionList.get(i).setInstructionAddress(instructionList.get(i).getInstructionAddress()+6);
							if(instructionList.get(i).getFirstOperandType() == Global.label)
							{
								instructionList.get(i).setFirstOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getFirstOperand()) + 6));
							}
							else if(instructionList.get(i).getFirstOperandType() == Global.variable)
							{
								for(int j=0; j<tmpLabelNames.size(); j++)
								{
									if(tmpLabelNames.get(j).equals(instructionList.get(i).getFirstOperand()))
									{
										instructionList.get(i).setFirstOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
										tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
										instructionList.get(i).setFirstOperandType(Global.label);
									}
								}
							}
							if(instructionList.get(i).getSecondOperandType() == Global.label)
							{
								instructionList.get(i).setSecondOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getSecondOperand()) + 6));
							}
							else if(instructionList.get(i).getSecondOperandType() == Global.variable)
							{
								for(int j=0; j<tmpLabelNames.size(); j++)
								{
									if(tmpLabelNames.get(j).equals(instructionList.get(i).getSecondOperand()))
									{
										instructionList.get(i).setSecondOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
										tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
										instructionList.get(i).setSecondOperandType(Global.label);
									}
								}
							}
						}
						
						currentInstruction.setSecondOperand(adReg[0]);
						currentInstruction.setSecondOperandType(Global.onlyRegister);
						
						//Suanki buyruk sonraki durumuna geldigi icin currentInstruction'i duzenlememiz gerekiyor.
						currentInstruction = newCurrentInstruction;
					}
					else if(currentInstruction.getSecondOperandType() == Global.onlyAddress)
					{
						int address;
						String tmpOperand = currentInstruction.getSecondOperand();
						//Eger hex ise
						if(tmpOperand.endsWith("h") || tmpOperand.endsWith("H"))
						{
							tmpOperand = tmpOperand.substring(0, tmpOperand.length()-1);
							address = Integer.parseInt(tmpOperand, 16);
						}
						//Eger binary ise
						else if(tmpOperand.endsWith("b") || tmpOperand.endsWith("B"))
						{
							tmpOperand = tmpOperand.substring(0, tmpOperand.length()-1);
							address = Integer.parseInt(tmpOperand, 2);
						}
						//Eger dec ise
						else if(tmpOperand.endsWith("d") || tmpOperand.endsWith("D"))
						{
							tmpOperand = tmpOperand.substring(0, tmpOperand.length()-1);
							address = Integer.parseInt(tmpOperand);
						}
						else//Yine dec'dir.
						{
							address = Integer.parseInt(tmpOperand);
						}
						
						Instruction newInstruction = null;
						Instruction newCurrentInstruction = null;
						
						newInstruction = new Instruction("PUSH BP", "PUSH", "BP", Global.register);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress());
						instructionList.insertElementAt(newInstruction, index);
						newCurrentInstruction = newInstruction;
						
						newInstruction = new Instruction("MOV BP "+Integer.toString(address), "MOV", "BP", Global.register, Integer.toString(address), Global.decimalNumber);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+2);
						instructionList.insertElementAt(newInstruction, index+1);
						
						newInstruction = new Instruction("POP BP", "POP", "BP", Global.register);
						newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+6);
						instructionList.insertElementAt(newInstruction, index+3);
						
						currentInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+4);
						
						//Simdi geri kalan buyruklari sirasiyla 6 byte ileri atalim.
						for(int i=index+4; i < instructionList.size(); i++)
						{
							instructionList.get(i).setInstructionAddress(instructionList.get(i).getInstructionAddress()+6);
							if(instructionList.get(i).getFirstOperandType() == Global.label)
							{
								instructionList.get(i).setFirstOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getFirstOperand()) + 6));
							}
							else if(instructionList.get(i).getFirstOperandType() == Global.variable)
							{
								for(int j=0; j<tmpLabelNames.size(); j++)
								{
									if(tmpLabelNames.get(j).equals(instructionList.get(i).getFirstOperand()))
									{
										instructionList.get(i).setFirstOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
										tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
										instructionList.get(i).setFirstOperandType(Global.label);
									}
								}
							}
							if(instructionList.get(i).getSecondOperandType() == Global.label)
							{
								instructionList.get(i).setSecondOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getSecondOperand()) + 6));
							}
							else if(instructionList.get(i).getSecondOperandType() == Global.variable)
							{
								for(int j=0; j<tmpLabelNames.size(); j++)
								{
									if(tmpLabelNames.get(j).equals(instructionList.get(i).getSecondOperand()))
									{
										instructionList.get(i).setSecondOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
										tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
										instructionList.get(i).setSecondOperandType(Global.label);
									}
								}
							}
						}
						
						currentInstruction.setSecondOperand("BP");
						currentInstruction.setSecondOperandType(Global.onlyRegister);
						
						//Suanki buyruk sonraki durumuna geldigi icin currentInstruction'i duzenlememiz gerekiyor.
						currentInstruction = newCurrentInstruction;
					}
					else if(currentInstruction.getSecondOperandType() == Global.hexadecimalNumber || currentInstruction.getSecondOperandType() == Global.binaryNumber || currentInstruction.getSecondOperandType() == Global.decimalNumber)
					{
						int immediate = 0;
						String tmpOperand = currentInstruction.getSecondOperand();
						
						//Eger hex ise
						if(currentInstruction.getSecondOperandType() == Global.hexadecimalNumber)
						{
							if(tmpOperand.endsWith("h") || tmpOperand.endsWith("H"))
							{
								tmpOperand = tmpOperand.substring(0, tmpOperand.length()-1);
								
							}
							immediate = Integer.parseInt(tmpOperand, 16);
						}
						//Eger binary ise
						else if(currentInstruction.getSecondOperandType() == Global.binaryNumber)
						{
							if(tmpOperand.endsWith("b") || tmpOperand.endsWith("B"))
							{
								tmpOperand = tmpOperand.substring(0, tmpOperand.length()-1);
								
							}
							immediate = Integer.parseInt(tmpOperand, 2);
						}
						//Eger decimal ise
						else if(currentInstruction.getSecondOperandType() == Global.decimalNumber)
						{
							if(tmpOperand.endsWith("d") || tmpOperand.endsWith("D"))
							{
								tmpOperand = tmpOperand.substring(0, tmpOperand.length()-1);
								
							}
							immediate = Integer.parseInt(tmpOperand);
						}
						
						//Eger mov veya add ise ozel bir durum vardir, ikinci operand decimal olabilir.
						if(currentInstruction.getOpcode().equals("MOV") || currentInstruction.getOpcode().equals("ADD"))
						{
							currentInstruction.setSecondOperand(Integer.toString(immediate));
							currentInstruction.setSecondOperandType(Global.decimalNumber);
						}
						else//Degilse register'a donustur.
						{
							Instruction newInstruction = null;
							Instruction newCurrentInstruction = null;
							
							newInstruction = new Instruction("PUSH BP", "PUSH", "BP", Global.register);
							newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress());
							instructionList.insertElementAt(newInstruction, index);
							newCurrentInstruction = newInstruction;
							
							newInstruction = new Instruction("MOV BP "+immediate, "MOV", "BP", Global.register, Integer.toString(immediate), Global.decimalNumber);
							newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+2);
							instructionList.insertElementAt(newInstruction, index+1);
							
							newInstruction = new Instruction("POP BP", "POP", "BP", Global.register);
							newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+6);
							instructionList.insertElementAt(newInstruction, index+3);
							
							currentInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+4);
							
							//Simdi geri kalan buyruklari sirasiyla 6 byte ileri atalim.
							for(int i=index+4; i < instructionList.size(); i++)
							{
								instructionList.get(i).setInstructionAddress(instructionList.get(i).getInstructionAddress()+6);
								if(instructionList.get(i).getFirstOperandType() == Global.label)
								{
									instructionList.get(i).setFirstOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getFirstOperand()) + 6));
								}
								else if(instructionList.get(i).getFirstOperandType() == Global.variable)
								{
									for(int j=0; j<tmpLabelNames.size(); j++)
									{
										if(tmpLabelNames.get(j).equals(instructionList.get(i).getFirstOperand()))
										{
											instructionList.get(i).setFirstOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
											tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
											instructionList.get(i).setFirstOperandType(Global.label);
										}
									}
								}
								if(instructionList.get(i).getSecondOperandType() == Global.label)
								{
									instructionList.get(i).setSecondOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getSecondOperand()) + 6));
								}
								else if(instructionList.get(i).getSecondOperandType() == Global.variable)
								{
									for(int j=0; j<tmpLabelNames.size(); j++)
									{
										if(tmpLabelNames.get(j).equals(instructionList.get(i).getSecondOperand()))
										{
											instructionList.get(i).setSecondOperand(Integer.toString(tmpLabelAddresses.get(j) + 6));
											tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+6);
											instructionList.get(i).setSecondOperandType(Global.label);
										}
									}
								}
							}
							
							currentInstruction.setSecondOperand("BP");
							currentInstruction.setSecondOperandType(Global.register);
							
							//Suanki buyruk sonraki durumuna geldigi icin currentInstruction'i duzenlememiz gerekiyor.
							currentInstruction = newCurrentInstruction;
						}
					}
					
					/*
					 * Ikinci operand'da olmamasi gereken durumlar
					 */
					else if(currentInstruction.getSecondOperandType() == Global.bytePtrOnlyAddress || 
						currentInstruction.getSecondOperandType() == Global.wordPtrOnlyAddress ||
						currentInstruction.getSecondOperandType() == Global.dWordPtrOnlyAddress || 
						currentInstruction.getSecondOperandType() == Global.qWordPtrOnlyAddress || 
						currentInstruction.getSecondOperandType() == Global.bytePtrOnlyRegister || 
						currentInstruction.getSecondOperandType() == Global.wordPtrOnlyRegister ||
						currentInstruction.getSecondOperandType() == Global.dWordPtrOnlyRegister || 
						currentInstruction.getSecondOperandType() == Global.qWordPtrOnlyRegister ||
						currentInstruction.getSecondOperandType() == Global.bytePtrAddressAddress || 
						currentInstruction.getSecondOperandType() == Global.bytePtrAddressRegister || 
						currentInstruction.getSecondOperandType() == Global.bytePtrRegisterAddress || 
						currentInstruction.getSecondOperandType() == Global.bytePtrRegisterRegister || 
						currentInstruction.getSecondOperandType() == Global.wordPtrAddressAddress || 
						currentInstruction.getSecondOperandType() == Global.wordPtrAddressRegister || 
						currentInstruction.getSecondOperandType() == Global.wordPtrRegisterAddress || 
						currentInstruction.getSecondOperandType() == Global.wordPtrRegisterRegister || 
						currentInstruction.getSecondOperandType() == Global.dWordPtrAddressAddress || 
						currentInstruction.getSecondOperandType() == Global.dWordPtrAddressRegister || 
						currentInstruction.getSecondOperandType() == Global.dWordPtrRegisterAddress	||
						currentInstruction.getSecondOperandType() == Global.dWordPtrRegisterRegister || 
						currentInstruction.getSecondOperandType() == Global.qWordPtrAddressAddress || 
						currentInstruction.getSecondOperandType() == Global.qWordPtrAddressRegister || 
						currentInstruction.getSecondOperandType() == Global.qWordPtrRegisterAddress || 
						currentInstruction.getSecondOperandType() == Global.qWordPtrRegisterRegister)
					{
						errors.addError(eList.GetError(eList.WRONG_PTR_USAGE_SEM));//Kuyruga yeni hatayi at.
						return false;
					}
				}
			}
			
			if(currentInstruction.getOperandCount() > 0)
			{
				boolean operandResult = false;
				if(currentInstruction.getOperandCount() == 1)
				{
					operandResult = si.areOperandsLegal(currentInstruction.getOpcode(),currentInstruction.getFirstOperandType());
				}
				else if(currentInstruction.getOperandCount() == 2)
				{
					operandResult = si.areOperandsLegal(currentInstruction.getOpcode(),currentInstruction.getFirstOperandType(),currentInstruction.getSecondOperandType());
				}
				
				if(operandResult == false)
				{
					errors.addError(eList.GetError(eList.WRONG_OPERAND_USAGE));//Kuyruga yeni hatayi at.
					return false;
				}
			}

			//Eger register-register operand kombinasyonu kullaniliyorsa
			//Bu yazmaclarin boyutlari tutuyor mu kontrolu yapalim.
			if(currentInstruction.getOperandCount() == 2)
			{
				String firstOperand = currentInstruction.getFirstOperand();
				int firstOperandType = currentInstruction.getFirstOperandType();
				String secondOperand = currentInstruction.getSecondOperand();
				int secondOperandType = currentInstruction.getSecondOperandType();
				
				//Eger ikinci operand yazmac ise
				if(secondOperandType == Global.register)
				{
					int found2 = -1, k = 0;
					while(k<Global.registerList.length && found2 == -1)
					{
						if(Global.registerList[k].equals(secondOperand))
						{
							found2 = k;
						}
						k++;
					}
					
					//Eger ilk operand da yazmac ise
					if(firstOperandType == Global.register)
					{
						int found1 = -1, t = 0;
						while(t<Global.registerList.length && found1 == -1)
						{
							if(Global.registerList[t].equals(firstOperand))
							{
								found1 = t;
							}
							t++;
						}
						if(Global.registerSizes[found1] != Global.registerSizes[found2])	
						{
							errors.addError(eList.GetError(eList.REGISTER_SIZE_MISMATCH));//Kuyruga yeni hatayi at.
							return false;
						}
					}
					//Eger ilk operand byte, word, dword veya qword ptr ise
					//Ikinci yazmacin bunlara uygun bir yazmac boyutuna sahip olmasi gereklidir.
					else if(firstOperandType == Global.bytePtrOnlyRegister)
					{
						if(Global.registerSizes[found2] != 1)
						{
							errors.addError(eList.GetError(eList.REGISTER_SIZE_MISMATCH));//Kuyruga yeni hatayi at.
							return false;
						}
					}
					else if(firstOperandType == Global.wordPtrOnlyRegister)
					{
						if(Global.registerSizes[found2] != 2)
						{
							errors.addError(eList.GetError(eList.REGISTER_SIZE_MISMATCH));//Kuyruga yeni hatayi at.
							return false;
						}
					}
					else if(firstOperandType == Global.dWordPtrOnlyRegister)
					{
						if(Global.registerSizes[found2] != 4)
						{
							errors.addError(eList.GetError(eList.REGISTER_SIZE_MISMATCH));//Kuyruga yeni hatayi at.
							return false;
						}
					}
					else if(firstOperandType == Global.qWordPtrOnlyRegister)
					{
						if(Global.registerSizes[found2] != 8)
						{
							errors.addError(eList.GetError(eList.REGISTER_SIZE_MISMATCH));//Kuyruga yeni hatayi at.
							return false;
						}
					}
				}
			}
			
			//Eger buyruk lea ise mov offset'e donusturelim.
			if(currentInstruction.getOpcode().equals("LEA"))
			{
				currentInstruction.setOpcode("MOV");
				currentInstruction.setSecondOperandType(Global.offset);
			}
			else if(currentInstruction.getOpcode().equals("NEG"))
			{
				currentInstruction.setOpcode("NOT");
				
				Instruction newInstruction = null;
				
				newInstruction = new Instruction("ADD " + currentInstruction.getFirstOperand() + " 1", "ADD", currentInstruction.getFirstOperand(), currentInstruction.getFirstOperandType(), "1", Global.decimalNumber);
				newInstruction.setInstructionAddress(currentInstruction.getInstructionAddress()+2);
				instructionList.insertElementAt(newInstruction, index+1);
				
				//Simdi geri kalan buyruklari sirasiyla 2 byte ileri atalim.
				for(int i=index+2; i < instructionList.size(); i++)
				{
					instructionList.get(i).setInstructionAddress(instructionList.get(i).getInstructionAddress()+2);
					if(instructionList.get(i).getFirstOperandType() == Global.label)
					{
						instructionList.get(i).setFirstOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getFirstOperand()) + 6));
					}
					else if(instructionList.get(i).getFirstOperandType() == Global.variable)
					{
						for(int j=0; j<tmpLabelNames.size(); j++)
						{
							if(tmpLabelNames.get(j).equals(instructionList.get(i).getFirstOperand()))
							{
								instructionList.get(i).setFirstOperand(Integer.toString(tmpLabelAddresses.get(j) + 2));
								tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+2);
								instructionList.get(i).setFirstOperandType(Global.label);
							}
						}
					}
					if(instructionList.get(i).getSecondOperandType() == Global.label)
					{
						instructionList.get(i).setSecondOperand(Integer.toString(Integer.parseInt(instructionList.get(i).getSecondOperand()) + 2));
					}
					else if(instructionList.get(i).getSecondOperandType() == Global.variable)
					{
						for(int j=0; j<tmpLabelNames.size(); j++)
						{
							if(tmpLabelNames.get(j).equals(instructionList.get(i).getSecondOperand()))
							{
								instructionList.get(i).setSecondOperand(Integer.toString(tmpLabelAddresses.get(j) + 2));
								tmpLabelAddresses.set(j, tmpLabelAddresses.get(j)+2);
								instructionList.get(i).setSecondOperandType(Global.label);
							}
						}
					}
				}
			}
		}
		//Yapilan islemlerden sonra code segment'te yer alan buyruklarin
		//Operand olarak barindirdiklari variable ve label'lari direct address'ler ile degistirmis olduk.
		return true;
	}
	
	public boolean isSemanticAnalysisSucceeded()
	{
		return semanticAnalysisSucceeded;
	}
	public Vector<Instruction> getInstructionList()
	{
		return instructionList;
	}
	public Vector<VarFactor> getDataSegmentAddressList()
	{
		return dataSegmentAddressList;
	}
	public int getStackSize()
	{
		return stackSize;
	}
}