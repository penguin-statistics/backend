package io.penguinstats.bean;

import java.util.ArrayList;
import java.util.List;

public class Paginator<T> {
    private final List<T> items;

    public Paginator(List<T> items) {
        this.items = items;
    }

    public List<T> getPage(int pageIndex, int pageSize) {
        List<T> page = new ArrayList<>();
        int from = (pageIndex - 1) * pageSize;
        int to = items.size() < pageIndex * pageSize ? items.size() : pageIndex * pageSize;
        for (int i = from; i < to; i++) {
            page.add(items.get(i));
        }

        return page;
    }
}
