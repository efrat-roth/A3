package DocumentStore;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentFactory {
    @Getter
    private Map<String,Document> documents;

    public void addDocument(Document document)
    {
        if (this.documents == null) {
            log.warn("Document has not been initialized: null pointer");
        }
        else if (this.documents.containsKey(document.getId())) {
            log.warn("Document has already been initialized: id " + document.getId());
        }
        else
        {
            if (documents == null) {
                documents = new HashMap<>();
            }
            documents.put(document.getId(), document)
        }
    }
}
