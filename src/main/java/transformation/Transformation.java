package transformation;

public interface Transformation<SourceType extends Object, TargetType extends Object> {

    TargetType apply(SourceType source);
}
