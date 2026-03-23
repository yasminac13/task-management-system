package dataaccess;

import java.io.*;
import businesslogic.TaskManagement;

public class DataPersister {
    private static final String FILE_NAME = "project_data.ser";

    public static void save(TaskManagement management) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(management);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TaskManagement load() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (TaskManagement) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new TaskManagement();
    }
}
