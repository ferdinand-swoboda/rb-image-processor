package domain;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.LinkedList;
import java.util.List;

@Root(name = "works", strict = false)
public class WorkList {

    @ElementList(inline = true)
    private List<Work> works;

    public WorkList() {
        works = new LinkedList<>();
    }

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkList workList = (WorkList) o;

        return works.equals(workList.works);
    }

    @Override
    public int hashCode() {
        return works.hashCode();
    }
}
