package com.thundersoft.android.musicplayer.util;

import java.util.ArrayList;
import java.util.List;

public class Pager<T> {
    private final List<T> list;
    private final int perPage;
    private final int totalPage;

    public Pager(List<T> list, int perPage) {
        this.list = list;
        this.perPage = perPage;
        this.totalPage = (int) Math.ceil((double) list.size() / perPage);
    }

    public int getPerPage() {
        return perPage;
    }

    public List<T> getList() {
        return list;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public List<T> paginate(int page) {
        int start = perPage * (page - 1);
        List<T> subList = new ArrayList<>();
        for (int i = start; i < list.size() && i < start + perPage; i++)
            subList.add(list.get(i));
        return subList;
    }

    public String currentAndTotal(int current) {
        return current + "/" + totalPage;
    }
}
