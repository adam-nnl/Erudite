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
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.lang.Integer;

/*************************************************************************
* Provide a file history mechanism for the File menu of a parent frame). *
*                                                                        *
* @since JDK 1.2                                                         *   
* @Edited by Adam Lara for JDK 6 support and additional features         *
* @original author Klaus Berg                                                     *
*************************************************************************/
public class FileHistory {

   private static final int MAX_ITEM_LEN = 50;      
   private static final String FILE_SEPARATOR_STR = System.getProperty("file.separator");
   private static String historySerFile; 
   private static int max_itemnames; 
   static ArrayList itemnameHistory = new ArrayList(max_itemnames); 
   static ArrayList pathnameHistory = new ArrayList(max_itemnames);

   private IFileHistory caller;
   private JComboBox fileMenu;

   // --- IFileHistory interface ----------------------------------------------

   /**
    * Interface that must be implemented by a GUI applcation frame
    * that wants to use the FileHistory class.
    * 
    * @author Klaus Berg
    * @since JDK 1.2
    */
   public static interface IFileHistory {

      /**
       * Get the application name to identify the configuration file in the
       * the USER_HOME directory. This name should be unique in this directory.
       * 
       * @return the application name
       */
      public String getApplicationName();

      /**
       * Get a handle to the frame's file menu.
       * 
       * @return the frame's file menu
       */
      public JComboBox getFileMenu();

      /**
       * Return the size of the main application frame.
       * It is used to center the file history maintenance window.
       * 
       * @return the main GUI frame's size
       */
      public Dimension getSize();

      /**
       * Return the main application frame.
       * It is used to center the file history maintenance window.
       * 
       * @return the main GUI frame
       */
      public JFrame getFrame();

      /**
        * Perform the load file activity.
        * 
        * @param path   the pathname of the loaded file
        */
      public void loadFile(String pathname, String filename);

   }

   // -------------------------------------------------------------------------

   // CONSTRUCTOR: caller is the parent frame that hosts the file menu
   public FileHistory(IFileHistory caller) {
      this.caller = caller;
      historySerFile = "FileHistory.xml";
      String max_itemnames_str = System.getProperty("itemnames.max", "9");
      try {
         max_itemnames = Integer.parseInt(max_itemnames_str);
      }
      catch (NumberFormatException e) {
         System.err.println(e);
         e.printStackTrace();
         System.exit(1);
      }
      if (max_itemnames < 1) {
         max_itemnames = 9;
      }
      fileMenu = caller.getFileMenu();      
   }

   /*******************************************************************
   * Initialize itemname and pathname arraylists from historySerFile. *
   * build up the additional entries in the File menu.                *
   *******************************************************************/
   public final void initFileMenuHistory() {
      if (new File(historySerFile).exists()) {
         try {
             FileInputStream fis = new FileInputStream(historySerFile);  
             XMLDecoder decoder =
              new XMLDecoder(new BufferedInputStream(fis));
             Integer itemnameCount = (Integer)decoder.readObject();
             decoder.close();
              if (itemnameCount > max_itemnames) {
              itemnameCount = max_itemnames;
            }
            for (int i=0; i<itemnameCount; i++) {
               itemnameHistory.add((String)decoder.readObject());
               pathnameHistory.add((String)decoder.readObject());               
               fileMenu.addItem(itemnameHistory.get(i)); 
            }
            for (int x=0; x<itemnameCount; x++) {
                fileMenu.addActionListener(new ItemListener(x));
            }            
            
            

            fis.close();
     
         }
         catch (Exception e) {
            System.err.println("Trouble reading file history entries: " + e);
            e.printStackTrace();
         }
      }
   }

   /***********************************************************
   * Save itemname and pathname arraylists to historySerFile. *
   ***********************************************************/
   public void saveHistoryEntries() {
      try {
       XMLEncoder e = new java.beans.XMLEncoder(
                          new BufferedOutputStream(
                              new FileOutputStream("FileHistory.xml")));
            Integer itemnameCount = new Integer(itemnameHistory.size());
            e.writeObject(itemnameCount);
            for (int i=0; i<itemnameCount; i++) {
            e.writeObject((String)itemnameHistory.get(i));
            e.writeObject((String)pathnameHistory.get(i));
            }
            
       e.close();

      }
      catch (Exception e) {
         System.err.println("Trouble saving file history entries: " + e);
         e.printStackTrace();
      }
   }

   /*******************************************************************
   * Insert the last loaded pathname into the File menu if it is not  *
   * present yet. Only max pathnames are shown (the max number can be *
   * set in Jmon.ini, default is 9). Every item starts with 	      *
   * "<i>: ", where <i> is in the range [1..max].	              *
   * The loaded itemname will become item number 1 in the list.       *
   *******************************************************************/
   public final void insertPathname(String pathname, String filename) {     
         // remove all itemname entries to prepare for re-arrangement
        ActionListener[] menuListeners = fileMenu.getActionListeners();
        for (int x=0; x<menuListeners.length; x++) {
            fileMenu.removeActionListener(menuListeners[x]);
        }       
        for (int i=fileMenu.getItemCount()-1, j=0; j < itemnameHistory.size(); i--, j++) {
            fileMenu.removeItemAt(i);//.remove(i);
        }
        if (itemnameHistory.size() == max_itemnames) {  // fileList is full: remove last entry to get space for the first item
            itemnameHistory.remove(max_itemnames-1);
            pathnameHistory.remove(max_itemnames-1);
        }
        itemnameHistory.add(0, filename);
        pathnameHistory.add(0, pathname);
        for (int i=0; i<itemnameHistory.size(); i++) {
            fileMenu.addItem(itemnameHistory.get(i));
        }
        for (int i=0; i<itemnameHistory.size(); i++) {
            fileMenu.addActionListener(new ItemListener(i)); 
        }        

   }   

   /**************************************************************
   * Create a JList instance with itemnameHistory as its model.  *
   **************************************************************/
   private final JList createItemList() {
      ListModel model = new ListModel();
      JList list = new JList(model);
      list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);      
      return list;
   }


   // --- Helper classes ------------------------------------------------------

   /***********************************************************
   * Create a tooltip location directly over the menu item,   *
   * ie, left allign the tooltip text in "overlay" technique. * 
   ***********************************************************/
   private final class MenuItemWithFixedTooltip extends JButton {

      public MenuItemWithFixedTooltip(String text) {
         super(text);
      }

      public Point getToolTipLocation(MouseEvent e) {
         Graphics g = getGraphics();
         FontMetrics metrics = g.getFontMetrics(g.getFont());
         String prefix = itemnameHistory.size() <= 9 ? "8: " : "88: "; 
         int prefixWidth = metrics.stringWidth(prefix);
         int x = JButton.TRAILING + JButton.LEADING -1 + prefixWidth;
         return new Point(x, 0);
      }
   }

   /***********************************
   * Listen to menu item selections.  *
   ***********************************/
   private final class ItemListener implements ActionListener {
      int itemNbr;

      ItemListener(int itemNbr) {
         this.itemNbr = itemNbr;
      }

      public void actionPerformed(ActionEvent e) {
         /*int key=fileMenu.getSelectedIndex();
         if(key==0){
            caller.loadFile((String)pathnameHistory.get(itemNbr), "menu action performed key=0");
         }
         else if(key>=0){
            caller.loadFile((String)pathnameHistory.get(key), "menu action performed key>0");
         }
         else if(key<0){
             return;
         }*/
         //JMenuItem item = (JMenuItem)e.getSource();
         
         //FileHistory.this.insertPathname(item.getToolTipText());
      }
   }

   /********************************************************
   * The list model for our File History dialog itemList.  *
   ********************************************************/
   private final class ListModel extends AbstractListModel {

      public Object getElementAt(int i) {
         return itemnameHistory.get(i);
      }

      public int getSize() {
         return itemnameHistory.size();
      }

   }

} // end class FileHistory
