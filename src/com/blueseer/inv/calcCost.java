/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn 

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
     
    public double getTotalCostSum(String item, String bom) {
        getTotalCostRecursive(item,1,bom);
        return (lowermtlcost + uppermtlcost + lowerlbrcost + upperlbrcost + lowerbdncost + upperbdncost + lowerovhcost + upperovhcost + loweroutcost + upperoutcost );
    }
    
    public ArrayList getTotalCost(String part, String bom) {
             ArrayList mylist = new ArrayList();
             getTotalCostRecursive(part,1,bom);
             mylist.add(lowermtlcost + uppermtlcost);
             mylist.add(lowerlbrcost + upperlbrcost);
             mylist.add(lowerbdncost + upperbdncost);
             mylist.add(lowerovhcost + upperovhcost);
             mylist.add(loweroutcost + upperoutcost);
             return mylist;
         }
           
           
    public ArrayList getTotalCostElements(String part, String bom) {
                  
             ArrayList mylist = new ArrayList();
             getTotalCostRecursive(part,1,bom);
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
           
    public ArrayList getTotalLowerCost(String part, String bom) {
             ArrayList mylist = new ArrayList();
             getTotalCostRecursive(part,1, bom);
            // getLbrCost(part);
          //   getBdnCost(part);
             mylist.add(lowermtlcost);
             mylist.add(lowerlbrcost);
             mylist.add(lowerbdncost);
             mylist.add(lowerovhcost);
             mylist.add(loweroutcost);
             return mylist;
         }
            
    public ArrayList getTotalUpperCost(String part, String bom) {
             ArrayList mylist = new ArrayList();
             getTotalCostRecursive(part,1, bom);
            // getLbrCost(part);
          //   getBdnCost(part);
             mylist.add(uppermtlcost);
             mylist.add(upperlbrcost);
             mylist.add(upperbdncost);
             mylist.add(upperovhcost);
             mylist.add(upperoutcost);
             return mylist;
         }
            
    public void getTotalCostRecursive(String mypart, double perqty, String bom)  {
        lastlevel++;
       
        String[] newpart = mypart.split("___");
        ArrayList<String> mylist = new ArrayList<String>();
        if (lastlevel == 1 && ! bom.isEmpty()) {
        mylist = OVData.getpsmstrlist(newpart[0], bom);
        } else {
        mylist = OVData.getpsmstrlist(newpart[0]);    
        }
        if (lastlevel > 1) {
                    thisovhcost = (parentqty * invData.getItemOvhCost(mypart));
                    ovhcost = ovhcost + thisovhcost;
                     thisoutcost = (parentqty * invData.getItemOvhCost(mypart));
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
            uppermtlcost = uppermtlcost + (invData.getItemMtlCost(newpart[0]));
            upperovhcost = upperovhcost + (invData.getItemOvhCost(newpart[0]));
            upperoutcost = upperoutcost + (invData.getItemOutCost(newpart[0]));
           
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
                    getTotalCostRecursive(value[1] + "___" + value[4] + "___" + value[3], parentqty, "");
                    lastlevel--;
                  } else {
                  parentqty = thisparent * Double.valueOf(value[3]);
                  thismtlcost = (parentqty * invData.getItemMtlCost(value[1]));
                  mtlcost = mtlcost + thismtlcost;
                  thisovhcost = (parentqty * invData.getItemOvhCost(value[1]));
                  ovhcost = ovhcost + thisovhcost;
                  thisoutcost = (parentqty * invData.getItemOutCost(value[1]));
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
                  thismtlcost = (parentqty * invData.getItemMtlCost(value[1]));
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
                    thisovhcost = (parentqty * invData.getItemOvhCost(mypart));
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
                   thisovhcost = (parentqty * invData.getItemOvhCost(value[1]));
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
                    thisoutcost = (parentqty * invData.getItemOvhCost(mypart));
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
                   thisoutcost = (parentqty * invData.getItemOutCost(value[1]));
                  outcost = outcost + thisoutcost;
              //   MainFrame.show(value[1] + " / " + thisovhcost + " / " + ovhcost);
                  }
           
              } 
        
        }
        lastlevel--;
        
    }
       
    
    
}
