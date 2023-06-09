package amyk.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Sudoku {

    private ArrayList<ArrayList<Integer>> previousGenerate, previousVerify;

    private ArrayList<Integer> board;

    private ArrayList<Integer> player;

    public Sudoku() {
        clear();
    }

    public void generatePlayer() {
        generatePlayer(generateRandom(40, 20));
    }

    public void start() {
        generateBoard();
        generatePlayer();

        System.out.println(toString());
        Scanner sc = new Scanner(System.in);

        while (!checkBoard(player)) {
            System.out.println(printBoard(player));

            System.out.println("\nPlease enter the row of the number");
            int row = sc.nextInt();
            System.out.println("Please enter the column of the number");
            int col = sc.nextInt();
            System.out.println("What is the value?");
            int value = sc.nextInt();

            while (value <= 0 || value > 9) {
                System.out.println("Invalid value");
                System.out.println("What is the value?");
                value = sc.nextInt();
            }

            player.set(value, (row - 1) * 9 + col - 1);
        }

        sc.close();

        System.out.println("Sudoku solved!");
    }

    public ArrayList<Integer> getPlayer() {
        return player;
    }

    public void generatePlayer(int num) {
        for (int i = 0; i < num; i++) {
            ArrayList<Integer> zerosIndex = getIndexes(player, 0);
            int rand = generateRandom(zerosIndex.size() - 1, 0);

            player.set(zerosIndex.get(rand), board.get(zerosIndex.get(rand)));
        }

		/* Test the solve method
		System.out.println(printBoard(board));
		System.out.println(printBoard(player));

		System.out.println(sudokuString(player));

		try {
			System.out.println(solve(getIndexes(player, 0).get(0), getIndexes(player, 0)));
		} catch (StackOverflowError e) {
			System.out.println("------");
		}

		System.out.println(printBoard(player));*/
    }

	/* Test the solve method
	public static void main(String[] args) {
		Sudoku s = new Sudoku();
		s.generateBoard();
		s.generatePlayer();
	} */

    public String sudokuString(ArrayList<Integer> l) {
        String sudoku = "";
        for (int i : l) {
            if (i == 0) {
                sudoku += ".";
            } else {
                sudoku += i;
            }
        }
        return sudoku;
    }

    public void generateBoard() {
        generateBoard(0);
    }

    private void generateBoard(int num) {
        if (!fullBoard() && !checkBoard(board)) {
            ArrayList<Integer> available = complement(
                    combineArrayList(Arrays.asList(getNeighbours(num, board), previousGenerate.get(num))));

            if (available.size() == 0) {
                board.set(num, 0);
                previousGenerate.get(num).clear();

                generateBoard(num - 1);
            } else {
                board.set(num, available.get(generateRandom(available.size(), 0)));
                previousGenerate.get(num).add(board.get(num));

                generateBoard(num + 1);
            }
        }
    }

    private boolean solve(int num, ArrayList<Integer> zeros) {
        ArrayList<Integer> available = complement(
                combineArrayList(Arrays.asList(getNeighbours(num, player), previousVerify.get(num))));

        if (available.size() == 0) {
            if (num == zeros.get(0)) {
                return true;
            } else {
                player.set(num, 0);
                previousVerify.get(num).clear();

                return solve(zeros.get(zeros.indexOf(num) - 1), zeros);
            }
        } else {
            player.set(num, available.get(0));
            previousVerify.get(num).add(player.get(num));

            if (num == zeros.get(zeros.size() - 1)) {
                if (available.size() == 1 && player.equals(board)) {
                    return solve(num, zeros);
                } else {
                    return false;
                }
            } else {
                return solve(zeros.get(zeros.indexOf(num) + 1), zeros);
            }
        }
    }

    private ArrayList<Integer> getBlock(int num, ArrayList<Integer> list) {
        ArrayList<Integer> temp = new ArrayList<Integer>();

        int t = num % 3 * 3 + (num / 3) * 27;

        for (int i = t; i < t + 20; i += 9) {
            temp.addAll(list.subList(i, i + 3));
        }

        return removeZeros(temp);
    }

    private ArrayList<Integer> getVerticalLine(int num, ArrayList<Integer> list) {
        ArrayList<Integer> temp = new ArrayList<Integer>();

        for (int i = num; i < num + 81; i += 9) {
            temp.add(list.get(i));
        }

        return removeZeros(temp);
    }

    private ArrayList<Integer> getHorizontalLine(int num, ArrayList<Integer> list) {
        return removeZeros(new ArrayList<Integer>(list.subList(num * 9, num * 9 + 9)));
    }


    private boolean checkBlock(int num, ArrayList<Integer> list) {
        return 9 == removeDuplicates(getBlock(num, list)).size();
    }

    private boolean checkVerticalLine(int num, ArrayList<Integer> list) {
        return 9 == removeDuplicates(getVerticalLine(num, list)).size();
    }

    private boolean checkHorizontalLine(int num, ArrayList<Integer> list) {
        return 9 == removeDuplicates(getHorizontalLine(num, list)).size();
    }

    private boolean fullBoard() {
        return !board.contains(0);
    }

    public boolean checkBoard(ArrayList<Integer> list) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (!(checkVerticalLine(col, list) || checkHorizontalLine(row, list)
                        || checkBlock(row / 3 * 3 + col / 3, list))) {
                    return false;
                }
            }
        }

        return true;
    }

    private ArrayList<Integer> getIndexes(ArrayList<Integer> num, Integer value) {
        ArrayList<Integer> temp = new ArrayList<Integer>();

        for (int i = 0; i < num.size(); i++) {
            if (value.equals(num.get(i))) {
                temp.add(i);
            }
        }

        return temp;
    }

    private ArrayList<Integer> complement(ArrayList<Integer> num) {
        ArrayList<Integer> temp = new ArrayList<Integer>();

        for (int i = 1; i < 10; i++) {
            if (!num.contains(i)) {
                temp.add(i);
            }
        }

        return temp;
    }

    private ArrayList<Integer> getNeighbours(int num, ArrayList<Integer> list) {
        return removeZeros(removeDuplicates(combineArrayList(Arrays.asList(getVerticalLine(num % 9, list),
                getHorizontalLine(num / 9, list), getBlock((num / 9) / 3 * 3 + (num % 9) / 3, list)))));
    }

    private ArrayList<Integer> combineArrayList(List<ArrayList<Integer>> list) {
        ArrayList<Integer> temp = new ArrayList<Integer>();

        for (ArrayList<Integer> i : list) {
            temp.addAll(i);
        }

        return temp;
    }

    private ArrayList<Integer> removeZeros(ArrayList<Integer> num) {
        num.removeAll(Collections.singleton(0));
        return num;
    }

    private ArrayList<Integer> removeDuplicates(ArrayList<Integer> num) {
        return new ArrayList<Integer>(new HashSet<Integer>(num));
    }

    public void clear() {
        board = new ArrayList<Integer>(Collections.nCopies(81, 0));
        player = new ArrayList<Integer>(Collections.nCopies(81, 0));
        previousGenerate = new ArrayList<ArrayList<Integer>>();
        previousVerify = new ArrayList<ArrayList<Integer>>();

        for (int i = 0; i < 81; i++) {
            previousGenerate.add(new ArrayList<Integer>());
            previousVerify.add(new ArrayList<Integer>());
        }
    }

    @SuppressWarnings("unused")
    private void generateRandomBoard() {
        for (int i = 0; i < board.size(); i++) {
            board.set(i, generateRandom(9, 1));
        }
    }

    private int generateRandom(int max, int min) {
        return new Random().nextInt(max) + min;
    }

    public String printBoard(ArrayList<Integer> num) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < 81; i++) {

            if (i != 0 && i % 27 == 0) {
                sb.append("\n  ");
                for (int j = 0; j < 13; j++) {
                    sb.append("_ ");
                }
                sb.append("\n");
            }

            if (i % 9 == 0) {
                sb.append("\n");
                sb.append("  ");
            }

            if (i % 3 == 0 && i % 9 != 0) {
                sb.append(" |  ");
            }

            if (num.get(i) == 0) {
                sb.append(" ");
            } else {
                sb.append(num.get(i));
            }
            sb.append(" ");
        }

        return sb.toString();
    }

    public String toString() {
        return printBoard(board);
    }
}