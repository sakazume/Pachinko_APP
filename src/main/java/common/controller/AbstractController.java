package common.controller;

import common.CommonClass;
import common.db.MappedSuper;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.template.FileTemplateLoader;
import de.neuland.jade4j.template.JadeTemplate;
import de.neuland.jade4j.template.TemplateLoader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by t-sakazume on 2015/02/27.
 */
public abstract class AbstractController extends CommonClass {

    public String getSql(String sqlKey) {
        String sql = MappedSuper.SQL_MAP.get(sqlKey);
        return sql;
    }

    /**
     * テンプレートファイルからhtmlの生成
     * @param fileName
     * @param model
     * @return
     */
    public String render(String fileName,Map<String,Object> model) {
        URL resource = getResource("jade/");
        File file = null;
        try {
            file = new File(resource.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        //テンプレートフォルダの設定
        JadeConfiguration config = new JadeConfiguration();
        TemplateLoader loader = new FileTemplateLoader(file.getAbsolutePath(), "UTF-8");
        config.setTemplateLoader(loader);


        JadeTemplate template = null;

        try {
            template = config.getTemplate(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return config.renderTemplate(template,model);
    }

    public String render(String fileName) {
        return this.render(fileName ,new HashMap<String,Object>());
    }
}
