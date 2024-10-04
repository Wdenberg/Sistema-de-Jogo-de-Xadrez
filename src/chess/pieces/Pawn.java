package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {
    private ChessMatch chessMatch;

    public Pawn(Board board, Color color, ChessMatch chessMatch) {
        super(board, color);
        this.chessMatch = chessMatch;
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position p =new Position(0,0);

        if(getColor() == Color.WHITE){
            p.setValues(position.getRows() -1, position.getColumns());
            if(getBoard().positionExists(p) && !getBoard().thereIsApiece(p)){
                mat[p.getRows()][p.getColumns()] = true;

            }
            p.setValues(position.getRows() -2, position.getColumns());
            Position p2 = new Position(position.getRows() -1, position.getColumns());
            if(getBoard().positionExists(p) && !getBoard().thereIsApiece(p) && getBoard().positionExists(p2) && !getBoard().thereIsApiece(p2)&& getMoveCount() == 0 ){
                mat[p.getRows()][p.getColumns()] = true;
            }
            p.setValues(position.getRows() -1, position.getColumns() - 1);
            if(getBoard().positionExists(p) && isThereOpponentPiece(p)){
                mat[p.getRows()][p.getColumns()] = true;

            }
            p.setValues(position.getRows() -1, position.getColumns() + 1);
            if(getBoard().positionExists(p) && isThereOpponentPiece(p)){
                mat[p.getRows()][p.getColumns()] = true;

            }
            //#specialmove en passant vulnerable Pice White
            if(position.getRows() == 3){
                Position left = new Position(position.getRows(), position.getColumns() -1);
                if(getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()){
                    mat[left.getRows() - 1][left.getColumns()] = true;

                }
                Position right = new Position(position.getRows(), position.getColumns() + 1);
                if(getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()){
                    mat[right.getRows() - 1][right.getColumns()] = true;

                }
            }
        }else {
            p.setValues(position.getRows() + 1, position.getColumns());
            if(getBoard().positionExists(p) && !getBoard().thereIsApiece(p)){
                mat[p.getRows()][p.getColumns()] = true;

            }
            p.setValues(position.getRows() + 2, position.getColumns());
            Position p2 = new Position(position.getRows() + 1, position.getColumns());
            if(getBoard().positionExists(p) && !getBoard().thereIsApiece(p) && getBoard().positionExists(p2) && !getBoard().thereIsApiece(p2)&& getMoveCount() == 0 ){
                mat[p.getRows()][p.getColumns()] = true;
            }
            p.setValues(position.getRows() + 1, position.getColumns() - 1);
            if(getBoard().positionExists(p) && isThereOpponentPiece(p)){
                mat[p.getRows()][p.getColumns()] = true;

            }
            p.setValues(position.getRows() + 1, position.getColumns() + 1);
            if(getBoard().positionExists(p) && isThereOpponentPiece(p)){
                mat[p.getRows()][p.getColumns()] = true;

            }
            //#specialmove en passant vulnerable Pice Black
            if(position.getRows() == 4){
                Position left = new Position(position.getRows(), position.getColumns() -1);
                if(getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()){
                    mat[left.getRows() + 1][left.getColumns()] = true;

                }
                Position right = new Position(position.getRows(), position.getColumns() -1);
                if(getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()){
                    mat[right.getRows() + 1][right.getColumns()] = true;

                }
            }
        }


        return mat;
    }

    @Override
    public String toString(){
        return "P";
    }
}
