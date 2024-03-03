package fr.eris.yaml.utils.storage;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Tuple<A, B> {
    @Setter public A a;
    @Setter public B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }
    public Tuple() {
        this(null, null);
    }
}
