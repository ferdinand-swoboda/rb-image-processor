package transformation;

import domain.Work;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WorksToHTMLTransformation implements Transformation<List<Work>, Map<String, StringWriter>> {

    private ITemplateEngine templateEngine;

    public WorksToHTMLTransformation() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        this.templateEngine = templateEngine;
    }

    @Override
    public Map<String, StringWriter> apply(List<Work> works) {
        // first image urls
        List<String> indexImageUrls = new LinkedList<>();

        // first image urls per camera make
        Map<String, List<String>> imageUrlsPerCameraMake = new HashMap<>();

        // first image urls per camera make and model
        Map<String, Map<String, List<String>>> imageUrlsPerCameraMakeAndModel = new HashMap<>();

        for(Work work : works) {
            if(indexImageUrls.size() < 10) {
                indexImageUrls.add(work.getUrlsOfImages().get(2));
            }

            if(!imageUrlsPerCameraMake.containsKey(work.getExif_make())) {
                imageUrlsPerCameraMake.put(work.getExif_make(), new LinkedList<>());
                imageUrlsPerCameraMakeAndModel.put(work.getExif_make(), new HashMap<>());
            }
            if(imageUrlsPerCameraMake.get(work.getExif_make()).size() < 10) {
                imageUrlsPerCameraMake.get(work.getExif_make()).add(work.getUrlsOfImages().get(2));
            }

            if(!imageUrlsPerCameraMakeAndModel.get(work.getExif_make()).containsKey(work.getExif_model())) {
                imageUrlsPerCameraMakeAndModel.get(work.getExif_make()).put(work.getExif_model(), new LinkedList<>());
            }
            if(imageUrlsPerCameraMakeAndModel.get(work.getExif_make()).get(work.getExif_model()).size() < 10) {
                imageUrlsPerCameraMakeAndModel.get(work.getExif_make()).get(work.getExif_model()).add(work.getUrlsOfImages().get(2));
            }
        }

        return buildHTMLPages(indexImageUrls, imageUrlsPerCameraMake, imageUrlsPerCameraMakeAndModel);
    }

    private Map<String, StringWriter> buildHTMLPages(List<String> indexImageUrls, Map<String, List<String>> imageUrlsPerCameraMake, Map<String, Map<String, List<String>>> imageUrlsPerCameraMakeAndModel) {
        Map<String, StringWriter> htmlPages = new HashMap<>();

        Pair<String, StringWriter> htmlPage = buildIndexHTMLPage(indexImageUrls);
        htmlPages.put(htmlPage.getKey(), htmlPage.getValue());

        for(Map.Entry<String, List<String>> imageUrlsOfCameraMake : imageUrlsPerCameraMake.entrySet()) {
            String cameraMake = imageUrlsOfCameraMake.getKey();
            htmlPage = buildCameraMakeHTMLPage(cameraMake, imageUrlsOfCameraMake.getValue());
            htmlPages.put(htmlPage.getKey(), htmlPage.getValue());

            for(Map.Entry<String, List<String>> imageUrlsOfCameraMakeAndModel : imageUrlsPerCameraMakeAndModel.get(cameraMake).entrySet()) {
                String cameraModel = imageUrlsOfCameraMakeAndModel.getKey();
                htmlPage = buildCameraMakeAndModelHTMLPage(cameraMake, cameraModel, imageUrlsOfCameraMake.getValue());
                htmlPages.put(htmlPage.getKey(), htmlPage.getValue());
            }
        }

        return htmlPages;
    }

    private Pair<String, StringWriter> buildIndexHTMLPage(List<String> imageUrls) {
        String documentName = "index.html";
        StringWriter htmlDocument = new StringWriter();
        Context context = new Context();
        context.setVariable("imageUrls", imageUrls);
        templateEngine.process("index", context, htmlDocument);
        return new ImmutablePair<>(documentName, htmlDocument);
    }

    private Pair<String, StringWriter> buildCameraMakeHTMLPage(String cameraMake, List<String> imageUrls) {
        String documentName = "imagesOf_" + cameraMake.trim().replaceAll(" +", "-") + ".html";
        StringWriter htmlDocument = new StringWriter();
        Context context = new Context();
        templateEngine.process("imagesByCameraMake", context, htmlDocument);
        return new ImmutablePair<>(documentName, htmlDocument);
    }

    private Pair<String, StringWriter> buildCameraMakeAndModelHTMLPage(String cameraMake, String cameraModel, List<String> imageUrls) {
        String documentName = "imagesOf_" + cameraMake.trim().replaceAll(" +", "-") + "_" + cameraModel.trim().replaceAll(" +", "-") + ".html";
        StringWriter htmlDocument = new StringWriter();
        Context context = new Context();
        templateEngine.process("imagesByCameraMakeAndModel", context, htmlDocument);
        return new ImmutablePair<>(documentName, htmlDocument);
    }
}
