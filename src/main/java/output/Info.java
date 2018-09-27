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
    private Integer depth;

    @EngineToGUI("seldepth")
    private Integer seldepth;

    @EngineToGUI("time")
    private Integer time;

    @EngineToGUI("nodes")
    private Integer nodes;

    @EngineToGUI("pv")
    private List<String> pv;

    @EngineToGUI("multipv")
    private Integer multipv;

    @EngineToGUI("score")
    private Score score;

    @EngineToGUI("currmove")
    private String currmove;

    @EngineToGUI("currmovenumber")
    private Integer currmovenumber;

    @EngineToGUI("hashfull")
    private Integer hashfull;

    @EngineToGUI("nps")
    private Integer nps;

    @EngineToGUI("tbhits")
    private Integer tbhits;

    @EngineToGUI("sbhits")
    private Integer sbhits;

    @EngineToGUI("string")
    private String string;

    @EngineToGUI("refutation")
    private List<String> refutation;

    @EngineToGUI("currline")
    private List<String> currline; // TODO: 09.09.18 cpunumber

    public Info(String data) {
        super(data);
    }
}
