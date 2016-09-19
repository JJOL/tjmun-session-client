package mx.x10.iowizportal.tjmunapp.windows.protocolwindows;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JJOL on 13/11/2015.
 */
public class BaseDesign {

    private Map<String, Node> elementBook = new HashMap<>();
    private Map<String, Object> thingsBook = new HashMap<>();
    private Pane root;

    public BaseDesign(Pane root) {
        this.root = root;
    }

    public Pane getRoot() {
        return root;
    }

    public Node getImportantElement(String name) {
        return elementBook.get(name);
    }

    public Object getImportantThing(String name) {
        return thingsBook.get(name);
    }

    public void insertImportantElement(String name, Node node) {
        elementBook.put(name, node);
    }

    public void insertImportantThing(String name, Object thing) {
        thingsBook.put(name, thing);
    }

}
