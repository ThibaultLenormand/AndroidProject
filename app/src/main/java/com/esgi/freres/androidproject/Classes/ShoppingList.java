package com.esgi.freres.androidproject.Classes;

public class ShoppingList {

    private Integer id;
    private String name;
    private Boolean completed;

    public Integer getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public Boolean getCompleted()
    {
        return this. completed;
    }


    public void setName(String name)
    {
        this.name = name;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setCompleted(Boolean completed) {this.completed = completed; }
}
