package irand;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;

/**
 * Created by gyutr20 on 2016/11/04.
 */
public class WebViewTest extends Application {

    public static void main(String[] args) {
        WebViewTest.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        //スクリーンショットを撮るサイト
        String url = "http://tool-taro.com/";
        //画像ファイルの保存先
        String filePath = "webview.png";

        WebView webView = new WebView();
        webView.setPrefSize(1440, 900);

        WebEngine webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            @Override
            public void changed(ObservableValue ov, State oldState, State newState) {
                if(newState != Worker.State.SUCCEEDED) {
                    return;
                }

                Document s = webEngine.getDocument();
                W3CDom w3c = new W3CDom();

                String test = w3c.asString(s);
                System.out.println(test);
//                //changeイベントが完了した時点でスクリーンショットを撮る
//                stage.setTitle(webEngine.getLocation());
//
//                WritableImage image = webView.snapshot(null, null);
//                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
//                try {
//                    ImageIO.write(bufferedImage, "png", new File(filePath));
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//                finally {
//                    //アプリケーションを終了する
////                        Platform.exit();
//                }
            }
        });

        //サイトにアクセスしてコンテンツをロードする
        webEngine.load(url);
        //WebViewをSceneに入れてStageにのせて表示する(そうしないとWebEngineのchangeイベントは成功しない)
        stage.setScene(new Scene(webView));
        stage.show();
    }
}
