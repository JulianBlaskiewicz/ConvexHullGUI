

import java.util.ArrayList;

public class Polygon {
	private ArrayList<Coord> points;
	
	Polygon() {
		points = new ArrayList<Coord>();
	}
	
	Polygon(ArrayList<Coord> P) {
		this();
		for(int i = 0; i < P.size(); i++) {
			points.add(P.get(i));
		}
	}
	
	public void addCoord(int x, int y) {
		Coord c = new Coord(x, y);
		points.add(c);
	}
	
	public void addCoord(double x, double y) {
		Coord c = new Coord(x, y);
		points.add(c);
	}
	
	public Coord getCoord(int index) {
		return points.get(index);
	}
	
	public int getNumCoords() {
		return points.size();
	}
	
	public ArrayList<Coord> returnPoints() {
		return this.points;
	}
	
	public boolean isBetweenCoords(Coord S, Coord F) {
		double leftMostX, rightMostX;
		leftMostX = points.get(0).getX();
		rightMostX = points.get(0).getX();
		for (int i = 1; i < points.size(); i++) {
			if (points.get(i).getX() < leftMostX) {
				leftMostX = points.get(i).getX();
			}
			if (points.get(i).getX() > rightMostX) {
				rightMostX = points.get(i).getX();
			}
		}
		if (leftMostX > S.getX() && rightMostX < F.getX()) {
			return true;
		}
		else return false;
	}
}
