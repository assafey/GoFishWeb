
package logic.exceptions;

//happend when adding player with name that already added
public class PlayerExistsException extends AddPlayerException {
    public PlayerExistsException() {
        super("Player already exists.");
    }
}
