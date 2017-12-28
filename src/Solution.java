import java.util.ArrayList;
import java.util.Collections;

import javafx.scene.paint.Color;

public class Solution {
	private int layers = 0;
	private ArrayList<Coord> allCoords;
	private ArrayList<Coord> ConvexHull;
	private ArrayList<Polygon> polygonsInvolved;
	private Coord startCoord, finishCoord;
	private GUI gui;
	
	Solution() {
		System.out.println("Created a solution");
		ConvexHull = new ArrayList<Coord>();
	}
	
	Solution(Coord S, Coord F, ArrayList<Polygon> P, GUI gui) {
		this();
		this.gui = gui;
		this.startCoord = S;
		this.finishCoord = F;
		this.polygonsInvolved = P;
		createCoordList();
		QuickHull(allCoords);
	}
	
	public void createCoordList() {
		allCoords = new ArrayList<Coord>();
		
		/* Add start and finish coord to list of those considerd */
		allCoords.add(startCoord);
		allCoords.add(finishCoord);
		
		/*Add all points from all involved polygons */
		for (int i = 0; i < polygonsInvolved.size(); i++) {
			for (int j = 0; j < polygonsInvolved.get(i).getNumCoords(); j++) {
				allCoords.add(polygonsInvolved.get(i).getCoord(j));
			}
			System.out.println("Finished adding polygon " + (i+1));
		}
	}
	
	public ArrayList<Coord> getHull() {
		return ConvexHull;
	}
	
	public void drawSolution() {
		for(int i = 0; i < ConvexHull.size() - 1; i++) {
			gui.drawLine(ConvexHull.get(i).getX(), ConvexHull.get(i).getY(), ConvexHull.get(i + 1).getX(), ConvexHull.get(i + 1).getY(), Color.BURLYWOOD);
		}
		gui.drawLine(ConvexHull.get(ConvexHull.size() - 1).getX(), ConvexHull.get(ConvexHull.size() - 1).getY(), ConvexHull.get(0).getX(), ConvexHull.get(0).getY(), Color.BURLYWOOD);
		for (int i = 0; i < ConvexHull.size(); i++) {
			gui.drawDot(ConvexHull.get(i).getX(), ConvexHull.get(i).getY(), Color.RED);
		}

	}
	
	public void QuickHull(ArrayList<Coord> S) {
		/* Find extreme points */
		Coord leftMost = S.get(0); // Set some initial values
		Coord rightMost = S.get(0);// ""
		// Iterate through to find extremes
		for(int i = 1; i < S.size(); i++) {
			if (S.get(i).getX() < leftMost.getX()) {
				leftMost = S.get(i);
			}
			else if (S.get(i).getX() > rightMost.getX()) {
				rightMost = S.get(i);
			}
		}
		// Add extremes to solution, remove from S
		ConvexHull.add(leftMost);
		ConvexHull.add(rightMost);

		S.remove(leftMost);
		S.remove(rightMost);
		
		// Segment leftMost-rightMost now divides the set
		ArrayList<Coord> S1 = new ArrayList<Coord>();
		ArrayList<Coord> S2 = new ArrayList<Coord>();
		
		// Iterate through to put in either S1 or S2
		for (int i = 0; i < S.size(); i++) {
			if (S.get(i).isRightOfLine(leftMost, rightMost)) {
				S1.add(S.get(i));
			}
			else if (!S.get(i).isRightOfLine(leftMost, rightMost)) {
				S2.add(S.get(i));
			}
		}
		
		// Start recursion
		FindHull(S1, leftMost, rightMost);
	    FindHull(S2, rightMost, leftMost);
		
	    System.out.println("Finshed after [" + layers + "] FindHull calls.");
	}
	
	public void FindHull(ArrayList<Coord> Sk, Coord P, Coord Q) {
		layers++;
		if (Sk.size() == 0) {
			// done
		}
		else {
			Coord C = Sk.get(0);
			for (int i = 0; i < Sk.size(); i++) {
				if (C.getDistanceToLine(P, Q) < Sk.get(i).getDistanceToLine(P, Q)) {
					C = Sk.get(i);
				}
			}
			
			//For ordering
			
			boolean unAdded = true;
			int count = 0;
			while (unAdded) {
				if (P == ConvexHull.get(count)) {
					ConvexHull.add(count + 1, C);
					unAdded = false;
				}
				count++;
			}
			
			//System.out.println("Adding C " + C.toString() + " to hull");
			//ConvexHull.add(C);
			Sk.remove(C);
			
			ArrayList<Coord> S0 = new ArrayList<Coord>(); // Inside PCQ
			ArrayList<Coord> S1 = new ArrayList<Coord>(); // Right side of PC
			ArrayList<Coord> S2 = new ArrayList<Coord>(); // Right side of CQ
			
			for (int i = 0; i < Sk.size(); i++) {
				if (Sk.get(i).isRightOfLine(P, C)) {
					S1.add(Sk.get(i));
				}
				else if (Sk.get(i).isRightOfLine(C, Q)) {
					S2.add(Sk.get(i));
				}
			}
			
			FindHull(S1, P, C); // P = P, C = Q
   			FindHull(S2, C, Q);	// P = C, Q = Q
			
		}
	}
}

/*
 * Input = a set S of n points 
   Assume that there are at least 2 points in the input set S of points
   QuickHull (S) 
   { 
       // Find convex hull from the set S of n points
       Convex Hull := {} 
       Find left and right most points, say A & B, and add A & B to convex hull 
       Segment AB divides the remaining (n-2) points into 2 groups S1 and S2 
           where S1 are points in S that are on the right side of the oriented line from A to B, 
           and S2 are points in S that are on the right side of the oriented line from B to A 
       FindHull (S1, A, B) 
       FindHull (S2, B, A) 
   }
   FindHull (Sk, P, Q) 
   { 
       // Find points on convex hull from the set Sk of points 
       // that are on the right side of the oriented line from P to Q
       If Sk has no point, then return. 
       From the given set of points in Sk, find farthest point, say C, from segment PQ 
       Add point C to convex hull at the location between P and Q 
       Three points P, Q, and C partition the remaining points of Sk into 3 subsets: S0, S1, and S2 
           where S0 are points inside triangle PCQ, S1 are points on the right side of the oriented 
           line from  P to C, and S2 are points on the right side of the oriented line from C to Q. 
       FindHull(S1, P, C) 
       FindHull(S2, C, Q) 
   }
   Output = Convex Hull
   */
