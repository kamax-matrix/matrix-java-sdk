package io.kamax.matrix;

import java.util.Optional;

public class MatrixUserData implements _MatrixUser {

    private _MatrixID mxId;
    private String name;

    @Override
    public _MatrixID getId() {
        return mxId;
    }

    public void setId(_MatrixID mxId) {
        this.mxId = mxId;
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public void setName(String name) {
        this.name = name;
    }

}
