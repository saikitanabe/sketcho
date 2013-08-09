package sample.dms;

/**
 *
 * @author Ben Alex
 * @version $Id: Directory.java 1784 2007-02-24 21:00:24Z luke_t $
 *
 */
public class Directory extends AbstractElement {
    public static final Directory ROOT_DIRECTORY = new Directory();

    private Directory() {
        super();
    }

    public Directory(String name, Directory parent) {
        super(name, parent);
    }

    public String toString() {
        return "Directory[fullName='" + getFullName() + "'; name='" + getName() + "'; id='" + getId() + "'; parent='" + getParent() + "']";
    }
    
}
