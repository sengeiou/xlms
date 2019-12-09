package com.gdswlw.library.http;

/**
 * Created by shihuanzhang on 2017-11-13.
 */

public enum UploadType {

    INPUT_FILE("file ",0),INPUT_STREAM("InputStream ", 1), BYTE_ARRAY_INPUT_STREAM("ByteArrayInputStream", 2);
    private String name ;
    private int index ;

    private UploadType( String name , int index ){
        this.name = name ;
        this.index = index ;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
}