package transformation;

import domain.Work;
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

    private Map<String, StringWriter> buildHTMLPages(List<String> indexImageUrls, Map<String, List<String>> cameraMakeToImageUrls, Map<String, Map<String, List<String>>> cameraMakeAndModelToImageUrls) {
        String indexDocumentName = getDocumentNameForIndexHTMLPage();
        Map<String, String> cameraMakeToDocumentName = new HashMap<>();
        Map<String, Map<String, String>> cameraMakeAndModelToDocumentName = new HashMap<>();

        // first, initialise the mappings from images by a camera make (e.g. "SUPERCAM") to associated document name (e.g. imagesOf_SUPERCAM.html)
        for(Map.Entry<String, List<String>> imageUrlsOfCameraMake : cameraMakeToImageUrls.entrySet()) {
            String cameraMake = imageUrlsOfCameraMake.getKey();
            cameraMakeToDocumentName.put(cameraMake, getDocumentNameForCameraMakeHTMLPage(cameraMake));
            cameraMakeAndModelToDocumentName.put(cameraMake, new HashMap<>());

            // second, initialise the mappings from images by a camera model (e.g. "S3000") to associated document name (e.g. imagesOf_SUPERCAM_S3000.html)
            for(Map.Entry<String, List<String>> imageUrlsOfCameraMakeAndModel : cameraMakeAndModelToImageUrls.get(cameraMake).entrySet()) {
                String cameraModel = imageUrlsOfCameraMakeAndModel.getKey();
                cameraMakeAndModelToDocumentName.get(cameraMake).put(cameraModel, getDocumentNameForCameraMakeAndModelHTMLPage(cameraMake, cameraModel));
            }
        }


        // then create the html pages and map the document name to the corresponding html page
        Map<String, StringWriter> documentNameToHTMLPage = new HashMap<>();

        // create the index page and map its document name to it
        documentNameToHTMLPage.put(indexDocumentName, buildIndexHTMLPage(cameraMakeToDocumentName, indexImageUrls));

        // for each camera make,
        for(Map.Entry<String, String> cameraMake : cameraMakeToDocumentName.entrySet()) {
            // create a page
            StringWriter htmlPage = buildCameraMakeHTMLPage(indexDocumentName, cameraMakeAndModelToDocumentName.get(cameraMake.getKey()), cameraMake.getKey(), cameraMakeToImageUrls.get(cameraMake));
            documentNameToHTMLPage.put(cameraMake.getKey(), htmlPage);

            // and create a page for each camera model of that camera make
            for(Map.Entry<String, String> cameraModel : cameraMakeAndModelToDocumentName.get(cameraMake.getKey()).entrySet()) {
                htmlPage = buildCameraMakeAndModelHTMLPage(indexDocumentName, cameraMakeToDocumentName.get(cameraMake), cameraMake.getKey(), cameraModel.getKey(), cameraMakeAndModelToImageUrls.get(cameraMake).get(cameraModel));
                documentNameToHTMLPage.put(cameraModel.getKey(), htmlPage);
            }
        }

        return documentNameToHTMLPage;
    }

    private StringWriter buildIndexHTMLPage( Map<String, String> cameraMakeToDocumentName, List<String> imageUrls) {
        StringWriter htmlDocument = new StringWriter();
        Context context = new Context();
        context.setVariable("cameraMakePageLinks", cameraMakeToDocumentName);
        context.setVariable("imageUrls", imageUrls);
        templateEngine.process("index", context, htmlDocument);
        return htmlDocument;
    }

    private StringWriter buildCameraMakeHTMLPage(String indexDocumentName, Map<String, String> cameraModelToDocumentName, String cameraMake, List<String> imageUrls) {
        StringWriter htmlDocument = new StringWriter();
        Context context = new Context();
        context.setVariable("indexPageLink", indexDocumentName);
        context.setVariable("cameraModelPageLinks", cameraModelToDocumentName);
        context.setVariable("imageUrls", imageUrls);
        context.setVariable("currentMake", cameraMake);
        templateEngine.process("imagesByCameraMake", context, htmlDocument);
        return htmlDocument;
    }

    private StringWriter buildCameraMakeAndModelHTMLPage(String indexDocumentName, String cameraMakeDocumentName, String cameraMake, String cameraModel, List<String> imageUrls) {
        StringWriter htmlDocument = new StringWriter();
        Context context = new Context();
        context.setVariable("indexPageLink", indexDocumentName);
        context.setVariable("cameraMakePageLink", cameraMakeDocumentName);
        context.setVariable("imageUrls", imageUrls);
        context.setVariable("currentMake", cameraMake);
        context.setVariable("currentModel", cameraModel);
        templateEngine.process("imagesByCameraMakeAndModel", context, htmlDocument);
        return htmlDocument;
    }

    private String getDocumentNameForIndexHTMLPage() {
        return "index.html";
    }

    private String getDocumentNameForCameraMakeHTMLPage(String cameraMake) {
        return "imagesOf_" + cameraMake.trim().replaceAll(" +", "-") + ".html";
    }

    private String getDocumentNameForCameraMakeAndModelHTMLPage(String cameraMake, String cameraModel) {
        return "imagesOf_" + cameraMake.trim().replaceAll(" +", "-") + "_" + cameraModel.trim().replaceAll(" +", "-") + ".html";
    }

}
