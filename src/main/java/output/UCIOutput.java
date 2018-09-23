package output;

import exception.JuciRuntimeException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

// TODO: 09.09.18 replace reflection (performance)
@Data
@Slf4j
public class UCIOutput {
    String data;

    static {
        System.out.println();
    }

    public UCIOutput(String data) {

        this.data = data;

        if (this.getClass() == UCIOutput.class) {
            return;
        }

        try {
            Map<String, ReflectionBuffer> fields = getEngineFieldsForObject(this);

            LinkedList<String> tokens = getTokens();

            while (tokens.size() > 0) {
                String token = tokens.pollFirst();
                if (!fields.containsKey(token)) {
                    continue;
                }

                ReflectionBuffer reflectionBuffer = fields.get(token);
                Field field = reflectionBuffer.getField();
                Object object = reflectionBuffer.getObject();

                if (field.getType() == Integer.class) {
                    field.set(object, Integer.parseInt(tokens.pollFirst()));
                    continue;
                }

                if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                    field.set(object, true);
                    continue;
                }

                List<String> list = new ArrayList<>();
                list.add(tokens.pollFirst());
                while (tokens.size() > 0 && !fields.containsKey(tokens.peekFirst())) {
                    list.add(tokens.pollFirst());
                }

                if (field.getType() == String.class) {
                    field.set(object, String.join(" ", list));
                    continue;
                }

                if (field.getType() == List.class) {
                    field.set(object, list);
                    continue;
                }

                log.warn("could not set field '{}' for outputClass '{}'", token, object.getClass());
            }
        } catch (IllegalAccessException | InstantiationException e) {
            log.error("failed to fill properties for outputClass {}", this.getClass(), e);
            throw new JuciRuntimeException(e);
        }
    }

    private Map<String, ReflectionBuffer> getEngineFieldsForObject(Object object) throws IllegalAccessException, InstantiationException {
        Map<String, ReflectionBuffer> fields = new HashMap<>();

        for (Field field : object.getClass().getDeclaredFields()) {
            EngineToGUI fieldAnnotation = field.getAnnotation(EngineToGUI.class);

            if (fieldAnnotation == null) {
                continue;
            }

            EngineToGUI fieldClassAnnotation = field.getType().getAnnotation(EngineToGUI.class);
            if (fieldClassAnnotation == null) {
                ReflectionBuffer reflectionBuffer = new ReflectionBuffer();
                reflectionBuffer.setObject(object);
                reflectionBuffer.setField(field);

                fields.put(fieldAnnotation.value(), reflectionBuffer);
            } else {
                Object o = field.getType().newInstance();
                field.set(object, o);
                Map<String, ReflectionBuffer> engineFieldsForObject = getEngineFieldsForObject(o);
                for (Map.Entry<String, ReflectionBuffer> entry : engineFieldsForObject.entrySet()) {
                    if (fields.containsKey(entry.getKey())) {
                        throw new IllegalStateException("duplicate key " + entry.getKey());
                    }
                    fields.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return fields;
    }

    LinkedList<String> getTokens() {
        return new LinkedList<>(Arrays.asList(data.split("\\s")));
    }

    @Override
    public String toString() {
        return data;
    }

    @Data
    private class ReflectionBuffer {
        private Object object;
        private Field Field;
    }
}
