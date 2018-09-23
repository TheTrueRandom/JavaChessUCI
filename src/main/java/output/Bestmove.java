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
    String bestmove;

    @EngineToGUI("ponder")
    String ponder;

    public Bestmove(String data) {
        super(data);
    }
}
