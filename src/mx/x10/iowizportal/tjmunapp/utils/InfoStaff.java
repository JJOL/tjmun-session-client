package mx.x10.iowizportal.tjmunapp.utils;

import mx.x10.iowizportal.tjmunapp.elements.TransitionFieldLabel;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JJOL on 10/11/2015.
 */
public class InfoStaff {

    List<TransitionFieldLabel> dataComponents;

    public InfoStaff(TransitionFieldLabel... nodes) {
        dataComponents = Arrays.asList(nodes);
    }

    public void saveData() {
        ArrayList<Object> objects = new ArrayList<>();
        for(TransitionFieldLabel field : dataComponents) {
            objects.add(field.save());
        }

        try {
            FileOutputStream fileOut = new FileOutputStream("infostaff.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(objects);
            out.close();
            fileOut.close();
            System.out.println("Info Staff Saved!");
        } catch (IOException ex) {
             ex.printStackTrace();
        }
    }

    public void loadData() {
        if(!(new File("infostaff.ser").exists()))
            return;
        ArrayList<Object> objects;
        try {
            FileInputStream fileIn = new FileInputStream("infostaff.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);

            objects = (ArrayList<Object>)in.readObject();

            in.close();
            fileIn.close();

            for(int i = 0; i < objects.size(); i++) {
                dataComponents.get(i).load(objects.get(i));
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }



    }

}
