package sedridor.amidst.preferences;

import java.io.IOException;

public interface PrefModel<T> {

    String getKey();

    T get();

    void set(T paramT) throws IOException;
}
