package src;

import java.awt.*;
import javax.swing.*;
import java.io.*;

public class PointPanel extends JPanel {
	private static final int SIZE = 19;
	
	private PointComponent[][] board;
	
	private Point nowPiece = Point.BLACK;
	private JLabel display;
	
	public void resetNowPiece () {
		nowPiece = Point.BLACK;
	}
	
	public void updateDisplay() {
		if (nowPiece == Point.WHITE)
			display.setText("°×ÆåÂä×Ó");
		else display.setText("ºÚÆåÂä×Ó");
	}
	
	public PointPanel (JLabel display) {
		this.display = display;
		setUp();
	}
	
	public Point getWinPiece () {
		updatePiece();
		return nowPiece;
	}
	
	public Point getNowPiece () { return nowPiece; }
	
	public boolean downPoint (int x,int y) {
		if (board[x][y].setPoint(nowPiece)) {
			updatePiece();
			updateDisplay();
			updateComponent();
			return checkWin(x,y);
		} return false;
	}
	
	public boolean takePoint (double x,double y) {
		int width = getWidth();
		int height = getHeight();
		
		int i = (int) (x / (width / SIZE));
		int j = (int) (y / (height / SIZE));
		return downPoint(j,i);
	}
	
	public boolean takePoint (PrintStream out,double x,double y) {
		int width = getWidth();
		int height = getHeight();
		
		int i = (int) (x / (width / SIZE));
		int j = (int) (y / (height / SIZE));
		out.printf("%d %d%n",j,i);
		if (downPoint(j,i)) {
			out.println("-1 -1\n");
			return true;
		} else return false;
	}
	
	public boolean checkWin (int line,int columus) {
		Point point = board[line][columus].getPiece();
		int min = -4,max = 4;
		int count = 0;
		
		while (line + min < 0) min++;
		while (line + max >= SIZE) max--;
		while (min <= max) {
			if (board[line + min][columus].getPiece() == point)
				count++;
			else
				count = 0;
			if (count >= 5) return true;
			min++;
		}
		
		min = -4;
		max = 4;
		count = 0;
		
		while (columus + min < 0) min++;
		while (columus + max >= SIZE) max--; 
		while (min <= max) {
			if (board[line][columus + min].getPiece() == point)
				count++;
			else
				count = 0;
			if (count >= 5) return true;
			min++;
		}
		
		min = -4;
		max = 4;
		count = 0;
		
		while (line + min < 0 || columus + min < 0) min++;
		while (line + max >= SIZE || columus + max >= SIZE) max--;
		while (min <= max) {
			if (board[line + min][columus + min].getPiece() == point)
				count++;
			else
				count = 0;
		if (count >= 5) return true;
			min++;
		}
		
		min = -4;
		max = 4;
		count = 0;
		
		while (line - min >= SIZE || columus + min < 0) min++;
		while (line - max < 0 || columus + max >= SIZE) max--;
		while (min <= max) {
			if (board[line - min][columus + min].getPiece() == point)
				count++;
			else
				count = 0;
			if (count >= 5) return true;
			min++;
		}
		
		return false;
	}
	
	public void updateComponent () {/*
		for (int i = 0;i < SIZE;i++)
			for ()*/
		repaint();
	}
	
	public void updatePiece () {
		if (nowPiece == Point.WHITE) nowPiece = Point.BLACK;
		else nowPiece = Point.WHITE;
	}
	
	public void clearBoard () {
		for (int i = 0;i < SIZE;i++)
			for (int j = 0;j < SIZE;j++) 
				board[i][j].clear();
	}
	
	private void setUp () {
		setLayout(new GridLayout(SIZE,SIZE));
		board = new PointComponent[SIZE][SIZE];
		
		for (int i = 0;i < SIZE;i++) 
			for (int j = 0;j < SIZE;j++) {
				board[i][j] = new PointComponent();
				add(board[i][j]);
			}
	}
}