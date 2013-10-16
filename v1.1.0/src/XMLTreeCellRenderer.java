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


import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class XMLTreeCellRenderer extends DefaultTreeCellRenderer {
    
    //colors for tree items
    private final Color elementColor = new Color(0, 0, 128);
    private final Color textColor = new Color(0, 128, 0);
 
    //remove icons
    public XMLTreeCellRenderer() {
        setOpenIcon(new ImageIcon("open.gif"));
        setClosedIcon(new ImageIcon("closed.gif"));
        setLeafIcon(new ImageIcon("leaf.gif"));
    }
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JDOMAdapterNode adapterNode = (JDOMAdapterNode)value;
        if(adapterNode.node.isRootElement()) {
            value = adapterNode.node.getName();
        } else if(adapterNode.node.getChildren().size() > 0) {
            value = adapterNode.node.getName();
        } else {
            value = adapterNode.node.getName() +" ["+adapterNode.node.getTextTrim()+"]";
        }
        
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
        if(!selected) {
            if(adapterNode.node.getTextTrim().length() == 0) {
                setForeground(elementColor);
            } else {
                setForeground(textColor);
            }
        }
        
        return this; 
    }
}
