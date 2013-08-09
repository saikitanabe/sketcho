package sample.dms;

import org.springframework.util.Assert;


/**
 *
 * @author Ben Alex
 * @version $Id: File.java 1784 2007-02-24 21:00:24Z luke_t $
 */
public class File extends AbstractElement {
    /** Content of the file, which can be null */
    private String content;

    public File(String name, Directory parent) {
        super(name, parent);
        Assert.isTrue(!parent.equals(Directory.ROOT_DIRECTORY), "Cannot insert File into root directory");
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String toString() {
        return "File[fullName='" + getFullName() + "'; name='" + getName() + "'; id='" + getId() + "'; content=" + getContent() + "'; parent='" + getParent() + "']";
    }

}
