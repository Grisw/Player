package pers.missingno.player.objects;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Music implements Serializable {

    private String title;
    private String singer;
    private String album;
    private long size;
    private int duration;
    private String url;
    private Bitmap pic;

    public Music(String title,String singer,String album,long size,int duration,String url,Bitmap pic){
        this.title=title;
        this.singer=singer;
        this.album=album;
        this.size=size;
        this.duration=duration;
        this.url=url;
        this.pic=pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    @Override
    public boolean equals(Object o) {
        if(url.equals(((Music)o).url)){
            return true;
        }else{
            return false;
        }
    }
}
