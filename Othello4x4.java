import javax.swing.*;
import java.awt.*;

public class Othello4x4 extends JFrame {

    private static final int SIZE = 4;
    private Board board;
    private DiscButton[][] buttons;
    private final char human = 'B';
    private final char ai = 'W';

    private boolean humanTurn = true;

    private JLabel titleLabel;
    private JLabel blackNameLabel;
    private JLabel whiteNameLabel;
    private JLabel blackValueLabel;
    private JLabel whiteValueLabel;
    private JLabel infoLabel;

    public Othello4x4() {
        super("Othello 4x4 - Human (Black) vs AI (White)");
        board = new Board(SIZE);
        buttons = new DiscButton[SIZE][SIZE];
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        titleLabel = new JLabel("SCORE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        blackNameLabel = new JLabel("Black", SwingConstants.CENTER);
        whiteNameLabel = new JLabel("White", SwingConstants.CENTER);
        blackNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        whiteNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        blackNameLabel.setForeground(Color.WHITE);
        whiteNameLabel.setForeground(Color.WHITE);

        blackValueLabel = new JLabel("0", SwingConstants.CENTER);
        whiteValueLabel = new JLabel("0", SwingConstants.CENTER);
        blackValueLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        whiteValueLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        blackValueLabel.setForeground(Color.WHITE);
        whiteValueLabel.setForeground(Color.WHITE);

        JPanel namesRow = new JPanel(new GridLayout(1, 2));
        namesRow.setOpaque(false);
        namesRow.add(blackNameLabel);
        namesRow.add(whiteNameLabel);

        JPanel valuesRow = new JPanel(new GridLayout(1, 2));
        valuesRow.setOpaque(false);
        valuesRow.add(blackValueLabel);
        valuesRow.add(whiteValueLabel);

        JPanel scorePanel = new JPanel(new GridLayout(3, 1));
        scorePanel.setBackground(new Color(40, 40, 40));
        scorePanel.add(titleLabel);
        scorePanel.add(namesRow);
        scorePanel.add(valuesRow);

        // ================== BOARD PANEL ==================
        JPanel boardPanel = new JPanel(new GridLayout(SIZE, SIZE));
        boardPanel.setBackground(new Color(0, 100, 0));

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                final int row = r;
                final int col = c;

                DiscButton btn = new DiscButton();
                btn.setPreferredSize(new Dimension(120, 120));
                btn.addActionListener(e -> onHumanMove(row, col));

                buttons[r][c] = btn;
                boardPanel.add(btn);
            }
        }

        infoLabel = new JLabel("Black = You, White = AI", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 16));

        setLayout(new BorderLayout());
        add(scorePanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(infoLabel, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        updateBoardView();
    }

    private void onHumanMove(int row, int col) {

        if (!board.hasAnyLegalMove(human, ai)) {
            JOptionPane.showMessageDialog(this, "You have no legal moves. AI will play.");
            humanTurn = false;
            aiMove();
            return;
        }

        if (!humanTurn) {
            JOptionPane.showMessageDialog(this, "Wait for AI to play.");
            return;
        }

        if (checkGameEnd()) return;

        if (!board.isLegalMove(row, col, human, ai)) {
            JOptionPane.showMessageDialog(this, "Illegal move.");
            return;
        }

        board = board.makeMove(row, col, human, ai);
        updateBoardView();

        if (checkGameEnd()) return;

        humanTurn = false;
        aiMove();
    }

    private void aiMove() {
        Timer timer = new Timer(1000, e -> {
            if (checkGameEnd()) return;

            if (!board.hasAnyLegalMove(ai, human) && board.hasAnyLegalMove(human, ai)) {
                JOptionPane.showMessageDialog(this, "No legal moves for AI. Your turn again.");
                humanTurn = true;
                return;
            }

            Move best = findBestMove(board, ai, human, 5);
            if (best != null) {
                board = board.makeMove(best.row, best.col, ai, human);
                updateBoardView();
            }

            if (!checkGameEnd()) humanTurn = true;
        });
        timer.setRepeats(false);
        timer.start();
    }

    private boolean checkGameEnd() {
        boolean humanHas = board.hasAnyLegalMove(human, ai);
        boolean aiHas = board.hasAnyLegalMove(ai, human);

        if (!humanHas && !aiHas) {
            int humanCount = board.count(human);
            int aiCount = board.count(ai);

            String msg = (aiCount > humanCount)
                    ? "AI wins!  AI: " + aiCount + "  You: " + humanCount
                    : (humanCount > aiCount)
                    ? "You win!  AI: " + aiCount + "  You: " + humanCount
                    : "Draw!  AI: " + aiCount + "  You: " + humanCount;

            JOptionPane.showMessageDialog(this, msg);
            return true;
        }
        return false;
    }

    private void updateBoardView() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                buttons[r][c].setPiece(board.cells[r][c]);

        blackValueLabel.setText(board.count(human) + "");
        whiteValueLabel.setText(board.count(ai) + "");
    }

    private Move findBestMove(Board b, char maxPlayer, char minPlayer, int depth) {
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        for (Move m : b.getLegalMoves(maxPlayer, minPlayer)) {
            int score = minimax(b.makeMove(m.row, m.col, maxPlayer, minPlayer),
                    depth - 1, false, maxPlayer, minPlayer,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);

            if (score > bestScore) {
                bestScore = score;
                bestMove = m;
            }
        }
        return bestMove;
    }

    private int minimax(Board b, int depth, boolean maximizing,
                        char maxPlayer, char minPlayer, int alpha, int beta) {

        if (depth == 0 || (!b.hasAnyLegalMove(maxPlayer, minPlayer) &&
                !b.hasAnyLegalMove(minPlayer, maxPlayer))) {

            return b.count(maxPlayer) - b.count(minPlayer);
        }

        if (maximizing) {
            int value = Integer.MIN_VALUE;
            for (Move m : b.getLegalMoves(maxPlayer, minPlayer)) {
                value = Math.max(value,
                        minimax(b.makeMove(m.row, m.col, maxPlayer, minPlayer),
                                depth - 1, false, maxPlayer, minPlayer, alpha, beta));
                alpha = Math.max(alpha, value);
                if (alpha >= beta) break;
            }
            return value;
        } else {
            int value = Integer.MAX_VALUE;
            for (Move m : b.getLegalMoves(minPlayer, maxPlayer)) {
                value = Math.min(value,
                        minimax(b.makeMove(m.row, m.col, minPlayer, maxPlayer),
                                depth - 1, true, maxPlayer, minPlayer, alpha, beta));
                beta = Math.min(beta, value);
                if (beta <= alpha) break;
            }
            return value;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Othello4x4().setVisible(true));
    }
}
