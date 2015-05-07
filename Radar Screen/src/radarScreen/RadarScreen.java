/**
 * 
 */
package radarScreen;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener ;
import java.awt.geom.Arc2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;

import static java.awt.RenderingHints.KEY_ANTIALIASING ;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON ;
import static java.awt.RenderingHints.KEY_COLOR_RENDERING ;
import static java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY ;
import static java.awt.RenderingHints.VALUE_COLOR_RENDER_SPEED;
import static java.awt.RenderingHints.KEY_INTERPOLATION ;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC ;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR ;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * @author Ian Shef
 *
 */
public class RadarScreen extends JFrame {

    private static final int GREENER_LIMIT = 255 ;
    private static final int SMOOTHNESS = 3 * 360  ;
    private static final int MS_INITIAL_DELAY = 1000 ;
    private static final int MS_REPEAT_DELAY = 40 ;
    private static final double SCAN_ADVANCE = 2.0 ;
    
    private static Color FOREGROUND_COLOR_START = new Color( 128, 255, 128) ;

    int centerX ;
    int centerY ;
    int width ;
    int height ;
    double previousArcStart = 0.0 ;
    double arcExtent = 360.0/SMOOTHNESS ;
    boolean firstTime = true ;
    Timer timer ;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;  // JFrame is Serializable

    public RadarScreen(String string) {
	super(string) ;
	timer = new Timer(MS_INITIAL_DELAY, new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		repaint() ;
	    }
	}) ;
	timer.setDelay(MS_REPEAT_DELAY);
    }

    public void scan() {
	timer.start();
    }


    /**
     * @param centerX the centerX to set
     */
    void setCenterX(int centerX) {
	this.centerX = centerX;
    }

    /**
     * @param centerY the centerY to set
     */
    void setCenterY(int centerY) {
	this.centerY = centerY;
    }

    /**
     * @param width the width to set
     */
    void setWidth(int width) {
	this.width = width;
    }

    /**
     * @param height the height to set
     */
    void setHeight(int height) {
	this.height = height;
    }

    @Override
    public void paint(Graphics g) {
	if (firstTime) {
	    renderFirstTime(g) ;
	    firstTime = false ;
	} else {
	    render(g);
	}
    }

    /**
     * @param g
     */
    private void render(Graphics g) {
	assert g instanceof Graphics2D : 
	    "In render, found a Graphics that is not a Graphics2D." ;
        Graphics2D g2 = (Graphics2D) g ;
        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON) ;
        g2.setRenderingHint(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_SPEED) ;
        g2.setRenderingHint(KEY_INTERPOLATION, 
        	VALUE_INTERPOLATION_NEAREST_NEIGHBOR) ;
        double arcStart = previousArcStart -  SCAN_ADVANCE ;
        previousArcStart = arcStart ;
        double arcToBeClearedExtent = -1.0 ;
        double arcToBeClearedStart = arcStart ;
        int greener = 0 ;
        Color c = FOREGROUND_COLOR_START ;
        Arc2D.Double a2d = new Arc2D.Double(centerX, centerY, 
    	    width, height, 
    	    arcStart, arcExtent, 
    	    Arc2D.PIE) ;
        Shape s = a2d ;
        while (greener < GREENER_LIMIT) {
        	g2.setPaint(c) ;
        	g2.fill(s) ;
//        	msgStartAndExtent("nth: ", a2d) ;
        	arcToBeClearedExtent -= a2d.getAngleExtent() ;
        	c=greener(c) ;
        	greener++ ;
        	arcStart += arcExtent ;
        	a2d.setAngleStart(arcStart) ;
        }
        a2d.setAngleExtent(arcToBeClearedExtent) ;
        arcStart += arcExtent ;
        a2d.setAngleStart(arcToBeClearedStart) ;
        g2.fill(s) ;
//        msgStartAndExtent("clr: ", a2d) ;
    }

    /**
     * @param g
     */
    private void renderFirstTime(Graphics g) {
	assert g instanceof Graphics2D : 
	    "In renderFirstTime, found a Graphics that is not a Graphics2D." ;
	Graphics2D g2 = (Graphics2D) g ;
	g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON) ;
	g2.setRenderingHint(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY) ;
	g2.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC) ;
	Color c = FOREGROUND_COLOR_START ;
	for (int i=0; i<GREENER_LIMIT; i++) {
	    c = greener(c) ;
	}
	Arc2D.Double a2d = new Arc2D.Double(centerX, centerY, 
		width, height, 
		0.0, 360.0, 
		Arc2D.PIE) ;
	Shape s = a2d ;
	g2.setPaint(c) ;
	g2.fill(s) ;
//	msgStartAndExtent("1st: ", a2d) ;
    }

    /**
     * @param a2d
     */
    @SuppressWarnings("unused")
    private void msgStartAndExtent(String prefix, Arc2D.Double a2d) {
	System.out.print(prefix) ;
	System.out.println("start: " + a2d.getAngleStart() 
		+ "  extent: " + a2d.getAngleExtent()) ;
    }
    
    public static Color greener(Color c) {
	int origAlpha = c.getAlpha() ;
	int origRed   = c.getRed() ;
	int origGreen = c.getGreen() ;
	int origBlue  = c.getBlue() ;
	int newRed    = origRed ;
	int newBlue   = origBlue ;
	int newGreen  = origGreen ;
	if (origRed >0) newRed  = origRed  - 1 ;
	if (origBlue>0) newBlue = origBlue - 1 ;
	if (       (origRed  == 0)
		&& (origBlue == 0)
		&& (origGreen > 0) ) {
	    newGreen = origGreen - 1 ;
	}
	return new Color(newRed, newGreen, newBlue, origAlpha) ;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		RadarScreen rs = new RadarScreen("Radar Screen") ;
		rs.setMaximizedBounds(null) ; // Maximized per the system.
		rs.setBackground(Color.BLACK) ;
		rs.setDefaultCloseOperation(EXIT_ON_CLOSE) ;
		rs.setExtendedState(MAXIMIZED_BOTH) ;
		rs.setVisible(true) ;
		Rectangle rsBounds = rs.getBounds() ;
		Insets    rsInsets = rs.getInsets() ;
		int v1 = rsBounds.width-rsInsets.left-rsInsets.right ;
		int v2 = rsBounds.height-rsInsets.top-rsInsets.bottom ;
		int v3 = 0 ;
		int v4 = 0 ;
		int v5 = 0 ;
		if (v1 == v2) {
		    v3 = v1 ;
		} else if (v1 < v2) {
		    v3 = v1 ;
		    v5 = (v2-v1)/2 ;
		} else /* v2 < v1 */ {
		    v3 = v2 ;
		    v4 = (v1-v2)/2 ;
		}
		rs.setWidth(v3) ;
		rs.setHeight(v3) ;
		rs.setCenterX(rsInsets.left + v4) ;
		rs.setCenterY(rsInsets.top + v5) ;
		rs.scan() ;
	    }
	});
    }

}
