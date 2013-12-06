
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.exceptions.MaxPlayersException;
import logic.exceptions.PlayerExistsException;
import logic.game_engine.App;
import logic.game_engine.Game;
import logic.game_engine.Game.Settings;
import logic.players.ComputerPlayer;
import logic.players.Player;

@WebServlet(name = "SettingsServlet", urlPatterns = {"/settings"})
public class SettingsServlet extends HttpServlet {

    private static int number = 0;
    
    private boolean addPlayerError = false;
    private boolean playerExists;
    private boolean maxPlayers;        
    
    private boolean showCards = true;
    private boolean multiRequests = true;
    
    private boolean showSavedConfigMessage = false;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {                
        addPlayerError = false;
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                
        String saveConfigButton = request.getParameter("saveConfigButton");
        String addPlayerButton = request.getParameter("addPlayerButton");
        String removePlayerButton = request.getParameter("removePlayerButton");

        //got save general configuration request
        if (saveConfigButton != null) {
            showSavedConfigMessage = true;
            String show_cards = request.getParameter("showCards");
            String multi_requests = request.getParameter("multiRequests");                    
            setGeneralConfig(show_cards, multi_requests);
        }
        //got add new player request
        else if (addPlayerButton != null) {
            String playerName = request.getParameter("addPlayerName");
            String playerType = request.getParameter("addPlayerType");                
            addPlayer(playerName, playerType);        
        }
        //got remove player request
        else if (removePlayerButton != null) {
            String playerToRemove = request.getParameter("playerToRemove");
            removePlayer(playerToRemove);
        }                                        
        
        processRequest(request, response);
    }        
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {                        
        createDeafaultGame(false);
        renderHtmlPage(response);        
    }        
    
    private void setGeneralConfig(String show_cards, String multi_requests) {
        if (show_cards == null) {
            showCards = false;
        }
        else {
            showCards = true;
        }
        
        if (multi_requests == null) {
            multiRequests = false;
        }
        else {
            multiRequests = true;
        }
        
        App.setGeneralConfig(show_cards, multi_requests);
    }
    
    private void removePlayer(String playerName) {
        if (playerName != null) {
            if (!playerName.isEmpty())
                App.removePlayer(playerName);
        }
    }
    
    private void addPlayer(String playerName, String playerType) {
        addPlayerError = false;
        if (playerType != null && playerName != null) {
            if (!playerName.isEmpty()) {                                
                try {
                    App.addPlayer(playerName, playerType);
                }
                catch (PlayerExistsException ex) {
                    addPlayerError = true;
                    maxPlayers = false;
                    playerExists = true;
                }
                catch (MaxPlayersException ex) {
                    addPlayerError = true;
                    maxPlayers = true;
                    playerExists = false;
                }
            }
        }
    }
    
    private void createDeafaultGame(boolean forceNewGame)
    {           
        Game game = App.getGame();
        
        if (game == null || forceNewGame) {
            App.init();
        }
    }        
    
    private void renderHtmlPage(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {                
            printHtmlConfig(out);
            printOpenHtml(out);
            printHeader(out);            
            printBody(out);
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
        out.println("<style>");
        out.println("body {padding-top: 60px;}");
        out.println("</style>");
        out.println("</head>");
    }
    
    private void printBody(PrintWriter out) {
        out.println("<body>");
        
        printToolbar(out);
        
        out.println("<div class=\"container\">");
        
        printSettingsHeader(out);
        printNewLine(out);
        
        printAddPlayerSection(out);
        printNewLine(out);
        
        printPlayersTable(out);
        printNewLine(out);
        
        printGeneralConfigurations(out);        
        
        out.println("</div>");         
        out.println("</body>");                
    }
    
    private void printNewLine(PrintWriter out) {
        out.println("<br>");
    }
    
    private void printToolbar(PrintWriter out) {
        out.println("<div class=\"navbar navbar-inverse navbar-fixed-top\">");
        out.println("<div class=\"navbar-inner\">");
        out.println("<div class=\"container\">");
        out.println("<button type=\"button\" class=\"btn btn-navbar\" data-toggle=\"collapse\" data-target=\".nav-collapse\">");
        out.println("<span class=\"icon-bar\"></span>");
        out.println("<span class=\"icon-bar\"></span>");
        out.println("<span class=\"icon-bar\"></span>");
        out.println("</button>");
        out.println("<a class=\"brand\" href=\"index.html\">GoFish</a>");
        out.println("<div class=\"nav-collapse collapse\">");
        out.println("<ul class=\"nav\">");
        out.println("<li><a href=\"index.html\">Home</a></li>");
        out.println("<li class=\"active\"><a href=\"settings\">Settings</a></li>");
        out.println("<li><a href=\"about.html\">About</a></li>");
        out.println("</ul>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
    }
    
    private void printSettingsHeader(PrintWriter out) {        
        out.println("<h1>Settings</h1>");        
    }
        
    private void printAddPlayerSection(PrintWriter out) {
        out.println("<legend><h4>Add Player:</h4></legend>");                
        out.println("<div><h6>Minimum Players: " + Settings.MIN_PLAYERS
                + " | Maximum Players: " + Settings.MAX_PLAYERS
                + " | Minimum Human Players: " + Settings.MIN_HUMAN_PLAYERS
                + "</h6></div>");
        out.println("<div>");    
        out.println("<form action='settings' method=\"post\">");
        out.println("<select name=\"addPlayerType\">");
        out.println("<option>Computer</option>");
        out.println("<option>Human</option>");
        out.println("</select>");        
        
        out.println("<div class=\"input-append\">");
        out.println("<input class=\"span2\" id=\"appendedInputButton\" name=\"addPlayerName\" type=\"text\">");                                
        out.println("<button type=\"submit\" class=\"btn\" name=\"addPlayerButton\">Add</button></td>");
        out.println("</form>");        
        out.println("</div>");         
        
        //show error if limit of players reached or player name already exist
        if (addPlayerError) {
            out.println("<div class=\"alert alert-error\">");              
            if (playerExists) {                
                out.println("<strong>Player already exists.</strong>");
            }
            else {
                out.println("<strong>Maximum players inserted.</strong>");
            }
            out.println("</div>");
        }
        
        out.println("</div>");        
    }
    
    //print players table
    private void printPlayersTable(PrintWriter out) {
        Settings settings = App.getGame().getSettings();
        if (settings.getPlayers() == null) {
            return;
        }
        else if (settings.getPlayers().isEmpty()) {
            return;
        }
        
        out.println("<div>");
        out.println("<table class=\"table\">");
        //headers
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>#</th><th>Name</th><th>Type</th><th>Remove</th>");
        out.println("</tr>");
        out.println("</thead>");
        //table body
        out.println("<tbody>"); 
        
        printPlayers(out);
        
        out.println("</tbody>");        
        out.println("</table>");
        
        out.println("</div>");
    }
    
    private void printPlayers(PrintWriter out) {
        Settings settings = App.getGame().getSettings();
        List<Player> players = settings.getPlayers();
        number = 0;
        for (Player player : players) {
            number++;
            printPlayerRow(player, out);
        }
    }
    
    private void printPlayerRow(Player player, PrintWriter out) {
        String name = player.getName();
        String type = (player instanceof ComputerPlayer ? "Computer":"Human");
        out.println("<tr>");
        out.println("<td>" + number + "</td>");
        out.println("<td>" + name + "</td>");
        out.println("<td>" + type + "</td>");        
        out.println("<form action='settings' method=\"post\">");
        out.println("<input type=\"hidden\" name=\"playerToRemove\" value=\"" + name + "\">");        
        out.println("<td><button type=\"submit\" class=\"btn btn-danger\" name=\"removePlayerButton\">X</button></td>");
        out.println("</form>");
        out.println("</tr>");
    }
    
    private void printGeneralConfigurations(PrintWriter out) {        
        out.println("<form action='settings' method=\"post\">");
        
        out.println("<div>");
        out.println("<legend><h3>General Configuration:</h3></legend>");
        
        out.println("<label class=\"checkbox\">");
        if (multiRequests)
            out.println("<input type=\"checkbox\" name=\"multiRequests\" checked>Multi Requests Game");
        else
            out.println("<input type=\"checkbox\" name=\"multiRequests\">Multi Requests Game");                    
        
        out.println("</label>");
        
        out.println("<label class=\"checkbox\">");
        if (showCards)
            out.println("<input type=\"checkbox\" name=\"showCards\" checked>Show Cards");
        else
            out.println("<input type=\"checkbox\" name=\"showCards\">Show Cards");        
        
        out.println("</label>");
        
        out.println("</div>");
        
        printNewLine(out);
        
        if (showSavedConfigMessage) {
            showSavedConfigMessage = false;
            printSavedMessage(out);
        }
        
        printSaveButton(out);                            
        
        out.println("</form>");
    }
    
    //print save message after pressing 'Sve Chenges' button and settings saved.
    private void printSavedMessage(PrintWriter out) {        
        out.println("<div class=\"alert alert-info\">");                                        
        out.println("<strong>Settings saved.</strong>");                
        out.println("</div>");
    }
    
    private void printSaveButton(PrintWriter out) {        
        out.println("<button type=\"submit\" class=\"btn btn-large btn-block btn-info\" name=\"saveConfigButton\">Save Changes</button>");
    }
    
    private void printCloseHtml(PrintWriter out) {
        out.println("</html>");
    }
}
