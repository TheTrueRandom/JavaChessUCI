package output;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
@EngineToGUI("option")
public class Option extends UCIOutput {

    @EngineToGUI("name")
    String name;

    @EngineToGUI("type")
    String type;

    @EngineToGUI("default")
    String defaultValue;

    @EngineToGUI("min")
    String minValue;

    @EngineToGUI("max")
    String maxValue;

    public Option(String data) {
        super(data);
    }
}

