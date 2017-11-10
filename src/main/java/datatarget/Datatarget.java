package datatarget;

import java.io.Serializable;

public interface Datatarget<Type extends Serializable> {

    void write(Type data);
}
