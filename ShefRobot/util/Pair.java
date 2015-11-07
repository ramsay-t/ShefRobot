package ShefRobot.util;

import java.util.Map;

/**
 * Simple pair class used internally
 * Core library equivalent is {@link Map.Entry}
**/
public class Pair<K,V> {

    public final K key;
    public final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int hashCode() { 
        return key.hashCode() ^ value.hashCode(); 
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair other = (Pair) o;
        return this.key.equals(other.key) && this.value.equals(other.value);
    }

}