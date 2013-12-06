
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.game_engine.App;
import logic.players.Player;

@WebServlet(name = "WinnerServlet", urlPatterns = {"/winner"})
public class WinnerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        renderHtmlPage(response);
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
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/winner.css\">");        
        out.println("</head>");
    }        
    
    private void printBody(PrintWriter out) {
        List<Player> winners = App.getWinners();
        
        //set titles according to number of players
        String winnersTitle = "";
        if (winners.size() > 1) {
            winnersTitle = "the winners are";
        }
        else {
            winnersTitle = "<h2>The winner is:";
        }

        //make a string of all winners, can be only one
        String winnersStr = "";
        for (Player player : winners) {  
            if (winnersStr.isEmpty())
                winnersStr = player.getName();
            else
                winnersStr += ", " + player.getName();
        }
        
        //show winner(s)
        out.println("<div id=\"wrap\">");
        out.println("<div class=\"container\">");
        out.println("<div class=\"page-header\" align=\"center\">");
        out.println("<h1>Game Finished !</h1>");
        out.println("</div>");        
        out.println("<div align=\"center\">");
        out.println("<img src=\"images/cup.png\" align=\"middle\">");                
        out.println("<h3>" + winnersTitle + "</h3>");        
        out.println("<p class=\"lead\">" + winnersStr + "</p>");
        out.println("<hr>");        
        out.println("<span>");        
        out.println("<ul class=\"nav nav-pills pull-right\">");
        out.println("<li class=\"active\"><a href=\"index.html\">Home</a></li>");        
        out.println("<li class=\"active\"><a href=\"game\">Restart</a></li>");        
        out.println("</ul>");        
        out.println("</span>");
        out.println("</div>");        
        out.println("</div>");
        out.println("<div id=\"push\"></div>");
        out.println("</div>");        
    }
    
    private void printCloseHtml(PrintWriter out) {
        out.println("</html>");
    }
}
