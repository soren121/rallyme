/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    rallyme.core.TemplateServlet
 */

package rallyme.core;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import javax.servlet.http.HttpServlet;

/**
    Base class for any controllers that need to use FreeMarker templates.
    Prevents FreeMarker configuration from being redefined in every class.
 */
public abstract class TemplateServlet extends HttpServlet {
    protected static final long serialVersionUID = 1L;
    protected Configuration freemarker;

    @Override
    public void init() {
         // Initialize FreeMarker
        freemarker = new Configuration(Configuration.VERSION_2_3_25);
        freemarker.setServletContextForTemplateLoading(getServletContext(), "/WEB-INF/templates");
        freemarker.setDefaultEncoding("UTF-8");
        freemarker.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
    }

}
