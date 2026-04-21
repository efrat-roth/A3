package DocumentStore;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class Document {
    @Getter
    private UUID id = UUID.randomUUID();
    @Setter @Getter @NonNull
    private Map<String,String> fields;

}