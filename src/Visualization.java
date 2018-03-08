import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Segment;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

@SuppressWarnings("serial")
public class Visualization extends JFrame {

	Background background;
	ImageIcon field = new ImageIcon(Visualization.class.getResource("field.png"));
	static final Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.025, 5, 10, 100);
	static final double scaleFeetToPixels = 30.7; //approximate pixels per foot
	static final double wheelbaseLength = 2.75;
	static final double wheelbaseWidth = 1.6;
	double initialX, initialY;
	Font font = new Font("Comic Sans", Font.BOLD, 30);
	
	public static void main(String[] args) {
		Waypoint[] centerLeftSwitch = new Waypoint[] {
				new Waypoint(0, 0, 0),
				new Waypoint(9, -6, Pathfinder.d2r(0)),

		};

		Waypoint[] centerRightSwitch = new Waypoint[] {
				new Waypoint(0, 0, 0),
				new Waypoint(8, 4, Pathfinder.d2r(0)),
		};

		Waypoint[] forward = new Waypoint[] {
				new Waypoint(0, 0, 0),
				new Waypoint(20, 0, Pathfinder.d2r(0))
		};
		
		Waypoint[] rightRightSwitch = new Waypoint[] {
				new Waypoint(0, 0, 0),
				new Waypoint(15, 0, 0),
				new Waypoint(23, -2, Pathfinder.d2r(-10)),
				new Waypoint(20, 3, Pathfinder.d2r(-90)),
				new Waypoint(17, -2.5, Pathfinder.d2r(180)),
				new Waypoint(20, -8, Pathfinder.d2r(-90)),
				new Waypoint(17, -5.5, Pathfinder.d2r(170)),
		};
		
		Waypoint[] rightRightScaleApproach = new Waypoint[] {
				new Waypoint(0, 0, 0),
				new Waypoint(15, 0, 0),
				new Waypoint(23, -2.5, Pathfinder.d2r(-25)),
		};
		
		//center y starting position = 15
		//right y starting position = 23
		
		try {
			new Visualization(rightRightScaleApproach, wheelbaseLength/2, 23, false);
			//new Visualization(waypoints2, wheelbaseLength/2, 15);
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public Visualization(Waypoint[] waypoints, double initialX, double initialY, boolean isReversed) throws Exception {
		setSize(field.getIconWidth(), field.getIconHeight());
		setVisible(true);
		setTitle("Pathfinder Visualizer - Bradley Boxer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		background = new Background(field.getIconWidth(), field.getIconHeight());
		add(background);
		addMouseListener(new Mouse());

		this.initialX = initialX;
		this.initialY = initialY;
		drawTrajectory(Pathfinder.generate(waypoints, config), isReversed);
		drawWaypoints(waypoints, isReversed);
		
		revalidate();
	}

	public void drawWaypoints(Waypoint[] waypoints, boolean isReversed) {
		for(int i=0;i<waypoints.length;i++) {
			Waypoint waypoint = waypoints[i]; 
			background.drawString((int)(scaleFeetToPixels*(waypoint.x+initialX)), (int)(scaleFeetToPixels*(waypoint.y+initialY)), ""+i);	
		}
	}
	
	public void drawTrajectory(Trajectory trajectory, boolean isReversed) {
		TankModifier tankModifier = new TankModifier(trajectory);
		tankModifier.modify(wheelbaseWidth);
		Trajectory left = tankModifier.getLeftTrajectory();
		Trajectory right = tankModifier.getRightTrajectory();
		
		for(int i=0;i<left.segments.length;i++) { //draw trajectory
			Segment leftSegment = left.segments[i];
			Segment rightSegment = right.segments[i];
			background.drawPoint((int)(scaleFeetToPixels*(leftSegment.x+initialX)), (int)(scaleFeetToPixels*(leftSegment.y+initialY)));
			background.drawPoint((int)(scaleFeetToPixels*(rightSegment.x+initialX)), (int)(scaleFeetToPixels*(rightSegment.y+initialY)));	
		}
	}
	
	private class Mouse implements MouseInputListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			System.out.println(Math.abs(initialX-e.getX()/scaleFeetToPixels)+" "+Math.abs(initialY-e.getY()/scaleFeetToPixels));
		}
		@Override public void mouseEntered(MouseEvent arg0) {}
		@Override public void mouseExited(MouseEvent arg0) {}
		@Override public void mousePressed(MouseEvent arg0) {}
		@Override public void mouseReleased(MouseEvent arg0) {}
		@Override public void mouseDragged(MouseEvent arg0) {}
		@Override public void mouseMoved(MouseEvent arg0) {}
	}

	private class Background extends JPanel {
		BufferedImage image;
		public Background(int width, int height) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			image = Visualization.toBufferedImage(field);
		}

		public void drawString(int x, int y, String str) {
			Graphics graphics = image.getGraphics();
			graphics.setColor(Color.PINK);
			graphics.setFont(font);
			graphics.drawString(str, x, y);
		}
		
		public void drawPoint(int x, int y) {
			Graphics graphics = image.getGraphics();
			graphics.setColor(Color.GREEN);
			graphics.fillRect(x+1, y+1, 2, 2);				
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.drawImage(image, 0, 0, this);
		}
	}

	public static BufferedImage toBufferedImage(ImageIcon icon) {
		BufferedImage bi = new BufferedImage(
				icon.getIconWidth(),
				icon.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		icon.paintIcon(null, g, 0,0);
		g.dispose();
		return bi;
	}
}
