package com.kurang.pack;

public class Category {
    public String name;
    public int isi;
    public int kardus;
    public int ada;

    public Category(String name, int isi, int kardus, int ada) {
        this.name = name;
        this.isi = isi;
        this.kardus = kardus;
        this.ada = ada;
    }

    public int getButuh() {
        return isi * kardus;
    }

    public int getKurang() {
        int k = getButuh() - ada;
        return k < 0 ? 0 : k;
    }
}
