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

import com.blueseer.utl.OVData;
import com.itextpdf.text.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
 ArrayList<String> superlist = new ArrayList<String>();
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
        DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
        Object root = model.getRoot();
        jTree1.setCellRenderer( new CustomCellRenderer() );
    	while(!model.isLeaf(root))
    	{
    		model.removeNodeFromParent((MutableTreeNode)model.getChild(root,0));
    	}
        jTree1.setVisible(false);
    }

    public void initvars(String[] arg) {
        
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
        bsmf.MainFrame.show(String.valueOf(lastlevel) + " / " + tbpart.getText().toString());
       
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
                    if (myvalue.equals("4100"))
                        bsmf.MainFrame.show("yep");
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
        jButton1 = new javax.swing.JButton();
        lblevel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        btprint = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("BOM Tree Lookup"));

        jButton1.setText("Search");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTree1.setFont(new java.awt.Font("Cantarell", 0, 18)); // NOI18N
        jScrollPane1.setViewportView(jTree1);

        btprint.setText("Print");
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
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
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btprint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
                        .addComponent(lblevel, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1)
                            .addComponent(btprint))
                        .addGap(19, 19, 19))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lblevel, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       bind_tree(tbpart.getText());
       prevlevel = 0;
       lastlevel = 0;
       calllevel = 0;
       
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
        
    }//GEN-LAST:event_btprintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btprint;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel lblevel;
    private javax.swing.JTextField tbpart;
    // End of variables declaration//GEN-END:variables
}
