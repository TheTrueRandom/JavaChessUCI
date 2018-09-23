package output;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
@EngineToGUI("id")
public class Id extends UCIOutput {

    @EngineToGUI("name")
    String name;

    @EngineToGUI("author")
    String author;

    public Id(String data) {
        super(data);
    }
}
