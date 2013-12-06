package logic.cards;

import logic.game_engine.Game;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Hand {
    
    Map<String, Set<Card>> quartets;
    
    public Hand()
    {
        quartets = Series.buildMap();
    }
    
    //add card to hand
    public void add(Card card)
    {
        for (String serie : card.getSeries()) {
            Set<Card> cards = quartets.get(serie);            
            cards.add(card);            
        }        
    }
    
    public Set<Card> hasQuartet(boolean remove)
    {
        Set<Card> qCards = new HashSet<Card>();
        for (String serie : quartets.keySet()) {
            Set<Card> cards = quartets.get(serie);
            if (cards.size() >= Game.QUARTET)
            {
                for (int i = 0; i < Game.QUARTET; i++) {                     
                    qCards.add((Card)cards.toArray()[i]);
                }
                if (remove)                
                    removeSimilars(qCards);                                    
                break;
            }
        }               
        
        return qCards;
    }
    
    //remove cards of a single quartet from hand
    private void removeSimilars(Set<Card> qCards)
    {
        for (Card card : qCards) {
            remove(card);
        }
    }
    
    //remove single card
    public void remove(Card card)
    {
        for (String serie : quartets.keySet()) {
            Set<Card> cards = quartets.get(serie);
            if (cards.contains(card)) {
                cards.remove(card);                
            }
        }                
    }
    
    public boolean hasCardInSerie(String serie)
    {
        Set<Card> cards = quartets.get(serie);
        return !cards.isEmpty();
    }
    
    public boolean contains(Card card)
    {
        for (String serie : quartets.keySet()) {
            Set<Card> cards = quartets.get(serie);
            if (cards.contains(card))
                return true;
        }
        return false;
    }
    
    public boolean isEmpty() 
    { 
        List<Set<Card>> cardsList = getCardsList();
        for (Set<Card> cards : cardsList) {
            if (!cards.isEmpty())                
                return false;            
        }
        return true;
    }
    
    public List<Set<Card>> getCardsList()
    {
        List<Set<Card>> cardsList = new ArrayList<Set<Card>>();
        for (String serie : quartets.keySet()) {
            Set<Card> cards = quartets.get(serie);
            cardsList.add(cards);            
        }
        return cardsList;
    }
    
    public List<Card> getCards()
    {
        List<Card> cards = new ArrayList<Card>();
        List<Set<Card>> cardsList = getCardsList();
        for (Set<Card> set : cardsList) {
            for (Card card : set) {
                if (!cards.contains(card))
                    cards.add(card);
            }            
        }
        return cards;
    }
}
