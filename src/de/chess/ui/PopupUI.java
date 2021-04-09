package de.chess.ui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

import de.chess.game.Winner;
import de.chess.main.Constants;
import de.chess.util.ColorUtil;
import de.chess.util.ImageUtil;
import de.chess.util.MathUtil;

public class PopupUI {
	
	private static final int SHADOW_MARGIN = 80;
	
	private static final BufferedImage BUFFER = new BufferedImage(280 + SHADOW_MARGIN, 160 + SHADOW_MARGIN, BufferedImage.TYPE_INT_ARGB);
	private static final Graphics2D BUFFER_GRAPHICS = (Graphics2D) BUFFER.getGraphics();
	
	private static float state;
	
	private static int displayWinner = Winner.NONE;
	
	private static boolean closed;
	
	static {
		BUFFER_GRAPHICS.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		BUFFER_GRAPHICS.setBackground(ColorUtil.TRANSPARENT);
	}
	
	public static BufferedImage generate(String title, float alpha) {
		BUFFER_GRAPHICS.clearRect(0, 0, BUFFER.getWidth(), BUFFER.getHeight());
		
		int width = BUFFER.getWidth() - SHADOW_MARGIN;
		int height = BUFFER.getHeight() - SHADOW_MARGIN;
		
		int off = SHADOW_MARGIN / 2;
		
		BUFFER_GRAPHICS.drawImage(ImageUtil.POPUP_SHADOW, 0, 2, null);
		
		BUFFER_GRAPHICS.setColor(Constants.COLOR_MENU_BACKGROUND);
		BUFFER_GRAPHICS.fillRoundRect(off, off, width, height, 4, 4);
		
		BUFFER_GRAPHICS.setColor(Constants.COLOR_HISTORY_BLACK);
		BUFFER_GRAPHICS.setFont(Constants.FONT_EXTRA_BOLD_LARGE);
		
		BUFFER_GRAPHICS.drawString(title, off + width/2 - BUFFER_GRAPHICS.getFontMetrics().stringWidth(title)/2, off + height/2 + 9);
		
		int disX = 16;
		int disY = 12;
		
		BUFFER_GRAPHICS.drawImage(ImageUtil.CLOSE_BUTTON, off + width - disX - ImageUtil.CLOSE_BUTTON.getWidth(), off + disY, null);
		
		DataBuffer data = BUFFER.getRaster().getDataBuffer();
		
		for(int i=0; i<data.getSize(); i++) {
			int rgb = data.getElem(i);
			
			int a = (rgb >>> 24) & 0xFF;
			a = Math.round(alpha * a);
			
			rgb = (rgb & 0x00FFFFFF) + (a << 24);
			
			data.setElem(i, rgb);
		}
		
		return BUFFER;
	}
	
	public static void updatePopup(Graphics2D graphics) {
		boolean show = isActive();
		
		state = MathUtil.lerp(state, show ? 1 : 0, 0.2f);
		
		if(state > 0.005f) {
			String s;
			
			if(displayWinner == Winner.DRAW) s = "Draw";
			else s = displayWinner == BoardUI.getHumanSide() ? "Victory" : "Defeat";
			
			drawPopup(graphics, s);
		} else {
			state = 0;
		}
	}
	
	private static void drawPopup(Graphics2D graphics, String title) {
		BufferedImage image = generate(title, state);
		
		float x = 66 + (Constants.BOARD_SIZE - image.getWidth()) / 2;
		float y = 66 + (Constants.BOARD_SIZE - image.getHeight()) / 2;
		
		x = x - (1 - state) * 40;
		
		AffineTransform trans = new AffineTransform();
		
		trans.translate(x, y);
		
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		graphics.drawImage(image, trans, null);
		
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
	}
	
	public static boolean isActive() {
		return BoardUI.getWinner() != Winner.NONE && BoardUI.getLastMoveState() == 1 && !closed;
	}
	
	public static boolean isHoveringCloseButton(int mx, int my, int width, int height) {
		int x = 66 + (Constants.BOARD_SIZE - BUFFER.getWidth()) / 2;
		int y = 66 + (Constants.BOARD_SIZE - BUFFER.getHeight()) / 2;
		
		int w = BUFFER.getWidth() - SHADOW_MARGIN;
		
		int off = SHADOW_MARGIN / 2;
		
		int disX = 16;
		int disY = 12;
		
		int margin = 10;
		
		int l = ImageUtil.CLOSE_BUTTON.getWidth();
		
		x += off + w - disX - l - margin;
		y += off + disY - margin;
		
		x = x - (int) ((1 - state) * 40);
		
		x = mx - x;
		y = my - y;
		
		int size = l + margin * 2;
		
		return x >= 0 && y>= 0 && x < size && y < size;
	}
	
	public static void close() {
		closed = true;
	}
	
	public static void setDisplayedWinner(int w) {
		closed = false;
		
		displayWinner = w;
	}
	
	public static float getState() {
		return state;
	}
	
}
