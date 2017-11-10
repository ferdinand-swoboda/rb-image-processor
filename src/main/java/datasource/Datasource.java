package datasource;

import java.io.IOException;

public interface Datasource<Type extends Object> {

    Type read() throws IOException;
}
