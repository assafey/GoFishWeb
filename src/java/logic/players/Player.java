package logic.players;

import logic.cards.Card;
import logic.cards.Hand;
import logic.comm.Request;
import logic.comm.Response;
import logic.game_engine.Game;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

abstract public class Player {
    protected String name;
    private static int static_id = 0;    
    protected int id;
    protected int number_of_quartet;        
    protected boolean sitASide;
    protected Hand hand;
    
    protected Request request;
    
    public Player(String name)
    {
        hand = new Hand();
        this.name = name;
        this.id = static_id++;
        number_of_quartet = 0;
        sitASide = false;
    }
    
    //refresh members of a player when new game with same settings initiate
    public void reset()
    {
        sitASide = false;
        hand = new Hand();
        number_of_quartet = 0;
    }
    
    public void dealCard(Card card)
    {
        hand.add(card);
    }
    
    //increase quartets counter by one
    public void incQuartetCounter()
    {
        number_of_quartet++;
    }
    
    public Set<Card> hasQuartet(boolean remove)
    {
        Set<Card> quartet = hand.hasQuartet(remove);                    
        return quartet;                    
    }
     
    //pass card to a player, and remove it from the current player hand
    public void passCard(Card card, Player player)
    {                
        hand.remove(card);
        player.dealCard(card);
    }
    
    public Card isCardInHand(Card card)
    {      
        for (Set<Card> cards : hand.getCardsList()) {            
            for (Card card1 : cards) {
                if (card1.equals(card))
                    return card1;
            }
        }
        
        return null;
    }                
        
    public void setRequest(Game game)
    {
        request = new Request(chooseRandomPlayer(game), chooseRandomCard(game));                
    }
        
    public void setRequest(Player player, Card card)
    {
        request = new Request(player, card);
    }
    
    private Player chooseRandomPlayer(Game game)
    {        
        Random random = new Random(System.currentTimeMillis());
        boolean good_choise;
        Player player;
        List<Player> players = game.getPlayers();
        do
        {            
            player = players.get(random.nextInt(players.size()));
            if (player.getId() == this.getId()) {
                good_choise = false;
            }
            else if (!game.isPlayerStillInGame(player)) {
                good_choise = false;
            }
            else {
                good_choise = true;
            }
        } while (!good_choise);
        
        return player;
    }        
    
    //choose a random card that is not in his hand and has the same serie in hand
    private Card chooseRandomCard(Game game)
    {
        boolean good_choise;
        Card card = null;
        do
        {
            card = game.getRandomCard();
            //if (!game.isCardStillInGame(card)) //will make the game very difficult
                //good_choise = false;
            if (hasTheSameCard(card))
                good_choise = false;
            else if (!hasTheSerieInHand(card))
                good_choise = false;
            else
                good_choise = true;
        } while (!good_choise);
        return card;
    }           
    
    public Response getResponse()
    {                             
        Card cardToAsk = request.getCard();
        Player playerToAsk = request.getPlayer();
        
        Card card = playerToAsk.isCardInHand(cardToAsk);
        if (card != null)
        {            
            playerToAsk.passCard(card, this);
            return Response.TOOK_FROM_PLAYER;
        }
        else
            return Response.NEXT_TURN;    
    }
    
    //indicate if a player sit a side (has no cards left in hand)
    public void SitASide(boolean sitASide)
    {
        this.sitASide = sitASide;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean result;
        
        if (obj == null)
            result = false;        
        else if (obj instanceof Player) {
            Player player = (Player)obj;
        
            if (player.getName().equals(this.getName()) && player.getId() == this.getId())
                result = true;
            else
                result = false;
        }
        else if (obj instanceof String) {
            String playerName = (String)obj;
            
            if (playerName.equals(this.getName()))
                result = true;
            else
                result = false;
        }
        else 
            result = false;
        
        return result;
        
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.name);
        hash = 47 * hash + this.id;
        return hash;
    }
    
    public boolean hasTheSameCard(Card card)
    {
        return hand.contains(card);
    }
    
    public boolean hasTheSerieInHand(Card card)
    {
        boolean hasSerie = false;
        for (String serie : card.getSeries()) {
            if (hand.hasCardInSerie(serie))
            {
                hasSerie = true;
                break;
            }
        }
        return hasSerie;
    }
    
    public boolean haveCards() { return !hand.isEmpty(); }
    
    public String getName() { return name; }
    public int getId() { return id; }
    public List<Card> getCards() { return hand.getCards(); }      
    public Request getRequest() { return request; }            
    public int getNumberOfQuartets() { return number_of_quartet; }
    public boolean isSittingASide() { return sitASide; }
    
}
