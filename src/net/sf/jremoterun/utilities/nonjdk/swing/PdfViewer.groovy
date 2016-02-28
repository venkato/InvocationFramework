package net.sf.jremoterun.utilities.nonjdk.swing

import groovy.transform.CompileStatic
import net.infonode.docking.View
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.idwutils.ViewAndPanel
import org.icepdf.core.pobjects.Document
import org.icepdf.ri.common.ComponentKeyBinding
import org.icepdf.ri.common.SwingController
import org.icepdf.ri.common.SwingViewBuilder
import org.icepdf.ri.common.views.DocumentViewController

import javax.swing.*
import java.util.logging.Logger

@CompileStatic
class PdfViewer {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File file
    SwingController controller
    SwingViewBuilder factory
    JPanel viewerComponentPanel

    PdfViewer(File filePath) {
        this.file = filePath

        controller = new SwingController();

// Build a SwingViewFactory configured with the controller

        factory = new SwingViewBuilder(controller);

// Use the factory to build a JPanel that is pre-configured
//with a complete, active Viewer UI.

        viewerComponentPanel = factory.buildViewerPanel();
        factory.buildFitActualSizeButton()
// add copy keyboard command
        ComponentKeyBinding.install(controller, viewerComponentPanel);

// add interactive mouse link annotation support via callback
        controller.getDocumentViewController().setAnnotationCallback(
                new org.icepdf.ri.common.MyAnnotationCallback(
                        controller.getDocumentViewController()));
        getDocumentComtroller().setViewType(org.icepdf.ri.common.views.DocumentViewControllerImpl.ONE_COLUMN_VIEW)
        getDocumentComtroller().setFitMode(DocumentViewController.PAGE_FIT_WINDOW_WIDTH)
        controller.openDocument(filePath.absolutePath);
        Document document = controller.getDocument()

    }

    Document getDocument(){
        return controller.getDocument();
    }

    DocumentViewController getDocumentComtroller(){
        return controller.getDocumentViewController();
    }




    View createView() {
        ViewAndPanel viewAndPanel = new ViewAndPanel("${file.name}", viewerComponentPanel)
        return viewAndPanel.view;
    }

}
