/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    rallyme.model.RallyEntry
 */

package rallyme.model;

public class RallyEntry {

    private int id;
    private String name;

    public RallyEntry(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

}
