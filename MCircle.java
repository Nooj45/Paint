// CS 0401 Fall 2016
// MCircle subclass of Mosaic

import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

// Note that there are no new instance variables and almost all necessary
// methods are inherited from Mosaic.  We do have to define the constructor
// and the saveFile method however.
public class MCircle extends Mosaic
{
	public MCircle()
	{
		sh = new Ellipse2D.Double(0,0,0,0);
	}
	
	// Constructor takes a diameter, an x,y coordinate for the center of the
	// circle and a color.
	public MCircle(double dia, double x, double y, Color c)
	{
		super();
		// See Ellipse2D.Double in the Java API.  This object for the
		// RectangularShape in the Mosaic is the only significant difference
		// between the MCircle and MSquare classes.
		sh = new Ellipse2D.Double(x-dia/2, y-dia/2, dia, dia);
		col = c;
		highlighted = false;
	}
	
	// saveFile() method returns a string on one line formatted properly to
	// include the information necessary to restore this image.  Note the
	// format of the String -- you will need to know this to restore a picture
	// from a file.  Note also that the color is save in rgb format.  See the
	// Color class for more information.
	public String saveFile()
	{
		StringBuilder b = new StringBuilder();
		b.append("Circle" + ",");
		b.append(sh.getWidth() + ",");
		b.append(sh.getCenterX() + "," + sh.getCenterY() + ",");
		b.append(col.getRed() + ":" + col.getGreen() + ":" + col.getBlue());
		return b.toString();
	}
}