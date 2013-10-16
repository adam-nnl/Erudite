/*
    Erudite v1.1.0 Copyright(c) 2013 Adam A Lara
    This software is dedicated to the pursuit of knowledge, the generations before(Dad) and after    (Harrison) me, and the love of my life- Jord'an  <3

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JEditorPane;
import javax.swing.SwingWorker;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.xpath.XPath;

public class Erudite_SW_runCmd extends SwingWorker<Integer, String> {

int cancelKey;    
    
  private static void failIfInterrupted() throws InterruptedException {
    if (Thread.currentThread().isInterrupted()) {
      throw new InterruptedException("Process interrupted before completion.");
    }
  }


  /** The text area where messages are written. */
  private final JEditorPane messagesTextArea;

  /**
   * Creates an instance of the worker
   * @param messagesTextArea
   *          The text area where messages are written
   */
  public Erudite_SW_runCmd(final JEditorPane messagesTextArea) {
    this.messagesTextArea = messagesTextArea;
  }

  @Override
  protected Integer doInBackground() throws Exception {
    // The number of instances the word is found
    int matches = 0;
        try {
            Erudite_gui.input=new double[Erudite_gui.inputNID.length];
            long time=System.currentTimeMillis();
            int example;
            do{     
               example = 0;
               Erudite_gui.jProgressBar2.setValue(0);
               for (int z = 0; z < Erudite_gui.inputRunset.length; z++) {
                   for (int x = 0; x < Erudite_gui.inputRunset[z].length; x++) { 
                       Erudite_gui.input[x] = Erudite_gui.inputRunset[z][x];
                   }
               //XMLOutputter fmt = new XMLOutputter();
               loadInputs(Erudite_gui.NNetMap);
               runNNet(Erudite_gui.NNetMap);
               getOutputs(Erudite_gui.NNetMap);
               //publish(fmt.outputString(Erudite_gui.NNetMap));
               publish("<br><b>Processing run: " + Erudite_gui.rsCNAME[example] + "</b>");
               for (int x = 0; x < Erudite_gui.output.length; x++) { //display outputs
                publish("<b>Final output for " + Erudite_gui.outputNID[x] + "(" + Erudite_gui.outputCNAME[x] + "):</b> " + String.valueOf(Erudite_gui.output[x]));
               }               
               clearNNet(Erudite_gui.NNetMap);
               example++;
               Erudite_gui.jProgressBar2.setValue(example);
               Erudite_gui.jProgressBar2.setString(example + " of " + String.valueOf(Erudite_gui.inputRunset.length) + " run examples processed");
               }
            }while(example < Erudite_gui.inputRunset.length && cancelKey>0); 
            Erudite_gui.jProgressBar2.setValue(Erudite_gui.inputRunset.length);
            Erudite_gui.jProgressBar2.setString("Done.");
            publish("<b>Time to process:</b> " + java.lang.String.valueOf((System.currentTimeMillis()-time))+"ms");
        } catch (Exception e) {e.printStackTrace();}    
    
    return matches;
  }

  @Override
  protected void process(final List<String> chunks) {
    // Updates the messages text area
   try {
      HTMLEditorKit kit = (HTMLEditorKit)messagesTextArea.getEditorKit();
      HTMLDocument doc = (HTMLDocument)messagesTextArea.getDocument();     
      for (final String string : chunks) {
        kit.insertHTML(doc, messagesTextArea.getCaretPosition(), string + "<br>", 0, 0,null);  
      }
    } catch (Exception e) {e.printStackTrace();} 
  }
  
private void loadInputs(Document NNetMap) {
     try {
    	//use XPath to find all synapse elements recieving input data
        int iN=0;
        do{
            String inNodeID=Erudite_gui.inputNID[iN];
            Element inNode = (Element) XPath.selectSingleNode(Erudite_gui.NNetMap, "/NNETWORK/SUBNET/LAYER/NEURODE[@N_ID='"+inNodeID+"']");   
            inNode.setAttribute("ACTIVE","1");
            inNode.setAttribute("ACTIVITY", String.valueOf(Erudite_gui.input[iN]));
            iN++;      
        }while(iN<Erudite_gui.input.length);         

    } catch (Exception e) {
        e.printStackTrace();
    }
    
 }

public void getOutputs(Document NNetMap) throws JDOMException {
    Erudite_gui.output=new double[Erudite_gui.outputNID.length];
    int oN=0;
    do{
      String outNodeID=Erudite_gui.outputNID[oN];
      Element outNode = (Element) XPath.selectSingleNode(Erudite_gui.NNetMap, "/NNETWORK/SUBNET/LAYER/NEURODE[@N_ID='"+outNodeID+"']");   
      Erudite_gui.output[oN]=java.lang.Double.parseDouble(outNode.getAttributeValue("ACTIVITY"));
      oN++;      
    }while(oN<Erudite_gui.output.length);

}

public void runNNet(Document NNetMap) throws JDOMException {
java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(Erudite_gui.maxThreads);   
java.util.List nodes=XPath.newInstance("//NEURODE[SYNAPSE/@ORG_NEURODE != 'INPUT']").selectNodes(NNetMap);
                Iterator itNeurodes=nodes.iterator();
     		do{	
                    EvaluateNeurode evalN = new EvaluateNeurode();
                    Element CurrentNode=(Element) itNeurodes.next();		//get current NEURODE						
                    evalN.NNetMap=Erudite_gui.NNetMap;
                    evalN.neurode=CurrentNode;   //get summed input for current NEURODE
                    executor.execute(evalN);
    		}while(itNeurodes.hasNext());   

        try{ 
            executor.shutdown();
            executor.awaitTermination(2, TimeUnit.SECONDS);
             } catch (Exception e) {e.printStackTrace();} 
}

public void clearNNet(Document NNetMap)throws JDOMException{
java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(Erudite_gui.maxThreads);  
        java.util.List nodes=XPath.newInstance("//NEURODE").selectNodes(NNetMap);
        Iterator itNeurodes=nodes.iterator();
     		do{
                          Element CurrentNode=(Element) itNeurodes.next();		//get current NEURODE	
                          ClearNode clear=new ClearNode();
                          clear.neurode=CurrentNode;
                          executor.execute(clear);
                     }while(itNeurodes.hasNext());
                      try{   
                          executor.shutdown();
                          executor.awaitTermination(1, TimeUnit.SECONDS);
                    } catch (Exception e) {e.printStackTrace();} 
 
}
  
  
  
}
