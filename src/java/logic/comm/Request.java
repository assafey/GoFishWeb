package logic.comm;

import logic.cards.Card;
import logic.players.Player;

//request for a crad and player
public class Request {
    Player player;
    Card card;
    
    public Request(Player player, Card card)
    {
        this.player = player;
        this.card = card;
    }

    public Player getPlayer() {
        return player;
    }

    public Card getCard() {
        return card;
    }        
}
