package logic.cards;

import java.util.Objects;
import java.util.Set;

public class Card implements CardInterface, Cloneable {

    protected String name;
    protected Set<String> series;        
    
    public Card(String name, Set<String> series)
    {
        this.name = name;
        this.series = series;        
    } 
    
    @Override
    public boolean equals(Object obj)
    {
        Card card;
        if (!(obj instanceof Card))
            return false;
        else
            card = (Card)obj;
        
        boolean equal = false;
        if (card.getName().equals(this.name))
            equal = true;
        
        return equal;
    }   

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.name);
        return hash;
    }
    
    @Override
    public Card clone()
    {
        return new Card(this.name, this.series);
    }
    
    @Override
    public Set<String> getSeries() { return series; }        
    
    @Override
    public String getName() { return name; }
}
