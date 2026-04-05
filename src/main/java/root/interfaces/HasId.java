package root.interfaces;

/**
 * A simple interface that defines methods for getting and setting an ID. This interface can be implemented by any class
 * that needs to have an ID property, such as entities that are stored in a database. The getId() method returns the ID
 * of the object,
 * TODO: Any code inserted here must have javadoc comments. This is a requirement for all code in this project.
 */

public interface HasId {
    /**
     * Gets the ID of the object.
     *
     * @return the ID of the object
     */
    Long getId();

    /**
     * Sets the ID of the object.
     *
     * @param id the ID to set
     */
    void setId(long id);
}
