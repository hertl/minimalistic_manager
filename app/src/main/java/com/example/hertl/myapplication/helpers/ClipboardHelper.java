/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Helper class for the clipboard function.
 */
public class ClipboardHelper {
    private int numberOfItems;
    private ArrayList<String> paths;
    private String clipboardPath;

    /**
     * Constructor
     *
     * @param clipboardPath the path of the clipboard file
     */
    public ClipboardHelper(String clipboardPath) {
        this.clipboardPath = clipboardPath;
        refreshClipboard();
    }

    /**
     * Adds list of paths to a clipboard.
     *
     * @param paths the list of paths
     */
    public void addToClipboard(ArrayList<String> paths) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(this.clipboardPath, true)));
            for (String path : paths) {
                out.println(path);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a path to the clipboard.
     *
     * @param path the path
     */
    public void addToClipboard(String path) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(this.clipboardPath, true)));
            out.println(path);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the clipboard
     */
    public void clearClipboard() throws IOException {

        PrintWriter out = new PrintWriter(this.clipboardPath);
        out.close();

    }

    /**
     * Checks clipboard size
     *
     * @return the clipboard size
     */
    int checkClipboardSize() {
        return this.numberOfItems;
    }

    public ArrayList<String> getPaths() {
        return paths;
    }

    /**
     * Refreshes the clipboard paths if the file was changed.
     */
    public void refreshClipboard() {
        BufferedReader reader;
        ArrayList<String> paths = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(this.clipboardPath));
            String path = reader.readLine();
            while (path != null) {
                paths.add(path);
                path = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sorts the paths by length
        Collections.sort(paths, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        });

        paths = checkSubDir(paths);

        this.numberOfItems = paths.size();
        this.paths = paths;

    }

    /**
     * Checks if one path is a sub path of another one, if it is, don't show the sob paths while
     * there is no need to do so.
     *
     * @param paths all paths
     * @return the final valid list of paths
     */
    private ArrayList<String> checkSubDir(ArrayList<String> paths) {
        ArrayList<String> finalPaths;
        finalPaths = (ArrayList<String>) paths.clone();
        for (int i = 0; i < paths.size(); i++) {
            if (!checkValidity(paths.get(i))) {
                finalPaths.remove(paths.get(i));
                continue;
            }
            for (int j = i + 1; j < paths.size(); j++)
                if (paths.get(j).contains(paths.get(i))) {
                    finalPaths.remove(paths.get(j));
                }
        }
        return finalPaths;
    }

    /**
     * Checks if the path exists in this device
     *
     * @param path the path to check
     * @return if the path is valid
     */
    private boolean checkValidity(String path) {
        File checkFile = new File(path);
        return checkFile.exists();
    }
}
