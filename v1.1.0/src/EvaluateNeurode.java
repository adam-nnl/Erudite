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

import java.util.*;
import javax.swing.JOptionPane;
import org.jdom2.*;
import org.jdom2.xpath.*;


public class EvaluateNeurode extends Thread{
    
    Element neurode;
    Document NNetMap;
    
	public void run(){ //throws JDOMException
            
                try{
                    
                double dInputSignal=sumInput(neurode);
		output(neurode, dInputSignal);
                
                }catch (Exception e) {e.printStackTrace();}
                
        }
        
        public synchronized double sumInput(Element node) throws JDOMException{
		List SynIns=node.getChildren("SYNAPSE");
                double bias=java.lang.Double.parseDouble(node.getAttributeValue("BIAS"));
		double sumInputs= 0 + bias;
		for (Iterator itSI=SynIns.iterator();itSI.hasNext();){
			Element SynapseI=(Element)itSI.next();    	
     			double weight=java.lang.Double.parseDouble(SynapseI.getAttributeValue("WEIGHT"));    			
     			String OrgNodeID=SynapseI.getAttributeValue("ORG_NEURODE");
                        Element OrgNode = (Element) XPath.selectSingleNode(Erudite_gui.NNetMap, "/NNETWORK/SUBNET/LAYER/NEURODE[@N_ID='"+OrgNodeID+"']"); 
     			int activeTF= java.lang.Integer.parseInt(OrgNode.getAttributeValue("ACTIVE"));
     			if(activeTF==1){
     				double value =java.lang.Double.parseDouble(OrgNode.getAttributeValue("ACTIVITY"));
     				sumInputs += weight*value;
     			}                        
                        else if(activeTF==-1){ 
                                int TFcheck = java.lang.Integer.parseInt(OrgNode.getAttributeValue("ACTIVE"));
                                while ( TFcheck != 1 ){
                                    TFcheck = java.lang.Integer.parseInt(OrgNode.getAttributeValue("ACTIVE"));
                                }
                                double value =java.lang.Double.parseDouble(OrgNode.getAttributeValue("ACTIVITY"));
                                sumInputs += weight*value;
     			}
     			else if(activeTF==0){
     				sumInputs += 0;
     			}

		}
		return sumInputs;
		
	}

	public double transferFunction(double WeightedInput){
		double squashedValue=Math.tanh(WeightedInput);
		return squashedValue;		
	}
        
	public double transferFunction2(double WeightedInput){
                double squashedValue = 1 / (1 + Math.exp(-1*WeightedInput));
		return squashedValue;		
	}       
	 
	public synchronized void output(Element node, double summedInputs) throws JDOMException{
                    int xfer=Integer.parseInt(node.getParentElement().getAttributeValue("TRANSFER_FUNCTION"));
                    if(xfer==1){
                    double output=transferFunction(summedInputs);
                    node.setAttribute("ACTIVITY", String.valueOf(output));
                    node.setAttribute("ACTIVE", "1");
                    }
                    else if(xfer==2){
                    double output=transferFunction2(summedInputs);
                    node.setAttribute("ACTIVITY", String.valueOf(output));  
                    node.setAttribute("ACTIVE", "1");
                    }

    }          
	 
}
