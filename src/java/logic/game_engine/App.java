package logic.game_engine;

import logic.cards.Card;
import logic.cards.Series;
import logic.cards.StandartCard;
import logic.cards.StandartCard.Rank;
import logic.cards.StandartCard.Suit;
import logic.comm.Response;
import logic.players.ComputerPlayer;
import logic.players.HumanPlayer;
import logic.players.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Timer;
import static logic.comm.Response.NEXT_TURN;
import static logic.comm.Response.TOOK_FROM_PLAYER;
import logic.exceptions.MaxPlayersException;
import logic.exceptions.PlayerExistsException;
import logic.game_engine.Game.Settings;

public class App {
        
    private static Game game;                                    
    private static Player currentPlayer;        
    
    public static Game getGame() {
        return game;
    }
    
    public static Player getCurrentPlayer() {
        return currentPlayer;
    }        
    
    //init the game engaine and the UI
    public static void init()
    {                
        createDeafaultGame();        
    }
    
    //create game with default standart cards
    private static void createDeafaultGame()
    {
        List<Card> cards = createDefaultCards();
        Series.addAll(buildSeries(cards)); //build default cards serires      
        game = new Game(new Game.Settings(), cards);           
    }
    
    //build cards serires
    private static Set<String> buildSeries(List<Card> cards)
    {
        Set<String> series = new HashSet<String>();
        for (Card card : cards) {
            for (String serie : card.getSeries()) {
                series.add(serie);
            }            
        }
        
        return series;
    }
    
    //create regular cards (rank, suit) for the game
    private static List<Card> createDefaultCards()
    {
        List<Card> cards = new ArrayList<Card>();            
                
        for (Suit suit: StandartCard.Suit.values()) {
            for (Rank rank: StandartCard.Rank.values()) {
                Map<String, String> props = new HashMap<String, String>();
                props.put("suit", suit.toString().toLowerCase());
                props.put("rank", rank.toString().toLowerCase());
                cards.add(new StandartCard(rank, suit));
            }
        }                                                        
        
        return cards;
    }    
    
    //set multi requests and force show cards in settings
    public static void setGeneralConfig(String showCards, String multiRequests) {
        boolean multi_requests, show_cards;
        if (showCards == null) {
            show_cards = false;
        }
        else {
            show_cards = true;
        }
        
        if (multiRequests == null) {
            multi_requests = false;
        }
        else {
            multi_requests = true;
        }
        
        Settings settings = game.getSettings();
        settings.setMultiRequest(multi_requests);
        settings.setShowCards(show_cards);
    }
    
    //remove player from game
    public static void removePlayer(String playerName) {
        Settings settings = game.getSettings();
        settings.deletePlayer(playerName);
    }
    
    //add player to game
    public static void addPlayer(String playerName, String type) throws PlayerExistsException, MaxPlayersException {
        Player player;
        if (type.equals("Human")) {
            player = new HumanPlayer(playerName);
        }
        else {
            player = new ComputerPlayer(playerName);
        }
        Settings settings = game.getSettings();
        settings.addPlayer(player);
    }
    
    //return if game created
    public static boolean isGameAvailable() {
        return (game != null);
    }
    
    //rtuens if the game has enough players to play
    //minimum player to play = 3, can be changed in settings
    public static boolean isEnoughPlayersToPlay() {
        Settings settings = game.getSettings();
        int playersCount = settings.getPlayers().size();
        if (playersCount >= Settings.MIN_PLAYERS)
            return true;
        else
            return false;
    }
    
    //returns if the game has at least one human player
    //game have to include 1 human
    public static boolean isGameIncludeHumans() {
        Settings settings = game.getSettings();
        List<Player> players = settings.getPlayers();
        boolean hasHumanInGame = false;
        for (Player player : players) {
            if (player instanceof HumanPlayer) {
                hasHumanInGame = true;
                break;
            }
        }
        return hasHumanInGame;
    }
    
    public static boolean isCurrentPlayerHasCardsInHand() {
        return currentPlayer.haveCards();
    }
    
    //
    public static void nextTurn() {
        game.nextTurn();
    }
    
    //returns a list of player's quartet
    //one at a time
    public static List<Card> getCurrentPlayerQuartet()
    {                               
        Set<Card> quartetSet = currentPlayer.hasQuartet(true); 
        if (!quartetSet.isEmpty())
            currentPlayer.incQuartetCounter();
        List<Card> quartet = new LinkedList<Card>();
        quartet.addAll(quartetSet);            
        return quartet;
    }        
    
    //send request to player, with card and player
    public static void sendRequest(String cardName, String playerName) {        
        Player playerToRequest = game.findPlayer(playerName);
        Card card = new StandartCard(cardName);
        currentPlayer.setRequest(playerToRequest, card);                       
    }    
    
    //get the response of the request above
    public static Response getResponse() {
        Response response = currentPlayer.getResponse();
        switch(response) {
            case NEXT_TURN:                                
                game.nextTurn();                   
                break;
            case TOOK_FROM_PLAYER:                                   
                if (!game.getSettings().isMultiRequest()) {
                    game.nextTurn();                                   
                }
                break;
        }         
        return response;
    }
    
    //starts new game
    public static void startGame() {        
        game.reset();
        game.start();    
        game.divideCards();
        play();
    }
    
    //set the current player turn
    public static void play() {
        currentPlayer = game.currentTurn(); //get current player turn        
    }    
    
    //returns if game finished
    //mean, players has no quartets left in game
    public static boolean isGameFinished() {
        boolean finished = game.isFinish();
        if (finished) {
            game.stop();
        }
        return finished;
    }
    
    //get the list of winners
    //can be one or more, depend on the number of quartets they have
    public static List<Player> getWinners() {
        List<Player> winners = game.theWinner();
        return winners;
    }
    
    public static void stopGame() {
        game.stop();
    }
    
    //returns a list of cards the current player can request from other players
    //mean, he has the same rank or suit
    public static List<Card> getCurrentPlayerCardsToRequest() {
        List<Card> cardsToRequest = new ArrayList<Card>();
        for (Card card : game.getDeck().getInitCards()) {
            if (currentPlayer.hasTheSerieInHand(card) && !currentPlayer.hasTheSameCard(card)) {
                cardsToRequest.add(card);
            }
        }
        return cardsToRequest;
    }
    
    //returns a list of players the current player can request
    //except him
    public static List<Player> getCurrentPlayerPlayersToRequest() {
        List<Player> playersToRequest = new ArrayList<Player>();
        for (Player player : game.getPlayers()) {
            if (!player.equals(currentPlayer)) {
                playersToRequest.add(player);
            }
        }
        return playersToRequest;
    }        
    
    //plays the computer player turn automaticaly after pressing continue in browser
    public static Response playComputerTurnAndGetResponse() {
        currentPlayer.setRequest(game);
        return getResponse();
    }
    
}
