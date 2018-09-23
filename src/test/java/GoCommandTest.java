import input.GoCommand;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GoCommandTest {
    @Test
    void goCommandBuilderCorrect() {
        assertThat(GoCommand.builder()
                .searchmoves(Arrays.asList("e2e4", "d2d4"))
                .ponder(true)
                .wtime(5000)
                .btime(10000)
                .winc(5)
                .binc(10)
                .movestogo(2)
                .depth(10)
                .nodes(100000)
                .mate(12)
                .movetime(2000)
                .infinite(true)
                .build()
                .getCommand(), is("go searchmoves e2e4 d2d4 ponder wtime 5000 btime 10000 winc 5 binc 10 movestogo 2 depth 10 nodes 100000 mate 12 movetime 2000 infinite"));

        assertThat(GoCommand.builder().build().getCommand(), is("go"));

        assertThat(GoCommand.builder()
                .movetime(2000)
                .build()
                .getCommand(), is("go movetime 2000"));

        assertThat(GoCommand.builder().infinite(true).build().getCommand(), is("go infinite"));
    }
}
