package datatarget;

import java.io.IOException;

public interface DataTarget<Type> {

    void write(Type data) throws IOException;
}
