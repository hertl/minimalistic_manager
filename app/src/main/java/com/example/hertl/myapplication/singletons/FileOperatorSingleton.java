/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.singletons;

import java.io.File;

/**
 * Singleton class that has instance that keeps some important information about copying items that
 * has to be kept all the time the application is running.
 */
public class FileOperatorSingleton {
    private static final FileOperatorSingleton instance = new FileOperatorSingleton();

    private String startingPath;
    private boolean isCopy;
    private File copiedFile;

    private FileOperatorSingleton() {
        this.startingPath = null;
    }

    public static FileOperatorSingleton getInstance() {
        return instance;
    }

    /**
     * Updates the path and sets what was the desired operation
     *
     * @param path     the path of the copied item
     * @param fileName the name of the file to be copied or moved
     * @param isCopy   if the operation is copy or move
     */
    public void updatePath(String path, String fileName, boolean isCopy) {
        this.startingPath = path;
        this.isCopy = isCopy;
        copiedFile = new File(path + fileName);
    }

    /**
     * Gets the path of the item to be copied.
     *
     * @return the path
     */
    public String getStartingPath() {
        return startingPath;
    }

    /**
     * Sets the path of the item to be copied.
     *
     * @param startingPath the path to be set
     */
    public void setStartingPath(String startingPath) {
        this.startingPath = startingPath;
    }

    /**
     * Checks if the operation is copy or move.
     *
     * @return the result
     */
    public boolean isCopy() {
        return isCopy;
    }

    /**
     * Gets the copied file.
     *
     * @return the file
     */
    public File getCopiedFile() {
        return copiedFile;
    }
}
