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

import org.jdom2.Element;

public class JDOMAdapterNode {
    
    /** the Element encapsulated by this node */
    public Element node;

    /**
     * Creates a new instance of the JDOMAdapterNode class
     * @param Element node
     */
    public JDOMAdapterNode(Element node) {
        this.node = node;
    }

    /**
     * Finds index of child in this node.
     * 
     * @param child The child to look for
     * @return index of child, -1 if not present (error)
     */
    public int index(JDOMAdapterNode child) {

        int count = childCount();
        for (int i = 0; i < count; i++) {
            JDOMAdapterNode n = this.child(i);
            if (child.node == n.node) {
                return i;
            }
        }
        return -1; // Should never get here.
    }

    /**
     * Returns an adapter node given a valid index found through
     * the method: public int index(JDOMAdapterNode child)
     * 
     * @param searchIndex find this by calling index(JDOMAdapterNode)
     * @return the desired child
     */
    public JDOMAdapterNode child(int searchIndex) {
        Element child = (Element)node.getChildren().get(searchIndex);
        return new JDOMAdapterNode(child);
    }
    
    public String getName(int searchIndex) {
        Element child = (Element)node.getChildren().get(searchIndex);
        return child.getName();
    }    

    /**
     * Return the number of children for this element/node
     * 
     * @return int number of children
     */
    public int childCount() {
        return node.getChildren().size();
    }
}
