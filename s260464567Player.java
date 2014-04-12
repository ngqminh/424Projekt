package s260464567;

import java.awt.Point;
import java.util.Queue;

import halma.CCBoard;
import halma.CCMove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

import boardgame.Board;
import boardgame.Move;
import boardgame.Player;

/**
 * 1. the heuristics (basic): The manhattan distance of your pieces to the
 * closest goal zone
 * 
 * 
 */
public class s260464567Player extends Player {
	int id = -1;
	int lastTurn = -1;
	ArrayList<CCMove> visited;
	Queue<CCMove> script = new LinkedList<CCMove>();
	// goals: {id {points {x, y}}
	// heuristic adds to score when goal zones are empty
	// the deeper the piece is, the more score it adds
	// this ensures that the pieces go in first, to avoid a scenario where a
	// piece is trapped out
	// these are sorted by score, for extra speed during scoring
	final static int goals[][][] = {
			{ { 15, 15 }, { 15, 14 }, { 14, 15 }, { 14, 14 }, { 13, 15 },
					{ 15, 13 }, { 12, 15 }, { 13, 14 }, { 15, 12 }, { 14, 13 },
					{ 12, 14 }, { 13, 13 }, { 14, 12 } },
			{ { 0, 15 }, { 1, 15 }, { 0, 14 }, { 2, 15 }, { 1, 14 }, { 0, 13 },
					{ 3, 15 }, { 2, 14 }, { 1, 13 }, { 0, 12 }, { 3, 14 },
					{ 2, 13 }, { 1, 12 } },
			{ { 15, 0 }, { 14, 0 }, { 15, 1 }, { 13, 0 }, { 14, 1 }, { 15, 2 },
					{ 12, 0 }, { 13, 1 }, { 14, 2 }, { 15, 3 }, { 12, 1 },
					{ 13, 2 }, { 14, 3 } },
			{ { 0, 0 }, { 0, 1 }, { 1, 0 }, { 2, 0 }, { 1, 1 }, { 0, 2 },
					{ 0, 3 }, { 1, 2 }, { 2, 1 }, { 3, 0 }, { 3, 1 }, { 2, 2 },
					{ 1, 3 } } };

	public s260464567Player() {
		super("260464567");
	}

	public s260464567Player(String s) {
		super(s);

	}

	@Override
	public Board createBoard() {
		return new CCBoard();
	}

	@Override
	public Move chooseMove(Board theBoard) {
		CCBoard board = (CCBoard) theBoard;
		Move bestMove = null;
		int turnNum = board.getTurnsPlayed();
		id = super.getColor();
		CCMove nullMove = new CCMove(id, null, null);
		// opening build order
		if (turnNum == 0) {

			script.add(unMirror(new CCMove(id, new Point(2, 2), new Point(3, 3))));

			script.add(unMirror(new CCMove(id, new Point(0, 0), new Point(2, 2))));
			script.add(unMirror(new CCMove(id, new Point(2, 2), new Point(4, 4))));
			script.add(nullMove);

			script.add(unMirror(new CCMove(id, new Point(2, 0), new Point(2, 2))));
			script.add(nullMove);

			script.add(unMirror(new CCMove(id, new Point(2, 1), new Point(2, 3))));
			script.add(unMirror(new CCMove(id, new Point(2, 3), new Point(4, 3))));
			script.add(unMirror(new CCMove(id, new Point(4, 3), new Point(4, 5))));
			script.add(nullMove);

			script.add(unMirror(new CCMove(id, new Point(4, 5), new Point(5, 5))));

			script.add(unMirror(new CCMove(id, new Point(1, 2), new Point(3, 2))));
			script.add(unMirror(new CCMove(id, new Point(3, 2), new Point(3, 4))));
			script.add(unMirror(new CCMove(id, new Point(3, 4), new Point(5, 4))));
			script.add(unMirror(new CCMove(id, new Point(5, 4), new Point(5, 6))));
			script.add(nullMove);

			script.add(unMirror(new CCMove(id, new Point(5, 6), new Point(6, 6))));
			System.out.println("Done");

		}
		// bestMove = board.getLegalMoves().get(0);

		// if(script.isEmpty()) {
		// ArrayList<CCMove> moves = board.getLegalMoves();
		// int min = manhattan(board.getPieces(id)) +
		// zoneScore(board.getPieces(id));
		// CCMove move = null;
		// for (CCMove i : moves) {
		// ArrayList<Point> prev = prevMove(board.getPieces(id), i);
		// int score = manhattan(prev) + zoneScore(prev);
		// if (min > score) {
		// move = i;
		// min = score;
		// }
		// }
		// script.add(move);
		// }
		System.out.println("START");
		if (script.isEmpty()) {
			int min = manhattan(board.getPieces(id)) + zoneScore(board, board.getPieces(id));
			System.out.println("Min is: " + min);
			HashMap<CCMove, Integer> scores = new HashMap<CCMove, Integer>();
			HashMap<CCMove, CCMove> paths = new HashMap<CCMove, CCMove>();
			paths = all(board, scores);
			System.out.println(paths.keySet().size());
			// System.out.println(paths.isEmpty());
			// System.out.println(paths.size());
			// System.out.println(min);
			CCMove move = null;
			System.out.println("Min is: " + min);
			System.out.println(manhattan(board.getPieces(id)));
			System.out.println(zoneScore(board, board.getPieces(id)));
			for (CCMove m : scores.keySet()) {
				int score = scores.get(m);
				System.out.println(m.toPrettyString());
				System.out.println(score);
				
				if (min >= score) {
					System.out.println("CHANGED");
					System.out.println(m.toPrettyString());
					if (m.equals(nullMove)) {
						if (board.getLegalMoves().size() > 1
								|| !board.getLegalMoves().get(0)
										.equals(nullMove)) {
							scores.put(m, Integer.MAX_VALUE);
							continue;
						}
					}
					min = score;
					move = m;
				}
			}
			CCMove prev = paths.get(move);
			Stack<CCMove> st = new Stack<CCMove>();
			while (move != null) {
				st.push(move);
				move = prev;
				prev = paths.get(move);
			}
			while (!st.isEmpty()) {
				script.add(st.pop());
			}
		}

		bestMove = script.poll();
		if (bestMove == null) {
			bestMove = nullMove;
		}
		System.out.println(turnNum);
		return bestMove;
	}

	// tried to be clever with hashmaps of positions...
	// a tree of moves would've been better...
	// used hashmap of moves instead, works fine, don't need this
	private CCMove inferMove(ArrayList<Point> to, ArrayList<Point> from) {
		Point end = null;
		Point start;
		// we know only 1 point differ, if we remove everything only the point
		// where the move was from should be left
		// only the point that the move was to would not be removed
		for (Point p : to) {
			if (from == null)
				break;
			if (!from.remove(p)) {
				end = p;
			}
		}
		CCMove move = null;

		if (end != null) {
			start = from.get(0);
			move = new CCMove(id, start, end);
		}

		return move;
	}

	// so I don't have to hard code opening builds for all locations
	// could be useful later? who knows
	// probably not
	private CCMove unMirror(CCMove move) {
		Point to = move.getTo();
		Point from = move.getFrom();
		if (id == 2) {
			Point newTo = new Point(to.x, 15 - to.y);
			Point newFrom = new Point(from.x, 15 - from.y);
			move = new CCMove(id, newFrom, newTo);
		} else if (id == 1) {
			Point newTo = new Point(15 - to.x, to.y);
			Point newFrom = new Point(15 - from.x, from.y);
			move = new CCMove(id, newFrom, newTo);
		} else if (id == 3) {
			Point newTo = new Point(15 - to.x, 15 - to.y);
			Point newFrom = new Point(15 - from.x, 15 - from.y);
			move = new CCMove(id, newFrom, newTo);
		}	
		return move;
	}

	public ArrayList<Point> prevMove(ArrayList<Point> pieces, CCMove move) {
		Point from = move.getFrom();
		Point to = move.getTo();
		ArrayList<Point> after = new ArrayList<Point>();
		if (from == null)
			return pieces;
		for (Point i : pieces) {
			if (i != null && i.x == from.x && i.y == from.y) {
				Point np = new Point((int) to.getX(), (int) to.getY());
				after.add(np);
			} else {
				Point np = new Point((int) i.getX(), (int) i.getY());
				after.add(np);
			}
		}
		return after;
	}

	// get manhattan distance: distance from each piece to the closest goal zone
	private int manhattan(ArrayList<Point> pieces) {
		int[][] zone = goals[id];
		int mdist = 0;
		// go through every piece in the list
		for (Point i : pieces) {
			int x = (int) i.getX();
			int y = (int) i.getY();
			int smallest = Integer.MAX_VALUE;
			// we go through every goal zone to find the closest one
			for (int j = 0; j < zone.length; j++) {
//				System.out.println("JAY" + j);
				int dx = Math.abs(zone[j][0] - x);
				int dy = Math.abs(zone[j][1] - y);
//				System.out.println(dx+dy);
				if ((dx + dy) < smallest) {
					smallest = dx + dy;
//					if (x == 2 && y == 2) System.out.println(x + " "  + y + " " + dx + " " + dy + " " + zone[j][0] + " " + zone[j][1]);
				}
			}

			// we update manhattan distance with distance from piece to closest
			// point
			mdist += smallest;
//			System.out.println("Piece: " + x + " " + y);
//			System.out.println(smallest);
			
		}
		System.out.println(mdist);
		return mdist;
	}

	// we also add a gradient score towards the corner piece, so the pieces will
	// move inwards instead of staying at the edge
	private int zoneScore(CCBoard theBoard, ArrayList<Point> pieces) {
		int score = 0;
		int[][] zone = goals[id];
		for (int j = 0; j < zone.length; j++) {
			int malus = 0;
			int malus2 = 0;
			// we assign a negative score "malus" to the goal zone
			if (j == 0)
				malus = 5;
			else if (0 < j && j < 3)
				malus = 4;
			else if (3 <= j && j < 6)
				malus = 3;
			else if (6 <= j && j < 10)
				malus = 2;
			else
				malus = 1;

			// if a piece is in that zone, no malus, else we add it to the score
			for (Point i : pieces) {
				if (i.getX() == zone[j][0] && i.getY() == zone[j][1])
					malus = 0;

				for (int otherId = 0; otherId < 4; otherId++) {
					if (otherId == id) {
						continue;
					}
					for (int square = 0; square < 13; square++) {
						if (i.x == goals[otherId][square][0]
								&& i.y == goals[otherId][square][1]) {
							malus = theBoard.getTurnsPlayed() / 10;
							// GET OUT GET OUT
						}
					}

				}
				
			}
			score += malus;
			if (id == 0) {
//				System.out.println(malus);
			}
		}
		return score;

	}

	private HashMap<CCMove, CCMove> all(CCBoard oBoard,
			HashMap<CCMove, Integer> scores) {
		HashMap<CCMove, CCMove> paths = new HashMap<CCMove, CCMove>();
		for (Point p : oBoard.getPieces(id)) {
			for (CCMove m : oBoard.getLegalMoveForPiece(p, id)) {
				CCBoard clone = (CCBoard) oBoard.clone();
				paths.put(m, null);
				clone.move(m);
				Integer score = manhattan(clone.getPieces(id))
						+ zoneScore(clone, clone.getPieces(id));
				scores.put(m, score);
//				System.out.println(m.toPrettyString());
//				System.out.println(score);
//				System.out.println(manhattan(clone.getPieces(id)));
//				System.out.println(zoneScore(clone, clone.getPieces(id)));
				
				paths = recurrHops(scores, paths, oBoard, m);
			}

		}

		return paths;
	}

	private HashMap<CCMove, CCMove> recurrHops(HashMap<CCMove, Integer> scores,
			HashMap<CCMove, CCMove> paths, CCBoard board, CCMove prevMove) {
		CCBoard nBoard = (CCBoard) board.clone();
		nBoard.move(prevMove);
		ArrayList<CCMove> legal = nBoard.getLegalMoves();
		if (nBoard.getTurn() != id) {
			return paths;
		}
		for (CCMove m : legal) {
			if (m.getTo() == null && legal.size() == 1
					|| nBoard.getLastMoved() == null) {
				return paths;
			}
			if (m.getTo() == null) {
				continue;
			}

			if (!paths.containsKey(m)) {
				paths.put(m, prevMove);
				Integer score = manhattan(nBoard.getPieces(id))
						+ zoneScore(nBoard, nBoard.getPieces(id));
				scores.put(m, score);
				System.out.println(m.toPrettyString());
				System.out.println(score);
				System.out.println(manhattan(nBoard.getPieces(id)));
				System.out.println(zoneScore(nBoard, nBoard.getPieces(id)));
				recurrHops(scores, paths, nBoard, m);
			}
		}
		return paths;

	}

}
