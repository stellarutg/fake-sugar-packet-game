import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class v1 {
	/**
	*   A function to print the board
	*   @param player1 	the positions of player1's pawns (computer)
	*   @param player2 	the positions of player2's pawns (human)
	*/
	public static void updateBoard(int[] player1, int[] player2) {
		// Initialize the board
		String board[][] = {{" ", ".", ".", ".", " "},
						    {".", ".", ".", ".", "."},
						    {".", ".", ".", ".", "."},
						    {".", ".", ".", ".", "."},
						    {" ", ".", ".", ".", " "}};

	    // Insert player1's pawn on the board (computer)
		board[player1[0]][1] = "x";
		board[player1[1]][2] = "x";
		board[player1[2]][3] = "x";
	   
	    // Insert player2's pawn on the board (human)
		board[1][player2[0]] = "o";
		board[2][player2[1]] = "o";
		board[3][player2[2]] = "o";

		// Print the board
		for(int i=0; i < board.length; i++) {
			for(int j=0; j < board[i].length; j++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.println();
		}
	}

	/**
	*   A function to move the human player
	*   @param player1 	the positions of player1's pawns (computer)
	*   @param player2 	the positions of player2's pawns (human)
	*/
	public static void movePlayer(int[] player1, int[] player2) {
		// Ask the user for a pawn to move
		System.out.print("Which pawn do you want to move? ");
		int move = 0;
		boolean result = false;
		try {
			// Read an int representing the pawn to move
			move = StdIn.readInt();
			result = updatePlayer(move, player1, player2);
		} catch(Exception e) {
			// If the input was not an int, check if the input was pass
			String pass = StdIn.readString();
			if(pass.equals("pass"))	return;
		}

		// If the input was illegal, ask again and repeat until legal or pass
		while(!result) {
			System.out.println("Illegal move, try again or 'pass' if no move can be done...");
			try {
				// Read an int representing the pawn to move
				move = StdIn.readInt();
				result = updatePlayer(move, player1, player2);
			} catch(Exception e) {
				// If the input was not an int, check if the input was pass
				String pass = StdIn.readString();
				if(pass.equals("pass"))	break;
			}
		}
	}

	/**
	*   A function to update a player's position
	*   @param otherPlayer 		the positions of one player
	*   @param updatePlayer 	the positions of the player to be updated
	*/
	public static boolean updatePlayer(int move, int[] otherPlayer, int[] updatePlayer) {
		// Only possible to update pawns 1, 2 and 3
		if(move < 1 || move > 3) {
			return false;
		} 

		// Check the state of the player to be updated
		int state = updatePlayer[move-1];
		if(updatePlayer[move-1] > 3) { 
			// The player to be updated is already at the end
			return false;
		}
		else if(state < 2 && otherPlayer[state] == move && otherPlayer[state+1] == move) {
			// Check if the pawn can be moved
			return false;
		} else if(state < 3 && otherPlayer[state] == move) {
			// Check if the pawn can be moved two forward
			updatePlayer[move-1] += 2;
			return true;
		} else {
			// Check if the pawn can be moved one forward
			updatePlayer[move-1] += 1;
			return true;
		}
	}

	/**
	*   A function to update the computer's position
	*   @param player1 	the positions of player1's pawns (computer)
	*   @param player2 	the positions of player2's pawns (human)
	*/
	public static void moveComputer(int[] player1, int[] player2) {
		// Check all available moves
		int move1 = play(1, player1, player2);
		int move2 = play(2, player1, player2);
		int move3 = play(3, player1, player2);

		// Find the best move
		int max = Math.max(Math.max(move1, move2), move3);

		// If max is 0 then no good move was found,
		// find a random move
		boolean[] check = new boolean[3];

		if(max == 0) {
			// Check if we can move any pawn in a random order
			max = (int)(Math.random()*3)+1;
			// Computer passes if all moves have been checked
			// and no move is legal
			while(!allMovesChecked(check) && !updatePlayer(max, player2, player1)) {
				max = (int) (Math.random()*3)+1;
				check[max-1] = true;
			}
		} else {
			updatePlayer(max, player2, player1);
		}
	}

	/**
	*   A function to check if all values in a boolean array
	*   are false
	*   @param check 	the array to check
	*/
	public static boolean allMovesChecked(boolean[] check) {
		for(int i=0; i<check.length; i++) {
			if(check[i] == false) {
				return false;
			}
		}
		return true;
	}

	/**
	*   Search the game tree
	*   @param move 		the move to check
	*   @param currPlayer 	the position of the current player
	*   @param nextPlayer 	the position of the next player
	*/
	public static int play(int move, int[] currPlayer, int[] nextPlayer) {
		// Create copies of currPlayer and nextPlayer to work with
		int[] tmp1 = copy(currPlayer);
		int[] tmp2 = copy(nextPlayer);

		// Return if the move is illegal
		if(!updatePlayer(move, tmp2, tmp1)) {
			return 0;
		}

		// Check if the move leads to a win
		if(checkGameStatus(tmp1)) {
			return 1;
		}
		// Check if the move leads to a loss
		if(checkGameStatus(tmp2)) {
			return 0;
		}

		// Check all available moves
		for(int i=0; i < tmp1.length; i++) {
			int m = i+1;

			if(updatePlayer(m, tmp2, tmp1)) {
				if(play(1, copy(tmp2), copy(tmp1)) == 0) return m;
				if(play(2, copy(tmp2), copy(tmp1)) == 0) return m;
				if(play(3, copy(tmp2), copy(tmp1)) == 0) return m;
			}
		}

		return 0;
	}

	/**
	*   Create a copy of an array
	*   @param array 	the array to copy
	*/
	public static int[] copy(int[] array) {
		int[] copy = new int[array.length];
		for(int i=0; i < array.length; i++) {
			copy[i] = array[i];
		}

		return copy;
	}

	/**
	*   Check if a player has won
	*   @param player 	the player's pawns' positions
	*/
	public static boolean checkGameStatus(int[] player) {
		for(int i=0; i < player.length; i++) {
			// If not all pawns are at the end return false
			if(player[i] != 4) {
				return false;
			}
		}
		return true;
	}

	/**
	*   A main function to start the game
	*/
	public static void main(String[] args) {
		int[] player1 = {0,0,0};
		int[] player2 = {0,0,0};
		updateBoard(player1, player2);
		System.out.println("----------------------------------");	

		while(!checkGameStatus(player1) && !checkGameStatus(player2)) {
			System.out.println("YOUR TURN:");
			movePlayer(player1, player2);
			//moveComputer(player2, player1);
			updateBoard(player1, player2);
			if(checkGameStatus(player2)) {
				break;
			}
			System.out.println("THE COMPUTER'S TURN:");
			moveComputer(player1, player2);
			//movePlayer(player2, player1);
			updateBoard(player1, player2);
			System.out.println("----------------------------------");	
		}

		String winner = "";
		if(checkGameStatus(player1)) winner = "The computer";
		else winner = "You";

		System.out.println(winner + " won!");
	}
}