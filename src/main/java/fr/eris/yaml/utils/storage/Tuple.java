package fr.eris.yaml.utils.storage;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Tuple<A, B> {
    @Setter private A a;
    @Setter private B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }
    public Tuple() {
        this(null, null);
    }

    public boolean equals(Object other) {
        return other instanceof Tuple &&
                ((Tuple<?, ?>) other).getA().equals(this.getA()) &&
                ((Tuple<?, ?>) other).getB().equals(this.getB());
    }
}
