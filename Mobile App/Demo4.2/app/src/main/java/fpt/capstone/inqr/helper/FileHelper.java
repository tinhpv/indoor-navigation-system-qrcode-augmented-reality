package fpt.capstone.inqr.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileHelper {
    // Chuyển link thành file lấy từ local

    public static final int TYPE_MAP = 124;
    public static final int TYPE_QR = 184;

    public FileHelper() {
    }

//

    public static void saveFileFromUrl(Context context, int type, String link) {
        //Internal path save file
        String folder = "image";


        File directory = context.getDir(folder, Context.MODE_PRIVATE);
//
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String typePath = "";

        if (type == TYPE_MAP) {
            typePath = "map";
        } else if (type == TYPE_QR) {
            typePath = "qr_code";
        }

        File typePathDir = new File(directory, typePath);
        if (!typePathDir.exists()) {
            typePathDir.mkdirs();
        }

        String fileName = link.substring(link.lastIndexOf('/') + 1);

        try {
            // check file exists
            File file = new File(typePathDir, fileName);
            if (!file.exists()) {
                URL url = new URL(link);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
//                    int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream(file);

                byte[] data = new byte[1024];

//                    long total = 0;
                int count;

                while ((count = input.read(data)) != -1) {
//                        total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
//                        publishProgress("" + (int) ((total * 100) / lengthOfFile));


                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

//                    System.out.println("OK - " + i);


            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }


    }

    public static InputStream getImage(Context context, int type, String fileName) {
        try {
//            String fileName = link.substring(link.lastIndexOf('/') + 1);
            File directory = context.getDir("image", Context.MODE_PRIVATE);

            String typePath = "";

            if (type == TYPE_MAP) {
                typePath = "map";
            } else if (type == TYPE_QR) {
                typePath = "qr_code";
            }

            //IMAGE
            File typePathDir = new File(directory, typePath);

            File file = new File(typePathDir, fileName + ".png");

            if (file.exists()) {
//                Uri uri = Uri.fromFile(file);
                InputStream is = new FileInputStream(file);
                return is;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    public static void deleteOldData(Context context, int numberOfChapter) {
        try {
            File directory = context.getDir("vietkoredu", Context.MODE_PRIVATE);

            String numberOfChapterStr = String.format("%02d", numberOfChapter);

            // CH13
            File chapterPathDir = new File(directory, "CH" + numberOfChapterStr);
            deleteDirectory(chapterPathDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        path.delete();
    }
}
