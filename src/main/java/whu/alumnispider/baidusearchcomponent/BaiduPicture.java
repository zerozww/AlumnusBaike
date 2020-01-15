package whu.alumnispider.baidusearchcomponent;

import whu.alumnispider.DAO.BaiduPictureDAO;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class BaiduPicture {
    private static final String frontPath = "E:/pictures/";

    private static BaiduPictureDAO baiduPictureDAO = new BaiduPictureDAO();

    /**
     * @return void
     * @description 下载图片保存到本地磁盘，并将图片本地位置更新到数据库中
     */
    public static void downloadAllImage() {
        List<String> pictureList = baiduPictureDAO.getPictures();
        for (String picture : pictureList) {
            saveImage(picture, frontPath);
        }
        List<HashMap<String, String>> pictureListSaved = baiduPictureDAO.getPictureSaved();
        for (HashMap<String, String> map : pictureListSaved) {
            String picture = map.get("picture");
            String picturePath = frontPath + map.get("id") + ".jpg";
            baiduPictureDAO.updatePictureLocation(picture, picturePath);
        }
    }

    private static void saveImage(String pictureUrl, String path) {
        URL url;
        int id;
        id = baiduPictureDAO.insertPictureId(pictureUrl);
        if (id == 0) {
            id = baiduPictureDAO.getPictureId(pictureUrl);
        } else if (id == -1) {
            return;
        }
        try {
            url = new URL(pictureUrl);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path + id + ".jpg"));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
            baiduPictureDAO.updatePictureSave(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
