
import java.util.ArrayList;
import java.util.Random;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

public class GUI extends Application{
	
	private static int size = 900;
	
	private int largePolyVertices = 10000;
	
	private Group root;
	private Canvas canvas;
	private BorderPane bp;
	private Scene scene;
	private GraphicsContext gc;
	
	private ArrayList<Coord> coords;
	private ArrayList<Coord> hull;
	
	private ArrayList<Polygon> polygons = new ArrayList<Polygon>();
	private Polygon polyToAdd;
	private ArrayList<Coord> checkpoints = new ArrayList<Coord>();
	
	private String mode;
	
	public void setUp(Stage stagePrimary) {
		/* JavaFX stuff */
		stagePrimary.setTitle("Convex Hull");
		Group root = new Group();
		Canvas canvas = new Canvas(size, size);
		root.getChildren().add(canvas);
		gc = canvas.getGraphicsContext2D();
		BorderPane bp = new BorderPane();
		gc = canvas.getGraphicsContext2D();
		setMouseEvents(canvas);	
		gc.setFill(Color.BEIGE);
		gc.fillRect(0, 0, size, size);					
		bp.setCenter(root);
		bp.setBottom(setButtons());
		Scene scene = new Scene(bp, size * 1.1, size * 1.1);
		stagePrimary.setScene(scene);
		
		coords = new ArrayList<Coord>();
	}
	
	public HBox setButtons() {
		// Test Button
		Button btnTest = new Button("Clear");
		btnTest.setOnAction(new EventHandler<ActionEvent>() {
			@Override
		    public void handle(ActionEvent event) {
				// Logic Here
		    	System.out.println("Clearing world");
		    	mode = "";
		    	polygons = new ArrayList<Polygon>();
		    	checkpoints = new ArrayList<Coord>();
		    	polyToAdd = new Polygon();
		    	drawWorld();
				}
		});
		
		Button btnPoly = new Button("New Polygon");
		btnPoly.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (mode != "poly") {
					System.out.println("New polygon button pushed");
					mode = "poly";
					polyToAdd = new Polygon();
				}
			}
		});
		
		Button btnS = new Button("Solve");
		btnS.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				final long startTime = System.currentTimeMillis();
				solve();
				final long endTime = System.currentTimeMillis();
				System.out.println("Time to find hull: " + (endTime - startTime) + " ms" );
			}
		});
		
		Button btnC = new Button("New Checkpoint");
		btnC.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (mode != "checkpoint") {
					System.out.println("New checkpoint button pushed");
					mode = "checkpoint";
				}
				else if (mode == "checkpoint") {
					mode = "";
				}
			}
		});
		
		Button btnT = new Button("Large Polygon");
		btnT.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Polygon p = new Polygon();
				Random r = new Random();
				while (p.getNumCoords() <= largePolyVertices) {
					System.out.println("Gen. " + p.getNumCoords() + " vertices");
					int x = r.nextInt(size - 100) + 50;
					int y = r.nextInt(size - 100) + 50;
					p.addCoord(x, y);
				}
				polygons.add(p);
				checkpoints.add(new Coord(25, size/2));
				checkpoints.add(new Coord(size - 25, size/2));
				drawWorld();
			}
		});
		    
		 
		HBox toReturn = new HBox(btnTest, btnPoly, btnC, btnS, btnT);
		toReturn.setSpacing(2);
		return toReturn;
	}
	
	public void drawWorld() {
		gc.setFill(Color.BEIGE);
		gc.fillRect(0, 0, size, size);
		for (int i = 0; i < coords.size(); i++) {
			drawDot(coords.get(i).getX(), coords.get(i).getY(), Color.BLACK);
		}
		for (int i = 0; i < polygons.size(); i++) {
			drawShape(polygons.get(i));
			for (int j = 0; j < polygons.get(i).getNumCoords(); j++) {
				drawDot(polygons.get(i).getCoord(j).getX(), polygons.get(i).getCoord(j).getY(), Color.BLACK);
			}
		}
		for (int i = 0; i < checkpoints.size(); i++) {
			drawDot(checkpoints.get(i).getX(), checkpoints.get(i).getY(), Color.GREEN);
		}
	}
	
	public void drawDot(double x, double y, Color c) {
		int rad = 3;
		gc.setFill(c);
		gc.fillArc(x-rad, y-rad, rad*2, rad*2, 0, 360, ArcType.ROUND);
	}
	
	public void drawLine(double x1, double y1, double x2, double y2, Color c) {
		gc.setStroke(c);
		gc.strokeLine(x1, y1, x2, y2);
	}
	
	public void setMouseEvents (Canvas canvas) {
	       canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
	    	           @Override
	    	           public void handle(MouseEvent e) {
	    	        	   if (e.isPrimaryButtonDown()) {
	    	        		   System.out.println("Primary click at " + e.getX() + ", " + e.getY());
	    	        		   
	    	        		   /* Poly mode, add new point */
	    	        		   if (mode == "poly") {
	    	        			   polyToAdd.addCoord(e.getX(), e.getY());
	    	        			   drawDot(e.getX(), e.getY(), Color.BLACK);
	    	        		   }
	    	        		   
	    	        		   if (mode == "checkpoint") {
	    	        			   Coord C = new Coord(e.getX(), e.getY());
	    	        			   checkpoints.add(C);
	    	        			   drawWorld();
	    	        			   mode = "";
	    	        		   }
	    	        	   }
	    	        	   else if (e.isSecondaryButtonDown()) {
	    	        		   System.out.println("Seconday click at " + e.getX() + ", " + e.getY());
	    	        		   
	    	        		   /* Finished creating poly */
	    	        		   if (mode == "poly") {
	    	        			   mode = "";
	    	        			   drawShape(polyToAdd);
	    	        			   polygons.add(polyToAdd);
	    	        		   }
	    	        	   }
	           }
	    	       });
	}
	
	public void drawShape(Polygon p) {
		ArrayList<Coord> ps = p.returnPoints();
		gc.setStroke(Color.BLACK);
		for(int i = 0; i < ps.size() - 1; i++) {
			gc.strokeLine(ps.get(i).getX(), ps.get(i).getY(), ps.get(i + 1).getX(), ps.get(i + 1).getY());
		}
		gc.strokeLine(ps.get(ps.size() - 1).getX(), ps.get(ps.size() - 1).getY(), ps.get(0).getX(), ps.get(0).getY());
	}
	
	public void solve() {
		if (checkpoints.size() > 1) {
			
			int numberOfHulls = checkpoints.size() - 1;
			ArrayList<Solution> S = new ArrayList<Solution>();
			for (int i = 0; i < numberOfHulls; i++) {
				S.add(new Solution(checkpoints.get(i), checkpoints.get(i + 1), idRelevantPolygons(checkpoints.get(i), checkpoints.get(i+1)), this));
			}
			System.out.println(S.size() + " separate solutions generated.");
			for (int i = 0; i < numberOfHulls; i++) {
				S.get(i).drawSolution();
			}
			for (int i = 0; i < checkpoints.size(); i++) {
				drawDot(checkpoints.get(i).getX(), checkpoints.get(i).getY(), Color.GREEN);
			}
		}
	}
	
	public ArrayList<Polygon> idRelevantPolygons(Coord S, Coord F) {
		ArrayList<Polygon> P = new ArrayList<Polygon>();
		for (int i = 0; i < polygons.size(); i++) {
			if (polygons.get(i).isBetweenCoords(S, F)) {
				P.add(polygons.get(i));
			}
		}
		return P;
	}
	
	public void start(Stage stagePrimary) throws Exception {
		setUp(stagePrimary);
		stagePrimary.show();
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
}

/* Timer code
 * final long startTime = System.currentTimeMillis();
for (int i = 0; i < length; i++) {
  // Do something
}
final long endTime = System.currentTimeMillis();

System.out.println("Total execution time: " + (endTime - startTime) );
*/
