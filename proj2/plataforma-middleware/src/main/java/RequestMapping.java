import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestMapping {
    String path();
    String method() default "GET"; // GET + POST  
    String consumes() default "application/json";
    String produces() default "application/json";
}