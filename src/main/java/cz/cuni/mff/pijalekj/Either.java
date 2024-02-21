package cz.cuni.mff.pijalekj;

import java.util.function.Function;

public class Either<L, R> {
    private L left = null;
    private R right = null;
    private Either(L leftValue, R rightValue) {
        this.left = leftValue;
        this.right = rightValue;
    }
    public static<L, R> Either<L,R> Left(L leftValue) {
        return new Either<L, R>(leftValue, null);
    }

    public static<L, R> Either<L,R> Right(L leftValue) {
        return new Either<L, R>(leftValue, null);
    }

    public L getLeft() throws Exception {
        if (this.left == null) {
            throw new Exception("Left is null: Check the presence before calling!");
        }
        return this.left;
    }
    public R getRight() throws Exception {
        if (this.right == null) {
            throw new Exception("Right is null: Check the presence before calling!");
        }
        return this.right;
    }

    public boolean hasRight() {
        return this.right != null;
    }

    public Either<L, R> bind(Function<R, R> func, Either<L, R> other) {
        if (!this.hasRight()) {
            return this;
        }

        return (Either<L, R>) Right(func.apply(this.right));
    }
}
