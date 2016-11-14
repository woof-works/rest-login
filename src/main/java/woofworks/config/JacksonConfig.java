package woofworks.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson specific configuration
 * 
 * @author Timothy
 *
 */
@Configuration
public class JacksonConfig {
	public static final DateTimeFormatter LOCAL_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

	public static final DateTimeFormatter LOCAL_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	/**
	 * Creates a object mapper for jackson to handle LocalDates using a custom serial / deserializer
	 * Taken from https://touk.pl/blog/2016/02/12/formatting-java-time-with-spring-boot-using-json/
	 * 
	 * @return
	 */
	@Bean
	@Primary
	public ObjectMapper serializingObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		JavaTimeModule javaTimeModule = new JavaTimeModule();

		javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
		javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());

		javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
		javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());

		objectMapper.registerModule(javaTimeModule);

		return objectMapper;
	}

	/**
	 * Serializes LocalDate's into a specific format
	 * Taken from https://touk.pl/blog/2016/02/12/formatting-java-time-with-spring-boot-using-json/
	 * 
	 * @author Timothy
	 *
	 */
	public class LocalDateSerializer extends JsonSerializer<LocalDate> {

		@Override
		public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers)
				throws IOException {
			gen.writeString(value.format(LOCAL_DATE));
		}
	}

	/**
	 * Deserializes LocalStrings into a LocalDate
	 * Taken from https://touk.pl/blog/2016/02/12/formatting-java-time-with-spring-boot-using-json/
	 * 
	 * @author Timothy
	 *
	 */
	public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

		@Override
		public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			return LocalDate.parse(p.getValueAsString(), LOCAL_DATE);
		}
	}

	/**
	 * Serializer for local date time
	 * 
	 * @author timothy
	 *
	 */
	public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

		@Override
		public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
				throws IOException {
			gen.writeString(value.format(LOCAL_DATE_TIME));
		}
	}

	/**
	 * Deserializer for local date time
	 * 
	 * @author timothy
	 *
	 */
	public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

		@Override
		public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException {
			return LocalDateTime.parse(p.getValueAsString(), LOCAL_DATE_TIME);
		}
	}
}
