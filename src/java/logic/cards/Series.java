package logic.cards;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Series {
    //static values of all series in game
    public static Set<String> VALUES = new HashSet<String>();        
    
    public static void add(String serie)
    {
        VALUES.add(serie);
    }
    
    public static void addAll(Set<String> values)
    {
        VALUES.addAll(values);
    }
    
    //build the series map
    public static Map<String, Set<Card>> buildMap()
    {
        Map<String, Set<Card>> map = new HashMap<String, Set<Card>>();                        
        for (String serie : VALUES) {
            Set<Card> cards = new HashSet<Card>();
            map.put(serie, cards);
        }
        
        return map;
    }
    
    public static Set<String> getValues() { return VALUES; }
}
