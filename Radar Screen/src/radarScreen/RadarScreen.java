/**
 * 
 */
package radarScreen;

import java.awt.Color;
import java.awt.geom.Arc2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Timer;
import java.util.TimerTask;

import static java.awt.RenderingHints.KEY_ANTIALIASING ;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON ;
import static java.awt.RenderingHints.KEY_COLOR_RENDERING ;
import static java.awt.RenderingHints.VALUE_COLOR_RENDER_SPEED;
import static java.awt.RenderingHints.KEY_INTERPOLATION ;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR ;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * @author Ian Shef
 *
 */
public class RadarScreen extends JFrame {

    private static final int GREENER_LIMIT = 250 ;
    private static final int SMOOTHNESS = 3 * 360  ;
    private static final int MS_INITIAL_DELAY = 0 ;
    private static final int MS_REPEAT_DELAY = 40 ;
    private static final double SCAN_ADVANCE = 2.0 ;
    private static final double ARCEXTENT = 360.0/SMOOTHNESS ;
    
    private static Color FOREGROUND_COLOR_START = new Color( 128, 255, 128) ;

    int centerX ;
    int centerY ;
    int width ;
    int height ;
    double previousArcStart = 0.0 ;
    TimerTask timerTask = new TimerTask() {
	@Override
	public void run() {
	    repaint() ;
	}
    } ;
    Timer timer = new Timer() ;
    Arc2D.Double a2d = new Arc2D.Double(
	    centerX - width/2, /* Bounding rectangle upper left corner x */
	    centerY - height/2,/* Bounding rectangle upper left corner y */
	    width, height, 
	    previousArcStart, ARCEXTENT, 
	    Arc2D.PIE) ;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;  // JFrame is Serializable

    /**
     * Construct a radar screen for scanning.
     * 
     * @param string The title to display on the window's frame.
     */
    public RadarScreen(String string) {
	super(string) ;
    }

    /**
     * Start the scan.
     */
    public void scan() {
	timer.scheduleAtFixedRate(
		timerTask, 
		MS_INITIAL_DELAY, 
		MS_REPEAT_DELAY) ;
    }

    /**
     * @param centerX the x coordinate of the center of the scan in pixels.
     */
    int setCenterX(int centerX) {
	this.centerX = centerX ;
	a2d = new Arc2D.Double(
		centerX - width/2, /* Bounding rectangle upper left corner x */
		centerY - height/2,/* Bounding rectangle upper left corner y */
		width, height, 
		previousArcStart, ARCEXTENT, 
		Arc2D.PIE) ;
	return centerX ;
    }

    /**
     * @param centerY the y coordinate of the center of the scan in pixels.
     */
    int setCenterY(int centerY) {
	this.centerY = centerY ;
	a2d = new Arc2D.Double(
		centerX - width/2, /* Bounding rectangle upper left corner x */
		centerY - height/2,/* Bounding rectangle upper left corner y */
		width, height, 
		previousArcStart, ARCEXTENT, 
		Arc2D.PIE) ;
	return centerY ;
    }

    /**
     * @param width the width of the displayed scan in pixels.
     */
    int setWidth(int width) {
	this.width = width ;
	a2d = new Arc2D.Double(
		centerX - width/2, /* Bounding rectangle upper left corner x */
		centerY - height/2,/* Bounding rectangle upper left corner y */
		width, height, 
		previousArcStart, ARCEXTENT, 
		Arc2D.PIE) ;
	return width ;
    }

    /**
     * @param height the height of the displayed scan in pixels.
     */
    int setHeight(int height) {
	this.height = height ;
	a2d = new Arc2D.Double(
		centerX - width/2, /* Bounding rectangle upper left corner x */
		centerY - height/2,/* Bounding rectangle upper left corner y */
		width, height, 
		previousArcStart, ARCEXTENT, 
		Arc2D.PIE) ;
	return height ;
    }

    @Override
    public void paint(Graphics g) {
	if (g instanceof Graphics2D) {
	    paintHelper((Graphics2D)g) ;
	} else {
	    System.out.println(
		    "In paint, found a Graphics " + 
		    "that is not a Graphics2D.") ;
	    System.exit(-1);
	}
    }

    /**
     * @param g
     */
    private void paintHelper(Graphics2D g) {
	setRenderingHints(g);
	previousArcStart -= SCAN_ADVANCE ;
	double arcStart = previousArcStart ;
	a2d.setAngleStart(previousArcStart) ;
	a2d.setAngleExtent(ARCEXTENT) ;
	Color c = FOREGROUND_COLOR_START ;
	for (int i = 0 ; i < GREENER_LIMIT ; i++) {
	    g.setPaint(c) ;
	    g.fill(a2d) ;
	    c=greener(c) ;
	    arcStart += ARCEXTENT ;
	    a2d.setAngleStart(arcStart) ;
	}
	fillArc(g, arcStart+ARCEXTENT, 360.0-(GREENER_LIMIT*ARCEXTENT));
    }

    /**
     * @param g The Graphics2D object to use for painting the arc, particularly 
     *          the color.
     * @param start The starting angle in degrees, where the x-axis is 
     *              zero degrees and positive degrees proceed counterclockwise.
     * @param extent The amount of the circle to fill, in degrees.
     */
    private void fillArc(Graphics2D g, double start, double extent) {
	a2d.setAngleStart(start) ;
	a2d.setAngleExtent(extent) ;
	g.fill(a2d) ;
    }

    /**
     * @param g
     */
    private void setRenderingHints(Graphics2D g) {
	g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON) ;
	g.setRenderingHint(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_SPEED) ;
	g.setRenderingHint(KEY_INTERPOLATION, 
		VALUE_INTERPOLATION_NEAREST_NEIGHBOR) ;
    }

    public static Color greener(Color c) {
	int origRed   = c.getRed() ;
	int origGreen = c.getGreen() ;
	int origBlue  = c.getBlue() ;
	return new Color(
		(origRed>0)?(origRed-1):origRed, 
		((origRed ==0)&&
		 (origBlue==0)&&
		 (origGreen>0))?origGreen-1:origGreen,
		(origBlue>0)?(origBlue-1):origBlue,
		c.getAlpha()) ;
    }

    /**
     * 
     */
    static void constructAndRun() {
	RadarScreen rs = new RadarScreen("Radar Screen");
	rs.setMaximizedBounds(null); // Maximized per the system.
	rs.setBackground(Color.BLACK);
	rs.setDefaultCloseOperation(EXIT_ON_CLOSE);
	rs.setExtendedState(MAXIMIZED_BOTH);
	rs.setVisible(true);
	Rectangle rsBounds = rs.getBounds();
	Insets rsInsets = rs.getInsets();
	//
	// v1 is width not counting relevant insets.
	//
	int v1 = rsBounds.width - rsInsets.left - rsInsets.right;
	//
	// v2 is height not counting relevant insets.
	//
	int v2 = rsBounds.height - rsInsets.top - rsInsets.bottom;
	rs.setHeight(rs.setWidth(Math.min(v1, v2)));
	rs.setCenterX(rsInsets.left + v1 / 2);
	rs.setCenterY(rsInsets.top + v2 / 2);
	rs.scan();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		constructAndRun() ;
	    }
	}) ;
    }
}
