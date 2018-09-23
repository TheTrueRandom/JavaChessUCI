package output;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@EngineToGUI("info")
public class Info extends UCIOutput {

    @EngineToGUI("depth")
    Integer depth;

    @EngineToGUI("seldepth")
    Integer seldepth;

    @EngineToGUI("time")
    Integer time;

    @EngineToGUI("nodes")
    Integer nodes;

    @EngineToGUI("pv")
    List<String> pv;

    @EngineToGUI("multipv")
    Integer multipv;

    @EngineToGUI("score")
    Score score;

    @EngineToGUI("currmove")
    String currmove;

    @EngineToGUI("currmovenumber")
    Integer currmovenumber;

    @EngineToGUI("hashfull")
    Integer hashfull;

    @EngineToGUI("nps")
    Integer nps;

    @EngineToGUI("tbhits")
    Integer tbhits;

    @EngineToGUI("sbhits")
    Integer sbhits;

    @EngineToGUI("string")
    String string;

    @EngineToGUI("refutation")
    List<String> refutation;

    @EngineToGUI("currline")
    List<String> currline; // TODO: 09.09.18 cpunumber

    public Info(String data) {
        super(data);
    }
}
