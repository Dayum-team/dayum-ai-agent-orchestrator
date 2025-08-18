package dayum.aiagent.orchestrator.client.chat.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.reflect.*;
import java.util.*;

public final class JsonSchemaGenerator {

  public static SchemaFactory.JsonSchema generate(Type type) {
    if (type instanceof ParameterizedType pt) {
      Type raw = pt.getRawType();
      if (raw instanceof Class<?> rawClz && isListLike(rawClz)) {
        Type itemType = pt.getActualTypeArguments()[0];
        return SchemaFactory.ArraySchema.array(generate(itemType)).build();
      }
      return emptyObject();
    }

    if (!(type instanceof Class<?> clz)) return emptyObject();

    if (isIntegerLike(clz)) return SchemaFactory.IntegerSchema.integer().build();

    if (clz == String.class || clz == CharSequence.class) {
      return SchemaFactory.StringSchema.string().build();
    }

    if (clz.isEnum()) {
      String[] names =
          Arrays.stream(clz.getEnumConstants())
              .map(e -> ((Enum<?>) e).name())
              .toArray(String[]::new);
      return SchemaFactory.StringSchema.string().enumeration(names).build();
    }

    if (clz.isArray()) {
      return SchemaFactory.ArraySchema.array(JsonSchemaGenerator.generate(clz.getComponentType()))
          .build();
    }

    if (clz.isRecord()) {
      var comps = clz.getRecordComponents();
      var builder = SchemaFactory.ObjectSchema.object();
      List<String> required = new ArrayList<>();
      for (RecordComponent rc : comps) {
        String name = jsonName(rc, rc.getName());
        builder.property(name, generate(rc.getGenericType()));
        if (isRequired(rc.getType(), rc.getAnnotation(JsonProperty.class))) {
          required.add(name);
        }
      }
      if (!required.isEmpty()) builder.required(required.toArray(String[]::new));
      return builder.build();
    }

    Field[] fields = clz.getDeclaredFields();
    List<Field> elems = new ArrayList<>();
    for (Field f : fields) {
      if (!Modifier.isStatic(f.getModifiers())) elems.add(f);
    }
    if (!elems.isEmpty()) {
      var builder = SchemaFactory.ObjectSchema.object();
      List<String> required = new ArrayList<>();
      for (Field f : elems) {
        String name = jsonName(f, f.getName());
        builder.property(name, generate(f.getGenericType()));
        if (isRequired(f.getType(), f.getAnnotation(JsonProperty.class))) {
          required.add(name);
        }
      }
      if (!required.isEmpty()) builder.required(required.toArray(String[]::new));
      return builder.build();
    }

    return emptyObject();
  }

  private static boolean isListLike(Class<?> clz) {
    return List.class.isAssignableFrom(clz) || Collection.class.isAssignableFrom(clz);
  }

  private static boolean isIntegerLike(Class<?> clz) {
    return clz == int.class
        || clz == Integer.class
        || clz == long.class
        || clz == Long.class
        || clz == short.class
        || clz == Short.class;
  }

  private static boolean isRequired(Class<?> type, JsonProperty jsonProperty) {
    if (jsonProperty != null && jsonProperty.required()) return true;
    return type.isPrimitive();
  }

  private static String jsonName(AnnotatedElement elt, String fallback) {
    JsonProperty p = elt.getAnnotation(JsonProperty.class);
    return (p != null && !p.value().isEmpty()) ? p.value() : fallback;
  }

  private static SchemaFactory.ObjectSchema emptyObject() {
    return SchemaFactory.ObjectSchema.object().build();
  }
}
