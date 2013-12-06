
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.cards.Card;
import logic.comm.Response;
import static logic.comm.Response.NEXT_TURN;
import static logic.comm.Response.TOOK_FROM_PLAYER;
import logic.game_engine.App;
import logic.game_engine.Game;
import logic.game_engine.Game.Settings;
import logic.players.HumanPlayer;
import logic.players.Player;

@WebServlet(name = "GameServlet", urlPatterns = {"/start"})
public class GameServlet extends HttpServlet {   
    
    private enum AlertType { SUCCESS, FAIL, NO_CARDS, COMPUTER_SUCCESS, COMPUTER_FAIL, FAULT };
    
    public static boolean resetServlet = false;
    
    private static final int BOTTOM_PLAYERS_MAX_COUNT = 3;
    private boolean showAlert = false;
    private boolean showGoFishAlert = false;
    private String cardName;
    private String playerName;
    
    private List<Map<String, List<Card>>> quartets;        
    
    private AlertType alertType;
    boolean nextTurn = false;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {                
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {                
        
        String goButton = request.getParameter("goButton");
        String continueButton = request.getParameter("continueButton");
        //request of card and player has been sent
        if (goButton != null) {
            playerName = request.getParameter("playerName");
            cardName = request.getParameter("cardName");
            goButtonPressed();
        }
        //request for continue to next play has been sent
        else if (continueButton != null) {
            //game finished
            if (App.isGameFinished()) {
                //forward to "winner" url, where winners are displayed
                getServletContext().getRequestDispatcher("/winner").forward(request, response);
            }
            else {
                //move to next player
                if (nextTurn) {
                    nextTurn = false;
                    App.nextTurn();
                }
                App.play();
            }
        }
        
        processRequest(request, response);
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        //reset needed to servlet members
        if (resetServlet) {
            resetServlet = false;
            quartets = new LinkedList<Map<String, List<Card>>>();
        }
        
        if (isPlayerHasCards()) {        
            playComputerPlayerTurn();        
            addCurrentPlayerQuartets();
        }
        //show players has no cards
        else {            
            showAlert = true;
            alertType = AlertType.NO_CARDS;
            nextTurn = true;
        }
        
        renderHtmlPage(response);
    }   
    
    private boolean isPlayerHasCards() {
        return App.isCurrentPlayerHasCardsInHand();
    }
    
    private void playComputerPlayerTurn() {
        if (App.getCurrentPlayer() instanceof HumanPlayer)
            return;
        
        showAlert = true;
        //play computer player turn
        Response response = App.playComputerTurnAndGetResponse();
        cardName = App.getCurrentPlayer().getRequest().getCard().getName();
        playerName = App.getCurrentPlayer().getRequest().getPlayer().getName();
        //prepare alert for the reponse of the computer player's request
        switch (response) {
            case NEXT_TURN:
                alertType = AlertType.COMPUTER_FAIL;
                break;
            case TOOK_FROM_PLAYER:
                alertType = AlertType.COMPUTER_SUCCESS;
                break;
            default:
                alertType = AlertType.FAULT;
                break;
        }
    }
    
    private void addCurrentPlayerQuartets() {
        List<Card> cards;
        //add current player quartets to sidebar
        do {
            cards = App.getCurrentPlayerQuartet();
            if (!cards.isEmpty()) {
                Map map = new HashMap<List<Card>, String>();
                map.put(App.getCurrentPlayer().getName(), cards);
                quartets.add(map);
                //prepare alert of "GoFish!"
                showGoFishAlert = true;
            }
        } while (!cards.isEmpty());        
    }
    
    private void goButtonPressed() {
        //send request of card and player to current player
        showAlert = true;
        App.sendRequest(cardName, playerName);
        Response response = App.getResponse(); 
        //prepare alert according to current player response
        switch (response) {
            case NEXT_TURN:
                alertType = AlertType.FAIL;
                break;
            case TOOK_FROM_PLAYER:
                alertType = AlertType.SUCCESS;
                break;
            default:
                alertType = AlertType.FAULT;
                break;
        }
    }
    
    private void renderHtmlPage(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {                
            printHtmlConfig(out);
            printOpenHtml(out);
            printHeader(out);            
            printGameBody(out);
            printCloseHtml(out);                                    
        } finally {            
            out.close();
        }
    }
    
    private void printHtmlConfig(PrintWriter out) {
        out.println("<!DOCTYPE html>");
    }
    
    private void printOpenHtml(PrintWriter out) {        
        out.println("<html lang=\"en\">");
    }
    
    private void printHeader(PrintWriter out) {   
        out.println("<head>");
        out.println("<meta charset=\"utf-8\">");
        out.println("<title>GoFishWeb</title>");        
        out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("<link href=\"bootstrap/css/bootstrap.css\" rel=\"stylesheet\">");                
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/game.css\">");
        out.println("<link href=\"bootstrap/css/bootstrap-responsive.css\" rel=\"stylesheet\">");        
        out.println("</head>");
    }
    
    private void printGameBody(PrintWriter out) {
        out.println("<body>");                
        
        printToolbar(out);
        
        printOpenContainer(out);
        
        printSideBar(out);
        
        printOpenRightSection(out);        
        
        printPlayerForm(out);
                
        printBottomPlayers(out);                
        
        printCloseRightSection(out);                
        
        printCloseContianer(out);
        
        out.println("</body>");
    }        
    
    private void printNewLine(PrintWriter out) {
        out.println("<br>");
    }
    
    private void printToolbar(PrintWriter out) {
        out.println("<div class=\"container-narrow\">");
        out.println("<div class=\"masthead\">");
        out.println("<ul class=\"nav nav-pills pull-right\">");
        out.println("<li class=\"active\"><a href=\"home\">Stop Game</a></li>");        
        out.println("</ul>");
        out.println("<h2 class=\"muted\">GoFish</h2>");
        out.println("</div>");  
        out.println("</div>");  
        out.println("<hr>");  
    }    
    
    //where actions of game are taken
    //and current player data displayed
    private void printPlayerForm(PrintWriter out) {                  
        printOpenWell(out);
          
        printCurrentPlayerThumbnail(out);                            
        printActionsSection(out);                                                  
                
        printCloseWell(out);                
    }
    
    private void printOpenContainer(PrintWriter out) {
        out.println("<div class=\"container-fluid\">");
        out.println("<div class=\"row-fluid\">");
    }
    
    //for quartets
    private void printSideBar(PrintWriter out) {
        out.println("<div class=\"span3\">");
        out.println("<div class=\"well sidebar-nav\">");
        out.println("<ul class=\"nav nav-list\">");
        out.println("<li class=\"nav-header\">Total Quartets: " + quartets.size() + "</li>");                                                  
        
        out.println("<div style=\"height:440px;width:250px;border:1px solid #ccc;overflow:auto;\">");        
        printQuartets(out);        
        out.println("</div>");                
        
        out.println("</ul>");
        out.println("</div><!--/.well -->");
        out.println("</div><!--/span3-->");
    }            
    
    private void printQuartets(PrintWriter out) {                
        if (quartets.isEmpty())
            return;
        
        //print all quartets on the sidebar
        for (Map<String, List<Card>> map : quartets) {
            List<Card> cards = new LinkedList<Card>();
            String name = "";
            for (String key : map.keySet()) {
                name = key;
                cards = map.get(key);
            }            
            printNewLine(out);
            Settings settings = App.getGame().getSettings();
            boolean showCards = settings.isShowCards();
            out.println("<li>");
            for (Card card : cards) {
                printCard(card, name, showCards, out);
            }
            out.println("</li>");            
        }        
        printNewLine(out);
    }
    
    private void printCard(Card card, String name, boolean showCards, PrintWriter out) {
        if (showCards) {
            out.println("<img src=\"images/" + card.getName().toLowerCase() + ".png\" width=\"50\" height=\"70\"/>");                            
        }
        else {
            //show cards of current player only
            if (App.getCurrentPlayer().equals(name)) {
                out.println("<img src=\"images/" + card.getName().toLowerCase() + ".png\" width=\"50\" height=\"70\"/>");                            
            }
            else {
                out.println("<img src=\"images/card-back.png\" width=\"50\" height=\"70\"/>");                            
            }
        }
    }
    
    private void printOpenRightSection(PrintWriter out) {
        out.println("<div class=\"span9\">");        
    }
    
    private void printOpenWell(PrintWriter out) {        
        out.println("<div class=\"well well-small\">");
    }
    
    //show current player
    private void printCurrentPlayerThumbnail(PrintWriter out) {
        Player currentPlayer = App.getCurrentPlayer();
        
        out.println("<div class=\"row-fluid\">");                
        out.println("<ul class=\"thumbnails\">");

        out.println("<li class=\"span4\">");
        out.println("<div class=\"thumbnail\">");     
        //set image for human or computer player
        if (currentPlayer instanceof HumanPlayer)
            out.println("<img src=\"images/human.png\"/>");
        else
            out.println("<img src=\"images/computer.png\"/>");
        out.println("<div class=\"caption\">");
        out.println("<h3>" + currentPlayer.getName() + "</h3>");
        out.println("<p>You have " + currentPlayer.getNumberOfQuartets() + " quartets.</p>");
        out.println("<p><b>It is Your Turn !</b></p>");
        out.println("</div><!--/caption -->"); 
        out.println("</div><!--/thumbnail-->");
        out.println("</li>");                                                        
                
        //print current player hand
        printCardsTitle(out); 
        printNewLine(out);
        printNewLine(out);        
        printNewLine(out);        
        printCards(out);        
        
        out.println("</ul>");                
        out.println("</div>");
    }
    
    private void printCardsTitle(PrintWriter out) {        
        out.println("<li class=\"span2\">");        
        out.println("<h4><b><u>Your Cards Are:</u></b></h4>");         
        out.println("</li>");        
    }
    
    private void printCards(PrintWriter out) {
        List<Card> cards = App.getCurrentPlayer().getCards();
        for (Card card : cards) {
            out.println("<li>");         
            out.println("<img src=\"images/" + card.getName().toLowerCase() + ".png\" width=\"50\" height=\"70\"/>");                            
            out.println("</li>");
        }                        
    }        
    
    private void printActionsSection(PrintWriter out) {        
        //show action panel
        if (showGoFishAlert) {
            showGoFishAlert = false;
            printGoFishAlert(out);
        }
        
        out.println("<hr>");                 
        
        if (!showAlert) {                        
            printSelectSection(out);
        }        
        else {
            showAlert = false;
            printAlertSection(out);
        }        
    }
    
    private void printSelectSection(PrintWriter out) {
        out.println("<form action='game' method=\"post\">");
        out.println("<div class=\"row-fluid\">");
        out.println("<div class=\"span4\">");
        out.println("<h4>Select Player:</h4>");          
        out.println("</div><!--span-->");
        out.println("<div class=\"span4\">");
        out.println("<h4>Select Card:</h4>");          
        out.println("</div><!--span-->");
        out.println("</div><!--row-->");          
        out.println("<div class=\"row-fluid\">");
        out.println("<div class=\"span4\">");
        out.println("<select name=\"playerName\">");
        printPlayersOption(out);
        out.println("</select>");          
        out.println("</div><!--span-->");
        out.println("<div class=\"span4\">");
        out.println("<select name=\"cardName\">");
        printCardsOption(out);
        out.println("</select>");          
        out.println("</div><!--span-->");
        out.println("<div class=\"span3\">");        
        out.println("<button class=\"btn btn-success\" type=\"submit\" name=\"goButton\">Go!</button>");        
        out.println("</div><!--span-->");
        out.println("</div><!--row-->");
        out.println("</form>");
    }
    
    private void printAlertSection(PrintWriter out) {
        out.println("<form action='game' method=\"post\">");
        out.println("<div class=\"row-fluid\">");
        out.println("<div class=\"span9\">");        
        out.println("<button class=\"btn btn-inverse\" type=\"submit\" name=\"continueButton\">Continue</button>");        
        out.println("</div><!--span-->");
        out.println("</div><!--row-->");
        out.println("</form>");
        printAlert(out);
    }        
    
    private void printPlayersOption(PrintWriter out) {
        //players combo box to request
        List<Player> players = App.getCurrentPlayerPlayersToRequest();
        for (Player player : players) {
            out.println("<option>" + player.getName() + "</option>");
        }        
    }
    
    private void printCardsOption(PrintWriter out) {
        //cards combo box to request
        List<Card> cards = App.getCurrentPlayerCardsToRequest();
        for (Card card : cards) {
            out.println("<option>" + card.getName() + "</option>");
        }        
    }
    
    private void printGoFishAlert(PrintWriter out) {
        out.println("<div class=\"row-fluid\">");
        out.println("<div class=\"span15\">");
        out.println("<div class=\"alert alert-info\">");                                          
        out.println("<strong>GoFish !!! look on the left sidebar to see the quartets.</strong>"); 
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
    }
    
    private void printAlert(PrintWriter out) {
        //show all kind of alert according to alertType member
        Player currentPlayer = App.getCurrentPlayer();        
        out.println("<div class=\"row-fluid\">");
        out.println("<div class=\"span15\">");
        switch(alertType) {
            case SUCCESS:
                out.println("<div class=\"alert alert-success\">");                                          
                out.println("<strong>" + playerName + " pass " + cardName + " to "
                    + currentPlayer.getName() + ".</strong>"); 
                break;
            case FAIL:
                out.println("<div class=\"alert alert-error\">");                                          
                out.println("<strong>" + playerName + " didn't have " + cardName + ".</strong>");
                break;
            case COMPUTER_SUCCESS:
                out.println("<div class=\"alert alert-success\">");                                          
                out.println("<strong>" + currentPlayer.getName() + " asked for " + cardName + 
                        " from " + playerName + " and he/she pass it to him/here.</strong>");
                break;
            case COMPUTER_FAIL:
                out.println("<div class=\"alert alert-error\">");                                          
                out.println("<strong>" + currentPlayer.getName() + " asked for " + cardName + 
                        " from " + playerName + " and he/she didn't have it.</strong>");
                break;
            case NO_CARDS:
                out.println("<div class=\"alert alert-block\">");                                          
                out.println("<strong>" + currentPlayer.getName() + " doesn't have any cards in hand.</strong>");
                break;
            case FAULT:
                out.println("<div class=\"alert alert-error\">");                                          
                out.println("<strong>fault occured.</strong>");
                break;
        }
        
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
    }
    
    private void printOpenBottomPlayersSection(PrintWriter out) {
        out.println("<div class=\"row-fluid\">");
    }
    
    private void printBottomPlayers(PrintWriter out) {
        //show all player at the bottom of the page
        Game game = App.getGame();
        Settings settings = game.getSettings();
        List<Player> players = game.getPlayers();
        int count = 0;
        printOpenBottomPlayersSection(out);
        for (Player player : players) {
            count++;
            if (count > BOTTOM_PLAYERS_MAX_COUNT) {
                count = 0;
                printCloseBottomPlayersSection(out);
                printOpenBottomPlayersSection(out);
            }
            out.println("<div class=\"span4\">");
            out.println("<ul class=\"thumbnails\">");
            out.println("<li class=\"span4\">");
            out.println("<div class=\"thumbnail\">");  
            if (player instanceof HumanPlayer)
                out.println("<img src=\"images/human.png\">");          
            else
                out.println("<img src=\"images/computer.png\">");          
            out.println("</div>");
            out.println("</li>");
            out.println("</ul>");
            out.println("<h2>" + player.getName() + "</h2>");
            if (settings.isShowCards())
                out.println("<p>Quartets: " + player.getNumberOfQuartets() + "</p>");
            out.println("</div><!--span-->");
        }
        printCloseBottomPlayersSection(out);
    }
    
    private void printCloseBottomPlayersSection(PrintWriter out) {
        out.println("</div>");
    }
    
    private void printCloseRightSection(PrintWriter out) {
        out.println("</div>");        
    }
    
    private void printCloseWell(PrintWriter out) {        
        out.println("</div>");
    }        
    
    private void printCloseContianer(PrintWriter out) {
        out.println("</div><!--row-fluid -->");                    
        out.println("</div><!--container-fluid -->");        
    }
    
    private void printCloseHtml(PrintWriter out) {
        out.println("</html>");
    }
    
    
}
