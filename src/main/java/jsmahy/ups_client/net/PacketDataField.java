package jsmahy.ups_client.net;

import java.lang.annotation.*;

/**
 * Marks some field of a packet as part of its data. The field must either implement {@link PacketData} , be of type
 * {@link String}, {@link Boolean}, or {@link Number}, otherwise an {@link AnnotationTypeMismatchException} is thrown.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PacketDataField {

	/**
	 * @return The order of the packet data
	 */
	int value() default 0;
}
