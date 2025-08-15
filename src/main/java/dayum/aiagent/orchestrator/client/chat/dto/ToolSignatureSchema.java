package dayum.aiagent.orchestrator.client.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ToolSignatureSchema {
  private ToolSignatureSchema() {}

  public sealed interface JsonSchema
      permits ObjectSchema, ArraySchema, StringSchema, IntegerSchema {}

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static final class ObjectSchema implements JsonSchema {
    public final String type = "object";
    public final Map<String, JsonSchema> properties;
    public final List<String> required;

    private ObjectSchema(Map<String, JsonSchema> properties, List<String> required) {
      this.properties = properties;
      this.required = required;
    }

    public static Builder object() {
      return new Builder();
    }

    public static final class Builder {
      private final Map<String, JsonSchema> props = new LinkedHashMap<>();
      private final List<String> req = new ArrayList<>();

      public Builder property(String name, JsonSchema schema) {
        props.put(name, schema);
        return this;
      }

      public Builder required(String... names) {
        req.addAll(Arrays.asList(names));
        return this;
      }

      public ObjectSchema build() {
        return new ObjectSchema(props, req.isEmpty() ? null : List.copyOf(req));
      }
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static final class ArraySchema implements JsonSchema {
    public final String type = "array";
    public final JsonSchema items;
    public final Integer minItems;
    public final Integer maxItems;
    public final Boolean uniqueItems;

    private ArraySchema(JsonSchema items, Integer minItems, Integer maxItems, Boolean uniqueItems) {
      this.items = items;
      this.minItems = minItems;
      this.maxItems = maxItems;
      this.uniqueItems = uniqueItems;
    }

    public static Builder array(JsonSchema items) {
      return new Builder(items);
    }

    public static final class Builder {
      private final JsonSchema items;
      private Integer minItems;
      private Integer maxItems;
      private Boolean uniqueItems;

      private Builder(JsonSchema items) {
        this.items = items;
      }

      public Builder minItems(int value) {
        this.minItems = value;
        return this;
      }

      public Builder maxItems(int value) {
        this.maxItems = value;
        return this;
      }

      public Builder uniqueItems(boolean value) {
        this.uniqueItems = value;
        return this;
      }

      public ArraySchema build() {
        return new ArraySchema(items, minItems, maxItems, uniqueItems);
      }
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static final class StringSchema implements JsonSchema {
    public final String type = "string";
    public final List<String> _enum;
    public final String pattern;

    private StringSchema(List<String> _enum, String pattern) {
      this._enum = _enum;
      this.pattern = pattern;
    }

    public static Builder string() {
      return new Builder();
    }

    public static final class Builder {
      private List<String> _enum;
      private String pattern;

      public Builder enumeration(String... values) {
        this._enum = Arrays.asList(values);
        return this;
      }

      public Builder pattern(String regex) {
        this.pattern = regex;
        return this;
      }

      public StringSchema build() {
        return new StringSchema(_enum, pattern);
      }
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static final class IntegerSchema implements JsonSchema {
    public final String type = "integer";
    public final Integer minimum;
    public final Integer maximum;

    @JsonProperty("default")
    public final Integer defaultValue;

    private IntegerSchema(Integer minimum, Integer maximum, Integer defaultValue) {
      this.minimum = minimum;
      this.maximum = maximum;
      this.defaultValue = defaultValue;
    }

    public static Builder integer() {
      return new Builder();
    }

    public static final class Builder {
      private Integer minimum;
      private Integer maximum;
      private Integer defaultValue;

      public Builder minimum(int value) {
        this.minimum = value;
        return this;
      }

      public Builder maximum(int value) {
        this.maximum = value;
        return this;
      }

      public Builder defaultValue(int value) {
        this.defaultValue = value;
        return this;
      }

      public IntegerSchema build() {
        return new IntegerSchema(minimum, maximum, defaultValue);
      }
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public record ToolSchema(String name, String description, JsonSchema parameters) {}
}
