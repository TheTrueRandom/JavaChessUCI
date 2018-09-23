package output;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
@ToString(of = {"bestmove", "ponder"})
public class CalculationResult {
    @Getter
    @Setter
    private String bestmove;

    @Getter
    @Setter
    private String ponder;

    @Getter
    private Map<Integer, Info> scoreMap;

    public CalculationResult() {
        scoreMap = new HashMap<>();
    }

    public void processInfo(Info info) {
        if (info.getScore() != null && info.getMultipv() != null) {
            scoreMap.put(info.getMultipv(), info);
        }
    }

    /**
     * get the last (most recent) info for a multipv (default 1 if no multipv is set)
     * it is guaranteed that {@link output.Info#score} is not null
     *
     * @param multipv
     * @return a info with score or null if no score for the specific multipv
     */
    public Info getLastScoreInfo(int multipv) {
        return scoreMap.get(multipv);
    }

    /**
     * @see #getLastScoreInfo(int)
     */
    public Info getLastScoreInfo() {
        return getLastScoreInfo(1);
    }


    /**
     * get bestmove for a specific multipv
     *
     * @param multipv
     * @return bestmove for a specific multipv, may be null if no information available
     */
    public String getBestmovePv(int multipv) {
        Info info = scoreMap.get(multipv);
        if (info == null) {
            return null;
        }

        if (info.getPv() == null || info.getPv().isEmpty()) {
            return null;
        }

        return info.getPv().get(0);
    }
}
