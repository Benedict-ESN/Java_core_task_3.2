package ru.kamraze;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Hello world!");
        File gamesDirectory = new File("savegames");
        if (!gamesDirectory.exists()) {
            System.out.println("Dir " + gamesDirectory + (gamesDirectory.mkdir() ? " successfully created\n" : " uncreated\n"));
        }

        GameProgress game1 = new GameProgress(100, 3, 10, 250.5);
        GameProgress game2 = new GameProgress(80, 2, 5, 150.3);
        GameProgress game3 = new GameProgress(1, 4, 8, 200.7);


        saveGame(gamesDirectory + "\\save1.dat", game1);
        saveGame(gamesDirectory + "\\save2.dat", game2);
        saveGame(gamesDirectory + "\\save3.dat", game3);

        // Получаем список файлов в директории
        File[] files = gamesDirectory.listFiles();
        // Проверяем, что директория не пуста и файлы не равны null
        if (files != null && files.length > 0) {
            // Перебираем все файлы и выводим их имена
            for (File file : files) {
                System.out.println(file.getName() + " : size " + file.length() + "b. " + file.getAbsolutePath());
            }
        } else {
            System.out.println("Директория пуста или не существует");
        }
        zipFiles("savegames\\SaveGames.zip", files);
    }


    public static void saveGame(String filePath, GameProgress gameProgress) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(gameProgress);
            System.out.println("Game saved successfully: " + filePath);
        } catch (FileNotFoundException fnf) {
            System.err.println("Path not found: " + fnf.getMessage());
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    public static void zipFiles(String fileZipPath, File[] filesToZip) {
        try (FileOutputStream fos = new FileOutputStream(fileZipPath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

// Цикл проходится по каждому файлу в списке файлов filesToZip.
            for (File fileToZip : filesToZip) {
// Проверяем, существует ли текущий файл, и если нет, выводим сообщение и переходим к следующему файлу.
                if (!fileToZip.exists()) {
                    System.err.println("File not found: " + fileToZip.getAbsolutePath());
                    continue;
                }
// Открываем поток для чтения содержимого файла fileToZip с помощью FileInputStream
                try (FileInputStream fis = new FileInputStream(fileToZip)) {
// Создаем новую запись ZIP с именем текущего файла fileToZip.
                    ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
// Указываем, что начинается новая запись в архиве ZIP
                    zos.putNextEntry(zipEntry);
// Определяем размер буфера в 1 Кб
                    byte[] buffer = new byte[1024];
                    int bytesRead;
// Читаем из файла fileToZip набор данных размером 1024 байт до тех пор, пока в bytesRead лежат значения больше 0.
                    while ((bytesRead = fis.read(buffer)) > 0) {
// Если bytesRead не 0, записываем его содержимое в zos = fileToZip внутри ZIP-архива
                        zos.write(buffer, 0, bytesRead);
                    }
// Если все файлы байты в fileToZip закончились, закрываем поток в текущий файл в ZIP-архиве
                    zos.closeEntry();
                    System.out.println("File zipped successfully: " + fileZipPath);
                } catch (IOException e) {
                    System.err.println("Error zipping file: " + e.getMessage());
                }
            }
// Если все файлы в filesZipPath закончились, радостно сообщаем об этом
            System.out.println("All files zipped successfully: " + fileZipPath);

        } catch (FileNotFoundException fnf) {
            System.err.println("Path not found: " + fnf.getMessage());
        } catch (IOException e) {
            System.err.println("Error creating zip file: " + e.getMessage());
        }
// А теперь удалим всё, что смогли сархивировать
        for (File fileToDelete : filesToZip) {
            if (fileToDelete.getName().equals("SaveGames.zip")) {
                continue;
            }
            if (fileToDelete.delete()) {
                System.out.println("File deleted successfully: " + fileToDelete);
            } else {
                System.err.println("Error deleting file: " + fileToDelete);
            }
        }

    }
}

