package de.chess.ui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.chess.audio.AudioUtil;
import de.chess.game.Board;
import de.chess.game.Move;
import de.chess.game.MoveFlag;
import de.chess.game.MoveGenerator;
import de.chess.game.MoveList;
import de.chess.game.PieceCode;
import de.chess.game.Winner;
import de.chess.io.Window;
import de.chess.main.Constants;
import de.chess.main.Main;
import de.chess.util.ImageUtil;
import de.chess.util.MathUtil;

public class BoardUI {
	
	private static final float ANIMATION_SPEED = 0.04f;
	private static final float ANIMATION_CUTOFF = 0.25f;
	
	private static int humanSide = MathUtil.RANDOM.nextInt(2);
	
	private static Move lastMove;
	
	private static double lastMoveState;
	
	private static long lastHumanMove;
	
	private static int selected = -1;
	
	private static ArrayList<Move> selectedMoves;
	
	private static int selectedSquare;
	
	private static boolean holdingPiece;
	
	private static boolean initialHold;
	
	private static Move[] pawnPromotions; 
	
	private static int winner = Winner.NONE;
	
	private static Point mouseClick;
	
	private static int clickType;
	
	public static void update(Board board) {
		if(lastMove != null) {
			
			if(lastMoveState < 1) {
				lastMoveState += ANIMATION_SPEED;
			} else {
				lastMoveState = 1;
			}
		}
		
		boolean noWinner = winner == Winner.NONE;
		
		if(noWinner && board.getSide() != humanSide && System.currentTimeMillis() - lastHumanMove >= 700 && PopupUI.getState() == 0) {
			int side = board.getSide();
			
			lastMove = board.makeAIMove();
			lastMoveState = 0;
			
			AudioUtil.playMoveSound(lastMove, side);
			
			checkForWinner(board);
		}
		
		if(mouseClick != null) {
			
			if(pawnPromotions != null) {
				if(clickType == Window.MOUSE_PRESSED) {
					int i = PromotionUI.isHoveringBox(mouseClick.x, mouseClick.y, UIManager.getWidth(), UIManager.getHeight());
					
					if(i != -1) {
						clearLastMove();
						
						board.makeMove(pawnPromotions[i]);
						
						lastMove = pawnPromotions[i];
						lastMoveState = 0;
						
						checkForWinner(board);
						
						AudioUtil.playMoveSound(lastMove, board.getSide());
						
						lastHumanMove = System.currentTimeMillis();
					}
					
					pawnPromotions = null;
				}
				
				mouseClick = null;
				
				return;
			}
			
			if(winner != Winner.NONE) {
				return;
			}
			
			int mx = mouseClick.x - 66;
			int my = mouseClick.y - 66;
			
			mouseClick = null;
			
			if(mx >= 0 && my >= 0) {
				int boardX = mx / Constants.TILE_SIZE;
				int boardY = my / Constants.TILE_SIZE;
				
				if(boardX < 8 && boardY < 8) {
					
					int index = boardY * 8 + boardX;
					
					if(humanSide == PieceCode.BLACK) {
						index = 63 - index;
					}
					
					if(clickType == Window.MOUSE_PRESSED) {
						if(selected == -1) {
							selectPiece(board, index, boardX, boardY);
						} else {
							if(index == selectedSquare) {
								holdingPiece = true;
								initialHold = false;
							}
							
							performMove(board, index, boardX, boardY, true, true);
						}
					} else if(selected != -1) {
						
						holdingPiece = false;
						
						performMove(board, index, boardX, boardY, false, false);
					}
					
				} else {
					if(clickType == Window.MOUSE_PRESSED) unselecPiece();
					else holdingPiece = false;
				}
			} else {
				if(clickType == Window.MOUSE_PRESSED) unselecPiece();
				else holdingPiece = false;
			}
		}
	}
	
	private static void performMove(Board b, int index, int boardX, int boardY, boolean withAnimation, boolean unselect) {
		Move[] moves = new Move[4];
		int l = 0;
		
		for(Move check : selectedMoves) {
			if(index == check.getTo()) {
				moves[l] = check;
				
				l++;
			}
		}
		
		if(l != 0) {
			unselecPiece();
			
			if(l == 1) {
				clearLastMove();
				
				int side = b.getSide();
				
				b.makeMove(moves[0]);
				
				lastMove = moves[0];
				lastMoveState = withAnimation ? 0 : 1;
				
				AudioUtil.playMoveSound(moves[0], side);
				
				checkForWinner(b);
				
				lastHumanMove = System.currentTimeMillis();
			} else {
				PromotionUI.setSide(b.getSide());
				
				int x = moves[0].getTo() % 8;
				
				if(humanSide == PieceCode.BLACK) {
					x = 7 - x;
				}
				
				PromotionUI.setOffset(x);
				
				pawnPromotions = moves;
			}
		} else if(index != selectedSquare) {
			if(unselect) {
				unselecPiece();
				
				selectPiece(b, index, boardX, boardY);
			}
			
		} else if(!unselect && !initialHold) unselecPiece();
	}
	
	private static void selectPiece(Board board, int index, int boardX, int boardY) {
		int p = board.getPiece(index);
		
		if(p != -1 && PieceCode.getColorFromSpriteCode(p) == board.getSide()) {
			MoveList list = new MoveList();
			
			MoveGenerator.generateAllMoves(board, list);
			
			ArrayList<Move> moves = MoveGenerator.getMovesForIndex(index, board, list, true);
			
			selectPiece(p, boardX, boardY, index, moves);
		}
	}
	
	private static void selectPiece(int p, int x, int y, int index, ArrayList<Move> moves) {
		selected = p;
		selectedMoves = moves;
		
		selectedSquare = index;
		
		holdingPiece = true;
		initialHold = true;
	}
	
	public static void unselecPiece() {
		selected = -1;
	}
	
	public static void drawBoard(Graphics2D graphics, Board board, int x, int y) {
		BufferedImage image = ImageUtil.BOARD_WHITE_SIDE;
		
		if(humanSide == PieceCode.BLACK) image = ImageUtil.BOARD_BLACK_SIDE;
		
		graphics.drawImage(image, x, y, null);
		
		int offsetX = 66;
		int offsetY = 66;
		
		if(lastMove != null) {
			int from = lastMove.getFrom();
			int to = lastMove.getTo();
			
			if(humanSide == PieceCode.BLACK) {
				from = 63 - from;
				to = 63 - to;
			}
			
			int fromX = from % 8;
			int fromY = from / 8;
			int toX = to % 8;
			int toY = to / 8;
			
			fromX = offsetX + fromX * Constants.TILE_SIZE;
			fromY = offsetY + fromY * Constants.TILE_SIZE;
			toX = offsetX + toX * Constants.TILE_SIZE;
			toY = offsetY + toY * Constants.TILE_SIZE;
			
			graphics.setColor(Constants.COLOR_BOARD_HIGHLIGHT);
			
			graphics.fillRect(fromX, fromY, Constants.TILE_SIZE, Constants.TILE_SIZE);
			graphics.fillRect(toX, toY, Constants.TILE_SIZE, Constants.TILE_SIZE);
		}
		
		if(selected != -1) {
			int square = selectedSquare;
			
			if(BoardUI.getHumanSide() == PieceCode.BLACK) {
				square = 63 - square;
			}
			
			int tileSize = Constants.TILE_SIZE;
			
			graphics.setColor(Constants.COLOR_BOARD_HIGHLIGHT);
			
			graphics.fillRect(offsetX + (square % 8) * tileSize, offsetY + (square / 8) * tileSize, tileSize, tileSize);
		}
		
		for(int cx=0; cx<8; cx++) {
			for(int cy=0; cy<8; cy++) {
				int i = cy * 8 + cx;
				
				int p = board.getPiece(i);
				
				if(p != -1) drawPiece(graphics, p, cx, cy, offsetX, offsetY);
			}
		}
		
		if(lastMove != null && lastMoveState != 1) {
			
			drawMovingPiece(graphics, board, lastMove.getFrom(), lastMove.getTo(), offsetX, offsetY);
			
			if(lastMove.getFlag() == MoveFlag.CASTLING_QUEEN_SIDE) {
				
				int rookY = lastMove.getTo() / 8;
				
				drawMovingPiece(graphics, board, 0, rookY, 3, rookY, offsetX, offsetY);
				
			} else if(lastMove.getFlag() == MoveFlag.CASTLING_KING_SIDE) {
				
				int rookY = lastMove.getTo() / 8;
				
				drawMovingPiece(graphics, board, 7, rookY, 5, rookY, offsetX, offsetY);
			}
		}
	}
	
	public static void drawHeldPiece(Graphics2D graphics) {
		if(selected != -1 && holdingPiece) {
			
			drawFloatingPiece(graphics, selected, UIManager.getMouseX() - Constants.TILE_SIZE / 2, UIManager.getMouseY() - Constants.TILE_SIZE / 2);
		}
	}
	
	public static void drawBoardCorners(Graphics2D graphics, int x, int y) {
		graphics.drawImage(ImageUtil.BOARD_CORNERS, x, y, null);
	}
	
	private static void drawPiece(Graphics2D graphics, int p, int x, int y, int offX, int offY) {
		int pos1X = x;
		int pos1Y = y;
		
		int pos2X = -1;
		int pos2Y = -1;
		
		int index = y * 8 + x;
		
		if(selected != -1 && holdingPiece && selectedSquare == index) return;
		
		if(pawnPromotions != null) {
			Move m = pawnPromotions[0];
			
			if(index == m.getFrom()) {
				pos1X = m.getTo() % 8;
				pos1Y = m.getTo() / 8;
			}
		} else if(lastMove != null && lastMoveState != 1) {
			
			if(lastMove.getTo() == index || (y == lastMove.getTo() / 8 && ((lastMove.getFlag() == MoveFlag.CASTLING_QUEEN_SIDE && x == 3) || (lastMove.getFlag() == MoveFlag.CASTLING_KING_SIDE && x == 5)))) {
				return;
			}
		}
		
		if(humanSide == PieceCode.BLACK) {
			pos1X = 7 - pos1X;
			pos1Y = 7 - pos1Y;
			
			if(pos2X != -1) {
				pos2X = 7 - pos2X;
				pos2Y = 7 - pos2Y;
			}
		}
		
		pos1X = offX + pos1X * Constants.TILE_SIZE;
		pos1Y = offY + pos1Y * Constants.TILE_SIZE;
		
		BufferedImage sprite = PieceCode.getSprite(p);
		
		if(pos2X == -1) {
			
			graphics.drawImage(sprite, pos1X, pos1Y, Constants.TILE_SIZE, Constants.TILE_SIZE, null);
			
		} else {
			pos2X = offX + pos2X * Constants.TILE_SIZE;
			pos2Y = offY + pos2Y * Constants.TILE_SIZE;
			
			double d = MathUtil.sigmoidCutOff(lastMoveState, ANIMATION_CUTOFF);
			
			AffineTransform trans = new AffineTransform();
			
			trans.translate(pos1X+(pos2X-pos1X)*d, pos1Y+(pos2Y-pos1Y)*d);
			
			float f = (float) Constants.TILE_SIZE / sprite.getWidth(); 
			
			trans.scale(f, f);
			
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			
			graphics.drawImage(sprite, trans, null);
			
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		}
	}
	
	private static void drawMovingPiece(Graphics2D graphics, Board b, int from, int to, int offX, int offY) {
		drawMovingPiece(graphics, b, from % 8, from / 8, to % 8, to / 8, offX, offY);
	}
	
	private static void drawMovingPiece(Graphics2D graphics, Board b, int x1, int y1, int x2, int y2, int offX, int offY) {
		int p = b.getPiece(y2 * 8 + x2);
		
		if(humanSide == PieceCode.BLACK) {
			x1 = 7 - x1;
			y1 = 7 - y1;
			
			x2 = 7 - x2;
			y2 = 7 - y2;
		}
		
		x1 = offX + x1 * Constants.TILE_SIZE;
		y1 = offY + y1 * Constants.TILE_SIZE;
		
		x2 = offX + x2 * Constants.TILE_SIZE;
		y2 = offY + y2 * Constants.TILE_SIZE;
		
		BufferedImage sprite = PieceCode.getSprite(p);
		
		double d = MathUtil.sigmoidCutOff(lastMoveState, ANIMATION_CUTOFF);
		
		AffineTransform trans = new AffineTransform();
		
		trans.translate(x1+(x2-x1)*d, y1+(y2-y1)*d);
		
		float f = (float) Constants.TILE_SIZE / sprite.getWidth(); 
		
		trans.scale(f, f);
		
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		graphics.drawImage(sprite, trans, null);
		
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
	}
	
	private static void drawFloatingPiece(Graphics2D graphics, int p, int x, int y) {
		int off = 66 - Constants.TILE_SIZE / 2;
		
		if(x < off) x = off;
		else if(x > off + Constants.BOARD_SIZE) x = off + Constants.BOARD_SIZE;
		
		if(y < off) y = off;
		else if(y > off + Constants.BOARD_SIZE) y = off + Constants.BOARD_SIZE;
		
		BufferedImage sprite = PieceCode.getSprite(p);
		
		graphics.drawImage(sprite, x, y, null);
	}
	
	public static void onMouseClick(Point p, int type) {
		if(Main.getBoard().getSide() == humanSide) {
			mouseClick = p;
			
			clickType = type;
		}
	}
	
	public static void clearLastMove() {
		lastMove = null;
	}
	
	private static void checkForWinner(Board b) {
		winner = b.findWinner();
		
		if(winner != Winner.NONE) {
			PopupUI.setDisplayedWinner(winner);
		}
	}
	
	public static int getHumanSide() {
		return humanSide;
	}
	
	public static void setHumanSide(int side) {
		humanSide = side;
	}
	
	public static int getWinner() {
		return winner;
	}
	
	public static void setWinner(int w) {
		winner = w;
	}
	
	public static double getLastMoveState() {
		return lastMoveState;
	}
	
	public static int getSelectedPiece() {
		return selected;
	}
	
	public static int getSelectedSquare() {
		return selectedSquare;
	}
	
	public static ArrayList<Move> getSelectedMoves() {
		return selectedMoves;
	}
	
	public static Move[] getPawnPromotions() {
		return pawnPromotions;
	}
	
}
