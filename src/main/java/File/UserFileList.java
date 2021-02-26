/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package File;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author johnnywu
 */
public class UserFileList {

    //Set<File> all = new HashSet<File>();
    ArrayList<File> al = new ArrayList<File>();


    public static void getAllFileAndFolder(File folder, ArrayList<File> al) {

    for (File file : folder.listFiles()) {
      al.add(file);
      if (file.isDirectory()) {
        getAllFileAndFolder(file, al);
      }
    }
  }

}
