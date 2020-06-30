package whu.alumnispider.baidusearchcomponent;

import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import whu.alumnispider.DAO.BaiduPictureDAO;
import whu.alumnispider.utils.SslUtil;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class BaiduPicture {
    // 图片本地保存地址
    private static final String frontPath = "D:/pictures_v2/";

    private static BaiduPictureDAO baiduPictureDAO = new BaiduPictureDAO();

    /**
     * @description 获取图片下载地址，若无，则返回null
     * @param html
     * @return java.lang.String
     */
    public static String getPicture(Html html) {
        String personPicturePath = "//div[@class='summary-pic']/a/img/@src";
        Selectable personPicturePage;
        List<String> personPictures;
        personPicturePage = html.xpath(personPicturePath);
        personPictures = personPicturePage.all();
        for (String personPicture : personPictures) {
            return personPicture;
        }
        return null;
    }

    /**
     * @return void
     * @description 下载图片保存到本地磁盘，并将图片本地位置更新到数据库中
     */
    /*
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
    */

    /**
     * @description 下载人物图片，保存至本地，命名根据人物百科ID
     * @param pictureUrl 人物图片的下载地址
     * @param baikeId 人物百科ID
     * @return 图片文件名
     */
    public static String downloadImage(String pictureUrl,int baikeId){
        URL url;
        String pictureName = baikeId + ".jpg";
        String pictureLocal = frontPath + pictureName;
        try {
            url = new URL(pictureUrl);
            if("https".equalsIgnoreCase(url.getProtocol())){
                SslUtil.ignoreSsl();
            }
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(pictureLocal));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
            return pictureName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
