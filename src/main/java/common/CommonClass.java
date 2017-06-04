package common;

import pachinko.controller.PatchinkoController;

import java.net.URL;

/**
 * 全ての基幹クラス
 * 全体の共通処理
 */
public abstract class CommonClass {

    public URL getResource(String path) {
        return CommonClass.class.getClassLoader().getResource(path);
    }

}
