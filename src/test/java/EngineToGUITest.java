import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import output.*;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class EngineToGUITest {
    @Test
    void info() {
        String rawInfo = "info multipv 1 depth 17 seldepth 34 nodes 17726895 time 1708 nps 10378744 hashfull 999 tbhits 0 score cp 14 upperbound pv e2e4 e7e6 g1f3 d7d5 b1c3 d5d4 c3b5 c7c5 c2c3 g8f6 e4e5 f6d5 c3d4 b8c6 a2a3 a7a6 d1a4 c5d4 b5d4";
        Info info = (Info) OutputFactory.generateUCIOutput(rawInfo);

        assertThat(info.getMultipv(), is(1));
        assertThat(info.getDepth(), is(17));
        assertThat(info.getSeldepth(), is(34));
        assertThat(info.getNodes(), is(17726895));
        assertThat(info.getTime(), is(1708));
        assertThat(info.getNps(), is(10378744));
        assertThat(info.getHashfull(), is(999));
        assertThat(info.getTbhits(), is(0));
        assertThat(info.getScore().getCp(), is(14));
        assertThat(info.getScore().getMate(), is(nullValue()));
        assertThat(info.getScore().isLowerbound(), is(false));
        assertThat(info.getScore().isUpperbound(), is(true));
        assertThat(info.getPv().size(), is(19));
        assertThat(info.getPv().get(0), is("e2e4"));
        assertThat(info.getPv().get(18), is("b5d4"));
    }

    @Test
    void infoScore() {
        Info info = (Info) OutputFactory.generateUCIOutput("info depth 43 seldepth 14 multipv 1 score mate 7 nodes 33499121 nps 34113157 tbhits 0 time 982 pv a3c3 h2h3 c3g3 e5d5 g3c3 h3h4 f7f6 d5e4 g7f7 e4d5 f6f5 d5e5 c3c5");
        Info info2 = (Info) OutputFactory.generateUCIOutput("info score mate -70");

        assertThat(info.getScore().getCp(), is(nullValue()));
        assertThat(info.getScore().getMate(), is(7));
        assertThat(info.getScore().isLowerbound(), is(false));
        assertThat(info.getScore().isUpperbound(), is(false));

        assertThat(info2.getScore().getCp(), is(nullValue()));
        assertThat(info2.getScore().getMate(), is(-70));
        assertThat(info2.getScore().isLowerbound(), is(false));
        assertThat(info2.getScore().isUpperbound(), is(false));
    }

    @Test
    @Disabled
    void reflectionPerformance() {
        OutputFactory.generateUCIOutput("bestmove"); //init reflections
        long start = System.nanoTime();
        int count = 1000;
        for (int i = 0; i < count; i++) {
            OutputFactory.generateUCIOutput("info depth 43 seldepth 14 multipv 1 score mate 7 nodes 33499121 nps 34113157 tbhits 0 time 982 pv a3c3 h2h3 c3g3 e5d5 g3c3 h3h4 f7f6 d5e4 g7f7 e4d5 f6f5 d5e5 c3c5");
        }
        System.out.println("create " + count + " infos took: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms");
    }

    @Test
    void id() {
        String rawId = "id name asdf 010918 64 asdf";
        String rawId2 = "id author X. YZ, asdf";

        assertThat(((Id) OutputFactory.generateUCIOutput(rawId)).getName(), is("asdf 010918 64 asdf"));
        assertThat(((Id) OutputFactory.generateUCIOutput(rawId)).getAuthor(), is(nullValue()));
        assertThat(((Id) OutputFactory.generateUCIOutput(rawId2)).getName(), is(nullValue()));
        assertThat(((Id) OutputFactory.generateUCIOutput(rawId2)).getAuthor(), is("X. YZ, asdf"));
    }

    @Test
    void option() {
        String option = "option name Analysis Contempt type combo default Both var Off var White var Black var Both";
        String option2 = "option name Threads type spin default 1 min 1 max 512";

        assertThat(((Option) OutputFactory.generateUCIOutput(option)).getName(), is("Analysis Contempt"));
        assertThat(((Option) OutputFactory.generateUCIOutput(option)).getType(), is("combo"));
        assertThat(((Option) OutputFactory.generateUCIOutput(option)).getDefaultValue(), is("Both var Off var White var Black var Both"));
        assertThat(((Option) OutputFactory.generateUCIOutput(option)).getMinValue(), is(nullValue()));
        assertThat(((Option) OutputFactory.generateUCIOutput(option)).getMaxValue(), is(nullValue()));

        assertThat(((Option) OutputFactory.generateUCIOutput(option2)).getName(), is("Threads"));
        assertThat(((Option) OutputFactory.generateUCIOutput(option2)).getType(), is("spin"));
        assertThat(((Option) OutputFactory.generateUCIOutput(option2)).getDefaultValue(), is("1"));
        assertThat(((Option) OutputFactory.generateUCIOutput(option2)).getMinValue(), is("1"));
        assertThat(((Option) OutputFactory.generateUCIOutput(option2)).getMaxValue(), is("512"));
    }

    @Test
    void bestmove() {
        String bestmove = "bestmove e2e4 ponder d7d5";
        String bestmove2 = "bestmove e2e4";

        assertThat(((Bestmove) OutputFactory.generateUCIOutput(bestmove)).getBestmove(), is("e2e4"));
        assertThat(((Bestmove) OutputFactory.generateUCIOutput(bestmove)).getPonder(), is("d7d5"));

        assertThat(((Bestmove) OutputFactory.generateUCIOutput(bestmove2)).getBestmove(), is("e2e4"));
        assertThat(((Bestmove) OutputFactory.generateUCIOutput(bestmove2)).getPonder(), is(nullValue()));
    }

    @Test
    void unrecognizedOutput() {
        String unrecognizedOutput = "asdf";

        UCIOutput uciOutput = OutputFactory.generateUCIOutput(unrecognizedOutput);
        assertThat(uciOutput.getClass().getSimpleName(), is(UCIOutput.class.getSimpleName()));
    }
}
