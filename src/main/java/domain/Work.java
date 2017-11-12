package domain;

import org.simpleframework.xml.*;

import java.util.Map;

@Root(name="work", strict = false)
public class Work {

    @Element
    private int id;

    @ElementMap(name = "urls", entry = "url", key = "type", attribute = true)
    private Map<String, String> urlsOfImages;

    @Element(name = "model", required = false)
    @Path("exif")
    private String exif_model;

    @Element(name = "make", required = false)
    @Path("exif")
    private String exif_make;

    public Work() {}

    public Work(int id, Map<String, String> urlsOfImages, String exif_model, String exif_make) {
        this.id = id;
        this.urlsOfImages = urlsOfImages;
        this.exif_model = exif_model;
        this.exif_make = exif_make;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Work work = (Work) o;

        if (id != work.id) return false;
        if (urlsOfImages != null ? !urlsOfImages.equals(work.urlsOfImages) : work.urlsOfImages != null) return false;
        if (exif_model != null ? !exif_model.equals(work.exif_model) : work.exif_model != null) return false;
        return exif_make != null ? exif_make.equals(work.exif_make) : work.exif_make == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (urlsOfImages != null ? urlsOfImages.hashCode() : 0);
        result = 31 * result + (exif_model != null ? exif_model.hashCode() : 0);
        result = 31 * result + (exif_make != null ? exif_make.hashCode() : 0);
        return result;
    }
}
