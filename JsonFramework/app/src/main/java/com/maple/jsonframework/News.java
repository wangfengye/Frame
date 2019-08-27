package com.maple.jsonframework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maple on 2019/8/24 20:51
 */
public class News {
    private int id;
    private String title;
    private List<Reader> readers;
    private HashMap<String, String> map;
    private ArrayList<Integer> ints;
    private ArrayList<ArrayList<Integer>> arrays;

    public ArrayList<ArrayList<Integer>> getArrays() {
        return arrays;
    }

    public void setArrays(ArrayList<ArrayList<Integer>> arrays) {
        this.arrays = arrays;
    }

    public ArrayList<Integer> getInts() {
        return ints;
    }

    public void setInts(ArrayList<Integer> ints) {
        this.ints = ints;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Reader> getReaders() {
        return readers;
    }

    public void setReaders(List<Reader> readers) {
        this.readers = readers;
    }

    public HashMap<String, String> getMap() {
        return map;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", readers=" + toStringReaders() +
                ", map=" + toStringMap() +
                ", ints="+ints.toString()+
                ", arrays"+arrays.toString()+
                '}';
    }

    private String toStringMap() {
        if (map == null) return "";
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.append(entry.getKey()).append('=').append(entry.getValue().toString()).append(',');
        }
        builder.append('}');
        return builder.toString();
    }

    private String toStringReaders() {
        if (readers == null) return "";
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < readers.size(); i++) {
            buffer.append(readers.get(i).toString());
        }
        return buffer.toString();
    }

    public static class Reader {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Reader{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
