package src;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;

public class PointComponent extends JComponent {
	public static Color boardColor = Color.CYAN;
	public static Color boardLineColor = Color.BLACK;
	public static Color lineColor = Color.BLACK;
	public static Color lastColor = Color.RED;
	
	private Point piece = null; //Point
	
	private boolean north,south,east,west;
	
	public PointComponent () {}
	
	private boolean last = false;
	private static final int LAST_SIZE = 8;
	
	public void paintComponent (Graphics g) { //draw Point
		int width = getWidth();
		int height = getHeight();
		g.setColor(boardColor);
		g.fillRect(0,0,width,height);
		
		int halfWidth = width >> 1;
		int halfHeight = height >> 1;
		g.setColor(boardLineColor);
		g.drawLine(0,halfHeight,width,halfHeight);
		g.drawLine(halfWidth,0,halfWidth,height);
		
		if (piece == null) return;
		
		Color color = null;
		assert piece == Point.WHITE || piece == Point.BLACK;
		if (piece == Point.BLACK)
			color = Color.BLACK;
		else color = Color.WHITE;
		
		int xAdd = (int) ((halfWidth / 10.0) * 9);
		int yAdd = (int) ((halfHeight / 10.0) * 9);
		Graphics2D g2 = (Graphics2D) g;
		
		int ex = halfWidth - xAdd;
		int ey = halfHeight - yAdd;
		int ewidth = xAdd << 1;
		int eheight = yAdd << 1;
		
		g2.setPaint(color);
		Ellipse2D epse = new Ellipse2D.Double(ex,ey,ewidth,eheight);
		g2.fill(epse);
		
		if (last) {
			g2.setPaint(lastColor);
			g2.drawLine(halfWidth,halfHeight + LAST_SIZE,halfWidth,halfHeight - LAST_SIZE);
			g2.drawLine(halfWidth - LAST_SIZE,halfHeight,halfWidth + LAST_SIZE,halfHeight);
			last = false;
		}
	}
	
	public Point getPiece () { return piece; }
	
	public boolean setPoint (Point point) { //set Point
		assert point != null;
		if (piece != null) return false;
		piece = point;
		last = true;
		return true;
	}
	
	public void clear () { piece = null; }
}