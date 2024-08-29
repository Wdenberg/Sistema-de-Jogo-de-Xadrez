package boardgame;

public class Position {
    private Integer rows;
    private Integer columns;

    public Position(Integer rows, Integer columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }


    @Override
    public String toString() {
        return rows + ", " + columns;
    }
}
