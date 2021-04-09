package de.chess.ui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import com.github.weisj.darklaf.theme.IntelliJTheme;

import de.chess.game.PieceCode;
import de.chess.game.Winner;
import de.chess.io.Window;
import de.chess.main.Constants;
import de.chess.main.Main;
import de.chess.util.MathUtil;

public class UIManager {
	
	private static Window window;
	
	private static int width;
	private static int height;
	
	private static int mouseX;
	private static int mouseY;
	
	private static int mouseXMoved;
	
	public static void createWindow() {
		window = new Window(Constants.WINDOW_DEFAULT_WIDTH + 16, Constants.WINDOW_DEFAULT_HEIGHT + 39);
		
		Window.installTheme(new IntelliJTheme());
		
		window.create();
	}
	
	public static void update() {
		BoardUI.update(Main.getBoard());
	}
	
	public static void drawFrame(Graphics2D graphics) {
		width = window.getWidth();
		height = window.getHeight();
		
		Point p = window.getMousePosition();
		if(p != null) {
			mouseXMoved = p.x - mouseX;
			mouseX = p.x;
			mouseY = p.y;
		} else mouseXMoved = 0;
		
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		graphics.setColor(Constants.COLOR_BACKGROUND);
		graphics.fillRect(0, 0, width, height);
		
		WidgetUI.drawWidgets(graphics, Main.getBoard());
		
		BoardUI.drawBoard(graphics, Main.getBoard(), 66, 66);
		
		MoveIndicatorUI.drawMoves(graphics, Main.getBoard());
		
		BoardUI.drawBoardCorners(graphics, 66, 66);
		
		BoardUI.drawHeldPiece(graphics);
		
		PromotionUI.updateDropDown(graphics);
		
		PopupUI.updatePopup(graphics);
	}
	
	public static void onMouseClick(Point p, int type) {
		BoardUI.onMouseClick(p, type);
		
		if(type == Window.MOUSE_RELEASED) {
			if(PopupUI.isActive()) {
				if(PopupUI.isHoveringCloseButton(p.x, p.y, width, height)) {
					
					PopupUI.close();
					
					return;
				}
			}
			
			int i = WidgetUI.isHoveringSelectionButton(p.x, p.y);
			
			if(i != -1) {
				WidgetUI.setSideSelection(i);
				
				int j;
				
				if(i == 0) j = MathUtil.RANDOM.nextInt(2);
				else if(i == 1) j = PieceCode.WHITE;
				else j = PieceCode.BLACK;
				
				int ply = Main.getBoard().getHistoryPly();
				
				if((ply != 0 && (ply != 1 || BoardUI.getHumanSide() == PieceCode.WHITE)) || j != BoardUI.getHumanSide()) {
					BoardUI.setHumanSide(j);
					
					BoardUI.clearLastMove();
					
					BoardUI.unselecPiece();
					
					Main.getBoard().reset();
					
					WidgetUI.setPrediction(0);
					
					WidgetUI.clearEvalHistory();
					
					BoardUI.setWinner(Winner.NONE);
				}
			}
		}
	}
	
	public static void drawSync() {
		window.drawSync();
	}
	
	public static int getWidth() {
		return width;
	}
	
	public static int getHeight() {
		return height;
	}
	
	public static int getMouseX() {
		return mouseX;
	}
	
	public static int getMouseY() {
		return mouseY;
	}
	
	public static int getMouseXMoved() {
		return mouseXMoved;
	}
	
}
