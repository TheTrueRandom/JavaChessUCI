package input;

import exception.JuciRuntimeException;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.List;

@Data
@Builder
public class GoCommand {
    private List<String> searchmoves;
    private Boolean ponder;
    private Integer wtime;
    private Integer btime;
    private Integer winc;
    private Integer binc;
    private Integer movestogo;
    private Integer depth;
    private Integer nodes;
    private Integer mate;
    private Integer movetime;
    private Boolean infinite;

    public String getCommand() {
        StringBuilder result = new StringBuilder("go");
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                Object value = field.get(this);

                if (value == null) {
                    continue; //nothing to append
                }

                Class type = field.getType();

                if (type == Boolean.class && !(Boolean) value) {
                    continue;
                }

                result.append(" ").append(field.getName());

                if (type == Boolean.class) {
                    continue;
                }

                if (type == List.class) {
                    for (Object o : ((List) value)) {
                        result.append(" ").append(o);
                    }
                    continue;
                }

                result.append(" ").append(value);
            }
            return result.toString();
        } catch (Exception e) {
            throw new JuciRuntimeException(e);
        }
    }
}
