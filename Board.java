import java.util.*;

class Board {
    char[][] cells;
    int size;

    Board(int size) {
        this.size = size;
        cells = new char[size][size];

        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                cells[r][c] = '.';

        int mid1 = size / 2 - 1;
        int mid2 = size / 2;

        cells[mid1][mid1] = 'W';
        cells[mid2][mid2] = 'W';
        cells[mid1][mid2] = 'B';
        cells[mid2][mid1] = 'B';
    }

    Board(Board other) {
        this.size = other.size;
        cells = new char[size][size];
        for (int r = 0; r < size; r++)
            System.arraycopy(other.cells[r], 0, cells[r], 0, size);
    }

    int count(char p) {
        int c = 0;
        for (int r = 0; r < size; r++)
            for (int col = 0; col < size; col++)
                if (cells[r][col] == p) c++;
        return c;
    }

    boolean inBounds(int r, int c) {
        return r >= 0 && r < size && c >= 0 && c < size;
    }

    boolean isLegalMove(int row, int col, char player, char opponent) {
            if (!inBounds(row, col) || cells[row][col] != '.') return false;

            int[] dr = {-1,-1,-1,0,0,1,1,1};
            int[] dc = {-1,0,1,-1,1,-1,0,1};

            for (int i = 0; i < 8; i++) {
                int r = row + dr[i];
                int c = col + dc[i];
                boolean seenOpponent = false;

                while (inBounds(r, c) && cells[r][c] == opponent) {
                    seenOpponent = true;
                    r += dr[i];
                    c += dc[i];
                }

                if (seenOpponent && inBounds(r, c) && cells[r][c] == player)
                    return true;
            }
            return false;
        }

    List<Move> getLegalMoves(char player, char opponent) {
        List<Move> list = new ArrayList<>();
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                if (isLegalMove(r, c, player, opponent))
                    list.add(new Move(r, c));
        return list;
    }

    boolean hasAnyLegalMove(char player, char opponent) {
        return !getLegalMoves(player, opponent).isEmpty();
    }

    Board makeMove(int row, int col, char player, char opponent) {
        Board next = new Board(this);
        next.cells[row][col] = player;

        int[] dr = {-1,-1,-1,0,0,1,1,1};
        int[] dc = {-1,0,1,-1,1,-1,0,1};

        for (int i = 0; i < 8; i++) {
            int r = row + dr[i];
            int c = col + dc[i];
            List<int[]> toFlip = new ArrayList<>();

            while (next.inBounds(r, c) && next.cells[r][c] == opponent) {
                toFlip.add(new int[]{r, c});
                r += dr[i];
                c += dc[i];
            }

            if (!toFlip.isEmpty() && next.inBounds(r, c) && next.cells[r][c] == player) {
                for (int[] p : toFlip)
                    next.cells[p[0]][p[1]] = player;
            }
        }
        return next;
    }
}
