package output;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
@EngineToGUI("option")
public class Option extends UCIOutput {

    @EngineToGUI("name")
    private String name;

    @EngineToGUI("type")
    private String type;

    @EngineToGUI("default")
    private String defaultValue;

    @EngineToGUI("min")
    private String minValue;

    @EngineToGUI("max")
    private String maxValue;

    public Option(String data) {
        super(data);
    }
}

