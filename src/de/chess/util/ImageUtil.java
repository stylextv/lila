package de.chess.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ImageUtil {
	
	public static final ArrayList<BufferedImage> ICONS = new ArrayList<BufferedImage>();
	
	public static BufferedImage BOARD_WHITE_SIDE;
	public static BufferedImage BOARD_BLACK_SIDE;
	
	public static BufferedImage POPUP_SHADOW;
	public static BufferedImage PROMOTION_SHADOW;
	
	public static BufferedImage PREDICTION_BAR_CORNERS;
	public static BufferedImage BOARD_CORNERS;
	public static BufferedImage EVAL_HISTORY_CORNERS;
	
	public static BufferedImage BLACK_BISHOP;
	public static BufferedImage BLACK_KING;
	public static BufferedImage BLACK_KNIGHT;
	public static BufferedImage BLACK_PAWN;
	public static BufferedImage BLACK_QUEEN;
	public static BufferedImage BLACK_ROOK;
	
	public static BufferedImage WHITE_BISHOP;
	public static BufferedImage WHITE_KING;
	public static BufferedImage WHITE_KNIGHT;
	public static BufferedImage WHITE_PAWN;
	public static BufferedImage WHITE_QUEEN;
	public static BufferedImage WHITE_ROOK;
	
	public static BufferedImage[] PIECE_ICONS = new BufferedImage[15];
	
	public static BufferedImage ATTACK_INDICATOR;
	
	public static BufferedImage PROFILE_ICON;
	
	public static BufferedImage SIDE_SELECTION_RANDOM;
	public static BufferedImage SIDE_SELECTION_WHITE;
	public static BufferedImage SIDE_SELECTION_BLACK;
	
	public static BufferedImage CLOSE_BUTTON;
	
	public static void load() {
		try {
			ICONS.add(loadImage("icons/icon16.png"));
			ICONS.add(loadImage("icons/icon24.png"));
			ICONS.add(loadImage("icons/icon32.png"));
			ICONS.add(loadImage("icons/icon64.png"));
			ICONS.add(loadImage("icons/icon128.png"));
			ICONS.add(loadImage("icons/icon256.png"));
			
			BOARD_WHITE_SIDE = loadImage("board_white_side.png");
			BOARD_BLACK_SIDE = loadImage("board_black_side.png");
			
			POPUP_SHADOW = loadImage("popup_shadow.png");
			PROMOTION_SHADOW = loadImage("promotion_shadow.png");

			BOARD_CORNERS = loadImage("corners_board.png");
			PREDICTION_BAR_CORNERS = loadImage("corners_prediction_bar.png");
			EVAL_HISTORY_CORNERS = loadImage("corners_eval_history.png");
			
			BLACK_BISHOP = loadImage("pieces/black_bishop.png");
			BLACK_KING = loadImage("pieces/black_king.png");
			BLACK_KNIGHT = loadImage("pieces/black_knight.png");
			BLACK_PAWN = loadImage("pieces/black_pawn.png");
			BLACK_QUEEN = loadImage("pieces/black_queen.png");
			BLACK_ROOK = loadImage("pieces/black_rook.png");
			
			WHITE_BISHOP = loadImage("pieces/white_bishop.png");
			WHITE_KING = loadImage("pieces/white_king.png");
			WHITE_KNIGHT = loadImage("pieces/white_knight.png");
			WHITE_PAWN = loadImage("pieces/white_pawn.png");
			WHITE_QUEEN = loadImage("pieces/white_queen.png");
			WHITE_ROOK = loadImage("pieces/white_rook.png");
			
			PIECE_ICONS[0] = loadImage("pieces/icons/pawn.png");
			PIECE_ICONS[1] = loadImage("pieces/icons/pawns_2.png");
			PIECE_ICONS[2] = loadImage("pieces/icons/pawns_3.png");
			PIECE_ICONS[3] = loadImage("pieces/icons/pawns_4.png");
			PIECE_ICONS[4] = loadImage("pieces/icons/pawns_5.png");
			PIECE_ICONS[5] = loadImage("pieces/icons/pawns_6.png");
			PIECE_ICONS[6] = loadImage("pieces/icons/pawns_7.png");
			PIECE_ICONS[7] = loadImage("pieces/icons/pawns_8.png");
			PIECE_ICONS[8] = loadImage("pieces/icons/bishop.png");
			PIECE_ICONS[9] = loadImage("pieces/icons/bishops.png");
			PIECE_ICONS[10] = loadImage("pieces/icons/knight.png");
			PIECE_ICONS[11] = loadImage("pieces/icons/knights.png");
			PIECE_ICONS[12] = loadImage("pieces/icons/rook.png");
			PIECE_ICONS[13] = loadImage("pieces/icons/rooks.png");
			PIECE_ICONS[14] = loadImage("pieces/icons/queen.png");
			
			ATTACK_INDICATOR = loadImage("attack_indicator.png");
			
			PROFILE_ICON = loadImage("profile/icon.png");
			
			SIDE_SELECTION_RANDOM = loadImage("side_selection/random.png");
			SIDE_SELECTION_WHITE = loadImage("side_selection/white.png");
			SIDE_SELECTION_BLACK = loadImage("side_selection/black.png");
			
			CLOSE_BUTTON = loadImage("close.png");
			
		} catch (Exception ex) {
			ex.printStackTrace();
			
			System.exit(1);
		}
	}
	
	private static BufferedImage loadImage(String name) throws IOException {
		return ImageIO.read(ImageUtil.class.getClassLoader().getResourceAsStream("assets/textures/"+name));
	}
	
}
