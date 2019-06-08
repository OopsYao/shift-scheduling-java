package ssssuper.vos.dispatcher;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) {
        EnhancedDispatcher.Code jj = new EnhancedDispatcher.Code(true, "jj");
        EnhancedDispatcher.Code ssjjjj = new EnhancedDispatcher.Code(true, "jj");
        EnhancedDispatcher.Code ss = new EnhancedDispatcher.Code(true, "jj ");
        assert ssjjjj.equals(jj);
        assert !ss.equals(jj);

    }

}
