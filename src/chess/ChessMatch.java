package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {

    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check;
    private boolean checkMate;
    private ChessPiece enPassantVulnerable;
    private ChessPiece promoted;


    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPices = new ArrayList<>();

    public ChessMatch(){
        board = new Board(8,8);
        turn = 1;
        currentPlayer = Color.WHITE;
        initialSetup();
    }

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }
    public boolean getCheck(){
        return check;
    }
    public boolean getCheckMate(){ return  checkMate; }
    public ChessPiece getEnPassantVulnerable(){
        return enPassantVulnerable;
    }

    public ChessPiece getPromoted() {
        return promoted;
    }

    public ChessPiece[][] getPieces(){
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i = 0; i < board.getRows(); i++){
            for (int j = 0; j < board.getColumns(); j++){
                mat[i][j] = (ChessPiece)  board.piece(i,j);
            }
        }
        return mat;
    }
    public  ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition){
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        ValidadeSourcePosition(source, target);

        Piece capturePiece = makeMove(source, target);
        if(testCheck(currentPlayer)){
            undoMove(source, target, capturePiece);
            throw new ChessException ("You can't put yourself in check");
        }
        ChessPiece movedPiece = (ChessPiece)board.piece(target);

        //#SpecialMove Pomotion
        promoted = null;
        if(movedPiece instanceof  Pawn){
            if(movedPiece.getColor() == Color.WHITE && target.getRows() == 0 || movedPiece.getColor() == Color.BLACK && target.getRows() == 7){
                promoted = (ChessPiece)board.piece(target);
                promoted = replacepromotedPiece("Q");
            }
        }

        check = (testCheck(opponent(currentPlayer))) ? true : false;
        if(testCheckMate(opponent(currentPlayer))){
            checkMate = true;
        }else {
            nextTurn();
        }

        //#specialmove en passant
        if(movedPiece instanceof Pawn && (target.getRows() == source.getRows() - 2 || target.getRows() == source.getRows() + 2)){
            enPassantVulnerable = movedPiece;
        }else {
            enPassantVulnerable = null;
        }

        return (ChessPiece) capturePiece;
    }
    public ChessPiece replacepromotedPiece(String type){
        if(promoted == null){
            throw  new IllegalStateException("There is no Piece to be promoted");
        }
        if(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")){
            return  promoted;
        }
        Position pos = promoted.getChessPosition().toPosition();
        Piece p = board.removePiece(pos);
        piecesOnTheBoard.remove(p);

        ChessPiece newPiece = newPice(type, promoted.getColor());
        board.placePiece(newPiece, pos);
        piecesOnTheBoard.add(newPiece);
        return newPiece;
    }

    private ChessPiece newPice(String type, Color color){
        if(type.equals("B")) return new Bishop(board,color);
        if(type.equals("N")) return new Knight(board,color);
        if(type.equals("Q")) return new Queen(board,color);
        return new Rook(board,color);
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition){
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    private Piece makeMove(Position source, Position target){
        ChessPiece p = ((ChessPiece)board.removePiece(source));
        p.increaseMoveCount();
        Piece capturePiece = board.removePiece(target);
        board.placePiece(p, target);

        if(capturePiece != null){
            piecesOnTheBoard.remove(capturePiece);
            capturedPices.add(capturePiece);
        }
        // # SpecicalMove castling KingSide Rook
        if(p instanceof King && target.getColumns() == source.getColumns() + 2){
            Position sourceT = new Position(source.getRows(), source.getColumns() + 3);
            Position targetT = new Position(source.getRows(), source.getColumns() + 1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
            board.placePiece(rook, targetT);
            rook.increaseMoveCount();
        }
        // # SpecicalMove castling QueenSide Rook
        if(p instanceof King && target.getColumns() == source.getColumns() - 2){
            Position sourceT = new Position(source.getRows(), source.getColumns() - 4);
            Position targetT = new Position(source.getRows(), source.getColumns() - 1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
            board.placePiece(rook, targetT);
            rook.increaseMoveCount();
        }
        // #special Move en Passant
        if(p instanceof Pawn){
           if(source.getColumns() != target.getColumns() && capturePiece == null){
               Position pawnPosition;
               if(p.getColor() == Color.WHITE){
                   pawnPosition = new Position(target.getRows()  + 1, target.getColumns());
               }else {
                   pawnPosition = new Position(target.getRows()  - 1, target.getColumns());
               }
               capturePiece = board.removePiece(pawnPosition);
               capturedPices.add(capturePiece);
               piecesOnTheBoard.remove(capturePiece);
           }
        }
        return capturePiece;
    }
    private void undoMove(Position source, Position target, Piece capturedPiece){
        ChessPiece p = ((ChessPiece)board.removePiece(target));
        p.decreaseMoveCount();
        board.placePiece(p, source);
        if(capturedPiece != null){
            board.placePiece(capturedPiece,target);
            capturedPices.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }

        // # SpecicalMove castling KingSide Rook
        if(p instanceof King && target.getColumns() == source.getColumns() + 2){
            Position sourceT = new Position(source.getRows(), source.getColumns() + 3);
            Position targetT = new Position(source.getRows(), source.getColumns() + 1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetT);
            board.placePiece(rook, sourceT);
            rook.decreaseMoveCount();
        }
        // # SpecicalMove castling QueenSide Rook
        if(p instanceof King && target.getColumns() == source.getColumns() - 2){
            Position sourceT = new Position(source.getRows(), source.getColumns() - 4);
            Position targetT = new Position(source.getRows(), source.getColumns() - 1);
            ChessPiece rook = (ChessPiece)board.removePiece(target);
            board.placePiece(rook, sourceT);
            rook.decreaseMoveCount();
        }
        // #specialMove en Passant
        if(p instanceof Pawn){
            if(source.getColumns() != target.getColumns() && capturedPiece == enPassantVulnerable){
                ChessPiece pawn = (ChessPiece)board.removePiece(target);
                Position pawnPosition;
                if(p.getColor() == Color.WHITE){
                    pawnPosition = new Position(3, target.getColumns());
                }else {
                    pawnPosition = new Position(4, target.getColumns());
                }
                board.placePiece(pawn, pawnPosition);


            }
        }
    }

    public void nextTurn(){
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }
    private void  validateSourcePosition(Position position){
        if(! board.thereIsApiece(position)){
            throw new ChessException("There is no piece on source position");
        }
        if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()){
            throw new ChessException("the chosen piece is not yours");
        }
        if(!board.piece(position).isThereAnyPossibleMove()){
            throw new ChessException("There is possible for the chosen piece");
        }
    }

    private void ValidadeSourcePosition(Position source, Position target){
        if(!board.piece(source).possibleMove(target)){
            throw  new ChessException("the chosen piece can't mover target position");
        }
    }

    private void placeNewPiece(char column, int row, ChessPiece piece){
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }
    private Color opponent(Color color){
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }
    private ChessPiece king (Color color){
        List<Piece> list = piecesOnTheBoard.stream().filter(x ->((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for(Piece p : list){
            if( p instanceof King){
                return (ChessPiece) p;
            }

        }
        throw new IllegalStateException("there is no "+ color + "King on the board");
    }
    private boolean testCheck(Color color){
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x ->((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
        for(Piece p: opponentPieces){
            boolean[][] mat = p.possibleMoves();
            if(mat[kingPosition.getRows()][kingPosition.getColumns()]){
                return true;
            }
        }
        return false;

    }
    private boolean testCheckMate(Color color){
        if(!testCheck(color)){
            return false;
        }
        List<Piece> list = piecesOnTheBoard.stream().filter(x ->((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p : list){
            boolean[][] mat = p.possibleMoves();
            for(int i = 0; i < board.getRows(); i++){
                for(int j = 0; j < board.getColumns(); j++){
                    if(mat[i][j]){
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i,j);
                        Piece capturedPiece = makeMove(source,target);
                        boolean testCheck = testCheck(color);
                        undoMove(source,target,capturedPiece);
                        if(!testCheck){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    private void initialSetup(){

        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));


        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));

        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));

    }

}
