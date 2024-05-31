package Tetris;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class TetrisCanvas extends JPanel implements Runnable, KeyListener {
		protected Thread worker;
		protected Color colors[];
		protected int w = 25;
		protected TetrisData data;
		protected int margin = 20;
		protected boolean stop, makeNew;
		protected int interval = 1500;
		protected int level = 2;
		protected Piece current;
		protected Piece temp; //추가
	    protected Piece nextPiece;  //추가
	    protected JPanel nextPane;  // 추가
	    protected JPanel savePane;  // 추가
	    protected Piece savedPiece; //추가
	    protected boolean canSwap = true; //추가
		public TetrisCanvas() {
			data = new TetrisData();
			
			addKeyListener(this);
			colors = new Color[8]; //테트리스 배경 및 조각 색
			colors[0] = new Color(80,80,80); // 배경색(검은색)
			colors[1] = new Color(255,0,0); // 빨간색
			colors[2] = new Color(0,255,0); // 녹색
			colors[3] = new Color(0,200,255); // 노란색
			colors[4] = new Color(255,255,0); // 하늘색
			colors[5] = new Color(255,150,0); // 황토색
			colors[6] = new Color(210,0,240); // 보라색
			colors[7] = new Color(40,0,240); // 파란색
	}
		
		public void start() { //게임시작
			data.clear();
			worker = new Thread(this);
			worker.start();
			makeNew = true;
			stop = false;
			requestFocus();
			repaint();
			current = randomPiece();
		}
		
		
		public void stop() {
			stop = true;
			current = null;
		}
		
		public void paint(Graphics g) {
			super.paint(g);
			
			for(int i = 0; i < TetrisData.ROW; i++) { // 쌓인 조각들 그리기
				for(int k = 0; k < TetrisData.COL; k++) {
					if(data.getAt(i, k) == 0) {
						g.setColor(colors[data.getAt(i, k)]);
						g.draw3DRect(margin/2 + w * k, margin/2 + w * i, w, w, true);
					}else {
						g.setColor(colors[data.getAt(i, k)]);
						g.fill3DRect(margin/2 + w * k, margin/2 + w * i, w, w, true);
					}
				}
			}
			
			if(current != null) {// 현재 내려오고 있는 테트리스 조각 그리기
				for(int i = 0; i < 4; i++) {
					g.setColor(colors[current.getType()]);
					g.fill3DRect(margin/2 + w * (current.getX()+current.c[i]), 
								margin/2 + w * (current.getY()+current.r[i]), 
								w, w, true);
				}
			}
			
	      if (nextPiece != null) { // 다음 블럭 보여줌
	            int nextPieceX = margin /４ + w * (12+ nextPiece.getMinX());
	            int nextPieceY = margin / 2 + w * (0+ nextPiece.getMaxY()+1);
	            g.setColor(Color.BLACK);
	            g.drawString("Next Piece", nextPieceX, nextPieceY -w/2 );
		         for (int i = 0; i < 4; i++) {
		            g.setColor(colors[nextPiece.getType()]);
		            g.fill3DRect(margin / 2 + w * (TetrisData.COL + 2 + nextPiece.c[i]),
		                  margin / 2 + w * (2 + nextPiece.r[i]), w, w, true);
		         }
		      }
	      
	      
          if (savedPiece != null) {  //추가
              int savedPieceX = margin / 2 + w * (12+ savedPiece.getMinX());
               int savedPieceY = margin / 2 + w * (6 + savedPiece.getMaxY() + 1);
               g.setColor(Color.BLACK);
               g.drawString("Saved Piece", savedPieceX, savedPieceY - w / 2);
            for (int i = 0; i < 4; i++) {
                g.setColor(colors[savedPiece.getType()]);
                g.fill3DRect(
                    margin / 2 + w * (12 + savedPiece.c[i]),
                    margin / 2 + w * (8 + savedPiece.r[i]),
                    w, w, true);
            }
            if (!canSwap) {
                g.setColor(Color.RED); // 교환이 불가능할 때 빨간색으로 표시
                g.drawString("교환불가", savedPieceX+w+60, savedPieceY - w / 2);
            }
            
           }


		   }
		
		
		public Dimension getPreferredSize() { // 테트리스 판의 크기 지정
			int tw = w * TetrisData.COL + margin;
			int th = w * TetrisData.ROW + margin;
			return new Dimension(tw, th);
		}
		
		
		public void run() {
			while(!stop) {
				try {
					if(makeNew) { // 새로운 테트리스 조각 만들기
						nextPiece = randomPiece();
						current = temp;
						temp = nextPiece;
						makeNew = false;
					} else {// 현재 만들어진 테트리스 조각 아래로 이동
						if(current.moveDown()) {
							makeNew = true;
							if(current.copy()) {
								stop();
								int score = data.getLine() * 175 * level;
								JOptionPane.showMessageDialog(this, "게임끝\n점수 : " + score);
							}
							current = null;
						}
						data.removeLines();
					}
					repaint();
					Thread.sleep(interval/level);
				} catch(Exception e) {}
			}
		}
		
		// 키보드를 이용해서 테트리스 조각 제어
		public void keyPressed(KeyEvent e) {
			if(current == null) return;
			
			switch(e.getKeyCode()) {
			case 37: // 왼쪽 화살표
				current.moveLeft();
				repaint();
				break;
			case 39: // 오른쪽 화살표
				current.moveRight();
				repaint();
				break;
			case 38: // 윗쪽 화살표
				current.rotate();
				repaint();
				break;
			case 40: // 아래쪽 화살표
				boolean temp = current.moveDown();
				if(temp) {
					makeNew = true;
					if(current.copy()) {
						stop();
						int score = data.getLine() * 175 * level;
						JOptionPane.showMessageDialog(this, "게임끝\n점수 : " + score);
					}
				}
				data.removeLines();
				repaint();
				break;
			case 32: //스페이스 바
				while(!current.moveDown()) {
				}
				makeNew = true;
				if(current.copy()) {
					stop();
					int score = data.getLine() * 175 * level;
					JOptionPane.showMessageDialog(this, "게임끝\n점수 : " + score);
				}
				data.removeLines();
				repaint();
				break;
			  case 67: // c 키  추가
			         if(canSwap) {
			         savePiece();
			         canSwap = false;
			         }
			         break;
			}
		}
		public Piece randomPiece() { // 새로운 테트리스 조각 만들기
			Piece pic;
			int random = (int)
			(Math.random() * Integer.MAX_VALUE) % 7;
			
			switch(random) {
			case 0:
				pic = new Bar(data);
				break;
			case 1:
				pic = new Tee(data);
				break;
			case 2:
				pic = new EI(data);
				break;
			case 3:
				pic = new Jee(data);
				break;
			case 4:
				pic = new Oee(data);
				break;
			case 5:
				pic = new See(data);
				break;
			case 6:
				pic = new Zee(data);
				break;
		    default:
		        pic = null;
		          break;
			}
			return pic;
		}
		
		
		   protected void savePiece() {
		       if (savedPiece == null) {
		           savedPiece = current;
		           current = nextPiece;
		           nextPiece =randomPiece();
		           makeNew = true;
		       } else if (canSwap) {
		           Piece tempPiece = savedPiece;
		           savedPiece = current;
		           current = tempPiece;
		           canSwap = false;
		       }
		       savedPiece.center = new Point(4,0);
		       repaint();
		   }

		public void keyReleased(KeyEvent e) { }
		public void keyTyped(KeyEvent e) { }
		
}
