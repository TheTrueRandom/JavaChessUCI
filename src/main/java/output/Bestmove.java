package output;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@EngineToGUI("bestmove")
public class Bestmove extends UCIOutput {

    @EngineToGUI("bestmove")
    private String bestmove;

    @EngineToGUI("ponder")
    private String ponder;

    public Bestmove(String data) {
        super(data);
    }
}
