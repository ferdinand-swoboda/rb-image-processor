package transformation;

/**
 * Represents a stateless transformation that generates data of @param{TargetType} based on data of @param{TargetType}.
 * The data of @param{TargetType} is not manipulated.
 * @param <SourceType> the source type before applying the transformation
 * @param <TargetType> the target type after applying the transformation
 */
public interface Transformation<SourceType, TargetType> {

    /**
     * Transforms the given data into data of the target type. The given data stays untouched.
     * @param data the data to be transformed
     * @return the transformed data
     */
    TargetType apply(SourceType data);
}
