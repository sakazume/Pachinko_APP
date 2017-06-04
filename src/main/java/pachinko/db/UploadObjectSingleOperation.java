package pachinko.db;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.avaje.ebean.Ebean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class UploadObjectSingleOperation {
    private static String bucketName     = "*** Provide bucket name ***";
    private static String keyName        = "*** Provide key ***";
    private static String uploadFileName = "test.jpg";
    private static StringBuffer Log = new StringBuffer();

    public static void main(String[] args) throws IOException {
        List<Note> list = Ebean.createQuery(Note.class).where().eq("published",1).eq("status",2).gt("version",2.99f).orderBy("published_at desc").findList();
        for (int i=0;i<list.size();i++) {
            Note note = list.get(i);
            Document doc = Jsoup.parse("<!DOCTYPE html><head></head><body>" + note.getText() + "</body></html>");
            Elements el = doc.select("img");
            List<Element> removeImgList =  getRemoveImags(el);

            System.out.println("------------------------------" + note.getId() + "チェック開始------------------------------" );
            Log.append("------------------------------" + note.getId() + "チェック開始------------------------------" );
            Log.append("\n");
            Log.append("|||" + note.getTitle() + "|||" );
            Log.append("\n");

            removeImgList.parallelStream().forEach(s -> {
                System.out.println(s.attr("src"));
                Log.append(s.attr("src"));
                Log.append("\n");
                try {
                    String urlStr = s.attr("src");
                    String[] split = urlStr.split("/");
                    String fileName = split[split.length - 1];

                    URL url = new URL(s.attr("src"));
                    InputStream is = url.openStream();

                    ByteArrayOutputStream xxx = new ByteArrayOutputStream();
                    byte[] buf = new byte[32768]; // この値は適当に変更してください
                    int size = 0;

                    while ((size = is.read(buf, 0, buf.length)) != -1) {
                        xxx.write(buf, 0, size);
                    }

                    is.close();

                    s3Uplode("note/uploadimage2/" + fileName, xxx.toByteArray());
                    el.attr("src", "http://img.u-note.me/" + "note/uploadimage2/" + fileName);
                    note.setText(doc.select("body").html());
                    Ebean.save(note);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.append(s.attr("src"));
                Log.append("\n");
                System.out.println(s.attr("src"));
            });


            System.out.println("------------------------------" + note.getId() + "チェック終了------------------------------" );
            Log.append("------------------------------" + note.getId() + "チェック終了------------------------------" );
            Log.append("\n");
        }

        File file = new  File("/Users/gyutr20/log.text");
        if(!file.isFile()) {
            file.createNewFile();
        }
        Path path = new File("/Users/gyutr20/log.text").toPath();
        try {
            Files.write(path,Log.toString().getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void  s3Uplode(File file, String filePath, String fileName) {
        String bucketName = "img.u-note.me";

        AWSCredentials credentials = new BasicAWSCredentials("AKIAIMJJFFMPTZHGJJ7Q", "LFBeOX5SFt15zU90BQi2e3IY5IosF7BvpwgFJ7Ji");
        AmazonS3 s3 = new AmazonS3Client(credentials);
        s3.putObject(new PutObjectRequest(bucketName, filePath+fileName, file));
    }

    public static void  s3Uplode(String filePath, byte[] by) {
        String bucketName = "img.u-note.me";

        String suf = getSuffix(filePath);
        if(suf=="jpg") {
            suf = "jpeg";
        }
        InputStream stream = new ByteArrayInputStream(by);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(by.length);
        metadata.setContentType("image/" + suf);


        AWSCredentials credentials = new BasicAWSCredentials("AKIAIMJJFFMPTZHGJJ7Q", "LFBeOX5SFt15zU90BQi2e3IY5IosF7BvpwgFJ7Ji");
        AmazonS3 s3 = new AmazonS3Client(credentials);
        s3.putObject(new PutObjectRequest(bucketName, filePath, stream, metadata).withCannedAcl(CannedAccessControlList.PublicRead));

    }

    public static List<Element> getRemoveImags(Elements el) {
        List<Element> result = new ArrayList<>();
        for(int elIndex=0;elIndex<el.size();elIndex++) {
            Element imgEl = el.get(elIndex);
            String url = imgEl.attr("src");
            if(-1!=url.indexOf("flickr") ) {
                result.add(imgEl);
            }
        }
        return result;
    }

    /**
     * ファイル名から拡張子を返します。
     * @param fileName ファイル名
     * @return ファイルの拡張子
     */
    public static String getSuffix(String fileName) {
        if (fileName == null)
            return null;
        int point = fileName.lastIndexOf(".");
        if (point != -1) {
            return fileName.substring(point + 1);
        }
        return fileName;
    }
}

