
package logic.exceptions;

//happend when adding player after maximum players already added
public class MaxPlayersException extends AddPlayerException {
    public MaxPlayersException() {
        super("Maximum Players Inserted.");
    }
}
