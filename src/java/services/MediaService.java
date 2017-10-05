package services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "mediaService")
@ApplicationScoped
public class MediaService {
    
    public static final Map<String, String> MIME_LOOKUP_TABLE = new HashMap<>();
    
    static {
        MIME_LOOKUP_TABLE.put("jpg", "image/jpeg");
        MIME_LOOKUP_TABLE.put("jpeg", "image/jpeg");
        MIME_LOOKUP_TABLE.put("png", "image/png");
        MIME_LOOKUP_TABLE.put("gif", "image/gif");
    }
    
    public StreamedContent getImage() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        } else {
            String filename = context.getExternalContext().getRequestParameterMap().get("filename");
            return new DefaultStreamedContent(new FileInputStream(new File(filename)), MIME_LOOKUP_TABLE.get(filename.substring(filename.lastIndexOf('.')+1)));
        }
    }
    
}
