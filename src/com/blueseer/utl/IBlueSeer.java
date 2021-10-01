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
package com.blueseer.utl;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

/**
 *
 * @author Terry Evans Vaughn
 */
public interface IBlueSeer {
        
    public void initvars(String[] x);
    
    public void setPanelComponentState(Object panel, boolean b);
    
    public void setComponentDefaultValues();
    
    // NOTE!!  the below methods that return a String[] array return a 2 element String array....element 1 contains either '0' or '1'.  
    // They are defined as follows:  '0' == Success.....'1' == Error.   2nd element is arbitrary description
    // Example of success:   String[] somearray = new String[]{"0", "Yay!!!  Something good happened"}; 
    // Example of bad:       String[] somearray = new String[]{"1", "Crap!!!! Another method bites the dust"}; 
    public String[] getRecord(String[] x);
   
    public String[] addRecord(String[] x);
    
    public String[] updateRecord(String[] x);
    
    public String[] deleteRecord(String[] x);
    
    public boolean validateInput(String e);
    
    public String[] setAction(int x);
        
    public void newAction(String x);
    
    public void executeTask(String x, String[] y);
   
    
}
