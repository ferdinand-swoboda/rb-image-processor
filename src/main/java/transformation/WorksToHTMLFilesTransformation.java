package transformation;

import domain.Work;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.File;
import java.io.StringWriter;
import java.util.List;

public class WorksToHTMLFilesTransformation implements Transformation<List<Work>, File> {

    private ITemplateEngine templateEngine;

    public WorksToHTMLFilesTransformation() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        this.templateEngine = templateEngine;
    }

    @Override
    public File apply(List<Work> works) {

    }

    private StringWriter buildIndexPage(List<String> imageUrls) {
        StringWriter writer = new StringWriter();
        Context context = new Context();
        templateEngine.process("index", context, writer);
        return writer;
    }

    private StringWriter buildCameraMakePage(String cameraMake, List<String> imageUrls) {
        StringWriter writer = new StringWriter();
        Context context = new Context();
        templateEngine.process("imagesByCameraMake", context, writer);
        return writer;
    }

    private StringWriter buildCameraMakeAndModelPage(String cameraMake, String cameraModel, List<String> imageUrls) {
        StringWriter writer = new StringWriter();
        Context context = new Context();
        templateEngine.process("imagesByCameraMakeAndModel", context, writer);
        return writer;
    }
}
