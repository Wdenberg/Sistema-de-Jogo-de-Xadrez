package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Rook  extends ChessPiece {

    public Rook(Board board, Color color) {
        super(board, color);
    }

    @Override
    public  String toString(){
        return "R";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

        Position p = new Position(0,0);
        //above
        p.setValues(position.getRows() - 1, position.getColumns());
        while (getBoard().positionExists(p) && !getBoard().thereIsApiece(p)){
            mat[p.getRows()][p.getColumns()] = true;
            p.setRows(p.getRows() - 1);
        }
        if(getBoard().positionExists(p) && isThereOpponentPiece(p)){
            mat[p.getRows()][p.getColumns()] = true;
        }

        //above left
        p.setValues(position.getRows() , position.getColumns() - 1);
        while (getBoard().positionExists(p) && !getBoard().thereIsApiece(p)){
            mat[p.getRows()][p.getColumns()] = true;
            p.setColumns(p.getColumns() - 1);
        }
        if(getBoard().positionExists(p) && isThereOpponentPiece(p)){
            mat[p.getRows()][p.getColumns()] = true;
        }

        //above Right
        p.setValues(position.getRows() , position.getColumns() + 1);
        while (getBoard().positionExists(p) && !getBoard().thereIsApiece(p)){
            mat[p.getRows()][p.getColumns()] = true;
            p.setColumns(p.getColumns() + 1);
        }
        if(getBoard().positionExists(p) && isThereOpponentPiece(p)){
            mat[p.getRows()][p.getColumns()] = true;
        }

        // below
        p.setValues(position.getRows() + 1, position.getColumns());
        while (getBoard().positionExists(p) && !getBoard().thereIsApiece(p)){
            mat[p.getRows()][p.getColumns()] = true;
            p.setRows(p.getRows() + 1);
        }
        if(getBoard().positionExists(p) && isThereOpponentPiece(p)){
            mat[p.getRows()][p.getColumns()] = true;
        }
        return mat;
    }
}
