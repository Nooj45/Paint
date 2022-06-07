import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.print.*;
import static java.lang.Math.sqrt;

public class Paint 
{
    public static String software = "Mosaic Art 1.0";
    private Mosaic m;
    private DrawPanel thePanel; 	// DrawPanel is a subclass of JPanel
    // See details below.
    private JPanel buttonPanel;
    private JFrame theWindow;
    private JButton paintIt, eraseIt, editIt; //add edit button

    // ArrayList of Mosaic to store the individual shapes.  Note that
    // since Mosaic is the superclass of both MCircle and MSquare, both
    // shapes can be stored in this ArrayList
    private ArrayList<Mosaic> chunks;
    private double X, Y;
    private double newSize, diffSize;
    private Color newColor, col, altColor;
    private int selected, count = 0;

    private boolean painting, erasing, editing, cir = true, sq = false, fileSaved = true;
    private String currFile;

    private JMenuBar theBar;
    private JMenu fileMenu, defaultMenu, effectsMenu, setShape;
    private JMenuItem endProgram, saveAs, printScene, setColor, setSize, twistShape, twistColor, twistSOff, twistCOff, square, circle, recolor, resize;
    private JMenuItem newFile, openFile, saveFile;
    private JPopupMenu popup;
    
    public Paint() 
    {
        theWindow = new JFrame(software);
        theWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        thePanel = new DrawPanel(600, 600);
        newSize = 15;
        newColor = Color.RED;
        diffSize = newSize;
        col = newColor; //for editing where color needs to be independent of defaults
        altColor = newColor;
        
        selected = -1;
        painting = false;
        erasing = false;
        editing = false;
        paintIt = new JButton("Paint");
        eraseIt = new JButton("Erase");
        editIt = new JButton("Edit");// add edit
        ActionListener bListen = new ButtonListener();
        paintIt.addActionListener(bListen);
        eraseIt.addActionListener(bListen);
        editIt.addActionListener(bListen); //add edit
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));
        buttonPanel.add(paintIt);
        buttonPanel.add(eraseIt);
        buttonPanel.add(editIt);
        theWindow.add(buttonPanel, BorderLayout.SOUTH);
        theWindow.add(thePanel, BorderLayout.NORTH);

        theBar = new JMenuBar();
        theWindow.setJMenuBar(theBar);
        fileMenu = new JMenu("File");
        theBar.add(fileMenu);
        newFile = new JMenuItem("New"); //addings in other file menu items
        openFile = new JMenuItem("Open"); // ^
        saveFile = new JMenuItem("Save"); // ^
        saveAs = new JMenuItem("Save As");
        printScene = new JMenuItem("Print");
        endProgram = new JMenuItem("Exit");
        fileMenu.add(newFile); //adding to File Menu
        fileMenu.add(openFile); // ^
        fileMenu.add(saveFile); // ^
        fileMenu.add(saveAs);
        fileMenu.add(printScene);
        fileMenu.add(endProgram);
        newFile.addActionListener(bListen); //addings ActionListener, so it does something
        openFile.addActionListener(bListen); // ^
        saveFile.addActionListener(bListen); // ^
        saveAs.addActionListener(bListen);
        printScene.addActionListener(bListen);
        endProgram.addActionListener(bListen);

        defaultMenu = new JMenu("Defaults"); // Adding DEFAULTS MENU************
        theBar.add(defaultMenu);
        setColor = new JMenuItem("Set Color");
        setSize = new JMenuItem("Set Size");
        setShape = new JMenu("Set Shape"); //subMenu
        square = new JMenuItem("Square");
        circle = new JMenuItem("Circle");
        defaultMenu.add(setColor);
        defaultMenu.add(setSize);
        defaultMenu.add(setShape);
        setShape.add(square);//adding choices in subMenu
        setShape.add(circle);// ^
        setColor.addActionListener(bListen); //lets user choose default color
        setSize.addActionListener(bListen); //lets user choose size of shape
        square.addActionListener(bListen); //making squares
        circle.addActionListener(bListen); //making circles
        //**********************************************************************
        
        popup = new JPopupMenu();//*********************Popup Menu for right clicking
        recolor = new JMenuItem("Recolor");
        resize = new JMenuItem("Resize");
        popup.add(recolor);
        popup.add(resize);
        recolor.addActionListener(bListen);
        resize.addActionListener(bListen);

        effectsMenu = new JMenu("Effects"); //Adding Effects Menu
        theBar.add(effectsMenu);
        twistShape = new JMenuItem("Start Twisting Shapes");
        twistColor = new JMenuItem("Start Twisting Colors");
        twistSOff = new JMenuItem("Stop Twisting Shapes");
        twistCOff = new JMenuItem("Stop Twisting Colors");
        effectsMenu.add(twistShape);
        effectsMenu.add(twistColor);
        twistShape.addActionListener(bListen);
        twistColor.addActionListener(bListen);
        twistSOff.addActionListener(bListen);
        twistCOff.addActionListener(bListen);
        //add ActionListener and buttons are toggles so update to Stop once clicked and vice versa

        theWindow.pack();
        theWindow.setVisible(true);
    }
    
    private class DrawPanel extends JPanel 
    {
        private int prefwid, prefht;

        // Initialize the DrawPanel by creating a new ArrayList for the images
        // and creating a MouseListener to respond to clicks in the panel.
        public DrawPanel(int wid, int ht) 
        {
            prefwid = wid;
            prefht = ht;

            chunks = new ArrayList<Mosaic>();

            // Add MouseListener to this JPanel to respond to the user
            // pressing the mouse.  In your assignment you will also need a
            // MouseMotionListener to respond to the user dragging the mouse.
            addMouseListener(new MListen());
            addMouseMotionListener(new MListen());
        }

        // This method allows a window that encloses this panel to determine
        // how much space the panel needs.  In particular, when the "pack()"
        // method is called from an outer JFrame, this method is called
        // implicitly and the result determines how much space is needed for
        // the JPanel
        public Dimension getPreferredSize() 
        {
            return new Dimension(prefwid, prefht);
        }

        // This method is responsible for rendering the images within the
        // JPanel.  You should not have to change this code.
        public void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            for (int i = 0; i < chunks.size(); i++)
                chunks.get(i).draw(g2d);
        }

        // Add a new Mosaic and repaint.  The repaint() method call requests
        // that the panel be redrawn.  Make sure that you call repaint()
        // after changes to your scenes so that the changes are actually
        // exhibited in the display.
        public void add(Mosaic m) 
        {
            chunks.add(m);
            repaint();
        }

        // Remove the Mosaic at index i and repaint
        public void remove(int i) 
        {
            if (chunks.size() > i)
                chunks.remove(i);
            repaint();
        }

        // Select a Mosaic that contains the point (x, y).  Note that this
        // is using the contains() method of the Mosaic class, which in turn
        // is checking within the underlying RectangularShape of the object.
        public int select(double x, double y) 
        {
            for (int i = 0; i < chunks.size(); i++) 
            {
                if (chunks.get(i).contains(x, y)) 
                    return i;
            }
            return -1;
        }
    }
    
    // Save the images within the window to a file.  Run this program to see the 
    // format of the saved file.
    public void saveImages() 
    {
        try 
        {
            fileSaved = true;
            PrintWriter P = new PrintWriter(new File(currFile));
            P.println(chunks.size());
            for (int i = 0; i < chunks.size(); i++) 
                P.println(chunks.get(i).saveFile());
            P.close();
        } 
        catch (IOException e) 
        {
            JOptionPane.showMessageDialog(theWindow, "I/O Problem - File not Saved");
        }
    }

    // Listener for some buttons.  Note that the JMenuItems are also listened
    // for here.  Like JButtons, JMenuItems also generate ActionEvents when
    // they are clicked on.  You will need to add more JButtons and JMenuItems
    // to your program and the logic of handling them will also be more
    // complex.  See details in the Assignment 5 specifications.
    private class ButtonListener implements ActionListener //*********************************************************************************************************** FIND THIS SECTION EASIER
    {
        public void actionPerformed(ActionEvent e) 
        {
            if (e.getSource() == paintIt) 
            {
                painting = true;
                paintIt.setForeground(Color.RED);
                erasing = false;
                eraseIt.setForeground(Color.BLACK);
                editing = false;
                editIt.setForeground(Color.BLACK);
            } 
            else if (e.getSource() == eraseIt) 
            {
                painting = false;
                paintIt.setForeground(Color.BLACK);
                erasing = true;
                eraseIt.setForeground(Color.RED);
                editing = false;
                editIt.setForeground(Color.BLACK);
            } 
            //add edit feature
            else if (e.getSource() == editIt)
            {
                painting = false;
                paintIt.setForeground(Color.BLACK);
                erasing = false;
                eraseIt.setForeground(Color.BLACK);
                editing = true;
                editIt.setForeground(Color.RED);
                m.highlight(true);
            }
            else if (e.getSource() == newFile) //adding in File, Open, and Save to program
            {
                if(chunks.size() > 0 && fileSaved == false) //any little changes to the scene, it will prompt the next questions
                {
                    int saveYN = JOptionPane.showConfirmDialog(theWindow,"Save Scene?", "Choose Yes or No", JOptionPane.YES_NO_OPTION);
                    if(currFile == null && saveYN == JOptionPane.YES_OPTION)//saving and no file created
                    {
                        try
                        {
                            currFile = JOptionPane.showInputDialog(theWindow, "Enter new file name", "Input", JOptionPane.INFORMATION_MESSAGE);
                            saveImages();
                        }
                        catch(Exception ex)
                        {
                            //looks like nothing is wrong
                        }
                    }
                    else if(currFile != null && saveYN == JOptionPane.YES_OPTION) //saving but file created
                        saveImages();
                }
                if(fileSaved) //if user saved right before clicking new, then it will show this
                    JOptionPane.showMessageDialog(theWindow,"File was already saved when you clicked New", "Message", JOptionPane.INFORMATION_MESSAGE);
                
                theWindow.dispose(); //gets rid of old window
                new Paint(); //makes the new one
            }
            else if (e.getSource() == openFile)
            {
                if(fileSaved == false)//only entering if user did not save after making changes
                {
                    int saveYN = JOptionPane.showConfirmDialog(theWindow,"Save Scene?", "Choose Yes or No", JOptionPane.YES_NO_OPTION);
                    if(currFile == null && saveYN == JOptionPane.YES_OPTION)//saving and no file created
                    {
                        try
                        {
                            currFile = JOptionPane.showInputDialog(theWindow, "Enter new file name", "Input", JOptionPane.INFORMATION_MESSAGE);
                            saveImages();
                        }
                        catch(Exception ex)
                        {
                            //looks like nothing is wrong
                        }
                    }
                    else if(currFile != null && saveYN == JOptionPane.YES_OPTION) //saving but file created
                        saveImages();
                }
                currFile = JOptionPane.showInputDialog(theWindow, "Enter name of file to find path", "Input", JOptionPane.INFORMATION_MESSAGE);
                
                JFileChooser fileChooser = new JFileChooser(currFile);//cant get file to open in Panel******************************************************
                int returnVal = fileChooser.showOpenDialog(theWindow);
                theWindow.setTitle(software + " - " + currFile);
                if(returnVal == JFileChooser.APPROVE_OPTION)  
                {
                    File file = fileChooser.getSelectedFile();
                    
                    try
                    {
                        FileInputStream fis = new FileInputStream(file);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        DrawPanel temp = (DrawPanel) ois.readObject();
                        fis.close();
                        ois.close();
                        thePanel = temp;
                        thePanel.repaint();
                    }
                    catch(Exception ex)
                    {
                        JOptionPane.showMessageDialog(theWindow, "Couldn't get it to open file, sorry", "Error", JOptionPane.ERROR_MESSAGE);//**************
                    }
                }
            }
            else if (e.getSource() == saveFile) //saving file
            {
                if(currFile == null)
                {
                    try
                    {
                        currFile = JOptionPane.showInputDialog(theWindow, "Enter new file name", "Input", JOptionPane.INFORMATION_MESSAGE);
                        saveImages();
                    }
                    catch(NullPointerException ex)
                    {
                        //have it do nothing, so it lookds like nothing is wrong
                    }
                }
                else
                    saveImages();
            }
            else if (e.getSource() == saveAs) 
            {
                try
                {
                    currFile = JOptionPane.showInputDialog(theWindow, "Enter new file name", "Input", JOptionPane.INFORMATION_MESSAGE);
                    saveImages();
                    theWindow.setTitle(software + " - " + currFile);
                }
                catch(NullPointerException i)
                {
                    //have it do nothing if user hits cancel instead of saving with a file name
                }
            } 
            else if (e.getSource() == endProgram) 
            {
                System.exit(0);
            }
            else if (e.getSource() == printScene) 
            {
                Printable thePPanel = new thePrintPanel(thePanel);
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setPrintable(thePPanel);
                boolean ok = job.printDialog();
                if (ok) 
                {
                    try 
                    {
                        job.print();
                    } 
                    catch (PrinterException ex) 
                    {
                        /* The job did not successfully complete */
                    }
                }
            }
            else if (e.getSource() == setColor) //lets user chooses color of shapes
            {
                newColor = JColorChooser.showDialog(theWindow,"Select Default Color",newColor);
                altColor = newColor;
            }
            else if (e.getSource() == setSize)
            {
                try
                {
                    String sizeString = JOptionPane.showInputDialog("Enter New Default Size");
                    newSize = Double.parseDouble(sizeString);
                    sq = false; //turns off twisting effects
                }
                catch(NumberFormatException i)
                {
                    JOptionPane.showMessageDialog(theWindow, "Enter a number for your Default Size", "Error", JOptionPane.ERROR_MESSAGE);
                }
                catch(NullPointerException i)
                {
                    //error doesnt show, does nothing if user hits cancel button
                }
            }
            else if (e.getSource() == square) //choose shape to be square
            {
                cir = false;
                sq = true;
            }
            else if (e.getSource() == circle) //chooser shape to be circle
            {
                cir = true;
                sq = false;
            }
            else if (e.getSource() == recolor) //under edit button
            {
                col = JColorChooser.showDialog(theWindow,"Select Color for shape",newColor);
            }
            else if (e.getSource() == resize) //under edit button
            {
                
                try
                {
                    String sizeString = JOptionPane.showInputDialog("Enter New Size");
                    diffSize = Double.parseDouble(sizeString);
                }
                catch(NumberFormatException i)
                {
                    JOptionPane.showMessageDialog(theWindow, "Enter a number for your Size", "Error", JOptionPane.ERROR_MESSAGE);
                }
                catch(NullPointerException i)
                {
                    //error doesnt show, does nothing if user hits cancel button
                }
            }
            else if (e.getSource() == twistShape) //twist shape
            {
                effectsMenu.remove(twistShape);
                effectsMenu.add(twistSOff);
                cir = true;
                sq = true;
            }
            else if (e.getSource() == twistColor) //twist colors
            {
                effectsMenu.remove(twistColor);
                effectsMenu.add(twistCOff);
                cir = true;
                sq = true;
                altColor = JColorChooser.showDialog(theWindow,"Select Alternating Color",altColor);
            }
            else if (e.getSource() == twistSOff) //turning them off
            {
                effectsMenu.remove(twistSOff);
                effectsMenu.add(twistShape);
                cir = true;
                sq = false;
                newSize = 15;
                newColor = Color.RED;
            }
            else if (e.getSource() == twistCOff) //turn off
            {
                effectsMenu.remove(twistCOff);
                effectsMenu.add(twistColor);
                cir = true;
                sq = false;
                newSize = 15;
                newColor = Color.RED;
                altColor = newColor;
            }
        }
    }
    
    // Simple mouse event handling to allow a mousePressed to add or remove
    // a Mosaic from the display.  You will need to enhance this
    // MouseAdapter and you will also need to add a MouseMotionListener to
    // your program.  In this simple program all of the Mosaics drawn are
    // MCircles and they all have the same size and color.  You must add in
    // your program the ability to change all of these attributes.
    private class MListen extends MouseAdapter implements MouseMotionListener
    {
        public void mousePressed(MouseEvent e) 
        {
            X = e.getX();  // Get the location where mouse was pressed
            Y = e.getY();
            if (painting)
            {
                // create new MCircle and add it to the ArrayList
                if(cir && sq == false) //paints circles
                {
                    m = new MCircle(newSize, X, Y, newColor);
                    thePanel.add(m);
                }
                else if(sq && cir == false) //paints squares
                {
                    m = new MSquare(newSize, X, Y, newColor);
                    thePanel.add(m);
                }
                else if(cir && sq)
                {
                    Mosaic temp;
                    if(count == 0) //lets you alternate shapes when you click only
                    {
                        m = new MCircle(newSize, X, Y, newColor);
                        thePanel.add(m);
                        count = 1;
                    }
                    else
                    {
                        temp = new MSquare(newSize, X, Y, altColor);
                        thePanel.add(temp);
                        count = 0;
                    }
                }
                fileSaved = false;
            }
            else if (erasing) 
            {
                // see if the point is within a shape -- if so delete
                // that shape
                int loc = thePanel.select(X, Y);
                if (loc > -1) 
                    thePanel.remove(loc);
                fileSaved = false;
            }
            else if (editing)
            {
                if(cir && sq == false)
                {
                    int size = 0;
                    size = thePanel.select(X, Y);
                    if(m.contains(X, Y))
                    {
                        thePanel.remove(size);
                        m = new MCircle(diffSize, X, Y, col);
                        if(e.getButton() == 3)
                            m.highlight(true);
                        thePanel.add(m);
                    }
                }
                else if(sq && cir == false)
                {
                    int size = 0;
                    size = thePanel.select(X, Y);
                    if(m.contains(X, Y))
                    {
                        thePanel.remove(size);
                        m = new MSquare(diffSize, X, Y, col);
                        if(e.getButton() == 3)
                            m.highlight(true);
                        thePanel.add(m);
                    }
                }
                fileSaved = false;
            }
        }
        
        public void mouseReleased(MouseEvent e) //for popup right click
        {
            try
            {
                if (e.isPopupTrigger() && editing)
                    popup.show(e.getComponent(), e.getX(), e.getY());
                m.highlight(false);
            }
            catch(NullPointerException i)
            {
                //Exception is caught, but it looks, to users, like nothing happened
            }
        }
        public void mouseMoved(MouseEvent e) //program doesnt do anything while moving mouse around only
        {}
        
        public void mouseDragged(MouseEvent e) //lets you draw while moving mouse
        {
            X = e.getX();
            Y = e.getY();
            if (painting) 
            {
                //when difference between mouse position and last image = size of MCircle
                //draw a new circle
                double differenceX, differenceY, diagonal;
                differenceX = X - m.sh.getCenterX();
                differenceY = Y - m.sh.getCenterY();
                diagonal = sqrt((Math.pow(X, 2.0) + Math.pow(Y,2.0))); //getting diagonal, seems to make drawing less awk
                if(Math.abs(differenceX) == newSize || Math.abs(differenceY) == newSize || Math.abs(diagonal) == newSize)
                {
                    if(cir && sq == false) //only circles
                    {
                        m = new MCircle(newSize, X, Y, newColor);
                        thePanel.add(m);
                    }
                    else if(sq && cir == false) //only squares
                    {
                        m = new MSquare(newSize, X, Y, newColor);
                        thePanel.add(m);
                    }
                    else if(cir && sq) //alternating shapes are awkward, but twisting!
                    {
                        Mosaic temp;
                        if (count == 0) //lets you alternate
                        {
                            m = new MCircle(newSize, X, Y, newColor);
                            thePanel.add(m);
                            count = 1;
                        } 
                        else
                        {
                            temp = new MSquare(newSize, X, Y, altColor);
                            thePanel.add(temp);
                            count = 0;
                        }
                    }
                    fileSaved = false;
                }
            }
            else if (erasing) //erasing 
            {
                int loc = thePanel.select(X, Y);
                if (loc > -1)
                    thePanel.remove(loc);
                fileSaved = false;
            }
            else if (editing)
            {
                if(cir && sq == false) //only drags last spot created/added to panel
                {
                    int size = 0;
                    size = thePanel.select(X, Y);
                    if(m.contains(X, Y))
                    {
                        thePanel.remove(size);
                        m = new MCircle(diffSize, X, Y, col);
                        m.highlight(true);
                        thePanel.add(m);
                    }
                }
                else if(sq && cir == false)
                {
                    int size = 0;
                    size = thePanel.select(X, Y);
                    if(m.contains(X, Y))
                    {
                        thePanel.remove(size);
                        m = new MSquare(diffSize, X, Y, col);
                        m.highlight(true);
                        thePanel.add(m);
                    }
                }
                fileSaved = false;
            }
        }
    }

    public static void main(String[] args) 
    {
        new Paint();
    }
}

// This class is taken from the Web and is somewhat buggy but it does a basic
// print of the panel.
class thePrintPanel implements Printable {

    JPanel panelToPrint;

    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) {
            /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }

        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform t = new AffineTransform();
        t.scale(0.9, 0.9);
        g2d.transform(t);
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        //pf.setOrientation(PageFormat.REVERSE_LANDSCAPE);
        /* Now print the window and its visible contents */
        panelToPrint.printAll(g);

        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }

    public thePrintPanel(JPanel p) {
        panelToPrint = p;
    }
}
