
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.game_engine.App;

@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {
    
    private String errorMessage;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        errorMessage = (String)request.getAttribute("errorMessage");        
        
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        errorMessage = (String)request.getAttribute("errorMessage");        
        
        processRequest(request, response);
    }    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (App.getGame() != null) 
            App.stopGame();
        renderHtmlPage(response);
    }   
    
    private void renderHtmlPage(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {           
            //back to home page with error message
            printHtmlConfig(out);
            printOpenHtml(out);
            printHeader(out);                        
            printHomeBody(out);            
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
    
    private void printHomeBody(PrintWriter out) {
        out.println("<body>");
        
        printToolbar(out);
        
        out.println("<div class=\"container\">");
        
        out.println("<h1>GoFish</h1>");
        printNewLine(out);
        printNewLine(out);
        out.println("<ul>");
        out.println("<li>You can start a new game.</li>");
        out.println("<li>You can press 'Settings' to change game configurations.</li>");
        out.println("</ul>");
        printNewLine(out);
        printNewLine(out);
        out.println("<div>");
        out.println("<form action='game' method=\"post\">");
        out.println("<button type=\"submit\" class=\"btn btn-success\">Start New Game</button>");
        out.println("</form>");
        
        //print error message if needed
        if (errorMessage != null) {
            if (!errorMessage.isEmpty()) {
                out.println("<div class=\"alert alert-error\">");                                          
                out.println("<strong>" + errorMessage + "</strong>");         
                out.println("</div>");
            }
        }
        
        out.println("<form action='settings' method=\"post\">");
        out.println("<button type=\"submit\" class=\"btn btn-info\">Settings</button>");
        out.println("</form>");        
        
        out.println("</div>");
        
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
        out.println("<li class=\"active\"><a href=\"index.html\">Home</a></li>");
        out.println("<li><a href=\"settings\">Settings</a></li>");
        out.println("<li><a href=\"about.html\">About</a></li>");
        out.println("</ul>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
    }        
    
    private void printCloseHtml(PrintWriter out) {
        out.println("</html>");
    }
}
