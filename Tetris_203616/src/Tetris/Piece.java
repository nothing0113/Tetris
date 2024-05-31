package Tetris;

import java.awt.Point;

public abstract class Piece {
	final int DOWN = 0;
	final int LEFT = 1;
	final int RIGHT = 2;
	protected int r[];
	protected int c[];
	protected TetrisData data; // 테트리스 내부 데이터
	protected Point center; //조각 중심 좌표
	
	public Piece(TetrisData data) {
		r = new int[4];
		c = new int[4];
		this.data = data;
		center = new Point(5,0);
	}
	
	public abstract int getType();
	public abstract int roteType();
	
	public int getX() { return center.x;}
	public int getY() { return center.y;}
	
	public boolean copy() {
		boolean value = false;
		int x = getX();
		int y = getY();
		if(getMinY() + y <= 0) {
			value = true;
		}
		
		for(int i = 0; i < 4; i++) {
			data.setAt(y + r[i], x + c[i], getType());
		}
		return value;
	}
	
	public boolean isOverlap(int dir) {
		int x = getX();
		int y = getY();
		switch(dir) {
		case 0 : // 아래
			for(int i = 0; i < r.length; i++) {
				if(data.getAt(y + r[i] + 1, x + c[i]) != 0) {
					return true;
				}
			}
			break;
		case 1 : // 왼쪽
			for(int i = 0; i < r.length; i++) {
				if(data.getAt(y + r[i] , x + c[i] - 1) != 0) {
					return true;
				}
			}
			break;
		case 2 : // 오른쪽
			for(int i = 0; i < r.length; i++) {
				if(data.getAt(y + r[i], x + c[i] + 1) != 0) {
					return true;
				}
			}
			break;
		}
		return false;
	}
	
	public int getMinX() {
		int min = c[0];
		for(int i = 1; i < c.length; i++) {
			if(c[i] < min) {
				min = c[i];
			}
		}
		return min;
	}
	
	public int getMaxX() {
		int max = c[0];
		for(int i = 1; i < c.length; i++) {
			if(c[i] > max) {
				max = c[i];
			}
		}
		return max;
	}
	
	public int getMinY() {
		int min = r[0];
		for(int i = 1; i < r.length; i++) {
			if(r[i] < min) {
				min = r[i];
			}
		}
		return min;
	}
	
	public int getMaxY() {
		int max = r[0];
		for(int i = 1; i < r.length; i++) {
			if(r[i] > max) {
				max = r[i];
			}
		}
		return max;
	}
	
	public boolean moveDown() { //아래로 이동
		if(center.y + getMaxY() + 1 < TetrisData.ROW) {
			if(isOverlap(DOWN) != true) {
				center.y++;
			}else {
				return true;
			}
		} else { return true;}
		
		return false;
	}
	
	
	public void moveLeft() { //왼쪽으로 이동
		if(center.x + getMinX() - 1 >= 0)
			if(isOverlap(LEFT) != true) {center.x--;}
		else return;
	}
	
	public void moveRight() { // 오른쪽으로 이동
		if(center.x + getMaxX() + 1 < TetrisData.COL)
			if(isOverlap(RIGHT) != true) {center.x++;}
		else return;
	}
	
	 public void rotate() {
	        int rc = roteType();
	        if (rc <= 1) return;

	        //현재 좌표 저장
	        int[] currentR = r.clone();
	        int[] currentC = c.clone();

	        //회전 
	        if (rc == 2) {
	            rotate4();
	            rotate4();
	            rotate4();
	        } else {
	            rotate4();
	        }

	        //회전된 도형이 범위를 벗어나는지 확인
	        int minX = getMinX();
	        int maxX = getMaxX();
	        int maxY = getMaxY();

	        if (center.x + minX < 0 || center.x + maxX >= TetrisData.COL || center.y + maxY >= TetrisData.ROW) {
	            //회전된 도형이 범위를 나감
	            r = currentR;
	            c = currentC;
	            return;
	        }

	        // 회전도형이 다른 블록과 겹치는지 확인
	        if (isOverlapWithBlocks()) {
	            //회전도형이 다른 블록과 겹치면 회전을 되돌리기
	            r = currentR;
	            c = currentC;
	        }
	    }

	    private boolean isOverlapWithBlocks() {
	        int x = center.x;
	        int y = center.y;
	        
	        for (int i = 0; i < r.length; i++) {
	            int newX = x + c[i];
	            int newY = y + r[i];

	            if (newX >= 0 && newX < TetrisData.COL && newY >= 0 && newY < TetrisData.ROW) {
	                if (data.getAt(newY, newX) != 0) {
	                    //회전된 도형이 블록과 겹침
	                    return true;
	                }
	            }
	        }
	        
	        return false;
	    }


	    
	    public void rotate4() {
	        for (int i = 0; i < 4; i++) {
	            int temp = c[i];
	            c[i] = -r[i];
	            r[i] = temp;
	        }
	    }
	}