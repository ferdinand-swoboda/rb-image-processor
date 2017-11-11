package transformation;

public interface Transformation<SourceType, TargetType> {

    TargetType apply(SourceType source);
}
