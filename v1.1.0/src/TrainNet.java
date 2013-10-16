/*
    Erudite v1.1.0 Copyright(c) 2013 Adam A Lara
    * 
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


import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.xpath.XPath;

/**
 *
 * @author Adam
 */
public class TrainNet extends Thread{
    
    Element neurode;
    double learningRate;
    
 	public void run(){ //throws JDOMException
            
                try{
                if(learningRate < 0){   //auto-adapting learning rate
                    double numOuts;
                    String nid=neurode.getAttributeValue("N_ID");
                    java.util.List outList=XPath.newInstance("//SYNAPSE[@ORG_NEURODE='"+nid+"']").selectNodes(Erudite_gui.NNetMap);
                    if(outList!=null && outList.size()!=0){
                        numOuts=outList.size();
                    }
                    else{
                        numOuts=1/neurode.getChildren("SYNAPSE").size();
                    }
                    double adptLearningRate = java.lang.Double.parseDouble(neurode.getParentElement().getParentElement().getAttributeValue("NNET_V2")) + Math.abs(java.lang.Double.parseDouble(neurode.getAttributeValue("NNET_V4")) * numOuts);
                    learningRate = adptLearningRate;
                }
                else{
                    learningRate=java.lang.Double.parseDouble(neurode.getParentElement().getParentElement().getAttributeValue("NNET_V2"));
                }    
                adjustBias(neurode); 
                adjustWeights(neurode);
                
                }catch (Exception e) {e.printStackTrace(); }
                
        }
        
        
	public void adjustWeights(Element node)throws JDOMException{
            //each backward connection's weight to equal current weight + LEARNING_RATE * output of neuron at other end of connection * this neuron's errorGradient
            int lock = java.lang.Integer.parseInt(node.getParentElement().getParentElement().getAttributeValue("ADJUST_LOCK"));
            Iterator synapses=node.getChildren().iterator(); //iterate through incoming synapses and adjust weights
            do{ 
                Element currentSynapse=(Element) synapses.next();
                String OrgNodeID=currentSynapse.getAttributeValue("ORG_NEURODE");
                //if OrgNode !=input then continue else abort/return
                if (!"INPUT".equals(OrgNodeID) && lock!=1){
                Element OrgNode = (Element) XPath.selectSingleNode(Erudite_gui.NNetMap, "/NNETWORK/SUBNET/LAYER/NEURODE[@N_ID='"+OrgNodeID+"']");
                double weight = java.lang.Double.parseDouble(currentSynapse.getAttributeValue("WEIGHT")) + (learningRate * (java.lang.Double.parseDouble(OrgNode.getAttributeValue("ACTIVITY")) * java.lang.Double.parseDouble(node.getAttributeValue("NNET_V4")))) ;
                currentSynapse.setAttribute("WEIGHT", String.valueOf(weight));
                }
            }while(synapses.hasNext());            
	}   
        
	public void adjustBias(Element node)throws JDOMException{
            //adjusted bias = current bias + LEARNING_RATE * errorGradient
            int lock = java.lang.Integer.parseInt(node.getParentElement().getParentElement().getAttributeValue("ADJUST_LOCK"));
            Element currentSynapse=(Element) node.getChild("SYNAPSE");
            String OrgNodeID=currentSynapse.getAttributeValue("ORG_NEURODE");
            if (!"INPUT".equals(OrgNodeID) && lock!=1){
                double bias= java.lang.Double.parseDouble(node.getAttributeValue("BIAS")) + (learningRate * java.lang.Double.parseDouble(node.getAttributeValue("NNET_V4"))); //original ver
                node.setAttribute("BIAS", String.valueOf(bias));
            }            
	}         
    
}
