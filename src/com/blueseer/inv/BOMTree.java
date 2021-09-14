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

package com.blueseer.inv;

import bsmf.MainFrame;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.OVData;
import com.itextpdf.text.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;

/**
 *
 * @author vaughnte
 */
public class BOMTree extends javax.swing.JPanel  {
 ArrayList<String[]> superlist = new ArrayList<String[]>();
String mystring = "";
int prevlevel = 0;
int lastlevel = 0;
int calllevel = 0;
boolean hasM = false;
double cost = 0.00;
double thiscost = 0.00;
double parentqty = 1;
double thisparent = 1;

DefaultTreeModel levelmodel = null;

    /**
     * Creates new form BOMTree
     */
    public BOMTree() {
        initComponents();
        setLanguageTags(this);
        DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
        Object root = model.getRoot();
        jTree1.setCellRenderer( new CustomCellRenderer() );
    	while(!model.isLeaf(root))
    	{
    		model.removeNodeFromParent((MutableTreeNode)model.getChild(root,0));
    	}
        jTree1.setVisible(false);
    }

    public void setLanguageTags(Object myobj) {
       JPanel panel = null;
        JTabbedPane tabpane = null;
        JScrollPane scrollpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else if (myobj instanceof JScrollPane) {
           scrollpane = (JScrollPane) myobj;    
        } else {
            return;
        }
       Component[] components = panel.getComponents();
       for (Component component : components) {
           if (component instanceof JPanel) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".panel." + component.getName())) {
                       ((JPanel) component).setBorder(BorderFactory.createTitledBorder(tags.getString(this.getClass().getSimpleName() +".panel." + component.getName())));
                    } 
                    setLanguageTags((JPanel) component);
                }
                if (component instanceof JLabel ) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JLabel) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    }
                }
                if (component instanceof JButton ) {
                    if (tags.containsKey("global.button." + component.getName())) {
                       ((JButton) component).setText(tags.getString("global.button." + component.getName()));
                    }
                }
                if (component instanceof JCheckBox) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JCheckBox) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    } 
                }
                if (component instanceof JRadioButton) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JRadioButton) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    } 
                }
       }
    }
    
    
    public void initvars(String[] arg) {
        if (arg != null && arg.length > 0) {
           tbpart.setText(arg[0]);
           getItemTree(arg[0]);
         }  
    }
    
    public void getItemTree(String parentitem) {
        if (! parentitem.isEmpty()) {
       bind_tree(parentitem);
       prevlevel = 0;
       lastlevel = 0;
       calllevel = 0;
       } else {
           bsmf.MainFrame.show(getMessageTag(1021,parentitem));
       }
    }
    
   
    
    public void leveltest() {
    
        
        levelmodel = (DefaultTreeModel)jTree1.getModel();
        ArrayList parts = OVData.getItemMasterMCodelist();
        
        DefaultMutableTreeNode mynode = get_nodes(tbpart.getText(), 0);
        //levelmodel.setRoot(mynode);
        //DefaultMutableTreeNode root = (DefaultMutableTreeNode) levelmodel.getRoot();
        lastlevel = 0;
        calllevel = 0;
        getMaxLevel(mynode);
      //  bsmf.MainFrame.show(String.valueOf(lastlevel) + " / " + tbpart.getText().toString());
       
}
    
    public void bind_tree(String parentpart) {
      //  jTree1.setModel(null);
       
       cost = 0;
        DefaultMutableTreeNode mynode = get_nodes(parentpart, 1);
        DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
        model.setRoot(mynode);
        jTree1.setVisible(true);
     
        
    
    }
     
    public void getMaxLevel(DefaultMutableTreeNode node) {
    int childCount = node.getChildCount();
    for (int i = 0; i < childCount; i++) {
        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
         calllevel = childNode.getLevel();
                    if (calllevel > lastlevel) {
                    lastlevel = calllevel;
                    }
        if (childNode.getChildCount() > 0) {
            getMaxLevel(childNode);
        } 
    }

   // System.out.println("+++" + node.toString() + "+++");

}
     
    public void bind_tree2(String part) {
      //  jTree1.setModel(null);
       
        DefaultMutableTreeNode mynode = get_parents(part);
       
        DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
        model.setRoot(mynode);
        jTree1.setVisible(true);
        
        
    }
    
    public void printBOM(String parentItem) {
        
        // print header info 
        if (lastlevel == 0) {  // this is the header print
           // System.out.println(lastlevel + " " + parentItem);
            superlist.add(new String[]{String.valueOf(lastlevel), parentItem, "", "", "", "" });
        }
        lastlevel++;
         String spaces = String.format("%"+(lastlevel * 2)+"s", "");
         ArrayList<String> mylist = OVData.getpsmstrlist(parentItem);
         for ( String myvalue : mylist) {
             String[] value = myvalue.toUpperCase().split(",");
                  if (value[2].toUpperCase().compareTo("M") == 0) {
                   // System.out.println(lastlevel + spaces + value[1]);
                    superlist.add(new String[]{String.valueOf(lastlevel), value[1], value[2], value[4], value[3], value[6]});
                      printBOM(value[1]);
                    lastlevel--;
                  } else {
                 // System.out.println(lastlevel + spaces + value[1]);
                  superlist.add(new String[]{String.valueOf(lastlevel), value[1], value[2], value[4], value[3], value[6]});
                    
                  }
         }
         
         // now print trailer info
    }
    
  
    
    public DefaultMutableTreeNode get_parents(String mypart)  {
          
        DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(mypart);
        
        ArrayList<String> mylist = new ArrayList<String>();
        mylist = OVData.getpsmstrparents(mypart);
        if ( ! mylist.isEmpty()) {
        for ( String myvalue : mylist) {
                    DefaultMutableTreeNode mfgnode = new DefaultMutableTreeNode(myvalue);   
                    String mystring = OVData.getItemStatusByPart(myvalue);
                    String mytype = OVData.getItemTypeByPart(mypart);
                    if (! mystring.equals("OBSOLETE")) {
                    mynode.add(mfgnode);
                   if (! myvalue.isEmpty() && ! mytype.equals("FG") ) {
                    DefaultMutableTreeNode newnode = get_parents(myvalue);
                   
                   mynode.add(newnode);
                   }
                    
                    }
                     
        }
        }
        return mynode;
     }

    public DefaultMutableTreeNode get_nodes(String mypart, double perqty)  {
        lastlevel++;
        DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(mypart);
        String[] newpart = mypart.split("___");
        ArrayList<String> mylist = new ArrayList<String>();
        mylist = OVData.getpsmstrlist(newpart[0]);
       
        
        
        for ( String myvalue : mylist) {
           if (lastlevel == 1)
                perqty = 1;
            
          //  thisparent = perqty;
            
            String[] value = myvalue.toUpperCase().split(",");
              if (value[0].toUpperCase().compareTo(newpart[0].toUpperCase().toString()) == 0) {
          
                  if (value[2].toUpperCase().compareTo("M") == 0) {
              
                    DefaultMutableTreeNode mfgnode = new DefaultMutableTreeNode();   
                //    parentqty = thisparent * Double.valueOf(value[3]);
                    lastlevel++;
                    mfgnode = get_nodes(value[1] + "___" + value[4] + "___" + value[3], parentqty);
                    lastlevel--;
                    mynode.add(mfgnode);
                 //   bsmf.MainFrame.show(value[1] + " / " + String.valueOf(lastlevel) + " / " + parqty);
                    
                  } else {
                  DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(value[1] + "___" + value[4] + "___" + value[3]);  
                  mynode.add(childnode);
                //  parentqty = thisparent * Double.valueOf(value[3]);
               //   thiscost = (parentqty * OVData.getItemMtlCost(value[1], "standard", "1516"));
               //   cost = cost + thiscost;
             //     bsmf.MainFrame.show(value[1] + " / " + String.valueOf(lastlevel) + " / " + parentqty + " / " + thiscost + " / " + cost);
                  }
           
              } 
        
        }
        lastlevel--;
        return mynode;
     }
     
    public DefaultMutableTreeNode get_nodes_file(String mypart) throws FileNotFoundException, IOException {
        DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(mypart);
        String[] newpart = mypart.split("___");
         BufferedReader myfile = new BufferedReader(new FileReader(new File("/home/vaughnte/psmstr.csv")));
        String line = "";
          while ((line = myfile.readLine()) != null) {
              String[] value = line.split(",");  
              if (value[0].toUpperCase().compareTo(newpart[0].toUpperCase().toString()) == 0) {
               
                  if (value[2].toUpperCase().compareTo("M") == 0) {
                    DefaultMutableTreeNode mfgnode = new DefaultMutableTreeNode();   
                    mfgnode = get_nodes_file(value[1] + "___" + value[4] + "___" + value[3]);
                    mynode.add(mfgnode);
                  } else {
                  DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(value[1] + "___" + value[4] + "___" + value[3]);   
                  mynode.add(childnode);
                  }
              }
          }
        myfile.close();
        return mynode;
     }
             
    public class CustomCellRenderer	extends JLabel	implements TreeCellRenderer {
      private ImageIcon		deckImage;
      private ImageIcon[]		suitImages;
      private ImageIcon[]		cardImages;
	private boolean			bSelected;


	public CustomCellRenderer()
	{
		// Load the images
		deckImage = new ImageIcon( "deck.gif" );

		suitImages = new ImageIcon[4];
		suitImages[0] = new ImageIcon( "clubs.gif" );
		suitImages[1] = new ImageIcon( "diamonds.gif" );
		suitImages[2] = new ImageIcon( "spades.gif" );
		suitImages[3] = new ImageIcon( "hearts.gif" );

		cardImages = new ImageIcon[13];
		cardImages[0] = new ImageIcon( "ace.gif" );
		cardImages[1] = new ImageIcon( "two.gif" );
		cardImages[2] = new ImageIcon( "three.gif" );
		cardImages[3] = new ImageIcon( "four.gif" );
		cardImages[4] = new ImageIcon( "five.gif" );
		cardImages[5] = new ImageIcon( "six.gif" );
		cardImages[6] = new ImageIcon( "seven.gif" );
		cardImages[7] = new ImageIcon( "eight.gif" );
		cardImages[8] = new ImageIcon( "nine.gif" );
		cardImages[9] = new ImageIcon( "ten.gif" );
		cardImages[10] = new ImageIcon( "jack.gif" );
		cardImages[11] = new ImageIcon( "queen.gif" );
		cardImages[12] = new ImageIcon( "king.gif" );
	}

	public Component getTreeCellRendererComponent( JTree tree,
					Object value, boolean bSelected, boolean bExpanded,
							boolean bLeaf, int iRow, boolean bHasFocus )
	{
		// Find out which node we are rendering and get its text
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
		String	labelText = (String)node.getUserObject();

		this.bSelected = bSelected;
               
		this.setFont(this.getFont().deriveFont(18.0f));
		// Set the correct foreground color
		if( !bSelected )
			setForeground( Color.black );
		else
			setForeground( Color.white );

		// Determine the correct icon to display
		if( labelText.startsWith("PC") ) {
			//setIcon( deckImage );
                          setForeground(Color.red);
                }
                if( labelText.startsWith("PM") ) {
			//setIcon( deckImage );
                          setForeground(Color.blue);
                }
		

		// Add the text to the cell
		setText( labelText );

		return this;
	}

	// This is a hack to paint the background.  Normally a JLabel can
	// paint its own background, but due to an apparent bug or
	// limitation in the TreeCellRenderer, the paint method is
	// required to handle this.
	public void paint( Graphics g )
	{
		Color		bColor;
		Icon		currentI = getIcon();

		// Set the correct background color
		bColor = bSelected ? SystemColor.textHighlight : Color.white;
		g.setColor( bColor );

		// Draw a rectangle in the background of the cell
		g.fillRect( 0, 0, getWidth() - 1, getHeight() - 1 );

		super.paint( g );
	}

}
         
         
         
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        tbpart = new javax.swing.JTextField();
        lblevel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        btprint = new javax.swing.JButton();
        btbrowse = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("BOM Tree Lookup"));
        jPanel1.setName("panelmain"); // NOI18N

        jTree1.setFont(new java.awt.Font("Cantarell", 0, 18)); // NOI18N
        jScrollPane1.setViewportView(jTree1);

        btprint.setText("Indented Print");
        btprint.setName("btindentedprint"); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tbpart, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(btprint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 171, Short.MAX_VALUE)
                        .addComponent(lblevel, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btprint)
                    .addComponent(tbpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblevel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btbrowse)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
       
        if ( tbpart.getText().isEmpty() || ! OVData.isValidItem(tbpart.getText())) {
            bsmf.MainFrame.show(getMessageTag(1021, tbpart.getText()));
            return;
        }
        
        lastlevel = 0;
        printBOM(tbpart.getText());
        
       
        try {
            
            final PrinterJob pjob = PrinterJob.getPrinterJob();
            pjob.setJobName("Graphics Demo Printout");
            pjob.setCopies(1);
            pjob.setPrintable(new Printable() {
                private boolean rootPaneCheckingEnabled;
                public int print(Graphics pg, PageFormat pf, int pageNum) {
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    Date now = new Date();
                    if (pageNum > 0) // we only print one page
                    {
                        return Printable.NO_SUCH_PAGE; // ie., end of job
                    }
                    pg.setFont(new java.awt.Font("TimesRoman", java.awt.Font.PLAIN, 12));

                    pg.drawString("Parent Item:", 50, 50);
                    pg.drawString(tbpart.getText(), 120, 50);
                    pg.drawString("Description:", 50, 70);
                    pg.drawString(OVData.getItemDesc(tbpart.getText()), 120, 70);

                    pg.drawString("Date: ", 500, 50);
                    pg.drawString(dfdate.format(now), 505, 70);
                   // pg.draw3DRect(500, 55, 60, 20, rootPaneCheckingEnabled);

                    pg.setFont(new java.awt.Font("TimesRoman", java.awt.Font.BOLD, 18));
                    pg.drawString("Item BOM Report", 210, 110);

                    pg.setFont(new java.awt.Font("TimesRoman", java.awt.Font.PLAIN, 12));
                    pg.drawString("Level", 50, 130);
                    pg.drawString("Item", 120, 130);
                    pg.drawString("Type", 220, 130);
                    pg.drawString("Desc", 260, 130);
                    pg.drawString("QtyPer", 440, 130);
                    pg.drawString("Operation", 500, 130);
                    int p = 0;
                   // String spaces = ""; String.format("%"+(lastlevel * 2)+"s", "");
                    int indent = 0;
                    for (String[] s : superlist) {
                     if (s[0].equals("0")) {continue;}    
                     indent = (Integer.valueOf(s[0]) - 1) * 10;
                     pg.drawString(s[0], 50 + indent, 150 + (p * 20));
                     pg.drawString(s[1], 120 , 150 + (p * 20));
                     pg.drawString(s[2], 220 , 150 + (p * 20));
                     pg.drawString(s[3], 260 , 150 + (p * 20));
                     pg.drawString(s[4], 440 , 150 + (p * 20));
                     pg.drawString(s[5], 500 , 150 + (p * 20));
                     p++;
                    }
                    /*
                    pg.drawString("Supplier", 260, 110);
                    pg.drawString("blah", 265, 138);
                    pg.drawString("Supplier Contact", 400, 110);
                    pg.drawString("blah", 405, 138);
                    pg.draw3DRect(50, 120, 60, 20, rootPaneCheckingEnabled);
                    pg.draw3DRect(140, 120, 110, 20, rootPaneCheckingEnabled);
                    pg.draw3DRect(260, 120, 110, 20, rootPaneCheckingEnabled);
                    pg.draw3DRect(400, 120, 110, 20, rootPaneCheckingEnabled);

                    pg.setFont(new java.awt.Font("TimesRoman", java.awt.Font.PLAIN, 8));
                    pg.draw3DRect(50, 160, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(true), 52, 168);
                    pg.drawString("QPR (8-D Required)", 80, 170);
                    pg.draw3DRect(50, 180, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(false), 52, 188);
                    pg.drawString("Infor Only (No 8-D Required)", 80, 190);
                    pg.drawString("Disposition of Nonbsmf.MainFrame.conformance", 50, 230);
                   
                    pg.drawString("14.1-1       Revised: 11/29/2012 ", 50, 725);
                    */
                    return Printable.PAGE_EXISTS;
                }
            });

            if (pjob.printDialog() == false) // choose printer
            {
                return;
            }
            pjob.print();
        } catch (PrinterException pe) {
            MainFrame.bslog(pe);
        }
       
         
    }//GEN-LAST:event_btprintActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, new String[]{"bomtree","it_item"});
    }//GEN-LAST:event_btbrowseActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btprint;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel lblevel;
    private javax.swing.JTextField tbpart;
    // End of variables declaration//GEN-END:variables
}
