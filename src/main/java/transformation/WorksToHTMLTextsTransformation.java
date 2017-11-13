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

/**
 * Represents a transformation that transforms a list of works into a set of HTML texts with corresponding text bodies and text names.
 * The resulting set of HTML texts allows to browse the works' images by camera make and camera model.
 * The transformation uses the Thymeleaf template engine to generate HTML text.
 */
public class WorksToHTMLTextsTransformation implements Transformation<List<Work>, Map<String, StringWriter>> {

    /**
     * the maximum number of images to display in one HTML text
     */
    private static final int NUMBER_OF_IMAGES_TO_DISPLAY = 10;
    /**
     * the HTML templates directory path inside the resources directory
     */
    private static final String TEMPLATES_DIR = "/templates/";
    /**
     * the suffix of the HTML templates
     */
    private static final String TEMPLATES_SUFFIX = ".html";

    /**
     * the template engine used to merge work image data and HTML templates
     */
    private final ITemplateEngine templateEngine;

    /**
     * Creates a @code{WorksToHTMLTextsTransformation}
     */
    public WorksToHTMLTextsTransformation() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setPrefix(TEMPLATES_DIR);
        resolver.setSuffix(TEMPLATES_SUFFIX);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        this.templateEngine = templateEngine;
    }

    /**
     * Transform the given list of works in a set of HTML texts that allows to browse the works' images
     * by camera make and camera model.
     * @param works the given list of works
     * @return the resulting set of HTML texts
     */
    @Override
    public Map<String, StringWriter> apply(List<Work> works) {

        //first, construct an index on the list of works to enable fast search by camera make;
        // works with no make are ignored
        Map<String, List<Work>> makeIndex = works.stream()
                .filter(work -> work.getExif_make() != null)
                .collect(Collectors.groupingBy(Work::getExif_make));

        //second, construct a 2-layered index to enable fast search by camera make and camera model;
        // works with no make or model are ignored
        Map<String, Map<String, List<Work>>> makeAndModelIndex = works.stream()
                .filter(work -> work.getExif_make() != null && work.getExif_model() != null)
                .collect(Collectors.groupingBy(Work::getExif_make, Collectors.groupingBy(Work::getExif_model)));

        return buildHTMLTexts(works, makeIndex, makeAndModelIndex);
    }

    /**
     * builds the set of HTML texts using the given list of works
     * and 2 indices (by camera make and by camera make/camera model) to access it
     * @param works the given list of works
     * @param makeIndex the given index by camera make
     * @param makeAndModelIndex the given index by camera make and camera model
     * @return the set of HTML texts
     */
    private Map<String, StringWriter> buildHTMLTexts(List<Work> works, Map<String, List<Work>> makeIndex, Map<String, Map<String, List<Work>>> makeAndModelIndex) {

        // first, set up the text names to be used as links inside the HTML text bodies, i.e.
        // the text name of the index HTML text
        String indexTextName = getTextNameForIndexHTMLText();

        // for each cameraMake, the text name of the corresponding camera make HTML text
        Map<String, String> cameraMakeToTextName = new HashMap<>();
        for(String cameraMake : makeIndex.keySet()) {
            cameraMakeToTextName.put(cameraMake, getTextNameForCameraMakeHTMLText(cameraMake));
        }

        // for each camera make and model, the text name of the corresponding camera make and model HTML text
        Map<String, Map<String, String>> cameraMakeAndModelToTextName = new HashMap<>();
        for(Map.Entry<String, Map<String, List<Work>>> makeAndModelToWorks : makeAndModelIndex.entrySet()) {
            String cameraMake = makeAndModelToWorks.getKey();
            cameraMakeAndModelToTextName.put(cameraMake, new HashMap<>());
            for(String cameraModel : makeAndModelToWorks.getValue().keySet()) {
                cameraMakeAndModelToTextName.get(cameraMake).put(cameraModel,
                        getTextNameForCameraMakeAndModelHTMLText(cameraMake, cameraModel));
            }
        }

        // then create the HTML text using the created text names
        return generateHTMLTexts(works, makeIndex, makeAndModelIndex, indexTextName, cameraMakeToTextName, cameraMakeAndModelToTextName);
    }

    /**
     * Generates the HTML texts, based on the given list of works and the indices.
     * An HTML text maps a text name to an HTML text body based on the given text name mappings.
     * The given text name mappings are also used to encode href links in the HTML text bodies.
     * @param works
     * @param makeIndex
     * @param makeAndModelIndex
     * @param indexTextName
     * @param cameraMakeToTextName
     * @param cameraMakeAndModelToTextName
     * @return the generated HTML texts where a text name maps to a text body
     */
    private Map<String, StringWriter> generateHTMLTexts(List<Work> works, Map<String, List<Work>> makeIndex, Map<String, Map<String, List<Work>>> makeAndModelIndex, String indexTextName, Map<String, String> cameraMakeToTextName, Map<String, Map<String, String>> cameraMakeAndModelToTextName) {

        StringWriter htmlTextBody;
        Map<String, StringWriter> textNameToTextBody = new HashMap<>();

        // create the index html text body
        htmlTextBody = buildIndexHTMLPage(cameraMakeToTextName,
                works.subList(0, (works.size() < NUMBER_OF_IMAGES_TO_DISPLAY) ? works.size() : NUMBER_OF_IMAGES_TO_DISPLAY));
        // add the new mapping index text name -> index text body to the set of generated texts
        textNameToTextBody.put(indexTextName, htmlTextBody);

        // for each camera make,
        for(Map.Entry<String, List<Work>> cameraMakeToWorks : makeIndex.entrySet()) {
            String cameraMake = cameraMakeToWorks.getKey();
            List<Work> worksOfCameraMake = cameraMakeToWorks.getValue();

            // create the html text body with the index text name and the camera model text names as navigation links
            htmlTextBody = buildCameraMakeHTMLPage(indexTextName,
                    cameraMakeAndModelToTextName.get(cameraMake),
                    worksOfCameraMake.subList(0, (worksOfCameraMake.size() < NUMBER_OF_IMAGES_TO_DISPLAY) ? worksOfCameraMake.size() : NUMBER_OF_IMAGES_TO_DISPLAY));
            // add the new mapping camera make text name -> camera make text body to the set of generated texts
            textNameToTextBody.put(cameraMakeToTextName.get(cameraMake), htmlTextBody);

            // for each camera model,
            for(Map.Entry<String, List<Work>> cameraModelToWorks : makeAndModelIndex.get(cameraMake).entrySet()) {
                String cameraModel = cameraModelToWorks.getKey();
                List<Work> worksOfCameraModel = cameraModelToWorks.getValue();

                // create a html text body for each camera make's model
                htmlTextBody = buildCameraMakeAndModelHTMLPage(indexTextName,
                        cameraMakeToTextName.get(cameraMake),
                        worksOfCameraModel.subList(0, (worksOfCameraModel.size() < NUMBER_OF_IMAGES_TO_DISPLAY) ? worksOfCameraModel.size() : NUMBER_OF_IMAGES_TO_DISPLAY));
                // add the new mapping camera model text name -> camera model text body to the set of generated texts
                textNameToTextBody.put(cameraMakeAndModelToTextName.get(cameraMake).get(cameraModel), htmlTextBody);
            }
        }

        return textNameToTextBody;
    }

    /**
     * Builds the index HTML text body using an HTML template
     * @param cameraMakeToTextName the HTML text names of the camera make texts to be linked to in the text body
     * @param works the list of works to be displayed in the text body
     * @return the index HTML text body
     */
    private StringWriter buildIndexHTMLPage( Map<String, String> cameraMakeToTextName, List<Work> works) {
        StringWriter htmlTextBody = new StringWriter();
        Context context = new Context();
        context.setVariable("cameraMakePageLinks", cameraMakeToTextName);
        context.setVariable("works", works);
        templateEngine.process("index", context, htmlTextBody);
        return htmlTextBody;
    }

    /**
     * Builds the HTML text body for the given works' camera make using an HTML template
     * @param indexTextName the index text name to be linked to in the text body
     * @param cameraModelToTextName the HTML text names of the camera model texts to be linked to in the text body
     * @param works the list of works to be displayed in the text body
     * @return the HTML text body of a certain camera make
     */
    private StringWriter buildCameraMakeHTMLPage(String indexTextName, Map<String, String> cameraModelToTextName, List<Work> works) {
        StringWriter htmlTextBody = new StringWriter();
        Context context = new Context();
        context.setVariable("indexPageLink", indexTextName);
        context.setVariable("cameraModelPageLinks", cameraModelToTextName);
        context.setVariable("works", works);
        templateEngine.process("imagesByCameraMake", context, htmlTextBody);
        return htmlTextBody;
    }

    /**
     * Builds the HTML text body for the given works' camera make and model using an HTML template
     * @param indexTextName the index text name to be linked to in the text body
     * @param cameraMakeTextName the HTML text names of the camera make texts to be linked to in the text body
     * @param works the list of works to be displayed in the text body
     * @return the HTML text body of a certain camera model
     */
    private StringWriter buildCameraMakeAndModelHTMLPage(String indexTextName, String cameraMakeTextName, List<Work> works) {
        StringWriter htmlTextBody = new StringWriter();
        Context context = new Context();
        context.setVariable("indexPageLink", indexTextName);
        context.setVariable("cameraMakePageLink", cameraMakeTextName);
        context.setVariable("works", works);
        templateEngine.process("imagesByCameraMakeAndModel", context, htmlTextBody);
        return htmlTextBody;
    }

    /**
     * @return the text name to be used for the index page
     */
    private String getTextNameForIndexHTMLText() {
        return "index.html";
    }

    /**
     * Returns the text name to be used for the HTML text of the given camera make
     * @param cameraMake the given camera make
     * @return the text name to be used for the HTML text
     */
    private String getTextNameForCameraMakeHTMLText(String cameraMake) {
        return "imagesOf_" + cameraMake.trim().replaceAll(" +", "-") + ".html";
    }

    /**
     * Returns the text name to be used for the HTML text of the given camera make / camera model combination
     * @param cameraMake the given camera make
     * @param cameraModel the given camera model
     * @return the text name to be used for the HTML text
     */
    private String getTextNameForCameraMakeAndModelHTMLText(String cameraMake, String cameraModel) {
        return "imagesOf_" + cameraMake.trim().replaceAll(" +", "-")
                + "_" + cameraModel.trim().replaceAll(" +", "-") + ".html";
    }

}
