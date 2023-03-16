package src;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.io.*;

public class MainFrame extends JFrame {
	public static void main (String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run () {
				JFrame frame = new MainFrame();
				
				frame.setTitle("五子棋");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				frame.setResizable(false);

				// frame.addComponentListener(new ComponentAdapter() {//拖动窗口监听
				// 	public void componentResized(ComponentEvent e) {  
				// 		int width = frame.getWidth();
				// 		int height = frame.getHeight();

				// 		if (width > height) frame.setSize(width, width);
				// 		else frame.setSize(height, height);
				// 	}  
				// }); 
			}
		});
	}
	
	private static final int DEFAULT_WIDTH = 600;
	private static final int DEFAULT_HEIGHT = 680; //宽高
	
	private PointPanel board; //棋盘
	
	private MouseListener player; //玩家监听器
	
	private JMenu mainMenu; //主菜单
	
	private JLabel display; //用于显示信息
	
	public MainFrame () {
		display = new JLabel("等待选择");
		display.setFont(FONT);
		add(display,BorderLayout.NORTH);
		
		board = new PointPanel(display);
		add(board);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		mainMenu = new JMenu("新游戏");
		menuBar.add(mainMenu);
		mainMenu.setFont(FONT);
		
		JMenuItem localGame = new JMenuItem("本地游戏");
		addMenuItem(localGame);
		localGame.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent event) {
				display.setText("本地游戏 黑棋下");
				board.updateComponent();
				board.clearBoard();
				addPlayer();
			}
		});
		
		JMenu netGameMenu = new JMenu("网络游戏");
		addMenuItem(netGameMenu);
		
		JMenuItem createRoomItem = new JMenuItem("创建房间");
		addMenuItem(netGameMenu,createRoomItem);
		createRoomItem.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent event) {
				new Thread(new Runnable(){
					public void run () {
						createRoom();
					}
				}).start();
			}
		});
		
		JMenuItem connectRoomItem = new JMenuItem("连接房间");
		addMenuItem(netGameMenu,connectRoomItem);
		connectRoomItem.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent event) {
				new Thread(new Runnable(){
					public void run () {
						connectRoom(JOptionPane.showInputDialog(MainFrame.this,"输入IP地址:"));
					}
				}).start();
			}
		});
			
		setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
	}
	
	private static final Font FONT = new Font("Serif",Font.PLAIN,13);
	
	public void addMenuItem (JComponent component) {
		component.setFont(FONT);
		mainMenu.add(component);
	}
	
	public void addMenuItem (JMenu menu,JComponent component) {
		component.setFont(FONT);
		menu.add(component);
	}
	
	private static final int NORMAL_PORT = 8189;
	private ServerSocket server;
	private Socket socket;
	
	public void createRoom () {
		board.clearBoard();
		board.updateComponent();
		
		try {
			display.setText("等待连接...");
			new Thread(new Connecter(new ServerSocket(NORMAL_PORT).accept(),Point.BLACK)).start();
			display.setText("对方已连接");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void connectRoom (String address) {
		board.clearBoard();
		board.updateComponent();
		
		int port = NORMAL_PORT;
		if (address.contains(":")) {
			int middle = 0;
			while (address.charAt(middle) != ':') middle++;
		
			port = Integer.parseInt(address.substring(middle + 1));
			address = address.substring(0,middle);
		}
		
		try {
			display.setText("连接中");
			new Thread(new Connecter(new Socket(address,port),Point.WHITE)).start();
			display.setText("已连接");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class Connecter implements Runnable {
		private Socket socket;
		private Point piece;
		
		public Connecter (Socket socket,Point piece) {
			this.socket = socket;
			this.piece = piece;
		}
		
		public void run () {
			try {
				Scanner in = new Scanner(socket.getInputStream());
				addNetPlayer(new PrintStream(socket.getOutputStream()),piece);
				
				display.setText("完成");
				
				while (in.hasNextLine()) {
					int i = in.nextInt();
					int j = in.nextInt();
					if (i == -1) netWin();
					else board.downPoint(i,j);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void removePlayer () {
		if (player == null) return;
		board.removeMouseListener(player);
	}
	
	public void win () {
		display.setText("结束");
		JOptionPane.showMessageDialog(MainFrame.this,board.getWinPiece() + " 获胜");
		removePlayer();
	}
	
	public void netWin () {
		display.setText("结束");
		JOptionPane.showMessageDialog(MainFrame.this,board.getWinPiece() + " 获胜");
		if (JOptionPane.showConfirmDialog(MainFrame.this,null,"是否重玩?",JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
			board.clearBoard();
			board.updateComponent();
		} else closeSocket();
	}
	
	public void addPlayer () {
		player = new MouseAdapter(){
			public void mouseClicked (MouseEvent event) {
				if (board.takePoint(event.getX(),event.getY())) {
					win();
				}
			}
		};
		board.addMouseListener(player);
	}
	
	public void addNetPlayer (final PrintStream out,final Point point) {
		player = new MouseAdapter(){
			public void mouseClicked (MouseEvent event) {
				if (point != board.getNowPiece()) return;
				if (board.takePoint(out,event.getX(),event.getY())) netWin();
			}
		};
		board.addMouseListener(player);
	}
	
	public void closeSocket () {
		try {
			socket.close();
			if (server != null) server.close();
		} catch (IOException e) {
			display.setText("无法关闭连接");
			e.printStackTrace();
		}
	}
}