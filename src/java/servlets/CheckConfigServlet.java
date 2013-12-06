package servlets;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.game_engine.App;

@WebServlet(name = "CheckConfigServlet", urlPatterns = {"/game"})
public class CheckConfigServlet extends HttpServlet {
    
    private boolean validGame;
    private String errorMessage;
    
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
        
        checkGameValidity();
        
        if (validGame) {
            //start new game
            if (!App.getGame().isStarted()) {
                App.startGame();
                GameServlet.resetServlet = true;
            }
            //forward game to "start" url, where game is played
            getServletContext().getRequestDispatcher("/start").forward(request, response);
        }
        else {
            //back to home page with error message
            request.setAttribute("errorMessage", errorMessage);
            getServletContext().getRequestDispatcher("/home").forward(request, response);            
        }
    } 
    
    private void checkGameValidity() {
        if (!App.isGameAvailable()) {
            validGame = false;
            errorMessage = "Please enter settings and set the game configurations.";
        }
        else if (!App.isGameIncludeHumans()) {
            validGame = false;
            errorMessage = "Game must include human players.\n" + 
                    "Please Change configurations in settings";
        }
        else if (!App.isEnoughPlayersToPlay()) {
            validGame = false;
            errorMessage = "Game must include minimum 3 players.\n" + 
                    "Please Change configurations in settings";
        }
        else {
            validGame = true;
        }
    }

}
