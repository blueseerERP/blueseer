/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn "VCSCode"

All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.blueseer.adm;

/**
 *
 * @author vaughnte
 */
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.*;
import java.util.Random;
import javax.swing.*;

import java.awt.geom.Path2D;
import java.util.Locale;

/*
 *  Support custom painting on a panel in the form of
 *
 *  a) images - that can be scaled, tiled or painted at original size
 *  b) non solid painting - that can be done by using a Paint object
 *
 *  Also, any component added directly to this panel will be made
 *  non-opaque so that the custom painting can show through.
 */
public  class BackGround extends JPanel
{
	public static final int SCALED = 0;
	public static final int TILED = 1;
	public static final int ACTUAL = 2;

	private Paint painter;
	private Image image;
	private int style = ACTUAL;
	private float alignmentX = 0.5f;
	private float alignmentY = 0.5f;
	private boolean isTransparentAdd = true;

	/*
	 *  Set image as the background with the SCALED style
	 */
	public BackGround(Image image)
	{
		this(image,ACTUAL);
	}
        
        
        public BackGround()
	{
		setLayout( new BorderLayout() );
	}
        
       

	/*
	 *  Set image as the background with the specified style
	 */
	public BackGround(Image image, int style)
	{
		setImage( image );
		setStyle( style );
		setLayout( new BorderLayout() );
	}

	/*
	 *  Set image as the backround with the specified style and alignment
	 */
	public BackGround(Image image, int style, float alignmentX, float alignmentY)
	{
		setImage( image );
		setStyle( style );
		setImageAlignmentX( alignmentX );
		setImageAlignmentY( alignmentY );
		setLayout( new BorderLayout() );
	}

	/*
	 *  Use the Paint interface to paint a background
	 */
	public BackGround(Paint painter)
	{
		setPaint( painter );
		setLayout( new BorderLayout() );
	}

	/*
	 *	Set the image used as the background
	 */
	public void setImage(Image image)
	{
		this.image = image;
		repaint();
	}

	/*
	 *	Set the style used to paint the background image
	 */
	public void setStyle(int style)
	{
		this.style = style;
		repaint();
	}

	/*
	 *	Set the Paint object used to paint the background
	 */
	public void setPaint(Paint painter)
	{
		this.painter = painter;
		repaint();
	}

	/*
	 *  Specify the horizontal alignment of the image when using ACTUAL style
	 */
	public void setImageAlignmentX(float alignmentX)
	{
		this.alignmentX = alignmentX > 1.0f ? 1.0f : alignmentX < 0.0f ? 0.0f : alignmentX;
		repaint();
	}

	/*
	 *  Specify the horizontal alignment of the image when using ACTUAL style
	 */
	public void setImageAlignmentY(float alignmentY)
	{
		this.alignmentY = alignmentY > 1.0f ? 1.0f : alignmentY < 0.0f ? 0.0f : alignmentY;
		repaint();
	}

	/*
	 *  Override method so we can make the component transparent
	 */
	public void add(JComponent component)
	{
		add(component, null);
	}

	/*
	 *  Override to provide a preferred size equal to the image size
	 */
	@Override
	public Dimension getPreferredSize()
	{
		if (image == null)
			return super.getPreferredSize();
		else
			return new Dimension(image.getWidth(null), image.getHeight(null));
	}

	/*
	 *  Override method so we can make the component transparent
	 */
	public void add(JComponent component, Object constraints)
	{
		if (isTransparentAdd)
		{
			makeComponentTransparent(component);
		}

		super.add(component, constraints);
	}

	/*
	 *  Controls whether components added to this panel should automatically
	 *  be made transparent. That is, setOpaque(false) will be invoked.
	 *  The default is set to true.
	 */
	public void setTransparentAdd(boolean isTransparentAdd)
	{
		this.isTransparentAdd = isTransparentAdd;
	}

	/*
	 *	Try to make the component transparent.
	 *  For components that use renderers, like JTable, you will also need to
	 *  change the renderer to be transparent. An easy way to do this it to
	 *  set the background of the table to a Color using an alpha value of 0.
	 */
	private void makeComponentTransparent(JComponent component)
	{
		component.setOpaque( false );

		if (component instanceof JScrollPane)
		{
			JScrollPane scrollPane = (JScrollPane)component;
			JViewport viewport = scrollPane.getViewport();
			viewport.setOpaque( false );
			Component c = viewport.getView();

			if (c instanceof JComponent)
			{
				((JComponent)c).setOpaque( false );
			}
		}
	}


        
        
        
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		//  Invoke the painter for the background

		if (painter != null)
		{
                     Dimension d = getSize();
                     Graphics2D g2 = (Graphics2D) g;
                     g2.setPaint(painter);
	             g2.fill( new Rectangle(0, 0, d.width, d.height) );
        
                   
                     drawV(g2);
                    
                     // drawRectangle(g2);
                          
                        
		}

		//  Draw the image

		if (image == null ) return;

		switch (style)
		{
			case SCALED :
				drawScaled(g);
				break;

			case TILED  :
				drawTiled(g);
				break;

			case ACTUAL :
				drawActual(g);
				break;

			default:
            	drawScaled(g);
		}
	}

        
        private void drawV(Graphics2D g2) {
             Random random = new Random();
                    // int xPoints[] = {55,67,109,73,83,55,27,37,1,43};
                   //  int yPoints[] = {0,36,36,54,96,72,96,54,36,36};
             int mywidth = this.getWidth();
             int myheight = this.getHeight();
             int adj = (int) (myheight * .33);
             
                     int xPoints[] = {0, mywidth / 2, mywidth, mywidth, mywidth / 2, 0  };
                     int yPoints[] = {-1 * adj, myheight - adj, -1 * adj, 100, myheight, 100};
                     GeneralPath star = new GeneralPath();
                        
                         star.moveTo(xPoints[0], yPoints[0]);
                           for (int count = 1 ; count < xPoints.length; count++) {
                           star.lineTo(xPoints[count], yPoints[count]);
                           }
                         star.closePath();
                         
                       //  g2.translate(this.getWidth()/2,this.getHeight()/2);
                         g2.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256) ));
                         g2.setColor(new Color(255, 255, 255));
                         g2.fill(star);
                         
                         
                        

                       
                         
                        /*
                          for (int count = 1; count <= 20; count++) {
                             g2.rotate(Math.PI/ 10.0);
                             g2.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256) ));
                             g2.fill(star);
                         }
                         */
                          
        }
        
        private void drawRectangle(Graphics2D g2) {
            double height = 400;
             double width = 300;
Path2D.Double path = new Path2D.Double();
path.moveTo(40.0, 40.0);
path.curveTo(0.0, 0.0, 8.0, 0.0, 8.0, 0.0);
path.lineTo(width - 8.0, 0.0);
path.curveTo(width, 0.0, width, 8.0, width, 8.0);
path.lineTo(width, height - 8.0);
path.curveTo(width, height, width - 8.0, height, width - 8.0, height);
path.lineTo(8.0, height);
path.curveTo(0.0, height, 0.0, height - 8.0, 0, height - 8.0);
path.closePath();
g2.fill(path);
        }
        
        
	/*
	 *  Custom painting code for drawing a SCALED image as the background
	 */
	private void drawScaled(Graphics g)
	{
		Dimension d = getSize();
		g.drawImage(image, 0, 0, d.width, d.height, null);
	}

	/*
	 *  Custom painting code for drawing TILED images as the background
	 */
	private void drawTiled(Graphics g)
	{
		   Dimension d = getSize();
		   int width = image.getWidth( null );
		   int height = image.getHeight( null );

		   for (int x = 0; x < d.width; x += width)
		   {
			   for (int y = 0; y < d.height; y += height)
			   {
				   g.drawImage( image, x, y, null, null );
			   }
		   }
	}

	/*
	 *  Custom painting code for drawing the ACTUAL image as the background.
	 *  The image is positioned in the panel based on the horizontal and
	 *  vertical alignments specified.
	 */
	private void drawActual(Graphics g)
	{
		Dimension d = getSize();
		Insets insets = getInsets();
		int width = d.width - insets.left - insets.right;
		int height = d.height - insets.top - insets.left;
		float x = (width - image.getWidth(null)) * alignmentX;
		float y = (height - image.getHeight(null)) * alignmentY;
		g.drawImage(image, (int)x + insets.left, (int)y + insets.top, this);
	}
        
        
        
        
}