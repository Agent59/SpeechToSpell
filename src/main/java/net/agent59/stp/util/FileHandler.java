package net.agent59.stp.util;

import net.agent59.stp.Main;

import java.io.File;

public class FileHandler {

    private static final String current_directory = System.getProperty("user.dir");
    public static final String PROJECT_DIRECTORY = current_directory;

    public static final String MODS_DIRECTORY = PROJECT_DIRECTORY + File.separatorChar + "mods";

    public static final String THIS_MOD_DIRECTORY = MODS_DIRECTORY + File.separatorChar + Main.MOD_NAME;

    public static final String RESOURCE_DIRECTORY = THIS_MOD_DIRECTORY + File.separatorChar + "resources";


    public static void createFolderIfNonexistent(String folderName, String path){
        File directory = new File(path + File.separatorChar + folderName);
        if (!(directory.isDirectory())) {
            boolean created_directory = directory.mkdir();
            if (!created_directory) {
                System.out.println("Couldn't create " + folderName + " directory in " + path);
            } else {
                System.out.println("Created " + folderName + " directory in " + path);
            }
        }
    }
}
