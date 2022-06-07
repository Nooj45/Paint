// CS 0401 Fall 2016
// MSquare subclass of Mosaic.

// The only difference between this class and MCircle is the underlying
// RectangularShape and the text shown in the saveFile() method.  See
// MCircle for more comments.

import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public class MSquare extends Mosaic
{
	public MSquare()
	{
		sh = new Rectangle2D.Double(0,0,0,0);
	}
	
	public MSquare(double dia, double x, double y, Color c)
	{
		super();
		sh = new Rectangle2D.Double(x-dia/2, y-dia/2, dia, dia);
		col = c;
		highlighted = false;
	}
	
	public String saveFile()
	{
		StringBuilder b = new StringBuilder();
		b.append("Square" + ",");
		b.append(sh.getWidth() + ",");
		b.append(sh.getCenterX() + "," + sh.getCenterY() + ",");
		b.append(col.getRed() + ":" + col.getGreen() + ":" + col.getBlue());
		return b.toString();
	}
}