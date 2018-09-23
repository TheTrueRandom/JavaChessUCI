package output;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// TODO: 09.09.18 replace reflection (performance)
@Slf4j
public class OutputFactory {
    private static final Map<String, Class<UCIOutput>> OUTPUT_MAP = new HashMap<>();

    static {
        Reflections reflections = new Reflections("output");
        Set<Class<?>> outputClasses = reflections.getTypesAnnotatedWith(EngineToGUI.class);
        for (Class<?> outputClass : outputClasses) {
            OUTPUT_MAP.put(outputClass.getAnnotation(EngineToGUI.class).value(), (Class<UCIOutput>) outputClass);
        }
    }

    public static UCIOutput generateUCIOutput(String data) {
        String[] split = data.split("\\s");
        String command = split[0];

        if (OUTPUT_MAP.containsKey(command)) {
            try {
                return OUTPUT_MAP.get(command).getConstructor(String.class).newInstance(data);
            } catch (IllegalAccessException | NoSuchMethodException | InstantiationException e) {
                log.error("Failed to create {}", OUTPUT_MAP.get(command), e);
            } catch (InvocationTargetException e) {
                log.warn("Can not parse '{}' to '{}' command - {}", data, command, e.getCause().getMessage());
            }
        }

        return new UCIOutput(data);
    }
}
