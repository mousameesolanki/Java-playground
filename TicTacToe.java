//Functional
/*
User
Game
MatchMaker
2 players play Tic-Tac-Toe

NxN board (not just 3x3 → scalable design)

Players take alternate turns

Detect winner or draw

Display board
LLD flow-FR,NFR,core entities and relationships like one to many between them,classes and interfaces,class design,implementation ion and exception handling,testing and edge cases
NFR-concurreny,low latency moves,scalability for multiple games,effective and fast matchmaking,robustness against invalid moves.
Core Entities and relationships:
Game->player1,player2,Board->Cell
Player-user
Move ->Player+Cell
MatchMaker->matches players and creates game instances

Classes and interfaces
GameController->handles user interactions, game logic, move validation, win/draw detection
WinningStrategy->interface for different winning conditions (rows, columns, diagonals)
Now class design and implementation in java
 */

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;

class Cell {
  int row;
  int col;
  char symbol;
  Cell(int row, int col) {
    this.row = row;
    this.col = col;
    this.symbol = '_'; // empty cell

  }
  boolean isEmpty() {
    return symbol == '_';
  }

}
class Player{
  String playerId;
  char symbol;
  Player(String playerId, char symbol){
    this.playerId = playerId;
    this.symbol = symbol;
  }
}
class Move{
  Player player;
  Cell cell;
  Move(Player player, Cell cell){
    this.player = player;
    this.cell = cell;
  }
}
class Board{
  int size;
  Cell[][] cells;
  Board(int size){  
    this.size = size;
    cells = new Cell[size][size];
    for(int i=0;i<size;i++){
      for(int j=0;j<size;j++){
        cells[i][j] = new Cell(i,j);
      }
    }
  }
  boolean makeMove(Move move){
    //check if move is valid and cell is empty
    int r=move.cell.row;
    int c=move.cell.col;
    if(!cells[r][c].isEmpty()){
      return false; //invalid move
    }
    cells[r][c].symbol = move.player.symbol;
    return true;
  }
  void printBoard(){
    for(int i=0;i<size;i++){
      for(int j=0;j<size;j++){
        System.out.print(cells[i][j].symbol+" ");
      }
      System.out.println();
    }
  }
}
enum GameStatus{
  IN_PROGRESS,
  DRAW,
  WIN
}
// class Game{
//   Player player1;
//   Player player2;
//   Board board;
//   GameStatus status;
//   Player winner;
//   Game(Player player1, Player player2, int boardSize){  
//     this.player1 = player1;
//     this.player2 = player2;
//     this.board = new Board(boardSize);
//     this.status = GameStatus.IN_PROGRESS;
//     this.winner = null;
//   }
// }
//winning strategy interface for different winning conditions (rows, columns, diagonals)
//each player maintains count of symbols in rows, columns and diagonals to quickly check for win condition after each move
class WinningStrategy{
  int size;
  Map<Character, int[]> rowCount= new HashMap<>();
  Map<Character, int[]> colCount= new HashMap<>();
 Map<Character, Integer> diagCount= new HashMap<>();
 Map<Character, Integer> antiDiagCount= new HashMap<>();
  WinningStrategy(int size,List<Player> players ){
    this.size = size;
    for(Player p: players){
      rowCount.put(p.symbol, new int[size]);
      colCount.put(p.symbol, new int[size]);
      diagCount.put(p.symbol, 0);
      antiDiagCount.put(p.symbol, 0);
    }
    
  }
  boolean checkWin(Move move){
    char symbol = move.player.symbol;
    int r=move.cell.row;
    int c=move.cell.col;
    rowCount.get(symbol)[r]++;
    colCount.get(symbol)[c]++;
    if(r==c){
      diagCount.put(symbol, diagCount.get(symbol)+1);
    }
    if(r+1==size-c){
      antiDiagCount.put(symbol, antiDiagCount.get(symbol)+1);
    }
    if(rowCount.get(symbol)[r]==size || colCount.get(symbol)[c]==size || diagCount.get(symbol)==size || antiDiagCount.get(symbol)==size){
      return true;
    }
    return false;
  }
}
class GameController{
  //have board and winning strategy as part of game controller to manage game state and logic
  Queue<Player> playersQueue;
  int movesCount;
  WinningStrategy winningStrategy;
  Board board;
 GameController(List<Player> players, int boardSize){
    this.playersQueue = new LinkedList<>(players);
    this.board = new Board(boardSize);
    this.winningStrategy = new WinningStrategy(boardSize, players);
    this.movesCount = 0;
  }
  void startGame(){
    //initialises turn and symbol and empty board
    Scanner sc = new Scanner(System.in);
    while(true){
      Player currentPlayer = playersQueue.poll();
      board.printBoard();
      System.out.println("Player "+currentPlayer.playerId+" ("+currentPlayer.symbol+") turn. Enter row and column:");
      int r = sc.nextInt();
      int c = sc.nextInt();
      Move move = new Move(currentPlayer, new Cell(r,c));
      if(!board.makeMove(move)){
        System.out.println("Invalid move. Try again.");
        playersQueue.add(currentPlayer); //retry same player
        continue;
      }
      movesCount++;
      if(winningStrategy.checkWin(move)){
        board.printBoard();
        System.out.println("Player "+currentPlayer.playerId+" wins!");
        break;
      }
     if(movesCount == board.size * board.size){
        board.printBoard();
        System.out.println("Game is a draw!");
        break;
      }
      playersQueue.offer(currentPlayer); //rotate turn
    }
  }
  GameStatus makeMove(Move move){
    if(playersQueue.peek() != move.player){
      throw new IllegalArgumentException("Not player's turn");
    }
    if(!board.makeMove(move)){
      throw new IllegalArgumentException("Invalid move");
    }
    movesCount++;
    if(winningStrategy.checkWin(move)){
      return GameStatus.WIN;
    }
    if(movesCount == board.size * board.size){
      return GameStatus.DRAW;
    }
    //rotate turn
    playersQueue.offer(playersQueue.poll());
    return GameStatus.IN_PROGRESS;
  }
}
//Driver code to test the implementation
public class TicTacToe{
  public static void main(String[] args) {
    Player p1 = new Player("Alice", 'X');
    Player p2 = new Player("Bob", 'O');
    GameController gameController = new GameController(List.of(p1, p2), 3);
    gameController.startGame();
  }
}