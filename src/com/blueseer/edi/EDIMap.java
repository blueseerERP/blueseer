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
package com.blueseer.edi;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.smb.SmbException;

/**
 *
 * @author terryva
 */
public abstract class EDIMap implements EDIMapi {
    
    String[] envelope = null;
         
    public static  int segcount = 0;

    DateFormat isadfdate = new SimpleDateFormat("yyMMdd");
    DateFormat gsdfdate = new SimpleDateFormat("yyyyMMdd");
    DateFormat isadftime = new SimpleDateFormat("HHmm");
    DateFormat gsdftime = new SimpleDateFormat("HHmm");
    Date now = new Date();

    public static String ISA = "";
    public static String GS = "";
    public static String GE = "";
    public static String IEA = "";

     public static String[] isaArray = new String[17];
     public static String[] gsArray = new String[9];
     public static String[] stArray = new String[3];
     public static String[] seArray = new String[3];
     public static String[] geArray = new String[3];
     public static String[] ieaArray = new String[3];

     public static String ST = "";
     public static String SE = "";

     public static String sender = "";
     public static String map = "";
     public static boolean isOverride = false;
     public static String doctype = "";

     public static String infile = "";
     public static String outdir = "";
     public static String outfile = "";
     public static String outputfiletype = "";
     public static String outputdoctype = "";
     public static String filename = "";
     public static String isactrl = "";
     public static String gsctrl = "";
     public static String stctrl = "";
     public static String ref = "";

     public static String header = "";
     public static String detail = "";
     public static String trailer = "";

     public static String isa06 = "";
     public static String isa08 = "";
     public static String isa13 = "";
     public static String isa09 = "";

     public static String gs02 = "";
     public static String gs03 = "";

     public static ArrayList<String> H = new ArrayList();
     public static ArrayList<String> D = new ArrayList();
     public static ArrayList<String> T = new ArrayList();

     public static String sd = "";
     public static String ed = "";
     public static String ud = "";

     public static String content = "";

    public static Map<String, ArrayList<String[]>> HASH = new  LinkedHashMap<String, ArrayList<String[]>>();
    public static Map<String, String[]> mappedInput = new  LinkedHashMap<String, String[]>();
    public static Map<String, ArrayList<String[]>> OSF = new  LinkedHashMap<String, ArrayList<String[]>>();
    public static ArrayList<String[]> ISF = new  ArrayList<String[]>();
    public static Map<String, HashMap<String,String>> OMD = new LinkedHashMap<String, HashMap<String,String>>();
    public static Map<String, HashMap<String,String>> IMD = new LinkedHashMap<String, HashMap<String,String>>();


     public static boolean isSet(ArrayList list, Integer index) {
     return index != null && index >=0 && index < list.size() && list.get(index) != null;
     }

    public static boolean isSet(String[] list, Integer index) {
     return index != null && index >=0 && index < list.length && list[index] != null;
     }

    public void setOutPutFileType(String filetype) {
        outputfiletype = filetype;
    }
    public void setOutPutDocType(String doctype) {
        outputdoctype = doctype;
    }

    public void setISA (String[] isa) {
         isa06 = isa[6].trim();
         isa08 = isa[8].trim();
         isa09 = isa[9].trim();
         isa13 = isa[13].trim();
     }

    public void setGS (String[] gs) {
         gs02 = gs[2].trim();
         gs03 = gs[3].trim();
     }

    public void setControl(String[] c) {
        sender = c[0];
        doctype = c[1];
        map = c[2];
        infile = c[3];
        isactrl = c[4];
        gsctrl = c[5];
        stctrl = c[6];
        ref = c[7];
        outfile = c[8];
        sd = delimConvertIntToStr(c[9]);
        ed = delimConvertIntToStr(c[10]);
        ud = delimConvertIntToStr(c[11]);
        isOverride = Boolean.valueOf(c[12]); // isOverrideEnvelope
     }

    public String delimConvertIntToStr(String intdelim) {
    String delim = "";
    int x = Integer.valueOf(intdelim);
    delim = String.valueOf(Character.toString((char) x));
    return delim;
  }

     
     public void setOutPutEnvelopeStrings(String[] c) { 

         if ( ! isOverride) {  // if not override...use internal partner / doc lookup for envelope info
           envelope = EDI.generateEnvelope(sender, doctype); // envelope array holds in this order (isa, gs, ge, iea, filename, controlnumber, gsctrlnbr)
           ISA = envelope[0];
           GS = envelope[1];
           GE = envelope[2];
           IEA = envelope[3];
           filename = envelope[4];
           outfile = filename;  
           isactrl = envelope[5];
           gsctrl = envelope[6];
           stctrl = String.format("%09d", Integer.valueOf(gsctrl));
           ST = "ST" + ed + doctype + ed + stctrl ;
           SE = "SE" + ed + String.valueOf(segcount) + ed + stctrl;  
           } else {
             // you can override elements within the envelope xxArray fields at this point....or merge into segment string
             // need to figure out what kind of error this bullshit is....
             ISA = c[13];
             GS = c[14];
             GE = "GE" + ed + "1" + ed + c[5];
             IEA = "IEA" + ed + "1" + ed + c[4];
             ST = "ST" + ed + doctype + ed + c[6]; 
             SE = "SE" + ed + "1" + ed + c[6];

             updateISA(9,""); // set date to now
             updateISA(10,"");  // set time to now

           
             header = "";
             detail = "";
             trailer = "";
             content = "";
           }

     }

     public String getISA(int i) {
         if (i > 16) {
             return "";
         }
       isaArray = ISA.split(EDI.escapeDelimiter(ed), -1);  
       return isaArray[i];
     }

     public String getGS(int i) {
         if (i > 8) {
             return "";
         }
       gsArray = GS.split(EDI.escapeDelimiter(ed), -1);  
       return gsArray[i];
     }


     public void updateISA(int i, String value) {
         isaArray = ISA.split(EDI.escapeDelimiter(ed), -1);
         switch (i) {
           case 1 :
             isaArray[i] = String.format("%-2s", value);
             break;
           case 2 :
             isaArray[i] = String.format("%-10s", value);
             break;
           case 3 :
             isaArray[i] = String.format("%-2s", value);
             break;
           case 4 :
             isaArray[i] = String.format("%-10s", value);
             break;
           case 5 :
             isaArray[i] = String.format("%-2s", value);
             break; 
           case 6 :
             isaArray[i] = String.format("%-15s", value);
             break;
           case 7 :
             isaArray[i] = String.format("%-2s", value);
             break;   
           case 8 :
             isaArray[i] = String.format("%-15s", value);
             break;  
           case 9 :
             isaArray[i] = isadfdate.format(now);
             break;  
           case 10 :
             isaArray[i] = isadftime.format(now);
             break;  
           case 11 :
             isaArray[i] = String.format("%-1s", value);  
             break;  
           case 12 :
             isaArray[i] = String.format("%-5s", value);  
             break;  
           case 13 :
             isaArray[i] = String.format("%-9s", value);  
             break;  
           case 14 :
             isaArray[i] = String.format("%-1s", value);  
             break;  
           case 15 :
             isaArray[i] = String.format("%-1s", value);  
             break; 
             case 16 :
             isaArray[i] = String.format("%-1s", value);  
             break; 
           default :
           break;
   }

         ISA = String.join(ed,isaArray);
     }

     public void updateGS(int i, String value) {
         if (i > 8) {
             return;
         }
         gsArray = GS.split(EDI.escapeDelimiter(ed), -1);
         gsArray[i] = String.valueOf(value);
         GS = String.join(ed,gsArray);
     }

     public void updateSE() {
         seArray = SE.split(EDI.escapeDelimiter(ed), -1);
         seArray[1] = String.valueOf(segcount);
         SE = String.join(ed,seArray);
     }

     public void updateGE() {
         geArray = GE.split(EDI.escapeDelimiter(ed), -1);
         geArray[1] = String.valueOf(segcount);
         GE = String.join(ed,geArray);
     }

     public String[] packagePayLoad(String[] c) {
         String[] x = new String[]{"success","transaction mapped successfully"};  // error, messg  ... error = 0 = success ; error = 1 = error
     
        
        if (outputfiletype.equals("X12")) {
           setOutPutEnvelopeStrings(c);
        }
        
         // concatenate all output strings to string variable 'content' 
        writeOMD();
        
        // get TP/Doc defaults
        String[] tp = OVData.getEDITPDefaults(sender, doctype);

        // create out batch file name
         int filenumber = OVData.getNextNbr("edifile");
         String batchfile = "X" + String.format("%07d", filenumber);

        if (outdir.isEmpty()) {
            outdir = tp[9];
            if (outdir.isEmpty()) {
                outdir = OVData.getEDIOutDir();
            }
        }

        if (outfile.isEmpty()) {
            outfile = tp[10] + String.format("%07d", filenumber) + "." + tp[11];
        }

        c[15] = outputdoctype;
        c[25] = batchfile;
        c[27] = outdir;
        c[29] = outputfiletype;


     System.out.println(String.join(",", c));

        // error handling
        if (batchfile.isEmpty()) {
            x[0] = "error";
            x[1] = "batch file is empty";
            return x;
        }
        if (outfile.isEmpty()) {
            x[0] = "error";
            x[1] = "batch file is empty";
            return x;
        }
        if (outputfiletype.isEmpty()) {
            x[0] = "error";
            x[1] = "output file type unknown";
            return x;
        }
            try {
                // Write output batch file
                EDI.writeFile(content, OVData.getEDIBatchDir(), batchfile);
                // Write to outfile
                EDI.writeFile(content, outdir, outfile);  // you can override output directory by assign 2nd parameter here instead of ""
            } catch (SmbException ex) {
                MainFrame.bslog(ex);
            } catch (IOException ex) {
                MainFrame.bslog(ex);
            }

        // need confirmation file was created    
        File file = new File(outdir + "/" + outfile);

        if (! file.exists()) {
            x[0] = "error";
            x[1] = "output file not created: " + file.getPath().toString();
            return x; 
        } else {
            c[23] = "success";
        }

     return x;
     }

    
     public static void mapSegment(String segment, String x, String y) {
    	 String[] z = new String[] {x,y};
    	 // get old arraylist and add to it
    	 ArrayList<String[]> old = new ArrayList<String[]>();
    	 if (HASH.get(segment) != null) {
    		 old = HASH.get(segment);
    	 }
    	 old.add(z);
    	 HASH.put(segment, old);
     }
     
     public static void commitSegment(String segment, int p) {
    	 // loop through HASH and create t for this segment
    	 HashMap<String, String> t = new LinkedHashMap<String,String>();
    	 Map<String, ArrayList<String[]>> X = new  LinkedHashMap<String, ArrayList<String[]>>(HASH);
    	 for (Map.Entry<String, ArrayList<String[]>> z : X.entrySet()) {
    		 if (z.getKey().equals(segment)) {
    			 ArrayList<String[]> k = z.getValue();
    			 for (String[] g : k) {
    				 t.put(g[0], g[1]);
    			 }
    			 
    		 }
    	 }
    	// HashMap<String, String> t = new HashMap<String,String>(j);
    	 if (! OMD.containsKey(segment)) {
    		OMD.put(segment + ":" + p, t);
    	 }	
         HASH.clear();
     }
     
     public static Map<String, ArrayList<String[]>> readOSF(String adf) throws IOException {
	        Map<String, ArrayList<String[]>> hm = new LinkedHashMap<String, ArrayList<String[]>>();
	        List<String[]> list = new ArrayList<String[]>();
	        Set<String> set = new LinkedHashSet<String>();
	        File cf = new File(adf);
	    	BufferedReader reader =  new BufferedReader(new FileReader(cf)); 
			String line;
			while ((line = reader.readLine()) != null) {
				if (! line.isEmpty()) {
				String[] t = line.split(",",-1);
				list.add(t);
				set.add(t[0]);
				}
			}
			reader.close();
			
			
			for (String s : set) {
				ArrayList<String[]> x = new ArrayList<String[]>();
				for (String[] ss : list) {
					if (ss[0].equals(s)) {
						x.add(ss);
					}
				}
				hm.put(s, x);
			}
			
			
	        return hm;
	    }

     public static ArrayList<String[]> readISF(String[] c, String ifile) throws IOException {
		String[] s = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		File cf = new File(ifile);
    	BufferedReader reader =  new BufferedReader(new FileReader(cf)); 
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) {
				continue;
			}
			if (! line.isEmpty()) {
			String[] t = line.split(",",-1);
			list.add(t);
			}
		}
		reader.close();
		
		
		return list;
	}
     
     public static Map<String, HashMap<String, String>> readIMD(String adf, ArrayList<String> doc) throws IOException {
	        Map<String, HashMap<String, String>> hm = new LinkedHashMap<String, HashMap<String, String>>();
	        HashMap<String, String> data = new LinkedHashMap<String, String>();
	        
                List<String[]> list = new ArrayList<String[]>();
	        Set<String> set = new LinkedHashSet<String>();
	        File cf = new File(adf);
	    	BufferedReader reader =  new BufferedReader(new FileReader(cf)); 
			String line;
			while ((line = reader.readLine()) != null) {
				if (! line.isEmpty()) {
				String[] t = line.split(",",-1);
				list.add(t);
				set.add(t[0]);
				}
			}
			reader.close();
			
                        for (String r : doc) {
                            for (String s : set) {
                                if (r.startsWith(s)) {
                                    // got def of data record...now fill fields
                                    int start = 0;
                                   
                                    for (String[] ss : list) {
					if (ss[0].equals(s)) {
                                              //  System.out.println("field:" + ss[1] + " " + start + "/" + (Integer.valueOf(ss[3]) + start));
						if (start == 0) {
                                                    start = ss[0].length();
                                                }
                                                data.put(ss[1], r.substring(start,(Integer.valueOf(ss[3]) + start)));
					        start += Integer.valueOf(ss[3]);
                                                
                                        }
				    }
                                hm.put(s, data);
                                break;
                                }
                            }
                        }
			
	        return hm;
	    }
     
     public static String[] writeOMD() {
          String[] r = new String[2];
    	 String segment = "";
    	 content = "";
    	 Map<String, HashMap<String,String>> MD = new LinkedHashMap<String, HashMap<String,String>>(OMD);
    		
    	 
    	 for (Map.Entry<String, HashMap<String,String>> z : MD.entrySet()) {
 		//	ArrayList<String[]> fields = z.getValue();
 			
                // write out all fields of this segment
                HashMap<String,String> mapValues = MD.get(z.getKey());
        //	System.out.println("loopentrycount:" + mapValuesLoops.keySet());
                // loop through integers

                        segment = z.getKey().split(":")[0];  // start with landmark

                        ArrayList<String[]> fields = OSF.get(segment);

                        for (String[] f : fields) {
                                if (f[4].equals("+")) {
                                        f[4] = "";
                                }
                                String format = "%" + f[4] + f[3] + "s";

                                // overlay with values that were actually assigned...otherwise blanks
                                if (mapValues.containsKey(f[1])) {
                                        if (mapValues.get(f[1]).length() > Integer.valueOf(f[3])) {
                                                segment += String.format(format, mapValues.get(f[1]).substring(0, Integer.valueOf(f[3]))); // properly formatted
                                        } else {
                                                segment += String.format(format, mapValues.get(f[1])); // properly formatted
                                        }

                                } else {
                                        segment += String.format(format, ""); // properly formatted
                                }
                        }
                        content += segment + "\n";
                        segment = ""; // reset the segment string


 			
 			
 			
 		}
    	 
    	OMD.clear();
        IMD.clear();
        HASH.clear();
        ISF.clear();
        OSF.clear();
    	 
    	 return r;
     }
     
     public static LinkedHashMap<String, String[]> mapInput(String[] c, ArrayList<String> data, ArrayList<String[]> ISF) throws IOException {
         LinkedHashMap<String,String[]> mappedData = new LinkedHashMap<String,String[]>();
                HashMap<String,Integer> keycount = new HashMap<String,Integer>();
                String parenthead = "";
                for (String s : data) {
                        String[] x = s.split("\\*",-1);
                        for (String[] z : ISF) {
                                if (x[0].equals(z[0])) {
                                        boolean foundit = false;
                                        String[] temp = parenthead.split(":");
                                        for (int i = temp.length - 1; i >= 0; i--) {
                                                if (z[1].compareTo(temp[i]) == 0) {
                                                        foundit = true;
                                                        String[] newarray = Arrays.copyOfRange(temp, 0, i + 1);
                                                        parenthead = String.join(":", newarray);							
                                                        break;
                                                }
                                        }
                                        if (! foundit) {
                                        continue;	
                                        } else {
                                                String keyreference = parenthead + ":" + x[0];
                                                if (keycount.containsKey(keyreference)) {
                                                        int g = keycount.get(keyreference);
                                                        g++;
                                                        keycount.put(keyreference, g);
                                                } else {
                                                        keycount.put(keyreference, 1);
                                                }

                                                mappedData.put(parenthead + ":" + x[0] + "+" + keycount.get(keyreference) , x);
                                                if (z[3].equals("yes")) {
                                                        parenthead = parenthead + (":" + z[0]);
                                                }
                                                break;
                                        }
                                }
                        }
                }
                return mappedData;
 }
	
     public static String getInput(String segment, Integer loop, Integer element) {
         String x = "";
         
         return x;
     }
     
     
}
