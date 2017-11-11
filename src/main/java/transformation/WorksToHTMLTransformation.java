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
import java.util.function.Function;
import java.util.stream.Collectors;

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

        //first, construct an index on the list of works to enable fast search by camera make
        Map<String, List<Work>> makeIndex = works.stream().collect(Collectors.groupingBy(Work::getExif_make));

        //second, construct a 2-layered index to enable fast search by camera make and camera model
        Map<String, Map<String, List<Work>>> makeAndModelIndex = works.stream().collect(Collectors.groupingBy(Work::getExif_make, Collectors.groupingBy(Work::getExif_model)));

        return buildHTMLPages(works, makeIndex, makeAndModelIndex);
    }

    private Map<String, StringWriter> buildHTMLPages(List<Work> works, Map<String, List<Work>> makeIndex, Map<String, Map<String, List<Work>>> makeAndModelIndex) {
        String indexDocumentName = getDocumentNameForIndexHTMLPage();
        Map<String, String> cameraMakeToDocumentName = makeIndex.keySet().stream().collect(Collectors.toMap(Function.identity(), cameraMake -> getDocumentNameForCameraMakeHTMLPage(cameraMake)));
        Map<String, Map<String, String>> cameraMakeAndModelToDocumentName = makeAndModelIndex.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().keySet().stream().collect(Collectors.toMap(Function.identity(), cameraModel -> getDocumentNameForCameraMakeAndModelHTMLPage(entry.getKey(), cameraModel)))));

        // then create the html pages and map the document name to the corresponding html page
        StringWriter htmlPage = null;
        Map<String, StringWriter> documentNameToHTMLPage = new HashMap<>();

        // create the index html page and map its document name to it
        htmlPage = buildIndexHTMLPage(cameraMakeToDocumentName, works.subList(0, 10));
        documentNameToHTMLPage.put(indexDocumentName, htmlPage);

        // for each camera make, map its document name to the created HTML page
        makeIndex.forEach((cameraMake, worksOfCameraMake) -> documentNameToHTMLPage.put(cameraMakeToDocumentName.get(cameraMake), buildCameraMakeHTMLPage(indexDocumentName, cameraMakeAndModelToDocumentName.get(cameraMake), worksOfCameraMake.subList(0, 10))));

        // create a html page for each camera make's model and map its document name to it
        makeAndModelIndex.forEach((cameraMake, cameraModelToWorks) -> cameraModelToWorks.forEach((cameraModel, worksOfCameraModel) -> documentNameToHTMLPage.put(cameraMakeAndModelToDocumentName.get(cameraMake).get(cameraModel), buildCameraMakeAndModelHTMLPage(indexDocumentName, cameraMakeToDocumentName.get(cameraMake), worksOfCameraModel.subList(0, 10)))));

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
