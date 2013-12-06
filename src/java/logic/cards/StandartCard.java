package logic.cards;

import java.util.HashSet;
import java.util.Set;

public class StandartCard extends Card {
    
    public enum Suit { 
        DIAMOND, CLUB, SPADE, HEART;         
        
        @Override
        public String toString()
        {
            switch(this)
            {
                case DIAMOND:
                    return "Diamond";
                case CLUB:
                    return "Club";
                case SPADE:
                    return "Spade";
                case HEART:
                    return "Heart";
                default:
                    return "Diamond";
            }
        }
    }
    
    public enum Rank { 
        TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, 
             JACK, QUEEN, KING, ACE;
             
        @Override
        public String toString()
        {
            switch(this)
            {
                case TWO:
                    return "2";//"Two";
                case THREE:
                    return "3";//Three";
                case FOUR:
                    return "4";//"Four";
                case FIVE:
                    return "5";//"Five";
                case SIX:
                    return "6";//"Six";
                case SEVEN:
                    return "7";//"Seven";
                case EIGHT:
                    return "8";//"Eight";
                case NINE:
                    return "9";//"Nine";
                case TEN:
                    return "10";//"Ten";
                case JACK:
                    return "J";//"Jack";
                case QUEEN:
                    return "Q";//"Queen";
                case KING:
                    return "K";//"King";
                case ACE:
                    return "A";//"Ace";
                default:
                    return "2";//"Two";
            }
        }
    }
    
    public StandartCard(Rank rank, Suit suit)
    {     
        super(rank.toString() + "-" + suit.toString(), 
                getDefaultSeries(rank, suit));        
    }      
    
    public StandartCard(String cardName) {        
        super(cardName, 
                getDefaultSeries(cardName.split("-")[0], cardName.split("-")[1]));        
    }        
    
    //get the default series of standart cards
    public static Set<String> getDefaultSeries(Rank rank, Suit suit)
    {
        Set<String> series = new HashSet<String>();
        series.add(rank.toString());
        series.add(suit.toString());        
        return series;
    }
        
    public static Set<String> getDefaultSeries(String rank, String suit)
    {
        Set<String> series = new HashSet<String>();
        series.add(rank);
        series.add(suit);        
        return series;
    }
}
