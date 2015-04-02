package bluepumpkin.controller;

public class ThrowableCaptor {
	public static Throwable captureThrowable(ExceptionThrower exceptionThrower) {
		try {
			exceptionThrower.throwException();
			// exception not thrown
			return null;
		} catch (Throwable caught) {
			return caught;
		}	
	}
}

@FunctionalInterface
interface ExceptionThrower {
	void throwException() throws Throwable;
}