package logic.cards;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {
    
    private List<Card> cards;
    private Random random;
    private List<Card> init_cards;    
    
    public Deck(List<Card> cards)
    {        
        random = new Random(System.currentTimeMillis());
        init(cards);        
    }        
        
    //init deck cards
    private void init(List<Card> cards)
    {                    
        this.cards = cards;         
        this.init_cards = copy(cards);
    }  
    
    //copy cards again to start new game with same settings
    public void refresh()
    {
        this.cards = copy(init_cards);
    }
    
    //copy cards list
    private List<Card> copy(List<Card> cards) 
    {
        List<Card> copyCards = new ArrayList<Card>();
        for (Card card : cards) {
            copyCards.add(card.clone());
        }
        return copyCards;
    }
    
    //get a specific single card from the deck
    public Card getCard(String name)
    {
        Card card = null;
        for (Card c : init_cards) {
            if (c.getName().toLowerCase().equals(name.toLowerCase()))
            {
                card = c;
                break;
            }
        }
        
        return card;
    }    
    
    //randomizely shuflle the cards
    public void shuffle()
    {        
        if (cards.size() <= 1)
            return;
        
        for(int i = 0; i < cards.size(); i++) {
            swapCards(i, random.nextInt(cards.size()));        
        }
    }    
    
    private void swapCards(int index1, int index2)
    {
        Card curr_card = cards.get(index1);        
        Card next_card = cards.get(index2);
        cards.set(index1, next_card);
        cards.set(index2, curr_card);
    }
        
    public Card getRandomCard(boolean remove)
    {  
        int index;
        if (cards.isEmpty())
            return null;
        else if (cards.size() == 1)
            index = 0;
        else
            index = random.nextInt(cards.size());
        
        Card card = cards.get(index);
        if (remove)
        {
            cards.remove(index);
            return card;        
        }
        else
        {
            return card.clone();
        }
    }        
    
    public Card getRandomCard()
    {
        int index;
        if (init_cards.isEmpty())
            return null;
        else if (init_cards.size() == 1)
            index = 0;
        else
            index = random.nextInt(init_cards.size());
        
        Card card = init_cards.get(index);
        return card.clone();
    }
    
    public List<Card> getCards() { return cards; }
    public List<Card> getInitCards() { return init_cards; }
    public int size() { return cards.size(); }
    public int totalSize() { return init_cards.size(); }
    public boolean isEmpty() { return cards.isEmpty(); }
}
