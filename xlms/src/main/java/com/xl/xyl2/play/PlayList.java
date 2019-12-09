package com.xl.xyl2.play;


import java.io.Serializable;
import java.util.List;

//播放区域
public class PlayList implements Serializable{
    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public List<PlayProgram> getPrograms() {
        return programs;
    }

    public void setPrograms(List<PlayProgram> programs) {
        this.programs = programs;
    }

    public String identification; //播放列表唯一标识
    public List<PlayProgram> programs;
}
