package output;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
@EngineToGUI("id")
public class Id extends UCIOutput {

    @EngineToGUI("name")
    private String name;

    @EngineToGUI("author")
    private String author;

    public Id(String data) {
        super(data);
    }
}
