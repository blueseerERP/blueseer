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
package com.blueseer.utl;

import bsmf.MainFrame;
import javax.swing.SwingWorker;

/**
 *
 * @author TerryVa
 */
public abstract class DBTask extends SwingWorker<String[], Void> {
        /*
         * Executed in background thread.
         */
          String type = "";
          String key = "";
          
        
          public abstract String[] addRecord(String key);
          public abstract String[] updateRecord(String key);
          public abstract String[] deleteRecord(String key);
          public abstract String[] initvars(String key);
          
          public DBTask(String type, String key) { 
              this.type = type;
              this.key = key;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            
             switch(this.type) {
                case "add":
                    message = addRecord(key);
                    break;
                case "edit":
                    message = updateRecord(key);
                    break;
                case "delete":
                    message = deleteRecord(key);    
                    break;
                default:
                    message = new String[]{"1", "unknown action"};
            }
            
            return message;
        }
 
        /*
         * Executed in event dispatch thread
         */
        public void done() {
            try {
            String[] message = get();
           
            BlueSeerUtils.endTask(message);
           if (this.type.equals("delete")) {
             initvars("");  
           }  else {
             initvars(key);  
           }
           
            
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
   
}
