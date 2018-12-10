/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueseer.inv;

import com.blueseer.utl.OVData;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author vaughnte
 */
public class calcCost {
    
      public double lastlevel = 0;
         public double thisparent = 1;
         public double parentqty = 1;
         public double mtlcost = 0;
         public double lbrcost = 0;
         public double bdncost = 0;
         public double ovhcost = 0;
         public double outcost = 0;
         public double thismtlcost = 0;
         public double thislbrcost = 0;
         public double thisbdncost = 0;
         public double thisovhcost = 0;
         public double thisoutcost = 0;
         
         public double lowermtlcost = 0;
         public double uppermtlcost = 0;
         public double lowerlbrcost = 0;
         public double upperlbrcost = 0;
         public double lowerbdncost = 0;
         public double upperbdncost = 0;
         public double lowerovhcost = 0;
         public double upperovhcost = 0;
         public double loweroutcost = 0;
         public double upperoutcost = 0;
         
         public Double getMtlCost(String part) {
             getMtlCostRecursive(part,1);
             return mtlcost;
         }
         
         public Double getLbrCost(String part) {
             lastlevel = 0;
             thisparent = 1;
             parentqty = 1;
             getLbrCostRecursive(part);
             return lbrcost;
         }         
         
          public Double getBdnCost(String part) {
             lastlevel = 0;
             thisparent = 1;
             parentqty = 1;
              getBdnCostRecursive(part);
             return bdncost;
         }        
          
           public Double getOvhCost(String part) {
             lastlevel = 0;
             thisparent = 1;
             parentqty = 1;
             getOvhCostRecursive(part,1);
             return ovhcost;
         }        
           
            public Double getOutCost(String part) {
                lastlevel = 0;
             thisparent = 1;
             parentqty = 1;
             getOutCostRecursive(part, 1);
             return outcost;
         }        
         
           public ArrayList getTotalCost(String part) {
             ArrayList mylist = new ArrayList();
             getTotalCostRecursive(part,1);
            // getLbrCost(part);
          //   getBdnCost(part);
             mylist.add(lowermtlcost + uppermtlcost);
             mylist.add(lowerlbrcost + upperlbrcost);
             mylist.add(lowerbdncost + upperbdncost);
             mylist.add(lowerovhcost + upperovhcost);
             mylist.add(loweroutcost + upperoutcost);
             return mylist;
         }
           
           
              public ArrayList getTotalCostElements(String part) {
                  
             ArrayList mylist = new ArrayList();
             getTotalCostRecursive(part,1);
            // getLbrCost(part);
          //   getBdnCost(part);
             mylist.add(lowermtlcost);
             mylist.add(lowerlbrcost);
             mylist.add(lowerbdncost);
             mylist.add(lowerovhcost);
             mylist.add(loweroutcost);
             mylist.add(uppermtlcost);
             mylist.add(upperlbrcost);
             mylist.add(upperbdncost);
             mylist.add(upperovhcost);
             mylist.add(upperoutcost);
             return mylist;
         }
           
            public ArrayList getTotalLowerCost(String part) {
             ArrayList mylist = new ArrayList();
             getTotalCostRecursive(part,1);
            // getLbrCost(part);
          //   getBdnCost(part);
             mylist.add(lowermtlcost);
             mylist.add(lowerlbrcost);
             mylist.add(lowerbdncost);
             mylist.add(lowerovhcost);
             mylist.add(loweroutcost);
             return mylist;
         }
            
             public ArrayList getTotalUpperCost(String part) {
             ArrayList mylist = new ArrayList();
             getTotalCostRecursive(part,1);
            // getLbrCost(part);
          //   getBdnCost(part);
             mylist.add(uppermtlcost);
             mylist.add(upperlbrcost);
             mylist.add(upperbdncost);
             mylist.add(upperovhcost);
             mylist.add(upperoutcost);
             return mylist;
         }
            
           public void getTotalCostRecursive(String mypart, double perqty)  {
        lastlevel++;
       
        String[] newpart = mypart.split("___");
        ArrayList<String> mylist = new ArrayList<String>();
        mylist = OVData.getpsmstrlist(newpart[0]);
       
        if (lastlevel > 1) {
                    thisovhcost = (parentqty * OVData.getItemOvhCost(mypart));
                    ovhcost = ovhcost + thisovhcost;
                     thisoutcost = (parentqty * OVData.getItemOvhCost(mypart));
                    outcost = outcost + thisoutcost;
                //    MainFrame.show(mypart + " / " + thisovhcost + " / " + ovhcost);
        }
        
         thislbrcost = OVData.getLaborAllOps(newpart[0]);
          lbrcost = lbrcost + thislbrcost;
        
         thisbdncost = OVData.getBurdenAllOps(newpart[0]);
          bdncost = bdncost + thisbdncost; 
          
          
        if (lastlevel == 1) {
            upperlbrcost = upperlbrcost + OVData.getLaborAllOps(newpart[0]);
            upperbdncost = upperbdncost + OVData.getBurdenAllOps(newpart[0]);
            // all mtl, ovh, out is generally considered 'lower'  ...but mtl, ovh, out 'could be' assigned 
            // for FG and therefore should be totals assigned to mtlcost, ovhcost, outcost variables below
            uppermtlcost = uppermtlcost + (OVData.getItemMtlCost(newpart[0]));
            upperovhcost = upperovhcost + (OVData.getItemOvhCost(newpart[0]));
            upperoutcost = upperoutcost + (OVData.getItemOutCost(newpart[0]));
           
        }  else {
            lowerlbrcost = lowerlbrcost + (parentqty * OVData.getLaborAllOps(newpart[0]));
            //lowerbdncost = lowerbdncost + OVData.getBurdenAllOps(newpart[0]); 
            lowerbdncost = lowerbdncost + (parentqty * OVData.getBurdenAllOps(newpart[0]));
           
        }
        
         
        
        for ( String myvalue : mylist) {
           if (lastlevel == 1)
                perqty = 1;
            
            thisparent = perqty;
            
            String[] value = myvalue.toUpperCase().split(",");
              if (value[0].toUpperCase().compareTo(newpart[0].toUpperCase().toString()) == 0) {
          
                  if (value[2].toUpperCase().compareTo("M") == 0) {
                    parentqty = thisparent * Double.valueOf(value[3]);
                    lastlevel++;
                    getTotalCostRecursive(value[1] + "___" + value[4] + "___" + value[3], parentqty);
                    lastlevel--;
                  } else {
                  parentqty = thisparent * Double.valueOf(value[3]);
                  thismtlcost = (parentqty * OVData.getItemMtlCost(value[1]));
                  mtlcost = mtlcost + thismtlcost;
                  thisovhcost = (parentqty * OVData.getItemOvhCost(value[1]));
                  ovhcost = ovhcost + thisovhcost;
                  thisoutcost = (parentqty * OVData.getItemOutCost(value[1]));
                  outcost = outcost + thisoutcost;
                  
                  }
           
              } 
        
        }
        lastlevel--;
         lowermtlcost = mtlcost;
         lowerovhcost = ovhcost;
         loweroutcost = outcost;
     }
           
           public void getMtlCostRecursive(String mypart, double perqty)  {
        lastlevel++;
       
        String[] newpart = mypart.split("___");
        ArrayList<String> mylist = new ArrayList<String>();
        mylist = OVData.getpsmstrlist(newpart[0]);
       
        
        
        for ( String myvalue : mylist) {
           if (lastlevel == 1)
                perqty = 1;
            
            thisparent = perqty;
            
            String[] value = myvalue.toUpperCase().split(",");
              if (value[0].toUpperCase().compareTo(newpart[0].toUpperCase().toString()) == 0) {
          
                  if (value[2].toUpperCase().compareTo("M") == 0) {
                    parentqty = thisparent * Double.valueOf(value[3]);
                    lastlevel++;
                    getMtlCostRecursive(value[1] + "___" + value[4] + "___" + value[3], parentqty);
                    lastlevel--;
                  } else {
                  parentqty = thisparent * Double.valueOf(value[3]);
                  thismtlcost = (parentqty * OVData.getItemMtlCost(value[1]));
                  mtlcost = mtlcost + thismtlcost;
                  }
           
              } 
        
        }
        lastlevel--;
        
     }
          
           public void getLbrCostRecursive(String mypart)  {
        ArrayList<String> mylist = OVData.getpsmstrlist(mypart);
          thislbrcost = OVData.getLaborAllOps(mypart);
          lbrcost = lbrcost + thislbrcost;
          //MainFrame.show(mypart + " / " + thislbrcost +  " / " + lbrcost);
        for ( String myvalue : mylist) {
            String[] value = myvalue.toUpperCase().split(",");
              if (value[0].toUpperCase().compareTo(mypart.toUpperCase().toString()) == 0) {
                  if (value[2].toUpperCase().compareTo("M") == 0) {
                    getLbrCostRecursive(value[1]);
                  }
              } 
        
        }
       
        
     }
           
           public void getBdnCostRecursive(String mypart)  {
        ArrayList<String> mylist = OVData.getpsmstrlist(mypart);
          thisbdncost = OVData.getBurdenAllOps(mypart);
          bdncost = bdncost + thisbdncost;
          //MainFrame.show(mypart + " / " + thisbdncost +  " / " + bdncost);
        for ( String myvalue : mylist) {
            String[] value = myvalue.toUpperCase().split(",");
              if (value[0].toUpperCase().compareTo(mypart.toUpperCase().toString()) == 0) {
                  if (value[2].toUpperCase().compareTo("M") == 0) {
                    getBdnCostRecursive(value[1]);
                  }
              } 
        
        }
       
        
     }
           
           public void getOvhCostRecursive(String mypart, double perqty)  {
        lastlevel++;
       
        String[] newpart = mypart.split("___");
        ArrayList<String> mylist = new ArrayList<String>();
        mylist = OVData.getpsmstrlist(newpart[0]);
       
        if (lastlevel > 1) {
                    thisovhcost = (parentqty * OVData.getItemOvhCost(mypart));
                    ovhcost = ovhcost + thisovhcost;
                //    MainFrame.show(mypart + " / " + thisovhcost + " / " + ovhcost);
        }
        
        for ( String myvalue : mylist) {
           if (lastlevel == 1)
                perqty = 1;
            
            thisparent = perqty;
            
            String[] value = myvalue.toUpperCase().split(",");
              if (value[0].toUpperCase().compareTo(newpart[0].toUpperCase().toString()) == 0) {
             
                 
              if (value[2].toUpperCase().compareTo("M") == 0) {
                    parentqty = thisparent * Double.valueOf(value[3]);
                    lastlevel++;
                    
                    getOvhCostRecursive(value[1] + "___" + value[4] + "___" + value[3], parentqty);
                    
                    
                    lastlevel--;
                  } else {
                  parentqty = thisparent * Double.valueOf(value[3]);
                   thisovhcost = (parentqty * OVData.getItemOvhCost(value[1]));
                  ovhcost = ovhcost + thisovhcost;
              //   MainFrame.show(value[1] + " / " + thisovhcost + " / " + ovhcost);
                  }
           
              } 
        
        }
        lastlevel--;
        
     }
           
           public void getOutCostRecursive(String mypart, double perqty)  {
        lastlevel++;
       
        String[] newpart = mypart.split("___");
        ArrayList<String> mylist = new ArrayList<String>();
        mylist = OVData.getpsmstrlist(newpart[0]);
       
        if (lastlevel > 1) {
                    thisoutcost = (parentqty * OVData.getItemOvhCost(mypart));
                    outcost = outcost + thisoutcost;
                //    MainFrame.show(mypart + " / " + thisovhcost + " / " + ovhcost);
        }
        
        for ( String myvalue : mylist) {
           if (lastlevel == 1)
                perqty = 1;
            
            thisparent = perqty;
            
            String[] value = myvalue.toUpperCase().split(",");
              if (value[0].toUpperCase().compareTo(newpart[0].toUpperCase().toString()) == 0) {
             
                 
              if (value[2].toUpperCase().compareTo("M") == 0) {
                    parentqty = thisparent * Double.valueOf(value[3]);
                    lastlevel++;
                    
                    getOutCostRecursive(value[1] + "___" + value[4] + "___" + value[3], parentqty);
                    
                    
                    lastlevel--;
                  } else {
                  parentqty = thisparent * Double.valueOf(value[3]);
                   thisoutcost = (parentqty * OVData.getItemOutCost(value[1]));
                  outcost = outcost + thisoutcost;
              //   MainFrame.show(value[1] + " / " + thisovhcost + " / " + ovhcost);
                  }
           
              } 
        
        }
        lastlevel--;
        
     }
       
    
    
}
