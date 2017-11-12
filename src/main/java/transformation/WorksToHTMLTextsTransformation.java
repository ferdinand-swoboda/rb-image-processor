package transformation;

import domain.Work;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorksToHTMLTextsTransformation implements Transformation<List<Work>, Map<String, StringWriter>> {

    private static final int NUMBER_OF_IMAGES_TO_DISPLAY = 10;
    public static final String TEMPLATES_DIR = "/templates/";
    public static final String TEMPLATES_SUFFIX = ".html";

    private ITemplateEngine templateEngine;

    public WorksToHTMLTextsTransformation() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setPrefix(TEMPLATES_DIR);
        resolver.setSuffix(TEMPLATES_SUFFIX);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        this.templateEngine = templateEngine;
    }

    @Override
    public Map<String, StringWriter> apply(List<Work> works) {

        //first, construct an index on the list of works to enable fast search by camera make; works with no make are ignored
        Map<String, List<Work>> makeIndex = works.stream().filter(work -> work.getExif_make() != null)
                .collect(Collectors.groupingBy(Work::getExif_make));

        //second, construct a 2-layered index to enable fast search by camera make and camera model; works with no make or model are ignored
        Map<String, Map<String, List<Work>>> makeAndModelIndex = works.stream().filter(work -> work.getExif_make() != null && work.getExif_model() != null)
                .collect(Collectors.groupingBy(Work::getExif_make, Collectors.groupingBy(Work::getExif_model)));

        return buildHTMLPages(works, makeIndex, makeAndModelIndex);
    }

    private Map<String, StringWriter> buildHTMLPages(List<Work> works, Map<String, List<Work>> makeIndex, Map<String, Map<String, List<Work>>> makeAndModelIndex) {
        String indexDocumentName = getDocumentNameForIndexHTMLPage();
        Map<String, String> cameraMakeToDocumentName = new HashMap<>();
        for(String cameraMake : makeIndex.keySet()) {
            cameraMakeToDocumentName.put(cameraMake, getDocumentNameForCameraMakeHTMLPage(cameraMake));
        }
        Map<String, Map<String, String>> cameraMakeAndModelToDocumentName = new HashMap<>();
        for(Map.Entry<String, Map<String, List<Work>>> makeAndModelToWorks : makeAndModelIndex.entrySet()) {
            String cameraMake = makeAndModelToWorks.getKey();
            cameraMakeAndModelToDocumentName.put(cameraMake, new HashMap<>());
            for(String cameraModel : makeAndModelToWorks.getValue().keySet()) {
                cameraMakeAndModelToDocumentName.get(cameraMake).put(cameraModel, getDocumentNameForCameraMakeAndModelHTMLPage(cameraMake, cameraModel));
            }
        }

        // then create the html pages and map the previously created document name to the corresponding html page
        StringWriter htmlPage;
        Map<String, StringWriter> documentNameToHTMLPage = new HashMap<>();

        // create the index html page and map its document name to it
        htmlPage = buildIndexHTMLPage(cameraMakeToDocumentName,
                works.subList(0, (works.size() < NUMBER_OF_IMAGES_TO_DISPLAY) ? works.size() : NUMBER_OF_IMAGES_TO_DISPLAY));
        documentNameToHTMLPage.put(indexDocumentName, htmlPage);

        // for each camera make, map its document name to the created HTML page
        for(Map.Entry<String, List<Work>> cameraMakeToWorks : makeIndex.entrySet()) {
            String cameraMake = cameraMakeToWorks.getKey();
            List<Work> worksOfCameraMake = cameraMakeToWorks.getValue();
            htmlPage = buildCameraMakeHTMLPage(indexDocumentName,
                    cameraMakeAndModelToDocumentName.get(cameraMake),
                    worksOfCameraMake.subList(0, (worksOfCameraMake.size() < NUMBER_OF_IMAGES_TO_DISPLAY) ? worksOfCameraMake.size() : NUMBER_OF_IMAGES_TO_DISPLAY));
            documentNameToHTMLPage.put(cameraMakeToDocumentName.get(cameraMake), htmlPage);

            for(Map.Entry<String, List<Work>> cameraModelToWorks : makeAndModelIndex.get(cameraMake).entrySet()) {
                // create a html page for each camera make's model and map its document name to it
                String cameraModel = cameraModelToWorks.getKey();
                List<Work> worksOfCameraModel = cameraModelToWorks.getValue();
                htmlPage = buildCameraMakeAndModelHTMLPage(indexDocumentName,
                        cameraMakeToDocumentName.get(cameraMake),
                        worksOfCameraModel.subList(0, (worksOfCameraModel.size() < NUMBER_OF_IMAGES_TO_DISPLAY) ? worksOfCameraModel.size() : NUMBER_OF_IMAGES_TO_DISPLAY));
                documentNameToHTMLPage.put(cameraMakeAndModelToDocumentName.get(cameraMake).get(cameraModel), htmlPage);
            }
        }

        return documentNameToHTMLPage;
    }

    private StringWriter buildIndexHTMLPage( Map<String, String> cameraMakeToDocumentName, List<Work> works) {
        StringWriter htmlDocument = new StringWriter();
        Context context = new Context();
        context.setVariable("cameraMakePageLinks", cameraMakeToDocumentName);
        context.setVariable("works", works);
        templateEngine.process("index", context, htmlDocument);
        return htmlDocument;
    }

    private StringWriter buildCameraMakeHTMLPage(String indexDocumentName, Map<String, String> cameraModelToDocumentName, List<Work> works) {
        StringWriter htmlDocument = new StringWriter();
        Context context = new Context();
        context.setVariable("indexPageLink", indexDocumentName);
        context.setVariable("cameraModelPageLinks", cameraModelToDocumentName);
        context.setVariable("works", works);
        templateEngine.process("imagesByCameraMake", context, htmlDocument);
        return htmlDocument;
    }

    private StringWriter buildCameraMakeAndModelHTMLPage(String indexDocumentName, String cameraMakeDocumentName, List<Work> works) {
        StringWriter htmlDocument = new StringWriter();
        Context context = new Context();
        context.setVariable("indexPageLink", indexDocumentName);
        context.setVariable("cameraMakePageLink", cameraMakeDocumentName);
        context.setVariable("works", works);
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
