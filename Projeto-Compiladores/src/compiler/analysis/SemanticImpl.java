package compiler.analysis;

import java.util.*;
import compiler.core.*;
import compiler.generator.CodeGenerator;
import compiler.util.Calculator;

public class SemanticImpl {

	public static int lineError = 0;

	private HashMap<String, Variable> variables = new HashMap<String, Variable>();
	private static SemanticImpl singleton;
	static CodeGenerator codeGenerator;
	private static Calculator calculator;
	private static List<Type> BASIC_TYPES;
	private static Map<String, List<String>> tiposCompativeis = new HashMap<String, List<String>>();
	private static List<String> testingOperators = new ArrayList<String>();
	private List<Variable> tempVariables = new ArrayList<Variable>();
	private Stack<ScopedEntity> scopeStack = new Stack<ScopedEntity>();
	private List<Type> secondaryTypes = new ArrayList<Type>();
	private ArrayList<Function> functions_ = new ArrayList<Function>();
	HashMap<String, ArrayList<Object>> funcParams = new HashMap<String, ArrayList<Object>>();
	HashMap<String, ArrayList<String>> procedures = new HashMap<String, ArrayList<String>>();
	HashMap<String, Object> functions = new HashMap<String, Object>();
	private ArrayList<String> identifiers = new ArrayList<String>();
	private Boolean scope;

	public static SemanticImpl getInstance() {
		if (singleton == null) {
			singleton = new SemanticImpl();
			codeGenerator = CodeGenerator.getInstance();
			calculator = new Calculator();
			initCollections();
		}
		return singleton;
	}

	public static void destroy() {
		singleton = null;
	}

	private static void initCollections() {
		initBasicTypes();
		initTypeCompatibility();
		iniTestingOperators();
	}

	private static void initBasicTypes() {
		BASIC_TYPES = new ArrayList<Type>() {
			{
				add(new Type("integer"));
				add(new Type("real"));
				add(new Type("char"));
				add(new Type("boolean"));
				add(new Type("enumerated"));
				add(new Type("subrange"));
				add(new Type("array"));
				add(new Type("record"));
				add(new Type("file"));
				add(new Type("set"));
			}
		};
	}

	private static void initTypeCompatibility() {
		List<String> realCompTypes = new ArrayList<String>();
		realCompTypes.add("integer");
		realCompTypes.add("real");

		List<String> intCompTypes = new ArrayList<String>();
		intCompTypes.add("integer");

		List<String> booleanCompTypes = new ArrayList<String>();
		booleanCompTypes.add("boolean");

		List<String> arrayCompTypes = new ArrayList<String>();
		booleanCompTypes.add("array");

		tiposCompativeis.put("real", realCompTypes);
		tiposCompativeis.put("integer", intCompTypes);
		tiposCompativeis.put("boolean", booleanCompTypes);
		tiposCompativeis.put("array", arrayCompTypes);
	}

	private static void iniTestingOperators() {
		testingOperators.add("<");
		testingOperators.add(">");
		testingOperators.add("<=");
		testingOperators.add(">=");
		testingOperators.add("=");
		testingOperators.add("<>");
	}

	public void addVariablesFromTempList(Type type) throws Exception {
		if (!checkValidExistingType(type)) {
			throw new Exception("Type doesn't exist!");
		}
		for (Variable variable : tempVariables) {
			variable.setType(type);
			addVariable(variable);
		}

		tempVariables = new ArrayList<Variable>();
	}

	public void addVariable(Variable variable) throws Exception {
		if (checkValidExistingType(new Type(variable.getIdentifier()))) {
			throw new Exception("Duplicate identifier");
		}
		if (scopeStack.isEmpty()) {
			validateVariableGlobal(variable);

			variables.put(variable.getIdentifier(), variable);
		} else {
			validateVariable(variable);
			getCurrentScope().addVariable(variable);
		}

		if (variable.getValue() != null) {
			checkVariableAttribution(variable.getIdentifier(), variable.getValue());
		}
	}

	private void validateVariableGlobal(Variable variable) throws Exception {
		if (checkVariableExistenceGlobal(variable.getIdentifier())) {
			throw new Exception("Name already exists");
		}
		if (!checkValidExistingType(variable.getType())) {
			throw new Exception("Type non existing");
		}
	}

	public boolean checkVariableExistenceGlobal(String variableName) {
		return variables.get(variableName) != null ? true : false;
	}

	private void validateVariable(Variable variable) throws Exception {
		if (checkVariableExistenceLocal(variable.getIdentifier())) {
			throw new Exception("Name already exists");
		}
		if (!checkValidExistingType(variable.getType())) {
			throw new Exception("Type non existing");
		}
	}

	public boolean checkVariableExistenceLocal(String variableName) {
		if (!scopeStack.isEmpty() && getCurrentScope().getVariable().get(variableName) != null) {
			return true;
		} else {
			return false;
		}
	}

	public ScopedEntity getCurrentScope() {
		return scopeStack.peek();
	}

	public boolean checkValidExistingType(Type type) {
		return BASIC_TYPES.contains(type) || secondaryTypes.contains(type);
	}

	public void checkVariableAttribution(String id, String function) throws Exception {
		if (!checkVariableExistence(id)) {
			throw new Exception("Variable doesn't exist");
		}
		Type identifierType = findVariableByIdentifier(id).getType();

		for (Function f : functions_) {
			if (f.getName().equals(function)) {
				if (!checkTypeCompatibility(identifierType, f.getDeclaredReturnType())) {
					String exceptionMessage = String.format("Incompatible types! %s doesn't match %s", identifierType,
							f.getDeclaredReturnType());
					throw new Exception(exceptionMessage);
				}
			}
		}
	}

	public boolean checkVariableExistence(String variableName) {
		if (!scopeStack.isEmpty() && getCurrentScope().getVariable().get(variableName) != null) {
			return true;
		} else {
			return variables.get(variableName) != null ? true : false;
		}
	}

	public Variable findVariableByIdentifier(String variableName) throws Exception {
		Variable var;
		if (!scopeStack.isEmpty() && getCurrentScope().getVariable().get(variableName) != null) {
			var = getCurrentScope().getVariable().get(variableName);
		} else {
			var = variables.get(variableName);
		}
		if (var == null) {
			throw new Exception("'" + variableName + "' not declared");
		}
		return var;
	}

	public Expression getExpressionCheckingError(Expression leftExpression, Expression rightExpression)
			throws Exception {
		if (rightExpression == null) {
			return leftExpression;
		}
		if (!checkTypeCompatibility(leftExpression.getType(), rightExpression.getType())) {
			throw new Exception("Incompatible types! " + leftExpression.getType() + " " + rightExpression.getType());
		}
		return new Expression(getMajorType(leftExpression.getType(), rightExpression.getType()));
	}

	public boolean checkTypeCompatibility(Type leftType, Type rightType) {
		if (leftType.equals(rightType)) {
			return true;
		} else {
			List<String> tipos = tiposCompativeis.get(leftType.getName());
			if (tipos == null)
				return false;
			return tipos.contains(rightType.getName());
		}
	}

	public boolean checkTypeExists(Type type) throws Exception {
		boolean existsType = BASIC_TYPES.contains(type) || secondaryTypes.contains(type);
		if (!existsType) {
			throw new Exception("Type '" + type.toString() + "' doesn't exists!");
		}
		return existsType;
	}

	public void checkVariableAttribution(String id, Expression expression) throws Exception {
		if (!checkVariableExistence(id)) {
			throw new Exception("Variable doesn't exist");
		}
		if (!expression.getType().equals(new Type("null")) && !checkValidExistingType(expression.getType())) {
			throw new Exception("Type non existing");
		}
		Type identifierType = findVariableByIdentifier(id).getType();
		if (expression.getType().equals(new Type("null"))) {
			return;
		}
		if (!checkTypeCompatibility(identifierType, expression.getType())) {
			String exceptionMessage = String.format("Incompatible types! %s doesn't match %s", identifierType,
					expression.getType());
			throw new Exception(exceptionMessage);
		}
	}

	public void typeIsNumber(Type type) throws Exception {
		if (!type.getName().equals("integer") && !type.getName().equals("real")) {
			throw new Exception("Type must be a number!");
		}
	}

	public void enterScope() {
		scope = true;
	}

	public void outScope() {
		scope = false;
	}

	public void addFunction(String identifier, Object returnedType) {
		String identifierSearched = identifier.toLowerCase();
		Type returnedTypeSearched = (Type) returnedType;

		if (functions.containsKey(identifierSearched) && identifiers.contains(identifierSearched)) {
			System.out.println("Identifier " + identifier + " already exists");
			System.exit(1);
		} else {

			if (!BASIC_TYPES.contains(returnedTypeSearched)) {
				System.out.println("Pascal does not regonizes the type: " + returnedType);
				System.exit(1);
			} else {
				functions.put(identifierSearched, returnedTypeSearched);
				funcParams.put(identifierSearched, null);
				System.out.println("Function add success");
			}
		}
	}

	public ArrayList<Object> returnArrayList(ArrayList<Object> listParam, Object pt) {
		ArrayList<Object> list_temp = new ArrayList<Object>();
		System.out.println("tipo pt: " + pt.getClass());
		if (ArrayList.class.isInstance(pt.getClass())){
			ArrayList<Object> list1 = (ArrayList) pt;
			listParam.addAll(list1);
			
		}else{
			if (listParam == null) {
				listParam = list_temp;
			}
			listParam.add(pt);
			System.out.println("TAMANHO: " + listParam.size());
		}
		return listParam;
		

	}
	
	public ArrayList<Object> getListTypes(ArrayList<Object> listParam, Object pt){
		ArrayList<Object> resultList = new ArrayList<Object>();
		for (Object object : listParam) {
			resultList.add(((Type) pt));
		}
		return resultList;
	}

	public void addFunctionParams(String identifier, ArrayList<Object> params) {
		String searchedIdentifier = identifier.toLowerCase();

		if (scope) {

			if (functions.containsKey(searchedIdentifier) || identifiers.contains(searchedIdentifier)) {

				System.out.println("AQUI");

				funcParams.put(searchedIdentifier, params);
				for (Object object : params) {
					System.out.println(object);
				}
				System.out.println(funcParams.size());

			} else {
				System.out.println("Function " + identifier + " does not exists");
				System.exit(1);
			}
		}
	}

	public void addProcedure(String identifier) {

		String identifierSearched = identifier.toLowerCase();
		if (procedures.containsKey(identifierSearched) && identifiers.contains(identifierSearched)) {
			System.out.println("PROCEDUREEE");
			System.out.println("Identifier " + identifier + " already exists");
			System.exit(1);
		} else {
			procedures.put(identifierSearched, null);
		}

	}

	public void addProcedureParams(String identifier, Object params) {
		String searchedIdentifier = identifier.toLowerCase();
		if (procedures.containsKey(searchedIdentifier) || identifiers.contains(searchedIdentifier)) {
			funcParams.put(searchedIdentifier, (ArrayList<Object>) params);
		} else {
			System.out.println("Procedure " + identifier + " does not exists");
			System.exit(1);
		}

	}

	public void addIdentifier(String identifier) {
		if (identifiers.contains(identifier.toLowerCase())) {
			System.out.println("Duplicated identifier: " + identifier);
			System.exit(1);
		} else {
			identifiers.add(identifier);
			System.out.println("Identifier: " + identifier);
		}
	}
	

	public boolean checkFunctionOverload(String identifier) {
		String searchedIdentifier = identifier.toLowerCase();
		if (!functions.containsKey(searchedIdentifier)) {
			System.out.println("This function:" + identifier + " is not existis");
			System.exit(1);
			return false;
			
		}
		System.out.println("This function " + identifier + "existis");
		return true;
	}
	
	public boolean checkProcedureOverload(String identifier) {
		String searchedIdentifier = identifier.toLowerCase();
		if (!procedures.containsKey(searchedIdentifier)) {
			System.out.println("This procedure:" + identifier + " is not existis");
			System.exit(1);
			return false;
			
		}
		System.out.println("This procedure " + identifier + "existis");
		return true;
	}

	private Operation getOperator(String op) {
		switch (op) {
		case "=":
			return Operation.EQ;
		case "+":
			return Operation.PLUS;
		case "-":
			return Operation.LESS;
		case "*":
			return Operation.MULT;
		case "/":
			return Operation.DIVI;
		case "<>":
			return Operation.DIF;
		case ">=":
			return Operation.GREATEQ;
		case "<=":
			return Operation.LESSEQ;
		case ">":
			return Operation.GREAT;
		case "<":
			return Operation.LESST;
		case ":=":
			return Operation.ASSIGNMENT;
		case "in":
			return Operation.IN;

		default:
			return Operation.PLUS;
		}
	}

	public void addType(Type type) throws Exception {
		if (!secondaryTypes.contains(type)) {
			secondaryTypes.add(type);
			List<String> tipos = new ArrayList<String>();
			tipos.add(type.getName());
			tiposCompativeis.put(type.getName(), tipos);
		} else {
			throw new Exception("Error: Type already exists!");
		}
	}

	public void addVariableToTempList(Variable var) {
		tempVariables.add(var);
	}

	private Expression getBooleanExpression(Expression le, Expression re) throws Exception {
		if (re != null) {
			if (checkTypeCompatibility(le.getType(), re.getType())
					|| checkTypeCompatibility(re.getType(), le.getType())) {

				return new Expression(new Type("boolean"));
			}
			throw new Exception("Incompatible types!");
		}
		return null;
	}

	private Type getMajorType(Type type1, Type type2) {
		return tiposCompativeis.get(type1.getName()).contains(type2.getName()) ? type1 : type2;
	}

	public void initializeVariable(String variableName) throws Exception {
		findVariableByIdentifier(variableName).setInitialized(true);
	}

	public void checkVariableIsInitialized(Variable variable) throws Exception {
		if (!variable.isInitialized()) {
			throw new Exception("Variable is not initialized");
		}
	}

	public Expression getExpressionType(Expression leftExpression, Expression rightExpression) throws Exception {
		Expression booleanExpression = getBooleanExpression(leftExpression, rightExpression);
		if (booleanExpression == null) {
			return leftExpression;
		}
		return booleanExpression;
	}

	public void checkBooleanExpression(Expression expression) throws Exception {
		if (!expression.getType().equals(new Type("boolean"))) {
			throw new Exception("Expression must be boolean!");
		}
	}

	public Variable getVariable(String identifier) throws Exception {
		for (Variable var : tempVariables) {
			if (identifier.equals(var.getIdentifier())) {
				return var;
			}
		}
		return findVariableByIdentifier(identifier);
	}
}
