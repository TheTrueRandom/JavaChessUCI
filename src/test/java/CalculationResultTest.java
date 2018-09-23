import org.junit.jupiter.api.Test;
import output.CalculationResult;
import output.Info;
import output.OutputFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CalculationResultTest {
    @Test
    void calculationResultMultiPv1() {
        String[] rawInfos = new String[]{
                "info depth 20 seldepth 29 multipv 1 score cp 65 nodes 4221919 nps 2040560 hashfull 981 tbhits 0 time 2069 pv e2e4 e7e5 b1c3 b8c6 f1c4 g8f6 g1f3 f8e7 e1g1 d7d6 d2d3 e8g8 c4b3 c8d7 c1d2 c6a5 c3d5 a5b3 d5f6 e7f6 a2b3 f6e7",
                "info depth 21 seldepth 27 multipv 1 score cp 62 nodes 5237118 nps 2038582 hashfull 990 tbhits 0 time 2569 pv e2e4 e7e5 g1f3 b8c6 f1c4 g8f6 e1g1 f8e7 d2d3 d7d6 b1c3 e8g8 h2h3 c6a5 c4b3 c8d7 c1e3 h7h6 d3d4 e5d4 e3d4 a5b3 a2b3",
                "info depth 22 currmove d2d4 currmovenumber 2",
                "info depth 22 currmove d2d3 currmovenumber 3",
                "info depth 22 currmove g1f3 currmovenumber 4",
                "info depth 22 currmove b2b4 currmovenumber 5",
                "info depth 22 currmove b1a3 currmovenumber 6",
                "info depth 22 currmove a2a4 currmovenumber 17",
                "info depth 22 currmove h2h4 currmovenumber 18",
                "info depth 22 currmove g2g3 currmovenumber 19",
                "info depth 22 currmove g1h3 currmovenumber 20",
                "info depth 22 seldepth 28 multipv 1 score cp 68 nodes 6377091 nps 2030920 hashfull 996 tbhits 0 time 3140 pv e2e4 e7e5 g1f3 b8c6 f1c4 g8f6 e1g1 f8e7 d2d3 d7d6 h2h3 e8g8 b1c3 c6a5 c4b3 c8d7 c3e2 h7h6 c1d2 c7c5 e2g3 a8c8 f1e1 a5b3 a2b3",
                "info depth 23 currmove e2e4 currmovenumber 1",
                "info depth 23 currmove d2d4 currmovenumber 2",
                "info depth 23 currmove b1c3 currmovenumber 3",
                "info depth 23 currmove g1f3 currmovenumber 4",
                "info depth 23 currmove e2e3 currmovenumber 5",
                "info depth 23 currmove c2c4 currmovenumber 6",
                "info depth 23 currmove a2a4 currmovenumber 19",
                "info depth 23 currmove g1h3 currmovenumber 20",
                "info depth 23 seldepth 30 multipv 1 score cp 59 upperbound nodes 9104109 nps 2025836 hashfull 999 tbhits 0 time 4494 pv e2e4 e7e6",
                "info depth 23 currmove e2e4 currmovenumber 1",
                "info depth 23 seldepth 31 multipv 1 score cp 69 lowerbound nodes 10505183 nps 2028810 hashfull 999 tbhits 0 time 5178 pv e2e4",
                "info depth 23 currmove e2e4 currmovenumber 1",
                "info depth 23 currmove d2d4 currmovenumber 2"
        };
        CalculationResult calculationResult = new CalculationResult();
        for (String rawInfo : rawInfos) {
            calculationResult.processInfo((Info) OutputFactory.generateUCIOutput(rawInfo));
        }

        assertThat(calculationResult.getLastScoreInfo(1).getDepth(), is(23));
        assertThat(calculationResult.getLastScoreInfo(1).getPv().get(0), is("e2e4"));
        assertThat(calculationResult.getLastScoreInfo(1).getScore().getMate(), is(nullValue()));
        assertThat(calculationResult.getLastScoreInfo(1).getScore().getCp(), is(69));
        assertThat(calculationResult.getLastScoreInfo(1).getScore().isLowerbound(), is(true));
        assertThat(calculationResult.getLastScoreInfo(1).getScore().isUpperbound(), is(false));
        assertThat(calculationResult.getLastScoreInfo(2), is(nullValue()));
    }

    @Test
    void calculationResultMultiPv3() {
        String[] rawInfos = new String[]{
                "info depth 1 seldepth 1 multipv 1 score mate 14 nodes 105 nps 105000 tbhits 0 time 1 pv d4d5",
                "info depth 1 seldepth 1 multipv 2 score cp 845 nodes 105 nps 105000 tbhits 0 time 1 pv e6d5",
                "info depth 1 seldepth 1 multipv 3 score cp 743 nodes 105 nps 105000 tbhits 0 time 1 pv g2g3",
                "info depth 2 seldepth 2 multipv 1 score mate 14 nodes 187 nps 187000 tbhits 0 time 1 pv d4d5 c6a8",
                "info depth 5 seldepth 5 multipv 1 score mate 14 nodes 983 nps 983000 tbhits 0 time 1 pv d4d5 c6a8 g2h3 a7a6 f7c7",
                "info depth 5 seldepth 5 multipv 2 score cp 753 nodes 983 nps 983000 tbhits 0 time 1 pv e6d5 c6c8 f7f4 c8d8 f4e4",
                "info depth 56 currmove d4d5 currmovenumber 1",
                "info depth 56 currmove f7f3 currmovenumber 7",
                "info depth 56 currmove g2f1 currmovenumber 8",
                "info depth 56 currmove g2g1 currmovenumber 9",
                "info depth 56 seldepth 16 multipv 1 score mate 8 nodes 20968019 nps 4327764 hashfull 942 tbhits 0 time 4845 pv d4d5 c6a8 f2f3 a8b8 d5d6 a7a5 d6d7 b8b2 g2h3 b2b8 f7e8 h8h7 e8b8 a5a4 b8g8",
                "info depth 55 seldepth 16 multipv 2 score mate 8 nodes 20968019 nps 4327764 hashfull 942 tbhits 0 time 4845 pv f2f3 c6a8 d4d5 a8b8 d5d6 a7a5 d6d7 b8b2 g2h3 b2b8 f7e8 h8h7 e8b8 a5a4 b8g8",
                "info depth 55 seldepth 18 multipv 3 score mate 9 nodes 20968019 nps 4327764 hashfull 942 tbhits 0 time 4845 pv g2h2 c6a8 d4d5 a8b8 h2h3 b8a8 f2f3 a7a6 d5d6 h8h7 d6d7 a8f3 f7f3 a6a5 d7d8q a5a4 d8g8",
                "info depth 56 currmove f2f3 currmovenumber 2",
                "info depth 56 currmove f7f3 currmovenumber 3",
                "info depth 56 currmove g2f1 currmovenumber 8",
                "info depth 56 currmove g2g1 currmovenumber 9",
                "info depth 56 seldepth 16 multipv 1 score mate 8 nodes 21437825 nps 4331748 hashfull 944 tbhits 0 time 4949 pv d4d5 c6a8 f2f3 a8b8 d5d6 a7a5 d6d7 b8b2 g2h3 b2b8 f7e8 h8h7 e8b8 a5a4 b8g8",
                "info depth 56 seldepth 16 multipv 2 score mate 8 nodes 21437825 nps 4331748 hashfull 944 tbhits 0 time 4949 pv f2f3 c6a8 d4d5 a8b8 d5d6 a7a5 d6d7 b8b2 g2h3 b2b8 f7e8 h8h7 e8b8 a5a4 b8g8",
                "info depth 55 seldepth 18 multipv 3 score mate 9 nodes 21437825 nps 4331748 hashfull 944 tbhits 0 time 4949 pv g2h2 c6a8 d4d5 a8b8 h2h3 b8a8 f2f3 a7a6 d5d6 h8h7 d6d7 a8f3 f7f3 a6a5 d7d8q a5a4 d8g8",
                "info depth 56 currmove g2h2 currmovenumber 3",
                "info depth 56 seldepth 16 multipv 1 score mate 8 nodes 21659372 nps 4331008 hashfull 945 tbhits 0 time 5001 pv d4d5 c6a8 f2f3 a8b8 d5d6 a7a5 d6d7 b8b2 g2h3 b2b8 f7e8 h8h7 e8b8 a5a4 b8g8",
                "info depth 56 seldepth 16 multipv 2 score mate 8 nodes 21659372 nps 4331008 hashfull 945 tbhits 0 time 5001 pv f2f3 c6a8 d4d5 a8b8 d5d6 a7a5 d6d7 b8b2 g2h3 b2b8 f7e8 h8h7 e8b8 a5a4 b8g8",
                "info depth 55 seldepth 18 multipv 3 score mate 9 nodes 21659372 nps 4331008 hashfull 945 tbhits 0 time 5001 pv g2h2 c6a8 d4d5 a8b8 h2h3 b8a8 f2f3 a7a6 d5d6 h8h7 d6d7 a8f3 f7f3 a6a5 d7d8q a5a4 d8g8"
        };

        CalculationResult calculationResult = new CalculationResult();
        for (String rawInfo : rawInfos) {
            calculationResult.processInfo((Info) OutputFactory.generateUCIOutput(rawInfo));
        }

        assertThat(calculationResult.getLastScoreInfo(1).getDepth(), is(56));
        assertThat(calculationResult.getLastScoreInfo(1).getPv().get(0), is("d4d5"));
        assertThat(calculationResult.getLastScoreInfo(1).getScore().getMate(), is(8));
        assertThat(calculationResult.getLastScoreInfo(1).getScore().getCp(), is(nullValue()));
        assertThat(calculationResult.getLastScoreInfo(2).getDepth(), is(56));
        assertThat(calculationResult.getLastScoreInfo(2).getPv().get(0), is("f2f3"));
        assertThat(calculationResult.getLastScoreInfo(2).getScore().getMate(), is(8));
        assertThat(calculationResult.getLastScoreInfo(2).getScore().getCp(), is(nullValue()));
        assertThat(calculationResult.getLastScoreInfo(3).getDepth(), is(55));
        assertThat(calculationResult.getLastScoreInfo(3).getPv().get(0), is("g2h2"));
        assertThat(calculationResult.getLastScoreInfo(3).getScore().getMate(), is(9));
        assertThat(calculationResult.getLastScoreInfo(3).getScore().getCp(), is(nullValue()));
        assertThat(calculationResult.getLastScoreInfo(4), is(nullValue()));
    }
}
