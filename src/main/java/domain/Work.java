package domain;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="work", strict = false)
public class Work {

    private int id;

    @ElementList(name = "url")
    @Path("urls")
    private List<String> urlsOfImages;

    @Element(name = "model")
    @Path("exif")
    private String exif_model;

    @Element(name = "make")
    @Path("exif")
    private String exif_make;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getUrlsOfImages() {
        return urlsOfImages;
    }

    public void setUrlsOfImages(List<String> urlsOfImages) {
        this.urlsOfImages = urlsOfImages;
    }

    public String getExif_model() {
        return exif_model;
    }

    public void setExif_model(String exif_model) {
        this.exif_model = exif_model;
    }

    public String getExif_make() {
        return exif_make;
    }

    public void setExif_make(String exif_make) {
        this.exif_make = exif_make;
    }
}
