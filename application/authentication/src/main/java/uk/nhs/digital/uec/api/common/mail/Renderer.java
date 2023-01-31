package uk.nhs.digital.uec.api.common.mail;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;

@Component
public class Renderer {

    private static final String BASE_PACKAGE_PATH = "/templates";
    private final Configuration configuration;

    public Renderer() {
        configuration = new Configuration(Configuration.VERSION_2_3_28);
        configuration.setClassForTemplateLoading(getClass(), BASE_PACKAGE_PATH);
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setOutputEncoding(StandardCharsets.UTF_8.name());
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(false);
    }

    public String render(String templateName, Object dataModel) throws IOException, TemplateException {
        Template template = configuration.getTemplate(templateName);
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);
        return writer.toString();
    }
}
