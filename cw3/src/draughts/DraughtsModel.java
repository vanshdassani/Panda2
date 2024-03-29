package draughts;

import java.awt.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * A class that represents a game of Draughts.
 */

public class DraughtsModel {

    private String gameName;
    private Colour currentPlayer;
    private Player player;
    private Set<Piece> pieces;

    /**
     * Constructs a game of Draughts from a save game.
     *
     * @param gameName the name of this game.
     * @param player the Player object used to get the Moves
     * from the users.
     * @param currentPlayer the colour of the current player in the game.
     * @param pieces the pieces left in the game.
     */
    public DraughtsModel(String gameName, Player player, Colour currentPlayer, Set<Piece> pieces) {
        this.gameName = gameName;
        this.player = player;
        this.currentPlayer = currentPlayer;
        this.pieces = new CopyOnWriteArraySet<Piece>(pieces);
    }

    /**
     * Constructs a new game of Draughts.
     *
     * @param gameName the name of this game.
     * @param player the Player object used to get the Moves
     * from the users.
     */
    public DraughtsModel(String gameName, Player player) {
        this.gameName = gameName;
        this.player = player;
        pieces = new CopyOnWriteArraySet<Piece>();
        currentPlayer = Colour.Red;
        initialisePieces();
    }

    // Creates the initial Set of Pieces.
    // (0, 0) is the top left of the board.
    private void initialisePieces() {
      //TODO:

      this.pieces.add(new Piece(Colour.White, 1, 0));
      this.pieces.add(new Piece(Colour.White, 3, 0));
      this.pieces.add(new Piece(Colour.White, 5, 0));
      this.pieces.add(new Piece(Colour.White, 7, 0));
      this.pieces.add(new Piece(Colour.White, 0, 1));
      this.pieces.add(new Piece(Colour.White, 2, 1));
      this.pieces.add(new Piece(Colour.White, 4, 1));
      this.pieces.add(new Piece(Colour.White, 6, 1));
      this.pieces.add(new Piece(Colour.White, 1, 2));
      this.pieces.add(new Piece(Colour.White, 3, 2));
      this.pieces.add(new Piece(Colour.White, 5, 2));
      this.pieces.add(new Piece(Colour.White, 7, 2));

      this.pieces.add(new Piece(Colour.Red, 0, 5));
      this.pieces.add(new Piece(Colour.Red, 2, 5));
      this.pieces.add(new Piece(Colour.Red, 4, 5));
      this.pieces.add(new Piece(Colour.Red, 6, 5));
      this.pieces.add(new Piece(Colour.Red, 1, 6));
      this.pieces.add(new Piece(Colour.Red, 3, 6));
      this.pieces.add(new Piece(Colour.Red, 5, 6));
      this.pieces.add(new Piece(Colour.Red, 7, 6));
      this.pieces.add(new Piece(Colour.Red, 0, 7));
      this.pieces.add(new Piece(Colour.Red, 2, 7));
      this.pieces.add(new Piece(Colour.Red, 4, 7));
      this.pieces.add(new Piece(Colour.Red, 6, 7));
    }

    /**
     * Starts the game.
     */
    public void start() {
        while(!isGameOver()) {
            turn();
        }
    }

    /**
     * Performs a turn in the game.
     */
    protected void turn() {
        Move move = getPlayerMove(validMoves(currentPlayer));
        if (move != null) play(move);
        nextPlayer();
    }

    // Plays a Move in the game.
    // @param move the Move to be played.
    protected void play(Move move) {
        Point destination = move.destination;
        Piece piece = move.piece;
        Point position = new Point(piece.getX(), piece.getY());
        piece.setX((int) destination.getX());
        piece.setY((int) destination.getY());

        boolean jump = removePiece(position, destination);
        boolean jumpOntoKing = jumpOntoKing(piece);
        checkForKing(piece);
        Set<Move> validMoves = validMoves(currentPlayer, piece, 1, true);
        if (piece.isKing()) validMoves.addAll(validMoves(currentPlayer, piece, -1, true));
        if (jump && validMoves.size() > 0 && !jumpOntoKing) {
            Move m = getPlayerMove(validMoves);
            play(m);
        }
    }

    // If a Piece has been jumped over, it will be removed.
    // Returns true if a Piece has been jumped over.
    // @param position the position of the jumping Piece.
    // @param destination the destination of the jumping Piece.
    // @return true if a Piece has been jumped over.
    protected boolean removePiece(Point position, Point destination) {
        int x = (int) (destination.getX() - position.getX());
        int y = (int) (destination.getY() - position.getY());
        if (x % 2 == 0) {
            x = (int) (position.getX() + (x / 2));
            y = (int) (position.getY() + (y / 2));
            Piece piece = getPiece(x, y);
            pieces.remove(piece);
            return true;
        }
        return false;
    }

    // Returns true if the player has jumped onto their opponents
    // kings row and they are not already a king.
    // @param piece the Piece that has made the Move.
    // @return true if the player has jumped onto their opponents
    // kings row and they are not already a king.
    protected boolean jumpOntoKing(Piece piece) {
        if ((piece.getColour().equals(Colour.Red) && piece.getY() == 0 && !piece.isKing())
            || (piece.getColour().equals(Colour.White) && piece.getY() == 7 && !piece.isKing())) {
            return true;
        }
        return false;
    }

    // Returns the Move selected by the users.
    // @param validMoves the Set of valid Moves for the current player.
    // @return the Move selected by the users.
    private Move getPlayerMove(Set<Move> validMoves) {
        return player.notify(validMoves);
    }

    // Updates the current player.
    private void nextPlayer() {
        if (currentPlayer.equals(Colour.Red)) currentPlayer = Colour.White;
        else currentPlayer = Colour.Red;
    }

    // Returns a Set of valid Moves for a player. These will only be one move ahead.
    // @param player the Colour of the player for whom the Moves should be generated.
    // @return a Set of valid Moves for a player.
    private Set<Move> validMoves(Colour player) {
        Set<Move> allValidMoves = new HashSet<Move>();
        int yOffset = 1;
        boolean jumpOnly = false;

        // loop through pieces for player, calling validMoves for each piece
        // and adding to validMoves
        for (Piece piece : this.pieces) {
            if (piece.getColour() == player) {
				// if piece is king, check also with yOffset = -1
                if (piece.isKing()) {
                    allValidMoves.addAll(validMoves(player, piece, -yOffset, jumpOnly));
                }
                // add all valid moves for current piece
                allValidMoves.addAll(validMoves(player, piece, yOffset, jumpOnly));
            }
        }
        return allValidMoves;
    }

    // Returns the Set of valid Moves for a normal Piece. These will only be one move ahead.
    // For normal players, yOffset = 1, for king players yOffset = -1. This means you
    // must call this function twice for king players, once with yOffset = 1 and
    // once with yOffset = -1.
    // @param player the Colour of the player to whom the Moves relate.
    // @param piece the Piece to generate the Moves for.
    // @param yOffset the distance to move in the y direction for a Move.
    // @param jumpOnly the boolean which decides whether to calculate valid Moves
    // for only jump Moves.
    // @return the Set of valid Moves for a normal Piece.
    private Set<Move> validMoves(Colour player, Piece piece, int yOffset, boolean jumpOnly) {
		// store valid moves, which we will return
        Set<Move> validMoves = new HashSet<Move>();

		if (player.equals(Colour.Red)) yOffset = -yOffset;
		// non-jump moves
		if (!jumpOnly) {
			// left
			if (isEmpty(piece.getX() - 1, piece.getY() + yOffset)) {
				validMoves.add(new Move(piece, piece.getX() - 1, piece.getY() + yOffset));
			}
			// right
			if (isEmpty(piece.getX() + 1, piece.getY() + yOffset)) {
				validMoves.add(new Move(piece, piece.getX() + 1, piece.getY() + yOffset));
			}
		}
		// jump moves
		int yOffset2 = 2 * yOffset;
		// left
		if ((isEmpty(piece.getX() - 2, piece.getY() + yOffset2)) && (getPiece(piece.getX() - 1, piece.getY() + yOffset) != null) && (getPiece(piece.getX() - 1, piece.getY() + yOffset).getColour() != player)) {
			validMoves.add(new Move(piece, piece.getX() - 2, piece.getY() + yOffset2));
		}
		// right
		if ((isEmpty(piece.getX() + 2, piece.getY() + yOffset2)) && (getPiece(piece.getX() + 1, piece.getY() + yOffset) != null) && (getPiece(piece.getX() + 1, piece.getY() + yOffset).getColour() != player)) {
			validMoves.add(new Move(piece, piece.getX() + 2, piece.getY() + yOffset2));
		}

        return validMoves;
    }

    // Returns true if the coordinates are empty.
    // If the coordinates are not on the board, it returns false.
    // @return true if the coordinates are empty.
    private boolean isEmpty(int x, int y) {
        if (getPiece(x, y) != null || 0 > x || x > 7 || 0 > y || y > 7) return false;
        return true;
    }

    // If any Pieces are on the other players king row,
    // it sets their king boolean to true.
    protected void checkForKing(Piece piece) {
        if ((piece.getColour().equals(Colour.Red) && piece.getY() == 0)
            || (piece.getColour().equals(Colour.White) && piece.getY() == 7)) piece.setKing(true);
    }

    /**
     * Returns the Colour of the current player.
     *
     * @return the Colour of the current player.
     */
    public Colour getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns the Set of Pieces.
     *
     * @return the Set of Pieces.
     */
    public Set<Piece> getPieces() {
        return pieces;
    }

    /**
     * Returns the name of the game.
     *
     * @return the name of the game.
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Returns the Piece with the specified coordinates.
     *
     * @param x the x coordinate of the Piece.
     * @param y the y coordinate of the Piece.
     * @return the Piece with the specified coordinates.
     */
    public Piece getPiece(int x, int y) {
        for (Piece piece : pieces) {
            if (piece.getX() == x && piece.getY() == y) return piece;
        }
        return null;
    }

    /**
     * Returns true if the game is over.
     *
     * @return true if the game is over.
     */
    public boolean isGameOver() {
        //TODO:
		if(((validMoves(Colour.Red).size() == 0) && (this.currentPlayer.equals(Colour.Red))) || ((validMoves(Colour.White).size() == 0) && (this.currentPlayer.equals(Colour.White)))) {
			return true;
		}
        return false;
    }

    /**
     * Returns a String containing who won the game.
     *
     * @return a String containing who won the game.
     */
    public String getWinningMessage() {
        if (validMoves(Colour.Red).size() == 0) return "White Player wins!";
        else return "Red Player wins!";
    }

}
