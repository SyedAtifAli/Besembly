package Errors;

import java.util.Vector;

import Root.Global;

public class ErrorList extends Vector<Error>
{	
	private static final long serialVersionUID = 1L;
	
	private static ErrorList instance = null;
	public static ErrorList get() 
	{
	    if(instance == null) 
	    {
	        instance = new ErrorList();
	    }
	    return instance;
	}
	
	//SYNTAX HATALARI
	public final int CODE_IS_EMPTY = 1;
	public final int COMMENT_LINE_NOT_CLOSED = 2;
	public final int COMMENT_LINE_NOT_OPENED = 3;
	public final int CONTENT_CONTAINS_ONLY_COMMENT = 4;
	public final int UNEXPECTED_SYNTAX_ERROR = 5;
	public final int LABEL_NAME_LENGTH_IS_INCORRECT = 6;
	public final int LABEL_NAME_STARTS_WITH_NUMBER = 7;
	public final int LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER = 8;
	public final int INSTRUCTIVE_COMMENT_IS_WRONGLY_USED = 9;
	public final int INSTRUCTIVE_COMMENT_ILLEGAL_DELIMITER = 10;
	public final int INSTRUCTIVE_COMMENT_WRONG_LENGTH = 11;
	public final int INSTRUCTIVE_COMMENT_DELIMITER_MISSING = 12;
	public final int ADDRESS_TAG_IS_NOT_CLOSED = 13;
	public final int WRONG_BINARY_NUMBER = 14;
	public final int WRONG_HEXADECIMAL_NUMBER = 15;
	public final int WRONG_DECIMAL_NUMBER = 16;
	public final int WRONG_NUMBER = 17;
	public final int WRONG_REGISTER_USAGE_BETWEEN_ADDRESS_TAGS = 18;
	public final int TOO_MUCH_PLUS_OCCURRENCE_BETWEEN_ADDRESS_TAGS = 19;
	public final int ILLEGAL_ADDRESING = 20;
	public final int WRONG_STRING_USAGE = 21;
	public final int WRONG_MODEL_DIRECTIVE = 22;
	public final int WRONG_STACK_DIRECTIVE = 23;
	public final int DUP_IS_WRONGLY_USED = 24;
	public final int DUP_PARENTHESES_IS_NOT_CLOSED = 25;
	public final int DUP_PARENTHESES_IS_EMPTY = 26;
	public final int DUP_PARENTHESIS_INSIDE_CONTAINS_WRONG_CHAR = 27;
	public final int DUP_PARENTHESIS_CONTAINS_NON_NUMERIC_VALUE = 28;
	public final int WRONG_SEGMENT_INITIALIZION = 29;
	public final int VARIABLE_NAME_LENGTH_IS_INCORRECT = 30;
	public final int VARIABLE_NAME_STARTS_WITH_NUMBER = 31;
	public final int VARIABLE_NAME_CONTAINS_ILLEGAL_CHARACTER = 32;
	
	public final int WRONG_CHAIN_INSTRUCTION_USAGE = 33;
	public final int EXPECTING_SPACE = 34;
	public final int EXPECTING_COMMA = 35;
	public final int EXPECTING_SPACE_OR_COMMA = 36;
	public final int UNEXPECTED_OPERAND = 37;
	public final int UNEXPECTED_VARIABLE_SIZE_DEFINER = 38;
	public final int UNEXPECTED_VARIABLE_NUMBER = 39;
	public final int WRONG_STACK_DIRECTIVE_USAGE = 40;
	public final int WRONG_OFFSET_USAGE = 41;
	public final int OPERAND_LIMIT_EXCEEDED = 42;
	
	public final int WRONG_INDIRECT_ADDRESSING_DIRECTIVE_USAGE = 43;
	
	public final int WRONG_END_DIRECTIVE_USAGE = 44;
	
	//SISTEM HATALARI
	public final int SYSTEM_ERROR_1 = 1001;
	public final int SMALL_MODEL_IS_NOT_SUPPORTED_YET = 1002;
	
	//SEMANTIC HATALAR
	public final int UNEXPECTED_SEMANTIC_ERROR = 2001;
	public final int MODEL_DIRECTIVE_IS_NOT_AT_FIRST = 2002;
	public final int FIRST_FRAGMENT_IS_NOT_DIRECTIVE = 2003;
	public final int MEMORY_DIRECTIVE_USED_MORE_THAN_ONCE = 2004;
	public final int CODE_DIRECTIVE_USED_MORE_THAN_ONCE = 2005;
	public final int DATA_DIRECTIVE_USED_MORE_THAN_ONCE = 2006;
	public final int STACK_DIRECTIVE_USED_MORE_THAN_ONCE = 2007;
	public final int CODE_STACK_USED_BEFORE_STACK = 2008;
	public final int CODE_STACK_USED_BEFORE_DATA = 2009;
	public final int STACK_PARAMETER_IS_NOT_NUMERIC = 2010;
	public final int DATA_DIRECTIVE_NOT_FOUND = 2011;
	public final int CODE_DIRECTIVE_NOT_FOUND = 2012;
	public final int DATA_DIRECTIVE_FOUND = 2013;
	public final int DATA_DIRECTIVE_USED_IN_WRONG_PLACE = 2014;
	
	public final int DATA_SEGMENT_CONTAINS_NON_VARIABLE_FRAGMENT = 2015;
	public final int VARIABLE_VALUE_TOO_LARGE = 2016;
	public final int ARRAY_DEFINITION_CANNOT_BE_FORMED_BY_STRING = 2017;
	public final int VARIABLE_DECLARATION_IN_CODE_SEGMENT = 2018;
	public final int SEGMENT_DECLARATION_IN_CODE_SEGMENT = 2019;
	public final int STACK_DECLARATION_IN_CODE_SEGMENT = 2020;
	public final int MODEL_DECLARATION_IN_CODE_SEGMENT = 2021;
	
	public final int MULTIPLE_STARTUP_DECLARATION = 2022;
	public final int STARTUP_USAGE_OUTSIDE_OF_CS = 2023;
	public final int EXIT_USAGE_OUTSIDE_OF_CS = 2024;
	public final int EXIT_USAGE_BEFORE_STARTUP_DECLARATION = 2025;
	public final int MULTIPLE_EXIT_DECLARATION = 2026;
	
	public final int VARIABLE_NAME_IS_ALREADY_DEFINED = 2027;
	public final int LABEL_NAME_IS_ALREADY_DEFINED = 2028;
	
	public final int INSTRUCTION_OPERAND_NUMBER_IS_WRONG = 2029;
	
	public final int WRONG_REGISTER_USAGE = 2030;
	
	public final int WRONG_EXIT_USAGE = 2031;
	
	public final int UNDEFINED_VARIABLE = 2032;
	public final int UNDEFINED_LABEL = 2033;
	
	public final int WRONG_OFFSET_USAGE_SEM = 2034;
	public final int WRONG_SEGMENT_INIT_USAGE_SEM = 2035;
	public final int WRONG_ADDRESS_USAGE_SEM = 2036;
	public final int WRONG_PTR_USAGE_SEM = 2037;
	
	public final int WRONG_OPERAND_USAGE = 2038;
	
	public final int REGISTER_SIZE_MISMATCH = 2039;
	
	public final int UNDEFINED_LABEL_NAME = 2040;
	public final int UNDEFINED_DIRECTIVE_USAGE = 2041;
	
	public final int STACK_DIRECTIVE_NOT_FOUND = 2042;
	
	///RUNTIME HATALARI
	public final int WRONG_FILE_CONTENT = 3001;

	protected ErrorList()
	{
		super();	
		FillVector();
	}
	
	private void FillVector()
	{
		//Syntax Hatalari
		this.add(new SyntaxError(CODE_IS_EMPTY,"Code is empty.","You should type at least one besemble instruction."));
		
		this.add(new SyntaxError(COMMENT_LINE_NOT_CLOSED,"Comment line is not closed.","Line that starts with comment tag /* must be ended with tag */."));
		this.add(new SyntaxError(COMMENT_LINE_NOT_OPENED,"Unopened comment line tag usage.","You should use /* before using */"));
		this.add(new SyntaxError(CONTENT_CONTAINS_ONLY_COMMENT,"Content of besemble code contains only comment line.","You should type more meaningful lines."));
		
		this.add(new SyntaxError(UNEXPECTED_SYNTAX_ERROR,"Unexpected syntax error.","An error has occurred unexpectedly."));
		
		this.add(new SyntaxError(LABEL_NAME_LENGTH_IS_INCORRECT,"Label name's length is incorrect.","Label name's length must be between 1-31"));
		this.add(new SyntaxError(LABEL_NAME_STARTS_WITH_NUMBER,"Label name starts with number.","Label name should start with a letter or a special character."));
		this.add(new SyntaxError(LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER,"Label name contains illegal character.","Label name can only contain letters, numbers or ?.@_$% characters."));
		
		this.add(new SyntaxError(INSTRUCTIVE_COMMENT_IS_WRONGLY_USED,"Instructive COMMENT is used wrongly.","Instructive COMMENT must be followed by space or new line."));
		this.add(new SyntaxError(INSTRUCTIVE_COMMENT_ILLEGAL_DELIMITER,"Instructive COMMENT's delimiter contains wrong characters.","Instructive COMMENT's delimiter can be only letter, number or one of the ?.@_$% characters."));
		this.add(new SyntaxError(INSTRUCTIVE_COMMENT_WRONG_LENGTH,"Instructive COMMENT is used wrongly.","Instructive COMMENT's delimiter should be only 1 character."));
		this.add(new SyntaxError(INSTRUCTIVE_COMMENT_DELIMITER_MISSING,"Instructive COMMENT's ending delimiter is missing.","Instructive COMMENT must be ended with same delimiter that used at first."));
		
		this.add(new SyntaxError(ADDRESS_TAG_IS_NOT_CLOSED,"Address tag '[' is not closed with ']'.","Address tag must be closed by ']'."));
		
		this.add(new SyntaxError(WRONG_BINARY_NUMBER,"Binary number represented in a wrong form.","Binary numbers can contain 0's, 1's with b at the end."));
		this.add(new SyntaxError(WRONG_HEXADECIMAL_NUMBER,"Hexadecimal number represented in a wrong form.","Hexadecimal numbers can contain 0-9 and A-F with h at the end."));
		this.add(new SyntaxError(WRONG_DECIMAL_NUMBER,"Decimal number represented in a wrong form.","Decimal numbers can contain 0-9 with or without d at the end."));
		this.add(new SyntaxError(WRONG_NUMBER,"Numbers can be decimal, hexadecimal or binary.","Number is represented in a wrong form."));
		
		this.add(new SyntaxError(WRONG_REGISTER_USAGE_BETWEEN_ADDRESS_TAGS,"Invalid register name usage detected.","You can use only supported registers between two address tags."));
		this.add(new SyntaxError(TOO_MUCH_PLUS_OCCURRENCE_BETWEEN_ADDRESS_TAGS,"Address tags can handle only one plus sign.","You can use only one plus between two address tags."));
		this.add(new SyntaxError(ILLEGAL_ADDRESING,"Invalid address usage detected.","Address tags cannot handle current form."));
		
		this.add(new SyntaxError(WRONG_STRING_USAGE,"Wrong string usage detected.","String must be started and ended with \" or \' delimiter."));
		
		this.add(new SyntaxError(WRONG_MODEL_DIRECTIVE,"Model directive cannot take that parameter.","Model directive can take only TINY, SMALL, MEDIUM, COMPACT, LARGE, HUGE, FLAT parameters."));
		this.add(new SyntaxError(WRONG_STACK_DIRECTIVE,"Stack directive can be followed by numbers.","Stack directive parameter must be numeric."));
		
		this.add(new SyntaxError(DUP_IS_WRONGLY_USED,"DUP operator is wrongly used.", "DUP operator must be in form DUP(number)"));
		this.add(new SyntaxError(DUP_PARENTHESES_IS_NOT_CLOSED,"DUP operator is wrongly used.", "DUP operator must be in form DUP(number)"));
		this.add(new SyntaxError(DUP_PARENTHESES_IS_EMPTY,"DUP operator must contain number.", "DUP operator must be in form DUP(number)"));
		this.add(new SyntaxError(DUP_PARENTHESIS_INSIDE_CONTAINS_WRONG_CHAR,"DUP operator must contain only numbers and spaces.", "DUP operator must be in form DUP(number)"));
		this.add(new SyntaxError(DUP_PARENTHESIS_CONTAINS_NON_NUMERIC_VALUE,"DUP operator contains non-numeric value.", "DUP operator must be in form DUP(number)"));
		
		this.add(new SyntaxError(WRONG_SEGMENT_INITIALIZION, "Wrong segment initialization.", "Segment initialization tag @ must be followed by DATA."));
		
		this.add(new SyntaxError(VARIABLE_NAME_LENGTH_IS_INCORRECT,"Variable name's length is incorrect.","Variable name's length must be between 3-31"));
		this.add(new SyntaxError(VARIABLE_NAME_STARTS_WITH_NUMBER,"Variable name starts with number.","Variable name should start with a letter or a special character."));
		this.add(new SyntaxError(VARIABLE_NAME_CONTAINS_ILLEGAL_CHARACTER,"Variable name contains illegal character.","Variable name can only contain letters, numbers or ?.@_$% characters."));
		
		this.add(new SyntaxError(WRONG_CHAIN_INSTRUCTION_USAGE, "Wrong chain instruction usage.", "Chain instructions can be followed by only one instruction which does not have any operand."));
		this.add(new SyntaxError(EXPECTING_SPACE, "Unexpected character usage instead of space.", "Previous token must be followed by space."));
		this.add(new SyntaxError(EXPECTING_COMMA, "Unexpected character usage instead of comma.", "Previous token must be followed by comma."));
		this.add(new SyntaxError(EXPECTING_SPACE_OR_COMMA, "Unexpected character usage instead of space or comma.", "Previous token must be followed by space or comma."));
		this.add(new SyntaxError(UNEXPECTED_OPERAND, "Unexpected operand usage on instruction.", "Operands could be register, address, immediate, opcode or label."));
		this.add(new SyntaxError(UNEXPECTED_VARIABLE_SIZE_DEFINER, "Unexpected variable size definer usage.", "Variable name must be followed by size definer directive."));
		this.add(new SyntaxError(UNEXPECTED_VARIABLE_NUMBER, "Unexpected variable number usage.", "Variable size definer must be followed by a number."));
		this.add(new SyntaxError(WRONG_STACK_DIRECTIVE_USAGE, "Wrong stack directive usage.", "Stack directive must be followed by a number."));
		this.add(new SyntaxError(WRONG_OFFSET_USAGE, "Wrong offset definition usage.", "Offset tag must be followed by a variable."));
		
		this.add(new SyntaxError(OPERAND_LIMIT_EXCEEDED, "Operand limit has been exceeded.", "Maximum operand count could be " + Global.maxOperandOfInstruction));
		
		this.add(new SyntaxError(WRONG_INDIRECT_ADDRESSING_DIRECTIVE_USAGE, "Wrong indirect addressing directive usage.", "Indirect addressing directive can be used like ... ptr [Address]"));
		
		this.add(new SyntaxError(WRONG_END_DIRECTIVE_USAGE, "Wrong end directive usage.", "END directive should be used with a label name"));
		
		//Sistem Hatalari
		this.add(new SystemError(SYSTEM_ERROR_1, "An unexpected system error has occurred. Error No: " + SYSTEM_ERROR_1, "Please contact with development desk."));
		this.add(new SystemError(SMALL_MODEL_IS_NOT_SUPPORTED_YET, "Small model type is not supported yet.", "You should use large model instead of small."));
		
		//Semantic Hatalar
		this.add(new SemanticError(UNEXPECTED_SEMANTIC_ERROR,  "An unexpected semantic error has occurred. Error No: " + UNEXPECTED_SEMANTIC_ERROR, "Please contact with development desk."));
		this.add(new SemanticError(MODEL_DIRECTIVE_IS_NOT_AT_FIRST, "Model directive is not first directive.", "Model directive should be used before other directives."));
		this.add(new SemanticError(FIRST_FRAGMENT_IS_NOT_DIRECTIVE, "Segment directive definition could not be found at first.", "You should define segments by using segment directives at first."));
		this.add(new SemanticError(MEMORY_DIRECTIVE_USED_MORE_THAN_ONCE, "More than once memory type directive declarations have been found.", "You should define only one memory type directive declaration."));
		this.add(new SemanticError(CODE_DIRECTIVE_USED_MORE_THAN_ONCE, "More than once code segment declarations have been found.", "You should define only one code segment."));
		this.add(new SemanticError(DATA_DIRECTIVE_USED_MORE_THAN_ONCE, "More than once data segment declarations have been found.", "You should define only one data segment."));
		this.add(new SemanticError(STACK_DIRECTIVE_USED_MORE_THAN_ONCE, "More than once stack segment declarations have been found.", "You should define only one stack segment."));
		this.add(new SemanticError(CODE_STACK_USED_BEFORE_STACK, "Code segment directive has been used before stack segment directive.", "You should declare stack segment directive before code segment directive."));
		this.add(new SemanticError(CODE_STACK_USED_BEFORE_DATA, "Code segment directive has been used before data segment directive.", "You should declare data segment directive before code segment directive."));
		this.add(new SemanticError(STACK_PARAMETER_IS_NOT_NUMERIC, "Non-numeric characters have been found in stack parameter.", "Stack parameter must contain only numeric characters."));
		this.add(new SemanticError(DATA_DIRECTIVE_NOT_FOUND, "Data segment declaration could not be found.", "In this memory type, data directive must be declared."));
		this.add(new SemanticError(CODE_DIRECTIVE_NOT_FOUND, "Code segment declaration could not be found.", "In this memory type, code directive must be declared."));
		this.add(new SemanticError(DATA_DIRECTIVE_FOUND, "Data segment declaration has been found.", "In this memory type, data directive cannot be declared."));
		this.add(new SemanticError(DATA_DIRECTIVE_USED_IN_WRONG_PLACE, "Data segment declaration has been used in a wrong place.", "In this memory type, data directive must be used before code segment directive declaration."));
		
		this.add(new SemanticError(DATA_SEGMENT_CONTAINS_NON_VARIABLE_FRAGMENT, "Data segment contains a non-variable fragment.", "Data segment must be formed by only variables."));
		this.add(new SemanticError(VARIABLE_VALUE_TOO_LARGE, "Variable value is larger than its definer's capacity.", "You should get the value to fit for its definer's capacity."));
		this.add(new SemanticError(ARRAY_DEFINITION_CANNOT_BE_FORMED_BY_STRING, "Array definition in Data Segment should be formed by characters or numbers. ", "String array definition is forbidden."));
		
		this.add(new SemanticError(VARIABLE_DECLARATION_IN_CODE_SEGMENT, "Variable declaration in the code segment.", "Variable declarations are only allowed in data segment."));
		this.add(new SemanticError(SEGMENT_DECLARATION_IN_CODE_SEGMENT, "Segments cannot be declared in another segment. ", "You should define segments at outside of other segments."));
		this.add(new SemanticError(STACK_DECLARATION_IN_CODE_SEGMENT, "Stack size cannot be declared in code segment.", "You should define stack size at outside of other segments."));
		this.add(new SemanticError(MODEL_DECLARATION_IN_CODE_SEGMENT, "Memory type cannot be declared in code segment.", "You should define memory type at outside of other segments."));
		
		this.add(new SemanticError(STARTUP_USAGE_OUTSIDE_OF_CS, ".startup directive usage at outside of code segment.", "Startup directive should be used in code segment."));
		this.add(new SemanticError(EXIT_USAGE_OUTSIDE_OF_CS, ".exit directive usage at outside of code segment.", "Exit directive should be used in code segment."));
		this.add(new SemanticError(EXIT_USAGE_BEFORE_STARTUP_DECLARATION, ".exit directive usage before declaring .startup", "Startup directive should be used before exit."));
		this.add(new SemanticError(MULTIPLE_EXIT_DECLARATION, "Multiple .exit declaration in code segment.", "Only one .exit directive declaration is allowed."));
		this.add(new SemanticError(MULTIPLE_STARTUP_DECLARATION, "Multiple .startup declaration in code segment.", "Only one .startup directive declaration is allowed."));
		
		this.add(new SemanticError(VARIABLE_NAME_IS_ALREADY_DEFINED, "Variable name has been alread defined.", "You should choose another variable name."));
		this.add(new SemanticError(LABEL_NAME_IS_ALREADY_DEFINED, "Label name has been alread defined.", "You should choose another label name."));
		
		this.add(new SemanticError(INSTRUCTION_OPERAND_NUMBER_IS_WRONG, "Instruction operand count does not match with entered instruction's operand.", "You should type the instruction properly."));
		
		this.add(new SemanticError(WRONG_REGISTER_USAGE, "Wrong register usage on instruction.", "You should type the instruction properly."));
		this.add(new SemanticError(WRONG_EXIT_USAGE, "Wrong exit usage in code segment.", "Exit directive should be used in the end of code segment."));
	
		this.add(new SemanticError(UNDEFINED_VARIABLE, "Undefined variable usage in code segment.", "You should define the relevant variable in data segment."));
		this.add(new SemanticError(UNDEFINED_LABEL, "Undefined label usage in code segment.", "You should define the relevant label firstly."));
		
		this.add(new SemanticError(WRONG_OFFSET_USAGE_SEM, "Wrong offset usage.", "Offset directive should be used in second operand."));
		this.add(new SemanticError(WRONG_SEGMENT_INIT_USAGE_SEM, "Wrong segment initialization usage.", "Segment initialization directive should be used in second operand."));
		this.add(new SemanticError(WRONG_ADDRESS_USAGE_SEM, "Wrong address usage.", "Addresses in instructions that are used in first operand should be identified by byte directives."));
		this.add(new SemanticError(WRONG_PTR_USAGE_SEM, "Wrong ptr directive usage.", "Ptr directive in instructions should be used in first operand."));
		
		this.add(new SemanticError(WRONG_OPERAND_USAGE, "Wrong operand usage on instruction.", "Operand(s) have used wrongly."));

		this.add(new SemanticError(REGISTER_SIZE_MISMATCH, "Register sizes do not match.", "You should use registers with same size."));
		
		this.add(new SemanticError(UNDEFINED_LABEL_NAME, "Undefined label name usage with end directive.", "You should define the label name precedingly."));
		this.add(new SemanticError(UNDEFINED_DIRECTIVE_USAGE, "Undefined directive usage in code segment.", "Unexpected directive usage detected in code segment."));
		
		this.add(new SemanticError(STACK_DIRECTIVE_NOT_FOUND, "Stack directive is not found.", "You should declare the stack directive before segments."));
		
		//Runtime Hatalari
		this.add(new RuntimeError(WRONG_FILE_CONTENT, "File cannot be invoked.", "File has corrupted."));
		//this.add(new RuntimeError(, "", ""));
	}
	public Error GetError(int errorCode)
	{
		Error result = null;
		
		for(int i=0; i<this.size(); i++)
		{
			if(this.get(i).GetErrorCode() == errorCode)
			{
				result = this.get(i);
				break;
			}
		}
		return result;
	}
}
