package dev.mlnr.spidey.objects.json;

import dev.mlnr.spidey.utils.requests.Requester;

public class UrbanDictionary
{
    private String author;
    private String definition;
    private String example;
    private String word;
    private int likes;
    private int dislikes;
    private boolean exists = false;
    private final String term;

    public UrbanDictionary(final String term)
    {
        this.term = term;
        setData();
    }

    private void setData()
    {
        final var json = Requester.executeRequest("http://api.urbandictionary.com/v0/define?term=" + term, null);
        final var list = json.getArray("list");
        if (list.isEmpty())
            return;
        final var data = list.getObject(0);
        this.author = data.getString("author");
        this.definition = data.getString("definition");
        this.example = data.getString("example");
        this.word = data.getString("word");
        this.likes = data.getInt("thumbs_up");
        this.dislikes = data.getInt("thumbs_down");
        this.exists = true;
    }

    public final String getAuthor() { return author; }
    public final String getDefinition() { return definition; }
    public final String getExample() { return example; }
    public final String getWord() { return word; }
    public final int getLikes() { return likes; }
    public final int getDislikes() { return dislikes; }
    public final boolean exists() { return exists; }
}
