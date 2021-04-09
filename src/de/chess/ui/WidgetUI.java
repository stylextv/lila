package de.chess.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.chess.ai.Evaluator;
import de.chess.ai.Search;
import de.chess.game.Board;
import de.chess.game.BoardConstants;
import de.chess.game.PieceCode;
import de.chess.main.Constants;
import de.chess.util.ColorUtil;
import de.chess.util.ImageUtil;
import de.chess.util.MathUtil;

public class WidgetUI {
	
	private static final int EVAL_RANGE = 2500;
	
	private static final int[] DEFAULT_PIECE_AMOUNT = new int[] {
			0,
			0,
			8,
			2,
			2,
			2,
			1
	};
	
	private static final int[] PIECE_ICON_OFFSETS = new int[] {
			0,
			0,
			0,
			10,
			8,
			12,
			14
	};
	
	private static int prediction;
	
	private static float lerpedPrediction;
	
	private static int sideSelection;
	
	private static float[] hoveringStates = new float[3];
	
	private static int[] evalHistory = new int[BoardConstants.MAX_GAME_MOVES];
	
	private static int evalHistoryPointer;
	
	public static void drawWidgets(Graphics2D graphics, Board b) {
		drawCapturedPieces(graphics, b, 66);
		
		drawPredictionBar(graphics, 16, 66);
		
		drawMenu(graphics, 66 + Constants.BOARD_SIZE + 30, 16);
	}
	
	private static void drawCapturedPieces(Graphics2D graphics, Board b, int x) {
		int dis = 10;
		
		int height = 20;
		
		drawCapturedPieces(graphics, b, BoardUI.getHumanSide(), 66, 66 - height - dis);
		drawCapturedPieces(graphics, b, (BoardUI.getHumanSide() + 1) % 2, 66, 66 + Constants.BOARD_SIZE + dis);
	}
	
	private static void drawCapturedPieces(Graphics2D graphics, Board b, int side, int x, int y) {
		x = drawCapturedPieces(graphics, b, side, PieceCode.PAWN, x, y);
		x = drawCapturedPieces(graphics, b, side, PieceCode.BISHOP, x, y);
		x = drawCapturedPieces(graphics, b, side, PieceCode.KNIGHT, x, y);
		x = drawCapturedPieces(graphics, b, side, PieceCode.ROOK, x, y);
		x = drawCapturedPieces(graphics, b, side, PieceCode.QUEEN, x, y);
	}
	
	private static int drawCapturedPieces(Graphics2D graphics, Board b, int side, int type, int x, int y) {
		int dis = 5;
		
		int defaultCount = DEFAULT_PIECE_AMOUNT[type];
		
		int count = b.getPieceAmount(PieceCode.getSpriteCode(side, type));
		
		int drawCount = defaultCount - count;
		
		if(drawCount <= 0) return x;
		
		BufferedImage image = ImageUtil.PIECE_ICONS[PIECE_ICON_OFFSETS[type] + drawCount - 1];
		
		graphics.drawImage(image, x, y, null);
		
		return x + image.getWidth() + dis;
	}
	
	private static void drawPredictionBar(Graphics2D graphics, int x, int y) {
		int target = prediction;
		
		if(target > EVAL_RANGE) target = EVAL_RANGE;
		else if(target < -EVAL_RANGE) target = -EVAL_RANGE;
		
		lerpedPrediction = MathUtil.lerp(lerpedPrediction, target, 0.05f);
		
		int w = 40;
		
		Color c1 = Constants.COLOR_PLAYER_WHITE;
		Color c2 = Constants.COLOR_PLAYER_BLACK;
		
		if(BoardUI.getHumanSide() == PieceCode.WHITE) {
			c1 = Constants.COLOR_PLAYER_BLACK;
			c2 = Constants.COLOR_PLAYER_WHITE;
		}
		
		graphics.setColor(c2);
		
		graphics.fillRect(x, y, w, Constants.BOARD_SIZE);
		
		float h = (lerpedPrediction + EVAL_RANGE) / EVAL_RANGE / 2 * Constants.BOARD_SIZE;
		
		float f = 0.075f;
		
		if(h < f) h = 0;
		else if(h > Constants.BOARD_SIZE - f) h = Constants.BOARD_SIZE;
		
		graphics.setColor(c1);
		
		graphics.fillRect(x, y, w, (int) h);
		
		if(prediction != 0) {
			String text = getPredictionText(prediction, (BoardUI.getHumanSide() + 1) % 2);
			
			int textY;
			
			Color c;
			
			graphics.setFont(Constants.FONT_BOLD);
			
			if(lerpedPrediction > 0) {
				
				textY = y + 8 + graphics.getFontMetrics().getHeight() - 5;
				c = c2;
			} else {
				
				textY = y + Constants.BOARD_SIZE - 10;
				c = c1;
			}
			
			graphics.setColor(c);
			
			graphics.drawString(text, x + (w - graphics.getFontMetrics().stringWidth(text)) / 2, textY);
		}
		
		graphics.drawImage(ImageUtil.PREDICTION_BAR_CORNERS, x, y, null);
	}
	
	private static String getPredictionText(int i, int side) {
		String s;
		
		if(Search.isMateScore(i)) {
			int moves;
			
			if(i > 0) {
				moves = (99999 - i) / 2 + 1;
			} else {
				moves = (i + 99998) / 2 + 1;
			}
			
			s = "M" + moves;
			
		} else {
			float pawns = (float) Math.abs(i) / Evaluator.GENERIC_PAWN_VALUE;
			
			s = MathUtil.DISPLAY_DECIMAL_FORMAT.format(pawns);
		}
		
		boolean b = i > 0;
		
		if(side == PieceCode.BLACK) b = !b;
		
		if(b) return "+" + s;
		else return "-" + s;
	}
	
	private static void drawMenu(Graphics2D graphics, int x, int y) {
		int w = UIManager.getWidth() - x - 16;
		int h = UIManager.getHeight() - y - 16;
		
		graphics.setColor(Constants.COLOR_MENU_BACKGROUND);
		
		graphics.fillRoundRect(x, y, w, h, 6, 6);
		
		drawMenuProfile(graphics, x + w / 2, y + 80);
		drawSideSelection(graphics, x + w / 2, y + 80 + 129 + 70 + 14);
		
		int dis = 12;
		
		int height = 300;
		
		drawEvaluationHistory(graphics, x + dis, y + h - dis - height, w - dis * 2, height);
	}
	
	private static void drawMenuProfile(Graphics2D graphics, int x, int y) {
		graphics.drawImage(ImageUtil.PROFILE_ICON, x - ImageUtil.PROFILE_ICON.getWidth() / 2, y, null);
		
		graphics.setFont(Constants.FONT_EXTRA_BOLD);
		
		int nameWidth = graphics.getFontMetrics().stringWidth(Constants.NAME);
		
		graphics.setFont(Constants.FONT_LIGHT);
		
		String elo = "(" + Constants.ELO + ")";
		
		int eloWidth = graphics.getFontMetrics().stringWidth(elo);
		
		int textX = x - (nameWidth + 7 + eloWidth) / 2;
		int textY = y + ImageUtil.PROFILE_ICON.getHeight() + 35;
		
		graphics.setColor(Constants.COLOR_TEXT_BLACK_TRANSPARENT);
		
		graphics.drawString(elo, textX + nameWidth + 7, textY);
		
		graphics.setFont(Constants.FONT_EXTRA_BOLD);
		
		graphics.setColor(Constants.COLOR_HISTORY_BLACK);
		
		graphics.drawString(Constants.NAME, textX, textY);
	}
	
	private static void drawSideSelection(Graphics2D graphics, int x, int y) {
		int i = isHoveringSelectionButton(UIManager.getMouseX(), UIManager.getMouseY());
		
		drawSideSelectionButton(graphics, 0, ImageUtil.SIDE_SELECTION_RANDOM, x, y, i);
		drawSideSelectionButton(graphics, 1, ImageUtil.SIDE_SELECTION_WHITE, x - 66, y, i);
		drawSideSelectionButton(graphics, 2, ImageUtil.SIDE_SELECTION_BLACK, x + 66, y, i);
	}
	
	private static void drawSideSelectionButton(Graphics2D graphics, int id, BufferedImage image, int x, int y, int hovering) {
		int target = hovering == id ? 1 : 0;
		
		hoveringStates[id] = MathUtil.lerp(hoveringStates[id], target, 0.4f);
		
		int size = 48;
		
		int roundness = 10;
		
		int cornerX = x - size / 2;
		int cornerY = y;
		
		graphics.setColor(Constants.COLOR_BUTTON_SHADOW);
		graphics.fillRoundRect(cornerX, cornerY + 4, size, size, roundness, roundness);
		
		graphics.setColor(Constants.COLOR_BUTTON_OUTLINE);
		graphics.fillRoundRect(cornerX, cornerY, size, size, roundness, roundness);
		
		graphics.setColor(ColorUtil.mixColors(Constants.COLOR_MENU_BACKGROUND, Constants.COLOR_BUTTON, hoveringStates[id]));
		
		graphics.fillRoundRect(cornerX + 2, cornerY + 2, size - 4, size - 4, roundness - 2, roundness - 2);
		
		graphics.drawImage(image, x - image.getWidth() / 2, y + (size - image.getHeight()) / 2, null);
	}
	
	private static void drawEvaluationHistory(Graphics2D graphics, int x, int y, int width, int height) {
		graphics.setColor(Constants.COLOR_HISTORY_BACKGROUND);
		
		graphics.fillRect(x, y, width, height);
		
		int barWidth = 12;
		
		int viewLimit = width / barWidth;
		
		int start = 0;
		
		int offsetX = 0;
		
		int barX = x;
		
		boolean overflow = evalHistoryPointer > viewLimit;
		
		if(overflow) {
			start = evalHistoryPointer - viewLimit - 1;
			
			offsetX = (evalHistoryPointer - start) * barWidth - width;
			
			barX -= offsetX;
		}
		
		if(evalHistoryPointer != 0) {
			graphics.setColor(Constants.COLOR_HISTORY_HIGHLIGHT);
			
			int highlightX;
			
			if(overflow) highlightX = x + width - barWidth;
			else highlightX = x + (evalHistoryPointer - 1) * barWidth;
			
			graphics.fillRect(highlightX, y, barWidth, height);
		}
		
		for(int i = start; i < evalHistoryPointer; i++) {
			int currentX = barX;
			int currentWidth = barWidth;
			
			if(currentX < x) {
				currentWidth -= x - currentX;
				
				currentX = x;
			}
			
			drawHistoryBar(graphics, evalHistory[i], currentX, y, currentWidth, height);
			
			barX += barWidth;
		}
		
		graphics.setColor(Constants.COLOR_HISTORY_MARKER);
		
		int step = 10;
		
		int linesStartX = x;
		
		if(overflow) {
			linesStartX = x + width - evalHistoryPointer * barWidth;
		}
		
		for(int lineX = linesStartX; lineX < x + width; lineX += barWidth * step) {
			if(lineX >= x && (lineX != x || overflow)) graphics.drawLine(lineX, y, lineX, y + height - 1);
		}
		
		graphics.drawLine(x, y + height / 2, x + width - 1, y + height / 2);
		
		graphics.drawImage(ImageUtil.EVAL_HISTORY_CORNERS, x, y, null);
	}
	
	private static void drawHistoryBar(Graphics2D graphics, int prediction, int x, int y, int width, int height) {
		if(prediction == 0) return;
		
		if(BoardUI.getHumanSide() == PieceCode.WHITE) {
			prediction = -prediction;
		}
		
		int maxHeight = height / 2;
		
		if(prediction > 0) {
			graphics.setColor(Constants.COLOR_HISTORY_WHITE);
			
			int h = (int) ((float) prediction / EVAL_RANGE * maxHeight);
			
			if(h > maxHeight) h = maxHeight;
			
			graphics.fillRect(x, y + height / 2 - h, width, h);
		} else {
			graphics.setColor(Constants.COLOR_HISTORY_BLACK);
			
			int h = (int) ((float) (-prediction) / EVAL_RANGE * maxHeight);
			
			if(h > maxHeight) h = maxHeight;
			
			graphics.fillRect(x, y + height / 2, width, h);
		}
	}
	
	public static int isHoveringSelectionButton(int mx, int my) {
		int x = 66 + Constants.BOARD_SIZE + 30;
		
		int w = UIManager.getWidth() - x - 16;
		
		x += w / 2;
		
		int y = 16 + 80 + 129 + 70 + 14;
		
		mx -= x;
		my -= y;
		
		int size = 48;
		
		if(my < 0 || my > size) return -1;
		
		mx += 90;
		
		if(mx >= 0 && mx < size) return 1;
		if(mx >= size + 18 && mx < size * 2 + 18) return 0;
		if(mx >= size * 2 + 36 && mx < size * 3 + 36) return 2;
		
		return -1;
	}
	
	public static void addToEvalHistory(int i) {
		evalHistory[evalHistoryPointer] = i;
		
		evalHistoryPointer++;
	}
	
	public static void clearEvalHistory() {
		evalHistoryPointer = 0;
	}
	
	public static void setPrediction(int i) {
		prediction = i;
	}
	
	public static int getSideSelection() {
		return sideSelection;
	}
	
	public static void setSideSelection(int i) {
		sideSelection = i;
	}
	
}
