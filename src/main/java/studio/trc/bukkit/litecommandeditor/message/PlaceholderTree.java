package studio.trc.bukkit.litecommandeditor.message;

import java.util.HashMap;
import java.util.Map;

import lombok.Setter;

public class PlaceholderTree<E> 
{
    @Setter
    private char startChar = '%';
    @Setter
    private char endChar = '%';
    
    private Node root;

    public PlaceholderTree() {
        this.root = new Node();
    }
    
    /**
     * Add a new placeholder to the tree
     * @param placeholder
     * @param element 
     */
    public void addPlaceholder(String placeholder, E element) {
        String[] path = placeholder.toLowerCase().split("_", -1);
        Node current = root;
        for (int i = 0;i < path.length;i++) {
            String key = path[i];
            Map<String, Node> children = current.children;
            if (!children.containsKey(key)) {
                Node newNode = new Node();
                children.put(key, newNode);
            }
            current = children.get(key);
            if (i == path.length - 1) {
                current.placeholder = placeholder;
                current.element = element;
            }
        }
    }
    
    /**
     * Clear the tree.
     */
    public void clear() {
        root = new Node();
    }
    
    /**
     * Return the number of nodes in the tree that store all elements.
     * @return 
     */
    public int size() {
        return count(root);
    }
    
    /**
     * Get the target placeholder's value.
     * @param placeholder
     * @return 
     */
    public E getPlaceholder(String placeholder) {
        String[] path = placeholder.toLowerCase().split("_");
        Node current = root;
        for (String key : path) {
            Map<String, Node> children = current.children;
            if (!children.containsKey(key)) {
                return null;
            }
            current = children.get(key);
        }
        return current.element;
    }
    
    /**
     * Get placeholders contained in the text.
     * @param text
     * @return 
     */
    public Map<String, E> getPlaceholderAbout(String text) {
        Map<String, E> result = new HashMap<>();
        StringBuilder builder = new StringBuilder(text);
        int i = 0;
        while (i < builder.length()) {
            if (builder.charAt(i) == startChar) {
                int start = i;
                int depth = 1;
                i++;
                while (i < builder.length() && depth > 0) {
                    if (builder.charAt(i) == startChar) {
                        if (startChar != endChar) {
                            depth++;
                        } else {
                            depth = (depth == 1) ? 0 : 1;
                        }
                    } else if (builder.charAt(i) == endChar && startChar != endChar) {
                        depth--;
                    }
                    i++;
                }
                if (depth == 0) {
                    String placeholder = builder.substring(start, i);
                    E element;
                    if ((element = getPlaceholder(placeholder)) != null) {
                        result.put(placeholder, element);
                    }
                    builder.deleteCharAt(i - 1);
                    builder.deleteCharAt(start);
                    i = start;
                }
            } else {
                i++;
            }
        }
        return result;
    }
     
    /**
     * Get all node values in the entire tree.
     * @return 
     */
    public Map<String, E> getAllPlaceholders() {
        Map<String, E> result = new HashMap<>();
        collectData(root, result);
        return result;
    }
    
    /**
     * Use DFS to add all node values to the result.
     * @param node
     * @param result 
     */
    private void collectData(Node node, Map<String, E> result) {
        if (node.element != null && node.placeholder != null) {
            result.put(node.placeholder, node.element);
        }
        node.children.values().stream().forEach(child -> collectData(child, result));
    }
    
    /**
     * Use DFS to count the quantity of data.
     * @param node
     */
    private int count(Node node) {
        int count = 0;
        if (node.element != null) {
            count++;
        }
        count = node.children.values().stream().map(n -> count(n)).reduce(count, Integer::sum);
        return count;
    }

    @Override
    public String toString() {
        return getAllPlaceholders().keySet().toString();
    }
    
    public class Node {
        private final Map<String, Node> children;
        private E element = null;
        private String placeholder = null;

        public Node() {
            this.children = new HashMap<>();
        }
    }
}
