
public class Coord {
	private double x;
	private double y;
	
	Coord() {
		this.x = 0;
		this.y = 0;
	}
	
	Coord(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
    private double calcCrossProduct(Coord origin, Coord p2) {
        return (p2.x - origin.x) * (this.y - origin.y) - (p2.y - origin.y) * (this.x - origin.x);
    }

    public boolean isRightOfLine(Coord P, Coord Q) {
        return Double.compare(calcCrossProduct(P, Q), 0) < 0;
    }
    
    public double getDistanceToLine(Coord a, Coord b) {
        return Math.abs((b.getX() - a.getX()) * (a.getY() - this.y) - (a.getX() - this.x) * (b.getY() - a.getY()))
                / Math.sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2));
    }
    
    public String toString() {
    	String str = "X: " + this.x + ", Y: " + this.y;
    	return str;
    }
}
