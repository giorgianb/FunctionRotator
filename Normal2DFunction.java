/* TODO: have Normal2DFunction check if the function is well formed. */
/* func enviroment should have a getFunction(String funcName) and getVariable(String varName).
 * Also shoulkd have a setValue() and setFunction() method.
 */
import FunctionEnviroment;
class Normal2DFunction {
	String name;
	String function;
	FunctionEnviroment funcEnviroment;

	Normal2DFunction(final String name, final String function, final FunctionEnviroment funcEnviroment) {
	}

	String getName() {
		return name;
	}

	String getFunction() {
		return function;
	}
};
