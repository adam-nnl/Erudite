/*
    Erudite v1.1.0 Copyright(c) 2013 Adam A Lara
    
    This software is dedicated to the pursuit of knowledge, 
    the generations before(Dad) and after(Harrison) me, 
    and the love of my life - Jord'an  <3

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.xpath.XPath;

public class Erudite_SW_trainCmd extends SwingWorker<Integer, String> {

    int cancelKey;
    
  private static void failIfInterrupted() throws InterruptedException {
    if (Thread.currentThread().isInterrupted()) {
      //throw new InterruptedException("Process interrupted before completion.");

    }
  }


  /** The text area where messages are written. */
  private final JEditorPane messagesTextArea;

  /**
   * Creates an instance of the worker
   * @param messagesTextArea
   *          The text area where messages are written
   */
  public Erudite_SW_trainCmd(final JEditorPane messagesTextArea) {
    this.messagesTextArea = messagesTextArea;
  }

  @Override
  protected Integer doInBackground() throws Exception {
    // The number of instances the word is found
    int matches=0;
        
        try {
            Erudite_gui.input=new double[Erudite_gui.inputNID.length];
            Erudite_gui.desiredOutput=new double[Erudite_gui.outputNID.length];
            if(Erudite_gui.jRadioButton3.isSelected()==true){      //train until cycles end
                int i=java.lang.Integer.parseInt(Erudite_gui.jTextField4.getText());
                int epochCount=0;
                double error;
                long time=System.currentTimeMillis();
                do{     
                   int example = 0;
                   Erudite_gui.jProgressBar2.setValue(0);
                   error=0;
                   matches=0;
                   int rndOrder = 0 + (int)(Math.random() * ((Erudite_gui.tsInput.length - 0) + 1));
                        for (int z = 0; z < Erudite_gui.tsInput.length; z++) {
                        for (int x = 0; x < Erudite_gui.tsInput[z].length; x++) { 
                            if(Erudite_gui.jCheckBox13.isSelected()){
                                Erudite_gui.input[x] = Erudite_gui.tsInput[(rndOrder + z) % Erudite_gui.tsInput.length][x];
                            }
                            else{
                                Erudite_gui.input[x] = Erudite_gui.tsInput[z][x];
                            }
                        }
                        for (int y = 0; y < Erudite_gui.tsOutput[z].length; y++) { 
                            if(Erudite_gui.jCheckBox13.isSelected()){
                                Erudite_gui.desiredOutput[y] = Erudite_gui.tsOutput[(rndOrder + z) % Erudite_gui.tsInput.length][y];
                            }
                            else{
                                Erudite_gui.desiredOutput[y] = Erudite_gui.tsOutput[z][y];
                            }
                        }                     
                        //XMLOutputter fmt = new XMLOutputter();
                        loadInputs(Erudite_gui.NNetMap);
                        runNNet(Erudite_gui.NNetMap);
                        getOutputs(Erudite_gui.NNetMap);
                        //publish(fmt.outputString(Erudite_gui.NNetMap));
                        trainNNet(Erudite_gui.NNetMap);
                        if(Integer.compare(correctOutputs(), Erudite_gui.outputNID.length)==0){
                            matches+=1;
                        }
                        error+=getAvgError();
                        clearNNet(Erudite_gui.NNetMap);
                        example++;
                        Erudite_gui.jProgressBar2.setValue(example);
                        Erudite_gui.jProgressBar2.setString(example + " of " + String.valueOf(Erudite_gui.tsInput.length) + " training examples processed");
                    }
                  i--;
                  error=error/Erudite_gui.tsInput.length;
                  epochCount++;
                  publish("<b>Epoch #" + String.valueOf(epochCount) + "  avg. error:</b> " + String.valueOf(error)+ "&emsp;&emsp;<b>" + matches + "</b>/" + Erudite_gui.tsInput.length +" <b>correct</b>");
                }while(i!=0 && cancelKey>0);
                Erudite_gui.jProgressBar2.setValue(Erudite_gui.tsInput.length);
                Erudite_gui.jProgressBar2.setString("Done.");
                if(Erudite_gui.jCheckBox4.isSelected()){
                   try{
                    SaveNet save = new SaveNet();
                    save.WriteFile(Erudite_gui.NNetMap, Erudite_gui.jComboBox1.getSelectedItem().toString());
                    }catch (Exception e) {
                    e.printStackTrace();
                    publish("Problem encountered while saving file:<br>" + e.toString());
                    }
                    publish("<b>File saved as:</b> " + Erudite_gui.jComboBox1.getSelectedItem().toString() + ".new.xml");
                }
                publish("<b>Time to train:</b> " + java.lang.String.valueOf((System.currentTimeMillis()-time))+"ms");
                
             } else if(Erudite_gui.jRadioButton2.isSelected()==true){      //train until acceptable error
                int epochCount=0;
                double error;
                double errorLimit = java.lang.Double.parseDouble(Erudite_gui.jTextField3.getText());
                long time=System.currentTimeMillis();
                do{
                    Erudite_gui.jProgressBar2.setValue(0);
                    error=0;
                    matches=0;
                    int example=0;
                    int rndOrder = 0 + (int)(Math.random() * ((Erudite_gui.tsInput.length - 0) + 1));
                    for (int z = 0; z < Erudite_gui.tsInput.length; z++) {
                        for (int x = 0; x < Erudite_gui.tsInput[z].length; x++) {
                            if(Erudite_gui.jCheckBox13.isSelected()){
                                Erudite_gui.input[x] = Erudite_gui.tsInput[(rndOrder + z) % Erudite_gui.tsInput.length][x];
                            }
                            else{
                                Erudite_gui.input[x] = Erudite_gui.tsInput[z][x];
                            }
                        }
                        for (int y = 0; y < Erudite_gui.tsOutput[z].length; y++) {
                            if(Erudite_gui.jCheckBox13.isSelected()){
                                Erudite_gui.desiredOutput[y] = Erudite_gui.tsOutput[(rndOrder + z) % Erudite_gui.tsInput.length][y];
                            }
                            else{
                                Erudite_gui.desiredOutput[y] = Erudite_gui.tsOutput[z][y];
                            }
                        }                           
                        //XMLOutputter fmt = new XMLOutputter();
                        loadInputs(Erudite_gui.NNetMap);
                        runNNet(Erudite_gui.NNetMap);
                        getOutputs(Erudite_gui.NNetMap);
                        trainNNet(Erudite_gui.NNetMap);
                        //publish(fmt.outputString(Erudite_gui.NNetMap));
                        if(Integer.compare(correctOutputs(), Erudite_gui.outputNID.length)==0){
                            matches+=1;
                        }                        
                        error+=getAvgError();
                        clearNNet(Erudite_gui.NNetMap);
                        example++;
                        Erudite_gui.jProgressBar2.setValue(example);
                        Erudite_gui.jProgressBar2.setString(example + " of " + String.valueOf(Erudite_gui.tsInput.length) + " training examples processed");
                    }
                    error=error/Erudite_gui.tsInput.length;
                    epochCount++;
                    publish("<b>Epoch #" +epochCount + "  avg. error:</b> " + String.valueOf(error)+ "&emsp;&emsp;<b>" + matches + "</b>/" + Erudite_gui.tsInput.length +" <b>correct</b>");
                }while(error > errorLimit && cancelKey>0);
                Erudite_gui.jProgressBar2.setValue(Erudite_gui.tsInput.length);
                Erudite_gui.jProgressBar2.setString("Done.");
                if(Erudite_gui.jCheckBox4.isSelected()){
                   try{
                    SaveNet save = new SaveNet();
                    save.WriteFile(Erudite_gui.NNetMap, Erudite_gui.jComboBox1.getSelectedItem().toString());
                    }catch (Exception e) {
                    e.printStackTrace();
                    publish("Problem encountered while saving file<br>" + e.toString());
                    }
                    publish("<b>File saved as:</b> " + Erudite_gui.jComboBox1.getSelectedItem().toString() + ".new.xml");
                }      
                publish("<b>Time to train:</b> " + java.lang.String.valueOf((System.currentTimeMillis()-time))+"ms");
                publish("<b>Total number of training epochs:</b> " + java.lang.String.valueOf(epochCount));
                
            } else if(Erudite_gui.jRadioButton1.isSelected()==true){      //train until percent correct
                int epochCount=0;
                double error;
                String pctCrct=null;
                //double errorLimit = java.lang.Double.parseDouble(Erudite_gui.jTextField3.getText());
                double pctCorrectThreshold = (java.lang.Double.parseDouble(Erudite_gui.jTextField1.getText()) / 100) * Erudite_gui.tsInput.length;
                long time=System.currentTimeMillis();
                do{
                    Erudite_gui.jProgressBar2.setValue(0);
                    error=0;
                    matches=0;
                    int example=0;
                    int rndOrder = 0 + (int)(Math.random() * ((Erudite_gui.tsInput.length - 0) + 1));
                    for (int z = 0; z < Erudite_gui.tsInput.length; z++) {
                        for (int x = 0; x < Erudite_gui.tsInput[z].length; x++) {
                            if(Erudite_gui.jCheckBox13.isSelected()){
                                Erudite_gui.input[x] = Erudite_gui.tsInput[(rndOrder + z) % Erudite_gui.tsInput.length][x];
                            }
                            else{
                                Erudite_gui.input[x] = Erudite_gui.tsInput[z][x];
                            }
                        }
                        for (int y = 0; y < Erudite_gui.tsOutput[z].length; y++) {
                            if(Erudite_gui.jCheckBox13.isSelected()){
                                Erudite_gui.desiredOutput[y] = Erudite_gui.tsOutput[(rndOrder + z) % Erudite_gui.tsInput.length][y];
                            }
                            else{
                                Erudite_gui.desiredOutput[y] = Erudite_gui.tsOutput[z][y];
                            }
                        }                           
                        //XMLOutputter fmt = new XMLOutputter();
                        loadInputs(Erudite_gui.NNetMap);
                        runNNet(Erudite_gui.NNetMap);
                        getOutputs(Erudite_gui.NNetMap);
                        trainNNet(Erudite_gui.NNetMap);
                        //publish(fmt.outputString(Erudite_gui.NNetMap));
                        if(Integer.compare(correctOutputs(), Erudite_gui.outputNID.length)==0){
                            matches+=1;
                        }                        
                        error+=getAvgError();
                        clearNNet(Erudite_gui.NNetMap);
                        example++;
                        Erudite_gui.jProgressBar2.setValue(example);
                        Erudite_gui.jProgressBar2.setString(example + " of " + String.valueOf(Erudite_gui.tsInput.length) + " training examples processed");
                        pctCrct = java.lang.Double.toString(roundr((((double)matches / Erudite_gui.tsInput.length) * 100), 2)) + "%";
                    }
                    error=error/Erudite_gui.tsInput.length;
                    epochCount++;
                    publish("<b>Epoch #" +epochCount + ":</b>&nbsp;" + matches + "/" + Erudite_gui.tsInput.length +" examples correct, <b>" + pctCrct + "</b>&emsp;" + String.valueOf(error) + " avg. error");
                }while(matches < pctCorrectThreshold && cancelKey>0);
                Erudite_gui.jProgressBar2.setValue(Erudite_gui.tsInput.length);
                Erudite_gui.jProgressBar2.setString("Done.");
                if(Erudite_gui.jCheckBox4.isSelected()){
                   try{
                    SaveNet save = new SaveNet();
                    save.WriteFile(Erudite_gui.NNetMap, Erudite_gui.jComboBox1.getSelectedItem().toString());
                    }catch (Exception e) {
                    e.printStackTrace();
                    publish("Problem encountered while saving file<br>" + e.toString());
                    }
                    publish("<b>File saved as:</b> " + Erudite_gui.jComboBox1.getSelectedItem().toString() + ".new.xml");
                }      
                publish("<b>Time to train:</b> " + java.lang.String.valueOf((System.currentTimeMillis()-time))+"ms");
                publish("<b>Total number of training epochs:</b> " + java.lang.String.valueOf(epochCount));
            }            
            

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

public static double roundr(double value, int sigDigits) {
    if (sigDigits < 0) throw new IllegalArgumentException();
    BigDecimal bigDecimal = new BigDecimal(value);
    bigDecimal = bigDecimal.setScale(sigDigits, BigDecimal.ROUND_HALF_UP);
    return bigDecimal.doubleValue();
}

public int correctOutputs() {
    int i=0;
    int correctOut=0;
    do{
        if(Math.abs(Erudite_gui.desiredOutput[i] - Erudite_gui.output[i]) < 0.13){  //java.lang.Double.parseDouble(Erudite_gui.jTextField3.getText())
            correctOut++;
        }
        i++;
    }while(i<Erudite_gui.output.length);    
    
    return correctOut;
}

public double getAvgError() throws JDOMException {
    int i = 0;
    double avgError=0.0;
    do{
        avgError += Math.abs(Erudite_gui.desiredOutput[i] - Erudite_gui.output[i]);
        i++;
    }while(i<Erudite_gui.output.length);
    avgError = avgError / Erudite_gui.output.length;
    return avgError;
}

public void trainNNet(Document nnet)  throws JDOMException {
    //1.search net map for all output layer neurodes, add to list, search for all hidden neurodes append to list, search for all input neurodes append to list
    //2.cycle through list, get EG. Cycle through list, MT adjust weights.
    java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(Erudite_gui.maxThreads); 
    java.util.List nodeList=XPath.newInstance("//LAYER[@LAYER_NAME = 'OUTPUT']/NEURODE").selectNodes(Erudite_gui.NNetMap);
    java.util.List Hnodes=XPath.newInstance("//LAYER[@LAYER_NAME = 'HIDDEN']/NEURODE").selectNodes(Erudite_gui.NNetMap);
    nodeList.addAll(Hnodes);
    
    Iterator nodeEGlist=nodeList.iterator(); //iterate through list and calculate error gradients
    do{ 
         Element currentNode=(Element) nodeEGlist.next(); 
         getErrorGradient(currentNode);
    }while(nodeEGlist.hasNext());
    
    Iterator nodeAWlist=nodeList.iterator();  //iterate through list and adjust weights and biases
    do{ 
         Element currentNode =(Element) nodeAWlist.next();
         TrainNet train = new TrainNet();
         if(Erudite_gui.jCheckBox2.isSelected()){   //auto-adapting learning rate
             train.learningRate= -1.0;
         }
         train.neurode=currentNode;
         executor.execute(train);
    }while(nodeAWlist.hasNext());
        
    try{ 
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (Exception e) {e.printStackTrace();}
}
    
private void getErrorGradient(Element node)throws JDOMException{
    //if-then-else for output/hidden/input layers, if-then for transfer functions, compute error gradient and update nnet map
    String layerType = node.getParentElement().getAttributeValue("LAYER_NAME").toString();
    int xfer=Integer.parseInt(node.getParentElement().getAttributeValue("TRANSFER_FUNCTION"));
    String nid = node.getAttributeValue("N_ID").toString();
    if(layerType.equals("OUTPUT")){ //if computing output neurode error gradient
         if(xfer==1){    //if hyperbolic tangent transfer function
            for(int i = 0; i <Erudite_gui.outputNID.length;i++){
                if(nid.equals(Erudite_gui.outputNID[i])){
                    double error = Erudite_gui.desiredOutput[i] - Erudite_gui.output[i];
                    double errorGradient = error *  ((1- Erudite_gui.output[i]) * (1 + Erudite_gui.output[i]));
                    node.setAttribute("NNET_V4", String.valueOf(errorGradient));
                }
            }             
         }
         else if(xfer==2){   //if sigmoid transfer function function
            for(int i = 0; i <Erudite_gui.outputNID.length;i++){
                if(nid.equals(Erudite_gui.outputNID[i])){
                    double error = Erudite_gui.desiredOutput[i] - Erudite_gui.output[i];
                    double errorGradient = error * (Erudite_gui.output[i] * (1 - Erudite_gui.output[i]));
                    node.setAttribute("NNET_V4", String.valueOf(errorGradient));              
                }
            }                
         }         
    }
    else if(layerType.equals("HIDDEN") || layerType.equals("INPUT")){  //if computing hidden or input neurode error gradient
        if (xfer==1){
            double sumWouts = 0.0;
            double nodeActivity = java.lang.Double.parseDouble(node.getAttributeValue("ACTIVITY"));
            java.util.List outputs = XPath.newInstance("//SYNAPSE[@ORG_NEURODE = '"+nid+"']").selectNodes(Erudite_gui.NNetMap);
            Iterator synapseO=outputs.iterator();
            do{ 
                Element currentNode =(Element) synapseO.next();
                sumWouts += java.lang.Double.parseDouble(currentNode.getAttributeValue("WEIGHT")) * java.lang.Double.parseDouble(currentNode.getParentElement().getAttributeValue("NNET_V4"));
            }while(synapseO.hasNext());            
            double errorGradient =  sumWouts * ((1-nodeActivity) * (1+nodeActivity));
            node.setAttribute("NNET_V4", String.valueOf(errorGradient));
        }
        else if(xfer==2){ 
            double sumWouts = 0.0;
            double nodeActivity = java.lang.Double.parseDouble(node.getAttributeValue("ACTIVITY"));
            java.util.List outputs = XPath.newInstance("//SYNAPSE[@ORG_NEURODE = '"+nid+"']").selectNodes(Erudite_gui.NNetMap);
            Iterator synapseO=outputs.iterator();
            do{ 
                Element currentNode =(Element) synapseO.next();
                sumWouts += java.lang.Double.parseDouble(currentNode.getAttributeValue("WEIGHT")) * java.lang.Double.parseDouble(currentNode.getParentElement().getAttributeValue("NNET_V4"));
            }while(synapseO.hasNext());            
            double errorGradient = sumWouts * (nodeActivity * (1-nodeActivity));
            node.setAttribute("NNET_V4", String.valueOf(errorGradient));          
        }
            
    }

}  

public void clearNNet(Document NNetMap)throws JDOMException{
java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(Erudite_gui.maxThreads);  
        java.util.List nodes=XPath.newInstance("//NEURODE").selectNodes(NNetMap);
        Iterator itNeurodes=nodes.iterator();
     		do{
                          Element CurrentNode=(Element) itNeurodes.next();
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
