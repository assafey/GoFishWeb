package logic.game_engine;

import logic.cards.Card;
import logic.players.Player;
import logic.cards.Deck;
import logic.cards.Hand;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import logic.exceptions.MaxPlayersException;
import logic.exceptions.PlayerExistsException;

public class Game {
 
    public static class Settings {                                    
        public static final int MAX_PLAYERS = 6;
        public static final int MIN_PLAYERS = 3;                    
        public static final int MIN_HUMAN_PLAYERS = 1;                    
                                    
        public static final int MIN_CARDS = 28;                                
                     
        private boolean multi_request;
        private boolean show_cards;
        private boolean xml_based;               
        
        private String xmlPath;
        
        List<Player> players;
        
        public Settings() {
            players = new ArrayList<Player>();
            this.multi_request = true;
            this.show_cards = true;  
            this.xml_based = false;
            this.xmlPath = "";            
        }
        
        public Settings(boolean multi_request, boolean show_cards) 
        {            
            players = new ArrayList<Player>();
            this.multi_request = multi_request;
            this.show_cards = show_cards;  
            this.xml_based = false;
            this.xmlPath = "";            
        }         
        
        public void addPlayer(Player player) throws PlayerExistsException, MaxPlayersException {
            if (players == null)
                players = new ArrayList<Player>();
            
            //check if player's name already exist
            for (Player plr : players) {
                if (plr.equals(player.getName()))
                    throw new PlayerExistsException();
            }                           
            
            if (players.size() < MAX_PLAYERS) {
                players.add(player);                           
            }
            else
                throw new MaxPlayersException();
            
        }
        
        public void deletePlayer(String playerName) {
            for (Player plr : players) {
                if (plr.equals(playerName)) {
                    players.remove(plr);
                    break;
                }                    
            }  
        }
        
        public void setXmlPath(String xmlPath) {
            this.xmlPath = xmlPath;
        }
        
        public String getXmlPath() {
            return this.xmlPath;
        }

        public boolean isMultiRequest() {
            return multi_request;
        }

        public void setMultiRequest(boolean multi_request) {
            this.multi_request = multi_request;
        }

        public boolean isShowCards() {
            return show_cards;
        }

        public void setShowCards(boolean show_cards) {
            this.show_cards = show_cards;
        }

        public boolean isXmlBased() {
            return xml_based;
        }

        public void setXmlBased(boolean xml_based) {
            this.xml_based = xml_based;
        }           

        public List<Player> getPlayers() {
            return players;
        }

        public void setPlayers(List<Player> players) {
            this.players = players;
        }                
    }
        
    public static class Turn {
        private Deque deque;        
        
        public Turn() {}
        
        private void initDeque(List<Player> players) {            
            deque = new ArrayDeque();      
            if (players != null)
                deque.addAll(players);
        }                
        
        public void refresh(List<Player> players) {
            initDeque(players);
        }        
        
        public Player current() {             
            return (Player)deque.getFirst();
        }
                
        public Player next() { 
            Player currentPlayer = (Player)deque.removeFirst();            
            deque.addLast(currentPlayer); //push to last place
            Player player = (Player)deque.getFirst(); //return next player in deque
            return player;
        }       
    }
    
    public static final int QUARTET = 4;
    public static final String COMPUTER_DEFAULT_NAME = "COMP";
    
    //private List<Player> players;    
    private Settings settings;
    private Deck deck;    
    private Turn turn; 
    
    private boolean started;
    
    public Game(Settings settings, List<Card> cards)
    {               
        deck = new Deck(cards);  
        
        this.settings = settings;
        
        if (!this.settings.isXmlBased())
            deck.shuffle(); 
        
        turn = new Turn();
    }        
               
    public Player currentTurn()
    {
        return turn.current();        
    }
    
    public Player nextTurn()
    {        
        return turn.next();        
    }
    
    public List<Player> getPlayers()
    {
        return settings.getPlayers();
    }
    
    //refresh the game with same settings
    public void refresh()
    {
        deck.refresh();
                
        if (!this.settings.isXmlBased())
            deck.shuffle();                             
        
        for (Player player : settings.getPlayers()) {
            player.reset();
        }
        
        turn.refresh(settings.getPlayers());
    }
    
    public void divideCards()
    {  
        for (Player player : settings.getPlayers()) {
            for (int i = 0; i < deck.totalSize() / settings.getPlayers().size(); i++) {
                Card card = deck.getRandomCard(true);
                if (card != null)
                    player.dealCard(card);
                else
                    break;
            }
        }
        
        if (!deck.isEmpty())
        {
            for (Player player : settings.getPlayers()) {
                Card card = deck.getRandomCard(true);
                if (card != null)
                    player.dealCard(card);
                else
                    break;
            }
        }
    }        
    
    //get random card from deck
    public Card getRandomCard(boolean removeCard)
    {       
        return deck.getRandomCard(removeCard);
    }
    
    //get random card from deck
    public Card getRandomCard()
    {
        return deck.getRandomCard();
    }
    
    private boolean isQuartetsLeft()
    {                
        List<Card> gameCards = new ArrayList<Card>();
        for (Player player : settings.getPlayers()) {
            List<Card> playerCards = player.getCards();
            gameCards.addAll(playerCards);
        }
        Hand hand = new Hand();
        for (Card card : gameCards) {
            hand.add(card);
        }
        Set<Card> quartetsLeft = hand.hasQuartet(false);
        return !quartetsLeft.isEmpty();
    }        
    
    public boolean isCardStillInGame(Card card)
    {
        for (Player player : settings.getPlayers()) {
            Card result = player.isCardInHand(card);
            if (result != null)
                return true;
        }
        return false;
    }
    
    public boolean isPlayerStillInGame(Player player)
    {
        return player.haveCards();
    }
    
    //return if game is finished, can not make any more quartets
    public boolean isFinish()
    {                
        boolean finish;
        
        finish = !isQuartetsLeft();
        
        return finish;
    }
    
    //returns the winner of the game, can return more than one player if it is even
    public List<Player> theWinner()
    {
        List<Player> winners = new ArrayList<Player>();
        
        if (!isFinish())
            return winners;
             
        winners.add(settings.getPlayers().get(0));
        for (Player player : settings.getPlayers()) {
            Player winner = winners.get(0);
            if (!winner.equals(player))
            {
                if (player.getNumberOfQuartets() > winner.getNumberOfQuartets())                    
                {
                    winners.clear();
                    winners.add(player);
                }
                else if (player.getNumberOfQuartets() == winner.getNumberOfQuartets())                    
                {
                    winners.add(player);
                }   
            }
        }
        
        return winners;
    }    
    
    public void setSettings(Settings settings) {
        this.settings = settings;
        //this.players = settings.getPlayers();
        //this.turn = new Turn(settings.getPlayers());        
    }
    
    public void start() {
        started = true;        
    }
    
    public void reset() {
        started = false;
        refresh();
    }
    
    public boolean isStarted() {
        return started;
    }
    
    public void stop() {
        started = false;
    }
    
    public Player findPlayer(String playerName) {
        Player player = null;
        
        for (Player plr : settings.getPlayers()) {
            if (plr.equals(playerName)) {
                player = plr;
                break;
            }
        }
        
        return player;
    }
    
    public Settings getSettings() { return settings; }
    public Deck getDeck() { return deck; }
    public boolean isCardsLeft() { return !deck.getCards().isEmpty(); }
}
