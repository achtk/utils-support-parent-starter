package com.chua.common.support.lang.spider.model;

import com.chua.common.support.lang.spider.Site;
import com.chua.common.support.lang.spider.Spider;
import com.chua.common.support.lang.spider.pipeline.CollectorPipeline;
import com.chua.common.support.lang.spider.pipeline.PageModelPipeline;
import com.chua.common.support.lang.spider.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * The spider for page model extractor.<br>
 * In webmagic, we call a POJO containing extract result as "page model". <br>
 * You can customize a crawler by write a page model with annotations. <br>
 * Such as:
 * <pre>
 * {@literal @}TargetUrl("http://my.oschina.net/flashsword/blog/\\d+")
 *  public class OschinaBlog{
 *
 *      {@literal @}ExtractBy("//title")
 *      private String title;
 *
 *      {@literal @}ExtractBy(value = "div.BlogContent",type = ExtractBy.Type.Css)
 *      private String content;
 *
 *      {@literal @}ExtractBy(value = "//div[@class='BlogTags']/a/text()", multi = true)
 *      private List&lt;String&gt; tags;
 * }
 * </pre>
 * And start the spider by:
 * <pre>
 *   OOSpider.create(Site.me().addStartUrl("http://my.oschina.net/flashsword/blog")
 *        ,new JsonFilePageModelPipeline(), OschinaBlog.class).run();
 * }
 * </pre>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class OoSpider<T> extends Spider {

    private ModelPageProcessor modelPageProcessor;

    private ModelPipeline modelPipeline;

    private PageModelPipeline pageModelPipeline;

    private List<Class> pageModelClasses = new ArrayList<Class>();

    protected OoSpider(ModelPageProcessor modelPageProcessor) {
        super(modelPageProcessor);
        this.modelPageProcessor = modelPageProcessor;
    }

    public OoSpider(PageProcessor pageProcessor) {
        super(pageProcessor);
    }

    /**
     * create a spider
     *
     * @param site              site
     * @param pageModelPipeline pageModelPipeline
     * @param pageModels        pageModels
     */
    public OoSpider(Site site, PageModelPipeline pageModelPipeline, Class... pageModels) {
        this(ModelPageProcessor.create(site, pageModels));
        this.modelPipeline = new ModelPipeline();
        super.addPipeline(modelPipeline);
        for (Class pageModel : pageModels) {
            if (pageModelPipeline != null) {
                this.modelPipeline.put(pageModel, pageModelPipeline);
            }
            pageModelClasses.add(pageModel);
        }
    }

    @Override
    protected CollectorPipeline getCollectorPipeline() {
        return new PageModelCollectorPipeline<T>(pageModelClasses.get(0));
    }

    public static OoSpider create(Site site, Class... pageModels) {
        return new OoSpider(site, null, pageModels);
    }

    public static OoSpider create(Site site, PageModelPipeline pageModelPipeline, Class... pageModels) {
        return new OoSpider(site, pageModelPipeline, pageModels);
    }

    public OoSpider addPageModel(PageModelPipeline pageModelPipeline, Class... pageModels) {
        for (Class pageModel : pageModels) {
            modelPageProcessor.addPageModel(pageModel);
            modelPipeline.put(pageModel, pageModelPipeline);
        }
        return this;
    }

    public OoSpider setIsExtractLinks(boolean isExtractLinks) {
        modelPageProcessor.setExtractLinks(isExtractLinks);
        return this;
    }

}
