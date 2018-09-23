package output;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@EngineToGUI("score")
public class Score {

    /**
     * Score in centipawns from the engine's perspective.
     * null if not available
     */
    @EngineToGUI("cp")
    Integer cp;

    /**
     * Mate in moves (not plies).
     * Negative if the engine is getting mated.
     * null if not available
     */
    @EngineToGUI("mate")
    Integer mate;

    /**
     * If true -> Score is just a lowerbound
     * Usually false
     */
    @EngineToGUI("lowerbound")
    boolean lowerbound;

    /**
     * If true -> Score is just an upperbound
     * Usually false
     */
    @EngineToGUI("upperbound")
    boolean upperbound;

    @Override
    public String toString() {
        return cp == null ? "mate " + mate : "cp " + cp;
    }
}
