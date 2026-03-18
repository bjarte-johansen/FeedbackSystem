package root.database;

import java.lang.reflect.Constructor;

public class FSQLClassMapping<T> {
    final Constructor<T> ctor;
    final FSQLColumnMapping[] columnMapping;

    /**
     * Creates a new FSQLClassMapping with the given constructor and column mapping.
     * @param ctor The constructor to use for creating instances of the class.
     * @param columnMapping The column mapping to use for mapping database columns to class fields.
     */
    public FSQLClassMapping(Constructor<T> ctor, FSQLColumnMapping[] columnMapping) {
        this.ctor = ctor;
        this.columnMapping = columnMapping;
    }


    /**
     * Creates a new instance of the class using the constructor provided in the FSQLClassMapping.
     * @return A new instance of the class.
     * @throws Exception If there is an error creating the instance, such as if the constructor is not accessible or if it throws an exception.
     */
    public T create() throws Exception{
        return ctor.newInstance();
    }
}
