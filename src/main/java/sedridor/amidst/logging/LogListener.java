package sedridor.amidst.logging;

public interface LogListener {

    void debug(Object... paramVarArgs);

    void info(Object... paramVarArgs);

    void warning(Object... paramVarArgs);

    void error(Object... paramVarArgs);

    void crash(Throwable paramThrowable, String paramString1, String paramString2);
}
